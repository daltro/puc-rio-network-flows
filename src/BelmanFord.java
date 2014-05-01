import java.util.LinkedList;

public class BelmanFord {

	public static void doBelmanFord(Network net){
		doBelmanFord(net,0);
	}

	static int LARGE = 0;//Integer.MAX_VALUE;

	public static void doBelmanFord(Network net, int start){
		
		for (Node n : net.getNodes()){
			n.getProps().put("bf.dist", LARGE);
			n.getProps().put("bf.parentNode", null);
			n.getProps().put("bf.parentArc", null);
		}
		
		net.getNodes()[start].getProps().put("bf.dist", 0);
		for (int i=0; i<net.getNodes().length; i++){
			
			for (int j=0; j<net.getNodes().length; j++){
								
				for (Arc arc : net.getNodes()[j].getArcs()){
					int distHead = (Integer)arc.getHead().getProps().get("bf.dist");
					int distTail = (Integer)arc.getTail().getProps().get("bf.dist");
					
//					if (distTail == LARGE)
//						continue;
					
					int cost = (Integer)arc.getProps().get("cost");
					distTail += cost;
					
					if (distTail<distHead){
						arc.getHead().getProps().put("bf.dist", distTail);
						arc.getHead().getProps().put("bf.parentNode", arc.getTail());
						arc.getHead().getProps().put("bf.parentArc", arc);
					}
				}
			}
			
//			System.out.println("------------------");
//			for (Node n : net.getNodes()){
//				System.out.println(n);
//			}
//			System.out.println("------------------");

		}
		
	}
	
	public static LinkedList<Arc> findNegativeCycles(Network net){
		
		doBelmanFord(net);

		for (int j=0; j<net.getNodes().length; j++){
			Node node = net.getNodes()[j];
			int distHead = (Integer)node.getProps().get("bf.dist");
			
			if (distHead<0){
				
				LinkedList<Arc> theCycle = new LinkedList<>();
				Arc arcIt = (Arc)node.getProps().get("bf.parentArc");
				do {
					theCycle.add(arcIt);
					arcIt = (Arc)arcIt.getTail().getProps().get("bf.parentArc");
				} while (!theCycle.contains(arcIt));
				
				theCycle.clear();
				do {
					theCycle.add(arcIt);
					arcIt = (Arc)arcIt.getTail().getProps().get("bf.parentArc");
				} while (!theCycle.contains(arcIt));
				
				return theCycle;
				
			}
		}
		
//		for (int j=0; j<net.getNodes().length; j++){
//			for (Arc arc : net.getNodes()[j].getArcs()){
//				
//				int distHead = (Integer)arc.getHead().getProps().get("bf.dist");
//				int distTail = (Integer)arc.getTail().getProps().get("bf.dist");
//				
//				int cost = (Integer)arc.getProps().get("cost");
//				distTail += cost;
//									
//				if (distTail<distHead){
//					
//					LinkedList<Arc> theCycle = new LinkedList<>();
//					theCycle.add(arc);
//					Arc arcIt = (Arc)arc.getTail().getProps().get("bf.parentArc");
//					while (!theCycle.contains(arcIt)){
//						theCycle.add(arcIt);
//						arcIt = (Arc)arcIt.getTail().getProps().get("bf.parentArc");
//					}
//					theCycle.clear();
//					do {
//						theCycle.add(arcIt);
//						arcIt = (Arc)arcIt.getTail().getProps().get("bf.parentArc");
//					} while (!theCycle.contains(arcIt));
//					
//					return theCycle;
//					
//				}
//			}
//		}
			
		return null;
		
	}
	
}
