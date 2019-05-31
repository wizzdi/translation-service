package com.flexicore.translation.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.security.SecurityContext;
import com.flexicore.translation.model.Translation;
import com.flexicore.translation.request.TranslationFiltering;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

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
