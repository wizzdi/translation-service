package com.flexicore.translation.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import com.flexicore.model.FilteringInformationHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TranslationFiltering extends FilteringInformationHolder {

    private Set<String> externalIds=new HashSet<>();
    private Set<String> languageCodes=new HashSet<>();
    private Set<String> translatedIds=new HashSet<>();
    @JsonIgnore
    private List<Baseclass> translated;


    public Set<String> getExternalIds() {
        return externalIds;
    }

    public <T extends TranslationFiltering> T setExternalIds(Set<String> externalIds) {
        this.externalIds = externalIds;
        return (T) this;
    }

    public Set<String> getLanguageCodes() {
        return languageCodes;
    }

    public <T extends TranslationFiltering> T setLanguageCodes(Set<String> languageCodes) {
        this.languageCodes = languageCodes;
        return (T) this;
    }

    public Set<String> getTranslatedIds() {
        return translatedIds;
    }

    public <T extends TranslationFiltering> T setTranslatedIds(Set<String> translatedIds) {
        this.translatedIds = translatedIds;
        return (T) this;
    }

    @JsonIgnore
    public List<Baseclass> getTranslated() {
        return translated;
    }

    public <T extends TranslationFiltering> T setTranslated(List<Baseclass> translated) {
        this.translated = translated;
        return (T) this;
    }
}
