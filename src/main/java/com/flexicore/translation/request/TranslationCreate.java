package com.flexicore.translation.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;

public class TranslationCreate {
	private String name;
	private String description;
	private String languageCode;
	private String externalId;
	private String translatedId;
	@JsonIgnore
	private Baseclass translated;

	public String getName() {
		return name;
	}

	public <T extends TranslationCreate> T setName(String name) {
		this.name = name;
		return (T) this;
	}

	public String getDescription() {
		return description;
	}

	public <T extends TranslationCreate> T setDescription(String description) {
		this.description = description;
		return (T) this;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public <T extends TranslationCreate> T setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
		return (T) this;
	}

	public String getExternalId() {
		return externalId;
	}

	public <T extends TranslationCreate> T setExternalId(String externalId) {
		this.externalId = externalId;
		return (T) this;
	}

	public String getTranslatedId() {
		return translatedId;
	}

	public <T extends TranslationCreate> T setTranslatedId(String translatedId) {
		this.translatedId = translatedId;
		return (T) this;
	}

	@JsonIgnore
	public Baseclass getTranslated() {
		return translated;
	}

	public <T extends TranslationCreate> T setTranslated(Baseclass translated) {
		this.translated = translated;
		return (T) this;
	}
}
