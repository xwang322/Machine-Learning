import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.io.FileReader;
import java.util.List;

public class HW2 {

	public static void main(String[] args) {

		final Double threshold=0.5;

		if (args.length < 4) 
		{
			System.out.println("usage: java neuralnet <data-set-file> n l e");
			System.exit(-1);
		}

		//Reading the training set 	
		DataSet trainingSet = createDataSet(args[0]);
		//get all the instances
		List<Instance> trainInstance = trainingSet.instances;
		int numOfCross = Integer.parseInt(args[1]);
		double learningRate = Double.parseDouble(args[2]);
		int maxEpoch = Integer.parseInt(args[3]);
		double initialWeight = 0.1;
		//System.out.println(trainingSet.labels.size());
		//System.out.println(trainingSet.attributes.get(0));

		//for(int i = 0; i  < trainInstance.size(); i++) {
		//	System.out.println(trainInstance.get(i).classValue);

		//	}

		//get the index of positive label and negative label
		List<Integer> dataPos = new ArrayList<Integer>();

		List<Integer> dataNeg = new ArrayList<Integer>();

		for(int i = 0; i  < trainInstance.size(); i++) {
			if(trainInstance.get(i).classValue == 1) {
				dataPos.add(i);
				//System.out.println(i);
			}
			else {
				dataNeg.add(i);
			}

		}
		//System.out.println(dataPos.size());
		//System.out.println(dataNeg.size());

		
		int seed = 0;
		//System.nanoTime();
		Collections.shuffle(dataPos, new Random(seed));
		Collections.shuffle(dataNeg, new Random(seed));
		//System.out.println(dataNeg.get(0));

		//use to keep track of all the instance fold number
		Integer[] actualLabel = new Integer[trainInstance.size()];
		Integer[] predictLabel = new Integer[trainInstance.size()];
		Double[] confidence = new Double[trainInstance.size()];
		Integer[] foldIndex = new Integer[trainInstance.size()];
		
		 int correctTrainSum = 0;
         int correctTestSum = 0;
         int trainSum = 0;
         int testSum = 0;
         
		//select cross fold index
		for(int i = 0; i < numOfCross; i++) {
			
			//System.out.println(i + "Crossssssssssssssssssssssssssssssss");
			
			int posStart = (int) dataPos.size() * i / numOfCross;
			int posEnd = (int) dataPos.size() * (i + 1) / numOfCross - 1;
			int negStart = (int) dataNeg.size() * i / numOfCross;
			int negEnd = (int) dataNeg.size() * (i + 1) / numOfCross - 1;

			//get the train and test set indexes
			List<Integer> trainIndex = new ArrayList<Integer>();
			List<Integer> testIndex = new ArrayList<Integer>();

			for(int j = 0; j < dataPos.size(); j++) {
				if(j >= posStart && j <= posEnd) {
					testIndex.add(dataPos.get(j));	
					//System.out.println(j);
				}
				else {
					trainIndex.add(dataPos.get(j));
				}
			}

			for(int j = 0; j < dataNeg.size(); j++) {
				if(j >= negStart && j <= negEnd) {
					testIndex.add(dataNeg.get(j));
					//System.out.println(j);
				}
				else {
					trainIndex.add(dataNeg.get(j));
				}
			}
			
			Collections.shuffle(testIndex, new Random(seed));
			Collections.shuffle(trainIndex, new Random(seed));
	
			//get the training and test instances
			List<Instance> trainset = new ArrayList<Instance>();
			//List<Instance> testset = new ArrayList<Instance>();	
			
			for(int k = 0; k < trainIndex.size(); k++) {
				trainset.add(trainInstance.get(trainIndex.get(k)));
				//System.out.println(trainInstance.get(trainIndex.get(k)).classValue);
			}
			
			//for(int k = 0; k < testIndex.size(); k++) {
		//		testset.add(trainInstance.get(testIndex.get(k)));
				//System.out.println(trainInstance.get(testIndex.get(k)).classValue);
		//	}
			
			//use the training set to set up neural net
			NNImpl nn=new NNImpl(trainset,learningRate,maxEpoch, initialWeight);
			nn.train();

			//accuracy for train set			
			int correctTrain = 0;
			int trainTotal = trainset.size();
			Double[] outputForTrain = new Double[trainset.size()];
			for(int j = 0; j < trainset.size(); j++) {
				outputForTrain[j] =  nn.calculateOutputForInstance(trainset.get(j));

				int thresholdedOutput=1;

				if(outputForTrain[j]<threshold)
				{
					thresholdedOutput=0;
				}

				if(thresholdedOutput==trainset.get(j).classValue)
				{
					correctTrain++;
				}
				
				//System.out.format("%.5f %d\n",outputForTrain[j], thresholdedOutput);
			}
			
			correctTrainSum += correctTrain;
			trainSum += trainTotal;
			
			
			//accuracy for test set			
			int correctTest = 0;
			int testTotal = testIndex.size();
			Double[] outputForTest = new Double[testIndex.size()];
			for(int j = 0; j < testIndex.size(); j++) {
				//System.out.println(trainInstance.get(testIndex.get(j)).classValue);
				outputForTest[j] =  nn.calculateOutputForInstance(trainInstance.get(testIndex.get(j)));
				
				int thresholdedOutput=1;

				if(outputForTest[j]<threshold)
				{
					thresholdedOutput=0;
				}

				if(thresholdedOutput==trainset.get(j).classValue)
				{
					correctTest++;
				}
				
				foldIndex[testIndex.get(j)] = i+1;
				actualLabel[testIndex.get(j)] = trainInstance.get(testIndex.get(j)).classValue;
				predictLabel[testIndex.get(j)] = thresholdedOutput;
				confidence[testIndex.get(j)] = outputForTest[j];
				
				//System.out.format("%.5f %d\n",outputForTest[j], thresholdedOutput);
			}
			
			correctTestSum += correctTest;
			testSum += testTotal;



		}//end of 10 cross fold stratify
		//System.out.println(correctTrainSum+ " " + trainSum + " " + correctTestSum + " " + testSum);
		System.out.format("Average Accuracy for train set with epochs %d is: %.5f\n",maxEpoch, 1.0*correctTrainSum/ trainSum);
		System.out.format("Average Accuracy for test set with epochs %d is: %.5f\n", maxEpoch, 1.0*correctTestSum/ testSum);
		
		//for ROC
		List<TestSetPair> allTestSet = new ArrayList<TestSetPair>();
		
		 for (int i = 0; i < trainInstance.size(); ++i) {
			 TestSetPair temp = new TestSetPair(i, actualLabel[i], confidence[i]);
			 allTestSet.add(temp);
			 System.out.format("Fold#: %d" +	"   Predicted class: %s   Actual class: %s" + 
		 "	Confidence of the instance being positive: %.5f\n",foldIndex[i], trainingSet.labels.get((predictLabel[i])), 
		 trainingSet.labels.get((actualLabel[i])), confidence[i]);
		 }
		 
		 int num_neg = dataNeg.size();
		 int num_pos = dataPos.size();
		 System.out.println();
		 System.out.println("ROC Coordinate\nFPR	TPR");
		 outputCoordinate(allTestSet, num_neg, num_pos);
		  
	}
	//print coordinate for ROC curve
	private static void outputCoordinate(List<TestSetPair> testSetInstance, Integer num_neg, Integer num_pos) {
		Collections.sort(testSetInstance);
		int TP = 0, FP = 0;
		int last_TP = 0;
		for(int i = 1; i < testSetInstance.size(); i++) {
			if((testSetInstance.get(i).classvalue != testSetInstance.get(i - 1).classvalue) 
					&& (testSetInstance.get(i).classvalue == 0) && (TP > last_TP)) {
				double FPR = 1.0*FP/num_neg;
				double TPR = 1.0*TP/num_pos;
				System.out.println(FPR + "	" + TPR);
				last_TP = TP;
			}
			if(testSetInstance.get(i).classvalue == 1)
				TP++;
			else
				FP++;			
		}
		//System.out.println(TP + " " + FP);
		double FPR = 1.0*FP/num_neg;
		double TPR = 1.0*TP/num_pos;
		System.out.println(FPR + "	" + TPR);
	
		
		//for(int i =0; i<testSetInstance.size();i++) {
			//System.out.println(testSetInstance.get(i).instanceIndex 
				//	+ " " + testSetInstance.get(i).classvalue +" " + testSetInstance.get(i).confidence);
		//}
		
		
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

}
