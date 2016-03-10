package pomdp;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import env.*;

public class BeliefSpace {
	
	//fields
	private ArrayList<FoodNode> foodNodes;
	private ArrayList<PoliceNode> policeNodes;
	private ArrayList<ArrayList<State>> states;
	private Environment env;
	private int sourceVetex;
	private int goalVertex;
	
	//constructor
	public BeliefSpace(Environment env, int source, int goal, PrintWriter out) throws IOException {
		states = new ArrayList<ArrayList<State>>();
		foodNodes = new ArrayList<FoodNode>();
		policeNodes = new ArrayList<PoliceNode>();
		this.sourceVetex=source;
		this.goalVertex=goal;
		this.env=env;
		initialize(out);
	}

	
	//Initializes the network.
	private void initialize(PrintWriter out) throws IOException {
		createStates(out);
		calculateTransitionStates();
	}
	
	//creating states
	private void createStates(PrintWriter out) throws IOException {
		//graph, police and food probability nodes
		int V = env.Graph().V();
		VertexInfo VI = env.Info();
		for (int i = 0; i < V; i++) {
			double foodProbabilty = VI.getFoodProbability(i);
			FoodNode foodNode = new FoodNode(i,foodProbabilty);
			foodNodes.add(foodNode);
			double policeProbabilty = VI.getPoliceProbability(i);
			PoliceNode policeNode = new PoliceNode(i,policeProbabilty);
			policeNodes.add(policeNode);
		}

		//initializing state certinties
		int base = 3; //number of possibilities per node in the array
		int factor= (int) Math.pow(base,env.V()); //number of possible permutations for given number 'base'
		int [][] foodCertinties = new int [factor+1][env.V()]; //food certinties array
		int [][] policeCertinties = new int [factor+1][env.V()]; //police certinties array
		fullJoint(foodCertinties,env.V(),0,1,factor,base);
		fullJoint(policeCertinties,env.V(),0,1,factor,base);
		
		//for each vertex
		State newState; 
		for (int i = 0; i< V; i++) {
			ArrayList<State> iStates = new ArrayList<State>();
			int [] connected = env.Graph().connectedVetexes(i);
			//assign states with current vertex
			for (int j = 1; j<=factor; j++) {
				for (int k = 1; k<=factor; k++) {
					if (legalState (foodCertinties[j],policeCertinties[k],connected,i)) {
						newState = new State(i,foodCertinties[j],policeCertinties[k],connected,goalVertex,env.Graph(),states);			
						iStates.add(newState);
					} 
					else {
						writeIlligalMoveToFlie(out,foodCertinties[j],policeCertinties[k],i);
					}
				}
			}
			
			//add current vertex states
			states.add(iStates);
			
		}
	}
	
	//writing illigal moves to file
	private void writeIlligalMoveToFlie (PrintWriter out, int[] foodCertinties,int[] policeCertinties, int i) {
		StringBuilder illigalMove = new StringBuilder();
		illigalMove.append("at v"+i+": ");
		illigalMove.append("food:");
		for (int q=0; q<foodCertinties.length; q++) 
			illigalMove.append(foodCertinties[i]+" ");		
		illigalMove.append(", police:");
		for (int q=0; q<policeCertinties.length; q++) 
			illigalMove.append(policeCertinties[q]+" ");	
		out.println(illigalMove);
	}
	
	//assign all legal transitions for each legal state
	private void calculateTransitionStates () {
		for (int i=0; i<states.size(); i++) {
			ArrayList<State> iStates =  states.get(i);
			for (int j = 0; j<iStates.size(); j++) {
				iStates.get(j).calculateLegalTransitionStates();
			}
		}
	}
	
	//calculating optimal policy for the given environment
	public void calculateOptimalPolicy () {
		boolean stopIteration = false;
		while (!stopIteration) { //while utilities keep changing 
			for (int i=states.size()-1; i>=0; i--) {
				ArrayList <State> iStates = states.get(i);
				for (int j=iStates.size()-1; j>=0; j--) {
					State currentState =  iStates.get(j);
					valueIteration(currentState);
				}
			}
			if (changedUtilities()==0)
				stopIteration=true;
		}	
	}
	
