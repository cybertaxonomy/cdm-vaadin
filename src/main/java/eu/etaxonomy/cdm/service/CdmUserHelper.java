/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.service;

import java.util.EnumSet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.api.application.RunAsAuthenticator;
import eu.etaxonomy.cdm.database.PermissionDeniedException;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.common.User;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CdmAuthority;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CdmAuthorityParsingException;
import eu.etaxonomy.cdm.persistence.hibernate.permission.ICdmPermissionEvaluator;
import eu.etaxonomy.cdm.persistence.hibernate.permission.Role;
import eu.etaxonomy.cdm.vaadin.security.RolesAndPermissions;
import eu.etaxonomy.cdm.vaadin.security.VaadinUserHelper;

/**
 * @author a.kohlbecker
 * @since May 19, 2017
 *
 */
@SpringComponent
@UIScope
public class CdmUserHelper extends VaadinUserHelper {

    public static final Logger logger = Logger.getLogger(CdmUserHelper.class);

    @Autowired
    private ICdmPermissionEvaluator permissionEvaluator;

    @Autowired
    @Qualifier("cdmRepository")
    private CdmRepository repo;

    @Autowired
    @Qualifier("runAsAuthenticationProvider")
    AuthenticationProvider runAsAuthenticationProvider;

    RunAsAuthenticator runAsAutheticator = new RunAsAuthenticator();

    public CdmUserHelper(){
        super();
        runAsAutheticator.setRunAsAuthenticationProvider(runAsAuthenticationProvider);

    }

    @Override
    public boolean userIsAutheticated() {
        Authentication authentication = getAuthentication();
        if(authentication != null){
            return authentication.isAuthenticated();
        }
        return false;
    }


    @Override
    public boolean userIsAnnonymous() {
        Authentication authentication = getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && authentication instanceof AnonymousAuthenticationToken;
    }

    @Override
    public String userName() {
        Authentication authentication = getAuthentication();
        if(authentication != null) {
            return authentication.getName();
        }
        return null;
    }

    @Override
    public boolean userIsAdmin() {
        Authentication authentication = getAuthentication();
        if(authentication != null) {
            return authentication.getAuthorities().stream().anyMatch(a -> {
                return a.getAuthority().equals(Role.ROLE_ADMIN.getAuthority());
            });
        }
        return false;
    }

    @Override
    public boolean userIsRegistrationCurator() {
        Authentication authentication = getAuthentication();
        if(authentication != null) {
            return authentication.getAuthorities().stream().anyMatch(a -> {
                return a.equals(RolesAndPermissions.ROLE_CURATION)
                        // doing faster regex check here instreas of using CdmAuthoritiy.fromString()
                        || a.getAuthority().matches("^Registration\\.\\[.*UPDATE");
            });
        }
        return false;
    }

    @Override
    public boolean userHasPermission(CdmBase entity, Object ... args){
        EnumSet<CRUD> crudSet = crudSetFromArgs(args);
        try {
            return permissionEvaluator.hasPermission(getAuthentication(), entity, crudSet);
        } catch (PermissionDeniedException e){
            //IGNORE
        }
        return false;
    }

    @Override
    public boolean userHasPermission(Class<? extends CdmBase> cdmType, Integer entitiyId, Object ... args){
        EnumSet<CRUD> crudSet = crudSetFromArgs(args);
        try {
            CdmBase entity = repo.getCommonService().find(cdmType, entitiyId);
            return permissionEvaluator.hasPermission(getAuthentication(), entity, crudSet);
        } catch (PermissionDeniedException e){
            //IGNORE
        }
        return false;
    }

    @Override
    public boolean userHasPermission(Class<? extends CdmBase> cdmType, Object ... args){
        EnumSet<CRUD> crudSet = crudSetFromArgs(args);
        try {
            return permissionEvaluator.hasPermission(getAuthentication(), cdmType, crudSet);
        } catch (PermissionDeniedException e){
            //IGNORE
        }
        return false;
    }

    public void logout() {
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(null);
        SecurityContextHolder.clearContext();
    }


    private EnumSet<CRUD> crudSetFromArgs(Object[] args) {
        EnumSet<CRUD> crudSet = EnumSet.noneOf(CRUD.class);
        for(int i = 0; i < args.length; i++){
            try {
                crudSet.add(CRUD.valueOf(args[i].toString()));
            } catch (Exception e){
                throw new IllegalArgumentException("could not add " + args[i], e);
            }
        }
        return crudSet;
    }


    /**
     * @return
     *
     * FIXME is it ok to use the SecurityContextHolder or do we need to hold the context in the vaadin session?
     */
    private SecurityContext currentSecurityContext() {
        return SecurityContextHolder.getContext();
    }

    /**
     * @return
     */
    private Authentication getAuthentication() {
        return currentSecurityContext().getAuthentication();
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public CdmAuthority createAuthorityFor(String username, CdmBase cdmEntity, EnumSet<CRUD> crud, String property) {
        UserDetails userDetails = repo.getUserService().loadUserByUsername(username);
        boolean newAuthorityAdded = false;
        CdmAuthority authority = null;
        if(userDetails != null){
            runAsAutheticator.runAsAuthentication(Role.ROLE_USER_MANAGER);
            User user = (User)userDetails;
            authority = new CdmAuthority(cdmEntity, property, crud);
            try {
                newAuthorityAdded = user.getGrantedAuthorities().add(authority.asNewGrantedAuthority());
            } catch (CdmAuthorityParsingException e) {
                throw new RuntimeException(e);
            }
            repo.getSession().flush();
            runAsAutheticator.restoreAuthentication();
            logger.debug("new authority for " + username + ": " + authority.toString());
            Authentication authentication = new PreAuthenticatedAuthenticationToken(user, user.getPassword(), user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.debug("security context refreshed with user " + username);
        }
        return newAuthorityAdded ? authority : null;

    }

    /**
     * @param username
     * @param cdmType
     * @param entitiyId
     * @param crud
     * @return
     */
    @Override
    public CdmAuthority createAuthorityFor(String username, Class<? extends CdmBase> cdmType, Integer entitiyId, EnumSet<CRUD> crud, String property) {

        CdmBase cdmEntity = repo.getCommonService().find(cdmType, entitiyId);
        return createAuthorityFor(username,cdmEntity, crud, property);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CdmAuthority createAuthorityForCurrentUser(CdmBase cdmEntity, EnumSet<CRUD> crud, String property) {
        return createAuthorityFor(userName(), cdmEntity, crud, property);

    }

    /**
     * @param cdmType
     * @param entitiyId
     * @param crud
     * @return
     */
    @Override
    public CdmAuthority createAuthorityForCurrentUser(Class<? extends CdmBase> cdmType, Integer entitiyId, EnumSet<CRUD> crud, String property) {
        return createAuthorityFor(userName(), cdmType, entitiyId, crud, property);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAuthorityForCurrentUser(CdmAuthority cdmAuthority) {
        removeAuthorityForCurrentUser(userName(), cdmAuthority);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAuthorityForCurrentUser(String username, CdmAuthority cdmAuthority) {

        UserDetails userDetails = repo.getUserService().loadUserByUsername(username);
        if(userDetails != null){
            runAsAutheticator.runAsAuthentication(Role.ROLE_USER_MANAGER);
            User user = (User)userDetails;
            user.getGrantedAuthorities().remove(cdmAuthority);
            repo.getSession().flush();
            runAsAutheticator.restoreAuthentication();
            Authentication authentication = new PreAuthenticatedAuthenticationToken(user, user.getPassword(), user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.debug("security context refreshed with user " + username);
        }

    }

}
