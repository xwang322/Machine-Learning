import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.List;



public class DecisionTree {
	private DecTreeNode root;
	private List<String> labels; // ordered list of class labels
	private List<String> attributes; // ordered list of attributes
	private Map<String, List<String>> attributeValues; // map to ordered
	int stopPoint;

	DecisionTree() {
		// no code necessary
		// this is void purposefully
	}

	DecisionTree(DataSet train, int m) {
		this.stopPoint = m;
		this.attributes = train.attributes;
		this.attributeValues = train.attributeValues;
		this.labels = train.labels;	
		ArrayList<Integer> allAttributes = new ArrayList<Integer>();
		for (int i = 0; i < this.attributes.size(); i++) {
			allAttributes.add(i);
			//System.out.println(i + "" + this.attributes.get(i));
		}
		root = buildTree(train.instances, allAttributes, null, -1, train);


	}


	/**
	 * Build the decision tree
	 * @param someInstances
	 * @param attributes
	 * @param parentInstances
	 * @param parentAttributeValue
	 * @param data
	 * @return
	 */
	private DecTreeNode buildTree(List<Instance> instances, List<Integer> restAttributes, 
			List<Instance> parentInstances, int parentAttributeValue, DataSet data) {

		int a = 0, b = 0;
		if(parentInstances != null) {
			for(int i = 0; i < instances.size(); i++)
			{
				if(instances.get(i).label.equals(data.labels.get(0)))
				{
					a++;
				}
				else if(instances.get(i).label.equals(data.labels.get(1)))
				{
					b++;
				}	                
			}
		}

		if (instances.size() < stopPoint) {
			int label = getMajority(instances, data);
			DecTreeNode node = new DecTreeNode(label, null, parentAttributeValue, true);
			node.neg = a;
			node.pos = b;
			return node;
		}
		else if(sameLabel(instances)) {
			int label = this.labels.indexOf(instances.get(0).label);
			DecTreeNode node = new DecTreeNode(label, null, parentAttributeValue, true);
			node.neg = a;
			node.pos = b;
			return node;
		}
		else if (instances.isEmpty()) {
			int label = 0;
			DecTreeNode node = new DecTreeNode(label, null, parentAttributeValue, true);
			node.neg = a;
			node.pos = b;
			return node;
		}
		else if(restAttributes.isEmpty()) {
			int label = getMajority(instances, data);
			DecTreeNode node = new DecTreeNode(label, null, parentAttributeValue, true);
			node.neg = a;
			node.pos = b;
			return node;
		}
		else {
			//determine the next best attribute
			int q = bestAttribute(instances, restAttributes, data);
			if(q < 0) {
				int label = getMajority(instances, data);
				DecTreeNode node = new DecTreeNode(label, null, parentAttributeValue, true);
				node.neg = a;
				node.pos = b;
				return node;
			}
			//System.out.println(q);
			List<Integer> remainAttributes = new ArrayList<Integer>();
			//get attributes other than the best attribute
			for (int i : restAttributes) {
				if (i != q || data.attributeValues.get(data.attributes.get(i)).size() == 1) {
					remainAttributes.add(i);
					//System.out.print(", " + i);
				}
			}
			//System.out.println();

			DecTreeNode node = null;

			if(data.attributeValues.get(data.attributes.get(q)).size() == 1) {
				double[] inforPair = getNumericlInfoGain(instances, q, data);
				double threshold = inforPair[0];
				node = new DecTreeNode(getMajority(instances, data), q, parentAttributeValue, false);
				node.threshold = threshold;
				node.isNumeric = true;	
				node.neg = a;
				node.pos = b;
				List<Instance> subExamples = getNumericSubExamples(instances, q, 
						threshold, 1);
				List<Instance> subExamples2 = getNumericSubExamples(instances, q, 
						threshold, 2);
				node.addChild(buildTree(subExamples, remainAttributes, instances, 0, data));
				//System.out.println("1 " + subExamples.size());
				node.addChild(buildTree(subExamples2, remainAttributes, instances, 1, data));
				//System.out.println("2  " + subExamples2.size());
			}

			else {
				//create node with attribute q
				node = new DecTreeNode(getMajority(instances, data), q, parentAttributeValue, false);

				//System.out.println("##" + attributes.get(q));

				for(int i = 0; i < data.attributeValues.get(data.attributes.get(q)).size(); i++) {
					List<Instance> subExamples = getSubExamples(instances, q, 
							data.attributeValues.get(data.attributes.get(q)).get(i));
					//	for(int k = 0; k  < subExamples.size(); k++) 
					//	System.out.print(",  " + subExamples.get(k).label);
					node.neg = a;
					node.pos = b;
					node.addChild(buildTree(subExamples, remainAttributes, instances, i, data));
				}
			}
			return node;

		}
	}

