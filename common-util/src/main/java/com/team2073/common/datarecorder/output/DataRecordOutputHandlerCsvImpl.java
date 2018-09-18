package com.team2073.common.datarecorder.output;

import com.team2073.common.datarecorder.model.DataRecordTable;
import com.team2073.common.datarecorder.model.RecordableRegistration;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

import static com.team2073.common.datarecorder.output.DataRecordOutputHandlerCsvImpl.CsvOutputHandlerState.*;

/**
 * @author pbriggs
 */
public class DataRecordOutputHandlerCsvImpl implements DataRecordOutputHandler {

    public enum CsvOutputHandlerState {
        NEW,
        INITIALIZING,
        INITIALIZED,
        INITIALIZATION_FAILED,
        SLEEPING,
        FLUSHING
    }

    public static final String DEFAULT_DELIMITTER = ",";
    public static final String DEFAULT_EOL = "\n";
    public static final String DEFAULT_OUT_SUB_DIR_NAME = "data-recorder";
    public static final String DEFAULT_OUT_FILE_DATE_PATTERN = "yyyy-MM-dd_HH.mm";
    public static final String DEFAULT_OUT_FILE_EXT = "csv";

    private Logger log = LoggerFactory.getLogger(getClass());

    // State
    private CsvOutputHandlerState state = NEW;

    // Configuration
    private String delimitter = DEFAULT_DELIMITTER;
    private String eol = DEFAULT_EOL;
    private File outDir;
    private String outSubDirName = DEFAULT_OUT_SUB_DIR_NAME;
    private File outFile;
    private String outFileDatePattern = DEFAULT_OUT_FILE_DATE_PATTERN;
    private String outFileExt = DEFAULT_OUT_FILE_EXT;

    @Override
    public void flushTable(RecordableRegistration registration, DataRecordTable table) {
        if (state == INITIALIZATION_FAILED)
            return;

        init();



    }

    private void init() {
        if (state == NEW)
            state = INITIALIZING;

        if (outDir == null) {
            outDir = FileUtils.getTempDirectory();
            outDir = new File(outDir, outSubDirName);
//            outFile = new File(outDir, )
        }
    }

    private void createOrOpenFile(RecordableRegistration registration) throws IOException {
        BufferedWriter bw;
        if (!outFile.exists()) {
            FileUtils.forceMkdirParent(outFile);
            bw = new BufferedWriter(new FileWriter(outFile));
        } else {
            bw = new BufferedWriter(new FileWriter(outFile, true));
        }
    }

    private void appendHeaders(RecordableRegistration registration, BufferedWriter bw) {
        String data = "timestamp" + registration.getFieldMappingList()
                .stream()
                .map(e -> e.getDataPointName())
                .collect(Collectors.joining(delimitter, delimitter, eol));
    }

    private String getFileName() {
        try {
            return new SimpleDateFormat(outFileDatePattern).format(new Date());
        } catch (IllegalArgumentException e) {
            log.warn("Error parsing filename date format [{}]. Using default pattern of [{}].",
                    outFileDatePattern, DEFAULT_OUT_FILE_DATE_PATTERN, e);
            return new SimpleDateFormat(DEFAULT_OUT_FILE_DATE_PATTERN).format(new Date());
        }
    }

}
