import java.util.ArrayList;

public final class Node {
	private final byte config[] = new byte[9];
	private final ArrayList<Node> links = new ArrayList<Node>(2);
	
	private boolean visited = false;
	private int pre = -1;;
	private int minPreForAP = -1;
	private Node parent = null;
	private boolean ap = false;
	private int component;
	
	public Node() {
	}
	
	public Node(byte config[]){
		System.arraycopy(config, 0, this.config, 0, 9);
	}
	
	public boolean isVisited() {
		return visited;
	}
	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	public int getPre() {
		return pre;
	}
	public void setPre(int disc) {
		this.pre = disc;
	}
	public int getMinPreForAP() {
		return minPreForAP;
	}
	public void setMinPreForAP(int minPreForAP) {
		this.minPreForAP = minPreForAP;
	}
	public Node getParent() {
		return parent;
	}
	public void setParent(Node parent) {
		this.parent = parent;
	}
	public boolean isAp() {
		return ap;
	}
	public void setAp(boolean ap) {
		this.ap = ap;
	}
	public byte[] getConfig() {
		return config;
	}
	public ArrayList<Node> getLinks() {
		return links;
	}
	
	public int getComponent() {
		return component;
	}
	public void setComponent(int component) {
		this.component = component;
	}
	public static final int getId(byte config[]){
		int res = 0;
		for (int i=0; i<9; i+=1){
			res*=10;
			res+=config[i];
		}
		return res;
	}
	public static final String toString(final byte config[]){
		StringBuilder res = new StringBuilder();
		res.append('[');
		for (int i=0; i<9; i+=1){
			res.append(config[i]==0?'-':Character.valueOf((char)('0'+config[i])));
		}
		res.append(']');
		return res.toString();
	}
	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		res.append('(');
		res.append(toString(config));
		res.append("("+getPre()+"/"+getMinPreForAP()+")");
		if (!links.isEmpty()){
			res.append("=>{");
			int c=0;
			for (Node link : links){
				if (c>0) res.append(',');
				res.append(toString(link.getConfig()));
			}
			res.append('}');
		}
		res.append(')');
		return res.toString();
	}
}
