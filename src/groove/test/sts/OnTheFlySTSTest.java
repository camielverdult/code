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
package groove.test.sts;

import groove.lts.MatchResult;
import groove.sts.Location;
import groove.sts.OnTheFlySTS;
import groove.sts.STSException;
import groove.sts.SwitchRelation;
import groove.trans.HostGraph;
import junit.framework.Assert;

/**
 * Tests for an on-the-fly sts.
 * @author Vincent de Bruijn
 * @version $Revision $
 */
@SuppressWarnings("all")
public class OnTheFlySTSTest extends STSTest {

    private OnTheFlySTS onTheFlySTS;

    /**
     * Constructor.
     * @param name The name of the test.
     */
    public OnTheFlySTSTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() {
        super.setUp();
        this.onTheFlySTS = new OnTheFlySTS();
        this.sts = this.onTheFlySTS;
    }

    /**
     * Tests hostGraphToStartLocation.
     */
    public void testHostGraphToStartLocation() {
        Location l = this.onTheFlySTS.hostGraphToStartLocation(this.g2);
        Assert.assertSame(this.onTheFlySTS.getCurrentLocation(), l);
        Assert.assertSame(this.onTheFlySTS.getStartLocation(), l);
    }

    @Override
    protected void testRuleMatchToSwitchRelation(HostGraph sourceGraph,
            MatchResult match) {
        this.onTheFlySTS.hostGraphToStartLocation(sourceGraph);
        try {
            SwitchRelation sr =
                this.onTheFlySTS.ruleMatchToSwitchRelation(sourceGraph, match);

            Assert.assertNotNull(sr);
            Assert.assertEquals(
                this.onTheFlySTS.getSwitchRelation(SwitchRelation.getSwitchIdentifier(
                    sr.getGate(), sr.getGuard(), sr.getUpdate())), sr);
        } catch (STSException e) {
            Assert.fail(e.getMessage());
        }
    }

}
