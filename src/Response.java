public final class Response {
	private long costFlow;
	private boolean feasibleSolution;
	private long time;
	
	public long getTime() {
		return time;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
	
	public Response(long _costFlow) {
		this.costFlow = _costFlow;
		this.feasibleSolution = true;
	}
	
	public Response(boolean _feasibleSolution) {
		this.feasibleSolution = _feasibleSolution;
	}
	
	public boolean isFeasibleSolution() {
		return feasibleSolution;
	}
	
	public long getCostFlow() {
		return costFlow;
	}
	
}
