import java.util.HashMap;
import java.util.Map;


public final class ResidualArc {
	private final Node head;
	private final Node tail;

	private final HashMap<String,Object> props = new HashMap<>(4);

	public ResidualArc(Node head, Node tail) {
		super();
		this.head = head;
		this.tail = tail;
	}

	public HashMap<String, Object> getProps() {
		return props;
	}
	
	public Node getHead() {
		return head;
	}
	
	public Node getTail() {
		return tail;
	}
	
	@Override
	public String toString() {
		String res = "ResidualArc("+tail.getId()+"->"+head.getId();
		for (Map.Entry<String, Object> e : props.entrySet()){
			res += ", "+e.getKey()+"=";
			if (e.getValue() instanceof Node){
				res+="Node("+((Node)e.getValue()).getId()+")";
			}
			else if (e.getValue() instanceof ResidualArc){
				res+="ResidualArc("+((ResidualArc)e.getValue()).getTail().getId()+"->"+((ResidualArc)e.getValue()).getHead().getId()+")";
			}
			else{
				res+=e.getValue();
			}
		}
		res+=")";
		return res;
	}

}
