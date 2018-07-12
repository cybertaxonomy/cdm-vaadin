/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;

import eu.etaxonomy.cdm.api.utility.SecurityContextAccess;
import eu.etaxonomy.cdm.api.utility.UserHelper;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.common.User;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CdmAuthority;
import eu.etaxonomy.cdm.vaadin.permission.AbstractVaadinUserHelper;
import eu.etaxonomy.cdm.vaadin.permission.RolesAndPermissions;

/**
 * @author a.kohlbecker
 * @since May 19, 2017
 *
 */
@SpringComponent
@UIScope
public class VaadinCdmUserHelper extends AbstractVaadinUserHelper implements SecurityContextAccess, Serializable {

    private static final long serialVersionUID = -2521474709047255979L;

    public static final Logger logger = Logger.getLogger(VaadinCdmUserHelper.class);

    private UserHelper userHelper;
    @Autowired
    @Qualifier("cdmUserHelper")
    public void setUserHelper(UserHelper userHelper){
        this.userHelper = userHelper;
        userHelper.setSecurityContextAccess(this);
    }

    public VaadinCdmUserHelper(){
        super();

    }

    @Override
    public boolean userIsAutheticated() {
        return userHelper.userIsAutheticated();
    }


    @Override
    public boolean userIsAnnonymous() {
        return userHelper.userIsAnnonymous();
    }

    @Override
    public User user() {
        return userHelper.user();
    }

    @Override
    public String userName() {
        return userHelper.userName();
    }

    @Override
    public boolean userIsAdmin() {
        return userHelper.userIsAdmin();
    }

    @Override
    public boolean userIsRegistrationCurator() {
        Authentication authentication = userHelper.getAuthentication();
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
        return userHelper.userHasPermission(entity, args);
    }

    @Override
    public boolean userHasPermission(Class<? extends CdmBase> cdmType, Integer entitiyId, Object ... args){
        return userHelper.userHasPermission(cdmType, entitiyId, args);
    }

    @Override
    public boolean userHasPermission(Class<? extends CdmBase> cdmType, UUID entitiyUuid, Object ... args){
        return userHelper.userHasPermission(cdmType, entitiyUuid, args);
    }

    @Override
    public boolean userHasPermission(Class<? extends CdmBase> cdmType, Object ... args){
        return userHelper.userHasPermission(cdmType, args);
    }

    @Override
    public void logout() {
        userHelper.logout();
    }


    /**
     * @return
     *
     * FIXME is it ok to use the SecurityContextHolder or do we need to hold the context in the vaadin session?
     */
    @Override
    public SecurityContext currentSecurityContext() {
        return SecurityContextHolder.getContext();
    }


    @Override
    public CdmAuthority createAuthorityFor(String username, CdmBase cdmEntity, EnumSet<CRUD> crud, String property) {
        return userHelper.createAuthorityFor(username, cdmEntity, crud, property);
    }


    @Override
    public CdmAuthority createAuthorityFor(String username, Class<? extends CdmBase> cdmType, Integer entitiyId, EnumSet<CRUD> crud, String property) {
        return userHelper.createAuthorityFor(username, cdmType, entitiyId, crud, property);
    }


    @Override
    public CdmAuthority createAuthorityFor(String username, Class<? extends CdmBase> cdmType, UUID entitiyUuid, EnumSet<CRUD> crud, String property) {
        return userHelper.createAuthorityFor(username, cdmType, entitiyUuid, crud, property);
    }


    @Override
    public CdmAuthority createAuthorityForCurrentUser(CdmBase cdmEntity, EnumSet<CRUD> crud, String property) {
        return userHelper.createAuthorityForCurrentUser(cdmEntity, crud, property);

    }


    @Override
    public CdmAuthority createAuthorityForCurrentUser(Class<? extends CdmBase> cdmType, Integer entitiyId, EnumSet<CRUD> crud, String property) {
        return createAuthorityFor(userName(), cdmType, entitiyId, crud, property);
    }


    @Override
    public CdmAuthority createAuthorityForCurrentUser(Class<? extends CdmBase> cdmType, UUID entitiyUuid, EnumSet<CRUD> crud, String property) {
        return userHelper.createAuthorityForCurrentUser(cdmType, entitiyUuid, crud, property);
    }


    @Override
    public void removeAuthorityForCurrentUser(CdmAuthority cdmAuthority) {
        userHelper.removeAuthorityForCurrentUser(cdmAuthority);
    }


    @Override
    public void removeAuthorityForCurrentUser(String username, CdmAuthority cdmAuthority) {
        userHelper.removeAuthorityForCurrentUser(username, cdmAuthority);
    }


    @Override
    public Collection<CdmAuthority> findUserPermissions(CdmBase cdmEntity, EnumSet<CRUD> crud) {
        return userHelper.findUserPermissions(cdmEntity, crud);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSecurityContextAccess(SecurityContextAccess securityContextAccess) {
        userHelper.setSecurityContextAccess(securityContextAccess);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Authentication getAuthentication() {
        return userHelper.getAuthentication();
    }

}
