import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DataSet {
	public List<String> labels = null;  
	public List<String> attributes = null;  
	public List<Instance> instances = null; 
	public Map<String, List<String> > attributeValues = null;

	//add label
	public void addLabels(String line) 
	{
		labels = new ArrayList<String>(2);

		line = line.replace("{","").trim();
		line = line.replace("}","").trim();

		//System.out.println(line);

		String[] splitline = line.split(", ");

		//add label
		for (int i = 0; i < splitline.length; i++) {
			labels.add(splitline[i]);
		}
	}

	public void addAttribute(String line) 
	{
		if(attributes == null) {
			attributes = new ArrayList<String>();
			attributeValues = new HashMap<String, List<String>>();
		}

		//get name
		int first = line.indexOf("'");
		int second = line.indexOf("'", first + 1);
		String attributeName =new String(line.substring(first + 1, second)); // not including '

		line = line.substring(second + 1).trim();

		if(attributeName.toLowerCase().equals("class"))
		{	
			line = line.replace("'","");
			//System.out.println(line);
			addLabels(line);
			return;
		}

		List<String> list = new ArrayList<String>();

		attributes.add(attributeName);


		line = line.replace("{","").trim();
		line = line.replace("}","").trim();
		line = line.replace("'","").trim();
		//System.out.println(line);
		String[] splitline = line.split(", ");

		//add attrubute value
		for (int i = 0; i < splitline.length; i++) 
		{
			String temp = splitline[i].trim();
			list.add(temp);
		}

		attributeValues.put(attributeName, list);

	}

	//add instance
	public void addInstance(String line) {

		line = line.replace("'","");
		//System.out.println(line);

		if (instances == null) 
		{
			instances = new ArrayList<Instance>();
		}		
		String[] splitline = line.split(",");

		if (splitline.length < 1 + attributes.size()) 
		{ 
			System.err.println("Instance doesn't contain enough attributes");
			System.out.println(line);
			return;
		}

		Instance instance = new Instance();
		for(int i = 0; i < splitline.length - 1; i ++)
			instance.addAttribute(splitline[i]);

		instance.setLabel(splitline[splitline.length - 1]);
		instances.add(instance);		
	}	
}
