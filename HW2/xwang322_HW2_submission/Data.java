import java.util.*;

public class Data {
	public ArrayList<String> attributes = new ArrayList<String>();
	public ArrayList<String> labels = new ArrayList<String>();
	public ArrayList<Dataset> datasetList = new ArrayList<Dataset>();
	public Map<String, ArrayList <String>> attributesValues = new HashMap<String, ArrayList<String>>();   //list and ArrayList differences? what is usage of map and HashMap?
	
	public void AddAttributes(String line) {
		// get the name of attributes by using index of and trim, not including @
		int one = line.indexOf("'");
		int two = line.indexOf("'", one + 1);
		String attributesNames = new String(line.substring(one + 1, two));
		// until now, attributes names have been saved in attributesNames already, now we need to get attributesValues
		line = line.substring(two + 1).trim();
		//we need to justify the attributesNames is "class"
		if (attributesNames.toLowerCase().equals("class")){
			AddLabels(line);
			return;	
		}
		attributes.add(attributesNames); // put attributesNames in attributes AL
		ArrayList<String> values = new ArrayList<String>();
		// token the {}, and save the rest class names to attributesValues
		line = line.replace("{","");
		line = line.replace("}","");
		line = line.trim();
		String[] attributeSplit = line.split(", "); // what if there is one more space after ","? how to deal with?
		// save to attributesValues
		for (int i = 0; i < attributeSplit.length; i++){
			String tmp = attributeSplit[i].trim();
			values.add(tmp);
		}
		attributesValues.put(attributesNames, values);
	}
	
	public void AddLabels(String line) {
		labels = new ArrayList<String>();  // where is this place wrong?
		line = line.replace("{", "");
		line = line.replace("}", "");
		line = line.trim();
		String[] linesplit = line.split(",");
		//System.out.println("class has" + linesplit.length + "classfications");
		for (int i = 0; i < linesplit.length; i++){
			String tmp = linesplit[i].trim();
			labels.add(tmp);  // put labels in labels AL
		}
	}
	
	public void AddDataSet(String line) {
		line = line.replace("'","");
		// do we need to handle missing data possibility?
		String[] linesplit = line.split(",");
		if (linesplit.length < attributes.size() + 1){
			System.err.println("Data has not enough attributes");
			System.out.println(line + "has some missing attributes");
			return;
		}
		Dataset dataset = new Dataset();
		for (int i = 0; i < linesplit.length - 1; i++){
			// System.out.println(linesplit[i]);
			dataset.AddAttributesData(linesplit[i]);
		}
		dataset.SetLabel(linesplit[linesplit.length - 1]);
		datasetList.add(dataset);
	}
}
