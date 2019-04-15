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

import com.vaadin.data.Container;
import com.vaadin.ui.NativeSelect;

import eu.etaxonomy.cdm.model.name.RegistrationStatus;

/**
 * @author a.kohlbecker
 * @since Jul 4, 2018
 *
 */
public class RegistrationStatusSelect extends NativeSelect {


    private static final long serialVersionUID = 2400974680207121417L;

    Object previousValue = null;

    public RegistrationStatusSelect(){
        super();
        init();
    }

    /**
     * @param caption
     * @param options
     */
    public RegistrationStatusSelect(String caption, Collection<?> options) {
        super(caption, options);
        init();
    }

    /**
     * @param caption
     * @param dataSource
     */
    public RegistrationStatusSelect(String caption, Container dataSource) {
        super(caption, dataSource);
        init();
    }

    /**
     * @param caption
     */
    public RegistrationStatusSelect(String caption) {
        super(caption);
        init();
    }

    /**
     *
     */
    public void init() {
        addStyleName("registration-status");
        setWidth("110px");

        addValueChangeListener(e -> updateStyles());
    }


    /**
     * @param newValue
     * @param previousValue
     */
    public void updateStyles() {
        Object newValue = getValue();
        String styles = getStyleName();
        if(previousValue != null){
            styles = styles.replace("registration-status-" + ((RegistrationStatus)previousValue).name(), "");
        }
        if(newValue != null){
            setStyleName(styles + " registration-status-" + ((RegistrationStatus)newValue).name());
        }
        previousValue = newValue;
    }

}
