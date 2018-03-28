package com.target.security.foldermonitor.workers;

/**
 * Worker Copier is a worker thread, which copies an entire folder or 
 * only selected files from the parent folder.
 * 
 */
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.target.security.foldermonitor.util.FolderMonitorUtil;

public class WorkerCopier implements Runnable {
	
	private static final Logger logger = Logger.getLogger(WorkerCopier.class.getName());		
	
	private File srcDir ;
	private File targetDir ;
	//If flag is true, delete the original file from the source location.
	private boolean keepOriginal = true;
	//List of files to copy, from the source to destination
	private List<Path> filesToCopy = null;
	
	public WorkerCopier(File src, File target, boolean keepOriginal) {
		this.srcDir = src;
		this.targetDir = target;
		this.keepOriginal = keepOriginal;
	}
	
	public WorkerCopier(File src, File target, boolean keepOriginal, List<Path> filesToCopy) {
		this.srcDir = src;
		this.targetDir = target;
		this.keepOriginal = keepOriginal;
		this.filesToCopy = filesToCopy;
	}
	
	@Override
	public void run() {
		//If no filesToCopy list is passed, copy the entire folder
		if(filesToCopy == null) {
			copyFolder();
		} else { 		//Else copy files passed by filesTocCopy. 
			copySelectedFiles(filesToCopy);
		}
	}
	
	/**
	 * Copy the entire folder from src foder to the destination folder.
	 */
	public void copyFolder() {
		//Get the list of files		
		File[] files = FolderMonitorUtil.getFilesFromSrcFolder(srcDir);										
		if(files.length == 0) {
			logger.log(Level.INFO, "No Files to copy");
			return ;
		}
		
		if(!targetDir.exists()) {
			targetDir.mkdir();
		}
		
		String pathSeperator = "/";
		for (File file : files) {
			if(file.isDirectory()) {
				File tarDir = new File(targetDir.getAbsolutePath().concat(pathSeperator+ file.getName()));
				new Thread(new WorkerCopier(file, tarDir,keepOriginal)).start();
			} else {
				File outputFile = new File(targetDir.getAbsolutePath().concat( pathSeperator + file.getName()));
				new FileCopier(file, outputFile, keepOriginal).startMove();	
			}
		}
	}
	
	/**
	 * Copy the selected files from source to destination
	 * 
	 * @param filesToCopy
	 */
	public void copySelectedFiles(List<Path> filesToCopy) {
		//Get the list of files								
		if(filesToCopy.isEmpty()) {
			logger.log(Level.INFO, "No Files to copy");
			return ;
		}
		
		if(!targetDir.exists()) {
			targetDir.mkdir();
		}
		
		for (Path path : filesToCopy) {
			int beginIndex = srcDir.getAbsolutePath().length();
			String newFile = path.toFile().getAbsolutePath().substring(beginIndex);
			File outputFile = new File(targetDir.getAbsolutePath().concat(newFile));
			new FileCopier(path.toFile(), outputFile, keepOriginal).startMove();			
		}
	}
	        
}
