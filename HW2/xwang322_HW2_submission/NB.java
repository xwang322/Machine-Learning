import java.util.*;

public class NB {  // NB is for Naive Bayes
	private int positivecounts;
	private int negativecounts;
	private Map<String, Map<String, Integer>> pos;
	private Map<String, Map<String, Integer>> neg;
	Data trainset;
	
	//put train set data into Data type
	public void train(Data trainset) {
		positivecounts = 0;
		negativecounts = 0;
		this.trainset = trainset;
		pos = new HashMap<String, Map<String, Integer>>();
		neg = new HashMap<String, Map<String, Integer>>();
		
		//allocate the attributes to map
		for (int i = 0; i < trainset.attributes.size(); i++) {
			Map<String, Integer> tempos = new HashMap<String, Integer>();
			Map<String, Integer> temneg = new HashMap<String, Integer>();
			ArrayList<String> attributesValues = trainset.attributesValues.get(trainset.attributes.get(i));
			for (String value: attributesValues) {
				// System.out.println(value);
				tempos.put(value, 0);
			    temneg.put(value, 0);
			    }
			pos.put(trainset.attributes.get(i), tempos);
		    neg.put(trainset.attributes.get(i), temneg);
		}
		
		ArrayList<Dataset> instances = trainset.datasetList;	// think more about here and compare with Jason's code	
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
					Map<String, Integer> tmp = neg.get(trainset.attributes.get(i));
					tmp.put(ins.attributes.get(i), tmp.get(ins.attributes.get(i)) + 1);
					neg.put(trainset.attributes.get(i),tmp);
				}
			}
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
		// System.out.println(attribute);
		// System.out.println(attributeValue);
		// System.out.println(label);
		if (label.equals(trainset.labels.get(0))) {
			// System.out.println(pos.get(attribute).get(attributeValue));
			// System.out.println(trainset.attributesValues.get(attribute).size());
			return (double) (pos.get(attribute).get(attributeValue) + 1) / (double) (positivecounts + trainset.attributesValues.get(attribute).size());
		}
		else
			return (double) (neg.get(attribute).get(attributeValue) +  + 1) / (negativecounts + trainset.attributesValues.get(attribute).size());
	}
	
	// classify the results
	public ClassifyResult classify(Dataset oneset){  // no idea why this is wrong
		ClassifyResult result = new ClassifyResult();
		double positiveprobability = classprobability(trainset.labels.get(0));
		double negativeprobability = classprobability(trainset.labels.get(1));
		for (int i = 0; i < oneset.attributes.size(); i++){
			positiveprobability *= attributeprobability(trainset.attributes.get(i), oneset.attributes.get(i), trainset.labels.get(0));
			negativeprobability *= attributeprobability(trainset.attributes.get(i), oneset.attributes.get(i), trainset.labels.get(1));
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
