package com.flexicore.translation.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.translation.model.Translation;

public class UpdateTranslation extends CreateTranslation{

    private String id;
    @JsonIgnore
    private Translation translation;

    public String getId() {
        return id;
    }

    public <T extends UpdateTranslation> T setId(String id) {
        this.id = id;
        return (T) this;
    }

    @JsonIgnore
    public Translation getTranslation() {
        return translation;
    }

    public <T extends UpdateTranslation> T setTranslation(Translation translation) {
        this.translation = translation;
        return (T) this;
    }
}