	private void valueIteration (State currentState) {
		//state is terminal, no transitions, utility always 0
		if (currentState.isTerminal()) {
			currentState.setUtility(currentState.getUtility(),null);
			return;
		}
		
		//state is not terminal, find best utility
		ArrayList <ArrayList <State>> transitionStates = currentState.getLegalTransitionStates();
		ArrayList <State> transitionGroup = new ArrayList <State>();
		Double[] utilities = new Double [transitionStates.size()];
		double tempUtility, weight, probability, nextUtility;

		//for each transition group (action)
		for (int i=0; i<transitionStates.size(); i++) {
			transitionGroup = transitionStates.get(i);
			tempUtility=0;
			for (int j=0; j<transitionGroup.size(); j++) { //go over every transition
				State nextActionState =  transitionGroup.get(j);
				probability=calculateTransitionProbability(currentState,nextActionState);
				if (probability>0) { //if probability of occorence>0 then add to utility mulyplied by probability
					weight = -env.Graph().getWeight(currentState.getCurrentIndex(),nextActionState.getCurrentIndex());
					if (currentState.hasFood())
						weight*=0.5;	
					nextUtility = nextActionState.getUtility();	
					tempUtility += currentState.getReward()+probability*(weight+nextUtility);	
				}

			}
			utilities[i]=tempUtility; //store temp utility
		}
		
		//find max utility 
		double maxUtility = currentState.getUtility();
		for (int i=0; i<utilities.length; i++) {
			if (utilities[i]>maxUtility) {
				maxUtility=utilities[i];
				transitionGroup=transitionStates.get(i);
			}
		}
		
		//update utility
		currentState.setUtility(maxUtility, transitionGroup);
	}
	
	//checks if are changed utilities 
	private int changedUtilities () {
		int sum=0;
		for (int i=0; i<states.size(); i++){
			ArrayList<State> iStates =  states.get(i);
			for (int j=0; j<iStates.size(); j++) {
				State current =  iStates.get(j);
				if (current.changed())
					sum++;
			}
		}
		return sum;
	}
	
	//prints optimal policy
	public void PrintOptimalPolicy (VertexInfo generetedGraph) {
		double maxIter=Double.POSITIVE_INFINITY;
		State state = findState(sourceVetex,generetedGraph,null);
		System.out.println("starting state: " +state);
		//for every transition state:
		//print it and go to next one depends on the transition outcome
		ArrayList <State> transitionStates = state.getNextOptimalStates();
		if (transitionStates==null) {
			System.out.println("No avalible transitions, agent has no moves!");
			System.out.println("----------------------------------------------------");
			return;
		}
		State currentTransitionState=transitionStates.get(0);
		int currentTransitionVertex = currentTransitionState.getCurrentIndex();
		State newState =  findState(currentTransitionVertex,generetedGraph,state);
		System.out.println("traversing to v"+currentTransitionVertex+":");
		System.out.println(newState);
		policyLoop(generetedGraph, newState,maxIter);
	}
	
	//helper function for printing optimal policy
	private void policyLoop (VertexInfo generetedGraph, State state, double maxIter) {
		if (maxIter==0) //debug condition
			return;
		if (state.isTerminal() && state.isGoal()) {
			System.out.println("Reached to goal!");
			System.out.println("----------------------------------------------------");
			return;
		}
		ArrayList <State> transitionStates = state.getNextOptimalStates();
		if (transitionStates==null || (state.isTerminal() && !state.isGoal())) {
			System.out.println("No avalible transitions, agent has no moves!");
			System.out.println("----------------------------------------------------");
			return;
		}
		int currentTransitionVertex = transitionStates.get(0).getCurrentIndex();
		State newState =  findState(currentTransitionVertex,generetedGraph,state);
		System.out.println("traversing to v"+currentTransitionVertex+":");
		System.out.println("current state: " +newState);
		policyLoop(generetedGraph, newState,(maxIter-1));
	}
	
