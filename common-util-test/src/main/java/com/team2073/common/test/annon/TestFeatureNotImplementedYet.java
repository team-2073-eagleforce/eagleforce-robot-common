package com.team2073.common.test.annon;

import com.team2073.common.test.TestConstants;
import org.junit.jupiter.api.Disabled;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks a test disabled due to the feature under test not being implemented.
 *
 * @author Preston Briggs
 */
@Retention(RetentionPolicy.RUNTIME)
@Disabled(TestConstants.FEATURE_NOT_IMPLEMENTED_YET)
public @interface TestFeatureNotImplementedYet {
}
