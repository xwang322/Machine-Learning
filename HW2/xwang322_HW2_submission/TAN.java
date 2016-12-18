import java.util.*;

public class TAN {
	private int positivecounts;
	private int negativecounts;
	private Map<String, Map<String, Integer>> pos;
	private Map<String, Map<String, Integer>> neg;
	Data trainset;
	double[][] MST; //Maximum Spanning Tree
	int[] parentNode;
	TreeStruct root;
	CPT[] cpt;
	
	public void train(Data trainset) {
		positivecounts = 0;
		negativecounts = 0;
		this.trainset = trainset;
		pos = new HashMap<String, Map<String, Integer>>();
		neg = new HashMap<String, Map<String, Integer>>();
		MST = new double[trainset.attributes.size()][trainset.attributes.size()];
		// double MST[][] = new double[trainset.attributes.size()][trainset.attributes.size()]; this is wrong
		parentNode = new int[trainset.attributes.size()];
		parentNode[0] = -1;  // no parent for root
		root = new TreeStruct(0, -1);  // no parent for root, set as -1
		cpt = new CPT[trainset.attributes.size()];

		//allocate the attributes to map
		for (int i = 0; i < trainset.attributes.size(); i++) {
			Map<String, Integer> tempos = new HashMap<String, Integer>();
			Map<String, Integer> temneg = new HashMap<String, Integer>();
			ArrayList<String> attributesValues = trainset.attributesValues.get(trainset.attributes.get(i));
			for (String value: attributesValues) {
				tempos.put(value, 0);
			    temneg.put(value, 0);
			}
   		    pos.put(trainset.attributes.get(i), tempos);
		    neg.put(trainset.attributes.get(i), temneg);
		}
		
		ArrayList<Dataset> instances = trainset.datasetList;
		for (Dataset ins : instances) {
			if (ins.label.equals(trainset.labels.get(0))) {
				positivecounts++;
				for (int i = 0; i < ins.attributes.size(); i++){
					Map<String, Integer> tmp = new HashMap<String, Integer>();
					tmp = pos.get(trainset.attributes.get(i));
					tmp.put(ins.attributes.get(i), tmp.get(ins.attributes.get(i)) + 1);
					pos.put(trainset.attributes.get(i),tmp);
				}
			}
			if (ins.label.equals(trainset.labels.get(1))) {
				negativecounts++;
				for (int i = 0; i < ins.attributes.size(); i++){
					Map<String, Integer> tmp = new HashMap<String, Integer>();
					tmp = neg.get(trainset.attributes.get(i));
					tmp.put(ins.attributes.get(i), tmp.get(ins.attributes.get(i)) + 1);
					neg.put(trainset.attributes.get(i),tmp);
				}
			}
		}
		// Build the tree
		BuildTree();
		ArrayList<TreeStruct> Tree = new ArrayList<TreeStruct>();
		Tree.add(root);
		while (!Tree.isEmpty()) {
			TreeStruct Child = Tree.remove(0);
			for (int i = 1; i < MST.length; i++) {
				//System.out.println(MST[Child.attributeNumber][i]);
				if (MST[Child.attributeNumber][i] > 0) {
					parentNode[i] = Child.attributeNumber;
					TreeStruct AnotherChild = new TreeStruct(i, Child.attributeNumber);
					Child.addNextNode(AnotherChild);
					Tree.add(AnotherChild);
				}
			}
		}
		
		// calculate the conditional probability table, given one attribute, find its parent
		for (int i = 1; i < trainset.attributes.size(); i++) {  // 0 or 1?
			String attribute1 = trainset.attributes.get(i);
			String attribute2 = trainset.attributes.get(parentNode[i]);
			int attrsize1 = trainset.attributesValues.get(attribute1).size();
			int attrsize2 = trainset.attributesValues.get(attribute2).size();
			// System.out.println(attrsize1);
			// System.out.println(attrsize2);
			cpt[i] = new CPT(attrsize1, attrsize2);
			ArrayList<String> value1 = trainset.attributesValues.get(attribute1);
			ArrayList<String> value2 = trainset.attributesValues.get(attribute2);
			for (int j = 0; j < value1.size(); j++) {
				for (int k = 0; k < value2.size(); k++) {
					cpt[i].cptable[j][k][0] = cptcal(attribute1, attribute2, 0, k, j);
					cpt[i].cptable[j][k][1] = cptcal(attribute1, attribute2, 1, k, j);
				}
			}	
		}
	}
	
