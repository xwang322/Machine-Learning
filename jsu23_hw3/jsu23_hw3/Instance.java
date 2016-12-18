import java.util.ArrayList;
import java.util.List;

/**
 * Holds data for particular instance.
 */

public class Instance 
{	
	public String label;
	public List<String> attributes = null;
	/**
	 * Add attribute values in the order of
	 * attributes as specified by the dataset
	 */
	public void addAttribute(String s) 
	{
		if (attributes == null) 
		{
			attributes = new ArrayList<String>();
		}
		attributes.add(s);
	}
		
	public void setLabel(String thislabel) 
	{
		label = thislabel;
	}
}
