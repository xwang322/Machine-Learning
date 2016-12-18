import java.util.ArrayList;
import java.util.List;

/**
 * Holds data for particular instance.
 */

public class Instance {
	public int classValue;
	public List<Double> attributes = null;

	/**
	 * Add attribute values in the order of
	 * attributes as specified by the dataset
	 */
	public void addAttribute(String i) {
		if (attributes == null) {
			attributes = new ArrayList<Double>();
		}
		attributes.add(Double.parseDouble(i));
	}
	
	public void setLabel(Integer label) {
		this.classValue = label;
	}
}
