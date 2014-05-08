import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class Network {
	
	private int largeCapacity = Integer.MAX_VALUE / 2;
	private int largeCost = Integer.MAX_VALUE / 1000;
	
	private ArrayList<Node> nodes = new ArrayList<Node>();
	
	public void calculateResidualCapacities() {
		Arc auxResArc;
		
		for (Node node : nodes) {
			
			for (Arc arc : node.getArcs()) {
				auxResArc = (Arc) arc.get("res.arc");
				auxResArc.set("cap",
				    (Integer) arc.get("cap") - (Integer) arc.get("flow"));
				
				auxResArc = (Arc) arc.get("res.arcInv");
				auxResArc.set("cap", arc.get("flow"));
			}
			
		}
	}
	
	private void calculateLargeNumbers() {
		
		int maxCap = 0;
		int maxCost = 0;
		
		for (Node node : nodes) {
			for (Arc arc : node.getArcs()) {
				maxCap = Math.max(maxCap, (Integer) arc.get("cap"));
				maxCost = Math.max(maxCost, (Integer) arc.get("cost"));
			}
		}
		
		largeCost = Math.max(maxCap, maxCost);
		largeCapacity = largeCost * 2;
		
		if((largeCost <= 0) || (largeCapacity <= 0))
			throw new IllegalStateException("Valores para largeCost e largeCapacity negativo.");

	}
	
	public void loadFromFile(File netFile) throws IOException {
		
		FileReader fin = new FileReader(netFile);
		try {
			@SuppressWarnings("resource")
			BufferedReader in = new BufferedReader(fin);
			
			int qtdNodes = 0;
			
			String line;
			while ((line = in.readLine()) != null) {
				if (line.startsWith("c "))
					continue; // comentário
					
				if (line.startsWith("p min ")) {
					// p min <nodes> <arcs>: definicão do problema
					String split[] = line.split("\\s+");
					// nodes = new Node[Integer.parseInt(split[2])];
					qtdNodes = Integer.parseInt(split[2]);
					
					// Cria todos os nós vazios no ArrayList
					for (int i = 0; i < qtdNodes; i++) {
						Node newNode = new Node(i + 1);
						newNode.set("flow", 0);
						nodes.add(newNode);
					}
					
				}
				
				else if (line.startsWith("n ")) {
					// Definição de um nó
					// n <id> <flow>
					String split[] = line.split("\\s+");
					nodes.get(Integer.parseInt(split[1]) - 1).set("flow",
					    Integer.parseInt(split[2]));
				}
				
				else if (line.startsWith("a ")) {
					
					// Definição de uma aresta
					// a <v> <w> <low> <cap> <cost>
					String split[] = line.split("\\s+");
					Arc newArc = new Arc(nodes.get(Integer.parseInt(split[2]) - 1),
					    nodes.get(Integer.parseInt(split[1]) - 1));
					
					newArc.getTail().getArcs().add(newArc);
					
					newArc.set("low", Integer.parseInt(split[3]));
					newArc.set("cap", Integer.parseInt(split[4]));
					newArc.set("cost", Integer.parseInt(split[5]));
					
					if (Integer.parseInt(split[3]) > 0) {
						System.err.println("Grafo tem lower bound!!");
						System.exit(1);
					}
					
					if (split.length == 7)
						newArc.set("flow", Integer.parseInt(split[6]));
					else
						newArc.set("flow", 0);
				}
				
			}
			
			// createResidualNetwork();
			
		} finally {
			fin.close();
		}
		
	}
	
	public void createResidualNetwork() {
		
		for (Node node : nodes)
			node.getResidualArcs().clear();
		
		// Cria os arcos da rede residual com custos mas sem capacidades
		for (Node node : nodes) {
			
			for (Arc arc : node.getArcs()) {
				if (arc.getTail() != node)
					throw new IllegalStateException("Teste de sanidade falhou");
				// Adiciona o arco na rede residual no mesmo sentido
				Arc newResArc = new Arc(arc.getHead(), arc.getTail());
				newResArc.getTail().getResidualArcs().add(newResArc);
				newResArc.set("cost", arc.get("cost"));
				
				// Adiciona o arco na rede residual no sentido inverso
				Arc newResArcInv = new Arc(arc.getTail(), arc.getHead());
				newResArcInv.getTail().getResidualArcs().add(newResArcInv);
				newResArcInv.set("cost", -(Integer) arc.get("cost"));
				
				// Faz a ligação dos arcos do grafo com os arcos da rede residual
				arc.set("res.arc", newResArc);
				arc.set("res.arcInv", newResArcInv);
				
				// Faz a ligação dos arcos da rede residual com os arcos do grafo
				newResArc.set("arc", arc);
				newResArcInv.set("arcInv", arc);
			}
		}
		
		// Faz o reCreate para criar as capacidades dos arcos na rede residual
		calculateResidualCapacities();
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
	
	public void clearNetworkDFS() {
		// Limpa as marcações da DFS no grafo
		for (Node node : nodes) {
			node.getProps().remove("dfs.P");
			node.getProps().remove("dfs.V");
		}
	}
	
	public void clearNetworkBFS() {
		// Limpa as marcações da DFS no grafo
		for (Node node : nodes) {
			node.getProps().remove("bfs.P");
			node.getProps().remove("bfs.V");
		}
	}
	
	public void updateFlow(Path path, int flow) {
		Arc auxArc;
		int auxFlow;
		for (Arc resArc : path.getPath()) {
			auxFlow = flow;
			
			// Se existe um arco com sentido inverso no grafo
			if (resArc.getProps().containsKey("arcInv")) {
				auxArc = (Arc) resArc.get("arcInv");
				if ((Integer) auxArc.get("flow") >= flow) {
					auxArc.set("flow", (Integer) auxArc.get("flow") - auxFlow);
					auxFlow = 0;
				} else {
					auxFlow -= (Integer) auxArc.get("flow");
					auxArc.set("flow", 0);
				}
			}
			
			// Se existe um arco com o mesmo sentido no grafo
			if (auxFlow > 0 && resArc.getProps().containsKey("arc")) {
				auxArc = (Arc) resArc.get("arc");
				auxArc.set("flow", (Integer) auxArc.get("flow") + auxFlow);
			}
			
		}
		
	}
	
	public int fordFulkerson(Node s, Node t) {
		int valueMaxFlow = 0;
		
		Path resPath = findPathResidual(s, t, 1); // Encontra um caminho aumentante
		clearNetworkDFS(); // Limpa as marcações da DFS
		
		while (resPath.size() > 0) {
			updateFlow(resPath, resPath.getBottleneck()); // Aumenta o fluxo no
			                                              // caminho aumentante
			calculateResidualCapacities(); // Atualiza a rede residual com o novo
			                               // fluxo
			resPath = findPathResidual(s, t, 1); // Encontra um caminho aumentante
			clearNetworkDFS(); // Limpa as marcações da DFS
		}
		
		for (Arc arc : s.getArcs())
			valueMaxFlow += (Integer) arc.get("flow");
		
		return valueMaxFlow;
	}
	
	public int edmondsKarp(Node s, Node t) {
		int valueMaxFlow = 0;
		
		Path resPath = findPathResidualBFS(s, t, 1); // Encontra um caminho
		                                             // aumentante
		
		int count = 0;
		long timer;
		long time = 0;
		while (resPath.size() > 0) {
			updateFlow(resPath, resPath.getBottleneck()); // Aumenta o fluxo no
			                                              // caminho aumentante
			calculateResidualCapacities(); // Atualiza a rede residual com o novo
			                               // fluxo
			timer = System.currentTimeMillis();
			resPath = findPathResidualBFS(s, t, 1); // Encontra um caminho aumentante
			time += System.currentTimeMillis() - timer;
			count += 1;
			if (count >= 1000) {
				System.out.println("Tempo médio de findPathResidual: " + (time / 1000)
				    + "ms.");
				count = 0;
				time = 0;
			}
		}
		
		for (Arc arc : s.getArcs())
			valueMaxFlow += (Integer) arc.get("flow");
		
		return valueMaxFlow;
	}
	
	public ArrayList<Node> getNodes() {
		return nodes;
	}
	
	public void includeSuperNodeSSP() {
		// Inclui um nó que terá aresta de todos os nós para ele e dele para todos
		// os nós
		
		Node superNode = new Node(-1);
		superNode.set("flow", 0);
		
		for (Node node : nodes) {
			Arc arc = new Arc(node, superNode);
			arc.set("cap", largeCapacity);
			arc.set("cost", largeCost);
			arc.set("flow", 0);
			superNode.getArcs().add(arc);
			
			Arc arcInv = new Arc(superNode, node);
			arcInv.set("cap", largeCapacity);
			arcInv.set("cost", largeCost);
			arcInv.set("flow", 0);
			node.getArcs().add(arcInv);
		}
		
		nodes.add(superNode);
		
	}
	
	public void transformDistributionToMaxFlow() {
		// Transforma o grafo do problema de distribuição com vários nós de
		// suprimento e demanda
		// para um grafo de um problema de fluxo máximo com uma fonte e um
		// sorvedouro
		
		// Cria os nós s e t no grafo
		Node nodeS = new Node(-1);
		nodeS.set("flow", 0);
		nodes.add(nodeS);
		Node nodeT = new Node(-2);
		nodeT.set("flow", 0);
		nodes.add(nodeT);
		
		// Adiciona arcos do nó S para os nós com fluxo positivo
		for (int i = 0; i < nodes.size() - 2; i++) {
			if ((Integer) nodes.get(i).get("flow") > 0) {
				Arc newArc = new Arc(nodes.get(i), nodeS);
				newArc.set("cap", nodes.get(i).get("flow"));
				newArc.set("flow", 0);
				newArc.set("cost", 0);
				nodeS.getArcs().add(newArc);
			}
		}
		
		// Adiciona arcos dos nós com fluxo negativo para o nó T
		for (int i = 0; i < nodes.size() - 2; i++) {
			if ((Integer) nodes.get(i).get("flow") < 0) {
				Arc newArc = new Arc(nodeT, nodes.get(i));
				newArc.set("cap", -(Integer) nodes.get(i).get("flow"));
				newArc.set("flow", 0);
				newArc.set("cost", 0);
				nodes.get(i).getArcs().add(newArc);
			}
		}
		
	}
	
	public boolean flowIsFeasibleToDistribution() {
		int flow = 0;
		int flowNodes = 0;
		
		// Determina o valor do fluxo máximo
		for (Arc arc : nodes.get(nodes.size() - 2).getArcs())
			flow += (Integer) arc.get("flow");
		
		// Computa a soma dos fluxos positivos
		for (Node node : nodes)
			flowNodes += (Integer) node.get("flow") > 0 ? (Integer) node.get("flow")
			    : 0;
		
		if (flow == flowNodes)
			return true;
		else
			return false;
	}
	
	public Response cycleCanceling() {
		
		System.out.println("-------------------------------");
		System.out.println("Rodando uma Cycles completa... ");
		System.out.println("-------------------------------");
		
		Response responseCycleCanceling; // = new ResponseCycleCanceling();
		
		// Inicializa rede
		transformDistributionToMaxFlow();
		createResidualNetwork();
		calculateLargeNumbers();
		
		long timer = System.currentTimeMillis();
		// Resolve o problema Max Flow com o algoritmo Ford-Fulkerson do nó S até o
		// nó T
		int maxFlow = edmondsKarp(nodes.get(nodes.size() - 2),
		    nodes.get(nodes.size() - 1));
		// int maxFlow = fordFulkerson(nodes.get(nodes.size()-2),
		// nodes.get(nodes.size()-1));
		System.out.println("Fluxo Máximo: " + maxFlow + " calculado em "
		    + (System.currentTimeMillis() - timer) + "ms.");
		
		// Verifica se o problema tem solução viável
		if (!flowIsFeasibleToDistribution()) {
			responseCycleCanceling = new Response(false);
			return responseCycleCanceling;
		}
		
		Path cycle = BelmanFord.findNegativeCycles(nodes, nodes.size() - 2);
		System.out.println("Custo do Fluxo inicial: " + calcCostFlow());
		
		int loops = 0;
		while (cycle.size() > 0) {
			// System.out.println("*****Ciclo: " + cycle);
			updateFlow(cycle, cycle.getBottleneck());
			// System.out.println("Custo do Fluxo: " + calcCostFlow());
			calculateResidualCapacities();
			cycle = BelmanFord.findNegativeCycles(nodes, nodes.size() - 2);
			System.out.print(".");
			if (++loops % 30 == 0)
				System.out.println();
		}
		
		System.out.println("###No more cycles;");
		
		responseCycleCanceling = new Response(calcCostFlow());
		return responseCycleCanceling;
	}
	
	public int calcCostFlow() {
		int costFlow = 0;
		
		for (Node node : nodes)
			for (Arc arc : node.getArcs()) {
				costFlow += (Integer) arc.get("flow") * (Integer) arc.get("cost");
			}
		
		return costFlow;
	}
		
	public Response sucessiveShortestPath() {
		//calculateLargeNumbers();
		
		// Inclui um super nó e arcos deste nó para todos os outros nós e de todos
		// os nós para ele
		includeSuperNodeSSP();
		
		createResidualNetwork();
		
		for (Node node : nodes) {
			node.set("ssp.p", 0);
			node.set("ssp.e", node.get("flow"));
		}
		
		for (Node node : nodes) {
			for (Arc arc : node.getArcs()) {
				arc.set("flow", 0);
			}
		}
		
		// Inicializa os custos reduzidos nos arcos da rede residual
		for (Node node : nodes)
			for (Arc resArc : node.getResidualArcs())
				resArc.set("reducedCost", resArc.get("cost"));
		
		
		return doSSP(0);
		
	}
	
	public Response doSSP(int cut){
		// Cria uma lista para o conjunto de nós de exesso e outra para o conjunto
		// de nós de deficit
		LinkedList<Node> excessNodes = new LinkedList<>();
		LinkedList<Node> deficitNodes = new LinkedList<>();
		
		// Popula os arrays de nós de excesso e deficit
		int e;
		for (Node node : nodes) {
			e = (Integer) node.get("ssp.e");
			if(e != 0){
				if (e <= cut)
					deficitNodes.add(node);
				if (e >= cut) 
					excessNodes.add(node);
			}
		}
		
		while (excessNodes.size() > 0) {
			Node k = excessNodes.removeFirst();
			Node l = deficitNodes.removeFirst();
			
			// Calcula as distâncias de k para tdos os outros vértices
			Dijkstra.doDijkstra(nodes, k, largeCapacity, cut);
		
			// Encontra o caminho de k até s
			Node auxNode = l;
			Arc auxResArc;
			Path path = new Path();
			while (auxNode.getProps().containsKey("djk.parent")) {
				auxResArc = (Arc) auxNode.get("djk.parentArc");
				path.add(auxResArc);
				auxNode = auxResArc.getTail();
			}
			
			// Atualiza os pontenciais
			for (Node node : nodes){
				if(Math.abs((Integer)node.get("ssp.e")) >= cut)
					node.set("ssp.p", (Integer) node.get("ssp.p") - (Integer) node.get("djk.dist"));
			}
			
			// Calcula delta
			int delta = Math.min((Integer) k.get("ssp.e"), -(Integer) l.get("ssp.e"));
			delta = Math.min(delta, path.getBottleneck());
			
			// Passa o fluxo pelo caminho
			updateFlow(path, delta);
			
			// Atualiza os valores de e(k) e e(l)
			k.set("ssp.e", (Integer) k.get("ssp.e") - delta);
			l.set("ssp.e", (Integer) l.get("ssp.e") + delta);
			path.clear();
			
			// Faz o update
			updateSSP(cut);
			
			if ((Integer) k.get("ssp.e") > 0)
				excessNodes.addLast(k);
			
			if ((Integer) l.get("ssp.e") < 0)
				deficitNodes.addLast(l);
			
		}
		
		//Verifica se a solução é viável
		for(Arc arc : nodes.get(nodes.size()-1).getArcs()){
			if((Integer)arc.get("flow") > 0)
				return new Response(false);
		}
		
		return new Response(calcCostFlow());
	}
	
	
	public void updateSSP(int cut) {
		// Atualiza a rede residual
		calculateResidualCapacities();
		
		// Atualiza os custos reduzidos
		for (Node node : nodes) {
			
			for (Arc resArc : node.getResidualArcs()) {
				//if((Integer)resArc.get("cap") < cut)
					//continue;
				
				resArc.set("reducedCost", (Integer) resArc.get("cost")
				    - (Integer) resArc.getTail().get("ssp.p")
				    + (Integer) resArc.getHead().get("ssp.p"));			
			}
			
		}
		
	}

	public Response capacityScaling(){
		int maxEdgeCapacity = 0;
		int delta, flow;
		Response response;
		Arc auxArc;
		Double deltaDouble;
		Double log2_10 = Math.log10(2);

		//-------------
		// Inclui um super nó e arcos deste nó para todos os outros nós e de todos os nós para ele
		includeSuperNodeSSP();
		
		createResidualNetwork();
		
		for (Node node : nodes) {
			node.set("ssp.p", 0);
			node.set("ssp.e", node.get("flow"));
		}
		
		for (Node node : nodes) {
			for (Arc arc : node.getArcs()) {
				arc.set("flow", 0);
			}
		}
		
		// Inicializa os custos reduzidos nos arcos da rede residual
		for (Node node : nodes)
			for (Arc resArc : node.getResidualArcs())
				resArc.set("reducedCost", resArc.get("cost"));
		
			
		//Encontra a aresta com a maior capacidade
		for(Node node : nodes)
			for(Arc arc : node.getArcs())
				maxEdgeCapacity = Math.max(maxEdgeCapacity, (Integer)arc.get("cap"));

		deltaDouble =  Math.log10(maxEdgeCapacity) / log2_10;
		delta = deltaDouble.intValue();
		deltaDouble = Math.pow(2, delta);
		delta = deltaDouble.intValue();
		
		while(delta >= 1){
			//Red-code para concertar os curstos residuais negativos
			for(Node node : nodes){
				for(Arc resArc : node.getResidualArcs()){
					if(((Integer)resArc.get("cap") >= delta) && ((Integer)resArc.get("reducedCost") < 0)){
						auxArc = (Arc)resArc.get("arc"); //Ver se não tem que olhar o arco inverso para passar fluxo também
						flow = (Integer)resArc.get("cap"); 
						auxArc.getProps().put("flow", (Integer)auxArc.get("flow") + flow);
						resArc.getTail().getProps().put("spp.e", (Integer)resArc.get("spp.e") - flow);
						resArc.getHead().getProps().put("spp.e", (Integer)resArc.get("spp.e") + flow);
					}
				}
			}
			
			response = doSSP(delta);
			if(response.isFeasibleSolution())
				return response;
			
			delta /= 2;
		}
		
		return new Response(false);
	}
	
	public void printGraph(){
		//Imprime o grafo
		System.out.println("Grafo----------------------");
		for(Node node: nodes){
			System.out.println(node);
			for(Arc arc: node.getArcs())
				System.out.println(arc);
		}

		System.out.println("Grafo Residual ----------------------");
		for(Node node: nodes){
			System.out.println(node);
			for(Arc resArc: node.getResidualArcs())
				System.out.println(resArc);
		}	
	}
	
	public void dump(boolean nodes, boolean arcs, boolean residualArcs) {
		System.out.println("Dump do grafo nodes=" + nodes + ", arcs=" + arcs
		    + ", residual=" + residualArcs);
		System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
		for (Node n : this.nodes) {
			if (nodes) {
				System.out.println(n.toString());
			}
			if (arcs) {
				for (Arc a : n.getArcs())
					System.out.println(" -> " + a.toString());
			}
			if (residualArcs) {
				for (Arc a : n.getResidualArcs())
					System.out.println(" R> " + a.toString());
			}
		}
	}
}
