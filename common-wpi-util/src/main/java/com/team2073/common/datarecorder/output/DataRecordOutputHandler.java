package com.team2073.common.datarecorder.output;

import com.team2073.common.datarecorder.model.DataRecordRow;
import com.team2073.common.datarecorder.model.DataRecordTable;

import java.util.List;

/**
 * @author pbriggs
 */
public interface DataRecordOutputHandler {

    /** Do any initialization such as creating files, connecting to databases, verifying state, etc. */
    public abstract void init();

    public abstract void flushTable(DataRecordTable registration, List<DataRecordRow> rowList);

}
