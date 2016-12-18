import java.util.ArrayList;
import java.util.List;


public class DecTreeNode {
	public Integer label; 
	public Integer attribute;
	public Integer parentAttributeValue; // if is the root, set to "-1"
	public boolean isNumeric;
	boolean terminal;
	public double threshold;
	public int neg, pos;
	List<DecTreeNode> children;

	DecTreeNode(Integer _label, Integer _attribute, Integer _parentAttributeValue, boolean isterminal) 
	{
		label = _label;
		attribute = _attribute;
		parentAttributeValue = _parentAttributeValue;
		terminal = isterminal;
		if (isterminal) 
		{
			children = null;
		} 
		else 
		{
			children = new ArrayList<DecTreeNode>();
		}
	}

	/**
	 * Add child to the node.
	 * 
	 * For printing to be consistent, children should be added
	 * in order of the attribute values as specified in the
	 * dataset.
	 */
	public void addChild(DecTreeNode child) {
		if (children != null) {
			children.add(child);
		}
	}

	public void print(int k) 
	{
		if(children != null) {
			if(!isNumeric)
			{

				for(DecTreeNode child: children) 
				{
					String outString = "";
					for (int i = 0; i < k; i++) 
					{
						outString += "|\t";
					}			
					outString += attribute+ " = " + child.parentAttributeValue + " [" + String.valueOf(child.neg) + " " + String.valueOf(child.pos) + "]";
					if (child.terminal) 
					{
						outString += ": " + child.label;					
					} 
					System.out.println(outString);				
					child.print(k+1);
				}
			}
			else
			{

				String outString = "";
				for (int i = 0; i < k; i++) 
				{
					outString += "|\t";
				}	
				outString += attribute + " <= " + String.valueOf(String.format("%.6f",threshold)) + " [" + String.valueOf(children.get(0).neg) + " " + String.valueOf(children.get(0).pos) + "]"; 
				if (children.get(0).terminal) 
				{
					outString += ": " + children.get(0).label;			
				} 
				System.out.println(outString);

				children.get(0).print(k+1);
				outString= "";
				for (int i = 0; i < k; i++) 
				{
					outString += "|\t";
				}	
				outString +=  attribute + " > " + String.valueOf(String.format("%.6f",threshold)) + " [" + String.valueOf(children.get(1).neg) + " " + String.valueOf(children.get(1).pos) + "]"; 
				if (children.get(1).terminal) 
				{
					outString += ": " + children.get(1).label;			
				} 
				System.out.println(outString);
				children.get(1).print(k+1);

			}		
		}
	}

}
