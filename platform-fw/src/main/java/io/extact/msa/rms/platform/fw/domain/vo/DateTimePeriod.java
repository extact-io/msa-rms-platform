package io.extact.msa.rms.platform.fw.domain.vo;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;

import org.apache.commons.lang3.Range;

public class DateTimePeriod {

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Range<ChronoLocalDateTime<?>> period;

    public DateTimePeriod(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        period = Range.between(startDateTime, endDateTime);
    }

    public LocalDateTime getStartDateTime() {
        return this.startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return this.endDateTime;
    }

    public boolean isOverlappedBy(DateTimePeriod otherPeriod) {
        return this.period.isOverlappedBy(otherPeriod.period);
    }
}
