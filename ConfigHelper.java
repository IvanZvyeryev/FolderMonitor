import java.util.Properties;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigHelper {
	private Path monitoredFolder;
	private Path reportFileName;
	private int reportInterval;
	
    ConfigHelper(String configFileName) throws Exception {
    	Properties config =loadConfig(configFileName);
    	validateMonitoredFolder(config);
    	setMonitoredFolder(config);
    	setReportFileName(config);
    	setReportInterval(config);
    }
    
    private Properties loadConfig(String configFileName) throws Exception {
        FileInputStream fis = new FileInputStream(configFileName);
        Properties config = new Properties();
        config.load(fis);
        fis.close();
        return config;
    }

    private void validateMonitoredFolder(Properties config) throws Exception {
        String monitoredFolder = config.getProperty("monitoredFolder");
        if ( monitoredFolder == null ) {
        	throw new Exception("Monitored folder is missing from the config. Application has stopped. Press enter to proceed");
        }
    }

	public void setMonitoredFolder(Properties config) {
		String stringPath = config.getProperty("monitoredFolder");
		stringPath = stringPath.replaceAll("\"", "");
		Path path = Paths.get(stringPath);
		this.monitoredFolder = path;
	}
	
	public void setReportFileName(Properties config) {
		String stringPath = config.getProperty("reportFileName");
		stringPath = stringPath.replaceAll("\"", "");
		Path path = Paths.get(stringPath);
		this.reportFileName = path;
	}
	
	public void setReportInterval(Properties config) {
		String stringReportInterval = config.getProperty("reportInterval");
		stringReportInterval = stringReportInterval.replaceAll("\"", "");
		int reportInterval = Integer.parseInt(stringReportInterval);
		this.reportInterval = reportInterval;
	} 
	
	public Path getMonitoredFolder() {
		return monitoredFolder;
	}
	
	public Path getReportFileName() {
		return reportFileName;
	}

	public int getReportInterval() {
		return reportInterval;
	}
}