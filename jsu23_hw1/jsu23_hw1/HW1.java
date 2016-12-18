import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Random;


public class HW1 {

	public static void main(String[] args) {
		//check if have enough argument
		if (args.length != 3) 
		{
			System.out.println("usage: java dt-learn <train-set-file> <test-set-file> m");
			System.exit(-1);
		}

		int m = Integer.parseInt(args[2]);
		//get the data from files
		DataSet trainSet = createDataSet(args[0]);
		DataSet testSet = createDataSet(args[1]); 

		//generate the tree
		DecisionTree trainTree = new DecisionTree(trainSet,m);
		//List<Instance> attributes2 = trainSet.instances;
		//double temp = trainTree.getEntropy(attributes2 , trainSet);
		trainTree.print();
		System.out.println(" ");
		System.out.println("<Predictions for the Test Set Instances>");

		//double temp1 = trainTree.calcTestAccuracy(testSet);
		//System.out.println("++" +temp1);
		int correct = 0;
		int count = 1;
		for (Instance instance : testSet.instances) 
		{
			String predicted = trainTree.classify(instance);

			String s = "";
			s +=  count++ +  ": Actual: " + instance.label + " Predicted: " + predicted ;
			System.out.println(s);
			if(instance.label.equals(predicted))
				correct++;

		}
		System.out.println(" ");
		System.out.println("Number of correctly classified: " +correct + " Total number of test instances: " + testSet.instances.size() );

/* Uncomment this for Part2
		Random generator = new Random();
		double avgAccuracy = 0.0;
		double minAccuracy = 1.0;
		double maxAccuracy = 0.0;
		int sampleSize = (int) (1 * trainSet.instances.size());
		//create specific size of training set
		for(int i = 0; i  < 10; i++) {
			DataSet newSet = createRandom(trainSet, generator, sampleSize);
			DecisionTree tree = new DecisionTree(newSet,m);
			double temp = tree.calcTestAccuracy(testSet);
			System.out.println(tree.calcTestAccuracy(testSet));
			avgAccuracy += temp;
			if(temp > maxAccuracy) {
				maxAccuracy = temp;
			}
			if(temp < minAccuracy) {
				minAccuracy = temp;
			}		
		}
		avgAccuracy = avgAccuracy / 10;
		System.out.println("Maximum Accuracy: " + maxAccuracy + 
				" Minimum Accuracy: " + minAccuracy + " Average Accuracy: " + avgAccuracy);
*/
		//double[] toReturn = trainTree.getNumericlInfoGain(aa, 11, trainSet);
		//double major = trainTree.getEntropy(attributes2, trainSet);
		//System.out.printf("threshod %f, gain %f", toReturn[0], toReturn[1]);
		//System.out.println(trainSet.attributeValues.get(trainSet.attributes.get(1)).indexOf(attributes2.get(2).attributes.get(1)));
		//trainTree.print();
		//System.out.println(major);
		//for(int i = 0; i < attributes2.size(); i++)
		//System.out.println(trainSet.attributeValues.get("sex").size());

		//System.out.println(temp);
	}

	//read the file
	private static DataSet createDataSet(String file) {

		DataSet set = new DataSet();
		BufferedReader in;
		boolean isData = false;

		try {
			in = new BufferedReader(new FileReader(file));
			while (in.ready()) { 
				String line = in.readLine();

				char pre = line.charAt(0);

				if(pre == '%') {
					continue;
				}

				else if(pre == '@') {
					if(line.length() >= 10 && line.substring(0, 10).toLowerCase().equals("@attribute")) {
						set.addAttribute(line);	
					}
					else if(line.length() >= 5 && line.substring(0, 5).toLowerCase().equals("@data")) {
						isData  = true;
					}
				}

				else if(isData) {
					//System.out.println(line);
					set.addInstance(line);
				}
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		} 		

		return set;
	}

	private static DataSet createRandom(DataSet data, Random generator, int size) {

		if(size >= data.instances.size())
		{
			return data;
		}
		//new dataset
		DataSet newDataSet 	= new DataSet();

		List<Integer> randomNums = new ArrayList<Integer>();

		for(int i = 0; i  < size; i++) {
			int randomIndex = generator.nextInt(data.instances.size());
			if(randomNums.contains(randomIndex)) {
				i--;
			}
			else {
				randomNums.add(randomIndex);
			}
		}
		//System.out.println(randomNums.size());
		//System.out.println(randomNums.size());

		//add labels
		List<String> newLabels  = new ArrayList<String>(2);
		for (int i = 0; i < data.labels.size(); i++)
		{
			newLabels.add(data.labels.get(i));
		}
		newDataSet.labels = newLabels;

		//add attributes
		List<String> newAttributes = new ArrayList<String>();
		for (int i= 0; i<data.attributes.size();i++)
		{
			newAttributes.add(data.attributes.get(i));
		}
		newDataSet.attributes = newAttributes;


		//add attribute values
		Map<String, List<String> > newAttributeValues = new HashMap<String, List<String>>();
		newAttributeValues.putAll(data.attributeValues);
		newDataSet.attributeValues = newAttributeValues;

		//add new instances
		List<Instance> newInstances = new ArrayList<Instance>();

		for (int i=0; i < data.instances.size(); i++)
		{
			if(randomNums.contains(i))
			{
				Instance newInst   = new Instance();
				newInst.attributes = new ArrayList<String>();
				Instance oldInst   = data.instances.get(i);
				newInst.label 	   = oldInst.label;

				for (int j=0; j < oldInst.attributes.size(); j++)
				{
					newInst.attributes.add(oldInst.attributes.get(j));
				}

				newInstances.add(newInst);
			}
		}
		newDataSet.instances = newInstances;
		return newDataSet;

	}

}
