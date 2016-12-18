//////////////////////////////////////////////////////////////
// Title: CS 760 HW2
// Author: Xiaodong Wang
// Email: xwang322@wisc.edu
// CS login: xiaodong
//////////////////////////////////////////////////////////////
import java.io.*;
import java.math.BigDecimal;
import java.util.*;


public class bayes {
	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("bayes <train-set-file> <test-set-file> <n|t>");
			System.exit(1); // 1 or -1?			
		}
		Data trainset = createData(args[0]);  
		Data testset = createData(args[1]);
		
		// if this is Naive Bayes
		if (args[2].equals("n")) {
			Data tmpset = trainset;
			//Random generator = new Random();
			//int samplesize = 100;
			//Data tmpset = CreateRandom(trainset, generator, samplesize);
			NB NBclassify = new NB();
			NBclassify.train(tmpset);
			for (int i = 0; i < tmpset.attributes.size(); i++){
				System.out.println(tmpset.attributes.get(i) + " class");
			}
			System.out.println();
			int number = 0;
			for (Dataset i : testset.datasetList) {
				ClassifyResult check = NBclassify.classify(i);
				double result = check.probability;
				BigDecimal bg = new BigDecimal(result).setScale(12, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
				System.out.println(check.label + " " + i.label + " " + bg);
				//System.out.println(check.label + " " + i.label + " " + check.probability);
				if (check.label.equals(i.label))
					number++;
			}
			System.out.println();
			System.out.println(number);
		}
		
		// if this is TAN
		if (args[2].equals("t")) {
			// Data tmpset = trainset;
			Random generator = new Random();
			int samplesize = 100;
			Data tmpset = CreateRandom(trainset, generator, samplesize);
			TAN TANclassify = new TAN();
			TANclassify.train(tmpset);
			TANclassify.print();
			System.out.println();
			int number = 0;
			for (Dataset i : testset.datasetList) {
				ClassifyResult check = TANclassify.classify(i);
				double result = check.probability;
				BigDecimal bg = new BigDecimal(result).setScale(12, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
				System.out.println(check.label + " " + i.label + " " + bg);
					// System.out.println(check.label + " " + i.label + " " + check.probability);
				if (check.label.equals(i.label))
					number++;
			}
			System.out.println();
			System.out.println(number);
		}
	}
	
	private static Data createData(String filename) {
		Data set = new Data();
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
						set.AddAttributes(line);
					}
					if (line.length() < 10 && line.substring(1,5).toLowerCase().equals("data")) {
						isData = true;
					}
				}
				else if(isData) {
					set.AddDataSet(line);
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
	
	// create random dataset for hw requirement
	private static Data CreateRandom(Data data, Random generator, int size) {
		if (size > data.datasetList.size()) {
			return data;
		}
		
		Data newset = new Data();
		ArrayList<Integer> RandomNumbers = new ArrayList<Integer>();
		for (int i = 0; i < size; i++) {
			int randomnumber = generator.nextInt(data.datasetList.size());
			if (RandomNumbers.contains(randomnumber)) {
				i--;
			}
			else {
				RandomNumbers.add(randomnumber);
			}
		}
		ArrayList<String> newLabels = new ArrayList<String>();
		for (int i = 0; i < data.labels.size(); i++) {
			newLabels.add(data.labels.get(i));
		}
		newset.labels = newLabels;
		ArrayList<String> newAttributes = new ArrayList<String>();
		for (int i = 0; i <data.attributes.size(); i++) {
			newAttributes.add(data.attributes.get(i));
		}
		newset.attributes = newAttributes;
		Map<String, ArrayList<String>> newAttributesValues = new HashMap<String, ArrayList<String>>();
		newAttributesValues.putAll(data.attributesValues); // this lines needs more understanding
		newset.attributesValues = newAttributesValues;
		// set up is done, right now we add instances
		ArrayList<Dataset> newDataset = new ArrayList<Dataset>();
		for (int i = 0; i < data.datasetList.size(); i++) {
			if (RandomNumbers.contains(i)) {
				Dataset newsingledata = new Dataset();
				newsingledata.attributes = new ArrayList<String>();
				Dataset oldsingledata = data.datasetList.get(i);
				newsingledata.label = oldsingledata.label;
				for (int j = 0; j < oldsingledata.attributes.size(); j++) {
					newsingledata.attributes.add(oldsingledata.attributes.get(j));
				}
				newDataset.add(newsingledata);
			}
		}
		newset.datasetList = newDataset;
		return newset;
	}
}
