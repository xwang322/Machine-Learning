import java.util.*;
/**
 * 
 * @author wxiaodong
 * this is for one input instance, which is one line of data in arff file 
 */

public class Instance {
	/**
	 * defines the public variable part
	 */
	public List<Double> attributeValues = null;
	public int label;
	
	/**
	 * defines two methods, one is for adding variable values, the other is for adding label
	 */
	public void setLabel(Integer label) {
		this.label = label;
	}
	public void addAttributeValue(String data) {
		if(attributeValues == null){
			attributeValues = new ArrayList<Double>();
		}
		attributeValues.add(Double.parseDouble(data));
	}

}
