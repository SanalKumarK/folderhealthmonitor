package com.target.security.foldermonitor.model;

/**
 * Folder Monitor Report DO
 */
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.target.security.foldermonitor.util.FolderMonitorConstants;

public class FolderMonitorReport {
	
	//folder size.
	private long currentSize;
	
	//list of archived files.
	private HashSet<Path> archivedFiles = new HashSet<>(); 
	
	public long getCurrentSize() {
		return currentSize;
	}
	public void setCurrentSize(long currentSize) {
		this.currentSize = currentSize;
	}
	
	public void addArchivedFiles(List<Path> archivedFiles) {		
		this.archivedFiles.addAll(archivedFiles);
	}
	
	public Set<Path> getArchivedFiles() {
		return archivedFiles;
	}
	
	public void clearArchivedFiles() {
		currentSize = 0;
		archivedFiles.clear();
	}
	
	/**
	 * toString is customized to format the report details.
	 */
	@Override
	public String toString() {
		String lineSeperator = System.getProperty("line.separator");
		StringBuilder buffer = new StringBuilder();
		buffer.append("Secured Folder Path : ").append(FolderMonitorConstants.SECURE_DIR).append(lineSeperator);
		buffer.append("Total Size : ").append(currentSize).append(lineSeperator);
		buffer.append("Archived Files Count : ").append(archivedFiles.size()).append(lineSeperator);
		
		if(!archivedFiles.isEmpty()) {
			buffer.append("Archived Files : ").append(lineSeperator);
			
			for (Path file : archivedFiles) {
				buffer.append(file.getFileName()).append(lineSeperator);
			}	
		}
		
		return buffer.toString();
	}
}