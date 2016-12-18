import java.util.List;
import java.util.HashMap;
import java.util.Map;


public class NaiveBayes {
	//for two label
	private Map<String, Map<String, Integer> > pos;
	private Map<String, Map<String, Integer> > neg;
	int posCount;
	int negCount;
	DataSet train;

	public void train(DataSet train)
	{
		posCount = 0; //counting the number of instances with positive label
		negCount = 0;		
		pos = new HashMap<String, Map<String,Integer>>();
		neg = new HashMap<String, Map<String,Integer>>();
		this.train = train;
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

			if(currInstance.label.equals(train.labels.get(0))) {
				posCount++;
				for(int i = 0; i < currInstance.attributes.size(); i++) {
					Map<String,Integer> tempMap = pos.get(train.attributes.get(i));
					tempMap.put(currInstance.attributes.get(i), tempMap.get(currInstance.attributes.get(i))+1);
					pos.put(train.attributes.get(i), tempMap);
				}
			}
			else {
				negCount++;
				for(int i = 0; i < currInstance.attributes.size(); i++) {
					Map<String,Integer> tempMap = neg.get(train.attributes.get(i));
					tempMap.put(currInstance.attributes.get(i), tempMap.get(currInstance.attributes.get(i))+1);
					neg.put(train.attributes.get(i), tempMap);
				}

			}
		}//end of instance for loop
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
			posProb *= p_given_l(train.attributes.get(i), currInstance.attributes.get(i), train.labels.get(0));
			negProb *= p_given_l(train.attributes.get(i), currInstance.attributes.get(i), train.labels.get(1));
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
	
}
