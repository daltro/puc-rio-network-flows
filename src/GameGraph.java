import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class GameGraph {
	
	private final HashMap<Integer, Node> nodes;
	HashMap<Integer, Integer> nodeCounts;
	
	public GameGraph() {
		int size=9*8*7*6*5*4*3*2*2;
		nodes = new HashMap<>(size);
	}
	
	public void buildGraph(){
		
		byte config[] = new byte[9];
		buildGraph(config, 0);
		
	}
	
	public HashMap<Integer, Integer> getNodeCounts() {
		return nodeCounts;
	}

	public void setNodeCounts(HashMap<Integer, Integer> nodeCounts) {
		this.nodeCounts = nodeCounts;
	}

	public HashMap<Integer, Node> getNodes() {
		return nodes;
	}

	private void buildGraph(byte[] config, int pos) {
		if (pos==9){
			int nodeId = Node.getId(config);
			Node theNode = nodes.get(nodeId);
			if (theNode==null){
				theNode = new Node(config);
				nodes.put(nodeId, theNode);
			}
			computeLinks(theNode);
			return;
		}
		
		digitLoop: for (byte i=0; i<9; i+=1){
			for (int j=0; j<pos; j+=1)
				if (config[j]==i)
					continue digitLoop;
			config[pos]=i;
			buildGraph(config, pos+1);
		}
	}
	
	private void computeLinks(Node theNode) {
		for (byte[] destConfig : getTransitions(theNode.getConfig())){
			int destNodeId = Node.getId(destConfig);
			Node destNode = nodes.get(destNodeId);
			if (destNode==null){
				destNode = new Node(destConfig);
				nodes.put(destNodeId, destNode);
				theNode.getLinks().add(destNode);
			}
			if (!destNode.getLinks().contains(theNode))
				destNode.getLinks().add(theNode);
		}
	}
	
	public static final byte[][] getTransitions(byte[] config){
		int zeroPos = 0;
		for (int i=0; i<9; i+=1){
			if (config[i]==0){
				zeroPos=i;
				break;
			}
		}
		switch(zeroPos){
		case 0:
			return new byte[][]{
					getSwapped(config, 0, 1),
					getSwapped(config, 0, 3)
			};
		case 1:
			return new byte[][]{
					getSwapped(config, 1, 0),
					getSwapped(config, 1, 2),
					getSwapped(config, 1, 4)
			};
		case 2:
			return new byte[][]{
					getSwapped(config, 2, 1),
					getSwapped(config, 2, 5)
			};
		case 3:
			return new byte[][]{
					getSwapped(config, 3, 0),
					getSwapped(config, 3, 4),
					getSwapped(config, 3, 6)
			};
		case 4:
			return new byte[][]{
					getSwapped(config, 4, 3),
					getSwapped(config, 4, 1),
					getSwapped(config, 4, 5),
					getSwapped(config, 4, 7)
			};
		case 5:
			return new byte[][]{
					getSwapped(config, 5, 2),
					getSwapped(config, 5, 4),
					getSwapped(config, 5, 8)
			};
		case 6:
			return new byte[][]{
					getSwapped(config, 6, 7),
					getSwapped(config, 6, 3)
			};
		case 7:
			return new byte[][]{
					getSwapped(config, 7, 6),
					getSwapped(config, 7, 4),
					getSwapped(config, 7, 8)
			};
		case 8:
			return new byte[][]{
					getSwapped(config, 8, 7),
					getSwapped(config, 8, 5)
			};
		default:
			throw new IllegalStateException("posição inválida!");
		}
	}
	
	private static final byte[] getSwapped(byte[] originalConfig, int p1, int p2){
		byte res[] = new byte[9];
		System.arraycopy(originalConfig, 0, res, 0, 9);
		res[p1] = originalConfig[p2];
		res[p2] = originalConfig[p1];
		return res;
	}
	
	public static byte[] getConfig(String numbers){
		byte res[] = new byte[9];
		for (int i=0; i<9; i+=1)
			res[i] = (byte)(numbers.charAt(i)-'0');
		return res;
	}
	
	public static void main(String[] args) {
		
		long timer = System.currentTimeMillis();
		GameGraph g = new GameGraph();
		g.buildGraph();
		System.out.println("Tempo de montagem do grafo: "+(System.currentTimeMillis()-timer));
		
		g.countNodesEdges();
		g.findLongestSolution();
	}
	
	private static void link (Node n1, Node n2){
		n1.getLinks().add(n2);
		n2.getLinks().add(n1);
	}

  private void buildDummyGraph01() {
    Node nos[] = new Node[5];
    for (int i=0; i<5; i+=1){
      byte[] c = new byte[9];
      Arrays.fill(c, (byte)i);
      nos[i] = new Node(c);
    }
    
    link(nos[0], nos[1]);
    link(nos[0], nos[2]);
    link(nos[0], nos[3]);
    link(nos[3], nos[4]);
    link(nos[1], nos[2]);
    
    for (Node no : nos){
      nodes.put(Node.getId(no.getConfig()), no);
      System.out.println("Nó: "+no);
    }
  }

  private void buildDummyGraph03() {
    Node nos[] = new Node[8];
    for (int i=0; i<8; i+=1){
      byte[] c = new byte[9];
      Arrays.fill(c, (byte)i);
      nos[i] = new Node(c);
    }
    
    link(nos[0], nos[1]);
    link(nos[1], nos[2]);
    link(nos[2], nos[3]);
    link(nos[2], nos[4]);
    link(nos[2], nos[5]);
    link(nos[5], nos[6]);
    link(nos[6], nos[7]);
    
    link(nos[1], nos[3]);
    link(nos[4], nos[0]);
    link(nos[5], nos[7]);
    
    for (Node no : nos){
      nodes.put(Node.getId(no.getConfig()), no);
      System.out.println("Nó: "+no);
    }

  }
  
	private void buildDummyGraph02() {
		Node nos[] = new Node[4];
		for (int i=0; i<4; i+=1){
			byte[] c = new byte[9];
			Arrays.fill(c, (byte)i);
			nos[i] = new Node(c);
		}
		
		link(nos[0], nos[1]);
		link(nos[1], nos[2]);
		link(nos[2], nos[3]);
		link(nos[3], nos[0]);

		for (Node no : nos){
			nodes.put(Node.getId(no.getConfig()), no);
			System.out.println("Nó: "+no);
		}
	}

  public void findLongestSolution(){
    
    for (Node no : nodes.values()){
      no.setAp(false);
      no.setComponent(-1);
      no.setMinPreForAP(-1);
      no.setParent(null);
      no.setPre(-1);
      no.setVisited(false);
    }
    
    long timer = System.currentTimeMillis();
    System.out.println("\n\nEncontrando solução mais longa....");
    
    Node solutionConfig = nodes.get(Node.getId(new byte[]{1,2,3,4,5,6,7,8,0}));
    
    final Node longest[] = new Node[]{null};
    final int longestDist[] = new int[]{0};
    BFS2.doBFS(solutionConfig, new NodeVisitor() {
      @Override
      public void visit(Node node) {
        if (node.getPre()>longestDist[0]){
          longest[0] = node;
          longestDist[0] = node.getPre();
        }
      }
    });
    
    LinkedList<Node> longests = new LinkedList<Node>();
    for (Node node : nodes.values()){
    	if (node.getPre()==longestDist[0]){
    		longests.add(node);
    		System.out.println("Configuração loonge: "+node.toString());
    	}
    }

    System.out.println("Tempo para a montagem da BFS: "+(System.currentTimeMillis()-timer));
    
    System.out.println("Total de nós com caminho máximo para a solução: "+longests.size());
    
    Node solver = longest[0];
    int step = 1;
    int k=0;
    while (solver!=null){
//      System.out.println("Passo "+(step<10?"0":"")+step+": "+getConfigLine(solver, 0));
//      System.out.println("        : "+getConfigLine(solver, 1));
//      System.out.println("        : "+getConfigLine(solver, 2));
//      System.out.println("---------------");
    	System.out.println("\\begin{small}"+step+":\\end{small}");
        System.out.println("\\begin{tabular}{|c|c|c|}");
        System.out.println("\\hline");
        System.out.println((solver.getConfig()[0]==0?" ":solver.getConfig()[0]) + " & "
        		+(solver.getConfig()[1]==0?" ":solver.getConfig()[1]) + " & "
        		+(solver.getConfig()[2]==0?" ":solver.getConfig()[2]) + " \\\\");
        System.out.println("\\hline");
        System.out.println((solver.getConfig()[3]==0?" ":solver.getConfig()[3]) + " & "
        		+(solver.getConfig()[4]==0?" ":solver.getConfig()[4]) + " & "
        		+(solver.getConfig()[5]==0?" ":solver.getConfig()[5]) + " \\\\");
        System.out.println("\\hline");
        System.out.println((solver.getConfig()[6]==0?" ":solver.getConfig()[6]) + " & "
        		+(solver.getConfig()[7]==0?" ":solver.getConfig()[7]) + " & "
        		+(solver.getConfig()[8]==0?" ":solver.getConfig()[8]) + " \\\\");
        System.out.println("\\hline");
        System.out.println("\\end{tabular}");
        if (++k==4){
        	System.out.println("\\\\[1cm]");
        	k=0;
        }
        else
        	System.out.println("&");
      step+=1;
      solver = solver.getParent();
    }
    
  }
  
  private static final String getConfigLine(Node node, int line){
    int pos[];
    switch (line){
      case 0: pos=new int[]{0,1,2}; break;
      case 1: pos=new int[]{3,4,5}; break;
      case 2: pos=new int[]{6,7,8}; break;
      default: throw new IllegalStateException();
    }
    return "["+(node.getConfig()[pos[0]]==0?" ":node.getConfig()[pos[0]])
      +(node.getConfig()[pos[1]]==0?" ":node.getConfig()[pos[1]])
      +(node.getConfig()[pos[2]]==0?" ":node.getConfig()[pos[2]])+"]";
  }
  
	public void countNodesEdges(){
	  
    for (Node no : nodes.values()){
      no.setAp(false);
      no.setComponent(-1);
      no.setMinPreForAP(-1);
      no.setParent(null);
      no.setPre(-1);
      no.setVisited(false);
    }
    
    long timer = System.currentTimeMillis();
    
    // Fazer uma DFS para contar os nós das componentes conexas.
    DFS.doDFS(nodes.values(), new NodeVisitor() {
      @Override
      public void visit(Node node) {}
    });
    
		nodeCounts = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> edgeCounts = new HashMap<Integer, Integer>();
		for (Node node : this.nodes.values()){
			Integer componentID = node.getComponent();
			// Contar nós por componente conexa:
			Integer nodeCount = nodeCounts.get(componentID);
			if (nodeCount==null)
				nodeCounts.put(componentID, 1);
			else
				nodeCounts.put(componentID, nodeCount+1);
			
			// Contar arestas (dobrado, depois devo dividir por 2):
			Integer edgeCount = edgeCounts.get(componentID);
			if (edgeCount==null)
				edgeCounts.put(componentID, 2);
			else
				edgeCounts.put(componentID, edgeCount+node.getLinks().size());
		}
		
		for (int componentId = 0; componentId<nodeCounts.size(); componentId+=1){
			System.out.println("Componente conexa "+componentId+": "
			  +nodeCounts.get(componentId)+" nós e "+(edgeCounts.get(componentId)/2)+" arestas.");
		}
	
		System.out.println("Tempo para cálculo das componentes conexas e pontos de articulação: "+(System.currentTimeMillis()-timer));
	
    System.out.println("\n\nPontos de articulação:");
		int apCount=0;
		for (Node node : nodes.values()){
			if(node.isAp()){
				System.out.println("Ponto de articulação: "+node);
				apCount+=1;
			}
		}
		System.out.println("Pronto. Pontos de articulação: "+apCount);
	}
	
}
