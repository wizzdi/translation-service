package com.flexicore.translation.response;

public class ImportI18NResponse {

    private int created;
    private int updated;
    private int unchanged;


    public int getCreated() {
        return created;
    }

    public <T extends ImportI18NResponse> T setCreated(int created) {
        this.created = created;
        return (T) this;
    }

    public int getUpdated() {
        return updated;
    }

    public <T extends ImportI18NResponse> T setUpdated(int updated) {
        this.updated = updated;
        return (T) this;
    }

    public int getUnchanged() {
        return unchanged;
    }

    public <T extends ImportI18NResponse> T setUnchanged(int unchanged) {
        this.unchanged = unchanged;
        return (T) this;
    }
}
