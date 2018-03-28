package com.target.security.foldermonitor.util;

public class FolderMonitorConstants {
	
	private FolderMonitorConstants() {
	}
	
	public static final long REPORT_INTERVAL = 5;
	public static final long COPIER_INTERVAL = 2;	
	public static final long MAX_FOLDER_SIZE = 104857600;  //100 MB
	public static final String TEMP_DIR = "c:/temp";
	public static final String SECURE_DIR = "c:/secured";
	public static final String ARCHIVE_DIR = "c:/archive";
	public static final int THREAD_POOL_SIZE = 5;
}