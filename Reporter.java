import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Map;

public class Reporter extends Thread {
	private Monitor monitor;
	private ConfigHelper configHelper;
	
	private long reportInterval;
	
	public Reporter(Monitor monitor, ConfigHelper configHelper) {
		this.monitor = monitor;
		this.configHelper = configHelper;
		this.reportInterval = configHelper.getReportInterval() * 1000;
	}
	
	public void run() {
		try {
			while (true) {
				System.out.println("INFO: Generating report");
				generateReport();
				Thread.sleep(reportInterval);
			}
		} catch (Exception ex) {
			System.out.println("ERROR: couldn't generate report");
		}
	}
	
	private void generateReport() throws Exception {
		Path reportPath = configHelper.getReportFileName();
		PrintWriter writer = new PrintWriter(reportPath.toFile(), "UTF-8");
		Map<String, String> data = monitor.getCache();
		for (String entry : data.keySet() ) {
			writer.println(data.get(entry));
		}
		if ( data.size() == 0 ) {
			writer.println("No entries");
		}
		writer.close();
		System.out.println("INFO: report generated");
	}	
}
