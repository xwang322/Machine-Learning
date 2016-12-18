
public class Confidence_Output implements Comparable<Confidence_Output>{
	public int instance_index;
	public int label;
	public double confidence_score;
	
	public Confidence_Output(int instance_index, int label, double confidence_score) {
		this.instance_index = instance_index;
		this.label = label;
		this.confidence_score = confidence_score;
	}
	
	public double Return_Confidence() {
		return this.confidence_score;
	}
	
	@Override
	public int compareTo(Confidence_Output compareConfidence) {
		double compareValue = compareConfidence.Return_Confidence();
		return new Double(compareValue).compareTo(this.confidence_score);
	}

}
