import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;

public class DFSNetworkRes {

	private static final String DFS_P = "dfs.P";
	private static final char VALUE = 'V';
	private static final String DFS_V = "dfs.V";

	private static final class DFSStack {
		public final Iterator<ResidualArc> linkIterator;
		public final Node node;
		public final ResidualArc actualArc;

		public DFSStack(Node node, ResidualArc link) {
			this.node = node;
			this.linkIterator = node.getResidualArcs().iterator();
			this.actualArc = link;
		}
	}

	public Path findPath(Node nodeStart, Node nodeEnd, int cut) {
		Path resPath = new Path();
		Stack<DFSStack> stack = new Stack<>();
		ResidualArc actualArc = nodeStart.getResidualArcs().iterator().next();

		DFSStack stackStep = new DFSStack(nodeStart, actualArc);
		setVisited(nodeStart);
		stack.push(stackStep);

		while (!stack.isEmpty()) {

			DFSStack stkNode = stack.peek();
			Node node = stkNode.node;
			if (!stkNode.linkIterator.hasNext()) {
				stkNode = stack.pop();
				continue;
			}

			ResidualArc link = stkNode.linkIterator.next();
			if((Integer)link.getProps().get("cap") < cut)
				continue;
			
			Node child = link.getHead();

			if (child == nodeEnd){ //Se encontrou o nó final
				resPath.add(link);
				while (stack.size() != 1) { //Faz a busca do caminho de nodeEnd até nodeStart
					stkNode = stack.pop();
					resPath.add(stkNode.actualArc);
				}
				//System.out.println(resPath);
				return resPath;
			}
			
			if (!isVisited(child)) { // Se o no ainda nao foi visitadoo
				DFSStack stkChild = new DFSStack(child, link);
				setVisited(child);
				setParent(child,node);
				stack.push(stkChild);
			} 		
		}
		return resPath;
	}
	
	private static final boolean isVisited(Node node){
		return node.getProps().containsKey(DFS_V);
	}

	private static final void setVisited(Node node){
		node.getProps().put(DFS_V, VALUE);
	}
	
	private static final Node getParent(Node node){
		return (Node)node.getProps().get(DFS_P);
	}
	
	private static final void setParent(Node node, Node parent){
		node.getProps().put(DFS_P, parent);
	}

}
