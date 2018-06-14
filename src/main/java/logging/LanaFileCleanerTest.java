package logging;

import logging.LogFileCleaner;

public class LanaFileCleanerTest {

	public static void main (String [] args) {
		LogFileCleaner.deleteFilesOlderThan("C:\\Users\\Lana\\Documents\\Delete-TestingFileCode", 14, 10);
	}

}
