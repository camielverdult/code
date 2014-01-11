/* GROOVE: GRaphs for Object Oriented VErification
 * Copyright 2003--2011 University of Twente
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
package groove.test.control;

import static org.junit.Assert.fail;
import groove.control.Call;
import groove.control.symbolic.OutEdge;
import groove.control.symbolic.Term;
import groove.grammar.Grammar;
import groove.grammar.Rule;
import groove.util.Groove;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author Arend Rensink
 * @version $Revision $
 */
@SuppressWarnings("javadoc")
public class TermDerivationTest {
    @Test
    public void testDelta() {
        setSource(delta());
        assertSuccFail(null, null);
        assertRest(false, 0);
    }

    @Test
    public void testEpsilon() {
        setSource(epsilon());
        assertSuccFail(null, null);
        assertRest(true, 0);
    }

    @Test
    public void testCall() {
        setSource(this.a);
        assertEdge(this.aCall, epsilon());
        assertSuccFail(null, null);
        assertRest(false, 0);
    }

    @Test
    public void testOr() {
        Term a = this.a;
        Term b = this.b;
        Term c = this.c;
        // a | (b|c) | skip
        setSource(a.or(b.seq(b).or(c)).or(epsilon()));
        assertEdge(this.aCall, epsilon());
        assertEdge(this.bCall, b);
        assertEdge(this.cCall, epsilon());
        assertRest(true, 0);
        assertSuccFail(null, null);
        // (try a;a else b) | c
        setSource(a.seq(a).tryElse(b).or(c));
        assertEdge(this.aCall, a.transit());
        assertSuccFail(c, b.or(c));
        assertRest(false, 0);
        // c | (if a;a else b) | c
        setSource(c.or(a.seq(a).ifElse(b)));
        assertEdge(this.aCall, a);
        assertSuccFail(c, c.or(b));
        assertRest(false, 0);
    }

    @Test
    public void testIfElse() {
        Term a = this.a;
        Term b = this.b;
        // if true else b
        setSource(epsilon().ifElse(b));
        assertSuccFail(null, null);
        assertRest(true, 0);
        // if a
        setSource(a.ifNoElse());
        assertEdge(this.aCall, epsilon());
        assertSuccFail(delta(), epsilon());
        assertRest(false, 0);
        // if (a|true) else b
        setSource(epsilon().or(a).ifElse(b));
        assertEdge(this.aCall, epsilon());
        assertSuccFail(null, null);
        assertRest(true, 0);
        // if a else b
        setSource(a.ifElse(b));
        assertEdge(this.aCall, epsilon());
        assertSuccFail(delta(), b);
        assertRest(false, 0);
        // if { if a } else b
        setSource(a.ifNoElse().ifElse(b));
        assertEdge(this.aCall, epsilon());
        assertSuccFail(delta(), epsilon().ifElse(b));
        assertRest(false, 0);
        // if { if a else b }
        setSource(a.ifElse(b).ifNoElse());
        assertEdge(this.aCall, epsilon());
        assertSuccFail(delta(), b.ifNoElse());
        assertRest(false, 0);
    }

    @Test
    public void testWhileDo() {
        Term a = this.a;
        Term b = this.b;
        Term c = this.c;
        // while { true }
        setSource(epsilon().whileDo());
        assertSuccFail(null, null);
        assertRest(false, 0);
        // while { a|b }
        setSource(a.or(b).whileDo());
        assertEdge(this.aCall, a.or(b).whileDo());
        assertEdge(this.bCall, a.or(b).whileDo());
        assertSuccFail(delta(), epsilon());
        assertRest(false, 0);
        // while { (a|b);c }
        setSource(a.or(b).seq(c).whileDo());
        assertEdge(this.aCall, c.seq(a.or(b).seq(c).whileDo()));
        assertEdge(this.bCall, c.seq(a.or(b).seq(c).whileDo()));
        assertSuccFail(delta(), epsilon());
        assertRest(false, 0);
        // while { if a }
        setSource(a.ifNoElse().whileDo());
        assertEdge(this.aCall, source());
        assertSuccFail(delta(), epsilon().seq(source().ifNoElse()));
        assertRest(false, 0);
    }

