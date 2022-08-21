package io.extact.msa.rms.platform.fw.domain;

public interface IdProperty {

    Integer getId();

    void setId(Integer id);

    default boolean isSameId(IdProperty other) {
        if (other == null) {
            return false;
        }
        if (this.getId() == null) {
            return false;
        }
        return this.getId().equals(other.getId());
    }
}
