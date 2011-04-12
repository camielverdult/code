/*
 * Groove Prolog Interface
 * Copyright (C) 2009 Michiel Hendriks, University of Twente
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package groove.prolog.builtin.graph;

import gnu.prolog.term.CompoundTerm;
import gnu.prolog.term.JavaObjectTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologException;
import groove.graph.Graph;
import groove.graph.Label;
import groove.prolog.util.PrologUtils;

/**
 * Get the edge set of a graph with a given label.
 * <code>label_edge_set(Graph,Atom,Edge)</code>
 * 
 * @author Michiel Hendriks, Lesley Wevers
 */
public class Predicate_label_edge_set extends GraphPrologCode {
    @Override
    public int execute(Interpreter interpreter, boolean backtrackMode,
            Term[] args) throws PrologException {
        try {
            Graph<?,?> graph = getGraph(args[0]);

            Label label = (Label) ((JavaObjectTerm) args[1]).value;

            Term edgeSetTerm =
                CompoundTerm.getList(PrologUtils.createJOTlist(graph.labelEdgeSet(label)));
            return interpreter.unify(edgeSetTerm, args[2]);
        } catch (Exception e) {
            return FAIL;
        }
    }
}
