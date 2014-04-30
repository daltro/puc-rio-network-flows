import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Network {
	
	private Node[] nodes = new Node[0];
	
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
								nodes[i] = new Node(i+1);
							}
						}
					}
					
					// Definição de uma aresta
					// a <v> <w> <low> <cap> <cost>
					String split[] = line.split("\\s+");
					Arc newArc = new Arc(
							nodes[Integer.parseInt(split[1])-1],
							nodes[Integer.parseInt(split[2])-1]);
					
					newArc.getHead().getLinks().add(newArc);
					
					newArc.getProps().put("low", Integer.parseInt(split[3]));
					newArc.getProps().put("cap", Integer.parseInt(split[4]));
					newArc.getProps().put("cost", Integer.parseInt(split[5]));
				}
				
			}
			
		}
		finally{
			fin.close();
		}
		
	}

	public Node[] getNodes() {
		return nodes;
	}
	
}
