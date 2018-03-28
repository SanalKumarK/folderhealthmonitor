package com.target.security.foldermonitor.util;
import java.io.File;
/**
 * Utility class for Folder Monitor Health
 */
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FolderMonitorUtil {
	
	private static final Logger logger = Logger.getLogger(FolderMonitorUtil.class.getName());	
	
	private FolderMonitorUtil() {
		
	}
	
	/**
	 * Returns the size of the folder.
	 * 
	 * @param folder
	 * @return
	 */
	public static long getFileSizeOfDir (Path folder) {
		long size = 0;
		try {
			size = Files.walk(folder)
				      .filter(p -> p.toFile().isFile())
				      .mapToLong(p -> p.toFile().length())
				      .sum();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Failed to find the size of the folder: ", e);
		}
		return size;
	}	

	/**
	 * Returns the list of files from the folder.
	 * 
	 * @param tempFolder
	 * @return
	 */
	public static File[] getFilesFromSrcFolder(File tempFolder) {
		if(tempFolder.exists() && tempFolder.isDirectory()) {
			return tempFolder.listFiles();
		}
		return new File[0];
	}
	

	/**
	 * Configure the unwanted extensions to remove from the secure folder
	 * Add/Remove the string from the below set, to filter the files.
	 * @return
	 */
	public static HashSet<String> getUnwantedExtensions() {
		HashSet<String> unwantedExts = new HashSet<>();
		unwantedExts.add("bat");
		unwantedExts.add("sh");
		unwantedExts.add("exe");
		return unwantedExts;
	}
	

	
	/**
	 * Sort the files based on the modified date in descending order. 
	 * 
	 * @param secureFolderPath
	 * @return
	 */
	public static List<Path> getSortedFilesOnModifiedDate(Path secureFolderPath) {
		List<Path> orderedFiles = null;
		try {
			orderedFiles = Files.walk(secureFolderPath)
					.filter(p -> p.toFile().isFile())
				.sorted((o1, o2)-> {
							if(o1.toFile().lastModified() > o2.toFile().lastModified()) {
								return -1;
							} else if(o1.toFile().lastModified() == o2.toFile().lastModified()) {
								return 0;
							} else {
								return 1;
							}
				})
				.collect(Collectors.toList());
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Failed to get sorted files : ", e);
		}
		return orderedFiles;
	}
}
