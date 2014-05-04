import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class Network {
	
	//private Node[] nodes = new Node[0];
	private ArrayList<Node> nodes = new ArrayList<Node>();
		
	public void reCreateResidualNetwork(){
		ResidualArc auxResArc;
		
		for(Node node : nodes){

			for(Arc arc : node.getArcs()){		
				auxResArc = (ResidualArc)arc.getProps().get("res.arc");
				auxResArc.getProps().put("cap", (Integer)arc.getProps().get("cap") - (Integer)arc.getProps().get("flow") );
				
				auxResArc = (ResidualArc)arc.getProps().get("res.arcInv");
				auxResArc.getProps().put("cap", (Integer)arc.getProps().get("flow") );					
			}
			
		}

/*		
		System.out.println("Grafo--------------");
		for(Node node : nodes){
			System.out.println(node);
			for(Arc Arc : node.getArcs())
				System.out.println(Arc);
		}

		System.out.println("Rede Residual--------------");
		for(Node node : nodes){
			System.out.println(node);
			for(ResidualArc resArc : node.getResidualArcs())
				System.out.println(resArc);
		}
*/

/*
		Arc arcInv;
		ResidualArc auxResArc;
		
		for(Node node : nodes){
			//System.out.println(node);
			for(Arc arc : node.getArcs()){
				//System.out.println(arc);

				//Verifica se existe um arco de head para tail no grafo
				arcInv = arc.getHead().hasArc(arc.getTail());
				
				if(arcInv == null){
					auxResArc = (ResidualArc)arc.getProps().get("res.arc");
					auxResArc.getProps().put("cap", (Integer)arc.getProps().get("cap") - (Integer)arc.getProps().get("flow") );
					
					auxResArc = (ResidualArc)arc.getProps().get("res.arcInv");
					auxResArc.getProps().put("cap", (Integer)arc.getProps().get("flow") );					
				}else{
					auxResArc = (ResidualArc)arc.getProps().get("res.arc");
					auxResArc.getProps().put("cap", (Integer)arc.getProps().get("cap") - (Integer)arc.getProps().get("flow") + (Integer)arcInv.getProps().get("flow"));
					
					auxResArc = (ResidualArc)arc.getProps().get("res.arcInv");
					auxResArc.getProps().put("cap", (Integer)arcInv.getProps().get("cap") - (Integer)arcInv.getProps().get("flow") + (Integer)arc.getProps().get("flow") );
				}
			}
			
		}
		
 */
		
	}
	
	public void loadFromFile(File netFile) throws IOException{
		
		FileReader fin = new FileReader(netFile);
		try{
			@SuppressWarnings("resource")
			BufferedReader in = new BufferedReader(fin);
			
			boolean nodesEnded = false;
			int qtdNodes = 0;
			
			String line;
			while ((line = in.readLine())!=null){
				if (line.startsWith("c "))
					continue; // comentário
				
				if (line.startsWith("p min ")){
					// p min <nodes> <arcs>: definicão do problema
					String split[] = line.split("\\s+");
					//nodes = new Node[Integer.parseInt(split[2])];
					qtdNodes = Integer.parseInt(split[2]);
					
					//Cria todos os nós vazios no ArrayList
					for(int i = 0; i < qtdNodes; i++){
						Node newNode = new Node(i+1);
						newNode.getProps().put("flow", 0);
						nodes.add(newNode);
					}

				}
				
				else if (line.startsWith("n ")){
					// Definição de um nó
					// n <id> <flow>
					String split[] = line.split("\\s+");
					Node newNode = new Node(Integer.parseInt(split[1]));
					//nodes[newNode.getId()-1] = newNode;
					newNode.getProps().put("flow", Integer.parseInt(split[2]));
					nodes.set(newNode.getId()-1, newNode);
				}
				
				else if (line.startsWith("a ")){
					
					if (!nodesEnded){
						nodesEnded = true;
						
/*						for (int i=0; i<nodes.size(); i+=1){
							if (nodes[i]==null){
								Node newNode = new Node(i+1);
								nodes[i] = newNode;
								newNode.getProps().put("flow", 0);
							}
						}
*/						
					}
					
					// Definição de uma aresta
					// a <v> <w> <low> <cap> <cost>
					String split[] = line.split("\\s+");
					Arc newArc = new Arc(
							nodes.get(Integer.parseInt(split[2])-1),
							nodes.get(Integer.parseInt(split[1])-1));
					
					newArc.getTail().getArcs().add(newArc);
					
					newArc.getProps().put("low", Integer.parseInt(split[3]));
					newArc.getProps().put("cap", Integer.parseInt(split[4]));
					newArc.getProps().put("cost", Integer.parseInt(split[5]));
					
					if(split.length == 7)
						newArc.getProps().put("flow", Integer.parseInt(split[6]));
					else
						newArc.getProps().put("flow", 0);
				}
				
			}
			
			//createResidualNetwork();
						
		}
		finally{
			fin.close();
		}
		
	}
	
	public void createResidualNetwork(){

		//Cria os arcos da rede residual com custos mas sem capacidades
		for(Node node : nodes){
			for(Arc arc : node.getArcs()){
				//Adiciona o arco na rede residual no mesmo sentido
				ResidualArc newResArc = new ResidualArc(arc.getHead(), arc.getTail());
				newResArc.getTail().getResidualArcs().add(newResArc);
				newResArc.getProps().put("cost", (Integer)arc.getProps().get("cost"));					
				
				//Adiciona o arco na rede residual no sentido inverso
				ResidualArc newResArcInv = new ResidualArc(arc.getTail(), arc.getHead());
				newResArcInv.getTail().getResidualArcs().add(newResArcInv);
				newResArcInv.getProps().put("cost", -(Integer)arc.getProps().get("cost"));
				
				//Faz a ligação dos arcos do grafo com os arcos da rede residual
				arc.getProps().put("res.arc", newResArc);
				arc.getProps().put("res.arcInv", newResArcInv);
				
				//Faz a ligação dos arcos da rede residual com os arcos do grafo
				newResArc.getProps().put("arc", arc);
				newResArcInv.getProps().put("arcInv", arc);
			}			
		}

		
/*		
		//Cria os arcos da rede residual com custos mas sem capacidades
		for(Node node : nodes){
			for(Arc arc : node.getArcs()){
				//Verifica se existe um arco de head para tail no grafo
				arcInv = arc.getHead().hasArc(arc.getTail());

				//Se tem arco inverso mas não foi visitado ainda ou não tem arco inverso
				//Então cria os arcos do grafo residual e as ligações entre os arcos do grafo e da rede residual
				if((arcInv != null && !arcInv.getProps().containsKey("res.arc")) || (arcInv == null)){
					//Adiciona o arco na rede residual no mesmo sentido
					ResidualArc newResArc = new ResidualArc(arc.getHead(), arc.getTail());
					newResArc.getTail().getResidualArcs().add(newResArc);
					newResArc.getProps().put("cost", (Integer)arc.getProps().get("cost"));					
					
					//Adiciona o arco na rede residual no sentido inverso
					ResidualArc newResArcInv = new ResidualArc(arc.getTail(), arc.getHead());
					newResArcInv.getTail().getResidualArcs().add(newResArcInv);
					newResArcInv.getProps().put("cost", -(Integer)arc.getProps().get("cost"));
					
					//Faz a ligação dos arcos do grafo com os arcos da rede residual
					arc.getProps().put("res.arc", newResArc);
					arc.getProps().put("res.arcInv", newResArcInv);
					
					//Faz a ligação dos arcos da rede residual com os arcos do grafo
					newResArc.getProps().put("arc", arc);
					newResArcInv.getProps().put("arcInv", arc);

				}else{ //Se tem arco inverso e ele já foi visitado
					
					//Cria as ligações entre os arcos do grafo e da rede residual 
					arc.getProps().put("res.arc", arcInv.getProps().get("res.arcInv"));
					arc.getProps().put("res.arcInv", arcInv.getProps().get("res.arc"));
					
					//Cria as ligações dos arcos da rede residual com os arcos do grafo
					ResidualArc auxResArc;
					auxResArc = (ResidualArc)arcInv.getProps().get("res.arcInv");
					auxResArc.getProps().put("arc", arc);
					auxResArc = (ResidualArc)arcInv.getProps().get("res.arc");
					auxResArc.getProps().put("arcInv", arc);
					
				}
			}			
		}
*/		
		//Faz o reCreate para criar as capacidades dos arcos na rede residual
		reCreateResidualNetwork();
	}

	public Path findPathResidual(Node nodeStart, Node nodeEnd) {
		DFSNetworkRes dfs = new DFSNetworkRes();
		Path resPath = dfs.findPath(nodeStart, nodeEnd);
		return resPath;	
	}
	
	public void clearNetworkDFS(){
		//Limpa as marcações da DFS no grafo
		for(Node node : nodes){
			node.getProps().remove("dfs.P");
			node.getProps().remove("dfs.V");
		}
	}
	
	public void updateFlow(Path path, int flow){
		Arc auxArc;
		int auxFlow;
		for(ResidualArc resArc : path.getPath()){
			auxFlow = flow;
			
			//Se existe um arco com sentido inverso no grafo
			if(resArc.getProps().containsKey("arcInv")){
				auxArc = (Arc)resArc.getProps().get("arcInv");
				if((Integer)auxArc.getProps().get("flow") >= flow){
					auxArc.getProps().put("flow", (Integer)auxArc.getProps().get("flow") - auxFlow);
					auxFlow = 0;
				}else{
					auxFlow -= (Integer)auxArc.getProps().get("flow");
					auxArc.getProps().put("flow", 0);				
				}
			}

			//Se existe um arco com o mesmo sentido no grafo
			if(resArc.getProps().containsKey("arc") && auxFlow > 0){
				auxArc = (Arc)resArc.getProps().get("arc");
				auxArc.getProps().put("flow", (Integer)auxArc.getProps().get("flow") + auxFlow);
			}

		}
		
/*		Arc auxArc;
		int auxFlow;
		
		for(ResidualArc resArc : path.getPath()){
			
			if((Integer)resArc.getProps().get("cost") > 0){
				auxArc = (Arc)resArc.getProps().get("arc");
				auxArc.getProps().put("flow", (Integer)resArc.getProps().get("flow") + flow);
			}else if((Integer)resArc.getProps().get("cost") < 0){
				auxArc = (Arc)resArc.getProps().get("arcInv");
				auxArc.getProps().put("flow", (Integer)resArc.getProps().get("flow") - flow);
			}else{
				auxFlow = flow;
				//Se existe um arco com sentido inverso no grafo
				if(resArc.getProps().containsKey("arcInv")){
					auxArc = (Arc)resArc.getProps().get("arcInv");
					if((Integer)auxArc.getProps().get("flow") >= flow){
						auxArc.getProps().put("flow", (Integer)auxArc.getProps().get("flow") - auxFlow);
						auxFlow = 0;
					}else{
						auxFlow -= (Integer)auxArc.getProps().get("flow");
						auxArc.getProps().put("flow", 0);				
					}
				}

				//Se existe um arco com o mesmo sentido no grafo
				if(resArc.getProps().containsKey("arc") && auxFlow > 0){
					auxArc = (Arc)resArc.getProps().get("arc");
					auxArc.getProps().put("flow", (Integer)auxArc.getProps().get("flow") + auxFlow);
				}
				
			}

		}
*/
		
	}
	
	public int fordFulkerson(Node s, Node t){
		int valueMaxFlow = 0;
		
		Path resPath = findPathResidual(s,t); //Encontra um caminho aumentante	
		clearNetworkDFS(); //Limpa as marcações da DFS
		
		while(resPath.size() > 0){
			updateFlow(resPath, resPath.getBottleneck()); //Aumenta o fluxo no caminho aumentante
			reCreateResidualNetwork(); //Atualiza a rede residual com o novo fluxo
			resPath = findPathResidual(s,t); //Encontra um caminho aumentante
			clearNetworkDFS(); //Limpa as marcações da DFS
		}

		for(Arc arc : s.getArcs())
			valueMaxFlow += (Integer)arc.getProps().get("flow");
		
		return valueMaxFlow;
	}
	
	public ArrayList<Node> getNodes() {
		return nodes;
	}
	
	public void transformDistributionToMaxFlow(){
		//Transforma o grafo do problema de distribuição com vários nós de suprimento e demanda
		//para um grafo de um problema de fluxo máximo com uma fonte e um sorvedouro
		
		//Cria os nós s e t no grafo
		Node nodeS = new Node(-1);
		nodeS.getProps().put("flow", 0);
		nodes.add(nodeS);
		Node nodeT = new Node(-2);
		nodeT.getProps().put("flow", 0);
		nodes.add(nodeT);
		
		//Adiciona arcos do nó S para os nós com fluxo positivo
		for(int i = 0; i < nodes.size() - 2; i++){
			if((Integer)nodes.get(i).getProps().get("flow") > 0){
				Arc newArc = new Arc(nodes.get(i), nodeS);
				newArc.getProps().put("cap", (Integer)nodes.get(i).getProps().get("flow"));
				newArc.getProps().put("flow", 0);				
				newArc.getProps().put("cost", 0);
				nodeS.getArcs().add(newArc);				
			}
		}

		//Adiciona arcos dos nós com fluxo negativo para o nó T
		for(int i = 0; i < nodes.size() - 2; i++){
			if((Integer)nodes.get(i).getProps().get("flow") < 0){
				Arc newArc = new Arc(nodeT, nodes.get(i)); 
				newArc.getProps().put("cap", -(Integer)nodes.get(i).getProps().get("flow"));
				newArc.getProps().put("flow", 0);
				newArc.getProps().put("cost", 0);
				nodes.get(i).getArcs().add(newArc);	
			}
		}		
		
	}
	
	public boolean flowIsFeasibleToDistribution(){
		int flow = 0;
		int flowNodes = 0;
		
		//Determina o valor do fluxo máximo
		for(Arc arc : nodes.get(nodes.size()-2).getArcs())
			flow += (Integer)arc.getProps().get("flow");
		
		//Computa a soma dos fluxos positivos 
		for(Node node : nodes)
			flowNodes += (Integer)node.getProps().get("flow") > 0 ? (Integer)node.getProps().get("flow") : 0;

		if(flow == flowNodes)
			return true;
		else
			return false;
	}

	public ResponseCycleCanceling cycleCanceling(){
		ResponseCycleCanceling responseCycleCanceling; // = new ResponseCycleCanceling();
		
		//Resolve o problema Max Flow com o algoritmo Ford-Fulkerson do nó S até o nó T
		int maxFlow = fordFulkerson(nodes.get(nodes.size()-2), nodes.get(nodes.size()-1));
		System.out.println("Fluxo Máximo: " + maxFlow);
			
		//Verifica se o problema tem solução viável
		if(!flowIsFeasibleToDistribution()){
			responseCycleCanceling = new ResponseCycleCanceling(false);
			return responseCycleCanceling;
		}
		
		Path cycle = BelmanFord.findNegativeCycles(nodes, nodes.size()-2);
		System.out.println("Custo do Fluxo: " + calcCostFlow());
		
		while(cycle.size() > 0){
			//System.out.println("*****Ciclo: " + cycle);
			updateFlow(cycle, cycle.getBottleneck());
				//System.out.println("Custo do Fluxo: " + calcCostFlow());
			reCreateResidualNetwork();
			cycle = BelmanFord.findNegativeCycles(nodes, nodes.size()-2);
		}
		
		System.out.println("###No more cycles;");
		
		responseCycleCanceling = new ResponseCycleCanceling(calcCostFlow());
		return responseCycleCanceling;
	}
	
	public int calcCostFlow(){
		int costFlow = 0;
		
		for(Node node : nodes)
			for(Arc arc : node.getArcs())
				costFlow += (Integer)arc.getProps().get("flow") * (Integer)arc.getProps().get("cost");
		
		return costFlow;
	}
	
}
