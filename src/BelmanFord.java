import java.util.ArrayList;
import java.util.LinkedList;

public class BelmanFord {

	public static void doBellmanFord(Network net){
		doBellmanFord(net,0);
	}

	static int LARGE = 0; //Integer.MAX_VALUE;

	public static void doBellmanFord(Network net, int start){
		doBellmanFord(net.getNodes(), start);
	}
	
	public static void doBellmanFord(ArrayList<Node> nodes, int start){
		
		for (Node n : nodes){	
			n.getProps().put("bf.dist", LARGE);
			n.getProps().put("bf.parentNode", null);
			n.getProps().put("bf.parentResArc", null);
			
		}
		nodes.get(start).getProps().put("bf.dist", 0);
		
		//Executa o algoritmo BelmanFord n-1 vezes
		for (int i=0; i<nodes.size()-1; i++){
			
			for (int j=0; j<nodes.size(); j++){
								
				for (ResidualArc resArc : nodes.get(j).getResidualArcs()){				
					//Despresa arestas com capacidade igual a zero
					if((Integer)resArc.getProps().get("cap") == 0)
						continue;
					
					int distHead = (Integer)resArc.getHead().getProps().get("bf.dist");
					int distTail = (Integer)resArc.getTail().getProps().get("bf.dist");
					
					int cost = (Integer)resArc.getProps().get("cost");
					//if(distTail != LARGE)
						distTail += cost;
					
					if (distTail<distHead){
						resArc.getHead().getProps().put("bf.dist", distTail);
						resArc.getHead().getProps().put("bf.parentNode", resArc.getTail());
						resArc.getHead().getProps().put("bf.parentResArc", resArc);
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
	
	public static Path findNegativeCycles(Network net){
		return findNegativeCycles(net, 0);
	}

	public static Path findNegativeCycles(Network net, int start){
		return findNegativeCycles(net.getNodes(), start);
	}
	
	public static Path findNegativeCycles(ArrayList<Node> nodes, int start){
		Path theCycle = new Path();
		boolean continuar = false;
		
		doBellmanFord(nodes, start);
		
		//Simula uma iteração do algoritmo Bellman-Ford, procurando se um nó teria sua distância alterada
		for (int j=0; j<nodes.size(); j++){
			Node node = nodes.get(j);
			
			for (ResidualArc resArc : node.getResidualArcs()){
				//Despresa arestas com capacidade igual a zero
				if((Integer)resArc.getProps().get("cap") == 0)
					continue;
				
				int distHead = (Integer)resArc.getHead().getProps().get("bf.dist");
				int distTail = (Integer)resArc.getTail().getProps().get("bf.dist");
				
				int cost = (Integer)resArc.getProps().get("cost");
				//if(distTail != LARGE)
					distTail += cost;
				
				if (distTail<distHead){					
					ResidualArc resArcIt = (ResidualArc)node.getProps().get("bf.parentResArc");
					
					do {
						theCycle.add(resArcIt);
						resArcIt = (ResidualArc)resArcIt.getTail().getProps().get("bf.parentResArc");
					} while (!theCycle.contains(resArcIt));
					
					theCycle.clear();
					
					do {
						theCycle.add(resArcIt);
						resArcIt = (ResidualArc)resArcIt.getTail().getProps().get("bf.parentResArc");
						if((Integer)resArcIt.getProps().get("cap") == 0){
							continuar = true;
							break;
						}
					} while (!theCycle.contains(resArcIt));
					
					if(continuar){
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
	
	public static void clearBellmanFord(ArrayList<Node> nodes){
		for (Node n : nodes){	
			n.getProps().remove("bf.dist");
			n.getProps().remove("bf.parentNode");
			n.getProps().remove("bf.parentResArc");
		}
	}
	
}