	public void print() {
		print(0, root);
		//System.out.println(root.children.get(1).attribute);
	}
	
	public String classify(Instance instance) {

		// Find the terminal node with the tree
		List<DecTreeNode> children = root.children;
		DecTreeNode temp = root;
		while(!temp.terminal) {
			if(!temp.isNumeric)  {
				int index = this.attributeValues.get(this.attributes.
						get(temp.attribute)).indexOf(instance.attributes.get(temp.attribute));
				temp = temp.children.get(index);
			}
				else {
				double threshold = temp.threshold;
				double attValue = Double.parseDouble(instance.attributes.get(temp.attribute));
				if(attValue <= threshold) {
					temp = temp.children.get(0);
				}
				else {
					temp = temp.children.get(1);
				}
				
			}
		}
		return labels.get(temp.label);
	}
	
	/**
	 * 
	 * @param testingSet
	 * @return
	 */
	public double calcTestAccuracy(DataSet testingSet)
	{
		double testAccuracy = 0;
		 for (Instance instance : testingSet.instances){
			 String label = classify(instance);
			 if (label.equals(instance.label))
				 testAccuracy++;
		 }
		 testAccuracy = testAccuracy/testingSet.instances.size();
		 return testAccuracy;	
	}
	

	

	/**
	 * 
	 * @param k
	 * @param p
	 */
	private void print(int k, DecTreeNode p) 
	{
		List<DecTreeNode> children = p.children;
		if(children != null) {
			if(!p.isNumeric)
			{

				for(DecTreeNode child: children) 
				{
					String outString = "";
					for (int i = 0; i < k; i++) 
					{
						outString += "|\t";
					}			
					outString += this.attributes.get(p.attribute) + " = " + this.attributeValues.get(this.attributes.get(p.attribute)).get(child.parentAttributeValue) + " [" + String.valueOf(child.neg) + " " + String.valueOf(child.pos) + "]";
					if (child.terminal) 
					{
						outString += ": " + this.labels.get(child.label);					
					} 
					System.out.println(outString);				
					print(k+1, child);
				}
			}
			else
			{

				String outString = "";
				for (int i = 0; i < k; i++) 
				{
					outString += "|\t";
				}	
				outString += this.attributes.get(p.attribute) + " <= " + String.valueOf(String.format("%.6f",p.threshold)) + " [" + String.valueOf(children.get(0).neg) + " " + String.valueOf(children.get(0).pos) + "]"; 
				if (children.get(0).terminal) 
				{
					outString += ": " +this.labels.get(children.get(0).label);			
				} 
				System.out.println(outString);

				print(k+1, children.get(0));
				outString= "";
				for (int i = 0; i < k; i++) 
				{
					outString += "|\t";
				}	
				outString +=  this.attributes.get(p.attribute) + " > " + String.valueOf(String.format("%.6f",p.threshold)) + " [" + String.valueOf(children.get(1).neg) + " " + String.valueOf(children.get(1).pos) + "]"; 
				if (children.get(1).terminal) 
				{
					outString += ": " + this.labels.get(children.get(1).label);			
				} 
				System.out.println(outString);
				print(k+1, children.get(1));

			}		
		}
	}



	/**
	 * 
	 * @param instances
	 * @param attribute
	 * @param attributeValue
	 * @param count
	 * @return
	 */
	private List<Instance> getNumericSubExamples(List<Instance> instances, int attribute, double attributeValue, int count) {
		List<Instance> subExamples =  new ArrayList<Instance>();
		for(Instance i : instances) {
			if(count == 1) {
				if((Double.parseDouble(i.attributes.get(attribute))) <= (attributeValue)) {
					subExamples.add(i);
				}
			}
			else {
				if((Double.parseDouble(i.attributes.get(attribute))) > (attributeValue)) {
					subExamples.add(i);
				}
			}
		}
		return subExamples;
	}

	/**
	 * Get subset of examples with certain attribute value
	 * @param instances
	 * @param attribute
	 * @param attributeValue
	 * @return
	 */
	private List<Instance> getSubExamples(List<Instance> instances, int attribute, String attributeValue) {
		List<Instance> subExamples =  new ArrayList<Instance>();
		for(Instance i : instances) {
			if(i.attributes.get(attribute).equals(attributeValue)) {
				subExamples.add(i);
			}
		}
		return subExamples;
	}

