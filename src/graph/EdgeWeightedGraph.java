package graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import dataStructures.Bag;
import dataStructures.Stack;
import io.In;

public class EdgeWeightedGraph {
    private static final String NEWLINE = System.getProperty("line.separator");

    private final int V;
    private int E;
    private Bag<Edge>[] adj;
    private ArrayList<int[]> connectedVertexes = null;
    
    /**
     * Initializes an empty edge-weighted graph with <tt>V</tt> vertices and 0 edges.
     *
     * @param  V the number of vertices
     * @throws IllegalArgumentException if <tt>V</tt> < 0
     */
    @SuppressWarnings("unchecked")
	public EdgeWeightedGraph(int V) {
        if (V < 0) throw new IllegalArgumentException("Number of vertices must be nonnegative");
        this.V = V;
        this.E = 0;
        adj = (Bag<Edge>[]) new Bag[V];
        for (int v = 0; v < V; v++) {
            adj[v] = new Bag<Edge>();
        }
    }

    /**
     * Initializes a random edge-weighted graph with <tt>V</tt> vertices and <em>E</em> edges.
     *
     * @param  V the number of vertices
     * @param  E the number of edges
     * @throws IllegalArgumentException if <tt>V</tt> < 0
     * @throws IllegalArgumentException if <tt>E</tt> < 0
     */
    public EdgeWeightedGraph(int V, int E) {
        this(V);
        if (E < 0) throw new IllegalArgumentException("Number of edges must be nonnegative");
        Random random = new Random();
        
        for (int i = 0; i < E; i++) {
            int v = random.nextInt(V);	
            int w = random.nextInt(V);
            double weight = random.nextInt()*random.nextDouble();
            Edge e = new Edge(v, w, weight);
            addEdge(e);
        }
    }

    /**  
     * Initializes an edge-weighted graph from an input stream.
     * The format is the number of vertices <em>V</em>,
     * followed by the number of edges <em>E</em>,
     * followed by <em>E</em> pairs of vertices and edge weights,
     * with each entry separated by whitespace.
     *
     * @param  in the input stream
     * @throws IndexOutOfBoundsException if the endpoints of any edge are not in prescribed range
     * @throws IllegalArgumentException if the number of vertices or edges is negative
     */
    public EdgeWeightedGraph(In in) {
        this(in.readInt());
        int E = in.readInt();
        if (E < 0) throw new IllegalArgumentException("Number of edges must be nonnegative");
        for (int i = 0; i < E; i++) {
            int v = in.readInt();
            int w = in.readInt();
            double weight = in.readDouble();
            Edge e = new Edge(v, w, weight);
            addEdge(e);
        }
    }

    /**
     * Initializes a new edge-weighted graph that is a deep copy of <tt>G</tt>.
     *
     * @param  G the edge-weighted graph to copy
     */
    public EdgeWeightedGraph(EdgeWeightedGraph G) {
        this(G.V());
        this.E = G.E();
        
        for (int v = 0; v < G.V(); v++) {
            // reverse so that adjacency list is in same order as original
            Stack<Edge> reverse = new Stack<Edge>();
            for (Edge e : G.adj[v]) {
            	Edge eCopy = new Edge(e);
                reverse.push(eCopy);
            }
            for (Edge e : reverse) {
                adj[v].add(e);
            }
        }
    }


    /**
     * Returns the number of vertices in this edge-weighted graph.
     *
     * @return the number of vertices in this edge-weighted graph
     */
    public int V() {
        return V;
    }

    /**
     * Returns the number of edges in this edge-weighted graph.
     *
     * @return the number of edges in this edge-weighted graph
     */
    public int E() {
        return E;
    }

