import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainBatchTester {
	
	public static void main(String[] args) throws IOException,
	InterruptedException {
		
		File netDirs = new File("netg");
		
		ArrayList<File> files = new ArrayList<File>();
		for (File f : netDirs.listFiles()){
			if (!f.getName().endsWith(".net"))
				continue;
			files.add(f);
		}
		Collections.sort(files, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				return Long.compare(o1.length(), o2.length());
			}
		});
		
		TimerAndReporter.enabled = true;
		TimerAndReporter.start();
		// TimerAndReporter.timeOut = 1000l * 3;
		TimerAndReporter.initReport();
		
		// Número de vezes que o programa irá repetir o teste igualmente para cada
		// arquivo
		// (para tirar média depois)
		final int numOfExecutions = 5;
		
		TimerAndReporter.ReportBean reportBean = new TimerAndReporter.ReportBean();
		
		for (int execution = 0; execution < numOfExecutions; execution += 1) {
			for (int fIdx = 0; fIdx<files.size(); fIdx+=1) {
				File file = files.get(fIdx);
				String progress = " ("+(fIdx+1)+"/"+files.size()+", execução "+(execution+1)+")";
				
				reportBean.instance = file.getName().substring(0,file.getName().length()-4);
				reportBean.execution = execution;
				
				Network newNet = new Network();
				System.out.print("Arquivo " + file.getName() + progress + " carregando... ");
				newNet.loadFromFile(file);
				System.out.println("ok.");
				
				reportBean.nodeCount = newNet.qtdNodes;
				reportBean.arcCount = newNet.qtdArcs;
				
				ContractAlg contract = new ContractAlg(reportBean);
				System.out.println("  * Corte mínimo(deterministic)...");
				try{
					TimerAndReporter.start();
					reportBean.algorithm = "deterministic";
					newNet.doDeterministicGlobalMinCut(reportBean);
				}catch(Throwable e){}
				
				System.out.println("  * Corte mínimo(contract)...");
				try{
					TimerAndReporter.start();
					reportBean.algorithm = "contract";
					contract.doContract(newNet);
				}catch(Throwable e){}
				
				System.out.println("  * Corte mínimo(fastCut)...");
				try{
					TimerAndReporter.start();
					reportBean.algorithm = "fastCut";
					contract.doFastCut(newNet);
				}catch(Throwable e){}
				
				
			}
		}
		
	}
	
}
