package com.team2073.common.robot.module;

import com.team2073.common.ctx.RobotContext;
import com.team2073.common.periodic.AsyncPeriodicRunnable;
import com.team2073.common.robot.adapter.SmartDashboardAdapter;
import com.team2073.common.util.ConversionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Preston Briggs
 */
public class DiagnosticLoggingModule implements AsyncPeriodicRunnable {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Runtime rt = Runtime.getRuntime();
    private final RobotContext robotContext = RobotContext.getInstance();
    private final SmartDashboardAdapter smartDashboard = robotContext.getSmartDashboard();
    
    private int activeThreads;
    private String freeMem;
    private String totalMem;
    private String maxHeapSize;
    
    @Override
    public void onPeriodicAsync() {
        activeThreads = Thread.activeCount();
        freeMem = ConversionUtil.humanReadableByteCount(rt.freeMemory());
        totalMem = ConversionUtil.humanReadableByteCount(rt.totalMemory());
        maxHeapSize = ConversionUtil.humanReadableByteCount(rt.maxMemory());
    
        logger.trace("Threads: " + activeThreads);
        smartDashboard.putNumber("diagnostics.activeThreads", activeThreads);
        
        logger.trace(String.format("Memory: free=[%s] \t total=[%s] \t max=[%s]", freeMem, totalMem, maxHeapSize));
        smartDashboard.putString("diagnostics.memory.free", freeMem);
        smartDashboard.putString("diagnostics.memory.totalMem", totalMem);
        smartDashboard.putString("diagnostics.memory.maxHeapSize", maxHeapSize);
    }
}
