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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.database.PermissionDeniedException;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
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

    @Autowired
    private ICdmPermissionEvaluator permissionEvaluator;

    @Autowired
    @Qualifier("cdmRepository")
    private CdmRepository repo;

    public CdmUserHelper(){
        super();
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

}