	//finds current state based on input and privious state
	public State findState (int v, VertexInfo VI, State prevState) {
		ArrayList <State> iStates = states.get(v);
		int [] policeCertinties = new int [env.V()];
		int [] foodCertinties = new int [env.V()];
		int [] connectedVetices = env.Graph().connectedVetexes(v);
		for (int i=0; i<env.V(); i++) {
			if (connectedVetices[i]==1 || i==v) {
				policeCertinties[i]=(int)VI.getPoliceProbability(i);
				foodCertinties[i]=(int)VI.getFoodProbability(i);
			}
			else {
				policeCertinties[i]=2;
				foodCertinties[i]=2;
			}
			if (prevState!=null) {
				if (prevState.getFoodCertinties()[i]!=2)
					foodCertinties[i]=prevState.getFoodCertinties()[i];
				if (prevState.getPoliceCertinties()[i]!=2)
					policeCertinties[i]=prevState.getPoliceCertinties()[i];
			}
		}
		for (int i=0; i<iStates.size(); i++) {
			if (iStates.get(i).equalTo(foodCertinties, policeCertinties, v)) {
				return  iStates.get(i);
			}
		}
		return null;
	}
	

	//initialize certainties
	private static void fullJoint (int [][] certinties,int v, int pos, int line, int factor, int base) {
		if (pos>=v) 
			return;
		for (int i=line; i<line+factor;i++){
			for (int j=1; j<=base; j++) {
				if (i<(line+j*(factor/base)) && i>=(line+(j-1)*(factor/base)))
					certinties[i][pos]=j-1;
			}
		}
		for  (int i=0; i<base; i++) {
			fullJoint(certinties,v,pos+1,line+i*(factor/base),factor/base,base);
		}
	}
	
	//calculating transition probability
	private double calculateTransitionProbability (State sourceState, State destinationState) {
		int [] foodSource = sourceState.getFoodCertinties();
		int [] policeSource = sourceState.getPoliceCertinties();
		int [] foodDest = destinationState.getFoodCertinties();
		int [] policeDest = destinationState.getPoliceCertinties();
		double probability=1; //stating at 1.0
		int v = env.V();
		for (int i=0; i<v; i++) {
			//same in source and destination, no change
			if ((foodSource[i]==foodDest[i]) && (policeSource[i]==policeDest[i]))
				continue;		
			
			//food exist
			if (foodSource[i]==2 && foodDest[i]==1) 
				probability=probability*foodNodes.get(i).getFoodPorbability();
			
			//food does not exist
			if (foodSource[i]==2 && foodDest[i]==0)
				probability=probability*Math.abs((1-foodNodes.get(i).getFoodPorbability()));
			
			//police exist
			if (policeSource[i]==2 && policeDest[i]==1)
				probability=probability*policeNodes.get(i).getPolicePorbability();
			
			//police does not exist
			if (policeSource[i]==2 && policeDest[i]==0)
				probability=probability*Math.abs((1-policeNodes.get(i).getPolicePorbability()));
		}
		return probability;
	}

	//print all states & transitions to out
	public void printStates(PrintWriter out) {
		out.println("Legal Belife States:");
		for (int i=0; i<states.size(); i++) {
			ArrayList <State> curentVertexStates = states.get(i);
			for(int j=0; j<curentVertexStates.size(); j++) {
				out.println("\nState:\n"+curentVertexStates.get(j));
				ArrayList <ArrayList <State>> transitionStates = curentVertexStates.get(j).getLegalTransitionStates();
				if (transitionStates!=null) {
					out.println("Legal transitions:");
				for (int k=0; k<transitionStates.size(); k++) {
					ArrayList <State> transitionGroup = transitionStates.get(k);
					for (int m=0; m<transitionGroup.size(); m++) {
						State currentTransitionState=transitionGroup.get(m);
						out.println(currentTransitionState);
					}
				} 
			}
			else
				out.println("NO LEGAL TRANSITION STATES!\n");
			}
		}
	}
	
	//check if state with those params is legal for the eu refugee problem
	private boolean legalState (int[] foodCertinties,int[] policeCertinties, int [] connectedVetexes, int sourceIndex){
		//current vertex unknown
		if (foodCertinties[sourceIndex]==2 || policeCertinties[sourceIndex]==2)
			return false;	
		
		//current vertex has police
		if (policeCertinties[sourceIndex]==1)
			return false;
		
		//connected vertex unknown
		for (int i=0; i<connectedVetexes.length; i++) {
			if (connectedVetexes[i]==1 && (foodCertinties[i]==2 || policeCertinties[i]==2))
				return false;
		}
		
		//police known and food unknown or other way around in the same vertex
		for (int i=0; i<connectedVetexes.length; i++) {
			if ((foodCertinties[i]!=2 && policeCertinties[i]==2) || (foodCertinties[i]==2 && policeCertinties[i]!=2))
				return false;
		}
		return true;
	}

}
