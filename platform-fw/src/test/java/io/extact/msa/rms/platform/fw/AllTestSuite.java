package io.extact.msa.rms.platform.fw;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.runner.RunWith;

@SuppressWarnings("deprecation")
@RunWith(JUnitPlatform.class)
@SelectPackages({
    "io.extact.msa.rms.platform"})
public class AllTestSuite {
}
