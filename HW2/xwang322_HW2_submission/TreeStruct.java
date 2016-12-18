import java.util.*;

public class TreeStruct {
	int attributeNumber;  // why I do not need to put "private" ahead?
	int parentNumber;
	ArrayList<TreeStruct> NextNode;
	
	public TreeStruct (int attributeNumber, int parentNumber) {
		this.attributeNumber = attributeNumber;
		this.parentNumber = parentNumber;
		NextNode = new ArrayList<TreeStruct>();
	}
	
	public void addNextNode (TreeStruct TreeNode) {
		NextNode.add(TreeNode);
	}
}
