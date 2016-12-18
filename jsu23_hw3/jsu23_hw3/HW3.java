///////////////////////////////////////////////////////////////////////////////
// Title:            CS760 HW3
// Author:           Junwei Su
// Email:            jsu23@wisc.edu
// CS Login:         junwei
///////////////////////////////////////////////////////////////////////////////

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HW3 {

	public static void main(String[] args) {
		if (args.length != 3) 
		{
			System.out.println("usage: java HW3 <trainFilename> <testFilename> <n|t>");
			System.exit(-1);
		}

		DataSet trainSet = createDataSet(args[0]);
		DataSet testSet = createDataSet(args[1]);	



		/*test
		List<String> attributes2 = trainSet.attributes;
		Map<String, List<String> > av = trainSet.attributeValues;
		for(int i = 0; i < attributes2.size(); i++) {
			List<String> aa = av.get(attributes2.get(i));
			System.out.print(attributes2.get(i));
			for(int j = 0; j < aa.size(); j++) {
				System.out.print("  " + aa.get(j));
			}
			System.out.println();
		}*/

		if(args[2].equals("n"))
		{

			//Random generator = new Random();
			//double avgAccuracy = 0.0;
		//	int sampleSize = 25;
			//for(int j = 0; j < 4; j++) {
				DataSet tempSet = trainSet;
				//createRandom(trainSet, generator, sampleSize);
				NaiveBayes classifier = new NaiveBayes();
				classifier.train(tempSet);
				for(int i = 0; i < trainSet.attributes.size(); i++) {
					System.out.println(trainSet.attributes.get(i) + " class");	
				}
				System.out.println();
				int count = 0;
				for(Instance i : testSet.instances)
				{
					ClassifyResult cr = classifier.classify(i);
					System.out.println(cr.label + " " + i.label + " " + cr.probability);
					if(cr.label.equals(i.label)) 
						count++;
				}
				System.out.println("\n" + count);
			//	avgAccuracy += (double)count/(double)testSet.instances.size();
			//}
			//System.out.println(avgAccuracy/4);

		}
		else if(args[2].equals("t"))
		{
			//Random generator = new Random();
			//double avgAccuracy = 0.0;
			//int sampleSize = 100;
		//	for(int j = 0; j < 4; j++) {
				DataSet tempSet = trainSet;//createRandom(train,generator,25);
				Tan classifier = new Tan();
				classifier.train(tempSet);
				classifier.print();
				System.out.println();
				int count = 0;
				for(Instance i : testSet.instances)
				{
					ClassifyResult cr = classifier.classify(i);
					System.out.println(cr.label + " " + i.label + " " + cr.probability);
					if(cr.label.equals(i.label)) 
						count++;
				}
				System.out.println("\n" + count);
			//	avgAccuracy += (double)count/(double)testSet.instances.size();
		//	}
			//System.out.println(avgAccuracy/4);

		}

		//classifier.train(trainSet);
		//System.out.println(classifier.calculateMutualInfo(trainSet.attributes.get(0),trainSet.attributes.get(0)));



	}


	//Reads a file and gets the list of instances
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

	/**
	 * create sub data set
	 * @param data
	 * @param generator
	 * @param size
	 * @return
	 */
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
