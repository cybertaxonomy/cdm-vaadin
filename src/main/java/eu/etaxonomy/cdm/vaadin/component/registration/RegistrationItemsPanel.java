/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.registration;

import java.util.Collection;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;

import eu.etaxonomy.cdm.api.service.dto.RegistrationWrapperDTO;
import eu.etaxonomy.cdm.model.ICdmEntityUuidCacher;
import eu.etaxonomy.vaadin.mvp.AbstractView;

/**
 * @author a.kohlbecker
 * @since Feb 6, 2018
 */
public class RegistrationItemsPanel extends Panel {

    private static final long serialVersionUID = -3763419770580196601L;

    public RegistrationItemsPanel(AbstractView<?,?> parentView, String caption, Collection<RegistrationWrapperDTO> regDtos, ICdmEntityUuidCacher cache){

        super(caption);

        Layout container = new CssLayout();
        container.setWidth(100, Unit.PERCENTAGE);
        setContent(container);
        for(RegistrationWrapperDTO dto : regDtos){
            RegistrationItem item = new RegistrationItem(dto, parentView, cache);
            container.addComponent(item);
        }
        setWidth(100, Unit.PERCENTAGE);
    }



}
