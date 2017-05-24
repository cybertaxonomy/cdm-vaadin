/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.component;

import com.vaadin.data.Container;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author a.kohlbecker
 * @since May 24, 2017
 *
 */
public class RelatedEntityListSelect<V extends Object> extends CompositeCustomField<V> {

    private static final long serialVersionUID = 6277565876657520311L;

    public static final String PRIMARY_STYLE = "v-related-entity-list-select";

    private Class<V> type;

    private CssLayout container = new CssLayout();

    private ListSelect select;

    private Button newButton = new Button(FontAwesome.PLUS);
    private Button editButton  = new Button(FontAwesome.EDIT);

    public RelatedEntityListSelect(String caption, Class<V> type, Container dataSource){
        this.type = type;
        select = new ListSelect(caption, dataSource);
        addStyledComponents(select, newButton, editButton);
        addSizedComponent(select);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Component initContent() {
        container.addComponents(select, newButton, editButton);
        setPrimaryStyleName(PRIMARY_STYLE);
        addDefaultStyles();
        return container;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends V> getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addDefaultStyles() {
        container.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldGroup getFieldGroup() {
        return null;
    }

    /**
     * @return the select
     */
    public ListSelect getSelect() {
        return select;
    }

    /**
     * @return the newButton
     */
    public Button getNewButton() {
        return newButton;
    }

    /**
     * @return the editButton
     */
    public Button getEditButton() {
        return editButton;
    }





}
