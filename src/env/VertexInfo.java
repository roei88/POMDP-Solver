package env;

import io.In;

public class VertexInfo {
	
	//fields
    private static final String NEWLINE = System.getProperty("line.separator");
    private final int V; 
    private double[][] vertices; 
   
    //constructors
	public VertexInfo(int V) {
        if (V < 0) throw new IllegalArgumentException("Number of vertices must be nonnegative");
        this.V = V;
        vertices = new double[V][2];
    }
	public VertexInfo(In in) {
        this(in.readInt());
        for (int i = 0; i < V; i++) {
            int v = in.readInt();
            double foodProbability = in.readDouble();
            double policeProbability = in.readDouble();
            vertices[v][0] = foodProbability;
            vertices[v][1] = policeProbability;
        }

    }
	public VertexInfo(VertexInfo vi) {
    	V= vi.V;
    	vertices = new double[V][2];
    	for(int i=0; i<vertices.length; i++)
    		  for(int j=0; j<vertices[i].length; j++)
    			  vertices[i][j]=vi.vertices[i][j];
      }
    
   //probabilities getters & setters
    public double getFoodProbability(int v){
    	return vertices[v][0];
    }
    public void setFoodProbability(int v, double probability){
    	vertices[v][0] = probability;
    }
    public double getPoliceProbability(int v){
    	return vertices[v][1];
    }
    public void setPoliceProbability(int v, double probability){
    	vertices[v][1] = probability;
    }
    
    //appending vertexes info
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("V#\t"+"Food\t"+"Police\t" + " " + NEWLINE);
        s.append("---\t"+"------\t"+"----\t" + " " + NEWLINE);
        for (int v = 0; v < V; v++) {
            s.append("v"+v+"\t");
            s.append("  "+vertices[v][0]+"  \t");
            s.append("  "+vertices[v][1]+"  \t");
            s.append(NEWLINE);
        }
        return s.toString();
    }
    
}
