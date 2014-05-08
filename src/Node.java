import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class Node {
	private final int id;
	private final ArrayList<Arc> arcs = new ArrayList<Arc>(2);
	private final ArrayList<Arc> resArcs = new ArrayList<Arc>(2);
	private final HashMap<String,Object> props = new HashMap<>(4);
	
	public Node(int id) {
		this.id = id;
	}
	
	public int getId(){
		testInterruption();
		return id;
	}
	
	public Map<String,Object> getProps(){
		testInterruption();
		return props;
	}

	private static final void testInterruption() {
		if (Thread.interrupted())
			throw new RuntimeException("Thread interrompida. Tempo excedido.");
	}
	
	public Object get(String key){
		testInterruption();
		return props.get(key);
	}
	
	public void set(String key, Object value){
		testInterruption();
		props.put(key, value);
	}
	
	public ArrayList<Arc> getArcs() {
		testInterruption();
		return arcs;
	}
	
	public ArrayList<Arc> getResidualArcs() {
		testInterruption();
		return resArcs;
	}
	
	public Arc hasArc(Node head){
		testInterruption();
		for(Arc arc : arcs){
			if(arc.getHead() == head)
				return arc;
		}
		return null;
	}
	
	@Override
	public String toString() {
		testInterruption();
		String res = "Node(id="+id;
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
