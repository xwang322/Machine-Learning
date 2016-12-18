import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Tan {
	private Map<String, Map<String, Integer> > pos;
	private Map<String, Map<String, Integer> > neg;
	int posCount;
	int negCount;
	DataSet train;
	Node root;
	int[] parent;
	CPT[] conProbTable;
	double[][] maxSpanningTree;

	public void train(DataSet train)
	{
		posCount = 0; //counting the number of instances with positive label
		negCount = 0;		
		pos = new HashMap<String, Map<String,Integer>>();
		neg = new HashMap<String, Map<String,Integer>>();
		parent = new int[train.attributes.size()];
		parent[0] = -1;
		root = new Node(0,-1); //root node, no parent
		conProbTable = new CPT[train.attributes.size()]; //Conditional Probability Tables
		this.train = train;
		maxSpanningTree = new double[train.attributes.size()][train.attributes.size()];
		//put the attributes
		for(int i = 0; i < train.attributes.size(); i++) {
			//temporary  map 
			Map<String,Integer> tempPos = new HashMap<String,Integer>();
			Map<String,Integer> tempNeg = new HashMap<String,Integer>();
			List<String> attributeValues = train.attributeValues.get(train.attributes.get(i));
			//add the values for this attribute
			for(String value: attributeValues) {
				tempPos.put(value, 0);
				tempNeg.put(value, 0);
			}
			pos.put(train.attributes.get(i), tempPos);
			neg.put(train.attributes.get(i), tempNeg);
		}

		for(Instance currInstance : train.instances) {
			//label 0
			if(currInstance.label.equals(train.labels.get(0))) {
				posCount++;
				for(int i = 0; i < currInstance.attributes.size(); i++) {
					Map<String,Integer> tempMap = pos.get(train.attributes.get(i));
					tempMap.put(currInstance.attributes.get(i), tempMap.get(currInstance.attributes.get(i))+1);
					pos.put(train.attributes.get(i), tempMap);
				}
			}
			//label 1
			else {
				negCount++;
				for(int i = 0; i < currInstance.attributes.size(); i++) {
					Map<String,Integer> tempMap = neg.get(train.attributes.get(i));
					tempMap.put(currInstance.attributes.get(i), tempMap.get(currInstance.attributes.get(i))+1);
					neg.put(train.attributes.get(i), tempMap);
				}

			}
		}//end of instance for loop	

		//create edges
		buildTree();
		List<Node> queue = new ArrayList<Node>();
		queue.add(root);
		while(!queue.isEmpty()) {
			Node current = queue.remove(0);
			for(int i = 1; i < maxSpanningTree.length; i++) {
				if(maxSpanningTree[current.attr][i] > 0) {
					parent[i] = current.attr;
					Node child = new Node(i, current.attr);
					current.addChild(child);
					queue.add(child);
				}
			}
		}

		//build cpt
		for(int i = 1; i < train.attributes.size(); i++) {
			String attribute1 = train.attributes.get(i);
			String attribute2 = train.attributes.get(parent[i]);
			int size1 = train.attributeValues.get(attribute1).size();
			int size2 = train.attributeValues.get(attribute2).size();
			conProbTable[i] = new CPT(size1,size2);
			int index1 = train.attributes.indexOf(attribute1);//get index of attribute
			int index2 = train.attributes.indexOf(attribute2) ;
			List<String> value1 = train.attributeValues.get(attribute1);//get the value list 
			//System.out.println(value1.size());
			List<String> value2 = train.attributeValues.get(attribute2);
			for(int j = 0; j < value1.size(); j++) {
				for(int k = 0; k < value2.size(); k++) {
					//System.out.println("value: " + i + " parent: " + k + " child: " + j );					
					conProbTable[i].cpt[j][k][0] = calculateCPT(attribute1, attribute2, 0, k, j);
					conProbTable[i].cpt[j][k][1] = calculateCPT(attribute1, attribute2, 1, k, j);
					//System.out.println(conProbTable[i].cpt[j][k][0]);
				}
			}
			
		}
	}
	
	
	public void print()
	{
		System.out.println(train.attributes.get(0) + " class");
		for(int i = 1; i < train.attributes.size(); i++)
			System.out.println(train.attributes.get(i) + " " + train.attributes.get(parent[i]) + " class");	
	}
	
	/**
	 * Build cpt table
	 * @param attribute
	 * @param parent
	 * @param labelInd
	 * @param pInd
	 * @param childInd
	 * @return
	 */
	public double calculateCPT(String attribute, String parent, int labelInd, int pInd, int childInd) {
		int index1 = train.attributes.indexOf(attribute);//get index of attribute
		int index2 = train.attributes.indexOf(parent) ;
		List<String> value1 = train.attributeValues.get(attribute);//get the value list 
		List<String> parent1 = train.attributeValues.get(parent);
		//int[] labelCount = new int[train.labels.size()]; //count label
		int[][] YXj = new int[train.labels.size()][parent1.size()];
		int[][][] YXiXj = new int[train.labels.size()][parent1.size()][value1.size()];

		for(Instance currInstance : train.instances) {
			int labelIndex = train.labels.indexOf(currInstance.label);
			int XiIndex = train.attributeValues.get(attribute).indexOf(currInstance.attributes.get(index1));
			int parentIndex = train.attributeValues.get(parent).indexOf(currInstance.attributes.get(index2));
			//labelCount[labelIndex]++;
			//YXi[labelIndex][XiIndex]++;
			YXj[labelIndex][parentIndex]++;
			YXiXj[labelIndex][parentIndex][XiIndex]++;
		}
		double cpt = 0;
		cpt = (double)(YXiXj[labelInd][pInd][childInd] + 1) / 
				(double)(YXj[labelInd][pInd] + value1.size());
		
		return cpt ;
	}

	/**
	 * Create MST
	 */
	public void buildTree() {
		double[][] mutualTable = new double[train.attributes.size()][train.attributes.size()];
		//create mutual info table
		for(int i = 0; i < mutualTable.length; i++) {
			for(int j = 0; j < mutualTable[i].length; j++) {
				mutualTable[i][j] = calculateMutualInfo(train.attributes.get(i), train.attributes.get(j));
			}
		}
		//A list keep track of the visited list
		int[] visited = new int[train.attributes.size()];
		visited[0] = 1;
		int count = 1;
		while(count < train.attributes.size()) {
			double maxWeight = -1;
			int maxI = -1;
			int maxJ = -1;
			for(int i = 0; i < mutualTable.length; i++) {
				if(visited[i] != 0) {
					for(int j = 0; j < mutualTable[i].length; j++) {
						if(visited[j] == 0) {
							if(mutualTable[i][j] > maxWeight) {
								maxI = i;
								maxJ = j;
								maxWeight = mutualTable[i][j];
							}
						}
					}
				}
			}
			visited[maxJ] = 1;
			maxSpanningTree[maxI][maxJ] = mutualTable[maxI][maxJ];
			//System.out.println(maxI + " " + maxJ);
			count++;

		}//end of while
	}

	/**
	 * Get conditional mutual information
	 * @param attribute1
	 * @param attribute2
	 * @return
	 */
	public double calculateMutualInfo(String attribute1, String attribute2) {
		if(attribute1.equals(attribute2))
			return -1;	
		int index1 = train.attributes.indexOf(attribute1);//get index of attribute
		int index2 = train.attributes.indexOf(attribute2) ;
		List<String> value1 = train.attributeValues.get(attribute1);//get the value list 
		List<String> value2 = train.attributeValues.get(attribute2);
		int[] labelCount = new int[train.labels.size()]; //count label
		//use to get p(xi|y) p(xj|y)
		int[][] YXi = new int[train.labels.size()][value1.size()];
		int[][] YXj = new int[train.labels.size()][value2.size()];
		//use to get p(xi,xj|y)
		int[][][] YXiXj = new int[train.labels.size()][value1.size()][value2.size()];

		for(Instance currInstance : train.instances) {
			int labelIndex = train.labels.indexOf(currInstance.label);
			int XiIndex = train.attributeValues.get(attribute1).indexOf(currInstance.attributes.get(index1));
			int XjIndex = train.attributeValues.get(attribute2).indexOf(currInstance.attributes.get(index2));
			labelCount[labelIndex]++;
			YXi[labelIndex][XiIndex]++;
			YXj[labelIndex][XjIndex]++;
			YXiXj[labelIndex][XiIndex][XjIndex]++;
		}
		double condMutualInfo = 0;
		for(int labelIndex = 0; labelIndex < train.labels.size(); labelIndex++) {
			for(int XiIndex = 0; XiIndex < value1.size(); XiIndex++) {
				for(int XjIndex = 0; XjIndex < value2.size(); XjIndex++) {
					//get p(xi,xj|y)
					double p_XiXj_Y = (double)(YXiXj[labelIndex][XiIndex][XjIndex] + 1) /
							(double)(labelCount[labelIndex] + value1.size()*value2.size());
					//get p(xi|y)
					double p_Xi_Y = (double)(YXi[labelIndex][XiIndex] + 1) /
							(double)(labelCount[labelIndex] + value1.size());
					//get p(xj|y)
					double p_Xj_Y = (double)(YXj[labelIndex][XjIndex] + 1) /
							(double)(labelCount[labelIndex] + value2.size());
					//get p(xi,xj,y)
					double p_XiXjY = (double)(YXiXj[labelIndex][XiIndex][XjIndex] + 1) /
							(double)(train.instances.size() + 2*value1.size()*value2.size());					
					condMutualInfo += p_XiXjY * log2(p_XiXj_Y / (p_Xi_Y * p_Xj_Y));		

				}
			}
		}

		return condMutualInfo;
	}


	/**
	 * Returns the prior probability of the label parameter
	 */
	public double p_l(String label) {
		if(label.equals(train.labels.get(0)))
			return (double) (posCount + 1)/(double) (posCount + negCount + 2);
		else
			return (double) (negCount + 1)/(double) (negCount + posCount+ 2);	
	}

	/**
	 * Returns the smoothed conditional probability of the attribute value given the label
	 * 
	 */
	public double p_given_l(String attribute, String attrValue, String label) {

		if(label.equals(train.labels.get(0)))
			return (double)(pos.get(attribute).get(attrValue) + 1)/
					(double) (posCount + train.attributeValues.get(attribute).size());
		else
			return (double)(neg.get(attribute).get(attrValue) + 1)/
					(double) (negCount + train.attributeValues.get(attribute).size());
	}

	/**
	 * Classifies an instance. 
	 */
	public ClassifyResult classify(Instance currInstance)
	{
		ClassifyResult result = new ClassifyResult();
		double posProb = p_l(train.labels.get(0));
		double negProb = p_l(train.labels.get(1));

		for(int i = 0; i < currInstance.attributes.size(); i++) {
			if(i == 0) {
				posProb *= p_given_l(train.attributes.get(i), currInstance.attributes.get(i), train.labels.get(0));
				negProb *= p_given_l(train.attributes.get(i), currInstance.attributes.get(i), train.labels.get(1));
			}
			else {
				String attribute1 = train.attributes.get(i);
				String attribute2 = train.attributes.get(parent[i]);
				int ind1 = train.attributeValues.get(attribute1).indexOf(currInstance.attributes.get(i));
				int ind2 = train.attributeValues.get(attribute2).indexOf(currInstance.attributes.get(parent[i]));
				posProb *= conProbTable[i].cpt[ind1][ind2][0];
				negProb *= conProbTable[i].cpt[ind1][ind2][1];				
			}
		}
		if(posProb > negProb) {
			result.label = train.labels.get(0);
			result.probability = posProb/(posProb + negProb);
		}
		else {
			result.label = train.labels.get(1);
			result.probability = negProb/(posProb + negProb);
		}
		return result ;

	}
	public static double log2(double x) {
		return Math.log(x)/Math.log(2);
	}	
}
