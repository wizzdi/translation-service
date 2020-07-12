package com.flexicore.translation.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;
import com.flexicore.translation.data.TranslationRepository;
import com.flexicore.translation.model.Translation;
import com.flexicore.translation.request.TranslationCreate;
import com.flexicore.translation.request.TranslationFiltering;
import com.flexicore.translation.request.TranslationUpdate;

import javax.ws.rs.BadRequestException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@PluginInfo(version = 1)
@Extension
@Component
public class TranslationService implements ServicePlugin {

	@Autowired
	private Logger logger;

	@PluginInfo(version = 1)
	@Autowired
	private TranslationRepository translationRepository;

	public Translation updateTranslation(TranslationUpdate updateTranslation,
										 SecurityContext securityContext) {
		if (updateTranslationNoMerge(updateTranslation,
				updateTranslation.getTranslation())) {
			translationRepository.merge(updateTranslation.getTranslation());
		}
		return updateTranslation.getTranslation();
	}

	public boolean updateTranslationNoMerge(
			TranslationCreate createTranslation, Translation translation) {
		boolean update = false;
		if (createTranslation.getName() != null
				&& !createTranslation.getName().equals(translation.getName())) {
			translation.setName(createTranslation.getName());
			update = true;
		}
		if (createTranslation.getDescription() != null
				&& !createTranslation.getDescription().equals(
						translation.getDescription())) {
			translation.setDescription(createTranslation.getDescription());
			update = true;
		}

		if (createTranslation.getExternalId() != null
				&& !createTranslation.getExternalId().equals(
						translation.getExternalId())) {
			translation.setExternalId(createTranslation.getExternalId());
			update = true;
		}

		if (createTranslation.getLanguageCode() != null
				&& !createTranslation.getLanguageCode().equals(
						translation.getLanguageCode())) {
			translation.setLanguageCode(createTranslation.getLanguageCode());
			update = true;
		}

		if (createTranslation.getTranslated() != null
				&& (translation.getTranslated() == null || !createTranslation
						.getTranslated().getId()
						.equals(translation.getTranslated().getId()))) {
			translation.setTranslated(createTranslation.getTranslated());
			update = true;
		}
		return update;

	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
			List<String> batchString, SecurityContext securityContext) {
		return translationRepository.getByIdOrNull(id, c, batchString,
				securityContext);
	}

	public PaginationResponse<Translation> getAllTranslations(
			TranslationFiltering translationFiltering,
			SecurityContext securityContext) {
		List<Translation> list = listAllTranslations(translationFiltering,
				securityContext);
		long count = translationRepository.countAllTranslations(
				translationFiltering, securityContext);
		return new PaginationResponse<>(list, translationFiltering, count);
	}

	public List<Translation> listAllTranslations(
			TranslationFiltering translationFiltering,
			SecurityContext securityContext) {
		return translationRepository.listAllTranslations(translationFiltering,
				securityContext);
	}

	public Translation createTranslation(TranslationCreate createTranslation,
										 SecurityContext securityContext) {
		Translation translation = createTranslationNoMerge(createTranslation,
				securityContext);
		translationRepository.merge(translation);
		return translation;

	}

	private Translation createTranslationNoMerge(
			TranslationCreate createTranslation, SecurityContext securityContext) {
		Translation translation = new Translation(
				createTranslation.getName(), securityContext);
		updateTranslationNoMerge(createTranslation, translation);
		return translation;
	}

	private void populate(TranslationCreate createTranslation,
						  SecurityContext securityContext) {
		String translatedId = createTranslation.getTranslatedId();
		Baseclass baseclass = translatedId != null ? getByIdOrNull(
				translatedId, Baseclass.class, null, securityContext) : null;
		createTranslation.setTranslated(baseclass);
	}

	public void validateCreate(TranslationCreate createTranslation,
							   SecurityContext securityContext) {
		populate(createTranslation, securityContext);
		if (createTranslation.getTranslated() == null
				&& createTranslation.getTranslatedId() != null) {
			throw new BadRequestException("No Baseclass with id "
					+ createTranslation.getTranslatedId());
		}

	}

	public void validateUpdate(TranslationUpdate updateTranslation,
							   SecurityContext securityContext) {
		populate(updateTranslation, securityContext);
		if (updateTranslation.getTranslated() == null
				&& updateTranslation.getTranslatedId() != null) {
			throw new BadRequestException("No Baseclass with id "
					+ updateTranslation.getTranslatedId());
		}
	}

	public void validate(TranslationFiltering translationFiltering,
			SecurityContext securityContext) {
		Set<String> translatedIds = translationFiltering.getTranslatedIds();
		Map<String, Baseclass> baseclassMap = translatedIds.isEmpty()
				? new HashMap<>()
				: translationRepository
						.listByIds(Translation.class, translatedIds,
								securityContext).parallelStream()
						.collect(Collectors.toMap(f -> f.getId(), f -> f));
		translatedIds.removeAll(baseclassMap.keySet());
		if (!translatedIds.isEmpty()) {
			throw new BadRequestException("No Baseclasses with ids "
					+ translatedIds);
		}
		translationFiltering.setTranslated(new ArrayList<>(baseclassMap
				.values()));
	}

	public Map<String, Object> generateI18nJson(
			TranslationFiltering translationFiltering,
			SecurityContext securityContext) {
		Map<String, Object> toRet = new HashMap<>();
		List<Translation> translations = listAllTranslations(
				translationFiltering, securityContext);
		for (Translation translation : translations) {
			if (translation.getExternalId() != null
					&& !translation.getExternalId().isEmpty()) {
				String[] split = translation.getExternalId().split("\\.");
				Map<String, Object> prev = toRet;
				for (int i = 0; i < split.length - 1; i++) {
					HashMap<String, Object> inner = new HashMap<>();
					prev.put(split[i], inner);
					prev = inner;
				}
				prev.put(split[split.length - 1], translation.getName());
			}
		}
		return toRet;
	}
}
