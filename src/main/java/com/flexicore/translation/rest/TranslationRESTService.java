package com.flexicore.translation.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interceptors.DynamicResourceInjector;
import com.flexicore.interceptors.SecurityImposer;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.security.SecurityContext;
import com.flexicore.translation.model.Translation;
import com.flexicore.translation.request.CreateTranslation;
import com.flexicore.translation.request.TranslationFiltering;
import com.flexicore.translation.request.UpdateTranslation;
import com.flexicore.translation.service.TranslationService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;

/**
 * Created by Asaf on 04/06/2017.
 */


@PluginInfo(version = 1)
@OperationsInside
@Interceptors({SecurityImposer.class, DynamicResourceInjector.class})
@Path("plugins/Translations")
@OpenAPIDefinition(tags = {
        @Tag(name = "Translations", description = "Translations Services")
})
@Tag(name = "Translations")

public class TranslationRESTService implements RestServicePlugin {

    @Inject
    @PluginInfo(version = 1)
    private TranslationService service;


    @POST
    @Produces("application/json")
    @Operation(summary = "getAllTranslations", description="returns all Translations")
    @Path("getAllTranslations")
    public PaginationResponse<Translation> getAllTranslations(
            @HeaderParam("authenticationKey") String authenticationKey,
            TranslationFiltering translationFiltering, @Context SecurityContext securityContext) {
        service.validate(translationFiltering,securityContext);
        return service.getAllTranslations(translationFiltering, securityContext);

    }


    @PUT
    @Produces("application/json")
    @Operation(summary = "updateTranslation", description="Updates Dashbaord")
    @Path("updateTranslation")
    public Translation updateTranslation(
            @HeaderParam("authenticationKey") String authenticationKey,
            UpdateTranslation updateTranslation, @Context SecurityContext securityContext) {
        Translation translationToClazz = updateTranslation.getId() != null ? service.getByIdOrNull(updateTranslation.getId(), Translation.class, null, securityContext) : null;
        if (translationToClazz == null) {
            throw new BadRequestException("no ui field with id  " + updateTranslation.getId());
        }
        updateTranslation.setTranslation(translationToClazz);
        service.validateUpdate(updateTranslation,securityContext);

        return service.updateTranslation(updateTranslation, securityContext);

    }


    @POST
    @Produces("application/json")
    @Operation(summary = "createTranslation", description="Creates Ui Field ")
    @Path("createTranslation")
    public Translation createTranslation(
            @HeaderParam("authenticationKey") String authenticationKey,
            CreateTranslation createTranslation, @Context SecurityContext securityContext) {
        service.validateCreate(createTranslation, securityContext);
        return service.createTranslation(createTranslation, securityContext);

    }


}

