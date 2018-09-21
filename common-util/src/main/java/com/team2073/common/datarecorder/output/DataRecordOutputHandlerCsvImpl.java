package com.team2073.common.datarecorder.output;

import com.team2073.common.assertion.Assert;
import com.team2073.common.datarecorder.model.DataRecordRow;
import com.team2073.common.datarecorder.model.DataRecordTable;
import com.team2073.common.datarecorder.model.Recordable;
import com.team2073.common.datarecorder.model.RecordableRegistration;
import com.team2073.common.util.Throw;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.team2073.common.datarecorder.output.DataRecordOutputHandlerCsvImpl.CsvOutputHandlerState.*;
import static com.team2073.common.util.ClassUtil.*;

/**
 * @author pbriggs
 */
public class DataRecordOutputHandlerCsvImpl implements DataRecordOutputHandler {

    private static final String DEFAULT_DELIMITTER = ",";
    private static final String DEFAULT_EOL = "\n";
    private static final String DEFAULT_OUT_SUB_DIR_NAME = "data-recorder";
    private static final String DEFAULT_OUT_FILE_DATE_PATTERN = "yyyy-MM-dd_HH.mm";
    private static final String DEFAULT_OUT_FILE_EXT = "csv";

    public enum CsvOutputHandlerState {
        NEW,
        INITIALIZING,
        INITIALIZED,
        INITIALIZATION_FAILED,
        SLEEPING,
        FLUSHING,
        FLUSHING_FAILED
    }

    private Logger log = LoggerFactory.getLogger(getClass());

    // DataRecorderState
    private CsvOutputHandlerState state = NEW;
    private boolean enabled = true;
    private String timestampString;

    // Configuration
    private String delimitter = DEFAULT_DELIMITTER;
    private String eol = DEFAULT_EOL;
    private File outDir;
    private File timestampedOutputDir;
    private Map<Recordable, File> outFileMap = new HashMap<>();
    private String outFileDatePattern = DEFAULT_OUT_FILE_DATE_PATTERN;
    private String outFileExt = DEFAULT_OUT_FILE_EXT;

    @Override
    public void init() {
        if (state == NEW)
            state = INITIALIZING;


        if (outDir == null) {
            outDir = FileUtils.getTempDirectory();
            outDir = new File(outDir, DEFAULT_OUT_SUB_DIR_NAME);
        }

        if (timestampedOutputDir == null) {
            timestampedOutputDir = new File(outDir, resolveDirectoryName());
        }

        if (!timestampedOutputDir.exists()) {
            try {
                FileUtils.forceMkdir(timestampedOutputDir);
            } catch (IOException e) {
                state = INITIALIZATION_FAILED;
                log.warn("Failed to create output directory. Disabling future output. Directory: [{}].",
                        timestampedOutputDir.getAbsolutePath(), e);
                return;
            }
        }

        if (state == INITIALIZING)
            state = INITIALIZED;
    }

    private String resolveDirectoryName() {
        if (timestampString == null) {
            try {
                timestampString = new SimpleDateFormat(outFileDatePattern).format(new Date());
            } catch (IllegalArgumentException e) {
                log.warn("Error parsing filename date format [{}]. Using default pattern of [{}].",
                        outFileDatePattern, DEFAULT_OUT_FILE_DATE_PATTERN, e);
                timestampString = new SimpleDateFormat(DEFAULT_OUT_FILE_DATE_PATTERN).format(new Date());
            }
        }
        return timestampString;
    }

    @Override
    public void flushTable(RecordableRegistration registration, DataRecordTable table) {
        if (disabledOrFailed())
            return;

        state = FLUSHING;
        File outputFile = getOrInitRecordableFile(registration.getInstance());
        try (BufferedWriter bw = createOrOpenFile(outputFile, registration)) {
            appendData(registration, table, bw);
        } catch (IOException e) {
            state = FLUSHING_FAILED;
            log.warn("Error flushing data to file [%s]. Disabling future output.", filePath(registration), e);
        }

        state = SLEEPING;
    }

    private File getOrInitRecordableFile(Recordable recordable) {
        // Check if we already have a file for this Recordable
        File outputFile = outFileMap.get(recordable);

        if (outputFile == null) {
            outputFile = new File(timestampedOutputDir, simpleName(recordable) + "." + outFileExt).getAbsoluteFile();
            outFileMap.put(recordable, outputFile);
        }

        return outputFile;
    }

