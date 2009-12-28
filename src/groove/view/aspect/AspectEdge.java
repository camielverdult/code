/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id: AspectEdge.java,v 1.10 2008-01-30 09:31:33 iovka Exp $
 */
package groove.view.aspect;

import groove.graph.AbstractBinaryEdge;
import groove.graph.DefaultLabel;
import groove.graph.Edge;
import groove.graph.Label;
import groove.view.FormatException;

import java.util.Collection;
import java.util.List;

/**
 * Edge enriched with aspect data. Aspect edge labels are interpreted as
 * {@link DefaultLabel}s.
 * @author Arend Rensink
 * @version $Revision$
 */
public class AspectEdge extends AbstractBinaryEdge<AspectNode,Label,AspectNode>
        implements AspectElement {
    /**
     * Constructs a new edge from an array of end nodes, a label, and a
     * collection of aspect values.
     * @param ends the end nodes of the new edge
     * @param label the label of the new edge
     * @param values the aspect values for the new edge
     * @throws FormatException
     */
    @Deprecated
    public AspectEdge(List<AspectNode> ends, DefaultLabel label,
            AspectValue... values) throws FormatException {
        super(ends.get(SOURCE_INDEX), label, ends.get(TARGET_INDEX));
        this.parseData = createParseData(label, values);
        testLabel();
    }

    /**
     * Constructs a new edge, with source and target node, label, and aspect
     * values as given.
     * @param source the source node for this edge
     * @param target the target node for this edge
     * @param parseData the aspect values for this edge.
     * @throws FormatException if the aspect values of <code>parseData</code>
     *         are inconsistent with those of the source or target nodes
     */
    AspectEdge(AspectNode source, AspectNode target, AspectMap parseData)
        throws FormatException {
        super(source, DefaultLabel.createLabel(parseData.getText()), target);
        for (AspectValue value : parseData.getDeclaredValues()) {
            if (!value.isEdgeValue()) {
                throw new FormatException(
                    "Aspect value '%s' cannot be used on edges", value);
            }
        }
        addInferences(parseData, source.getAspectMap(), target.getAspectMap());
        this.parseData = parseData;
        testLabel();
    }

    /**
     * Adds values to the aspect map of an edge that are inferred from source
     * and target nodes.
     * @param sourceData map of aspect values for the source node
     * @param targetData map of aspect values for the target node
     * @throws FormatException if an explicitly declared aspect value is
     *         overruled
     */
    private void addInferences(AspectMap edgeData, AspectMap sourceData,
            AspectMap targetData) throws FormatException {
        for (Aspect aspect : Aspect.allAspects) {
            AspectValue edgeValue = edgeData.get(aspect);
            AspectValue sourceValue = sourceData.get(aspect);
            AspectValue sourceInference =
                sourceValue == null ? null : sourceValue.sourceToEdge();
            AspectValue targetValue = targetData.get(aspect);
            AspectValue targetInference =
                targetValue == null ? null : targetValue.targetToEdge();
            AspectValue result =
                aspect.getMax(edgeValue, sourceInference, targetInference);
            if (result != null && !result.equals(edgeValue)) {
                edgeData.addInferredValue(result);
            }
        }
    }

    public AspectValue getValue(Aspect aspect) {
        AspectValue result = getAspectMap().get(aspect);
        if (result == null) {
            result = aspect.getDefaultValue();
        }
        return result;
    }

    public Collection<AspectValue> getDeclaredValues() {
        return this.parseData.getDeclaredValues();
    }

    /**
     * Returns the map from aspects to aspect values for this edge, lazily
     * creating it first.
     */
    public AspectMap getAspectMap() {
        return this.parseData;
    }

    /**
     * Returns the plain text label for the aspect edge.
     */
    public String getPlainText() {
        return this.parseData.toString();
    }

    /**
     * Returns the label that this edge gets, when compiled to a model edge.
     * Convenience method for {@code getAspectMap().toModelLabel(regExpr)}
     * @param regExpr if <code>true</code>, recognise regular expressions
     * @throws FormatException if the label contains a format error
     */
    public Label getModelLabel(boolean regExpr) throws FormatException {
        return getAspectMap().toModelLabel(regExpr);
    }

    /**
     * Includes the hash code of the associated aspect values.
     */
    @Override
    protected int computeHashCode() {
        int result = super.computeHashCode();
        for (Aspect aspect : Aspect.allAspects) {
            AspectValue value = getValue(aspect);
            if (value != null) {
                result += value.hashCode();
            }
        }
        return result;
    }

    /**
     * Tests equality of type, ends and aspect values.
     */
    @Override
    public boolean equals(Object obj) {
        return isTypeEqual(obj) && isEndEqual((Edge) obj)
            && isLabelEqual((Edge) obj) && isAspectEqual((AspectEdge) obj);
    }

    /**
     * Tests if the object is an {@link AspectEdge}.
     */
    @Override
    protected boolean isTypeEqual(Object obj) {
        return obj instanceof AspectEdge;
    }

    /** Tests if the aspect map of this edge equals that of the other. */
    protected boolean isAspectEqual(AspectEdge other) {
        for (Aspect aspect : Aspect.allAspects) {
            if (getValue(aspect) != other.getValue(aspect)) {
                return false;
            }
        }
        return true;
    }

    /**
     * This implementation makes sure that edges with node type labels are
     * ordered before other edge.
     */
    @Override
    protected int compareToEdge(Edge obj) {
        assert obj instanceof AspectEdge : String.format(
            "Can't compare aspect edge '%s' to non-aspect edge '%s'", this, obj);
        AspectEdge other = (AspectEdge) obj;
        int result;
        // first compare the source, then the node type property, then the
        // label, then the target
        result = source().compareTo(other.source());
        if (result == 0) {
            result = isNodeType() - other.isNodeType();
        }
        if (result == 0) {
            result = label().compareTo(other.label());
        }
        if (result == 0) {
            result = target().compareTo(other.target());
        }
        return result;
    }

    /** Tests if this aspect edge stands for a node type. */
    private int isNodeType() {
        return NodeTypeAspect.isNodeType(this) ? 0 : 1;
    }

    /**
     * This implementation defers to {@link #getPlainText()}
     */
    @Override
    protected String getLabelText() {
        return getPlainText();
    }

    /**
     * Tests if the parsed edge label is allowed by all inferred aspects.
     * @throws FormatException if there is an aspect whose value for this edge
     *         is incompatible with the edge label
     * @see Aspect#testLabel(Label, AspectValue, AspectValue)
     */
    protected void testLabel() throws FormatException {
        for (AspectValue declaredAspectValue : getDeclaredValues()) {
            Aspect aspect = declaredAspectValue.getAspect();
            AspectValue inferredValue = getAspectMap().get(aspect);
            aspect.testLabel(label(), declaredAspectValue, inferredValue);
        }
    }

    /** Callback factory method. */
    @Deprecated
    AspectMap createParseData(Label label, AspectValue[] values)
        throws FormatException {
        AspectMap result = new AspectMap(computeDeclaredAspectMap(values));
        return result;
    }

    /**
     * Converts an array of aspect values into an aspect map, and adds the
     * values inferred from the source and target nodes.
     * @param values the explicit aspect values for the edge
     * @return an aspect map combining the explicit and the inferred aspect
     *         values
     * @throws FormatException if <code>values</code> contains duplicate values
     *         for an aspect, or the values are inconsistent with the inferred
     *         values
     */
    @Deprecated
    final protected AspectMap computeDeclaredAspectMap(AspectValue[] values)
        throws FormatException {
        AspectMap result = new AspectMap();
        for (AspectValue value : values) {
            result.addDeclaredValue(value);
        }
        return result;
    }

    /**
     * The aspect information of the label, set at construction time.
     */
    private final AspectMap parseData;
}
