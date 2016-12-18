import java.util.*;
import java.io.*;

public class nn {

	public static void main(String[] args) {
		Double threshold = 0.5;
		if(args.length < 4) {
			System.out.println("neuralnet trainfile num_folds learning_rate num_epochs");
			System.exit(-1);
		}
		Dataset wholeset = createData(args[0]); 
		List<Instance> instances = wholeset.instances;
		int folds = Integer.parseInt(args[1]);
		int numepoch = Integer.parseInt(args[3]);
		double learning_rate = Double.parseDouble(args[2]);
		double initial_weight = 0.1;
		List<Integer> PositiveData = new ArrayList<Integer>();  // need to sort instances into positive and negative category for stratified cv
		List<Integer> NegativeData = new ArrayList<Integer>();
		for(int i = 0; i < instances.size(); i++) {
			if(instances.get(i).label == 1) {
				PositiveData.add(i);
			}
			else {
				NegativeData.add(i);
			}
		}
		int seed = 0;
		Collections.shuffle(PositiveData, new Random(seed));
		Collections.shuffle(NegativeData, new Random(seed));
		
		int TotalTrain_Correct = 0;
		int TotalTrain_Sum = 0;
		int TotalTest_Correct = 0;
		int TotalTest_Sum = 0;
		Integer[] fold_Index = new Integer[instances.size()];
		Integer[] actual_Label = new Integer[instances.size()];
		Integer[] predict_Label = new Integer[instances.size()];
		Double[] confidence_table = new Double[instances.size()];
		
		for(int i = 0; i < folds; i++) {
			int Positive_Start = PositiveData.size() * i / folds;
			int Positive_End = PositiveData.size() * (i + 1) / folds - 1;
			int Negative_Start = NegativeData.size() * i / folds;
			int Negative_End = NegativeData.size() * (i + 1) / folds - 1;
			List<Integer> trainIndex = new ArrayList<Integer>();
			List<Integer> testIndex = new ArrayList<Integer>();
			for(int j = 0; j < PositiveData.size(); j++) {
				if(j <= Positive_End && j >= Positive_Start) {
					testIndex.add(PositiveData.get(j));
				}
				else {
					trainIndex.add(PositiveData.get(j));
				}
			}
			for(int j = 0; j < NegativeData.size(); j++) {
				if(j <= Negative_End && j >= Negative_Start) {
					testIndex.add(NegativeData.get(j));
				}
				else {
					trainIndex.add(NegativeData.get(j));
				}
			}
			Collections.shuffle(trainIndex, new Random(seed));
			Collections.shuffle(testIndex, new Random(seed));
			
			List<Instance> training_set = new ArrayList<Instance>(); // this is training
			for(int j = 0; j < trainIndex.size(); j++) {
				training_set.add(instances.get(trainIndex.get(j)));
			}
			
			List<Instance> test_set = new ArrayList<Instance>(); // this is training
			for(int j = 0; j < testIndex.size(); j++) {
				test_set.add(instances.get(testIndex.get(j)));
			}
			
			NeuralNetwork nn = new NeuralNetwork(training_set, learning_rate, numepoch, initial_weight);
			nn.train();
			
			int Training_Correct = 0;
			int TotalTraining = training_set.size();
			Double[] Training_Output = new Double[TotalTraining];
			for(int j = 0; j < TotalTraining; j++) {
				Training_Output[j] = nn.SingleInstanceOutput(training_set.get(j));
				int AssumedOutput = 1;
				if(Training_Output[j] < threshold) {
					AssumedOutput = 0;
				}
				if(AssumedOutput == training_set.get(j).label) {
					Training_Correct++;
				}
			}
			TotalTrain_Sum += TotalTraining;
			TotalTrain_Correct += Training_Correct;
			
			int Test_Correct = 0;
			int TotalTest = test_set.size();
			Double[] Test_Output = new Double[TotalTest];
			for(int j = 0; j < TotalTest; j++) {
				Test_Output[j] = nn.SingleInstanceOutput(test_set.get(j));
				int AssumedOutput = 1;
				if(Test_Output[j] < threshold) {
					AssumedOutput = 0;
				}
				if(AssumedOutput == test_set.get(j).label) {
					Test_Correct++;
				}
				fold_Index[testIndex.get(j)] = i+1;
				actual_Label[testIndex.get(j)] = instances.get(testIndex.get(j)).label;
				predict_Label[testIndex.get(j)] = AssumedOutput;
				confidence_table[testIndex.get(j)] = Test_Output[j]; 
			}
			TotalTest_Sum += TotalTest;
			TotalTest_Correct += Test_Correct;
		}
		//System.out.format("Average accuracy for train set with epoch required %d is : %.5f\n", numepoch, 1.0 * TotalTrain_Correct / TotalTrain_Sum);
		//System.out.format("Average accuracy for test set with epoch required %d is : %.5f\n", numepoch, 1.0 * TotalTest_Correct / TotalTest_Sum);

		List<Confidence_Output> allTestSet = new ArrayList<Confidence_Output>();

		for(int i = 0; i < instances.size(); i++) {
			Confidence_Output temp = new Confidence_Output(i, actual_Label[i], confidence_table[i]);
			allTestSet.add(temp);
			System.out.format("fold_of_instance: %d" + " predicted_class: %s actual_class: %s" + " confidence_of_predication: %.5f\n", fold_Index[i], wholeset.labels.get(predict_Label[i]), wholeset.labels.get(actual_Label[i]), confidence_table[i]);
		}
		int Positive_num = PositiveData.size();
		int Negative_num = NegativeData.size();
		//System.out.println(Positive_num + " " + Negative_num);
		//System.out.println("ROC curve: FPR TPR");
        CoordinateOutput(allTestSet, Negative_num, Positive_num);
		//System.out.println("Finished");
	}
	
