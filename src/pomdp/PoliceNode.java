package pomdp;

public class PoliceNode extends Node {
	
	//constructor
	public PoliceNode(int index, double probability) {
		super(index,probability);
	}

	//Police Probability getter
	protected void setPolicePorbability(double probability) {
		setPorobability(probability);
	}
	
	//Police Probability setter	
	protected double getPolicePorbability() {
		return getPorobability();
	}
	
}
