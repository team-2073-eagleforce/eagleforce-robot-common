package com.team2073.common.test.annon;

import com.team2073.common.test.TestConstants;
import org.junit.jupiter.api.Disabled;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks a test disabled due to the test not being written yet.
 *
 * @author Preston Briggs
 */
@Retention(RetentionPolicy.RUNTIME)
@Disabled(TestConstants.TEST_FAILING)
public @interface TestFailing {
}
