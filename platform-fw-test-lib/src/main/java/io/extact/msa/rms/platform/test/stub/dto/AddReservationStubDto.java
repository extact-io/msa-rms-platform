package io.extact.msa.rms.platform.test.stub.dto;

import java.time.LocalDateTime;

import io.extact.msa.rms.platform.fw.domain.Transformable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Getter @Setter @ToString
public class AddReservationStubDto implements Transformable {
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String note;
    private int rentalItemId;
    private int userAccountId;
}
