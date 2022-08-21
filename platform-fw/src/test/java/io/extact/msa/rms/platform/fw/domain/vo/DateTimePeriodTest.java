package io.extact.msa.rms.platform.fw.domain.vo;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class DateTimePeriodTest {

    @Test
    void testPeriod() {

        // reservedPeriod(2020/1/1 00:01-2020/12/31 23:59)
        DateTimePeriod reservedPeriod = new DateTimePeriod(LocalDateTime.of(2020, 1, 1, 0, 1), LocalDateTime.of(2020, 12, 31, 23, 59));

        // pattern1:終了日時(-2020/1/1 00:00) ＜ 予約済み利用開始日時
        DateTimePeriod requestPeriod = new DateTimePeriod(LocalDateTime.of(2019, 12, 1, 0, 0), LocalDateTime.of(2020, 1, 1, 0, 0));
        assertThat(reservedPeriod.isOverlappedBy(requestPeriod)).isFalse();

        // pattern2:終了日時(-2020/1/1 00:01) ＝ 予約済み利用開始日時
        requestPeriod = new DateTimePeriod(LocalDateTime.of(2019, 12, 1, 0, 0), LocalDateTime.of(2020, 1, 1, 0, 1));
        assertThat(reservedPeriod.isOverlappedBy(requestPeriod)).isTrue();

        // pattern3:予約済み利用開始日時      ＜ 終了日時-2020/1/1 00:02)
        requestPeriod = new DateTimePeriod(LocalDateTime.of(2019, 12, 1, 0, 0), LocalDateTime.of(2020, 1, 1, 0, 2));
        assertThat(reservedPeriod.isOverlappedBy(requestPeriod)).isTrue();

        // pattern4:予約済み期間に期間(2020/1/1 00:02 - 2020/12/31 23:58)が含まれる
        requestPeriod = new DateTimePeriod(LocalDateTime.of(2020, 1, 1, 0, 2), LocalDateTime.of(2020, 12, 31, 23, 58));
        assertThat(reservedPeriod.isOverlappedBy(requestPeriod)).isTrue();

        // pattern5:予約済み期間を期間(2020/1/1 00:00 - 2021/1/1 00:00)が含む
        requestPeriod = new DateTimePeriod(LocalDateTime.of(2020, 1, 1, 0, 0), LocalDateTime.of(2021, 1, 1, 0, 0));
        assertThat(reservedPeriod.isOverlappedBy(requestPeriod)).isTrue();

        // pattern6:予約済み期間と期間(2020/1/1 00:01 - 2020/12/31 23:59)同じ
        requestPeriod = new DateTimePeriod(LocalDateTime.of(2020, 1, 1, 0, 1), LocalDateTime.of(2020, 12, 31, 23, 59));
        assertThat(reservedPeriod.isOverlappedBy(requestPeriod)).isTrue();

        // pattern7:予約済み利用終了日時 ＝ 開始日時(2020/12/31 23:59-)
        requestPeriod = new DateTimePeriod(LocalDateTime.of(2020, 12, 31, 23, 59), LocalDateTime.of(2021, 1, 1, 0, 0));
        assertThat(reservedPeriod.isOverlappedBy(requestPeriod)).isTrue();

        // pattern8:予約済み利用終了日時 ＜ 開始日時(2021/1/1 00:00-)
        requestPeriod = new DateTimePeriod(LocalDateTime.of(2021, 1, 1, 0, 0), LocalDateTime.of(2021, 1, 1, 0, 1));
        assertThat(reservedPeriod.isOverlappedBy(requestPeriod)).isFalse();
    }

}
