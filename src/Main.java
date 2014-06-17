import java.io.File;
import java.io.IOException;

public class Main {
	
	public static void main(String[] args) throws IOException {
		
		File netDirs = new File("netg");
		for (File file : netDirs.listFiles()) {
			if (!file.getName().endsWith(".net"))
				continue;
			
			playWithFile(file);
			
		}
		
	}
	
	private static final void playWithFile(File file) throws IOException {
		Network newNet = new Network();
		
		System.out.println("Arquivo " + file.getName() + " carregando... ");
		long timer = System.currentTimeMillis();
		
		newNet.loadFromFile(file);
		
		ContractAlg contract = new ContractAlg();
		System.out.println("Corte mínimo: " + contract.doContract(newNet));
		
/*		Arc arc = newNet.getNodes().get(1).getHashArc(1);
		newNet.contractEdge(arc);

		arc = newNet.getNodes().get(3).getHashArc(3);
		newNet.contractEdge(arc);
*/		
		
		
		
		timer = System.currentTimeMillis() - timer;
		System.out.println("Ok em " + timer + "ms.");
		
		timer = System.currentTimeMillis();
		

/*		
 		Response response = newNet.cycleCanceling(true);
		// Response response = newNet.sucessiveShortestPath();
		// Response response = newNet.capacityScaling();
		if (response.isFeasibleSolution())
			System.out.println("Custo do fluxo: " + response.getCostFlow());
		else
			System.out.println("Problema sem solução viável.");
*/
		
		
		
		timer = System.currentTimeMillis() - timer;
		
		System.out.println(newNet.getNodes().size() + " visitados em " + timer
		    + "ms.");
		
		// newNet.dump(true, false, false);
		
	}
	
}