import java.util.LinkedList;

public class BFSNetworkRes {

	private static final String BFS_P = "bfs.P";
	private static final String BFS_PL = "bfs.PLink";
	private static final char VALUE = 'V';
	private static final String BFS_V = "bfs.V";

	public final Path findPath(Node nodeStart, Node nodeEnd, int cut) {

		LinkedList<Node> queue = new LinkedList<>();
		queue.addFirst(nodeStart);
		
		while (!queue.isEmpty()) {
			Node node = queue.removeLast();

			setVisited(node);
			
			for (Arc link : node.getResidualArcs()) {
				
				if ((Integer)link.getProps().get("cap") < cut)
					continue;
				
				Node linkedNode = link.getHead();
				if (!isVisited(linkedNode)) {
					queue.addFirst(linkedNode);
					setParent(linkedNode, node);
					setParentLink(linkedNode, link);
					
					if (linkedNode == nodeEnd){
						Path res = new Path();
						Node cursor = linkedNode;
						while (getParent(cursor)!=null){
							res.add((Arc)cursor.getProps().get(BFS_PL));
							cursor = (Node)cursor.getProps().get(BFS_P);
						}
						return res;
					}
				}
			}
		}
		
		return new Path();
		
	}

	private static final boolean isVisited(Node node) {
		return node.getProps().containsKey(BFS_V);
	}

	private static final void setVisited(Node node) {
		node.getProps().put(BFS_V, VALUE);
	}

	private static final Node getParent(Node node){
		return (Node)node.getProps().get(BFS_P);
	}
	
	private static final void setParent(Node node, Node parent){
		node.getProps().put(BFS_P, parent);
	}
	
	@SuppressWarnings("unused")
	private static final Arc getParentLink(Node node){
		return (Arc)node.getProps().get(BFS_PL);
	}
	
	private static final void setParentLink(Node node, Arc parent){
		node.getProps().put(BFS_PL, parent);
	}

}
