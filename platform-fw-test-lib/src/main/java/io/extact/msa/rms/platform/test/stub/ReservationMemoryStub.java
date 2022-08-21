package io.extact.msa.rms.platform.test.stub;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.extact.msa.rms.platform.fw.domain.vo.DateTimePeriod;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.rms.platform.test.stub.dto.AddReservationStubDto;
import io.extact.msa.rms.platform.test.stub.dto.ReservationStubDto;

public class ReservationMemoryStub {

    private static Comparator<ReservationStubDto> ID_ASC = Comparator.comparing(ReservationStubDto::getId);
    private static Map<Integer, ReservationStubDto> RESERVATION_DATA = Map.of(
            1, ReservationStubDto.of(1, LocalDateTime.of(2020, 4, 1, 10, 0, 0), LocalDateTime.of(2020, 4, 1, 12, 0, 0), "メモ1", 3, 1, null, null),
            2, ReservationStubDto.of(2, LocalDateTime.of(2020, 4, 1, 16, 0, 0), LocalDateTime.of(2020, 4, 1, 18, 0, 0), "メモ2", 3, 2, null, null),
            3, ReservationStubDto.of(3, LocalDateTime.of(2099, 4, 1, 10, 0, 0), LocalDateTime.of(2099, 4, 1, 12, 0, 0), "メモ3", 3, 1, null, null));

    private Map<Integer, ReservationStubDto> dtoMap;

    private boolean childrenOfComposition;
    private RentalItemMemoryStub itemStub;
    private UserAccountMemoryStub userStub;

    public ReservationMemoryStub() {
        childrenOfComposition = true;
        this.itemStub = new RentalItemMemoryStub();
        this.userStub = new UserAccountMemoryStub();
    }

    public ReservationMemoryStub(RentalItemMemoryStub itemStub, UserAccountMemoryStub userStub) {
        this.itemStub = itemStub;
        this.userStub = userStub;
    }

    public void init() {
        dtoMap = new HashMap<>();
        dtoMap.putAll(RESERVATION_DATA);
        if (childrenOfComposition) {
            itemStub.init();
            userStub.init();
        }
    }

    public List<ReservationStubDto> getAll() {
        return dtoMap.values().stream()
                .map(this::composeDto)
                .sorted(ID_ASC)
                .toList();
    }

    public ReservationStubDto add(AddReservationStubDto dto) {
        valiateRelation(dto.getRentalItemId(), dto.getUserAccountId());
        var conditionOfPeriod = new DateTimePeriod(dto.getStartDateTime(), dto.getEndDateTime());
        if (dtoMap.values().stream().anyMatch(
                r -> new DateTimePeriod(r.getStartDateTime(), r.getEndDateTime()).isOverlappedBy(conditionOfPeriod))) {
            throw new BusinessFlowException("Stub Error", CauseType.DUPRICATE);
        }
        int max = dtoMap.keySet().stream().max(Comparator.naturalOrder()).get();
        var newValue = ReservationStubDto.of(max + 1, dto.getStartDateTime(), dto.getEndDateTime(), dto.getNote(),
                dto.getRentalItemId(), dto.getUserAccountId(), null, null);
        return composeDto(dtoMap.compute(newValue.getId(), (key, old) -> newValue));
    }

    public ReservationStubDto update(ReservationStubDto dto) {
        if (!dtoMap.containsKey(dto.getId())) {
            throw new BusinessFlowException("Stub Error", CauseType.NOT_FOUND);
        }
        valiateRelation(dto.getRentalItemId(), dto.getUserAccountId());
        return composeDto(dtoMap.computeIfPresent(dto.getId(), (key, old) -> cloneDto(dto)));
    }

    public void delete(int deleteId) {
        if (!dtoMap.containsKey(deleteId)) {
            throw new BusinessFlowException("Stub Error", CauseType.NOT_FOUND);
        }
        dtoMap.remove(deleteId);
    }

    // ---------------------------------------

