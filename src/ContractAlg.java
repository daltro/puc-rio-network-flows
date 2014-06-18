import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

public class ContractAlg {
	
	private static final Random gerador = new Random();
	private static final double sqrt2 = Math.sqrt(2d);
	
	public static long algoIteractionTime = 0;
	
	public int doContract(Network net) {
		// Chama o contract para o novo grafo
		int minCut = Integer.MAX_VALUE;
		int maxTimes = net.getNodes().size() * net.getNodes().size() * (int) Math.log(net.getNodes().size());
		
		for (int i = 0; i < maxTimes / 100; i++) {
			long timer = System.nanoTime();
			long timer2 = System.currentTimeMillis();
			long cloneTimer = Network.clonningTime;
			
			// Copia o grafo
			Network newNet = net.clone();
			
			int cut = contract(newNet, 2);
			
			long itTime = (System.nanoTime() - timer);
			long itClon = (Network.clonningTime - cloneTimer);
			algoIteractionTime += itTime;
			
			System.out.println("Contract.cut="+cut+", tempo total: " + itTime+"ns - "+(System.currentTimeMillis()-timer2)+"ms" + "\t cloning: "+(((double) itClon / itTime) * 100d) + "%");
			
			if (cut < minCut) {
				minCut = cut;
			}
			
			if (minCut <= 1) {
				// Corte mínimo <= 1 já indica mínimo global.
				break;
			}
		}
		
		return minCut;
	}
	
	public int doFastCut(Network net) {
		// Chama o contract para o novo grafo
		int minCut = Integer.MAX_VALUE;
		int maxTimes = net.getNodes().size() * net.getNodes().size();
		
		for (int i = 0; i < maxTimes / 100; i++) {
			// Copia o grafo
			Network newNet = net.clone();
			long timer = System.nanoTime();
			long timer2 = System.currentTimeMillis();
			long cloneTimer = Network.clonningTime;
			int cut = fastCut(newNet);
			long itTime = (System.nanoTime() - timer);
			long itClon = (Network.clonningTime - cloneTimer);
			algoIteractionTime += itTime;
			// System.out.println("Corte mínimo: " + minCut + " - Corte: " + cut);
			
			System.out.println("FastCut.cut="+cut+", tempo total: " + itTime+"ns - "+(System.currentTimeMillis()-timer2)+"ms" + "\t cloning: "+(((double) itClon / itTime) * 100d) + "%");
			
			if (cut < minCut) {
				minCut = cut;
			}
			
			if (minCut <= 1) {
				// Corte mínimo <= 1 já indica mínimo global.
				break;
			}
		}
		
		return minCut;
	}
	
	private int fastCut(Network net) {
		
		// System.out.println("FastCut com qtdNodes=" + net.qtdNodes +
		// "\t e qtdArcs=" + net.qtdArcs);
		
		// Caso base
		if (net.qtdNodes <= 6) {
			// Força bruta
			// 1) Separar os nós efetivos
			ArrayList<Node> remainingNodes = new ArrayList<>(6);
			for (Node n : net.getNodes())
				if (n!=null && !n.hasDeleted())
					remainingNodes.add(n);
			
			// 2) Enumerar todas as partições possíveis
			int minCut = Integer.MAX_VALUE;
			HashSet<Node> setA = new HashSet<>(remainingNodes.size());
			HashSet<Integer> setCut = new HashSet<>();
			int combinations = (int) Math.pow(2, remainingNodes.size()) - 1;
			for (int i = 1; i < combinations; i += 1) {
				// O n-ésimo bit de i indica se o n-ésimo nó irá para A ou B na partição
				setA.clear();
				setCut.clear();
				// Distribui conjunto "A" e "B"
				// System.out.print("\nPerm: ");
				for (int n = 0; n < remainingNodes.size(); n += 1) {
					if ((i >> n & 1) == 0) {
						setA.add(remainingNodes.get(n));
						// System.out.print(n + "/");
					}
				}
				// System.out.println();
				// if (setA.isEmpty()) {
				// System.out.println("PERIGO!");
				// }
				
				int thisCut = 0;
				for (Node n : setA) {
					for (Map.Entry<Integer, Arc> e : n.getHashArcs().entrySet()) {
						if (e.getValue().getHead().hasDeleted())
							continue;
						
						if (!setA.contains(e.getValue())) {
							if (!setCut.contains(e.getKey())) {
								setCut.add(e.getKey());
								thisCut += e.getValue().qtd;
							}
						}
					}
				}
				
				// if (thisCut == 1) {
				// System.out.println("PERIGO2!");
				// int tc2 = 0;
				// setCut.clear();
				// for (Node n : setA) {
				// for (Map.Entry<Integer, Arc> e : n.getHashArcs().entrySet()) {
				// if (e.getValue().getHead().hasDeleted())
				// continue;
				//
				// if (!setA.contains(e.getValue())) {
				// if (!setCut.contains(e.getKey())) {
				// setCut.add(e.getKey());
				// tc2 += (Integer) e.getValue().getProps().get("qtd");
				// }
				// }
				// }
				// }
				// System.out.println("tc2=" + tc2);
				//
				// }
				
				minCut = Math.min(minCut, thisCut);
			}
			
			return minCut;
			
		} else {
			int t = (int) (net.qtdNodes / sqrt2) + 1;
			
			Network h1 = net; // Não precisa clonar um dos ramos da recursão.
			Network h2 = new Network(net);
			
			contract(h1, t);
			contract(h2, t);
			
			int c1 = fastCut(h1);
			int c2 = fastCut(h2);
			
			return Math.min(c1, c2);
		}
		
	}
	
	private int contract(Network net, int remainingNodes) {
		
		final int totalContractions = net.qtdNodes - remainingNodes;
		
		for (int cont = 0; cont < totalContractions; cont++) {
			
			// Sorteia um arco
			int nArc = gerador.nextInt(net.qtdArcs) + 1;
			
			// Encontra o nó que contém o arco no grafo
			int arcAcumum = 0;
			int cursor = 0;
			Node node = null;
			while (node==null) {
				Node _node = net.getNodes().get(cursor);
				cursor = (cursor+1);
				if (cursor == net.getNodes().size()) cursor = 0;
				if (_node != null && _node.hasDeleted() == false) {
					arcAcumum += _node.getDegree();
					if (arcAcumum >= nArc) {
						arcAcumum -= _node.getDegree();
						node = _node;
						break;
					}
				}
			}
			
			//			if(node == null)
			//				System.out.println("Ola");
			
			// Encontra o arco dentro do nó
			Arc arc = null;
			for (Map.Entry<Integer,Arc> n : node.getHashArcs().entrySet()) {
				arcAcumum += n.getValue().qtd;
				if (arcAcumum >= nArc) {
					arc = n.getValue();
					break;
				}
			}
			
			// if(arc == null)
			// System.out.println("Peguei");
			
			// if(cont == 10)
			// System.out.println("Hi!");
			
			// Contrai o arco
			net.contractEdge(arc);
			
			// Verifica se o grau do Tail está correto
			int tailDegree = arc.getTail().getDegree();
			int contDegree = 0;
			
			for (Arc neighbor : arc.getTail().getHashArcs().values()) {
				contDegree += neighbor.qtd;
			}
			
			if (tailDegree != contDegree)
				System.out.println("Problema no Degree");
			
		}
		
		return net.qtdArcs / 2;
	}
	
}
