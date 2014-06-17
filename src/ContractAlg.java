import java.util.Random;

public class ContractAlg {

	public int doContract(Network net){
		//Chama o contract para o novo grafo
		int minCut = Integer.MAX_VALUE;
		int times = net.getNodes().size() * net.getNodes().size() * (int)Math.log(net.getNodes().size());	
		
		for(int i = 0; i < times/100; i++){
			//Copia o grafo
			Network newNet = net.clone();
			
			int cut = contract(newNet);
			
			//System.out.println("Corte mínimo: " + minCut + " - Corte: " + cut);
			
			if(cut < minCut){
				minCut = cut; 
			}
		}
		
		return minCut;
	}
	
	private int contract(Network net){
		Random gerador = new Random();
		
		for(int cont = 0; cont < net.getNodes().size() - 2; cont++){

			
			//Sorteia um arco
			int nArc = gerador.nextInt(net.qtdArcs) + 1;
			
			//Encontra o nó que contém o arco no grafo
			int arcAcumum = 0;
			Node node = null;
			for(Node _node : net.getNodes()){
				if(_node.hasDeleted() == false){
					arcAcumum += _node.getDegree();
					if(arcAcumum >= nArc){
						arcAcumum -= _node.getDegree();
						node = _node;
						break;
					}
				}
			}
			
			//if(node == null)
				//System.out.println("Ola");

			
			//Encontra o arco dentro do nó
			Arc arc = null;
			for(int neighbor : node.getHashArcs().keySet()){
				arcAcumum += (int)node.getHashArc(neighbor).get("qtd");
				if(arcAcumum >= nArc){
					arc = node.getHashArc(neighbor);
					break;
				}			
			}
	
			//if(arc == null)
				//System.out.println("Peguei");

			//if(cont == 10)
				//System.out.println("Hi!");
			
			//Contrai o arco
			net.contractEdge(arc);
			
			//Verifica se o grau do Tail está correto
			int tailDegree = arc.getTail().getDegree();
			int contDegree = 0;
			
			for(int neighbor : arc.getTail().getHashArcs().keySet()){
				Arc auxArc = arc.getTail().getHashArc(neighbor);
				contDegree += (int)auxArc.get("qtd");
			}
			
			if(tailDegree != contDegree)
				System.out.println("Problema no Degree");
			
		}

		return net.qtdArcs / 2;
	}
	
	
	
}
