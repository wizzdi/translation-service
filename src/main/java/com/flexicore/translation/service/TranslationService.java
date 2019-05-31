package com.flexicore.translation.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;
import com.flexicore.translation.data.TranslationRepository;
import com.flexicore.translation.model.Translation;
import com.flexicore.translation.request.CreateTranslation;
import com.flexicore.translation.request.TranslationFiltering;
import com.flexicore.translation.request.UpdateTranslation;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.logging.Logger;

@PluginInfo(version = 1)

public class TranslationService implements ServicePlugin {

    @Inject
    private Logger logger;

    @Inject
    @PluginInfo(version = 1)
    private TranslationRepository translationRepository;




    public Translation updateTranslation(UpdateTranslation updateTranslation, SecurityContext securityContext) {
        if (updateTranslationNoMerge(updateTranslation, updateTranslation.getTranslation())) {
            translationRepository.merge(updateTranslation.getTranslation());
        }
        return updateTranslation.getTranslation();
    }

    public boolean updateTranslationNoMerge(CreateTranslation createTranslation, Translation translation) {
        boolean update =false;
        if(createTranslation.getName()!=null && !createTranslation.getName().equals(translation.getName())){
            translation.setName(createTranslation.getName());
            update=true;
        }
        if(createTranslation.getDescription()!=null && !createTranslation.getDescription().equals(translation.getDescription())){
            translation.setDescription(createTranslation.getDescription());
            update=true;
        }

        if(createTranslation.getExternalId()!=null && !createTranslation.getExternalId().equals(translation.getExternalId())){
            translation.setExternalId(createTranslation.getExternalId());
            update=true;
        }

        if(createTranslation.getLanguageCode()!=null && !createTranslation.getLanguageCode().equals(translation.getLanguageCode())){
            translation.setLanguageCode(createTranslation.getLanguageCode());
            update=true;
        }

        if(createTranslation.getTranslated()!=null && (translation.getTranslated()==null||!createTranslation.getTranslated().getId().equals(translation.getTranslated().getId()))){
            translation.setTranslated(createTranslation.getTranslated());
            update=true;
        }
        return update;

    }


    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, List<String> batchString, SecurityContext securityContext) {
        return translationRepository.getByIdOrNull(id, c, batchString, securityContext);
    }

    public PaginationResponse<Translation> getAllTranslations(TranslationFiltering translationFiltering, SecurityContext securityContext) {
       List<Translation> list=listAllTranslations(translationFiltering,securityContext);
       long count=translationRepository.countAllTranslations(translationFiltering,securityContext);
       return new PaginationResponse<>(list,translationFiltering,count);
    }

    public List<Translation> listAllTranslations(TranslationFiltering translationFiltering, SecurityContext securityContext) {
       return translationRepository.listAllTranslations(translationFiltering,securityContext);
    }


    public Translation createTranslation(CreateTranslation createTranslation, SecurityContext securityContext) {
        Translation translation = createTranslationNoMerge(createTranslation, securityContext);
        translationRepository.merge(translation);
        return translation;

    }

    private Translation createTranslationNoMerge(CreateTranslation createTranslation, SecurityContext securityContext) {
        Translation translation = Translation.s().CreateUnchecked(createTranslation.getName(), securityContext);
        translation.Init();
        updateTranslationNoMerge(createTranslation, translation);
        return translation;
    }

    private void populate(CreateTranslation createTranslation, SecurityContext securityContext) {
        String translatedId = createTranslation.getTranslatedId();
        Baseclass baseclass= translatedId !=null?getByIdOrNull(translatedId,Baseclass.class,null,securityContext):null;
        createTranslation.setTranslated(baseclass);
    }

    public void validateCreate(CreateTranslation createTranslation, SecurityContext securityContext) {
        populate(createTranslation, securityContext);
        if(createTranslation.getTranslated()==null){
            throw new BadRequestException("No Baseclass with id "+createTranslation.getTranslatedId());
        }

    }


    public void validateUpdate(UpdateTranslation updateTranslation, SecurityContext securityContext) {
        populate(updateTranslation,securityContext);
    }
}