	/**
	 * Get overall majority
	 * @param instances
	 * @param data
	 * @return
	 */
	private int getMajority(List<Instance> instances, DataSet data) {

		int count1 = 0, count2 =0;
		for(int i = 0; i < instances.size(); i++) {
			if(instances.get(i).label.equals(data.labels.get(0))) {
				count1++;
			}
			else {
				count2++;
			}
		}
		if(count1 >= count2)
			return 0;
		else
			return 1;	
	}

	/**
	 * find the best attribute
	 * @param instances
	 * @param attributes
	 * @param data
	 * @return
	 */
	private int bestAttribute(List<Instance> instances, List<Integer> attributes, DataSet data) {
		double infoGain = -1; 
		int attributeToReturn = -1;
		double entropy = getEntropy(instances, data);
		for(int i = 0; i < attributes.size(); i++) {
			double info = 0;

			if(data.attributeValues.get(data.attributes.get(attributes.get(i))).size() == 1) {	
				//System.out.println(data.attributes.get(i));
				//System.out.println(attributes.get(i));
				double[] numericalPair =  getNumericlInfoGain(instances, attributes.get(i), data);
				info = numericalPair[1];
			}
			else {
				double conditionalEntropy = getConditionalEntropy(attributes.get(i), instances, data);
				info = entropy - conditionalEntropy;
			}
			if(info > infoGain) {
				attributeToReturn = attributes.get(i);
				infoGain = info;
			}
		}
		if(infoGain < 0) {
			return -1;
		}
		//System.out.println(infoGain);	
		return attributeToReturn;
	} 

	/**
	 * Get Threshold
	 * @param theAttribute
	 * @param instances
	 * @param data
	 * @return
	 */
	private double[] getNumericlInfoGain(List<Instance> theInstances, int theAttribute, DataSet data)  {

		//System.out.println("***" +data.attributes.get(theAttribute));

		List<Double> candidate = new ArrayList<Double>();
		double infoGain = -1;
		int totalInstances = theInstances.size();
		double[] toReturn =  new double[2];
		double entropy = getEntropy(theInstances, data);

		for(int i = 0; i < theInstances.size(); i++) {
			if(!candidate.contains(Double.parseDouble(theInstances.get(i).attributes.get(theAttribute)))) {
				candidate.add(Double.parseDouble(theInstances.get(i).attributes.get(theAttribute)));
			}
		}
		Collections.sort(candidate);

		for(int j = 0; j < candidate.size()-1; j++)
		{
			double tempThresh = (candidate.get(j) + candidate.get(j+1))/2;
			boolean posLeft = false, posRight = false, negLeft = false, negRight = false;

			for(int k = 0; k < theInstances.size(); k++) {
				if(Double.parseDouble(theInstances.get(k).attributes.get(theAttribute))==candidate.get(j))
				{
					if(theInstances.get(k).label.equals(data.labels.get(0)))
					{
						negLeft = true;
					}
				}
				if(Double.parseDouble(theInstances.get(k).attributes.get(theAttribute))==candidate.get(j+1))
				{
					if(theInstances.get(k).label.equals(data.labels.get(0)))
					{
						negRight = true;
					}
				}
				if(Double.parseDouble(theInstances.get(k).attributes.get(theAttribute))==candidate.get(j))
				{
					if(theInstances.get(k).label.equals(data.labels.get(1)))
					{
						posLeft = true;
					}
				}
				if(Double.parseDouble(theInstances.get(k).attributes.get(theAttribute))==candidate.get(j+1))
				{
					if(theInstances.get(k).label.equals(data.labels.get(1)))
					{
						posRight = true;
					}
				}
				if((posRight && negLeft) || (negRight && posLeft))
				{

					break;
				}		
			} //inside for loop

			if((posRight && negLeft) || (negRight && posLeft))
			{
				int leftPos = 0, leftNeg =0, rightPos =0, rightNeg = 0;
				for(int k = 0; k < theInstances.size(); k++) {
					if(Double.parseDouble(theInstances.get(k).attributes.get(theAttribute)) <= tempThresh)
					{
						if(theInstances.get(k).label.equals(data.labels.get(0)))
							leftPos++;
						else
							leftNeg++;
					}

					else if(Double.parseDouble(theInstances.get(k).attributes.get(theAttribute)) > tempThresh)
					{
						if(theInstances.get(k).label.equals(data.labels.get(0)))
							rightPos++;
						else
							rightNeg++;
					}	
				}

				double conditionalEntropy = 0.0;
				double temp1 = 0;
				double temp2 = 0;
				if(leftPos != 0 && (leftPos + leftNeg) != 0) {
					double condProb1 = (double)leftPos/(double)(leftPos + leftNeg);
					temp1 += -condProb1 * log2(condProb1);
				}
				if(leftNeg != 0 && (leftPos + leftNeg) != 0) {
					double condProb2 = (double)leftNeg/(double)(leftPos + leftNeg);
					temp1 += - condProb2 * log2(condProb2);
				}
				if(rightPos != 0 && (rightPos + rightNeg) != 0) {
					double condProb3 = (double)rightPos/(double)(rightPos + rightNeg);
					temp2 +=  -condProb3 * log2(condProb3);
				}
				if(rightNeg != 0 && (rightPos + rightNeg) != 0) {
					double condProb4 = (double)rightNeg/(double)(rightPos + rightNeg);
					temp2 +=  - condProb4 * log2(condProb4);
				}
				//System.out.println(leftPos + " " + leftNeg + " " + rightPos + " " + rightNeg  );
				//System.out.println(condProb1 + " " + condProb2 + " " + condProb3 + " " + condProb4  );

				//System.out.println(temp1 + " " + temp2 + " ");
				conditionalEntropy = 1.0* (leftPos + leftNeg)/(totalInstances) * temp1 + 
						1.0* (rightPos + rightNeg)/(totalInstances) * temp2;

				double info = entropy - conditionalEntropy;
				//System.out.println(conditionalEntropy);
				if(info > infoGain) {
					toReturn[0]= tempThresh;
					infoGain = info;
					toReturn[1] = infoGain;
				}
			}

		}

		return toReturn;
	}


