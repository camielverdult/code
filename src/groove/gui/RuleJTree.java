/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2007 University of Twente
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 *
 * $Id: RuleJTree.java,v 1.33 2008-03-18 12:54:42 iovka Exp $
 */
package groove.gui;

import groove.control.ControlView;
import groove.explore.util.MatchesIterator;
import groove.graph.GraphInfo;
import groove.graph.GraphProperties;
import groove.graph.Label;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.lts.GraphTransition;
import groove.lts.Transition;
import groove.trans.Match;
import groove.trans.NameLabel;
import groove.trans.RuleMatch;
import groove.trans.RuleNameLabel;
import groove.util.CollectionOfCollections;
import groove.util.Converter;
import groove.util.Groove;
import groove.view.AspectualRuleView;
import groove.view.DefaultGrammarView;
import groove.view.GrammarView;
import groove.view.RuleView;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * Panel that displays a two-level directory of rules and matches.
 * @version $Revision: 1.33 $
 * @author Arend Rensink
 */
public class RuleJTree extends JTree implements SimulationListener {
	/** Creates an instance for a given simulator. */
    protected RuleJTree(final Simulator simulator) {
        this.simulator = simulator;
        simulator.addSimulationListener(this);
        setRootVisible(false);
        setShowsRootHandles(true);
        setEnabled(false);
        setToggleClickCount(0);
        setCellRenderer(new MyTreeCellRenderer());
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        // set icons
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) cellRenderer;
        renderer.setLeafIcon(Groove.GRAPH_MATCH_ICON);
        renderer.setOpenIcon(Groove.RULE_SMALL_ICON);
        renderer.setClosedIcon(Groove.RULE_SMALL_ICON);
        addTreeSelectionListener(createRuleSelectionListener());
        this.listenToSelectionChanges = true;
        addMouseListener(new MyMouseListener());
        topDirectoryNode = new DefaultMutableTreeNode();
        ruleDirectory = new DefaultTreeModel(topDirectoryNode, true);
        setModel(ruleDirectory);
        ActionMap am = getActionMap();
        am.put(Options.UNDO_ACTION_NAME, simulator.getUndoAction());
        am.put(Options.REDO_ACTION_NAME, simulator.getRedoAction());
        InputMap im = getInputMap();
        im.put(Options.UNDO_KEY, Options.UNDO_ACTION_NAME);
        im.put(Options.REDO_KEY, Options.REDO_ACTION_NAME);
        // add tool tips
        ToolTipManager.sharedInstance().registerComponent(this);
    }

    /**
     * Fills the rule directory with rule nodes, based on a given rule system.
     * Sets the current LTS to the grammar's LTS.
     */
    public synchronized void setGrammarUpdate(DefaultGrammarView grammar) {
    	displayedGrammar = grammar;
		if (grammar == null) {
			dirNodeMap.clear();
			ruleNodeMap.clear();
			matchNodeMap.clear();
			matchTransitionMap.clear();
			topDirectoryNode.removeAllChildren();
			ruleDirectory.reload();
		} else {
			loadGrammar(grammar);
		}
		refresh();
    }

	/**
	 * Loads the j-tree with the data of the given (non-<code>null</code>)
	 * grammar.
	 */
	private void loadGrammar(DefaultGrammarView grammar) {
		boolean oldListenToSelectionChanges = listenToSelectionChanges;
        listenToSelectionChanges = false;
        setShowAnchorsOptionListener();
        dirNodeMap.clear();
        ruleNodeMap.clear();
        matchNodeMap.clear();
        matchTransitionMap.clear();
        topDirectoryNode.removeAllChildren();

        // if the rule system has multiple priorities, we want an extra level of nodes
        // then we need to remember the last priority encountered
        int lastPriority = Integer.MAX_VALUE;
        DefaultMutableTreeNode topNode = topDirectoryNode;
        boolean hasSpecialPriorities = grammar.getPriorityMap().size() > 1;
        // get the rule names
        for (RuleView rule: new CollectionOfCollections<RuleView>(grammar.getPriorityMap().values())) {
            RuleNameLabel ruleName = rule.getNameLabel();
            // create new top node for the rule, if the rule has a different priority then the last
            if (hasSpecialPriorities) {
                int rulePriority = rule.getPriority();
                if (lastPriority != rulePriority) {
                    lastPriority = rulePriority;
                    topNode = new PriorityTreeNode(lastPriority);
                    topDirectoryNode.add(topNode);
                }
            }
            // recursively add parent directory nodes as required
            DefaultMutableTreeNode parentNode = addParentNode(topNode, ruleName);
            // create the rule node and register it
            AspectualRuleView ruleView = grammar.getRuleMap().get(ruleName);
            RuleTreeNode ruleNode = new RuleTreeNode(ruleView);
            parentNode.add(ruleNode);
            expandPath(new TreePath(ruleNode.getPath()));
            ruleNodeMap.put(ruleName, ruleNode);
        }
        ruleDirectory.reload(topDirectoryNode);
        listenToSelectionChanges = oldListenToSelectionChanges;
	}
    
	/** Refreshes the view, to add match nodes. */
    public synchronized void startSimulationUpdate(GTS gts) {
        refresh();
	}

    /**
     * Refreshes the available match nodes, based on a new state.
     * The current LTS is inspected to find out the relevant derivations.
     * Expands all rule nodes to show the available matches.
     */
    public synchronized void setStateUpdate(GraphState state) {
        refresh();
    }

    /**
     * Sets the tree selection to a given rule name.
     * Does <i>not</i> trigger actions based on the selection change.
     */
    public synchronized void setRuleUpdate(NameLabel name) {
        refresh();
    }

    /**
     * Sets the tree selection to a given derivation.
     * Does <i>not</i> trigger actions based on the selection change.
     */
    public synchronized void setTransitionUpdate(GraphTransition transition) {
        refresh();
    }

    /**
     * Sets the tree selection to a given derivation.
     * Does <i>not</i> trigger actions based on the selection change.
     */
    public void setMatchUpdate(RuleMatch match) {
        refresh();
    }

    /**
     * Sets the directory tree as in <tt>setStateUpdate</tt> for the currently
     * selected derivation's cod state.
     */
    public synchronized void applyTransitionUpdate(GraphTransition transition) {
        refresh();
    }
    
    /**
	 * In addition to delegating the method to <tt>super</tt>,
	 * sets the background color to <tt>null</tt> when disabled and
	 * back to the default when enabled.
	 */
	@Override
	public void setEnabled(boolean enabled) {
	    if (enabled != isEnabled()) {
	        if (!enabled) {
	            enabledBackground = getBackground();
	            setBackground(null);
	        } else if (enabledBackground != null) {
	            setBackground(enabledBackground);
	        }
	    }
	    super.setEnabled(enabled);
	}

	/**
     * Sets a listener to the anchor image option, if that has not yet been done.
     */
    protected void setShowAnchorsOptionListener() {
    	if (! anchorImageOptionListenerSet) {
    		JMenuItem showAnchorsOptionItem = simulator.getOptions().getItem(Options.SHOW_ANCHORS_OPTION);
    		if (showAnchorsOptionItem != null) {
    	        // listen to the option controlling the rule anchor display
    			showAnchorsOptionItem.addItemListener(new ItemListener() {
    				public void itemStateChanged(ItemEvent e) {
    					refresh();
    				}
    	        });
    	        anchorImageOptionListenerSet = true;
    		}
    	}
    }

    /** Adds tree nodes for all levels of a structured rule name. */
    private DefaultMutableTreeNode addParentNode(DefaultMutableTreeNode topNode, RuleNameLabel ruleName) {
        RuleNameLabel parent = ruleName.parent();
        if (parent == null)
            // there is no parent rule name; the parent node is the top node
            return topNode;
        else {
            // there is a proper parent rule; look it up in the node map
            DirectoryTreeNode result = dirNodeMap.get(parent);
            if (result == null) {
                // the parent node did not yet exist in the tree
                // check recursively for the grandparent
                DefaultMutableTreeNode grandParentNode = addParentNode(topNode, parent);
                // make the parent node and register it
                result = new DirectoryTreeNode(parent);
                grandParentNode.add(result);
                dirNodeMap.put(parent, result);
            }
            return result;
        }
    }

    void refresh() {
    	boolean oldListenToSelectionChanges = listenToSelectionChanges;
        listenToSelectionChanges = false;
        if (getCurrentState() == null) {
            refreshMatchesClosed(Collections.<GraphTransition>emptySet());
    	} else if (setDisplayedState(getCurrentState())) {
    		if( getCurrentState().isClosed()) {
    			refreshMatchesClosed(getCurrentGTS().outEdgeSet(getCurrentState()));
    		} else {
    			Set<RuleMatch> matches = new HashSet<RuleMatch>();
    			MatchesIterator matchIter = new MatchesIterator(getCurrentState(), getCurrentGTS().getRecord().freshCache(getCurrentState(), false));
    			while (matchIter.hasNext()) {
    			    matches.add(matchIter.next());
    			}
    			refreshMatchesOpen(matches);
    		}
    	}
    	DefaultMutableTreeNode treeNode = null;
    	if (getCurrentTransition() != null) {
    		treeNode = matchNodeMap.get(getCurrentTransition().getMatch());
    	} else if( getCurrentMatch() != null ) {
    		treeNode = matchNodeMap.get(getCurrentMatch());
    	} else if (getCurrentRule() != null) {
    		treeNode = ruleNodeMap.get(getCurrentRule().getNameLabel());
    	}
        if (treeNode != null) {
            setSelectionPath(new TreePath(treeNode.getPath()));
        }
        setEnabled(displayedGrammar != null);
        setBackground(getCurrentGTS() == null ? null : TREE_ENABLED_COLOR);
        listenToSelectionChanges = oldListenToSelectionChanges;
    }
    
    /**
     * Refreshes the match nodes, based on a given derivation edge set.
     * @param matches the set of derivation edges used to create match nodes
     */
    private void refreshMatchesOpen(Set<? extends RuleMatch> matches) {
        // remove current matches
    	for (MatchTreeNode matchNode: matchNodeMap.values()) {
            ruleDirectory.removeNodeFromParent(matchNode);
        }
        // clean up current match node map
        matchNodeMap.clear();
        matchTransitionMap.clear();
        // expand all rule nodes and subsequently collapse all directory nodes
        for (DefaultMutableTreeNode nextNode: ruleNodeMap.values()) {
            if (!(nextNode instanceof DirectoryTreeNode)) {
                expandPath(new TreePath(nextNode.getPath()));
            }
        }
        for (DefaultMutableTreeNode nextNode: ruleNodeMap.values()) {
            if (nextNode instanceof DirectoryTreeNode) {
                collapsePath(new TreePath(nextNode.getPath()));
            }
        }
        // recollect the derivations so that they are ordered according to the rule events
        SortedSet<RuleMatch> orderedMatches = new TreeSet<RuleMatch>(new Comparator<RuleMatch>() {
			public int compare(RuleMatch o1, RuleMatch o2) {
				return getCurrentGTS().getRecord().getEvent(o1).compareTo(getCurrentGTS().getRecord().getEvent(o2));
			}
        });
        orderedMatches.addAll(matches);
        // insert new matches
        for (RuleMatch match: orderedMatches) {
            Label ruleName = match.getRule().getName();
            RuleTreeNode ruleNode = ruleNodeMap.get(ruleName);
            assert ruleNode != null : String.format("Rule %s has no image in map %s", ruleName, ruleNodeMap);
            int nrOfMatches = ruleNode.getChildCount();
            MatchTreeNode matchNode = new MatchTreeNode(nrOfMatches + 1, match);
            ruleDirectory.insertNodeInto(matchNode, ruleNode, nrOfMatches);
            expandPath(new TreePath(ruleNode.getPath()));
            matchNodeMap.put(match, matchNode);
        }
        
        for( GraphTransition trans : getCurrentGTS().outEdgeSet(getCurrentState())) {
        	matchTransitionMap.put(trans.getMatch(), trans);
        }
    }

    /**
     * Refreshes the match nodes, based on a given derivation edge set.
     * @param derivations the set of derivation edges used to create match nodes
     */
    private void refreshMatchesClosed(Set<? extends GraphTransition> derivations) {
        // remove current matches
    	for (MatchTreeNode matchNode: matchNodeMap.values()) {
            ruleDirectory.removeNodeFromParent(matchNode);
        }
        // clean up current match node map
        matchNodeMap.clear();
        matchTransitionMap.clear();
        
        // expand all rule nodes and subsequently collapse all directory nodes
        for (DefaultMutableTreeNode nextNode: ruleNodeMap.values()) {
            if (!(nextNode instanceof DirectoryTreeNode)) {
                expandPath(new TreePath(nextNode.getPath()));
            }
        }
        for (DefaultMutableTreeNode nextNode: ruleNodeMap.values()) {
            if (nextNode instanceof DirectoryTreeNode) {
                collapsePath(new TreePath(nextNode.getPath()));
            }
        }
        // recollect the derivations so that they are ordered according to the rule events
        SortedSet<GraphTransition> orderedDerivations = new TreeSet<GraphTransition>(new Comparator<GraphTransition>() {
			public int compare(GraphTransition o1, GraphTransition o2) {
				return o1.getEvent().compareTo(o2.getEvent());
			}
        });
        orderedDerivations.addAll(derivations);
        // insert new matches
        for (GraphTransition edge: orderedDerivations) {
            Label ruleName = edge.getEvent().getRule().getName();
            RuleTreeNode ruleNode = ruleNodeMap.get(ruleName);
            assert ruleNode != null : String.format("Rule %s has no image in map %s", ruleName, ruleNodeMap);
            int nrOfMatches = ruleNode.getChildCount();
            MatchTreeNode matchNode = new MatchTreeNode(nrOfMatches + 1, edge.getMatch());
            ruleDirectory.insertNodeInto(matchNode, ruleNode, nrOfMatches);
            expandPath(new TreePath(ruleNode.getPath()));
            matchNodeMap.put(edge.getMatch(), matchNode);
            matchTransitionMap.put(edge.getMatch(), edge);
        }
    }
    
    /** Convenience method to retrieve the current GTS from the simulator. */
    GTS getCurrentGTS() {
    	return simulator.getCurrentGTS();
    }

    /** Convenience method to retrieve the currently selected transition from the simulator. */
    private GraphTransition getCurrentTransition() {
    	return simulator.getCurrentTransition();
    }

    /** Convenience method to retrieve the currently selected match from the state panel */
    private RuleMatch getCurrentMatch() {
    	return simulator.getCurrentMatch();
    }
    
    /** Convenience method to retrieve the currently selected state from the simulator. */
    GraphState getCurrentState() {
    	return simulator.getCurrentState();
    }

    /** Convenience method to retrieve the currently selected rule from the simulator. */
    private RuleView getCurrentRule() {
    	return simulator.getCurrentRule();
    }

    /** 
     * Sets the {@link #displayedState} field to a given value, and
     * returns an indication whether the new value differs from the old.
     * @param state the new value of the displayed state
     * @return <code>true</code> if the new value differs from the old
     */
    private boolean setDisplayedState(GraphState state) {
    	boolean result = state != displayedState;
    	displayedState = state;
    	return result;
    }

