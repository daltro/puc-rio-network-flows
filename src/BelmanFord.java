import java.util.ArrayList;

public class BelmanFord {
	
	public static void doBellmanFord(Network net) {
		doBellmanFord(net, 0);
	}
	
	static int LARGE = 0; // Integer.MAX_VALUE;
	
	public static void doBellmanFord(Network net, int start) {
		doBellmanFord(net.getNodes(), start);
	}
	
	public static void doBellmanFord(ArrayList<Node> nodes, int start) {
		
		for (Node n : nodes) {
			n.set("bf.dist", LARGE);
			n.set("bf.parentNode", null);
			n.set("bf.parentResArc", null);
		}
		nodes.get(start).set("bf.dist", 0);
		
		// Executa o algoritmo BelmanFord n-1 vezes
		for (int i = 0; i < nodes.size() - 1; i++) {
			
			for (Node j : nodes) {
				
				for (Arc resArc : j.getResidualArcs()) {
					// Despresa arestas com capacidade igual a zero
					if ((Integer) resArc.get("cap") == 0)
						continue;
					
					int distHead = (Integer) resArc.getHead().get("bf.dist");
					int distTail = (Integer) resArc.getTail().get("bf.dist");
					
					int cost = (Integer) resArc.get("cost");
					// if(distTail != LARGE)
					distTail += cost;
					
					if (distTail < distHead) {
						resArc.getHead().set("bf.dist", distTail);
						resArc.getHead().set("bf.parentNode", resArc.getTail());
						resArc.getHead().set("bf.parentResArc", resArc);
					}
				}
			}
			
		}
	}
	
	public static Path findNegativeCycles(Network net) {
		return findNegativeCycles(net, 0);
	}
	
	public static Path findNegativeCycles(Network net, int start) {
		return findNegativeCycles(net.getNodes(), start);
	}
	
	public static Path findNegativeCycles(ArrayList<Node> nodes, int start) {
		Path theCycle = new Path();
		boolean continuar = false;
		
		doBellmanFord(nodes, start);
		
		// Simula uma iteração do algoritmo Bellman-Ford, procurando se um nó teria
		// sua distância alterada
		for (int j = 0; j < nodes.size(); j++) {
			Node node = nodes.get(j);
			
			for (Arc resArc : node.getResidualArcs()) {
				// Despresa arestas com capacidade igual a zero
				if ((Integer) resArc.get("cap") == 0)
					continue;
				
				int distHead = (Integer) resArc.getHead().get("bf.dist");
				int distTail = (Integer) resArc.getTail().get("bf.dist");
				
				int cost = (Integer) resArc.get("cost");
				// if(distTail != LARGE)
				distTail += cost;
				
				if (distTail < distHead) {
					Arc resArcIt = (Arc) node.get("bf.parentResArc");
					
					do {
						theCycle.add(resArcIt);
						resArcIt = (Arc) resArcIt.getTail().get("bf.parentResArc");
					} while (!theCycle.contains(resArcIt));
					
					theCycle.clear();
					
					do {
						theCycle.add(resArcIt);
						resArcIt = (Arc) resArcIt.getTail().get("bf.parentResArc");
						if ((Integer) resArcIt.get("cap") == 0) {
							continuar = true;
							break;
						}
					} while (!theCycle.contains(resArcIt));
					
					if (continuar) {
						continuar = false;
						theCycle.clear();
						continue;
					}
					
					return theCycle;
					
				}
			}
		}
		
		clearBellmanFord(nodes);
		return theCycle;
		
	}
	
	public static void clearBellmanFord(ArrayList<Node> nodes) {
		for (Node n : nodes) {
			n.getProps().remove("bf.dist");
			n.getProps().remove("bf.parentNode");
			n.getProps().remove("bf.parentResArc");
		}
	}
	
}
