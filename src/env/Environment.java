package env;

import graph.EdgeWeightedGraph;
import io.In;

/**
 *  The <tt>Enviroment</tt> class represents the world state of the EU refugee problem.
 *  it contains an edge-weighted graph of vertices, and VertexInfo type which represents
 *  the Borders, food amount and locations of the various agents.
 *  @author Roy Cohen
 */

public class Environment{
	
    private static final String NEWLINE = System.getProperty("line.separator");
	
	EdgeWeightedGraph G;
	private VertexInfo VI;

	private boolean _debugMode = false;;
	
    /**  
     * Initializes an Enviroment from two input stream.
     * @param  graphIn  edge-weighted graph input stream
     * @param  vertexInfoIn extended world info input stream
     */
	public Environment(In graphIn, In vertexInfoIn){
		// Creating the graph:
        G = new EdgeWeightedGraph(graphIn);
        // Creating the vertex info:
        VI = new VertexInfo(vertexInfoIn);
	}
	
	//hard-copy constructor
	public Environment(Environment env){
		// Creating the graph:
        G = new EdgeWeightedGraph(env.G);
        // Creating the vertex info:
        VI = new VertexInfo(env.VI);
	}
	
    /**
     * Returns the number of edges in this edge-weighted graph.
     *
     * @return the number of edges in this edge-weighted graph
     */
    public int E() {
        return G.E();
    } 	

    /**
     * Returns the number of vertices in this edge-weighted graph.
     *
     * @return the number of vertices in this edge-weighted graph
     */
    public int V() {
        return G.V();
    } 
	
    
    /**
     * Returns the edge-weighted graph.
     *
     * @return the edge-weighted graph.
     */
    public EdgeWeightedGraph Graph() {
        return G;
    } 	

    /**
     * Returns the number of vertices in this edge-weighted graph.
     *
     * @return the number of vertices in this edge-weighted graph
     */
    public VertexInfo Info() {
        return VI;
    } 
    
    /**
     * Updates the locations of the various agents.
     * @return the number of vertices in this edge-weighted graph
     */
    public void updadeLocations() {
        
    } 
    
    /**
     * Returns a string representation of Enviroment.
     * @return the number of vertices <em>V</em>, followed by the number of edges <em>E</em>,
     *         followed by the <em>V</em> adjacency lists of edges, followed by additional world state info.
     */
	public String toString(){
		StringBuilder s = new StringBuilder();
        s.append(G + " " + NEWLINE);
        s.append(VI + " " + NEWLINE);
        return s.toString();
	
	}

	// supporting debug:
	public void setDebugMode(boolean debugMode) {
		_debugMode  = debugMode;
	}
	
	public boolean onDebugMode() {
		return _debugMode;
	}
}
