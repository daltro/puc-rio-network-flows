import java.io.File;
import java.io.IOException;
import java.util.Arrays;


public class Main {

	public static void main(String[] args) throws IOException {
		
		
		//Teste
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
		
		System.out.print(" * Rodando uma DFS completa... ");
		timer = System.currentTimeMillis();
		final long visitCount[] = new long[]{0};
		DFS.doDFS(Arrays.asList(newNet.getNodes()), new NodeVisitor() {
			@Override
			public void visit(Node node) {
				visitCount[0]+=1;
			}
		});
		timer = System.currentTimeMillis() - timer;
		System.out.println(visitCount[0]+"/"+newNet.getNodes().length+" visitados em "+timer+"ms.");
	}
	
}
