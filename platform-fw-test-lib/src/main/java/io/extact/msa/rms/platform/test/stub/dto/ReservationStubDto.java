package io.extact.msa.rms.platform.test.stub.dto;

import java.time.LocalDateTime;
import java.util.function.Function;

import io.extact.msa.rms.platform.fw.domain.Transformable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Getter @Setter @ToString
public class ReservationStubDto implements Transformable {

    private Integer id;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String note;
    private int rentalItemId;
    private int userAccountId;
    private RentalItemStubDto rentalItemDto;
    private UserAccountStubDto userAccountDto;

    public <R> R getRentalItemDto(Function<RentalItemStubDto, R> converter) {
        if (rentalItemDto == null) {
            return null;
        }
        return converter.apply(rentalItemDto);
    }

    public <R> R getUserAccountDto(Function<UserAccountStubDto, R> converter) {
        if (userAccountDto == null) {
            return null;
        }
        return converter.apply(userAccountDto);
    }
}
