import java.util.HashMap;
import java.util.Map;

public final class Arc {
	private final Node head;
	private final Node tail;
	
	private final HashMap<String, Object> props = new HashMap<>(4);
	
	public Arc(Node head, Node tail) {
		super();
		this.head = head;
		this.tail = tail;
	}
	
	public HashMap<String, Object> getProps() {
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
	
	public Node getHead() {
		TimerAndReporter.testTimeout();
		return head;
	}
	
	public Node getTail() {
		TimerAndReporter.testTimeout();
		return tail;
	}
	
	@Override
	public String toString() {
		TimerAndReporter.testTimeout();
		String res = "Arc(" + tail.getId() + "->" + head.getId();
		for (Map.Entry<String, Object> e : props.entrySet()) {
			res += ", " + e.getKey() + "=";
			if (e.getValue() instanceof Node) {
				res += "Node(" + ((Node) e.getValue()).getId() + ")";
			} else if (e.getValue() instanceof Arc) {
				res += "Arc(" + ((Arc) e.getValue()).getTail().getId() + "->"
				    + ((Arc) e.getValue()).getHead().getId() + ")";
			} else {
				res += e.getValue();
			}
		}
		res += ")";
		return res;
	}
	
}
