package logging;

import java.io.File;
import java.nio.file.DirectoryIteratorException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;
import com.google.common.io.Files;

import ch.qos.logback.core.net.SyslogOutputStream;

public class LogFileCleaner {
	private final static Logger logger = LoggerFactory.getLogger(LogFileCleaner.class);

	public static void deleteFilesOlderThan(String baseDirPath, int time, long maxStorageAllowedInKb) {
		File baseDir = new File(baseDirPath);
		long baseDirInitLength = getBaseDirSize(baseDir);
		List<File> allFilesOne = getAllFiles(baseDir);
		int numFilesAtStart = allFilesOne.size();
		sort(allFilesOne);
		deleteLogsTooOld(allFilesOne, time);
		ifNotEnoughRoomDelete(baseDir, allFilesOne, maxStorageAllowedInKb);
		printNumberFilesDeleted(numFilesAtStart, baseDir);
		printNumberKbDeleted(baseDirInitLength, baseDir);
	}

	private static List<File> getAllFiles(File theDir) {

		File[] listingAllFiles = theDir.listFiles();
		List<File> allFiles = new ArrayList<File>();

		if (theDir.exists()) {
			for (File subDir : listingAllFiles) {
				if (subDir.isFile()) {
					allFiles.add(subDir);
				} else {// If sub part of base is not file, but directory
					allFiles.addAll(getAllFiles(subDir));
				}
			}
			return allFiles;
		} else {
			logger.info("This directory [{}] does not exist.", theDir);
			return null;
		}
	}

	private static List<File> sort(List<File> list) {
		Collections.sort(list, new DateLastModifiedComparator());
		return list;
	}

	private static List<File> deleteLogsTooOld(List<File> list, int maxDays) {
		Iterator<File> iter = list.iterator();
		while (iter.hasNext()) {
			File f = iter.next();
			long lastModified = f.lastModified();
			Date dt = new Date(lastModified);
			long difference = System.currentTimeMillis() - dt.getTime();
			long time = maxDays * 24 * 60 * 60 * 1000;
			if (difference > time) {
				logger.info("Deleting [{}] because it's over the specified time threshold.", f.getName());
				iter.remove();
				f.delete();
			}
		}
		return list;
	}

	private static void ifNotEnoughRoomDelete(File directory, List<File> list, long maxSizeAllowed) {
		Iterator<File> iter = list.iterator();
		while (iter.hasNext()) {
			File f = iter.next();
			if (getBaseDirSize(directory) > maxSizeAllowed) {
				logger.info(
						"Deleting [{}] because there's not enough space in the base directory."
								+ " Size goes over [{}], and there's [{}] kb in dir now.",
						f.getName(), maxSizeAllowed, (getBaseDirSize(directory)));
				iter.remove();
				f.delete();
			}
		}
	}

	private static List<File> deleteNonLogs(List<File> list) {
		for (File file : list) {
			if (FilenameUtils.getExtension(file.getPath()) != ".log") {
				list.remove(file);
				file.delete();
			}
		}
		return list;
	}

	private static long getBaseDirSize(File folder) {
		long length = 0;
		File[] filesOfBaseDir = folder.listFiles();
		for (File f : filesOfBaseDir) {
			if (f.isFile()) {
				length += (f.length() / 1000);
			} else {
				length += getBaseDirSize(f);
			}
		}
		return length;
	}

	private static void printNumberFilesDeleted(int initNumberOfFiles, File currentBaseDir) {
		List<File> filesInBaseDirNow = new ArrayList<File>();
		for (File f : currentBaseDir.listFiles()) {
			filesInBaseDirNow.add(f);
		}
		int numFilesDeleted = initNumberOfFiles - filesInBaseDirNow.size();
		logger.info("[{}] files deleted.", numFilesDeleted);
	}

	private static void printNumberKbDeleted(long initBaseDirLength, File currentBaseDir) {
		long kbDeleted = initBaseDirLength - (getBaseDirSize(currentBaseDir));
		logger.info("[{}] kb deleted.", kbDeleted);
	}

}

class DateLastModifiedComparator implements Comparator<File> {

	@Override
	public int compare(File o1, File o2) {
		if (o1.lastModified() > o2.lastModified()) {
			return 1;
		}
		if (o2.lastModified() > o1.lastModified()) {
			return -1;
		} else {
			return 0;
		}
	}

}
