public final class Response {
	private int costFlow;
	private boolean feasibleSolution;
	private long time;
	
	public long getTime() {
		return time;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
	
	public Response(int _costFlow) {
		this.costFlow = _costFlow;
		this.feasibleSolution = true;
	}
	
	public Response(boolean _feasibleSolution) {
		this.feasibleSolution = _feasibleSolution;
	}
	
	public boolean isFeasibleSolution() {
		return feasibleSolution;
	}
	
	public int getCostFlow() {
		return costFlow;
	}
	
}