	public static void CoordinateOutput(List<Confidence_Output> testSetinstances, Integer Negative_num, Integer Positive_num) {
		Collections.sort(testSetinstances);
		int TP = 0; 
		int FP = 0;
		int last_TP = 0;

		for(int i = 1; i < testSetinstances.size(); i++) {
			if((testSetinstances.get(i).label != testSetinstances.get(i-1).label) && (testSetinstances.get(i).label == 0) && (TP > last_TP)) {
				double FPR = 1.0 * FP / Negative_num;
				double TPR = 1.0 * TP / Positive_num;
				//System.out.println(FPR + " " + TPR);
				last_TP = TP;
			}
			if(testSetinstances.get(i).label == 1) {
				TP++;
			}
			else {
				FP++;
			}
		}
		double FPR = 1.0 * FP / Negative_num;
		double TPR = 1.0 * TP / Positive_num;
		//System.out.println(FPR + " " + TPR);
	}
	
	private static Dataset createData(String filename) {
		Dataset set = new Dataset();
		BufferedReader in;
		boolean isData = false;
		
		try {
			in = new BufferedReader(new FileReader(filename));
			while (in.ready()) {
				String line = in.readLine();
				if(line.isEmpty())
					continue;
				char sign = line.charAt(0);
				// judge the sign char is what and then go to different branches
				if (sign == '%' ) {
					continue;
				}
				if (sign == '@') {
					if (line.length() > 10 && line.substring(1,10).toLowerCase().equals("attribute")) {
						set.addAttributes(line);
					}
					if (line.length() < 10 && line.substring(1,5).toLowerCase().equals("data")) {
						isData = true;
					}
				}
				else if(isData) {
					set.addInstances(line);
				}
			}
			in.close();
		}
		catch (Exception e) {
			e.printStackTrace(); // understand what is this going on?
			System.exit(-1); // -1 or 1, needs to check the book
		}
		return set;
	}

}
