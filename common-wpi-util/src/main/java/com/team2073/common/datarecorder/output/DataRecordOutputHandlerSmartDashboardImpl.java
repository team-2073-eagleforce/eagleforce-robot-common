package com.team2073.common.datarecorder.output;

import com.team2073.common.assertion.Assert;
import com.team2073.common.ctx.RobotContext;
import com.team2073.common.datarecorder.model.DataRecord;
import com.team2073.common.datarecorder.model.DataRecordRow;
import com.team2073.common.datarecorder.model.DataRecordTable;
import com.team2073.common.robot.adapter.SmartDashboardAdapter;

import java.util.List;

/**
 * @author pbriggs
 */
public class DataRecordOutputHandlerSmartDashboardImpl implements DataRecordOutputHandler {

    private static final String KEY_PREFIX = "datarecord";

    private final SmartDashboardAdapter output;

    public DataRecordOutputHandlerSmartDashboardImpl() {
        this(RobotContext.getInstance().getSmartDashboard());
    }

    public DataRecordOutputHandlerSmartDashboardImpl(SmartDashboardAdapter output) {
        Assert.assertNotNull(output, "output");
        this.output = output;
    }

    @Override
    public void init() {
    }

    @Override
    public void flushTable(DataRecordTable registration, List<DataRecordRow> rowList) {
        for (DataRecordRow row : rowList) {
            for (DataRecord<?> cell : row.getDataPointList()) {
                // TODO: Convert to snake case? If so, cache these conversions in a map for efficiency
                // TODO: Actually, either way, cache these names
                String sdKey = KEY_PREFIX + "." + registration.getName() + "." + cell.getFieldName();
                output.putValue(sdKey, cell.getFieldValue());
            }
        }
    }

}