	/**
	 * Get Entropy
	 * @param instances
	 * @param data
	 * @return
	 */
	private double getEntropy(List<Instance> instances, DataSet data) {	
		int numOfLabel = data.labels.size();
		int totalInstances = instances.size();
		//	for(Instance i : instances) {
		//for(int k = 0; k <)

		//	}
		//number of entry for each label
		int[] labelCounts = new int[numOfLabel];
		for (Instance instance : instances) {
			for(int i = 0; i < numOfLabel; i++) {
				if(instance.label.equals(data.labels.get(i)))
					labelCounts[i]++;
			}
		}

		//calculate the entropy	
		double[] probs = new double[numOfLabel];
		double entropy= 0;
		for (int i = 0; i < labelCounts.length; i++) {
			if (labelCounts[i] != 0) {
				probs[i] = (double)labelCounts[i] / (double)totalInstances;
				entropy = entropy + (-probs[i]*log2(probs[i]));
			}
		}

		//System.out.println(entropy);
		return entropy;
	}

	/**
	 * Get Conditional Entropy  
	 * @param theAttribute
	 * @param instances
	 * @param data
	 * @return
	 */
	private double getConditionalEntropy(int theAttribute, List<Instance> instances, DataSet data) {

		int numAttributeValues = data.attributeValues.get(data.attributes.get(theAttribute)).size();
		int numOfLabel = data.labels.size();
		int totalInstances = instances.size();
		List<String> attributeValues = data.attributeValues.get(data.attributes.get(theAttribute));


		//count the number of each attribute with each label

		double[] attributeCount = new double[numAttributeValues];
		double[][] labelCount = new double[numAttributeValues][numOfLabel];		
		for (Instance someInstant : instances) {
			attributeCount[attributeValues.indexOf(someInstant.attributes.get(theAttribute))]++;
			for(int i = 0; i < numOfLabel; i++) {
				if(someInstant.label.equals(data.labels.get(i)))
					labelCount[attributeValues.indexOf(someInstant.attributes.get(theAttribute))][i]++;
			}

		}

		//calculate subEntropy 	
		double[] subEntropy = new double[numAttributeValues];
		for (int i = 0; i < numAttributeValues; i++ ) {
			for (int j = 0; j < numOfLabel; j++) {
				if (attributeCount[i] != 0 && labelCount[i][j] != 0) {
					double condProb = labelCount[i][j] / attributeCount[i];
					subEntropy[i] = subEntropy[i] + (-condProb*log2(condProb));
				}	
			}
			subEntropy[i] = subEntropy[i] * attributeCount[i] / totalInstances; 
		}
		//sum all subEntropy	
		double conditionalEntropy= 0;
		for (double someEntropy : subEntropy) {
			conditionalEntropy = conditionalEntropy + someEntropy;
		}

		return conditionalEntropy;

	}




	/**
	 * check if instances have the same Label
	 * @param instances
	 * @return
	 */
	private boolean sameLabel(List<Instance> instances) {

		String label = null;
		if(instances.isEmpty()) {
			return false;
		}
		for (Instance someinstance : instances) {
			if (label == null) {
				label = someinstance.label;
			} else if (!label.equals(someinstance.label)) {
				return false;
			}
		}
		return true;
	}


	/**
	 * 
	 * @param x
	 * @return log2(x)
	 */
	public static double log2(double x) {
		return Math.log(x)/Math.log(2);
	}	
}
