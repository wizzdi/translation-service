package com.flexicore.translation.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Baseclass_;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.security.SecurityContext;
import com.flexicore.translation.model.Translation;
import com.flexicore.translation.model.Translation_;
import com.flexicore.translation.request.TranslationFiltering;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
public class TranslationRepository extends AbstractRepositoryPlugin {


    public List<Translation> listAllTranslations(TranslationFiltering translationFiltering, SecurityContext securityContext) {
        CriteriaBuilder cb=em.getCriteriaBuilder();
        CriteriaQuery<Translation> q=cb.createQuery(Translation.class);
        Root<Translation> r=q.from(Translation.class);
        List<Predicate> preds=new ArrayList<>();
        addTranslationPredicates(preds,cb,r,translationFiltering);
        QueryInformationHolder<Translation> queryInformationHolder=new QueryInformationHolder<>(translationFiltering,Translation.class,securityContext);
        return getAllFiltered(queryInformationHolder,preds,cb,q,r);
    }

    private void addTranslationPredicates(List<Predicate> preds, CriteriaBuilder cb, Root<Translation> r, TranslationFiltering translationFiltering) {
        if(translationFiltering.getExternalIds()!=null && !translationFiltering.getExternalIds().isEmpty()){
            preds.add(r.get(Translation_.id).in(translationFiltering.getExternalIds()));
        }
        if(translationFiltering.getLanguageCodes()!=null && !translationFiltering.getLanguageCodes().isEmpty()){
            preds.add(r.get(Translation_.languageCode).in(translationFiltering.getLanguageCodes()));
        }

        if(translationFiltering.getTranslated()!=null && !translationFiltering.getTranslated().isEmpty()){
            Set<String> ids=translationFiltering.getTranslated().parallelStream().map(f->f.getId()).collect(Collectors.toSet());
            Join<Translation, Baseclass> join=r.join(Translation_.translated);
            preds.add(join.get(Baseclass_.id).in(ids));
        }
    }

    public long countAllTranslations(TranslationFiltering translationFiltering, SecurityContext securityContext) {
        CriteriaBuilder cb=em.getCriteriaBuilder();
        CriteriaQuery<Long> q=cb.createQuery(Long.class);
        Root<Translation> r=q.from(Translation.class);
        List<Predicate> preds=new ArrayList<>();
        addTranslationPredicates(preds,cb,r,translationFiltering);
        QueryInformationHolder<Translation> queryInformationHolder=new QueryInformationHolder<>(translationFiltering,Translation.class,securityContext);
        return countAllFiltered(queryInformationHolder,preds,cb,q,r);
    }
}
