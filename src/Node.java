import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class Node {
	private final int id;
	private final ArrayList<Arc> arcs;
	private final ArrayList<Arc> resArcs;
	private final HashMap<String, Object> props;
	
	// Para o segundo trabalho
	private final HashMap<Integer, Arc> hashArcs;
	private boolean deleted;
	private int degree;
	
	public Node(int id) {
		this.id = id;
		arcs = new ArrayList<Arc>(2);
		resArcs = new ArrayList<Arc>(2);
		hashArcs = new HashMap<>(16);
		props = new HashMap<>(4);
		deleted = false;
		degree = 0;
	}
	
	public Node(Node toClone) {
		this.id = toClone.id;
		this.deleted = toClone.deleted;
		this.degree = toClone.degree;
		arcs = new ArrayList<Arc>(toClone.arcs.size());
		resArcs = new ArrayList<Arc>(toClone.resArcs.size());
		hashArcs = new HashMap<>(toClone.hashArcs.size()*2);
		props = new HashMap<>(toClone.props.size()*2);
	}
	
	public int getDegree() {
		return degree;
	}
	
	public void incDegree(int inc) {
		degree += inc;
	}
	
	public void setDegree(int val) {
		degree = val;
	}
	
	public void deleteNode() {
		deleted = true;
	}
	
	public boolean hasDeleted() {
		return deleted;
	}
	
	public void setHashArc(int nodeId, Arc arc) {
		hashArcs.put(nodeId, arc);
	}
	
	public Map<Integer, Arc> getHashArcs() {
		return hashArcs;
	}
	
	public void deleteHashArc(int nodeId) {
		hashArcs.remove(nodeId);
	}
	
	public Arc getHashArc(int nodeId) {
		return hashArcs.get(nodeId);
	}
	
	// -------
	
	public int getId() {
		TimerAndReporter.testTimeout();
		return id;
	}
	
	public Map<String, Object> getProps() {
		TimerAndReporter.testTimeout();
		return props;
	}
	
	public Object get(String key) {
		TimerAndReporter.testTimeout();
		return props.get(key);
	}
	
	public void set(String key, Object value) {
		TimerAndReporter.testTimeout();
		props.put(key, value);
	}
	
	public ArrayList<Arc> getArcs() {
		TimerAndReporter.testTimeout();
		return arcs;
	}
	
	public ArrayList<Arc> getResidualArcs() {
		TimerAndReporter.testTimeout();
		return resArcs;
	}
	
	public Arc hasArc(Node head) {
		TimerAndReporter.testTimeout();
		for (Arc arc : arcs) {
			if (arc.getHead() == head)
				return arc;
		}
		return null;
	}
	
	@Override
	public String toString() {
		TimerAndReporter.testTimeout();
		String res = "Node(id=" + id;
		for (Map.Entry<String, Object> e : props.entrySet()) {
			res += ", " + e.getKey() + "=";
			if (e.getValue() instanceof Node) {
				res += "Node(" + ((Node) e.getValue()).getId() + ")";
			} else if (e.getValue() instanceof Arc) {
				res += "Arc(" + ((Arc) e.getValue()).getTail().getId() + "->" + ((Arc) e.getValue()).getHead().getId() + ")";
			} else {
				res += e.getValue();
			}
		}
		res += ")";
		return res;
	}
}