    public List<ReservationStubDto> findByRentalItemAndStartDate(int itemId, LocalDate startDate) {
        return dtoMap.values().stream()
                .filter(r -> r.getRentalItemId() == itemId)
                .filter(r -> r.getStartDateTime().toLocalDate().equals(startDate))
                .map(this::composeDto)
                .sorted(ID_ASC)
                .toList();
    }

    public List<ReservationStubDto> findByReserverId(int reserverId) {
        return dtoMap.values().stream()
                .filter(r -> r.getUserAccountId() == reserverId)
                .map(this::composeDto)
                .sorted(ID_ASC)
                .toList();
    }

    public List<ReservationStubDto> findByRentalItemId(int itemId) {
        return dtoMap.values().stream()
                .filter(r -> r.getRentalItemId() == itemId)
                .map(this::composeDto)
                .sorted(ID_ASC)
                .toList();
    }

    public Optional<ReservationStubDto> findOverlappedReservation(int itemId, LocalDateTime from, LocalDateTime to) {
        var conditionOfPeriod = new DateTimePeriod(from, to);
        return dtoMap.values().stream()
                .filter(r -> r.getRentalItemId() == itemId)
                .filter(r -> new DateTimePeriod(r.getStartDateTime(), r.getEndDateTime())
                        .isOverlappedBy(conditionOfPeriod))
                .map(this::composeDto)
                .findFirst();
    }

    public List<ReservationStubDto> findOverlappedReservations(LocalDateTime from, LocalDateTime to) {
        var conditionOfPeriod = new DateTimePeriod(from, to);
        return dtoMap.values().stream()
                .filter(r -> new DateTimePeriod(r.getStartDateTime(), r.getEndDateTime())
                        .isOverlappedBy(conditionOfPeriod))
                .map(this::composeDto)
                .sorted(ID_ASC)
                .toList();
    }

    public boolean hasRentalItemWith(int itemId) {
        return dtoMap.values().stream()
                .anyMatch(r -> r.getRentalItemId() == itemId);
    }

    public boolean hasUserAccountWith(int userId) {
        return dtoMap.values().stream()
                .anyMatch(r -> r.getUserAccountId() == userId);
    }

    public void cancel(int reservationId, int reserverId) throws BusinessFlowException {
        var target = dtoMap.get(reservationId);
        if (target == null) {
            throw new BusinessFlowException("Stub Error", CauseType.NOT_FOUND);
        }
        if (target.getUserAccountId() != reserverId) {
            throw new BusinessFlowException("Stub Error", CauseType.FORBIDDEN);
        }
        dtoMap.remove(reservationId);
    }

    // ---------------------------------------

    private ReservationStubDto cloneDto(ReservationStubDto src) {
        return ReservationStubDto.of(src.getId(), src.getStartDateTime(), src.getEndDateTime(), src.getNote(),
                src.getRentalItemId(), src.getUserAccountId(), null, null);
    }

    private ReservationStubDto composeDto(ReservationStubDto reservationDto) {
        if (itemStub != null) {
            var itemDto = itemStub.get(reservationDto.getRentalItemId())
                    .orElseThrow(() -> new BusinessFlowException(
                            "target does not exist for id:[" + reservationDto.getRentalItemId() + "]",
                            CauseType.NOT_FOUND));
            reservationDto.setRentalItemDto(itemDto);
        }
        if (userStub != null) {
            var userDto = userStub.get(reservationDto.getUserAccountId())
                    .orElseThrow(() -> new BusinessFlowException(
                            "target does not exist for id:[" + reservationDto.getUserAccountId() + "]",
                            CauseType.NOT_FOUND));
            reservationDto.setUserAccountDto(userDto);
        }
        return reservationDto;
    }

    private void valiateRelation(int itemId, int userId) {
        var existsItem = itemStub.exists(itemId);
        if (!existsItem) {
            throw new BusinessFlowException("RentalItem does not exist for rentalItemId.", CauseType.NOT_FOUND);
        }
        var existsUser = itemStub.exists(userId);
        if (!existsUser) {
            throw new BusinessFlowException("UserAccount does not exist for userAccountId.", CauseType.NOT_FOUND);
        }
    }
}
