import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class Node {
	private final int id;
	private final ArrayList<Arc> links = new ArrayList<Arc>(2);
	private final HashMap<String,Object> props = new HashMap<>(4);
	
	public Node(int id) {
		this.id = id;
	}
	
	public int getId(){
		return id;
	}
	
	public Map<String,Object> getProps(){
		return props;
	}
	
	public ArrayList<Arc> getLinks() {
		return links;
	}
	
	@Override
	public String toString() {
		String res = "Node(id="+id;
		for (Map.Entry<String, Object> e : props.entrySet()){
			res += ", "+e.getKey()+"=";
			if (e.getValue() instanceof Node){
				res+="Node("+((Node)e.getValue()).getId()+")";
			}
			else if (e.getValue() instanceof Arc){
				res+="Arc("+((Arc)e.getValue()).getHead().getId()+"->"+((Arc)e.getValue()).getTail().getId()+")";
			}
			else{
				res+=e.getValue();
			}
		}
		res+=")";
		return res;
	}
}