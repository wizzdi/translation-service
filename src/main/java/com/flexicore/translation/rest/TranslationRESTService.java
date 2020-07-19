package com.flexicore.translation.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.security.SecurityContext;
import com.flexicore.translation.model.Translation;
import com.flexicore.translation.request.ImportI18NRequest;
import com.flexicore.translation.request.TranslationCreate;
import com.flexicore.translation.request.TranslationFiltering;
import com.flexicore.translation.request.TranslationUpdate;
import com.flexicore.translation.response.ImportI18NResponse;
import com.flexicore.translation.service.TranslationService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.Collections;
import java.util.Map;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Asaf on 04/06/2017.
 */

@PluginInfo(version = 1)
@OperationsInside
@ProtectedREST
@Path("plugins/Translations")
@OpenAPIDefinition(tags = {@Tag(name = "Translations", description = "Translations Services")})
@Tag(name = "Translations")
@Extension
@Component
public class TranslationRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private TranslationService service;

	@POST
	@Produces("application/json")
	@Operation(summary = "getAllTranslations", description = "returns all Translations")
	@Path("getAllTranslations")
	public PaginationResponse<Translation> getAllTranslations(
			@HeaderParam("authenticationKey") String authenticationKey,
			TranslationFiltering translationFiltering,
			@Context SecurityContext securityContext) {
		service.validate(translationFiltering, securityContext);
		return service
				.getAllTranslations(translationFiltering, securityContext);

	}

	@POST
	@Produces("application/json")
	@Operation(summary = "generateI18nJson", description = "generateI18nJson")
	@Path("generateI18nJson")
	public Map<String, Object> generateI18nJson(
			@HeaderParam("authenticationKey") String authenticationKey,
			TranslationFiltering translationFiltering,
			@Context SecurityContext securityContext) {
		service.validate(translationFiltering, securityContext);
		return service.generateI18nJson(translationFiltering, securityContext);

	}

	@GET
	@Produces("application/json")
	@Operation(summary = "generateI18nJsonAll", description = "returns translations in i18n format")
	@Path("generateI18nJsonAll/{langCode}")
	public Map<String, Object> generateI18nJsonAll(
			@HeaderParam("authenticationKey") String authenticationKey,
			@PathParam("langCode") String langCode,
			@Context SecurityContext securityContext) {
		TranslationFiltering translationFiltering = new TranslationFiltering().setLanguageCodes(Collections.singleton(langCode));
		return service.generateI18nJson(translationFiltering, securityContext);

	}

	@POST
	@Produces("application/json")
	@Operation(summary = "importI18n", description = "imports i18n to system transalations")
	@Path("importI18n")
	public ImportI18NResponse importI18n(
			@HeaderParam("authenticationKey") String authenticationKey,
			ImportI18NRequest importI18NRequest,
			@Context SecurityContext securityContext) {
		service.validate(importI18NRequest,securityContext);
		return service.importI18n(importI18NRequest, securityContext);

	}

	@PUT
	@Produces("application/json")
	@Operation(summary = "updateTranslation", description = "Updates translation")
	@Path("updateTranslation")
	public Translation updateTranslation(
			@HeaderParam("authenticationKey") String authenticationKey,
			TranslationUpdate updateTranslation,
			@Context SecurityContext securityContext) {
		Translation translationToClazz = updateTranslation.getId() != null
				? service.getByIdOrNull(updateTranslation.getId(),
						Translation.class, null, securityContext) : null;
		if (translationToClazz == null) {
			throw new BadRequestException("no ui field with id  "
					+ updateTranslation.getId());
		}
		updateTranslation.setTranslation(translationToClazz);
		service.validateUpdate(updateTranslation, securityContext);

		return service.updateTranslation(updateTranslation, securityContext);

	}

	@POST
	@Produces("application/json")
	@Operation(summary = "createTranslation", description = "Creates Translation")
	@Path("createTranslation")
	public Translation createTranslation(
			@HeaderParam("authenticationKey") String authenticationKey,
			TranslationCreate createTranslation,
			@Context SecurityContext securityContext) {
		service.validateCreate(createTranslation, securityContext);
		return service.createTranslation(createTranslation, securityContext);

	}

}
