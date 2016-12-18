import java.util.ArrayList;
import java.util.List;

public class Node {
	int attr;
	public int parent;
	public List<Node> children;

	public Node(int attribute, int parent) {
		this.attr = attribute;
		this.parent = parent;
		children = new ArrayList<Node>();
	}
	
	public void addChild(Node node)
	{
		children.add(node);
	}
}
