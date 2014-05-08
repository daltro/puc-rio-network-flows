import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.TreeSet;

public class Dijkstra {
	
	public static final String DJK_DIST = "djk.dist";
	public static final String DJK_PARENT_NODE = "djk.parent";
	public static final String DJK_PARENT_ARC = "djk.parentArc";
	public static final String DJK_ARC_COST = "reducedCost";
	
	public static final void doDijkstra(ArrayList<Node> nodes, Node startNode,
	    final int large) {
		
		for (Node n : nodes) {
			n.set(DJK_DIST, large);
			n.getProps().remove(DJK_PARENT_NODE);
		}

/*		
		TreeSet<Node> queue = new TreeSet<>(new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				Integer i1 = (Integer) o1.get(DJK_DIST);
				Integer i2 = (Integer) o2.get(DJK_DIST);
				return i1 - i2;
			}
		});
*/
		
		PriorityQueue<Node> queue = new PriorityQueue<>(nodes.size(), new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				Integer i1 = (Integer) o1.get(DJK_DIST);
				Integer i2 = (Integer) o2.get(DJK_DIST);
				return i1 - i2;
			}
		});
					
		startNode.set(DJK_DIST, 0);
		queue.addAll(nodes);

		while (!queue.isEmpty()) {
			
			Node u = queue.remove();
			
			if ((Integer) u.get(DJK_DIST) == large) {
				break;
			}
			
			for (Arc arc : u.getResidualArcs()) {
				if((Integer)arc.get("cap") == 0)
					continue;
				
				if ((Integer) arc.get(DJK_ARC_COST) < 0) {
					throw new IllegalStateException(
					    "Teste de sanidade falhou. Custo para dijkstra é negativo.");
				}
				
				long alt = (Integer) u.get(DJK_DIST);
				alt += (Integer) arc.get(DJK_ARC_COST);
				Node v = arc.getHead();
				int distV = (Integer) v.get(DJK_DIST);
				if (alt < distV) {
					v.set(DJK_DIST, (int) alt);
					v.set(DJK_PARENT_NODE, u);
					v.set(DJK_PARENT_ARC, arc);
					queue.remove(v);
					queue.add(v);
				}
			}
			
		}
		
	}
	
}