	// calculate conditional mutual information gain
	public double CMI(String attribute1, String attribute2) {
		if (attribute1 == attribute2)
			return -1;
		int[] labelcount = new int[trainset.labels.size()]; // actually this array size is 2
		int index1 = trainset.attributes.indexOf(attribute1);
		int index2 = trainset.attributes.indexOf(attribute2);
		ArrayList<String> value1 = trainset.attributesValues.get(attribute1);
		ArrayList<String> value2 = trainset.attributesValues.get(attribute2);
		int [][] YXi = new int[trainset.labels.size()][value1.size()];
		int [][] YXj = new int[trainset.labels.size()][value2.size()];
		int [][][]YXiXj = new int[trainset.labels.size()][value1.size()][value2.size()];
		
		ArrayList<Dataset> instances = trainset.datasetList;	// think more about here and compare with Jason's code	
		for (Dataset ins : instances) {
			int labelindex = trainset.labels.indexOf(ins.label);
			int Xilabel = trainset.attributesValues.get(attribute1).indexOf(ins.attributes.get(index1));	// needs twice thinking
			int Xjlabel = trainset.attributesValues.get(attribute2).indexOf(ins.attributes.get(index2));
			labelcount[labelindex]++;
			YXi[labelindex][Xilabel]++;
			YXj[labelindex][Xjlabel]++;
			YXiXj[labelindex][Xilabel][Xjlabel]++;
		}
		double mutualInforGain = 0;
		// looping from label
		for (int labelindex = 0; labelindex < trainset.labels.size(); labelindex++) {
			for (int Xiindex = 0; Xiindex < value1.size(); Xiindex++) {
				for (int Xjindex = 0; Xjindex < value2.size(); Xjindex++) {
					double P_Xi_Y = (double)(YXi[labelindex][Xiindex] + 1) / (double)(value1.size() + labelcount[labelindex]);
					double P_Xj_Y = (double)(YXj[labelindex][Xjindex] + 1) / (double)(value2.size() + labelcount[labelindex]);
					double P_XiXj_Y = (double)(YXiXj[labelindex][Xiindex][Xjindex] + 1) / (double)(labelcount[labelindex] + value1.size() * value2.size());
					double P_XiXjY = (double)(YXiXj[labelindex][Xiindex][Xjindex] + 1) / (double)(trainset.datasetList.size() + 2 * value1.size() * value2.size());  // 2 or trainset.labels.size()?
					mutualInforGain += P_XiXjY * log2 (P_XiXj_Y / (P_Xj_Y * P_Xi_Y));  //+= is not =
				}
			}
			
		}
		return mutualInforGain;
	}
	
	// calculate cpt
	public double cptcal(String attr, String attrparent, int labelindex, int parent, int child) {
		int index1 = trainset.attributes.indexOf(attr);
		int index2 = trainset.attributes.indexOf(attrparent);
		ArrayList<String> value1 = trainset.attributesValues.get(attr);
		ArrayList<String> value2 = trainset.attributesValues.get(attrparent);
		int[][] YXj = new int[trainset.labels.size()][value2.size()];
		int[][][] YXiXj = new int[trainset.labels.size()][value2.size()][value1.size()];  // thinks more
		for (Dataset ins : trainset.datasetList) {
			int labelindexlocal = trainset.labels.indexOf(ins.label);
			int attrindex = trainset.attributesValues.get(attr).indexOf(ins.attributes.get(index1));
			int parentindex = trainset.attributesValues.get(attrparent).indexOf(ins.attributes.get(index2));
			YXj[labelindexlocal][parentindex]++;
			YXiXj[labelindexlocal][parentindex][attrindex]++;
		}
		double singlecpt = 0;
		singlecpt = (double)(YXiXj[labelindex][parent][child] + 1) / (double)(YXj[labelindex][parent] + value1.size());  // why add one here?  value1.size()?
		return singlecpt;
	}
		
