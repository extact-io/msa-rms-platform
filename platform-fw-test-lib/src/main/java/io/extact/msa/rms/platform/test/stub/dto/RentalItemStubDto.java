package io.extact.msa.rms.platform.test.stub.dto;

import io.extact.msa.rms.platform.fw.domain.Transformable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Getter @Setter @ToString
public class RentalItemStubDto implements Transformable {
    private Integer id;
    private String serialNo;
    private String itemName;
}
