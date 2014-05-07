import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;



public class Network {
	
	public static final int LARGE = Integer.MAX_VALUE / 2;
	public static final int Large = Integer.MAX_VALUE / 100;
	
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
					nodes.get(Integer.parseInt(split[1])-1).getProps().put("flow", Integer.parseInt(split[2]));
				}
				
				else if (line.startsWith("a ")){
									
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
					
					if (Integer.parseInt(split[3]) > 0){
						System.err.println("Grafo tem lower bound!!");
						System.exit(1);
					}
					
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
		
		//Faz o reCreate para criar as capacidades dos arcos na rede residual
		reCreateResidualNetwork();
	}

	public Path findPathResidual(Node nodeStart, Node nodeEnd, int cut) {
		clearNetworkDFS();
		DFSNetworkRes dfs = new DFSNetworkRes();
		Path resPath = dfs.findPath(nodeStart, nodeEnd, cut);
		return resPath;	
	}

	public Path findPathResidualBFS(Node nodeStart, Node nodeEnd, int cut) {
		clearNetworkBFS();
		BFSNetworkRes bfs = new BFSNetworkRes();
		Path resPath = bfs.findPath(nodeStart, nodeEnd, cut);
		return resPath;	
	}

	public void clearNetworkDFS(){
		//Limpa as marcações da DFS no grafo
		for(Node node : nodes){
			node.getProps().remove("dfs.P");
			node.getProps().remove("dfs.V");
		}
	}
	
	public void clearNetworkBFS(){
		//Limpa as marcações da DFS no grafo
		for(Node node : nodes){
			node.getProps().remove("bfs.P");
			node.getProps().remove("bfs.V");
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
		
	}
	
	public int fordFulkerson(Node s, Node t){
		int valueMaxFlow = 0;
		
		Path resPath = findPathResidual(s,t, 1); //Encontra um caminho aumentante	
		clearNetworkDFS(); //Limpa as marcações da DFS
		
		while(resPath.size() > 0){
			updateFlow(resPath, resPath.getBottleneck()); //Aumenta o fluxo no caminho aumentante
			reCreateResidualNetwork(); //Atualiza a rede residual com o novo fluxo
			resPath = findPathResidual(s,t, 1); //Encontra um caminho aumentante
			clearNetworkDFS(); //Limpa as marcações da DFS
		}

		for(Arc arc : s.getArcs())
			valueMaxFlow += (Integer)arc.getProps().get("flow");
		
		return valueMaxFlow;
	}
	
	public int edmondsKarp(Node s, Node t){
		int valueMaxFlow = 0;
		
		Path resPath = findPathResidualBFS(s,t,1); //Encontra um caminho aumentante	
		
		int count=0;
		long timer;
		long time = 0;
		while(resPath.size() > 0){
			updateFlow(resPath, resPath.getBottleneck()); //Aumenta o fluxo no caminho aumentante
			reCreateResidualNetwork(); //Atualiza a rede residual com o novo fluxo
			timer = System.currentTimeMillis();
			resPath = findPathResidualBFS(s,t,1); //Encontra um caminho aumentante
			time += System.currentTimeMillis()-timer;
			count += 1;
			if (count >= 1000){
				System.out.println("Tempo médio de findPathResidual: "+(time/1000)+"ms.");
				count = 0;
				time = 0;
			}
		}

		for(Arc arc : s.getArcs())
			valueMaxFlow += (Integer)arc.getProps().get("flow");
		
		return valueMaxFlow;
	}
	
	public ArrayList<Node> getNodes() {
		return nodes;
	}
	
	public void includeSuperNodeSSP(){
		//Inclui um nó que terá aresta de todos os nós para ele e dele para todos os nós
		
		Node superNode = new Node(-1);
		superNode.getProps().put("flow", 0);
		
		for(Node node : nodes){
			Arc arc = new Arc(node, superNode);
			arc.getProps().put("cap", LARGE);
			arc.getProps().put("cost", Large);
			arc.getProps().put("flow", 0);
			superNode.getArcs().add(arc);
			
			Arc arcInv = new Arc(superNode, node);
			arcInv.getProps().put("cap", LARGE);
			arcInv.getProps().put("cost", Large);
			arcInv.getProps().put("flow", 0);
			node.getArcs().add(arcInv);
		}
		
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

	public Response cycleCanceling(){
		Response responseCycleCanceling; // = new ResponseCycleCanceling();
		
		// Inicializa rede
		transformDistributionToMaxFlow();
		createResidualNetwork();
		long timer = System.currentTimeMillis();
		//Resolve o problema Max Flow com o algoritmo Ford-Fulkerson do nó S até o nó T
		int maxFlow = edmondsKarp(nodes.get(nodes.size()-2), nodes.get(nodes.size()-1));
		//int maxFlow = fordFulkerson(nodes.get(nodes.size()-2), nodes.get(nodes.size()-1));
		System.out.println("Fluxo Máximo: " + maxFlow+ " calculado em "+(System.currentTimeMillis()-timer)+"ms.");

		//Verifica se o problema tem solução viável
		if(!flowIsFeasibleToDistribution()){
			responseCycleCanceling = new Response(false);
			return responseCycleCanceling;
		}
		
		Path cycle = BelmanFord.findNegativeCycles(nodes, nodes.size()-2);
		System.out.println("Custo do Fluxo inicial: " + calcCostFlow());
		
		while(cycle.size() > 0){
			//System.out.println("*****Ciclo: " + cycle);
			updateFlow(cycle, cycle.getBottleneck());
				//System.out.println("Custo do Fluxo: " + calcCostFlow());
			reCreateResidualNetwork();
			cycle = BelmanFord.findNegativeCycles(nodes, nodes.size()-2);
		}
		
		System.out.println("###No more cycles;");
		
		responseCycleCanceling = new Response(calcCostFlow());
		return responseCycleCanceling;
	}
	
	public int calcCostFlow(){
		int costFlow = 0;
		
		for(Node node : nodes)
			for(Arc arc : node.getArcs())
				costFlow += (Integer)arc.getProps().get("flow") * (Integer)arc.getProps().get("cost");
		
		return costFlow;
	}
	
	public Response sucessiveShortestPath(){
		Response response;
		Node k, l;
		Path path = new Path();
		int delta;

		//Inclui um super nó e arcos deste nó para todos os outros nós e de todos os nós para ele
		includeSuperNodeSSP();
		
		for(Node node : nodes){
			node.getProps().put("ssp.p", 0);
			node.getProps().put("ssp.e", node.getProps().get("flow"));
		}
		
		for(Node node : nodes){
			for(Arc arc : node.getArcs()){
				arc.getProps().put("flow", 0);			
			}
		}
		
		//Inicializa os custos reduzidos nos arcos da rede residual
		for(Node node : nodes)
			for(ResidualArc resArc : node.getResidualArcs())
				resArc.getProps().put("reducedCost", resArc.getProps().get("cost"));			
		
		//Cria uma lista para o conjunto de nós de exesso e outra para o conjunto de nós de deficit
		ArrayList<Node> excessNodes = new ArrayList<Node>();
		ArrayList<Node> deficitNodes = new ArrayList<Node>();
		
		//Popula os arrays de nós de excesso e deficit
		for(Node node : nodes){
			if((Integer)node.getProps().get("ssp.e") < 0)
				deficitNodes.add(node);
			else if((Integer)node.getProps().get("ssp.e") > 0)
				excessNodes.add(node);
		}
		
		while(excessNodes.size() > 0){
			k = excessNodes.remove(0);
			l = deficitNodes.remove(0);
			
			//Calcula as distâncias de k para tdos os outros vértices
			Dijkstra.doDijkstra(nodes, k);
			
			//Encontra o caminho de k até s
			Node auxNode = l;
			ResidualArc auxResArc;
			while(auxNode.getProps().containsKey("djk.parent")){
				auxResArc = (ResidualArc)auxNode.getProps().get("djk.parent");			
				path.add(auxResArc);			
				auxNode = (Node)auxResArc.getTail();			
			}
		
			//Atualiza os pontenciais
			for(Node node : nodes)
				node.getProps().put("ssp.p", (Integer)node.getProps().get("ssp.p") - (Integer)node.getProps().get("djk.dist"));

			//Calcula delta
			delta = Math.min((Integer)k.getProps().get("ssp.e"), -(Integer)l.getProps().get("ssp.e"));
			delta = Math.min(delta, path.getBottleneck());
			
			//Passa o fluxo pelo caminho
			updateFlow(path, delta);
			
			//Atualiza os valores de e(k) e e(l)
			k.getProps().put("ssp.e", (Integer)k.getProps().get("ssp.e") - delta);
			l.getProps().put("ssp.e", (Integer)l.getProps().get("ssp.e") + delta);		
			path.clear();
			
			//Faz o update
			updateSSP();
			
		}
		
		
		response = new Response(calcCostFlow());
		return response;
	}
	
	public void updateSSP(){
		//Atualiza a rede residual
		reCreateResidualNetwork();
		
		//Atualiza os custos reduzidos
		for(Node node : nodes){
			for(ResidualArc resArc : node.getResidualArcs()){
				resArc.getProps().put("reducedCost", (Integer)resArc.getProps().get("cost") - (Integer)resArc.getTail().getProps().get("spp.p")  + (Integer)resArc.getHead().getProps().get("spp.p") );
			}
		}
		
	}
	
}