    @Test
    public void testAtom() {
        Term a = this.a;
        Term b = this.b;
        Term c = this.c;
        // atomic true
        setSource(epsilon().atom());
        assertSuccFail(null, null);
        assertRest(true, 0);
        // atomic a
        setSource(a.atom());
        assertEdge(this.aCall, epsilon());
        assertSuccFail(null, null);
        assertRest(false, 0);
        // atomic { a; b }
        setSource(a.seq(b).atom());
        assertEdge(this.aCall, b.transit());
        assertSuccFail(null, null);
        assertRest(false, 0);
        // atomic { if a;b else b;c }
        setSource(a.seq(b).ifElse(b.seq(c)).atom());
        assertEdge(this.aCall, b.transit());
        assertSuccFail(delta().atom(), b.seq(c).atom());
        assertRest(false, 0);
    }

    @Test
    public void testTransit() {
        Term a = this.a;
        Term c = this.c;
        // @a
        setSource(a.transit());
        assertEdge(this.aCall, epsilon());
        assertSuccFail(null, null);
        assertRest(false, 1);
        // @((a|skip).c)
        setSource(a.or(epsilon()).seq(c).transit());
        assertEdge(this.aCall, c.transit());
        assertEdge(this.cCall, epsilon());
        assertSuccFail(null, null);
        assertRest(false, 1);
        // @(alap { a;a; })
        setSource(a.seq(a).alap().transit());
        assertEdge(this.aCall, a.transit().seq(a.seq(a).alap()).transit());
        assertSuccFail(delta(), epsilon());
        assertRest(false, 1);
    }

    /** Predicts an outgoing transition of the current state. */
    private void assertEdge(Call call, Term target) {
        OutEdge edge = new OutEdge(call, target);
        Assert.assertTrue(String.format("%s not in %s", edge, this.edges),
            this.edges.remove(edge));
    }

    /** Predicts the success and failure of the current state.
     * Should be invoked after all regular transitions have been predicted.
     */
    private void assertSuccFail(Term success, Term failure) {
        Assert.assertEquals(Collections.emptyList(), this.edges);
        Assert.assertEquals(success, source().getSuccess());
        Assert.assertEquals(failure, source().getFailure());
    }

    /** Predicts the final nature and transition depth of the current state. */
    private void assertRest(boolean isFinal, int depth) {
        Assert.assertEquals(isFinal, source().isFinal());
        Assert.assertEquals(depth, source().getTransitDepth());
    }

    private Term delta() {
        return p.delta();
    }

    private Term epsilon() {
        return p.epsilon();
    }

    private Term source() {
        return this.source;
    }

    private void setSource(Term term) {
        this.source = term;
        this.edges = new ArrayList<OutEdge>(term.getOutEdges());
        // make sure the other values are properly computed
        this.source.getSuccess();
        this.source.getFailure();
        this.source.isFinal();
        this.source.getTransitDepth();
        if (DEBUG) {
            System.out.println(this.source.toDebugString());
            System.out.println();
        }
    }

    /** Returns the rule with a given name. */
    private Rule getRule(String name) {
        return this.grammar.getRule(name);
    }

    private Term source;
    private Collection<OutEdge> edges;
    private final Grammar grammar;

    {
        Grammar grammar;
        try {
            grammar = Groove.loadGrammar(CONTROL_DIR + "abc").toGrammar();
        } catch (Exception e) {
            fail(e.getMessage());
            grammar = null;
        }
        this.grammar = grammar;
    }

    private final Call aCall, bCall, cCall;
    private final Term a, b, c;
    {
        this.aCall = new Call(getRule("a"));
        this.bCall = new Call(getRule("b"));
        this.cCall = new Call(getRule("c"));
        this.a = p.call(this.aCall);
        this.b = p.call(this.bCall);
        this.c = p.call(this.cCall);
    }

    private final static Term p = Term.prototype();
    private static final String CONTROL_DIR = "junit/control/";
    private static final boolean DEBUG = true;

}
