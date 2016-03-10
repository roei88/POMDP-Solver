package pomdp;

public class Node {
	
	//fields
	protected int index;	
	protected double probability;	

	//Constructors
	public Node(int index) {
		this.index = index;
		this.probability = 0;
	}
	
	public Node(int index, double probability) {
		this.index = index;
		this.probability = probability;
	}
	
	//index getter
	protected int getIndex() {
		return this.index;
	}
	
	//probability getter
	protected double getPorobability() {
		return this.probability;
	}
	//probability setter
	protected void setPorobability(double probability) {
		this.probability = probability;
	}
	
}
