/**
 * Copyright (C) 2015 EDIT
 * European Distributed Institute of Taxonomy
 * http://www.e-taxonomy.eu
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * See LICENSE.TXT at the top of this package for the full license terms.
 */
package eu.etaxonomy.cdm.vaadin.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import eu.etaxonomy.cdm.common.URI;

/**
 * @author cmathew
 * @since 28 Apr 2015
 */
public class CdmVaadinAuthentication {

    private static final Logger logger = LogManager.getLogger();

    public static final String KEY = "key_authentication";

    Map<String, Authentication> hostAuthenticationMap = new HashMap<>();

    public void addAuthentication(URI requestSourceUri, String requestSourceContext, Authentication authentication) {
        addAuthentication(getRequestSource(requestSourceUri, requestSourceContext), authentication);
    }

    public void addAuthentication(String requestSource, Authentication authentication) {
        if(requestSource == null || requestSource.isEmpty()) {
            throw new IllegalStateException("When setting authentication, host cannot be null or empty");
        }

        if(authentication == null) {
            throw new IllegalStateException("When setting authentication, authentication object cannot be null");
        }
        hostAuthenticationMap.put(requestSource, authentication);
    }

    public boolean isAuthenticated(URI uri, String context) {
        if(uri != null && context != null && !context.isEmpty()) {
            Authentication authentication = hostAuthenticationMap.get(getRequestSource(uri, context));
            if(authentication != null) {
                return authentication.isAuthenticated();
            }
        }
        return false;
    }

    public Authentication getAuthentication(URI uri, String context){
        return hostAuthenticationMap.get(getRequestSource(uri, context));
    }

    public boolean setSecurityContextAuthentication(URI uri, String context) {
        if(uri != null && context != null && !context.isEmpty()) {
            Authentication authentication = hostAuthenticationMap.get(getRequestSource(uri, context));
            if(authentication != null && authentication.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return true;
            }
        }
        return false;
    }

    public static String getRequestSource(URI uri, String context) {
        String source = uri.getHost() + ":" + String.valueOf(uri.getPort()) + context;
        logger.warn(" request source : " + source);
        return source;
    }



}
