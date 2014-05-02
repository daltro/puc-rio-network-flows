import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;


public class Main {

	public static void main(String[] args) throws IOException {
		
		File netDirs = new File("netg");
		for (File file : netDirs.listFiles()){
			if (!file.getName().endsWith(".net"))
				continue;
			
			playWithFile(file);
			
		}
		
	}
	
	
	private static final void playWithFile(File file) throws IOException{
		Network newNet = new Network();
		
		System.out.print("Arquivo "+file.getName()+" carregando... ");
		long timer = System.currentTimeMillis();
		
		newNet.loadFromFile(file);
		
		timer = System.currentTimeMillis() - timer;
		System.out.println("Ok em "+timer+"ms.");
		
		System.out.print(" * Rodando uma Cycles completa... ");
		timer = System.currentTimeMillis();
//		final long visitCount[] = new long[]{0};
//		DFS.doDFS(Arrays.asList(newNet.getNodes()), new NodeVisitor() {
//			@Override
//			public void visit(Node node) {
//				visitCount[0]+=1;
//				System.out.println("Visitei "+visitCount[0]+": "+node);
//			}
//		});
		

		newNet.reCreateResidualNetwork();
		Path resPath = newNet.findPathResidual(newNet.getNodes()[0], newNet.getNodes()[5]);
		if(resPath != null){
			System.out.println("Path: " + resPath);
			resPath.invertPath();
			System.out.println("InvPath: " + resPath);
			System.out.println("Capacidade minima: " + resPath.getBottleneck());
		}
			
/*		
		LinkedList<Arc> cycle = BelmanFord.findNegativeCycles(newNet);

		System.out.println("------------------");
		for (Node n : newNet.getNodes()){
			System.out.println(n);
		}
		System.out.println("------------------");

		System.out.println(cycle);
		
		timer = System.currentTimeMillis() - timer;
		System.out.println(newNet.getNodes().length+" visitados em "+timer+"ms.");
*/		
	}
	
}
