import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class TimerAndReporter {
	
	private TimerAndReporter() {
	}
	
	public static boolean enabled = false;
	
	public static File reportFile = new File("report.csv");
	
	public static long startTime = 0l;
	
	public static long timeOut = 1000l * 60l * 5;
	
	public static void start() {
		startTime = System.currentTimeMillis();
	}
	
	public static void testTimeout() {
		if (enabled && (System.currentTimeMillis() - startTime > timeOut))
			throw new RuntimeException("Tempo excedido!");
	}
	
	public static long elapsed() {
		return System.currentTimeMillis() - startTime;
	}
	
	public static void initReport() {
		int idx = 1;
		while (reportFile.exists()) {
			idx += 1;
			reportFile = new File("report_" + idx + ".csv");
		}
	}
	
	public static void writeReport(String instanceName, String type,
	    String feasibleSolution, int executionIdx, int loops, long costFlow,
	    long timePreparing, long timeRunning) {
		
		String reportLine = instanceName + "," + type + "," + feasibleSolution
		    + "," + (executionIdx + 1) + "," + loops + "," + costFlow + ","
		    + timePreparing + "," + timeRunning;
		
		try {
			FileOutputStream fOut = new FileOutputStream(reportFile, true);
			try {
				PrintWriter w = new PrintWriter(fOut);
				w.println(reportLine);
				w.flush();
				w.close();
			} finally {
				fOut.flush();
				fOut.close();
			}
		} catch (IOException ioe) {
			throw new RuntimeException("Erro ao escrever arquivo de relatório!", ioe);
		}
		
	}
	
}
