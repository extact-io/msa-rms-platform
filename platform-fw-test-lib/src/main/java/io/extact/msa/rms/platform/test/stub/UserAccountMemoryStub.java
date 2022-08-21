package io.extact.msa.rms.platform.test.stub;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.extact.msa.rms.platform.fw.domain.vo.UserType;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException.CauseType;
import io.extact.msa.rms.platform.test.stub.dto.AddUserAccountStubDto;
import io.extact.msa.rms.platform.test.stub.dto.UserAccountStubDto;

public class UserAccountMemoryStub {

    private static Comparator<UserAccountStubDto> ID_ASC = Comparator.comparing(UserAccountStubDto::getId);
    private static Map<Integer, UserAccountStubDto> USER_DATA = Map.of(
            1, UserAccountStubDto.of(1, "member1", "member1", "メンバー1", "070-1111-2222", "連絡先1", UserType.MEMBER),
            2, UserAccountStubDto.of(2, "member2", "member2", "メンバー2", "080-1111-2222", "連絡先2", UserType.MEMBER),
            3, UserAccountStubDto.of(3, "admin", "admin", "管理者", "050-1111-2222", "連絡先3", UserType.ADMIN));

    private Map<Integer, UserAccountStubDto> dtoMap;

    public void init() {
        dtoMap = new HashMap<>();
        dtoMap.putAll(USER_DATA);
    }

    public List<UserAccountStubDto> getAll() {
        return dtoMap.values().stream()
                .sorted(ID_ASC)
                .toList();
    }

    public Optional<UserAccountStubDto> get(int userId) {
        return Optional.ofNullable(dtoMap.get(userId));
    }

    public UserAccountStubDto add(AddUserAccountStubDto addDto) {
        if (dtoMap.values().stream().anyMatch(u -> u.getLoginId().equals(addDto.getLoginId()))) {
            throw new BusinessFlowException("Stub Error", CauseType.DUPRICATE);
        }
        int max = dtoMap.keySet().stream().max(Comparator.naturalOrder()).get();
        var newValue = UserAccountStubDto.of(max + 1, addDto.getLoginId(), addDto.getPassword(), addDto.getUserName(), addDto.getPhoneNumber(),
                addDto.getContact(), addDto.getUserType());
        return dtoMap.compute(newValue.getId(), (key, old) -> newValue);
    }

    public UserAccountStubDto update(UserAccountStubDto dto) {
        if (!dtoMap.containsKey(dto.getId())) {
            throw new BusinessFlowException("Stub Error", CauseType.NOT_FOUND);
        }
        return dtoMap.computeIfPresent(dto.getId(), (key, old) -> cloneDto(dto));
    }

    public void delete(int userId) {
        if (!dtoMap.containsKey(userId)) {
            throw new BusinessFlowException("Stub Error", CauseType.NOT_FOUND);
        }
        dtoMap.remove(userId);
    }

    public UserAccountStubDto authenticate(String loginId, String password) {
        return dtoMap.values().stream()
                .filter(user -> user.getLoginId().equals(loginId))
                .filter(user -> user.getPassword().equals(password))
                .findFirst()
                .orElseThrow(() -> new BusinessFlowException("Stub Error", CauseType.NOT_FOUND));
    }

    private UserAccountStubDto cloneDto(UserAccountStubDto src) {
        return UserAccountStubDto.of(src.getId(), src.getLoginId(), src.getPassword(), src.getUserName(),
                src.getPhoneNumber(),
                src.getContact(), UserType.valueOf(src.getUserType()));
    }

    public boolean exists(int userId) {
        return dtoMap.containsKey(userId);
    }
}
