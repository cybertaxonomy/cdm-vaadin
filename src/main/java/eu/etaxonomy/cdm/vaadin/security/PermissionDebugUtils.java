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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.common.User;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CdmAuthority;
import sun.security.provider.PolicyParser.ParsingException;

/**
 * PermissionDebugUtils provide the following tools:
 * <ul>
 *   <li>{@link #addGainPerEntityPermissionButton(AbstractComponentContainer, Class, Integer, EnumSet)}:
 *   A button which gives a per entity authority to the current user.</li>
 * </ul>
 *
 *
 *
 * To enable the PermissionDebugUtils you need to activate the spring profile <code>debug</code>. You can add
 * <code>-Dspring.profiles.active=debug</code> to the command starting the jvm
 * or set this as an environment variable.
 *
 * @author a.kohlbecker
 * @since Oct 11, 2017
 *
 */
@SpringComponent
@UIScope
@Profile("debug")
public class PermissionDebugUtils {


    private final static Logger logger = Logger.getLogger(PermissionDebugUtils.class);

    public static final String VADDIN_SESSION_KEY = "PERMISSION_DEBUG_UTILS";

    public static final String SYSTEM_PROP_KEY = "GainPerEntityPermissionButtons";


    @Autowired
    @Qualifier("cdmRepository")
    private CdmRepository repo;

    public PermissionDebugUtils() {
        VaadinSession.getCurrent().setAttribute(VADDIN_SESSION_KEY, this);
    }

    public static PermissionDebugUtils fromSession() {
        return (PermissionDebugUtils)VaadinSession.getCurrent().getAttribute(VADDIN_SESSION_KEY);
     }

    public Button addGainPerEntityPermissionButton(AbstractComponentContainer toContainer, Class<? extends CdmBase> cdmType, Integer entitiyId, EnumSet<CRUD> crud){
        Button button = gainPerEntityPermissionButton(cdmType, entitiyId, crud);
        if(button != null){
            toContainer.addComponent(button);
        }
        return button;
    }

    public Button gainPerEntityPermissionButton(Class<? extends CdmBase> cdmType, Integer entitiyId, EnumSet<CRUD> crud){

       Button button = new Button(FontAwesome.BOLT);
       button.addClickListener(e -> createAuthority(cdmType, entitiyId, crud));
       button.addStyleName(ValoTheme.BUTTON_DANGER);
       return button;

    }

    /**
     * @param cdmType
     * @param entitiyId
     * @param crud
     * @return
     */
    private void createAuthority(Class<? extends CdmBase> cdmType, Integer entitiyId, EnumSet<CRUD> crud) {
        String username = UserHelper.fromSession().userName();
        UserDetails userDetails = repo.getUserService().loadUserByUsername(username);
        if(userDetails != null){
            User user = (User)userDetails;
            CdmBase entity = repo.getCommonService().find(cdmType, entitiyId);
            CdmAuthority authority = new CdmAuthority(entity, crud);
            try {
                user.getGrantedAuthorities().add(authority.asNewGrantedAuthority());
            } catch (ParsingException e) {
                throw new RuntimeException(e);
            }
            repo.getUserService().saveOrUpdate(user);
            Authentication authentication = new PreAuthenticatedAuthenticationToken(user, user.getPassword(), user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }
}
