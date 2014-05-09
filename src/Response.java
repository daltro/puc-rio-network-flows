public final class Response {
	private long costFlow;
	private boolean feasibleSolution;
	private long timePreparing;
	private long timeRunning;
	private int loops;
	
	public int getLoops() {
		return loops;
	}
	
	public void setLoops(int loops) {
		this.loops = loops;
	}
	
	public void addLoops(int loops) {
		this.loops += loops;
	}
	
	public long getTimePreparing() {
		return timePreparing;
	}
	
	public void setTimePreparing(long time) {
		this.timePreparing = time;
	}
	
	public void addTimePreparing(long time) {
		this.timePreparing += time;
	}
	
	public long getTimeRunning() {
		return timeRunning;
	}
	
	public void setTimeRunning(long timeRunning) {
		this.timeRunning = timeRunning;
	}
	
	public void addTimeRunning(long timeRunning) {
		this.timeRunning += timeRunning;
	}
	
	public void setCostFlow(long costFlow) {
		this.costFlow = costFlow;
	}
	
	public void setFeasibleSolution(boolean feasibleSolution) {
		this.feasibleSolution = feasibleSolution;
	}
	
	public boolean isFeasibleSolution() {
		return feasibleSolution;
	}
	
	public long getCostFlow() {
		return costFlow;
	}
	
}
