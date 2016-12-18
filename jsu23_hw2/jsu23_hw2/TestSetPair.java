/**
 * 
 * @author Junwei
 *
 */
public class TestSetPair implements Comparable<TestSetPair>{
	public int instanceIndex;
	public int classvalue;
	public double confidence;
	
	public TestSetPair(int instanceIndex, int classvalue, double confidence) {
		this.classvalue = classvalue;
		this.instanceIndex = instanceIndex;
		this.confidence = confidence;
	}
	
	public double getConfidence() {
		return this.confidence;
	}

	@Override
	public int compareTo(TestSetPair compareConfidence) {
		double compareQuantity = ((TestSetPair) compareConfidence).getConfidence();
		return new Double(compareQuantity).compareTo(this.confidence);
	}
}