    private BufferedWriter createOrOpenFile(File outputFile, RecordableRegistration registration) throws IOException {
        BufferedWriter bw;
        if (!outputFile.exists()) {
            FileUtils.forceMkdirParent(outputFile);
            bw = new BufferedWriter(new FileWriter(outputFile));
            appendHeaders(registration, bw);
            log.info("Created output file [{}].", filePath(registration));
        } else {
            bw = new BufferedWriter(new FileWriter(outputFile, true));
        }
        return bw;
    }

    private void appendHeaders(RecordableRegistration registration, BufferedWriter bw) throws IOException {
        String headers = "timestamp" + registration.getFieldMappingList()
                .stream()
                .map(e -> e.getDataPointName())
                .collect(Collectors.joining(delimitter, delimitter, eol));
        try {
            bw.write(headers);
        } catch (IOException e) {
            Throw.ioEx("Error writing headers. File: [%s]. Headers: [%s].", filePath(registration), headers, e);
        }
    }

    private void appendData(RecordableRegistration registration, DataRecordTable table, BufferedWriter bw) throws IOException {
        if (table.entrySet().isEmpty()) {
            log.warn("Requested to write data but no data to write. Recordable: [{}].", simpleName(registration));
            return;
        }

        String data = "";
        for (Entry<LocalDateTime, DataRecordRow> row : table.entrySet()) {
            data += row.getKey().toEpochSecond(ZoneOffset.UTC) + delimitter;
            data = row.getValue()
                    .stream()
                    .map(e -> e.getFieldToString())
                    .collect(Collectors.joining(delimitter, data, eol));
        }

        try {
            bw.write(data);
        } catch (IOException e) {
            Throw.ioEx("Error writing data. File: [%s]. Data: [%s].", filePath(registration), e);
        }

    }

    private boolean failed() {
        return state == INITIALIZATION_FAILED || state == FLUSHING_FAILED;
    }

    private boolean disabledOrFailed() {
        return failed() && !enabled;
    }

    private String filePath(RecordableRegistration registration) {
        return filePath(registration.getInstance());
    }

    private String filePath(Recordable recordable) {
        return outFileMap.get(recordable).getAbsolutePath();
    }

    // Getters/Setters
    // ============================================================
    public boolean isEnabled() {
        return enabled;
    }

    public void enable() {
        setEnabled(true);
    }

    public void disable() {
        setEnabled(false);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDelimitter() {
        return delimitter;
    }

    /** Delimitter character used separate columns in the output csv. Commonly a comma "," or a tab "\t".
     * Defaults to {@link #DEFAULT_DELIMITTER}. */
    public void setDelimitter(String delimitter) {
        Assert.assertNotNull(delimitter, "delimitter");
        this.delimitter = delimitter;
    }

    public String getEol() {
        return eol;
    }

    /** End of line character used to separate lines in the output csv. Defaults to {@link #DEFAULT_EOL}. */
    public void setEol(String eol) {
        Assert.assertNotNull(eol, "eol");
        this.eol = eol;
    }

    public File getOutDir() {
        return outDir;
    }

    /** The root directory files will be output to. One sub directory (named via {@link #setOutFileDatePattern(String)})
     *  will be created inside this root directory for each instance of this class (usually only one per run of the JVM).
     *  <br/>
     *  Defaults to "${system temp dir}/data-recorder". */
    public void setOutDir(File outDir) {
        Assert.assertNotNull(outDir, "outDir");
        this.outDir = outDir;
    }

    public String getOutFileDatePattern() {
        return outFileDatePattern;
    }

    /** The date pattern to be used when creating a new sub directory at startup.<br/><br/>
     * See {@link SimpleDateFormat} for pattern information.<br/><br/>
     * Defaults to {@link #DEFAULT_OUT_FILE_DATE_PATTERN}. */
    public void setOutFileDatePattern(String outFileDatePattern) {
        Assert.assertNotNull(outFileDatePattern, "outFileDatePattern");
        this.outFileDatePattern = outFileDatePattern;
    }

    public String getOutFileExt() {
        return outFileExt;
    }

    /** The file extension of the output file. Defaults to {@link #DEFAULT_OUT_FILE_EXT}. */
    public void setOutFileExt(String outFileExt) {
        Assert.assertNotNull(outFileExt, "outFileExt");
        this.outFileExt = outFileExt;
    }

    public Map<Recordable, File> getOutFileMap() {
        return Collections.unmodifiableMap(outFileMap);
    }
}
