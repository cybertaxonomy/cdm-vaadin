/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.permission;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Profile;

import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.permission.CRUD;
import eu.etaxonomy.cdm.service.UserHelperAccess;

/**
 * PermissionDebugUtils provide the following tools:
 * <ul>
 *   <li>{@link #addGainPerEntityPermissionButton(AbstractComponentContainer, Class, Integer, EnumSet)}:
 *   A button which gives a per entity authority to the current user.</li>
 * </ul>
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
@Profile("debug")
public class PermissionDebugUtils implements Serializable {

    private static final long serialVersionUID = -210079304170235459L;

    private static PermissionDebugUtils bean;

    @PostConstruct
    public void registerBean() {
        PermissionDebugUtils.bean = this;
    }

    public static PermissionDebugUtils bean() {
        return bean;
     }

    public static Button addGainPerEntityPermissionButton(AbstractComponentContainer toContainer, Class<? extends CdmBase> cdmType,
            UUID entitiyUuid, EnumSet<CRUD> crud, String property){

        PermissionDebugUtils pu = PermissionDebugUtils.bean();
        if(pu != null){
            Button button = pu.gainPerEntityPermissionButton(cdmType, entitiyUuid, crud, property);
            if(button != null){
                toContainer.addComponent(button);
            }
            return button;
        }
        return null;
    }

    public Button gainPerEntityPermissionButton(Class<? extends CdmBase> cdmType, UUID entitiyUuid, EnumSet<CRUD> crud, String property){

       Button button = new Button(FontAwesome.BOLT);
       button.addClickListener(e -> UserHelperAccess.userHelper().createAuthorityFor(UserHelperAccess.userHelper().userName(), cdmType, entitiyUuid, crud, property));
       button.addStyleName(ValoTheme.BUTTON_DANGER);
       return button;

    }


}
