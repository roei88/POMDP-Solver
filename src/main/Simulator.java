/**
 * @author Roy Cohen
 * 
 */

package main;
import java.io.IOException;
import java.io.PrintWriter;
import env.Environment;
import env.VertexInfo;
import io.In;
import io.StdIn;
import io.StdOut;
import pomdp.BeliefSpace;

public class Simulator {
    // change to TRUE to turn on debug mode:
	private static final boolean DEBUG_MODE = false;
   
	//fields
	private static final String NEWLINE = System.getProperty("line.separator");   
	private static Environment env;
	private static BeliefSpace pomdpBeliedSpace;
	private static boolean stopSimulation=false;
	private static int sourceVetex;
	private static int destinationVetex;
	private static VertexInfo graphInstance=null;

	public static void main(String[] args) throws IOException {	
		init(args);
		simulate(args);
	}
	
	//Initializing simulation
	private static void init(String[] args) throws IOException{
		
		//creating the world environment
		In graphIn = new In(args[0]);
		In vertexInfoIn = new In(args[1]);		
		env = new Environment(graphIn, vertexInfoIn);
		env.setDebugMode(DEBUG_MODE);
			
		//user query
		if (!env.onDebugMode()) {
			System.out.println("EU Refugee Problem - POMDP Solver");
			System.out.println("Graph input:");
			System.out.println(env.Graph());
			System.out.println("\nFood & police probabilities:");
			System.out.println(env.Info());
			System.out.println("Please choose agent source vertex from v0 to v"+(env.V()-1)+":");
			sourceVetex = StdIn.readInt();
			System.out.println("Please choose agent destination vertex:");
			while ((destinationVetex = StdIn.readInt())==sourceVetex) {
				System.out.println("Please choose different vertex.");
			}
		}
		
		//DEBUG_MODE
		else { 
			sourceVetex = 1;
			destinationVetex = 4;
		}
				
		//creating belief space & policy
		PrintWriter out =new PrintWriter("illegal states.txt"); 
		pomdpBeliedSpace = new BeliefSpace(env,sourceVetex,destinationVetex,out); 
		out.close();
		pomdpBeliedSpace.calculateOptimalPolicy();
		out = new PrintWriter("belief_space.txt");
		pomdpBeliedSpace.printStates(out);
		out.close();
		System.out.print("POMDP Belied space created.\nStates & transitions printed to output files.\n\n");	
	}
	
	
	//Main simulator function
	private static void simulate(String[] args) throws IOException{
		
		
		while (!stopSimulation) {
			//choose action
			StdOut.println("Please choose action:");
			StdOut.println("1. Print graph" + NEWLINE+ 
							"2. Set new graph & route" + NEWLINE+ 
							"3. Set police & food locations" + NEWLINE+ 
							"4. Run simulation" + NEWLINE+ 
							"5. Quit");
			int firstAction = StdIn.readInt();
			
			if (firstAction==1) {
				StdOut.println(env.Graph());
			}
			if (firstAction==2) {
				init(args);
				graphInstance=null;
			}
			if (firstAction==3) {
				In vertexInfoIn = new In("./inputs/locations.txt");		
				graphInstance = new VertexInfo(vertexInfoIn);
				StdOut.println("Locations set to:");
				StdOut.println(graphInstance);
			}
			if (firstAction==4) {
				if (graphInstance==null) 
					StdOut.println("Please set graph instance before running simulation:.\n\n");
				else {
					StdOut.println(graphInstance);
					pomdpBeliedSpace.PrintOptimalPolicy(graphInstance);
				}
			}
			if (firstAction==5) {
				stopSimulation=true;
			}
		}
	}
	
}