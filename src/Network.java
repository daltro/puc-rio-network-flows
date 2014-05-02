import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Network {
	
	private Node[] nodes = new Node[0];
	
	public void reCreateResidualNetwork(){

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

		
/*		for(Node node : nodes){
			System.out.println(node);
			for(ResidualArc resArc : node.getResidualArcs()){
				System.out.println(resArc);
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
			
			String line;
			while ((line = in.readLine())!=null){
				if (line.startsWith("c "))
					continue; // comentário
				
				if (line.startsWith("p min ")){
					// p min <nodes> <arcs>: definicão do problema
					String split[] = line.split("\\s+");
					nodes = new Node[Integer.parseInt(split[2])];
				}
				
				else if (line.startsWith("n ")){
					// Definição de um nó
					// n <id> <flow>
					String split[] = line.split("\\s+");
					Node newNode = new Node(Integer.parseInt(split[1]));
					nodes[newNode.getId()-1] = newNode;
					newNode.getProps().put("flow", Integer.parseInt(split[2]));
				}
				
				else if (line.startsWith("a ")){
					
					if (!nodesEnded){
						nodesEnded = true;
						for (int i=0; i<nodes.length; i+=1){
							if (nodes[i]==null){
								Node newNode = new Node(i+1);
								nodes[i] = newNode;
								newNode.getProps().put("flow", 0);
							}
						}
					}
					
					// Definição de uma aresta
					// a <v> <w> <low> <cap> <cost>
					String split[] = line.split("\\s+");
					Arc newArc = new Arc(
							nodes[Integer.parseInt(split[2])-1],
							nodes[Integer.parseInt(split[1])-1]);
					
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
			
			createResidualNetwork();
						
		}
		finally{
			fin.close();
		}
		
	}
	
	public void createResidualNetwork(){
		Arc arcInv;
		
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
					newResArc.getProps().put("cost", arc.getProps().get("cost"));					
					
					//Adiciona o arco na rede residual no sentido inverso
					ResidualArc newResArcInv = new ResidualArc(arc.getTail(), arc.getHead());
					newResArcInv.getTail().getResidualArcs().add(newResArcInv);
					newResArcInv.getProps().put("cost", -(Integer)arc.getProps().get("cost"));
					
					//Faz a ligação dos arcos do grafo com os arcos da rede residual
					arc.getProps().put("res.arc", newResArc);
					arc.getProps().put("res.arcInv", newResArcInv);

				}else{ //Se tem arco inverso e ele já foi visitado, só cria as ligações entre os arcos do grafo e da rede residual 
					arc.getProps().put("res.arc", arcInv.getProps().get("res.arcInv"));
					arc.getProps().put("res.arcInv", arcInv.getProps().get("res.arc"));
				}
			}			
		}
		
	}

	public Path findPathResidual(Node nodeStart, Node nodeEnd) {
		DFSNetworkRes dfs = new DFSNetworkRes();
		Path resPath = dfs.findPath(nodeStart, nodeEnd);
		return resPath;	
	}
	
	public Node[] getNodes() {
		return nodes;
	}
	
}
