package pomdp;
import java.util.ArrayList;
import java.util.Iterator;

import graph.Edge;
import graph.EdgeWeightedGraph;

public class State {
	
	//fields
	private boolean terminalState;
	private int sourceIndex;
	private int goalIndex;
	private double utility;
	private double reward;
	private int  foodCertinties [];
	private int  policeCertinties [];
	private int  connectedVetexes [];
	private int  knownVertices [];
	private boolean utilityChanged = false;
	private boolean isGoal;
	private int utilityUpdates=0;
	private EdgeWeightedGraph graph;
	private ArrayList<ArrayList<State>> otherStates;
	private ArrayList<ArrayList<State>> legalTransitionStates;
	private ArrayList<State> optimalTransitionStates;
	
	//empty constructor
	public State() {
		terminalState=false;
		sourceIndex=0;
		utility=0;
		reward=0;
		foodCertinties=null;
		policeCertinties=null;
		connectedVetexes=null;
		graph=null;
		otherStates=null;
		legalTransitionStates=null;
		optimalTransitionStates=null;
		isGoal=false;
	}
	
	//constructor
	public State(int sourceIndex,int [] foodCertinties,int [] policeCertinties,int [] connectedVetexes,  int goal, EdgeWeightedGraph graph,ArrayList<ArrayList<State>> otherStates ) {
		this.sourceIndex=sourceIndex;
		this.foodCertinties=foodCertinties;
		this.policeCertinties=policeCertinties;
		this.connectedVetexes=connectedVetexes;
		this.isGoal=(sourceIndex==goal);
		this.goalIndex=goal;
		this.graph=graph;
		this.otherStates=otherStates;
		legalTransitionStates=null;
		optimalTransitionStates=null;
		if (hasPolice())
			reward=-1000000;
		else 
			reward=0;
			
		boolean blocked = false;
		for (int i=0; i<connectedVetexes.length; i++) {
			if ((policeCertinties[i]==1) && (i==goalIndex))
				blocked = true;
		}
		
		if(isGoal || blocked) {
			utility=0;
			terminalState=true;
		}
		else {
			utility=Double.NEGATIVE_INFINITY;
			//utility=-1000000;
			terminalState=false;
		}			
		
	}

	//current state has food
	protected boolean hasFood () {
		if (foodCertinties[sourceIndex]==1) 
			return true;
		return false;
	}
	
	//current state has police
	protected boolean hasPolice () {
		if (policeCertinties[sourceIndex]==1) 
			return true;
		return false;
	}
	
	//current state is terminal
	protected boolean isTerminal () {
		return terminalState;
	}
	
	//utility getter
	protected double getUtility (){
		return utility;
	}
	
	//utility setter
	protected void setUtility (double val, ArrayList<State> states){
		if (val>utility) {
			utility=val;
			utilityChanged=true;
			optimalTransitionStates = states;
		}
		else 
			utilityChanged=false;
		utilityUpdates++;
	}
	
	//reward getter
	protected double getReward (){
		return reward;
	}
	
	//true if utility has been updated at least one and was changed
	protected boolean changed (){
		return (utilityUpdates>0 && utilityChanged);
	}
	
	//optimal states getter
	protected ArrayList<State> getNextOptimalStates () {
		return optimalTransitionStates;
	}
	
	//add transition states
	protected void calculateLegalTransitionStates () {
		//initialize transition states array
		ArrayList<ArrayList<State>> transitionStates=new ArrayList<ArrayList<State>>();
		if (!terminalState) {

			//move through the edges
			Iterator<Edge> edges = graph.edges().iterator();
			while (edges.hasNext()) {
				Edge edge = edges.next();
				if (!edge.hasVertex(sourceIndex))
					continue;
				//found edge for current vertex
				int otherVertex = edge.other(sourceIndex);
				ArrayList <State> iStates = otherStates.get(otherVertex);
				ArrayList <State> transitionGroup = new ArrayList <State>();
				//initialize all transition states for current edge
				for (int i=0; i<iStates.size(); i++) {
					State current = iStates.get(i);
					//current is legal transition
					if (legalTransition(current)) {
						transitionGroup.add(current);
					}
				}
				if (transitionGroup.size()>0)
					transitionStates.add(transitionGroup);
			}
		}
		//set transition states
		legalTransitionStates=transitionStates;
	}
	

	//checks if transition is legal
	protected boolean legalTransition (State transitionState) {
	
		for (int i=0; i<foodCertinties.length; i++) {
			//food known before an unknown after
			if (foodCertinties[i]!=2 && foodCertinties[i]!=transitionState.foodCertinties[i])
				return false;
			
			//police known before an unknown after
			if (policeCertinties[i]!=2 && policeCertinties[i]!=transitionState.policeCertinties[i])
				return false;
							
			//food is unknown and after transition should remain unknown
			if (foodCertinties[i]==2 && transitionState.connectedVetexes[i]==0 && transitionState.foodCertinties[i]!=2 && i!=transitionState.sourceIndex)
				return false;
			
			//police is unknown and after transition should remain unknown
			if (policeCertinties[i]==2 && transitionState.connectedVetexes[i]==0 && transitionState.policeCertinties[i]!=2  && i!=transitionState.sourceIndex)
				return false;
			
			//agent did not actually moved
			if (transitionState.sourceIndex==this.sourceIndex)
				return false;
			
		}
	
		return true;
	}
	
	//true if state has same certinties and source index
	protected boolean equalTo (int[] foodCertinties, int[] policeCertinties, int sourceIndex) {
		for (int i=0; i<foodCertinties.length; i++) {
			if (this.foodCertinties[i]!=foodCertinties[i] || this.policeCertinties[i]!=policeCertinties[i])
				return false;
		}
		if (this.sourceIndex!= sourceIndex)
			return false;
		return true;

	}
	
	//checks if police in vertex v is unknown in current state 
	public boolean unkownkPoliceAt (int v) {
		return policeCertinties[v]==2;

	}
	
	//all legal transition states getter
	public ArrayList<ArrayList<State>> getLegalTransitionStates () {
		return legalTransitionStates;
	}
	
	//setting known vertex
	public void setKnownVertice (State state) {
		for (int i=0; i<knownVertices.length; i++) {
			if (state.policeCertinties[i]!=2)
				knownVertices[i]=1;
		}
	}
	
	//current index getter
	public int getCurrentIndex () {
		return sourceIndex;
	}
	
	//current index getter
	public boolean isGoal () {
		return this.isGoal;
	}
	
	//food certainties getter
	public int [] getFoodCertinties () {
		return foodCertinties;
	}
	
	//police certainties getter
	public int [] getPoliceCertinties () {
		return policeCertinties;
	}
	
	//optimal transitions getter
	public ArrayList<State> getOptimalTransitionStates () {
		return optimalTransitionStates;
	}
	
	//optimal transitions setter
	public void setOptimalTransitionStates(ArrayList<State> states) {
		optimalTransitionStates =states;
	}
	
	//appending info about current state
	public String toString() {
		StringBuilder ans = new StringBuilder();
		ans.append("at v"+sourceIndex+": ");
		ans.append("food:");
		for (int i=0; i<foodCertinties.length; i++) 
			ans.append(foodCertinties[i]+" ");		
		ans.append(", police:");
		for (int i=0; i<policeCertinties.length; i++) 
			ans.append(policeCertinties[i]+" ");	
		ans.append(", utility: ");
		ans.append(utility+"");
		return ans.toString();
	}
}
