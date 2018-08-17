/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.FontIcon;

/**
 * @author a.kohlbecker
 * @since Jul 6, 2018
 *
 */
public enum ButtonFactory {

    CREATE_NEW("New", FontIcons.ICON_CREATE_NEW),
    DELETE("Delete", FontAwesome.TRASH),
    EDIT_ITEM("Edit", FontAwesome.EDIT),
    ADD_ITEM("Add item", FontAwesome.PLUS),
    REMOVE_ITEM("Remove item", FontAwesome.MINUS),
    REMOVE_ALL_ITEMS("Remove", FontAwesome.REMOVE);

    private String description;
    private FontIcon icon;

    ButtonFactory(String description, FontIcon icon){
        this.description  = description;
        this.icon = icon;
    }


    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the icon
     */
    public FontIcon getIcon() {
        return icon;
    }

    public com.vaadin.ui.Button createButton(){
        com.vaadin.ui.Button button = new com.vaadin.ui.Button(icon);
        button.setDescription(description);
        return button;
    }

}
