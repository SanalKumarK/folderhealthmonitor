package com.target.security.foldermonitor.workers;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.target.security.foldermonitor.model.FolderMonitorReport;
import com.target.security.foldermonitor.util.FolderMonitorConstants;
import com.target.security.foldermonitor.util.FolderMonitorUtil;

public class FolderMonitorWorker implements Runnable {
	
	private static final Logger logger = Logger.getLogger(FolderMonitorWorker.class.getName());

	private File srcDir ;
	private File targetDir ;
	private FolderMonitorReport report;
	
	private String pathSeperator = "/";
	
	public FolderMonitorWorker(File src, File target, FolderMonitorReport report) {
		this.srcDir = src;
		this.targetDir = target;
		this.report = report;
	}
	
	@Override
	public void run() {
		// Get the list of files
		long targetFileSize = FolderMonitorUtil.getFileSizeOfDir(targetDir.toPath());
		
		//Get the new files list, and get the total required space.
		File[] srcDirfiles = FolderMonitorUtil.getFilesFromSrcFolder(srcDir);
		long totalRequiredSpace = 0;		
		
		ArrayList<Path> newFilesToMove = new ArrayList<>();
		boolean copyFile;
		long tempSize; 
		for (File file : srcDirfiles) {
			copyFile = false;
			if(file.isFile()) {
				File tarFile = new File(targetDir.getAbsolutePath().concat(pathSeperator).concat(file.getName()));
				if(!tarFile.exists()) {
					copyFile = true;					
				} else {
					if(file.lastModified() > tarFile.lastModified()) {
						copyFile = true;						
					}
				}
				tempSize = totalRequiredSpace + file.length();
				if(copyFile && ( tempSize <= FolderMonitorConstants.MAX_FOLDER_SIZE)) {
					totalRequiredSpace = tempSize;
					newFilesToMove.add(file.toPath());
				}
			}
		}
		
		if(!newFilesToMove.isEmpty()) {
			long emptySpaceAvailable = FolderMonitorConstants.MAX_FOLDER_SIZE - targetFileSize;			
			if(totalRequiredSpace > emptySpaceAvailable) {
				long archiveSpace = totalRequiredSpace - emptySpaceAvailable;
				//Archive files for the required space
				startArchiver(archiveSpace);	
			}
			startWorkerCopier(srcDir, targetDir, false, newFilesToMove);
		}
	}
	
	/**
	 * Schedule the worker copier on every interval. 
	 * Worker Copier copies the files from the temp to the secured folder.
	 */
	private void startWorkerCopier(File srcDir, File destDir, boolean keepOriginal,  List<Path> filesToCopy) {
		logger.log(Level.INFO, "Starting worker copier.");
		
		WorkerCopier workerCopier = new WorkerCopier(srcDir, destDir, keepOriginal, filesToCopy);
	    Thread copier = new Thread(workerCopier, "Worker-Copier");	    
	    copier.start();
	}
	
	/**
	 * Schedule the worker archiver on every interval.
	 * Archiver copies the files from the secured folder and keep in the archived folder.
	 */
	private void startArchiver(long reqSize) {	
	    WorkerArchiver workerarchiver = new WorkerArchiver(reqSize, report);
	    workerarchiver.run();
	}

}
