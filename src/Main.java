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
		

/*		newNet.reCreateResidualNetwork();
		Path resPath = newNet.findPathResidual(newNet.getNodes()[0], newNet.getNodes()[5]);

		System.out.println("Path: " + resPath);
		resPath.invertPath();
		System.out.println("InvPath: " + resPath);
		System.out.println("Capacidade minima: " + resPath.getBottleneck());
*/

/*		System.out.println("Grafo--------------");
		for(Node node : newNet.getNodes()){
			System.out.println(node);
			for(Arc Arc : node.getArcs())
				System.out.println(Arc);
		}

		System.out.println("Rede Residual--------------");
		for(Node node : newNet.getNodes()){
			System.out.println(node);
			for(ResidualArc resArc : node.getResidualArcs())
				System.out.println(resArc);
		}
*/
		
		Response response = newNet.cycleCanceling();
		if(response.isFeasibleSolution())
			System.out.println("Custo do fluxo: " + response.getCostFlow());
		else
			System.out.println("Problema sem solução viável.");

/*		
		System.out.println("--------Verificação Final----------");

		System.out.println("Grafo--------------");
		for(Node node : newNet.getNodes()){
			System.out.println(node);
			for(Arc Arc : node.getArcs())
				System.out.println(Arc);
		}

		System.out.println("Rede Residual--------------");
		for(Node node : newNet.getNodes()){
			System.out.println(node);
			for(ResidualArc resArc : node.getResidualArcs())
				System.out.println(resArc);
		}
		
		System.out.println("------------------");
*/
		
		timer = System.currentTimeMillis() - timer;
		System.out.println(newNet.getNodes().size()+" visitados em "+timer+"ms.");

	}
	
}