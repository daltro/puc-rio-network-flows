import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ArticulationPoint {
//	int verticesNumber; // Num. de vertices
//	List<Integer>[] adj; // array de lista de adjac�ncias

	static int time = 0;
//
//	
//	public ArticulationPoint(int V) {
//		this.verticesNumber = V;
//		this.adj = new ArrayList[V];
//
//		for (int i = 0; i < V; i++) {
//			adj[i] = new ArrayList<Integer>();
//		}
//	}
//
//	public ArticulationPoint(int V, List<Integer>[] adjacencias) {
//		this.verticesNumber = V;
//		this.adj = adjacencias;
//
//		for (int i = 0; i < V; i++) {
//			adj[i] = new ArrayList<Integer>();
//		}
//	}
//	
//	public void addEdge(int v, int w) {
//		adj[v].add(w);
//		adj[w].add(v); // O grafo eh nao direcionado
//	}

	// Fun��o recursiva que encontra pontos de articula��o usando DFS traversal
	// u - O proximo vertice a ser visitado
	// visited[] - mantem relacao de vertices visitados
	// disc[] - armazena o tempo de descoberta dos vertices visitados
	// parent[] - armazena os (vertices) pais na DFS tree
	// ap[] --> armazena pontos de articula��o
	public void findArticulationPointHelp(Node node) {

		// Conta os filhos na DFS Tree
		int children = 0;

		// Marca o n� atual como visitado
		node.setVisited(true);

		// Inicializa o tempo de descoberta e a vari�vel low
		time++;
		node.setMinPreForAP(time);
		node.setPre(time);

		// Percorre toda a lista de adjac�ncias do vertice
		for (Node child : node.getLinks()) {
			// Se v n�o foi visitado ainda, ent�o marque-o como filho de u
			// na DFS tree 
			if (!child.isVisited()) {
				children++;
				child.setParent(node);
				
				findArticulationPointHelp(child);

				// Verifique se a sub�rvore com raiz em v tem uma conex�o para um dos ancestrais de u
				node.setMinPreForAP(Math.min(node.getMinPreForAP(), child.getMinPreForAP()));

				// u eh um ponto de articula��o nos seguintes casos

				// (1) Se u eh raiz da arvore DFS e tem dois ou mais filhos.
				if (node.getParent()==null && children > 1)
					node.setAp(true);

				// (2) Se u nao eh raiz e o valor de low de um de seus filhos eh 
				// maior que o valor de descoberta de u
				if (node.getParent() != null && child.getMinPreForAP() >= node.getPre())
					node.setAp(true);
			}

			// Atualiza valor de low de u .
			else if (node.getParent()!=child)
				node.setMinPreForAP(Math.min(node.getMinPreForAP(), child.getPre()));
		}
	}

	// Executa a DFS traversal. Usa a fun��o recursiva findArticulationPointHelp()
	public void findArticulationPoint(Collection<Node> nodes) {

		// Chama a funcao auxiliar recursiva para enocntrar pontos de articula��o na 
		// DFS tree cuja raiz eh o vertice i
		for (Node node : nodes)
			if (!node.isVisited())
				findArticulationPointHelp(node);

		// imprimir os pontos de articula��o em ap[]
		for (Node node : nodes)
			if (node.isAp())
				System.out.println(node);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println("Pontos de articula��o no grafo 1:");
//		ArticulationPoint graphAP1 = new ArticulationPoint(5);
//		graphAP1.addEdge(1, 0);
//		graphAP1.addEdge(0, 2);
//		graphAP1.addEdge(2, 1);
//		graphAP1.addEdge(0, 3);
//		graphAP1.addEdge(3, 4);
//		graphAP1.findArticulationPoint();
//
//		System.out.println("\nPontos de articula��o no grafo 2:");
//		ArticulationPoint graphAP2 = new ArticulationPoint(4);
//		graphAP2.addEdge(0, 1);
//		graphAP2.addEdge(1, 2);
//		graphAP2.addEdge(2, 3);
//		graphAP2.findArticulationPoint();
//
//		System.out.println("\nPontos de articula��o no grafo 3:");
//		ArticulationPoint graphAP3 = new ArticulationPoint(7);
//		graphAP3.addEdge(0, 1);
//		graphAP3.addEdge(1, 2);
//		graphAP3.addEdge(2, 0);
//		graphAP3.addEdge(1, 3);
//		graphAP3.addEdge(1, 4);
//		graphAP3.addEdge(1, 6);
//		graphAP3.addEdge(3, 5);
//		graphAP3.addEdge(4, 5);
//		graphAP3.findArticulationPoint();
		GameGraph game = new GameGraph();
		game.buildGraph();
		new ArticulationPoint().findArticulationPoint(game.getNodes().values());
	}
}
