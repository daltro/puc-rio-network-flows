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
		newNet.makeDotFile(file);
		ContractAlg contract = new ContractAlg();
		System.out.println("Rodando algoritmos em " + file.getName() + "...");
		System.out.println("Corte mínimo(contract): " + contract.doContract(newNet) + " em " + (System.currentTimeMillis() - timer) + "ms");
		timer = System.currentTimeMillis();
		System.out.println("Corte mínimo(fastCut): " + contract.doFastCut(newNet) + " em " + (System.currentTimeMillis() - timer) + "ms");
		
		/*
		 * Arc arc = newNet.getNodes().get(1).getHashArc(1);
		 * newNet.contractEdge(arc);
		 * 
		 * arc = newNet.getNodes().get(3).getHashArc(3); newNet.contractEdge(arc);
		 */
		
		// newNet.dump(true, false, false);
		
	}
	
}