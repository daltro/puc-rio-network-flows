import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.concurrent.LinkedBlockingQueue;

public class TimerAndReporter {
	
	public static final class ReportBean{
		public String instance;
		public int nodeCount;
		public int arcCount;
		public String algorithm;
		public int execution;
		public int iteraction;
		public int iteractionMinCut;
		public int executionMinCut;
		public long iteractionTimeMillis;
		public long executionTimeAcumulated;
	}
	
	public static File reportFile = new File("report.csv");
	
	private static final LinkedBlockingQueue<ReportBean> writeQueue	= new LinkedBlockingQueue<>();
	
	public static final void writeReport(ReportBean bean){
		ReportBean toWrite = new ReportBean();
		toWrite.instance = bean.instance;
		toWrite.nodeCount = bean.nodeCount;
		toWrite.arcCount = bean.arcCount;
		toWrite.algorithm = bean.algorithm;
		toWrite.execution = bean.execution;
		toWrite.iteraction = bean.iteraction;
		toWrite.iteractionMinCut = bean.iteractionMinCut;
		toWrite.executionMinCut = bean.executionMinCut;
		toWrite.iteractionTimeMillis = bean.iteractionTimeMillis;
		toWrite.executionTimeAcumulated = bean.executionTimeAcumulated;
		writeQueue.add(toWrite);
	}
	
	private static final Thread writerThread = new Thread(){
		@Override
		public void run() {
			try{
				while(!isInterrupted()){
					
					ReportBean bean = writeQueue.take();
					
					FileOutputStream fOut = new FileOutputStream(reportFile, true);
					try {
						PrintWriter w = new PrintWriter(fOut);
						w.println(
								bean.instance+","+
										bean.nodeCount+","+
										bean.arcCount+","+
										bean.algorithm+","+
										bean.execution+","+
										bean.iteraction+","+
										bean.iteractionMinCut+","+
										bean.executionMinCut+","+
										bean.iteractionTimeMillis+","+
										bean.executionTimeAcumulated
								);
						w.flush();
						w.close();
					} finally {
						fOut.flush();
						fOut.close();
					}
					
				}
			}catch(Throwable e){
				e.printStackTrace(System.err);
				Runtime.getRuntime().exit(1);
			}
		}
	};
	static{
		writerThread.setDaemon(true);
		writerThread.setPriority(Thread.MIN_PRIORITY);
		writerThread.start();
	}
	
	private TimerAndReporter() {
	}
	
	public static boolean enabled = false;
	
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
	
	public static void initReport() throws FileNotFoundException {
		int idx = 1;
		while (reportFile.exists()) {
			idx += 1;
			reportFile = new File("report_" + idx + ".csv");
		}
		PrintWriter headerWriter = new PrintWriter(reportFile);
		headerWriter.println(
				"instance,"+
						"nodeCount,"+
						"arcCount,"+
						"algorithm,"+
						"execution,"+
						"iteraction,"+
						"iteractionMinCut,"+
						"executionMinCut,"+
						"iteractionTimeMillis,"+
						"executionTimeAcumulated"
				);
		headerWriter.close();
		System.out.println("Relatório em "+reportFile.getAbsolutePath());
	}
	
}
