
import java.util.*;

public class Node{
	private int type=0; //0=input,1=biasToOutput,2=Output
	public ArrayList<NodeWeightPair> parents=null;
	private Double inputValue=0.0;
	private Double outputValue=0.0;
	
	//Create a node with a specific type
	public Node(int type)
	{
		if(type>2 || type<0)
		{
			System.out.println("Incorrect value for node type");
			System.exit(1);
			
		}
		else
		{
			this.type=type;
		}
		
		if (type==2)
		{
			parents=new ArrayList<NodeWeightPair>();
		}
	
	}
	
	//For an input node sets the input value which will be the value of a particular attribute
	public void setInput(Double inputValue)
	{
		if(type==0)//If input node
		{
			this.inputValue=inputValue;
		}
	}
	
	/**
	 * Calculate the output of a sigmoid node.
	 * You can assume that outputs of the parent nodes have already been calculated
	 * You can get this value by using getOutput()
	 * @param train: the training set
	 */
	public void calculateOutput()
	{
		
		if(type==2)//Not an input or bias node
		{
			double sum = 0;
			for(int i = 0; i < parents.size(); i++) {
				sum += parents.get(i).weight * parents.get(i).node.getOutput();
			}
			outputValue = sigmoid(sum);		
		}
	}
	
	//Gets the output value
	public double getOutput()
	{
		
		if(type==0)//Input node
		{
			return inputValue;
		}
		else if(type==1)//Bias node
		{
			return 1.00;
		}
		else
		{
			return outputValue;
		}
		
	}
	
	//get the sigmoid value
	private static double sigmoid(double x) {
		return 1.0/(1.0 + Math.exp(-x));
	}
	
}