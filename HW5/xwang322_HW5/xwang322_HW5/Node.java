import java.util.*;

public class Node {
	/**
	 * this defines 4 variables, nodelevel, its parents' weights and input output value
	 */
	private int nodelevel = 0; // input:0 output:2 bias:1 do we need to set bias separately?
	public ArrayList<Weight> parents = null; // record all parents nodes to this node, their weights, if this node is input, set them to 0, output then create a list
	private Double inputValue = (double) 0;
	private Double outputValue = (double) 0;
	
	/**
	 * this defines the functions of construction a node, then set up input node level and calculate output
	 */
	public Node(int type) {
		if(type<0 || type>2) {
			System.out.println("Input Node type is wrong!");
			System.exit(1);
		}
		else {
			this.nodelevel = type;
		}
		if(type == 2) {
			parents = new ArrayList<Weight>();
		}
	}
	
	public void SetInput(Double inputValue) {
		if(nodelevel == 0) {
			this.inputValue = inputValue;
		}
	}
	
	public double Sigmoid(double m) {
		return 1.0/(1.0 + Math.exp(-m));
	}

	public void OnlyOutput() {
		if(nodelevel == 2) {
			double sum = 0;
			for(int i = 0; i < parents.size(); i++) {
				sum += parents.get(i).weight * parents.get(i).parentNode.GeneralOutput();
			}
			outputValue = Sigmoid(sum);
		}
	}
	
	public double GeneralOutput() {
		if(nodelevel == 0) {
			return inputValue;
		}
		else if(nodelevel == 1) {
			return 1.0;
		}
		else {
			return outputValue;
		}
	}
}
