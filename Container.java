public class Container {
	private static final String CONFIG_FILE_NAME = "config.properties";
	
	private ConfigHelper configHelper;
	private Monitor monitor;
	private Reporter reporter;
	
	Container() throws Exception {
		this.configHelper = new ConfigHelper(CONFIG_FILE_NAME);
		this.monitor = new Monitor(configHelper);
		this.reporter = new Reporter(monitor, configHelper);
	}
	
	public void startApplication() throws Exception {
		try {
			System.out.println("INFO: Starting monitor");
			reporter.start();
			monitor.start();
		} catch (Exception ex) {
			reporter.interrupt();
			System.out.println("ERROR: error occured during runtime. Application is shut down. Press enter to proceed");
			System.in.read();
		}
	}
}
