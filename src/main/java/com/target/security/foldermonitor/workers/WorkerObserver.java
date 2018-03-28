package com.target.security.foldermonitor.workers;

/**
 * WorkerObserver listens for the folder events. 
 * Listens for any CREATE/MODIFY/DELETE operations on the parent folder,
 * and remove the unwanted files from the folder.
 * 
 *  Clients should pass the directory to observe and the extensions of the 
 *  files to be removed from the monitoring folder.
 */
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkerObserver implements Runnable{
	
	private static final Logger logger = Logger.getLogger(WorkerObserver.class.getName());	
	
	private final WatchService watcher;
	private final Map<WatchKey, Path> keys;
	private Path observDir;
	private HashSet<String> unwantedExts;
	
	/**
	 * 
	 * @param obserDir root Directory to monitor
	 * @param unwantedExts set of extensions to delete 
	 * @throws IOException
	 */
	public WorkerObserver(Path obserDir, HashSet<String> unwantedExts) throws IOException {
		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<>(); 
		this.observDir = obserDir;
		this.unwantedExts = unwantedExts;
	}
		
	/**
	 * Register the root and its child folders for the ADD/MODIFY/DELETE operations.
	 *  
	 * @param dir
	 * @throws IOException
	 */
	public void registerObserveDir(Path dir) throws IOException {
		Files.walk(dir).forEach(path -> {
			try {
				if(path.toFile().isDirectory()) {
					WatchKey key = path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
							StandardWatchEventKinds.ENTRY_DELETE,
							StandardWatchEventKinds.ENTRY_MODIFY);
					keys.put(key, path);	
				}
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Failed Observing dir: ", e);
			}
		});	
	}	
	
	
	@Override
	public void run() {
		try {
			registerObserveDir(observDir);
			processEvents();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Worker Observer stopped execution, following error occured : ", e);
		}
	}

	/**
	 * Listens for events, and when any file is added/modified,
	 * validates whether the file is executable or not.
	 * If executable file, delete from the folder.
	 */
	private void processEvents() {
		for(;;) {
			if(true) return;
			// wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }
            
            Path dir = keys.get(key);
            if (dir == null) {
                logger.log(Level.WARNING, "WatchKey not recognized!!");
                continue;
            }
            
            for (WatchEvent<?> event : key.pollEvents()) {
            	
                WatchEvent.Kind kind = event.kind();
 
                // Context for directory entry event is the file name of entry                
                Path name = ((WatchEvent<Path>)event).context();
                Path child = dir.resolve(name);
                
                // if directory is created, and watching recursively, then register it and its sub-directories
                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    try {
                    	if (child.toFile().isDirectory()) {
                            registerObserveDir(child);
                        } else {
                        	String extension = getExtension(name.toFile().getName());                        	
                        	if(unwantedExts.contains(extension)){
                        		logger.log(Level.INFO, "Deleting the executable file : {0}", child);
                        		Files.delete(child);
                        	}
                        }
                    } catch (IOException x) {
                    	logger.log(Level.SEVERE, "Could not register the listener for new directory {0}. ",child);
                    }
                }
                
                // reset key and remove from set if directory no longer accessible
                boolean valid = key.reset();
                if (!valid) {
                    keys.remove(key);
                    
                    // all directories are inaccessible
                    if (keys.isEmpty()) {
                        break;
                    }
                }
            }
		}
	}
	
	/**
	 * Find the extension from the file name.
	 * 
	 * @param filename
	 * @return
	 */
	private static String getExtension (String filename) {
		return filename.substring(filename.lastIndexOf('.')+1);
	}
}
