import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

public class Network {
	
	private int largeCapacity = Integer.MAX_VALUE / 10;
	private int largeCost = Integer.MAX_VALUE / 1000;
	public int qtdArcs = 0;
	public int qtdNodes = 0;
	
	public static long clonningTime = 0;
	
	private final ArrayList<Node> nodes;
	
	public Network() {
		nodes = new ArrayList<Node>();
	}
	
	public Network(Network toClone) {
		long timer = System.nanoTime();
		
		nodes = new ArrayList<Node>(toClone.nodes.size());
		qtdArcs = toClone.qtdArcs;
		qtdNodes = toClone.qtdNodes;
		
		for (Node nodeToClone : toClone.getNodes()){
			if (nodeToClone!=null && !nodeToClone.hasDeleted())
				nodes.add(new Node(nodeToClone));
			else
				nodes.add(null);
		}
		
		for (int i = 0; i < nodes.size(); i += 1) {
			Node original = toClone.nodes.get(i);
			if (original == null || original.hasDeleted())
				continue;
			Node clone = nodes.get(i);
			for (Map.Entry<Integer, Arc> a : original.getHashArcs().entrySet()) {
				if (a.getValue().getHead().hasDeleted())
					continue;
				Arc cloneArc = new Arc(nodes.get(a.getValue().getHead().getId() - 1), nodes.get(a.getValue().getTail().getId() - 1), a.getValue().getProps().size());
				cloneArc.qtd = a.getValue().qtd;
				clone.getHashArcs().put(a.getKey(), cloneArc);
			}
		}
		clonningTime += (System.nanoTime() - timer);
	}
	
