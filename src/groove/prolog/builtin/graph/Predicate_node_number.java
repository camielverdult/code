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

import gnu.prolog.term.IntegerTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologException;
import groove.graph.DefaultNode;
import groove.graph.Node;
import groove.lts.AbstractGraphState;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class Predicate_node_number extends GraphPrologCode {
    @Override
    public int execute(Interpreter interpreter, boolean backtrackMode,
            Term[] args) throws PrologException {
        Node node = getNode(args[0]);
        int nr;
        if (node instanceof DefaultNode) {
            nr = ((DefaultNode) node).getNumber();
        } else if (node instanceof AbstractGraphState) {
            nr = ((AbstractGraphState) node).getNumber();
        } else {
            // no node number
            return FAIL;
        }
        IntegerTerm term = IntegerTerm.get(nr);
        return interpreter.unify(args[1], term);
    }
}
