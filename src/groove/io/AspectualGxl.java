/* $Id: AspectualGxl.java,v 1.5 2007-04-24 10:06:47 rensink Exp $ */
package groove.io;

import groove.graph.Graph;
import groove.graph.GraphShape;
import groove.graph.aspect.AspectGraph;
import groove.util.FormatException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Class to marshal and unmarshal {@link AspectGraph}s as GXL files.
 * @author Arend Rensink
 * @version $Revision $
 */
public class AspectualGxl implements Xml<AspectGraph> {
	/** Constructs a reader on top of an {@link UntypedGxl}. */
	public AspectualGxl() {
		this(new UntypedGxl());
	}
	
	/** Constructs a reader on top of a given graph marshaller. */
	public AspectualGxl(Xml<Graph> innerMarshaller) {
		this.marshaller = innerMarshaller;
	}

	/** 
	 * Converts the aspect graph to a plain graph and marshals it 
	 * using the inner marshaller.
	 * @see AspectGraph#toPlainGraph()
	 */
	public void marshalGraph(AspectGraph graph, File file) throws FormatException,
			IOException {
		marshaller.marshalGraph(graph.toPlainGraph(), file);
	}

	/**
	 * Unmarshals the file using the inner marshaller 
	 * and converts the resulting graph to an {@link AspectGraph}.
	 * @see AspectGraph#fromPlainGraph(GraphShape)
	 */
	public AspectGraph unmarshalGraph(File file) throws FormatException, IOException {
			return AspectGraph.getFactory().fromPlainGraph(marshaller.unmarshalGraph(file));
	}

    /** Delegates the method to the inner marshaller. */
    public void setPropertyKeys(Collection<String> propertyNames) {
		marshaller.setPropertyKeys(propertyNames);
	}

	/** The marshaller to get and store graphs, which are then converted to aspect graphs. */
	private final Xml<Graph> marshaller;
}
