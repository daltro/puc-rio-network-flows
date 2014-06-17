import java.util.Random;

public class ContractAlg {

	public int doContract(Network net){
		//Chama o contract para o novo grafo
		int minCut = Integer.MAX_VALUE;
		
		for(int i = 0; i < 10; i++){
			//Copia o grafo
			Network newNet = net.clone();
			
			int cut = contract(newNet);
			
			if(cut < minCut){
				minCut = cut; 
			}
		}
		
		return minCut;
	}
	
	private int contract(Network net){
		
		for(int cont = 0; cont < net.getNodes().size() - 2; cont++){
			//Sorteia um arco
			Random gerador = new Random(1);
			int nArc = gerador.nextInt(net.qtdArcs) + 1;
			
			//Encontra o nó que contém o arco no grafo
			int i, arcAcumum = 0;
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
			
			//Encontra o arco dentro do nó
			Arc arc = null;
			for(int neighbor : node.getHashArcs().keySet()){
				arcAcumum++;
				if(arcAcumum == nArc){
					arc = node.getHashArc(neighbor);
					break;
				}			
			}
	
			//Contrai o arco
			net.contractEdge(arc);
		}
		
	
		return 0;
	}
	
	
	
}
