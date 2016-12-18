import java.util.*;
/**
 * @author wxiaodong
 * this part defines the instance data structure
 */
public class Dataset {
	/**
	 * this defines 3 variables for a input dataset, labels, attributes and instances, not attribute value range as this is pure numeric
	 */
	public List<String> labels = null;
	public List<String> attributes = null;
	public List<Instance> instances = null;
	/**
	 * this part defines 3 functions, add label, add attributes and add instances
	 */
	public void addLabel(String labelline) {
		labels = new ArrayList<String>(2); // as this homework is for binary, so set 2 directly
		labelline = labelline.replace("{","");
		labelline = labelline.replace("}", "");
		labelline = labelline.trim();
		String[] labellinesplit = labelline.split(",");
		for(int i = 0; i <labellinesplit.length; i++) {
			labels.add(labellinesplit[i].trim());
		}		
	}
	public void addAttributes(String attrline) {
		if(attributes == null) {
			attributes = new ArrayList<String>();
		}
		int first_apostrophe = attrline.indexOf("'");
		int second_apostrophe = attrline.indexOf("'", first_apostrophe + 1);  // why sometimes needs 'new' while sometimes not?
		String attrname = new String(attrline.substring(first_apostrophe + 1, second_apostrophe)); // still need to consider 'class' case
		String attrtype = new String(attrline.substring(second_apostrophe + 1).trim());
		if(attrname.toLowerCase().equals("class")) {
			addLabel(attrtype);
			return;
		}
		attributes.add(attrname);
	}
	public void addInstances(String instance) {
		if(instances == null) {
			instances = new ArrayList<Instance>(); //Instance is data type, instance is one line example, instances is arraylist of all Instances type data			
		}
		String[] separate = instance.split(",");
		if(separate.length < attributes.size() + 1) {
			System.out.println("There are some missing values");
			return;
		}
		Instance dataline = new Instance();
		for(int i = 0; i <separate.length - 1; i++) {
			dataline.addAttributeValue(separate[i]);
		}
		if(separate[separate.length - 1].equals(this.labels.get(0))) { // assume there are only two labels
			dataline.setLabel(0);
		}
		else {
			dataline.setLabel(1);
		}
		instances.add(dataline);
	}
}
