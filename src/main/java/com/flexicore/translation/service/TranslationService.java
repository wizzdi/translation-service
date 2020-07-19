package com.flexicore.translation.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.model.FileResource;
import com.flexicore.security.SecurityContext;
import com.flexicore.translation.data.TranslationRepository;
import com.flexicore.translation.model.Translation;
import com.flexicore.translation.request.ImportI18NRequest;
import com.flexicore.translation.request.TranslationCreate;
import com.flexicore.translation.request.TranslationFiltering;
import com.flexicore.translation.request.TranslationUpdate;
import com.flexicore.translation.response.ImportI18NResponse;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.BadRequestException;
import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
@Extension
@Component
public class TranslationService implements ServicePlugin {

    private static final ObjectMapper objectMapper = new ObjectMapper();

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

    public void validate(ImportI18NRequest importI18NRequest,
                         SecurityContext securityContext) {
        if (importI18NRequest.getLangCode() == null || importI18NRequest.getLangCode().isEmpty()) {
            throw new BadRequestException("langCode must be non null non empty string");
        }
        String fileResourceId = importI18NRequest.getFileResourceId();
        FileResource fileResource = fileResourceId != null ? getByIdOrNull(fileResourceId, FileResource.class, null, securityContext) : null;
        if (fileResource == null) {
            throw new BadRequestException("No File resource with id " + fileResourceId);
        }
        importI18NRequest.setFileResource(fileResource);
        try {
            TypeReference<Map<String, Object>> ref = new TypeReference<Map<String, Object>>() {
            };
            Map<String, Object> i18N = objectMapper.readValue(new File(importI18NRequest.getFileResource().getFullPath()), ref);
            importI18NRequest.setI18N(i18N);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "failed parsing i18n file: " + importI18NRequest.getFileResource().getFullPath());
            throw new BadRequestException("Could not parse file into i18N");
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

    public ImportI18NResponse importI18n(ImportI18NRequest importI18NRequest, SecurityContext securityContext) {
        ImportI18NResponse response = new ImportI18NResponse();

        Map<String, TranslationCreate> translations = getTranslations(importI18NRequest.getLangCode(), importI18NRequest.getI18N());
        Map<String, Translation> existing = listAllTranslations(new TranslationFiltering().setLanguageCodes(Collections.singleton(importI18NRequest.getLangCode())).setExternalIds(translations.keySet()), securityContext).stream().collect(Collectors.toMap(f -> f.getExternalId(), f -> f, (a, b) -> a));
        List<Object> toMerge = new ArrayList<>();
        for (TranslationCreate value : translations.values()) {
            Translation translation = existing.get(value.getExternalId());
            if (translation == null) {
                translation = createTranslationNoMerge(value, securityContext);
                toMerge.add(translation);
                existing.put(translation.getExternalId(), translation);
                response.setCreated(response.getCreated() + 1);
            } else {
                if (updateTranslationNoMerge(value, translation)) {
                    toMerge.add(translation);
                    response.setUpdated(response.getUpdated() + 1);
                } else {
                    response.setUnchanged(response.getUnchanged() + 1);
                }
            }
        }
        translationRepository.massMerge(toMerge);
        return response;

    }

    private Map<String, TranslationCreate> getTranslations(String langCode, Map<String, Object> i18n) {
        HashMap<String, TranslationCreate> output = new HashMap<>();
        getTranslations(langCode, null, i18n, output);
        return output;
    }

    private void getTranslations(String langCode, String prefix, Map<String, Object> i18n, Map<String, TranslationCreate> output) {
        for (Map.Entry<String, Object> entry : i18n.entrySet()) {
            String externalId = prefix == null ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                getTranslations(langCode, externalId, (Map<String, Object>) value, output);
            }
            if (value instanceof String) {
                output.put(externalId, new TranslationCreate().setExternalId(externalId).setName((String) value).setLanguageCode(langCode));
            }

        }
    }
}
