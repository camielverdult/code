package groove.gui;

import groove.control.ControlAutomaton;
import groove.gui.jgraph.ControlJModel;
import groove.gui.jgraph.GraphJModel;
import groove.gui.jgraph.JGraph;
import groove.gui.layout.Layouter;
import groove.trans.GraphGrammar;

import java.util.Collection;
import java.util.Collections;

public class CAPanel extends JGraphPanel<JGraph> {

	private Options options;
	private ControlAutomaton control; 
	private Layouter layouter;
	
	public CAPanel(Simulator simulator)
	{
		super(new JGraph(new ControlJModel(),false), true , simulator.getOptions());
		this.getJGraph().setConnectable(false);
		this.getJGraph().setDisconnectable(false);
		this.getJGraph().setEnabled(false);
		layouter = new MyForestLayouter();
	}

	public void setGrammar(GraphGrammar grammar)
	{
		if( grammar.getControl() == null ) {
			this.getJGraph().setModel(ControlJModel.EMPTY_JMODEL);
			this.setEnabled(false);
		}
		else
		{
			control = grammar.getControl();
			GraphJModel model = GraphJModel.newInstance(control, getOptions());
			//new ControlJModel(grammar.getControl(), options);
			//System.out.println("Nodes: " + grammar.getControl().nodeCount() + " / Edges: " + grammar.getControl().edgeCount());
			this.getJGraph().setModel(model);
			this.getJGraph().setEnabled(true);
			layouter.newInstance(getJGraph()).start(true);
			this.refreshStatus();
		}
	}
	
	/**
	 * A specialization of the forest layouter that takes the LTS start graph
	 * as its suggested root.
	 */
	private class MyForestLayouter extends groove.gui.layout.ForestLayouter {
	    /**
	     * Creates a prototype layouter
	     */
	    public MyForestLayouter() {
	        super();
	    }
	
	    /**
	     * Creates a new instance, for a given {@link JGraph}.
	     */
	    public MyForestLayouter(String name, JGraph jgraph) {
	        super(name, jgraph);
	    }
	
	    /**
	     * This method returns a singleton set consisting of the LTS start state.
	     */
	    @Override
	    protected Collection<?> getSuggestedRoots() {
	        GraphJModel jModel = (GraphJModel) getJModel();
	        return Collections.singleton(jModel.getJVertex(control.startState()));
	    }
	
	    /**
	     * This implementation returns a {@link MyForestLayouter}.
	     */
	    @Override
	    public Layouter newInstance(JGraph jGraph) {
	        return new MyForestLayouter(name, jGraph);
	    }
	}
	
}
