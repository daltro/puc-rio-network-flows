import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;


public class Dijkstra {
	
	public static final String DJK_DIST="djk.dist";
	public static final String DJK_PARENT="djk.parent";
	public static final String DJK_ARC_COST = "cost";
	
	public static final void doDijkstra(ArrayList<Node> nodes, Node startNode){
		
		for (Node n : nodes){
			n.getProps().put(DJK_DIST, Integer.MAX_VALUE);
			n.getProps().remove(DJK_PARENT);
		}
		
		PriorityQueue<Node> queue = new PriorityQueue<>(nodes.size(), new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				Integer i1 = (Integer)o1.getProps().get(DJK_DIST);
				Integer i2 = (Integer)o2.getProps().get(DJK_DIST);
				return i1-i2;
			}
		});
		
		startNode.getProps().put(DJK_DIST, 0);
		queue.addAll(nodes);
		
		while (!queue.isEmpty()){
			
			Node u = queue.remove();
			
			if ((Integer)u.getProps().get(DJK_DIST) == Integer.MAX_VALUE){
				break;
			}
			
			for (ResidualArc arc : u.getResidualArcs()){
				
				if ((Integer)arc.getProps().get(DJK_ARC_COST)<0){
					continue;
				}
				
				long alt = (Integer)u.getProps().get(DJK_DIST);
				alt += (Integer)arc.getProps().get(DJK_ARC_COST);
				Node v = arc.getHead();
				int distV = (Integer)v.getProps().get(DJK_DIST);
				if (alt < distV){
					v.getProps().put(DJK_DIST, (int)alt);
					v.getProps().put(DJK_PARENT, u);
					queue.remove(v); // TODO: Será que vai ficar lento?
					queue.add(v);
				}
			}

		}
		
	}
	
}
