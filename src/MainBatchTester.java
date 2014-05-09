import java.io.File;
import java.io.IOException;

public class MainBatchTester {
	
	private enum TestType {
		/**
		 * Cycle Cancelling - Ford Fulkerson
		 */
		CC_F,
		
		/**
		 * Cycle Cancelling - Edmonds Karp
		 */
		CC_E,
		
		/**
		 * Sucessive Shortest Paths
		 */
		SSP,
		
		/**
		 * Sucessive Shortest Paths, com Capacity Scalling
		 */
		SSP_CS
	}
	
	public static void main(String[] args) throws IOException,
	    InterruptedException {
		
		File netDirs = new File("netg");
		
		TimerAndReporter.enabled = true;
		// TimerAndReporter.timeOut = 1000l * 3;
		TimerAndReporter.initReport();
		
		// Número de vezes que o programa irá repetir o teste igualmente para cada
		// arquivo
		// (para tirar média depois)
		final int numOfExecutions = 5;
		
		for (int execution = 0; execution < numOfExecutions; execution += 1) {
			for (File file : netDirs.listFiles()) {
				if (!file.getName().endsWith(".net"))
					continue;
				
				runTestsWith(file, execution);
				
			}
		}
		
	}
	
	private static void runTestsWith(File file, int execution)
	    throws IOException, InterruptedException {
		
		for (TestType type : TestType.values()) {
			runOneTest(file, type, execution);
		}
	}
	
	private static boolean runOneTest(File file, final TestType type,
	    int executionIdx) throws IOException, InterruptedException {
		
		String instanceName = file.getName().substring(0,
		    file.getName().length() - 4);
		
		TimerAndReporter.start();
		
		System.out.println("\n\n" + instanceName + " com " + type + " - teste #"
		    + executionIdx);
		System.out.println("-------------------------------------------------");
		
		// Carregar arquivo
		final Network net = new Network();
		System.out.print("* Carregando " + file.getAbsolutePath() + "...");
		net.loadFromFile(file);
		System.out.println("ok em " + TimerAndReporter.elapsed() + "ms.");
		
		Response result;
		boolean methodResult;
		
		System.out.println("* Chamando algoritmo de " + type + "...");
		try {
			switch (type) {
				case CC_F:
					result = net.cycleCanceling(false);
					break;
				case CC_E:
					result = net.cycleCanceling(true);
					break;
				case SSP:
					result = net.sucessiveShortestPath();
					break;
				case SSP_CS:
					result = net.capacityScaling();
					break;
				default:
					throw new IllegalStateException();
			}
			methodResult = true;
		} catch (Throwable e) {
			result = new Response();
			result.setFeasibleSolution(true);
			methodResult = false;
			System.out.println("* !! Thread interrompida por exceção: "
			    + e.getClass().getName() + " - " + e.getMessage());
		}
		
		if (!methodResult || result == null) {
			result = new Response();
			result.setFeasibleSolution(true);
			methodResult = false;
		}
		
		TimerAndReporter.writeReport(instanceName, type.toString(),
		    Boolean.toString(result.isFeasibleSolution()), executionIdx,
		    result.getLoops(), result.getCostFlow(), result.getTimePreparing(),
		    result.getTimeRunning());
		
		System.out
		    .print("* Rodando Garbage Collector para evitar interferências no tempo...");
		Runtime.getRuntime().gc();
		System.out.println("Ok!");
		
		// Retorna false se a execução extourou o tempo máximo.
		return methodResult;
		
	}
}
