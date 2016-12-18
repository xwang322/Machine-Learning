/**
 * The main class that handles the entire network
 * Has multiple attributes each with its own use
 * 
 */

import java.util.*;


public class NNImpl{
	public ArrayList<Node> inputNodes=null;//list of the output layer nodes.
	public Node outputNode=null;//the output node
	public List<Instance> trainingSet=null;//the training set
	Double learningRate = 1.0; // variable to store the learning rate
	int maxEpoch=1; // variable to store the maximum number of epochs

	/**
	 * This constructor creates the nodes necessary for the neural network
	 * Also connects the nodes of different layers
	 * After calling the constructor the last node of both inputNodes and  
	 * hiddenNodes will be bias nodes. The other nodes of inputNodes are of type
	 * input. The remaining nodes are of type sigmoid. 
	 */

	public NNImpl(List<Instance> trainingSet, Double learningRate, int maxEpoch, Double outputWeights)
	{
		this.trainingSet=trainingSet;
		this.learningRate=learningRate;
		this.maxEpoch=maxEpoch;
		
		//System.out.println(learningRate);
		//System.out.println(maxEpoch);
	   //System.out.println(outputWeights);
		//input layer nodes
		
		inputNodes=new ArrayList<Node>();
		int inputNodeCount = trainingSet.get(0).attributes.size();
		//System.out.println(inputNodeCount);
		for(int i=0;i<inputNodeCount;i++)
		{
			Node node=new Node(0);
			inputNodes.add(node);
		}

		//bias node from input layer to output
		Node biasToOutput=new Node(1);
		inputNodes.add(biasToOutput);


		//Output node
		outputNode=new Node(2);

		//Connecting input nodes with output node
		for(int i=0;i<inputNodes.size();i++)
		{
			NodeWeightPair nwp=new NodeWeightPair(inputNodes.get(i),outputWeights);
			outputNode.parents.add(nwp);
		}
	}

	/**
	 * Get the output from the neural network for a single instance
	 * 
	 * The parameter is a single instance
	 */

	public Double calculateOutputForInstance(Instance inst)
	{
		List<Double> attributes = inst.attributes;
		for(int i = 0; i < inputNodes.size() - 1; i++) {
			//set the value for the input node
			inputNodes.get(i).setInput(attributes.get(i));
		}

		outputNode.calculateOutput();


		return outputNode.getOutput();
	}



	/**
	 * Train the neural networks with the given parameters
	 * 
	 * The parameters are stored as attributes of this class
	 */

	public void train()
	{
		for(int count = this.maxEpoch; count  > 0; count--) {
			for(Instance currInstance: this.trainingSet) {
				double predictValue = calculateOutputForInstance(currInstance);
				int actualValue = currInstance.classValue;
				//System.out.println(actualValue);
				//calculate (Tk - Ok)*Ok*(1-Ok)
				double deltaK = (actualValue - predictValue) * predictValue * (1 - predictValue);

				for(int j = 0; j < outputNode.parents.size(); j++) {
					double activationOfInput = outputNode.parents.get(j).node.getOutput();
					//oldHiddenOutput.add(activationOfHidden);
					double deltaWeightJK = this.learningRate * activationOfInput * deltaK;

					//update the parent weight between input and output units
					outputNode.parents.get(j).weight = outputNode.parents.get(j).weight + deltaWeightJK;			
				}//end of update weights between hidden and output units loop

			}//end of all trainging instance

		}//end of maxepoch loop

	}



}