//    /** 
//     * Sets the {@link #displayedGrammar} field to a given value, and
//     * returns an indication whether the new value differs from the old.
//     * @param grammar the new value of the displayed grammar
//     * @return <code>true</code> if the new value differs from the old
//     */
//    private boolean setDisplayedGrammar(GrammarView grammar) {
//    	boolean result = grammar != displayedGrammar;
//    	displayedGrammar = grammar;
//    	return result;
//    }

    /**
     * Creates the selection listener to be used to
     * react on selections in this rule directory.
     * The current implementation returns a <tt>RuleSelectionListener</tt>.
     * @see RuleJTree.RuleSelectionListener
     */
    protected TreeSelectionListener createRuleSelectionListener() {
        return new RuleSelectionListener();
    }

    /** 
     * Creates a popup menu for this panel. 
     * @param node the node for which the menu is created
     */
    protected JPopupMenu createPopupMenu(TreeNode node) {
        JPopupMenu res = new JPopupMenu();
        res.add(simulator.getNewRuleAction());
        if (node instanceof RuleTreeNode) {
			res.addSeparator();
			res.add(simulator.getEnableRuleAction());
			res.addSeparator();
			res.add(simulator.getCopyRuleAction());
			res.add(simulator.getDeleteRuleAction());
			res.add(simulator.getRenameRuleAction());
            res.addSeparator();
            res.add(simulator.getEditRulePropertiesAction());
            res.add(simulator.getEditRuleAction());
		} else if (node instanceof MatchTreeNode) {
            res.addSeparator();
            res.add(simulator.getApplyTransitionAction());
        }
        return res;
    }

    /**
	 * Indicates if the rule tree is at the moment should react to selection changes.
	 */
	final boolean isListenToSelectionChanges() {
		return this.listenToSelectionChanges;
	}

	/**
	 * Directory of production rules and their matchings to the current state.
	 * Alias to the underlying model of this <tt>JTree</tt>.
	 * 
	 * @invariant <tt>ruleDirectory == getModel()</tt>
	 */
    protected final DefaultTreeModel ruleDirectory;
    /**
     * Alias for the top node in <tt>ruleDirectory</tt>.
     * @invariant <tt>topDirectoryNode == ruleDirectory.getRoot()</tt>
     */
    protected final DefaultMutableTreeNode topDirectoryNode;
    /**
     * The simulator to which this directory belongs.
     * @invariant simulator != null
     */
    protected final Simulator simulator;
    /** 
     * Mapping from rule names in the current grammar to rule nodes in
     * the current rule directory.
     * @invariant <tt>ruleNodeMap: StructuredRuleName --> DirectoryTreeNode
     *                                               \cup RuleTreeNode</tt>
     */
    protected final Map<NameLabel,RuleTreeNode> ruleNodeMap = new HashMap<NameLabel,RuleTreeNode>();
    /** 
     * Mapping from rule names in the current grammar to rule nodes in
     * the current rule directory.
     * @invariant <tt>ruleNodeMap: StructuredRuleName --> DirectoryTreeNode
     *                                               \cup RuleTreeNode</tt>
     */
    protected final Map<NameLabel,DirectoryTreeNode> dirNodeMap = new HashMap<NameLabel,DirectoryTreeNode>();
    /** 
     * Mapping from derivation edges in the current LTS to match nodes in
     * the current rule directory.
     * @invariant <tt>matchNodeMap: Transition --> MatchTreeNode</tt>
     */
