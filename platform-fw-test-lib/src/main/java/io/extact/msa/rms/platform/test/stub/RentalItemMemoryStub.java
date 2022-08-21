package io.extact.msa.rms.platform.test.stub;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.rms.platform.test.stub.dto.AddRentalItemStubDto;
import io.extact.msa.rms.platform.test.stub.dto.RentalItemStubDto;

public class RentalItemMemoryStub {

    private static Comparator<RentalItemStubDto> ID_ASC = Comparator.comparing(RentalItemStubDto::getId);
    private static Map<Integer, RentalItemStubDto> ITEM_DATA = Map.of(
            1, RentalItemStubDto.of(1, "A0001", "レンタル品1号"),
            2, RentalItemStubDto.of(2, "A0002", "レンタル品2号"),
            3, RentalItemStubDto.of(3, "A0003", "レンタル品3号"),
            4, RentalItemStubDto.of(4, "A0004", "レンタル品4号"));

    private Map<Integer, RentalItemStubDto> dtoMap;

    public void init() {
        dtoMap = new HashMap<>();
        dtoMap.putAll(ITEM_DATA);
    }

    public List<RentalItemStubDto> getAll() {
        return dtoMap.values().stream()
                .sorted(ID_ASC)
                .toList();
    }

    public Optional<RentalItemStubDto> get(int itemId) {
        return Optional.ofNullable(dtoMap.get(itemId));
    }

    public RentalItemStubDto add(AddRentalItemStubDto addDto) {
        if (dtoMap.values().stream().anyMatch(i -> i.getSerialNo().equals(addDto.getSerialNo()))) {
            throw new BusinessFlowException("Stub Error", CauseType.DUPRICATE);
        }
        int max = dtoMap.keySet().stream().max(Comparator.naturalOrder()).get();
        var newValue = RentalItemStubDto.of(max + 1, addDto.getSerialNo(), addDto.getItemName());
        return dtoMap.compute(newValue.getId(), (key, old) -> newValue);
    }

    public RentalItemStubDto update(RentalItemStubDto dto) {
        if (!dtoMap.containsKey(dto.getId())) {
            throw new BusinessFlowException("Stub Error", CauseType.NOT_FOUND);
        }
        return dtoMap.computeIfPresent(dto.getId(), (key, old) -> cloneDto(dto));
    }

    public void delete(int itemId) {
        if (!dtoMap.containsKey(itemId)) {
            throw new BusinessFlowException("Stub Error", CauseType.NOT_FOUND);
        }
        dtoMap.remove(itemId);
    }

    public boolean exists(int itemId) {
        return dtoMap.containsKey(itemId);
    }

    private RentalItemStubDto cloneDto(RentalItemStubDto src) {
        return RentalItemStubDto.of(src.getId(), src.getSerialNo(), src.getItemName());
    }
}
