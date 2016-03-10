package pomdp;

public class FoodNode extends Node {
	
	//constructor
	public FoodNode(int index, double probability) {
		super(index,probability);
	}
	
	//Resource Probability setter
	protected void setResourcePorbability(double probability) {
		setPorobability(probability);
	}
	
	//Resource Probability getter
	protected double getFoodPorbability() {
		return getPorobability();
	}
	
}