//    protected final Map<Transition,MatchTreeNode> transitionNodeMap = new HashMap<Transition,MatchTreeNode>();

    /**
     * Mapping from RuleMatches in the current LTS to match nodes in the rule directory
     */
    protected final Map<RuleMatch, MatchTreeNode> matchNodeMap = new HashMap<RuleMatch, MatchTreeNode>();
    
    /**
     * Mapping from RuleMatches to transitions in the current LTS, for fast selecting
     */
    protected final Map<RuleMatch, GraphTransition> matchTransitionMap = new HashMap<RuleMatch, GraphTransition>();
    
    /**
     * Switch to determine whether changes in the tree selection model
     * should trigger any actions right now.
     */
    private transient boolean listenToSelectionChanges;

    /**
     * The background colour of this component when it is enabled.
     */
    private Color enabledBackground;
    /** Flag to indicate that the anchor image option listener has been set. */
    private boolean anchorImageOptionListenerSet = false;
    /** The currently displayed state. */
    private GraphState displayedState;
    /** The currently displayed grammar. */
    private GrammarView<?,?> displayedGrammar;
    
    /** 
     * Transforms a given rule name into the string that shows this rule is disabled.
     * This implementation puts brackets around the rule name.
     * @param name The rule name; non-<code>null</code>
     * @return a string constructed from <code>name</code> that shows the rule to be disabled
     */
    static public String showDisabled(String name) {
    	return "("+name+")";
    }
    
    static private final Color TREE_ENABLED_COLOR;
    
    static {
    	JLabel label = new JLabel();
    	label.setEnabled(true);
    	TREE_ENABLED_COLOR = Color.WHITE;
    }
    
	/**
	 * Selection listener that invokes <tt>setRule</tt> if a
	 * rule node is selected, and <tt>setDerivation</tt> if
	 * a match node is selected.
	 * @see Simulator#setRule
	 * @see Simulator#setTransition
	 */
	private class RuleSelectionListener implements TreeSelectionListener {
		/**
		 * Empty constructor with the correct visibility.
		 */
		public RuleSelectionListener() {
			// Empty
		}
		
	    public void valueChanged(TreeSelectionEvent evt) {
	        // only do something if a path was added to the selection
	        if (isListenToSelectionChanges() && evt.isAddedPath()) {
	            Object selectedNode = evt.getPath().getLastPathComponent();
	            if (selectedNode instanceof RuleTreeNode) {
	                // selected tree node is a production rule (level 1 node)
	                simulator.setRule(((RuleTreeNode) selectedNode).getRule().getNameLabel());
	            } else if (selectedNode instanceof MatchTreeNode) {
	                // selected tree node is a match (level 2 node)
	            	RuleMatch match = ((MatchTreeNode) selectedNode).edge();
	            	GraphTransition trans = matchTransitionMap.get(match);
	            	if( trans != null ) {
		            	simulator.setTransition(trans);	            		
	            	} else {
	            		simulator.setMatch(match);
	            	}
	            	
	                if (simulator.getGraphPanel() == simulator.getRulePanel()) {
	                	simulator.setGraphPanel(simulator.getStatePanel());
	                }
	            }
	        }
	    }
	}

	/** 
	 * Mouse listener that creates the popup menu and switches the view to 
	 * the rule panel on double-clicks.
	 */
	private class MyMouseListener extends MouseAdapter {
		/**
		 * Empty constructor with the correct visibility.
		 */
		public MyMouseListener() {
			// empty
		}
		
        @Override
        public void mousePressed(MouseEvent evt) {
        	if (evt.getButton() == MouseEvent.BUTTON3) {
        		TreePath selectedPath = getPathForLocation(evt.getX(), evt.getY());
        		if (selectedPath != null) {
        			setSelectionPath(selectedPath);
        		}
        	}
            maybeShowPopup(evt);
        }

        @Override
        public void mouseReleased(MouseEvent evt) {
            maybeShowPopup(evt);
        }

        @Override
        public void mouseClicked(MouseEvent evt) {
        	if (evt.getButton() != MouseEvent.BUTTON1) {
        		return;
        	}
        	TreePath path = getSelectionPath();
        	if (path == null) { return; }
        	Object selectedNode = path.getLastPathComponent();
        	if (selectedNode instanceof RuleTreeNode) {
    			simulator.setGraphPanel(simulator.getRulePanel());
    			return;
    		}
        	if (selectedNode instanceof MatchTreeNode) {
        		RuleMatch match = ((MatchTreeNode) selectedNode).edge();
        		GraphTransition trans = matchTransitionMap.get(match);
        		if (trans == null) {
        			Iterator<GraphTransition> outTransitions = getCurrentState().getTransitionIter();
    				while (outTransitions.hasNext()) {
    					GraphTransition t = outTransitions.next();
    					if (t.getMatch().equals(match)) {
    						trans = t;
    						matchTransitionMap.put(match, trans);
    						break;
    					}
    				}
                    simulator.setMatch(match);
        		} else {
                    // if trans is not null, it has been added to the matchTransitionMap
                    simulator.setTransition(trans);
        		}
        		if (evt.getClickCount() == 2) {
        			simulator.applyMatch();
        		}
        	}
        }

        private void maybeShowPopup(MouseEvent evt) {
            if (evt.isPopupTrigger()) {
                TreePath selectedPath = getPathForLocation(evt.getX(), evt.getY());
                TreeNode selectedNode = selectedPath == null ? null : (TreeNode) selectedPath.getLastPathComponent();
                createPopupMenu(selectedNode).show(evt.getComponent(), evt.getX(), evt.getY());
            }
        }
    }
	
	/**
	 * Priority nodes (used only if the rule system has multiple priorities)
	 */
	private class PriorityTreeNode extends DefaultMutableTreeNode {
	    /**
	     * Creates a new priority node based on a given priority.
	     * The node can (and will) have children.
	     */
	    public PriorityTreeNode(int priority) {
	        super((priority==ControlView.ANY_RULE_PRORITY)?"Priority ANY":"Priority "+priority, true);
	    }
	}

	/**
	 * Rule nodes (= level 1 nodes) of the directory
	 */
	private class RuleTreeNode extends DefaultMutableTreeNode {
	    /**
	     * Creates a new rule node based on a given rule name.
	     * The node can have children.
	     */
	    public RuleTreeNode(AspectualRuleView rule) {
	        super(rule, true);
	    }
	
	    /**
	     * Convenience method to retrieve the user object as a rule name.
	     */
	    public AspectualRuleView getRule() {
	        return (AspectualRuleView) getUserObject();
	    }
	
	    /**
	     * To display, show child name only. Also visualise enabledness.
	     * @see RuleJTree#showDisabled(String)
	     */
	    @Override
	    public String toString() {
	    	String name = getRule().getNameLabel().child();
	    	if (getRule().isEnabled()) {
	    		return name;
	    	} else {
	    		return showDisabled(name);
	    	}
	    }
	    
	    /** Returns HTML-formatted tool tip text for this rule node. */
	    public String getToolTipText() {
	    	StringBuilder result = new StringBuilder();
	    	result.append("Rule ");
	    	result.append(Converter.STRONG_TAG.on(getRule().getName()));
        	GraphProperties properties = GraphInfo.getProperties(getRule().getAspectGraph(), false);
        	if (properties != null && !properties.isEmpty()) {
        	    boolean hasProperties;
        	    String remark = properties.getRemark();
        	    if (remark != null) {
            	    result.append(": ");
                    result.append(Converter.toHtml(remark));
                    hasProperties = properties.size() > 1;
        	    } else {
        	        hasProperties = true;
        	    }
        	    if (hasProperties) {
                    for (String key : properties.getPropertyKeys()) {
                        if (!GraphProperties.isSystemKey(key) && !key.equals(GraphProperties.REMARK_KEY)) {
                            result.append(Converter.HTML_LINEBREAK);
                            result.append(propertyToString(key, properties.getProperty(key)));
                        }
                    }
                }
        	}
    		Converter.HTML_TAG.on(result);
        	return result.toString();
	    }
	    
	    /** Returns an HTML-formatted string for a given key/value-pair. */
	    private String propertyToString(String key, String value) {
	    	return "<b>"+key+"</b> = "+value;
	    }
	}

	/**
	 * Directory nodes (= level 0 nodes) of the directory
	 */
	private class DirectoryTreeNode extends DefaultMutableTreeNode {
	    /**
	     * Creates a new rule node based on a given rule name.
	     * The node can have children.
	     */
	    public DirectoryTreeNode(RuleNameLabel name) {
	        super(name, true);
	    }
	
	    /**
	     * Convenience method to retrieve the user object as a rule name.
	     */
	    public RuleNameLabel name() {
	        return (RuleNameLabel) getUserObject();
	    }
	
	    /**
	     * To display, show child name only
	     */
	    @Override
	    public String toString() {
	        return name().child();
	    }
	}

	/**
	 * Match nodes (= level 2 nodes) of the directory.
	 * Stores a <tt>Transition</tt> as user object.
	 */
	private class MatchTreeNode extends DefaultMutableTreeNode {
	    /**
	     * Creates a new match node on the basis of a given number and derivation edge.
	     * The node cannot have children.
	     */
	    public MatchTreeNode(int nr, Transition edge) {
	        super(edge, false);
	        this.nr = nr;
	    }
	
	    /**
	     * Creates a new match node on the basis of a given number and the RuleMatch.
	     * The node cannot have children.
	     * @param nr
	     * @param edge
	     */
	    public MatchTreeNode(int nr, Match edge) {
	    	super(edge, false);
	    	this.nr = nr;
	    }
	    
	    /**
	     * Convenience method to return the underlying derivation edge.
	     */
	    public RuleMatch edge() {
	        return (RuleMatch) getUserObject();
	    }
	
	    /**
	     * Object identity is good enough as a notion of equality.
	     */
	    @Override
	    public boolean equals(Object obj) {
	        return this == obj;
	    }
	
	    /**
	     * A description of this derivation edge in the rule directory.
	     * Returns <tt>"Match ??</tt>, where <tt>??</tt> is the node number.
	     */
	    @Override
	    public String toString() {
	    	return simulator.getOptions().isSelected(Options.SHOW_ANCHORS_OPTION) ? simulator.getCurrentGTS().getRecord().getEvent(edge()).getAnchorImageString(): "Match " + nr;
	    }
	
	    /** The number of this match, used in <tt>toString()</tt> */
	    private final int nr;
	}

	/**
	 * Class to provide proper icons for directory nodes
	 */
	private class MyTreeCellRenderer extends DefaultTreeCellRenderer {
		/**
		 * Empty constructor with the correct visibility.
		 */
		public MyTreeCellRenderer() {
			// empty
		}
		
	    @Override
	    public Component getTreeCellRendererComponent(
	        JTree tree,
	        Object value,
	        boolean sel,
	        boolean expanded,
	        boolean leaf,
	        int row,
	        boolean hasFocus) {
	        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
	
	        if (value instanceof DirectoryTreeNode) {
	            setIcon(Groove.GPS_FOLDER_ICON);
	        } else if (value instanceof PriorityTreeNode) {
	            setIcon(null);
	        }
	        if (value instanceof RuleTreeNode) {
	        	setToolTipText(((RuleTreeNode) value).getToolTipText());
	        } else {
	        	setToolTipText(null);
	        }
	        setOpaque(!sel);
	        return this;
	    }
	}
}
