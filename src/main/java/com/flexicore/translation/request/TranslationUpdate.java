package com.flexicore.translation.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.translation.model.Translation;

public class TranslationUpdate extends TranslationCreate {

	private String id;
	@JsonIgnore
	private Translation translation;

	public String getId() {
		return id;
	}

	public <T extends TranslationUpdate> T setId(String id) {
		this.id = id;
		return (T) this;
	}

	@JsonIgnore
	public Translation getTranslation() {
		return translation;
	}

	public <T extends TranslationUpdate> T setTranslation(
			Translation translation) {
		this.translation = translation;
		return (T) this;
	}
}
