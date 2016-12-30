package org.bitbucket.askllc.fermenter.cookbook.domain.service.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;

import org.bitbucket.askllc.fermenter.cookbook.domain.transfer.json.ObjectMapperManager;

@Provider
@Consumes({ MediaType.APPLICATION_JSON, "application/*+json", "text/json" })
@Produces({ MediaType.APPLICATION_JSON, "application/*+json", "text/json" })
public class JacksonObjectMapperResteasyProvider extends ResteasyJackson2Provider {

	private ObjectMapper objectMapper = ObjectMapperManager.getObjectMapper();

	/**
	 * {@inheritDoc}
	 */
	public ObjectMapper locateMapper(Class<?> type, MediaType mediaType) {
		return objectMapper;
	}

}