    // throw an IndexOutOfBoundsException unless 0 <= v < V
    private void validateVertex(int v) {
        if (v < 0 || v >= V)
            throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V-1));
    }

    /**
     * Adds the undirected edge <tt>e</tt> to this edge-weighted graph.
     *
     * @param  e the edge
     * @throws IndexOutOfBoundsException unless both endpoints are between 0 and V-1
     */
    public void addEdge(Edge e) {
        int v = e.either();
        int w = e.other(v);
        validateVertex(v);
        validateVertex(w);
        adj[v].add(e);
        adj[w].add(e);
        E++;
    }

    /**
     * Returns the edges incident on vertex <tt>v</tt>.
     *
     * @param  v the vertex
     * @return the edges incident on vertex <tt>v</tt> as an Iterable
     * @throws IndexOutOfBoundsException unless 0 <= v < V
     */
    public Iterable<Edge> adj(int v) {
        validateVertex(v);
        return adj[v];
    }

    /**
     * Returns the degree of vertex <tt>v</tt>.
     *
     * @param  v the vertex
     * @return the degree of vertex <tt>v</tt>               
     * @throws IndexOutOfBoundsException unless 0 <= v < V
     */
    public int degree(int v) {
        validateVertex(v);
        return adj[v].size();
    }

    /**
     * Returns all edges in this edge-weighted graph.
     * To iterate over the edges in this edge-weighted graph, use foreach notation:
     * <tt>for (Edge e : G.edges())</tt>.
     *
     * @return all edges in this edge-weighted graph, as an iterable
     */
    public Iterable<Edge> edges() {
        Bag<Edge> list = new Bag<Edge>();
        for (int v = 0; v < V; v++) {
            int selfLoops = 0;
            for (Edge e : adj(v)) {
                if (e.other(v) > v) {
                    list.add(e);
                }
                // only add one copy of each self loop (self loops will be consecutive)
                else if (e.other(v) == v) {
                    if (selfLoops % 2 == 0) list.add(e);
                    selfLoops++;
                }
            }
        }
        return list;
    }
    
    /**
     * TODO: ADD COMMENTS
     * @return 
     */
    public Edge nextMove(int s, int d) {
    	Iterable<Edge> edgeList = adj(s);
    	Iterator<Edge> edgeListIterator = edgeList.iterator();
    	Edge move;
    	while (((move=(Edge) edgeListIterator.next())).other(s)!=d);
    	return move;
    }

    /**
     * Returns a string representation of the edge-weighted graph.
     * This method takes time proportional to <em>E</em> + <em>V</em>.
     *
     * @return the number of vertices <em>V</em>, followed by the number of edges <em>E</em>,
     *         followed by the <em>V</em> adjacency lists of edges
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("|V|="+V+ NEWLINE);
        s.append("|E|="+E+ NEWLINE+NEWLINE);
        s.append("Edges from:" + NEWLINE+NEWLINE);
        for (int v = 0; v < V; v++) {
            s.append("v"+v + ": ");
            for (Edge e : adj[v]) {
                s.append(e + "    ");
            }
            s.append(NEWLINE+NEWLINE);
        }
        return s.toString();
    }

    public void updateEdgeWeight(int v) {   	
		for (int i=0; i<V; i++){
			Iterator<Edge> edgeIteror= adj[i].iterator();
			while (edgeIteror.hasNext()){
				Edge currEdge = edgeIteror.next();
				if (currEdge.hasVertex(v))
					currEdge.updateWeight(Double.POSITIVE_INFINITY);
			}
		}
		
    }

    
  //returning weight for edge <source,destination>
  	public double getWeight (int source, int destination) {
  		Iterator<Edge> edgesFromSource = adj[source].iterator();
  		Edge edge;
  		while (edgesFromSource.hasNext()) {
  			edge=edgesFromSource.next();
  			if (edge.hasVertex(destination))
  				return edge.weight();  				
  		}
  		return -1;
  	}
  	
  	public int [] connectedVetexes (int v) {
  		if (connectedVertexes==null) {
  		
  		connectedVertexes = new ArrayList<int[]>();
  		for (int i=0; i<adj.length; i++) {
  			Bag<Edge> bag = this.adj[i];
  			Iterator<Edge> edges = bag.iterator();
  			int [] curr = new int [V];
  			while (edges.hasNext()) {
  				Edge edge = edges.next();
				curr[edge.other(i)]=1;
  			}
  			connectedVertexes.add(curr);
  		}
  		}
		return connectedVertexes.get(v);
	}

}