	public void calculateResidualCapacities() {
		Arc auxResArc;
		
		for (Node node : nodes) {
			
			for (Arc arc : node.getArcs()) {
				auxResArc = (Arc) arc.get("res.arc");
				auxResArc.set("cap", (Integer) arc.get("cap") - (Integer) arc.get("flow"));
				
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
		
		if ((largeCost <= 0) || (largeCapacity <= 0))
			throw new IllegalStateException("Valores para largeCost e largeCapacity negativo.");
		
	}
	
	public void loadFromFile(File netFile) throws IOException {
		
		FileReader fin = new FileReader(netFile);
		try {
			@SuppressWarnings("resource")
			BufferedReader in = new BufferedReader(fin);
			
			String line;
			while ((line = in.readLine()) != null) {
				if (line.startsWith("c "))
					continue; // comentário
				
				if (line.startsWith("p min ")) {
					// p min <nodes> <arcs>: definicão do problema
					String split[] = line.split("\\s+");
					// nodes = new Node[Integer.parseInt(split[2])];
					qtdNodes = Integer.parseInt(split[2]);
					qtdArcs = 2 * Integer.parseInt(split[3]);
					
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
					nodes.get(Integer.parseInt(split[1]) - 1).set("flow", Integer.parseInt(split[2]));
				}
				
				else if (line.startsWith("a ")) {
					
					// Definição de uma aresta
					// a <v> <w> <low> <cap> <cost>
					String split[] = line.split("\\s+");
					Arc newArc = new Arc(nodes.get(Integer.parseInt(split[2]) - 1), nodes.get(Integer.parseInt(split[1]) - 1));
					
					Arc newArcHead = new Arc(nodes.get(Integer.parseInt(split[1]) - 1), nodes.get(Integer.parseInt(split[2]) - 1));
					
					if (newArc.getTail().getHashArcs().containsKey(newArc.getHead().getId())) {
						newArc.qtd = 2;
						newArcHead.qtd = 2;
					} else {
						newArc.qtd = 1;
						newArcHead.qtd = 1;
					}
					
					newArc.getTail().setHashArc(newArc.getHead().getId(), newArc);
					newArc.getHead().setHashArc(newArc.getTail().getId(), newArcHead);
					
					newArc.getHead().incDegree(1);
					newArc.getTail().incDegree(1);
					
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
				System.out.println("Tempo médio de findPathResidual: " + (time / 1000) + "ms.");
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
			flowNodes += (Integer) node.get("flow") > 0 ? (Integer) node.get("flow") : 0;
			
			if (flow == flowNodes)
				return true;
			else
				return false;
	}
	
	public Response cycleCanceling(boolean edmondsKarp) {
		
		final Response responseCycleCanceling = new Response();
		
		// Inicializa rede
		transformDistributionToMaxFlow();
		createResidualNetwork();
		calculateLargeNumbers();
		
		TimerAndReporter.start();
		// Resolve o problema Max Flow com o algoritmo Ford-Fulkerson do nó S até o
		// nó T
		int maxFlow;
		if (edmondsKarp) {
			maxFlow = edmondsKarp(nodes.get(nodes.size() - 2), nodes.get(nodes.size() - 1));
		} else {
			maxFlow = fordFulkerson(nodes.get(nodes.size() - 2), nodes.get(nodes.size() - 1));
		}
		responseCycleCanceling.setTimePreparing(TimerAndReporter.elapsed());
		
		System.out.println("Fluxo Máximo: " + maxFlow + " calculado em " + TimerAndReporter.elapsed() + "ms.");
		
		// Verifica se o problema tem solução viável
		if (!flowIsFeasibleToDistribution()) {
			responseCycleCanceling.setFeasibleSolution(false);
			return responseCycleCanceling;
		}
		
		TimerAndReporter.start();
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
			if (++loops % 100 == 0)
				System.out.println();
		}
		responseCycleCanceling.setTimeRunning(TimerAndReporter.elapsed());
		responseCycleCanceling.setLoops(loops);
		responseCycleCanceling.setCostFlow(calcCostFlow());
		responseCycleCanceling.setFeasibleSolution(true);
		
		System.out.println("###No more cycles;");
		
		return responseCycleCanceling;
	}
	
	public long calcCostFlow() {
		long costFlow = 0;
		
		for (Node node : nodes)
			for (Arc arc : node.getArcs()) {
				costFlow += (Integer) arc.get("flow") * (Integer) arc.get("cost");
			}
		
		return costFlow;
	}
	
	public Response sucessiveShortestPath() {
		// calculateLargeNumbers();
		
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
		
		return doSSP(1);
		
	}
	
	public Response doSSP(int cut) {
		final Response res = new Response();
		
		TimerAndReporter.start();
		
		// Cria uma lista para o conjunto de nós de exesso e outra para o conjunto
		// de nós de deficit
		LinkedList<Node> excessNodes = new LinkedList<>();
		LinkedList<Node> deficitNodes = new LinkedList<>();
		
		// Popula os arrays de nós de excesso e deficit
		int e;
		for (Node node : nodes) {
			e = (Integer) node.get("ssp.e");
			if (e != 0) {
				if (e <= -cut)
					deficitNodes.add(node);
				if (e >= cut)
					excessNodes.add(node);
			}
		}
		
		res.setTimePreparing(TimerAndReporter.elapsed());
		TimerAndReporter.start();
		
		int loops = 0;
		
		while (excessNodes.size() > 0 && deficitNodes.size() > 0) {
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
			for (Node node : nodes) {
				// if (Math.abs((Integer) node.get("ssp.e")) >= cut)
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
			
			if ((Integer) k.get("ssp.e") >= cut)
				excessNodes.addLast(k);
			
			if ((Integer) l.get("ssp.e") <= -cut)
				deficitNodes.addLast(l);
			
			System.out.print(".");
			if (++loops % 90 == 0)
				System.out.println();
		}
		
		res.setFeasibleSolution(true);
		res.setLoops(loops);
		res.setTimeRunning(TimerAndReporter.elapsed());
		
		// Limpa propriedades dos arcos
		for (Node node : nodes) {
			node.getProps().remove("djk.parentArc");
			node.getProps().remove("djk.dist");
			node.getProps().remove("djk.parent");
		}
		
		// Verifica se a solução é viável
		for (Node node : nodes) {
			for (Arc arc : node.getArcs()) {
				if ((arc.getHead().getId() == -1 || arc.getTail().getId() == -1) && ((Integer) arc.get("flow") > 0)) {
					res.setFeasibleSolution(false);
					return res;
				}
			}
		}
		
		res.setCostFlow(calcCostFlow());
		return res;
	}
	
	public void updateSSP(int cut) {
		// Atualiza a rede residual
		calculateResidualCapacities();
		
		// Atualiza os custos reduzidos
		for (Node node : nodes) {
			
			for (Arc resArc : node.getResidualArcs()) {
				// if((Integer)resArc.get("cap") < cut)
				// continue;
				
				resArc.set("reducedCost", (Integer) resArc.get("cost") - (Integer) resArc.getTail().get("ssp.p") + (Integer) resArc.getHead().get("ssp.p"));
			}
			
		}
		
		// printGraph();
		
	}
	
	public Response capacityScaling() {
		int maxEdgeCapacity = 0;
		int delta, flow;
		final Response response = new Response();
		Arc auxArc;
		Double deltaDouble;
		Double log2_10 = Math.log10(2);
		
		TimerAndReporter.start();
		// -------------
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
		
		// Encontra a aresta com a maior capacidade
		for (Node node : nodes) {
			if (node.getId() == -1)
				continue;
			for (Arc arc : node.getArcs()) {
				if (arc.getHead().getId() == -1)
					continue;
				maxEdgeCapacity = Math.max(maxEdgeCapacity, (Integer) arc.get("cap"));
			}
		}
		
		deltaDouble = Math.log10(maxEdgeCapacity) / log2_10;
		delta = deltaDouble.intValue();
		deltaDouble = Math.pow(2, delta);
		delta = deltaDouble.intValue();
		
		response.setTimePreparing(TimerAndReporter.elapsed());
		response.setLoops(0);
		
		// printGraph();
		
		while (delta >= 1) {
			// Red-code para concertar os curstos residuais negativos
			TimerAndReporter.start();
			for (Node node : nodes) {
				for (Arc resArc : node.getResidualArcs()) {
					if (((Integer) resArc.get("cap") >= delta) && ((Integer) resArc.get("reducedCost") < 0)) {
						flow = (Integer) resArc.get("cap");
						
						// Verifica se tem aresta no grafo com o mesmo sentido, senão coloca
						// fluxo na aresta inversa
						if (resArc.getProps().containsKey("arc")) {
							auxArc = (Arc) resArc.get("arc");
							auxArc.getProps().put("flow", (Integer) auxArc.get("flow") + flow);
						} else {
							auxArc = (Arc) resArc.get("arcInv");
							auxArc.getProps().put("flow", (Integer) auxArc.get("flow") - flow);
						}
						
						resArc.getTail().getProps().put("ssp.e", (Integer) resArc.getTail().get("ssp.e") - flow);
						resArc.getHead().getProps().put("ssp.e", (Integer) resArc.getHead().get("ssp.e") + flow);
					}
				}
			}
			
			calculateResidualCapacities();
			// printGraph();
			response.addTimeRunning(TimerAndReporter.elapsed());
			
			Response subRes = doSSP(delta);
			System.out.println("/");
			
			response.addTimePreparing(subRes.getTimePreparing());
			response.addTimeRunning(subRes.getTimeRunning());
			response.addLoops(subRes.getLoops());
			response.setCostFlow(subRes.getCostFlow());
			
			// printGraph();
			
			delta /= 2;
		}
		
		response.setFeasibleSolution(true);
		return response;
		
	}
	
	public void printGraph() {
		// Imprime o grafo
		System.out.println("Grafo----------------------");
		for (Node node : nodes) {
			System.out.println(node);
			for (Arc arc : node.getArcs())
				System.out.println(arc);
		}
		
		System.out.println("Grafo Residual ----------------------");
		for (Node node : nodes) {
			System.out.println(node);
			for (Arc resArc : node.getResidualArcs())
				System.out.println(resArc);
		}
	}
	
	public void dump(boolean nodes, boolean arcs, boolean residualArcs) {
		System.out.println("Dump do grafo nodes=" + nodes + ", arcs=" + arcs + ", residual=" + residualArcs);
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
	
	public void dispose() {
		for (Node n : nodes) {
			for (Arc c : n.getArcs())
				c.getProps().clear();
			for (Arc c : n.getResidualArcs())
				c.getProps().clear();
			n.getProps().clear();
			n.getArcs().clear();
			n.getResidualArcs().clear();
		}
		nodes.clear();
	}
	
	public void contractEdge(Arc arc) {
		Node tail = arc.getTail();
		Node head = arc.getHead();
		
		// Marca o nó da cabeça com removido
		head.deleteNode();
		
		// Atualiza o grau do tail
		int nArcs = arc.qtd;
		tail.incDegree(-nArcs);
		
		// Atualiza a quantidade de arcos do grafo
		qtdArcs -= 2 * nArcs;
		
		// Atualiza a quantidade de nós efetivos
		qtdNodes -= 1;
		
		// Apaga a aresta do Tail para o Head
		tail.deleteHashArc(head.getId());
		
		for (Map.Entry<Integer, Arc> neighbor : head.getHashArcs().entrySet()) {
			if (neighbor.getKey() != tail.getId()) { // Exclui o próprio nó para não
				// criar
				// arcos de laço
				// Verifica se já existe um arco entre os nós
				if (tail.getHashArcs().containsKey(neighbor.getKey())) {
					int qtdTail = tail.getHashArcs().get(neighbor.getKey()).qtd;
					int qtdHead = neighbor.getValue().qtd;
					
					// Adiciona os vizinhos do Head no Tail
					tail.getHashArcs().get(neighbor.getKey()).qtd = qtdTail + qtdHead;
					
					// Nos vizinhos do Head, troca as arestas que vão para o Head por
					// arestas para o Tail
					nodes.get(neighbor.getKey() - 1).getHashArc(tail.getId()).qtd = qtdTail + qtdHead;
					
					// Apaga aresta do vizinho do Head para o Head
					nodes.get(neighbor.getKey() - 1).deleteHashArc(head.getId());
					
					// Atualiza o grau dos nós
					tail.incDegree(qtdHead); // Acrescenta os arcos dos vizinhos do Head
					// para o Tail
				} else {
					int qtdHead = neighbor.getValue().qtd;
					
					// Cria arco do Tail para o vizinho do Head
					Arc newArcTail = new Arc(nodes.get(neighbor.getKey() - 1), nodes.get(tail.getId() - 1));
					newArcTail.qtd = qtdHead;
					tail.setHashArc(neighbor.getKey(), newArcTail);
					
					// Cria arco do vizinho do Head para o Tail
					Arc newArcNeighbor = new Arc(nodes.get(tail.getId() - 1), nodes.get(neighbor.getKey() - 1));
					newArcNeighbor.qtd = qtdHead;
					nodes.get(neighbor.getKey() - 1).setHashArc(tail.getId(), newArcNeighbor);
					
					// Apaga aresta do vizinho do Head para o Head
					nodes.get(neighbor.getKey() - 1).deleteHashArc(head.getId());
					
					// Atualiza o grau dos nós
					tail.incDegree(qtdHead); // Acrescenta os arcos dos vizinhos do Head
					// para o Tail
				}
			}
		}
	}
	
	@Override
	public Network clone() {
		
		return new Network(this);
		
		// Network net = new Network();
		//
		// net.qtdArcs = qtdArcs;
		//
		// // Cria todos os nós sem arcos no ArrayList
		// for (Node node : nodes) {
		// Node newNode = new Node(node.getId());
		// newNode.setDegree(node.getDegree());
		// net.getNodes().add(newNode);
		// }
		//
		// // Cria os arcos dos nós
		// for (Node node : nodes) {
		// // Adiciona os HashArcs
		// for (int neighbor : node.getHashArcs().keySet()) {
		// Arc arc = node.getHashArc(neighbor);
		// Arc newArc = new Arc(net.getNodes().get(arc.getHead().getId() - 1),
		// net.getNodes().get(arc.getTail().getId() - 1));
		// newArc.set("qtd", (int) arc.get("qtd"));
		// net.getNodes().get(node.getId() - 1).setHashArc(neighbor, newArc);
		// }
		// }
		//
		// return net;
	}
	
	public void makeDotFile(File f) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(new File(f.getAbsolutePath() + ".out"));
		
		out.println("Graph G{");
		out.println("overlap=scale;");
		out.println("splines=true;");
		out.println("node[label=\"\",chape=circle,width=0.1,height=0.1]");
		
		for (Node n : getNodes()) {
			for (Arc a : n.getHashArcs().values()) {
				if (a.getHead().getId() > a.getTail().getId()) {
					out.println("n" + a.getHead().getId() + " -- n" + a.getTail().getId() + ";");
				}
			}
		}
		
		out.println("}");
		
		out.flush();
		out.close();
		
	}
	
}
