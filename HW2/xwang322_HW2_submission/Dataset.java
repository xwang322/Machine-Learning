import java.util.*;

public class Dataset {
	public String label;
	public ArrayList<String> attributes;

	public void AddAttributesData(String value) {
		if (attributes == null) {
			attributes = new ArrayList<String>();
		}
		attributes.add(value);
	}
	
	public void SetLabel(String l) {
		label = l;
	}
}
