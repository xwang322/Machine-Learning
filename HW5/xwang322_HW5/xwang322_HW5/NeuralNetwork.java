import java.util.*;
/**
 * @author wxiaodong
 * this part defines the NN structure and back propagation mechanism
 */
public class NeuralNetwork {
	/**
	 * defines public variables
	 */
	public List<Node> inputNodes = null;
	public Node outputNode = null;
	public double learning_rate = 0.1;
	public List<Instance> trainset = null;
	public int numepoch = 1; // times of running rounds, need to change based on different requirement
	
	public NeuralNetwork(List<Instance> trainset, Double learning_rate, int numepoch, Double inputWeight) {  // parameters to be determined, especially inputWeight
		this.trainset = trainset;
		this.learning_rate = learning_rate;
		this.numepoch = numepoch;
		inputNodes = new ArrayList<Node>();
		int NodeCounts = trainset.get(0).attributeValues.size();
		for(int i = 0; i < NodeCounts; i++) {
			Node node = new Node(0); // needs the constructing node type£º0 in this case as it is input node
			inputNodes.add(node);
		}
		outputNode = new Node(2);
		for(int i = 0; i < inputNodes.size(); i++) {
			Weight nodeweight = new Weight(inputNodes.get(i), inputWeight); // inputWeight is input from user and this Weight is only for output Node
		    outputNode.parents.add(nodeweight);
		}
	}
	
	public double SingleInstanceOutput(Instance instance) {
		List<Double> attributeValues = instance.attributeValues;
		for(int i = 0; i < inputNodes.size(); i++) {
			inputNodes.get(i).SetInput(attributeValues.get(i));
		}
		outputNode.OnlyOutput();
		return outputNode.GeneralOutput();
	}
	
	/**
	 * this part is using back propagation method to use training dataset to get a best weight distribution
	 */
	public void train() {
		for(int i = 0; i < this.numepoch - 1; i++) {
			for(Instance j : this.trainset) {
				double OneInstancePredict = SingleInstanceOutput(j);
				int OneInstanceActual = j.label;
				double delta = OneInstancePredict * (1 - OneInstancePredict) * (OneInstanceActual - OneInstancePredict);
				for(int m = 0; m < inputNodes.size(); m++) {
					double activationInput = outputNode.parents.get(m).parentNode.GeneralOutput();
					double deltaWeight = this.learning_rate * delta * activationInput;
					outputNode.parents.get(m).weight = outputNode.parents.get(m).weight + deltaWeight;
				}
			}
		}
	}
	
}
