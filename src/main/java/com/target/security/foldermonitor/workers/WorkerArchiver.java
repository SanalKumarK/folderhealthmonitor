package com.target.security.foldermonitor.workers;

/**
 * WorkerArchiver archives the old files from the folder.
 */
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.target.security.foldermonitor.model.FolderMonitorReport;
import com.target.security.foldermonitor.util.FolderMonitorConstants;
import com.target.security.foldermonitor.util.FolderMonitorUtil;

public class WorkerArchiver {
	
	private static final Logger logger = Logger.getLogger(WorkerArchiver.class.getName());	
	
	private long archiveSize = 0;
	private FolderMonitorReport report;
	
	/**
	 * 
	 * @param size size required to be archived from the folder.
	 * @param report archive report holder.
	 */
	public WorkerArchiver(long size, FolderMonitorReport report) {
		archiveSize = size;
		this.report = report;
	}
	
	public void run() {
		logger.log(Level.INFO, "Archiving started...");
		File secureDir = new File(FolderMonitorConstants.SECURE_DIR);
		File archiveDir = new File(FolderMonitorConstants.ARCHIVE_DIR);
		//get the old files list to archive 
		List<Path> filesToArchive = getFilesToArchive(secureDir.toPath(), archiveSize);
		report.addArchivedFiles(filesToArchive);
		if(!filesToArchive.isEmpty()) {
			new Thread(new WorkerCopier(secureDir, archiveDir, false, filesToArchive)).start();	
		}       
	}
	
	/**
	 * Returns the list of files, to be moved from the source folder. 
	 * Sort the files list based on the modified time in ascending order. 
	 * Select the files to be archived by adding the old files till archive size is acquired. 
	 *   
	 * @param secureFolderPath source folder to perform archive operation
	 * @return list of files to be archived
	 */
	private List<Path> getFilesToArchive(Path secureFolderPath, long archiveSize) {
		//Sort the list of files in the secureFolderPath, on last modified time descending order 
		List<Path> orderedFiles = FolderMonitorUtil.getSortedFilesOnModifiedDate(secureFolderPath);
		long reqSize = 0;
		long fileSize = 0;
		List<Path> filesToBeArchived = new ArrayList<>();
		//Loop through each file and add the files till archive size achieved.		
		for (Path file : orderedFiles) {
			fileSize = file.toFile().length();
			reqSize += fileSize;
			filesToBeArchived.add(file);
			if(reqSize > archiveSize) {
				break;
			}
		}
		return filesToBeArchived;
	}
}
