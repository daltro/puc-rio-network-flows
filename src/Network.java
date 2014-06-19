import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

public class Network {
	
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
	
	private void calculateResidualCapacities() {
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
					newArc.getProps().put("cap", 1);
					newArc.getProps().put("flow", 0);
					Arc newArcHead = new Arc(nodes.get(Integer.parseInt(split[1]) - 1), nodes.get(Integer.parseInt(split[2]) - 1));
					newArcHead.getProps().put("cap", 1);
					newArcHead.getProps().put("flow", 0);
					
					if (newArc.getTail().getHashArcs().containsKey(newArc.getHead().getId())) {
						newArc.qtd = 2;
						newArcHead.qtd = 2;
					} else {
						newArc.qtd = 1;
						newArcHead.qtd = 1;
					}
					
					newArc.getTail().getArcs().add(newArc);
					newArc.getHead().getArcs().add(newArcHead);
					
					newArc.getTail().setHashArc(newArc.getHead().getId(), newArc);
					newArc.getHead().setHashArc(newArc.getTail().getId(), newArcHead);
					
					newArc.getHead().incDegree(1);
					newArc.getTail().incDegree(1);
					
				}
				
			}
			
			createResidualNetwork();
			
		} finally {
			fin.close();
		}
		
	}
	
	private void createResidualNetwork() {
		
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
				
				// Adiciona o arco na rede residual no sentido inverso
				Arc newResArcInv = new Arc(arc.getTail(), arc.getHead());
				newResArcInv.getTail().getResidualArcs().add(newResArcInv);
				
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
	
	private Path findPathResidualBFS(Node nodeStart, Node nodeEnd, int cut) {
		clearNetworkBFS();
		BFSNetworkRes bfs = new BFSNetworkRes();
		Path resPath = bfs.findPath(nodeStart, nodeEnd, cut);
		return resPath;
	}
	
	private void clearNetworkBFS() {
		// Limpa as marcações da DFS no grafo
		for (Node node : nodes) {
			node.getProps().remove("bfs.P");
			node.getProps().remove("bfs.V");
		}
	}
	
	private void updateFlow(Path path, int flow) {
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
	
	public int doDeterministicGlobalMinCut(TimerAndReporter.ReportBean reportBean){
		
		int minCut = Integer.MAX_VALUE;
		
		Node s = nodes.get(0);
		
		long executionStart = System.nanoTime();
		
		for (int i=1; i<nodes.size(); i+=1){
			Node t = nodes.get(i);
			
			int thisCut = edmondsKarp(s, t);
			
			minCut = Math.min(thisCut, minCut);
		}
		
		if (reportBean!=null){
			long now = System.nanoTime();
			reportBean.iteraction = 0;
			reportBean.iteractionMinCut = minCut;
			reportBean.executionMinCut = minCut;
			reportBean.executionTimeAcumulated = now - executionStart;
			reportBean.iteractionTimeMillis = reportBean.executionTimeAcumulated;
			TimerAndReporter.writeReport(reportBean);
		}
		
		return minCut;
		
	}
	
	public int edmondsKarp(Node s, Node t) {
		int valueMaxFlow = 0;
		
		// "Zera" o grafo
		for (Node n : nodes){
			for (Arc a : n.getArcs())
				a.getProps().put("flow", 0);
		}
		calculateResidualCapacities();
		
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
