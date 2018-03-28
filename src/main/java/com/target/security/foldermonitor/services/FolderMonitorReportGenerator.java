package com.target.security.foldermonitor.services;

/**
 * Report Generator class, get the folder metrics and updates the report on every interval.
 * Print the report in the log.
 */
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.target.security.foldermonitor.model.FolderMonitorReport;
import com.target.security.foldermonitor.util.FolderMonitorUtil;

public class FolderMonitorReportGenerator implements Runnable {
	
	private static final Logger logger = Logger.getLogger(FolderMonitorService.class.getName());
	
	private FolderMonitorReport report;
	private File secDir;
	
	public FolderMonitorReportGenerator(File secDir, FolderMonitorReport report) {		
		this.secDir = secDir;
		this.report = report;
	}
	
	@Override
	public void run() {
		if(report != null && secDir != null) {			
			long folderSize = FolderMonitorUtil.getFileSizeOfDir(secDir.toPath());
			report.setCurrentSize(folderSize );			
			logger.log(Level.INFO, "{0}{1}", new Object[]{System.getProperty("line.separator"), report});
			report.clearArchivedFiles();
		}
	}
}