	// define log2 function
	public static double log2(double x) {
		return Math.log(x) / Math.log(2);
	}
	
	// build the tree based on CMI
	public void BuildTree(){
		double mutualIGTable[][] = new double[trainset.attributes.size()][trainset.attributes.size()];
		for (int i = 0; i < mutualIGTable.length; i++) {
			for (int j = 0; j < mutualIGTable[i].length; j++) {   // changed
				mutualIGTable[i][j] = CMI(trainset.attributes.get(i), trainset.attributes.get(j));
			}
		}
		int[] visited = new int[trainset.attributes.size()];
		visited[0] = 1; // set the first attribute as root
		int cnt = 1; // you have looped already one node as the starting point
		while (cnt < trainset.attributes.size()) {  // I think the loop is kind of cubersome, but we will see in the future
			double MaxWeight = -1; // set initial value as -1
			int maxi = -1; // set maximum edge first node as -1;
			int maxj = -1; // same for second node
			for (int i = 0; i < mutualIGTable.length; i++) {
				if (visited[i] != 0) {
					for (int j = 0; j < mutualIGTable[i].length; j++) {  // changed
						if (visited[j] == 0) {
							if (mutualIGTable[i][j] > MaxWeight) {
								maxi = i;
								maxj = j;
								MaxWeight = mutualIGTable[i][j];
							}
						}
					}
				}
			}
			visited[maxj] = 1;
			MST[maxi][maxj] = mutualIGTable[maxi][maxj];
			cnt++;
		}
	}
	
	// print function
	public void print() {
		System.out.println(trainset.attributes.get(0) + " class");
		for (int i = 1; i < trainset.attributes.size(); i++){
			System.out.println(trainset.attributes.get(i) + " " + trainset.attributes.get(parentNode[i]) + " class");
		}	
	}

    // calculate the P(y) probability
	public double classprobability(String label) {
		if (label.equals(trainset.labels.get(0))) {
			return (double) (positivecounts + 1) / (double) (positivecounts+negativecounts+2);
		}
		else return (double) (negativecounts + 1) / (double) (positivecounts+negativecounts+2);
	}
	
    // calculate the real counts probability
	public double attributeprobability(String attribute, String attributeValue, String label) {
		if (label.equals(trainset.labels.get(0))) {
			return (double) (pos.get(attribute).get(attributeValue) + 1) / (positivecounts + trainset.attributesValues.get(attribute).size());
		}
		else return (double) (neg.get(attribute).get(attributeValue) +  + 1) / (negativecounts + trainset.attributesValues.get(attribute).size());
	}
			
	// classify the results
	public ClassifyResult classify(Dataset oneset){ 
		ClassifyResult result = new ClassifyResult();
		double positiveprobability = classprobability(trainset.labels.get(0));
		double negativeprobability = classprobability(trainset.labels.get(1));
		for (int i = 0; i < oneset.attributes.size(); i++){
			if (i == 0) {
				positiveprobability *= attributeprobability(trainset.attributes.get(i), oneset.attributes.get(i), trainset.labels.get(0));
				negativeprobability *= attributeprobability(trainset.attributes.get(i), oneset.attributes.get(i), trainset.labels.get(1));			
			}
			else{
				String attribute1 = trainset.attributes.get(i);
				String attribute2 = trainset.attributes.get(parentNode[i]);
				int attr1index = trainset.attributesValues.get(attribute1).indexOf(oneset.attributes.get(i));
				int attr2index = trainset.attributesValues.get(attribute2).indexOf(oneset.attributes.get(parentNode[i]));
				positiveprobability *= cpt[i].cptable[attr1index][attr2index][0];
				negativeprobability *= cpt[i].cptable[attr1index][attr2index][1];
			}
		}
		if (positiveprobability > negativeprobability) {
			result.label = trainset.labels.get(0);
			result.probability = positiveprobability / (positiveprobability + negativeprobability); 
		}
		if (positiveprobability <= negativeprobability) {
			result.label = trainset.labels.get(1);
			result.probability = negativeprobability / (positiveprobability + negativeprobability); 
		}
		return result;
	}
}

