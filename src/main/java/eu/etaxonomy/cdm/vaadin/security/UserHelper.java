/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.security;

import java.util.EnumSet;

import com.vaadin.server.VaadinSession;

import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.common.User;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CdmAuthority;

/**
 * UserHelper interface. Implementations should use the {@link #VADDIN_SESSION_KEY} to auto registers
 * in the VaadinSession.
 *
 * @author a.kohlbecker
 * @since May 23, 2017
 *
 */
public interface UserHelper {

    public static final String VADDIN_SESSION_KEY = "USER_HELPER";

    /**
     * Static accessor method to obtain the auto-registered UserHelper-Bean from the
     * VaadinSession.
     *
     * @return
     */
    public static UserHelper fromSession() {
       return (UserHelper)VaadinSession.getCurrent().getAttribute(VADDIN_SESSION_KEY);
    }

    boolean userHasPermission(Class<? extends CdmBase> cdmType, Integer entitiyId, Object ... args);

    boolean userHasPermission(Class<? extends CdmBase> cdmType, Object ... args);

    boolean userHasPermission(CdmBase entity, Object ... args);

    boolean userIsRegistrationCurator();

    boolean userIsAdmin();

    User user();

    String userName();

    boolean userIsAnnonymous();

    boolean userIsAutheticated();

    /**
     *
     * @param username
     * @param cdmEntity
     * @param crud
     * @param property
     * @return the newly created CdmAuthority only if a new CdmAuthority has been added to the user otherwise
     * <code>null</code> in case the operation failed of if the user was already granted with this authority.
     */
    public CdmAuthority createAuthorityFor(String username, CdmBase cdmEntity, EnumSet<CRUD> crud, String property);

    /**
     *
     * @param username
     * @param cdmType
     * @param entitiyId
     * @param crud
     * @param property
     * @return the newly created CdmAuthority only if a new CdmAuthority has been added to the user otherwise
     * <code>null</code> in case the operation failed of if the user was already granted with this authority.
     */
    public CdmAuthority createAuthorityFor(String username, Class<? extends CdmBase> cdmType, Integer entitiyId, EnumSet<CRUD> crud, String property);

    /**
     * @param cdmType
     * @param entitiyId
     * @param crud
     * @return the newly created CdmAuthority only if a new CdmAuthority has been added to the user otherwise
     * <code>null</code> in case the operation failed of if the user was already granted with this authority.
     */
    public CdmAuthority createAuthorityForCurrentUser(Class<? extends CdmBase> cdmType, Integer entitiyId, EnumSet<CRUD> crud, String property);

    /**
     * @param cdmType
     * @param entitiyId
     * @param crud
     * @return the newly created CdmAuthority only if a new CdmAuthority has been added to the user otherwise
     * <code>null</code> in case the operation failed of if the user was already granted with this authority.
     */
    public CdmAuthority createAuthorityForCurrentUser(CdmBase cdmEntity, EnumSet<CRUD> crud, String property);

    /**
     * @param newAuthority
     */
    public void removeAuthorityForCurrentUser(CdmAuthority newAuthority);

    /**
     * @param username
     * @param newAuthority
     */
    public void removeAuthorityForCurrentUser(String username, CdmAuthority newAuthority);


}
