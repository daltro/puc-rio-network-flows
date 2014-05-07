import java.util.LinkedList;

public class BFS {

	private static final String BFS_P = "bfs.P";
	private static final String BFS_D = "bfs.D";
	private static final char VALUE = 'V';
	private static final String BFS_V = "bfs.V";

	public static final void doBFS(Node root, NodeVisitor visitor) {

		LinkedList<Node> queue = new LinkedList<>();
		queue.addFirst(root);
		setVisited(root);

		while (!queue.isEmpty()) {
			Node node = queue.removeLast();

			setVisited(node);
			visitor.visit(node);

			for (Arc link : node.getArcs()) {
				Node linkedNode = link.getTail();
				if (!isVisited(linkedNode)) {
					queue.addFirst(linkedNode);
					int newPre = getDistance(node) + 1;
					if (getDistance(linkedNode) == -1
							|| getDistance(linkedNode) >= newPre) {
						setDistance(linkedNode,newPre);
						setParent(linkedNode, node);
					}
				}
			}
		}
	}

	private static final boolean isVisited(Node node) {
		return node.getProps().containsKey(BFS_V);
	}

	private static final void setVisited(Node node) {
		node.set(BFS_V, VALUE);
	}

	@SuppressWarnings("unused")
	private static final Node getParent(Node node){
		return (Node)node.get(BFS_P);
	}
	
	private static final void setParent(Node node, Node parent){
		node.set(BFS_P, parent);
	}

	private static final int getDistance(Node node){
		Integer dist = (Integer)node.get(BFS_D);
		return dist==null?0:dist.intValue();
	}
	
	private static final void setDistance(Node node, int dist){
		node.set(BFS_D, dist);
	}

}
