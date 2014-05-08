import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class MainBatchTester {
	
	private enum TestType {
		/**
		 * Cycle Cancelling
		 */
		CC,
		
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
		
		// Número de vezes que o programa irá repetir o teste igualmente para cada
		// arquivo
		// (para tirar média depois)
		final int numOfExecutions = 2;
		
		// Formato do relatório:
		// nome_arquivo,algoritmo,há solução viável?,índice da execução,custo do
		// fluxo global,tempo de execução (ms).
		File reportFile = new File("report.csv");
		
		PrintWriter reportWriter = new PrintWriter(reportFile);
		try {
			for (File file : netDirs.listFiles()) {
				if (!file.getName().endsWith(".net"))
					continue;
				
				runTestsWith(file, reportWriter, numOfExecutions);
				
			}
		} finally {
			reportWriter.close();
		}
	}
	
	private static void runTestsWith(File file, PrintWriter report,
	    int numOfExecutions) throws IOException, InterruptedException {
		
		for (TestType type : TestType.values()) {
			for (int execution = 0; execution < numOfExecutions; execution += 1) {
				if (!runOneTest(file, report, type, execution))
					break;
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private static boolean runOneTest(File file, final PrintWriter report,
	    final TestType type, int executionIdx) throws IOException,
	    InterruptedException {
		
		String instanceName = file.getName().substring(0,
		    file.getName().length() - 4);
		
		long timer;
		
		System.out.println("\n\n" + instanceName + " com " + type + " - teste #"
		    + executionIdx);
		System.out.println("-------------------------------------------------");
		
		// Carregar arquivo
		final Network net = new Network();
		System.out.print("* Carregando " + file.getAbsolutePath() + "...");
		timer = System.currentTimeMillis();
		net.loadFromFile(file);
		System.out.println("ok em " + (System.currentTimeMillis() - timer) + "ms.");
		
		final Response result[] = new Response[1];
		final long[] time = new long[1];
		System.out.println("* Chamando algoritmo de " + type + "...");
		Thread runThread = new Thread() {
			@Override
			public void run() {
				time[0] = System.currentTimeMillis();
				switch (type) {
					case CC:
						result[0] = net.cycleCanceling();
						break;
					case SSP:
						result[0] = net.sucessiveShortestPath();
						break;
					case SSP_CS:
						result[0] = net.capacityScaling();
						break;
					default:
						throw new IllegalStateException();
				}
				time[0] = System.currentTimeMillis() - time[0];
			}
		};
		runThread.setPriority(Thread.MAX_PRIORITY);
		runThread.setDaemon(true);
		runThread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				System.out.println("* !! Thread interrompida por exceção: "+e.getClass().getName()+" - "+e.getMessage());
			}
		});
		
		final long timeout = 1000l * 60000l * 5l;
		//final long timeout = 5000l;
		
		runThread.start();
		runThread.join(timeout); // Esperar no máximo 5 minutos.
		
		boolean methodResult = !runThread.isAlive();
		
		if (runThread.isAlive()) {
			runThread.interrupt();
			runThread.join(5000);
			if (runThread.isAlive())
				runThread.stop(new RuntimeException("Tempo limite excedido!"));
		}
		
		if (!methodResult || result[0]==null) {
			time[0] = 0;
			result[0] = new Response(0);
		}
		
		String reportLine = instanceName + "," + type + ","
		    + result[0].isFeasibleSolution() + "," + (executionIdx + 1) + ","
		    + result[0].getCostFlow() + "," + time[0];
		System.out.println("* Linha CSV para relatório: " + reportLine);
		report.println(reportLine);
		
		System.out
		    .print("* Rodando Garbage Collector para evitar interferências no tempo...");
		Runtime.getRuntime().gc();
		System.out.println("Ok!");
		
		// Retorna false se a execução extourou o tempo máximo.
		return methodResult;
		
	}
}
