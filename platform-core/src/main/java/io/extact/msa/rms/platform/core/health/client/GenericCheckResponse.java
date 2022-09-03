package io.extact.msa.rms.platform.core.health.client;

import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.health.HealthCheckResponse.Status;

import lombok.ToString;

@ToString
public class GenericCheckResponse {

    private Status status;
    private List<Check> checks;

    public String getStatus() {
        return status.name();
    }

    public void setStatus(String value) {
        this.status = Status.valueOf(value);
    }

    public List<Check> getChecks() {
        return checks;
    }

    public void setChecks(List<Check> value) {
        this.checks = value;
    }

    public boolean isDown() {
        return this.status == Status.DOWN;
    }

    @ToString
    public static class Check {
        private Status status;
        private String name;
        private Map<String, Object> data;

        public String getStatus() {
            return status.name();
        }

        public void setStatus(String value) {
            this.status = Status.valueOf(value);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public void setData(Map<String, Object> data) {
            this.data = data;
        }
    }
}
