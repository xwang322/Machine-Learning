
public class Weight {
	public Node parentNode; // this is the parent node
	public Double weight; // this is the weight from parent to child
	
	public Weight(Node parentNode, Double weight) {
		this.parentNode = parentNode;
		this.weight = weight;
	}
}
