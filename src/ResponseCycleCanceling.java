
public final class ResponseCycleCanceling {
	private int costFlow;
	private boolean feasibleSolution;

	public ResponseCycleCanceling(int _costFlow) {
		this.costFlow = _costFlow;
		this.feasibleSolution = true;
	}
	
	public ResponseCycleCanceling(boolean _feasibleSolution) {
		this.feasibleSolution = _feasibleSolution;
	}
	
	public boolean isFeasibleSolution(){
		return feasibleSolution;
	}
	
	public int getCostFlow(){
		return costFlow;
	}
	
}
