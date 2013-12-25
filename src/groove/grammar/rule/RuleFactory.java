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
 * $Id$
 */
package groove.grammar.rule;

import groove.algebra.Operator;
import groove.algebra.syntax.Expression;
import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeFactory;
import groove.grammar.type.TypeGuard;
import groove.grammar.type.TypeLabel;
import groove.grammar.type.TypeNode;
import groove.graph.ElementFactory;
import groove.graph.Label;
import groove.util.Dispenser;

import java.util.List;

/** Factory class for graph elements. */
public class RuleFactory extends ElementFactory<RuleNode,RuleEdge> {
    /** Private constructor. */
    private RuleFactory(TypeFactory typeFactory) {
        this.typeFactory = typeFactory;
    }

    /** Factory method for a default rule node. */
    public RuleNode createNode(int nr, TypeLabel typeLabel, boolean sharp,
            List<TypeGuard> typeGuards) {
        return createNode(Dispenser.single(nr), typeLabel, sharp, typeGuards);
    }

    /** Factory method for a default rule node. */
    public RuleNode createNode(Dispenser dispenser, TypeLabel typeLabel,
            boolean sharp, List<TypeGuard> typeGuards) {
        int nr = dispenser.getNext();
        TypeNode type = getTypeFactory().createNode(typeLabel);
        DefaultRuleNode result = newNode(nr, type, sharp, typeGuards);
        registerNode(result);
        return result;
    }

    /** Creates a variable node for a given algebra term, and with a given node number. */
    public VariableNode createVariableNode(int nr, Expression term) {
        TypeNode type = getTypeFactory().getDataType(term.getSignature());
        VariableNode result = new VariableNode(nr, term, type);
        registerNode(result);
        return result;
    }

    /** Creates an operator node for a given node number and arity. */
    public RuleNode createOperatorNode(int nr, Operator operator,
            List<VariableNode> arguments, VariableNode target) {
        RuleNode result = new OperatorNode(nr, operator, arguments, target);
        registerNode(result);
        return result;
    }

    /* This implementation creates a node with top node type. */
    @Override
    protected RuleNode newNode(int nr) {
        return newNode(nr, getTypeFactory().getTopNode(), true, null);
    }

    /** Callback factory node for a rule node. */
    private DefaultRuleNode newNode(int nr, TypeNode type, boolean sharp,
            List<TypeGuard> typeGuards) {
        return new DefaultRuleNode(nr, type, sharp, typeGuards);
    }

    /** Creates a label with the given text. */
    public RuleLabel createLabel(String text) {
        return new RuleLabel(text);
    }

    /** Gets the appropriate type edge from the type factory. */
    @Override
    public RuleEdge createEdge(RuleNode source, Label label, RuleNode target) {
        RuleLabel ruleLabel = (RuleLabel) label;
        TypeLabel typeLabel = ruleLabel.getTypeLabel();
        TypeEdge type =
            typeLabel == null ? null : getTypeFactory().createEdge(
                source.getType(), typeLabel, target.getType(), false);
        return new RuleEdge(source, ruleLabel, type, target);
    }

    @Override
    public RuleGraphMorphism createMorphism() {
        return new RuleGraphMorphism();
    }

    /** Returns the type factory used by this rule factory. */
    public TypeFactory getTypeFactory() {
        return this.typeFactory;
    }

    /** The type factory used for creating node and edge types. */
    private final TypeFactory typeFactory;

    /** Returns a fresh instance of this factory, without type graph. */
    public static RuleFactory newInstance() {
        return newInstance(TypeFactory.newInstance());
    }

    /** Returns a fresh instance of this factory, for a given type graph. */
    public static RuleFactory newInstance(TypeFactory typeFactory) {
        return new RuleFactory(typeFactory);
    }
}
