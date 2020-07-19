package com.flexicore.translation.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.FileResource;

import java.util.Map;

public class ImportI18NRequest {
    private String fileResourceId;
    @JsonIgnore
    private FileResource fileResource;
    @JsonIgnore
    private Map<String,Object> i18N;
    private String langCode;

    public String getFileResourceId() {
        return fileResourceId;
    }

    public <T extends ImportI18NRequest> T setFileResourceId(String fileResourceId) {
        this.fileResourceId = fileResourceId;
        return (T) this;
    }

    @JsonIgnore
    public FileResource getFileResource() {
        return fileResource;
    }

    public <T extends ImportI18NRequest> T setFileResource(FileResource fileResource) {
        this.fileResource = fileResource;
        return (T) this;
    }

    public String getLangCode() {
        return langCode;
    }

    public <T extends ImportI18NRequest> T setLangCode(String langCode) {
        this.langCode = langCode;
        return (T) this;
    }

    @JsonIgnore
    public Map<String, Object> getI18N() {
        return i18N;
    }

    public <T extends ImportI18NRequest> T setI18N(Map<String, Object> i18N) {
        this.i18N = i18N;
        return (T) this;
    }
}
