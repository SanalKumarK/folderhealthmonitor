package com.target.security.foldermonitor.workers;

/**
 * File Copier, copies complete folder or selected files from source to destination directory
 */
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileCopier {

	private static final Logger logger = Logger.getLogger(FileCopier.class.getName());

	private File srcFile ;
	private File destFile;
	private boolean keepOriginal;
	
	/**
	 * 
	 * @param srcFile
	 * @param destFile
	 */
	public FileCopier(File srcFile, File destFile) {
		this.srcFile = srcFile;
		this.destFile = destFile;
	}
	
	/**
	 * Copies the file to the destination folder, 
	 * keepOriginal is false, delete the file from the source directory
	 *  
	 * @param srcFile
	 * @param destFile
	 * @param keepOriginal
	 */
	public FileCopier(File srcFile, File destFile, boolean keepOriginal) {
		this.srcFile = srcFile;
		this.destFile = destFile;
		this.keepOriginal = keepOriginal;
	}
	
	/**
	 * Copy the file from the source to the destination directory.
	 * If parent directories are missing, creates all the parent directory of copying file.
	 * If keepOriginal is false, delete the file from the source directory. 
	 */
	public void startMove() {
		try {
			
			if(!destFile.getParentFile().exists()) {				
				destFile.getParentFile().mkdirs();
				logger.log(Level.FINE,"Created parent directories of " + destFile.getPath());
			}
			
			if(!destFile.exists()) {				
				Files.copy(srcFile.toPath(), destFile.toPath());				
				logger.log(Level.FINE, "Copied file {0} to {1}." , new Object[]{srcFile.getPath() , destFile.getPath()});	
			}
			
			if(!keepOriginal) {
				if(srcFile.delete()) {
					logger.log(Level.FINE, "Deleted file {0} " , srcFile.getPath());
				} else {
					logger.log(Level.FINE, "Did not delete the file {0} " , srcFile.getPath());
				}				
			}
			
		} catch (IOException e) {
			logger.log(Level.FINE, "Copying Failed : " ,e);
		}
	}
}
