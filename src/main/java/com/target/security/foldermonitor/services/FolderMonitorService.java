package com.target.security.foldermonitor.services;

/**
 * FolderMonitorService schedules the worker copier report generator and 
 * observer classes.
 * 
 */
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.target.security.foldermonitor.model.FolderMonitorReport;
import com.target.security.foldermonitor.util.FolderMonitorConstants;
import com.target.security.foldermonitor.util.FolderMonitorUtil;
import com.target.security.foldermonitor.workers.FolderMonitorWorker;
import com.target.security.foldermonitor.workers.WorkerObserver;

public class FolderMonitorService {
	
	private static final Logger logger = Logger.getLogger(FolderMonitorService.class.getName());
	
	//Scheduled Thread Pool for scheduling the copier, monitor and observer classes
	private static ScheduledExecutorService scheduledThreadPool = 
			Executors.newScheduledThreadPool(FolderMonitorConstants.THREAD_POOL_SIZE);
	
	//Report of archiving activities which is shared with other services.
	private FolderMonitorReport report; 

	/**
	 * Start service by scheduling the copier, worker and listener service
	 */
	public static void startApp() {
		logger.log(Level.INFO, "Starting the Folder Monitor Service...");
		FolderMonitorService service = new FolderMonitorService();
		
		File secureDir = new File(FolderMonitorConstants.SECURE_DIR);
		
	    service.createSecureFolder();
	    
		service.report = new FolderMonitorReport();
		service.scheduleReportGenerator(secureDir, service.report);		
		service.scheduleFolderMonitorService(service.report);
		
		service.startSecureFolderObserver();
	}
	
	/**
	 * Schedule the report generator which runs on every 5mins, and 
	 * print the report. 
	 * 
	 * @param secureDir
	 * @param archiveDir
	 * @param report
	 */	
	private void scheduleReportGenerator(File secureDir, FolderMonitorReport report ) {	    
		FolderMonitorReportGenerator generator = new FolderMonitorReportGenerator(secureDir, report);
		//scheduledThreadPool.scheduleAtFixedRate(generator, 0, FolderMonitorConstants.REPORT_INTERVAL, TimeUnit.MINUTES);		
		scheduledThreadPool.scheduleAtFixedRate(generator, 0, 30, TimeUnit.SECONDS);
	}
	
	/**
	 * Create the secured folder.
	 */
	private void createSecureFolder() {
		File secureDir = new File(FolderMonitorConstants.SECURE_DIR);
		if(!secureDir.getParentFile().exists()) {				
			secureDir.getParentFile().mkdirs();
			logger.info("Created secured folder " + secureDir.getPath());
		}
		if(!secureDir.exists()) {
			secureDir.mkdir();
		}
	}

	/**
	 * Schedule worker copier. 
	 * WorkerCopier copies the files from the temp to the secured folder.
	 */
	private void scheduleFolderMonitorService(FolderMonitorReport report) {
		logger.log(Level.INFO, "Scheduling worker copier at {0} minutes", 
				FolderMonitorConstants.COPIER_INTERVAL);
		File tempDir = new File(FolderMonitorConstants.TEMP_DIR);
	    File secureDir = new File(FolderMonitorConstants.SECURE_DIR);
	    
	    FolderMonitorWorker workerService = new FolderMonitorWorker(tempDir, secureDir, report);
		scheduledThreadPool.scheduleAtFixedRate(workerService, 0, FolderMonitorConstants.COPIER_INTERVAL, TimeUnit.MINUTES);	    
	}
	
	/**
	 * Start the folder monitoring service, which listens for any activities in the folder. 
	 */
	private void startSecureFolderObserver() {		
		File obserDir = new File(FolderMonitorConstants.SECURE_DIR);
		
		if(!obserDir.exists()) {
			return;
		}
		
		WorkerObserver observer;
		try {
			//getUnwantedExtensions contains the unwanted file extensions to be removed from the folder.
			observer = new WorkerObserver(obserDir.toPath(), FolderMonitorUtil.getUnwantedExtensions());
			new Thread(observer,"Folder Observer").start();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Failed to start observer : ", e);
		}
	}	
	
	/**
	 * Stop service by shutting down the scheduler.
	 */
	public static void stopApp() {
		logger.log(Level.INFO, "Stopping the Folder Monitor Service...");
		scheduledThreadPool.shutdown();
	}
	
}
