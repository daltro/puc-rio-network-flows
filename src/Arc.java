import java.util.HashMap;
import java.util.Map;


public final class Arc {
	private final Node head;
	private final Node tail;

	private final HashMap<String,Object> props = new HashMap<>(4);

	public Arc(Node head, Node tail) {
		super();
		this.head = head;
		this.tail = tail;
	}

	private static final void testInterruption() {
		if (Thread.interrupted())
			throw new RuntimeException("Thread interrompida. Tempo excedido.");
	}

	public HashMap<String, Object> getProps() {
		testInterruption();
		return props;
	}
	
	public Object get(String key){
		testInterruption();
		return props.get(key);
	}
	
	public void set(String key, Object value){
		testInterruption();
		props.put(key, value);
	}
	
	public Node getHead() {
		testInterruption();
		return head;
	}
	
	public Node getTail() {
		testInterruption();
		return tail;
	}
	
	@Override
	public String toString() {
		testInterruption();
		String res = "Arc("+tail.getId()+"->"+head.getId();
		for (Map.Entry<String, Object> e : props.entrySet()){
			res += ", "+e.getKey()+"=";
			if (e.getValue() instanceof Node){
				res+="Node("+((Node)e.getValue()).getId()+")";
			}
			else if (e.getValue() instanceof Arc){
				res+="Arc("+((Arc)e.getValue()).getTail().getId()+"->"+((Arc)e.getValue()).getHead().getId()+")";
			}
			else{
				res+=e.getValue();
			}
		}
		res+=")";
		return res;
	}

}
