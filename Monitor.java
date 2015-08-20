import java.io.File;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.bind.DatatypeConverter;

public class Monitor {
	private Path monitoredFolder;
	
	private Map<String, String> cache;
	
	Monitor(ConfigHelper configHelper) throws Exception {
		this.monitoredFolder = configHelper.getMonitoredFolder();
		this.cache = Collections.synchronizedMap(new HashMap<String, String>());
		recordInintFolderStatus();
	}
	
	private void recordInintFolderStatus() throws Exception {
		System.out.println("INFO: recording initial folder status");
		File[] files = new File(monitoredFolder.toString()).listFiles();
		for ( File file : files ) {
		    if ( file.isFile() ) {
		        processFile(file.getName());
		    }
		}
	}
	
	public void start() throws Exception {
		FileSystem fileSystem = monitoredFolder.getFileSystem();
		try ( WatchService watchService = fileSystem.newWatchService() ) {
			monitoredFolder.register(watchService, 
					StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_MODIFY,
					StandardWatchEventKinds.ENTRY_DELETE);
			WatchKey key = null;
			while (true) {
				key = watchService.take();
				for ( WatchEvent<?> watchEvent : key.pollEvents() ) {
					if ( watchEvent.kind() == StandardWatchEventKinds.OVERFLOW ) {
						continue;
					} else if ( watchEvent.kind() == StandardWatchEventKinds.ENTRY_CREATE || watchEvent.kind() == StandardWatchEventKinds.ENTRY_MODIFY ) {
						Path file = ((WatchEvent<Path>) watchEvent).context();
						String fileName = file.getFileName().toString();
						Path fullPath = Paths.get(monitoredFolder.toString(), fileName);
						if ( !Files.isDirectory(fullPath) ) {
							processFile(file.getFileName().toString());
						} else {
							System.out.println("INFO: New folder entry. Skipping");
						}
					}
				}
				if ( !key.reset() ) {
					throw new Exception("Monitored folder is no longer valid");
				}
			}
		}
	}
	
	private void processFile(String fileName) throws Exception {
		Path pathToFile = Paths.get(monitoredFolder.toString(), fileName);
		String fileHash = getFileHash(pathToFile);
		if ( !cache.containsKey(fileHash) ) {
			System.out.println("INFO: New entry detected. Processing file: " + fileName);
			String fileInfo = getFileInfo(pathToFile);
			cache.put(fileHash, fileInfo);
		} else {
			System.out.println("INFO: Duplicate entry detected. Skipping file: " + fileName);
		}
	}
	
	private String getFileHash(Path pathToFile) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(Files.readAllBytes(pathToFile));
		byte[] hash = md.digest();
		String hashInHex = DatatypeConverter.printHexBinary(hash).toUpperCase();
		return hashInHex;
	}
	
	private String getFileInfo(Path pathToFile) throws Exception {
		StringBuilder result = new StringBuilder("");
		BasicFileAttributes attributes = Files.readAttributes(pathToFile, BasicFileAttributes.class);
		//TODO - resolve fileName for saving the structure of report if fileName contains ";" according to specification
		result.append(pathToFile.getFileName());
		result.append(";");
		result.append(pathToFile.toFile().length());
		result.append(" bytes");
		result.append(";");
		result.append(attributes.creationTime());
		result.append(";");
		result.append(getStringTimeStamp());
		return result.toString();
	}
	
	private String getStringTimeStamp() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(new Date());
	}
	
	public Map<String, String> getCache() {
		return cache;
	}
}
