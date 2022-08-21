package io.extact.msa.rms.platform.fw.it;

import io.helidon.microprofile.tests.junit5.AddConfig;

@AddConfig(key = "persistence.apiType", value = "file")
class FileApplicationIntegrationTest extends AbstractApplicationIntegrationTest {
}
