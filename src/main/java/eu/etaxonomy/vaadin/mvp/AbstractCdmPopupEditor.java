/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.mvp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Layout.MarginHandler;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.vaadin.view.PerEntityAuthorityGrantingEditor;

/**
 * @author a.kohlbecker
 * @since May 5, 2017
 *
 */
public abstract class AbstractCdmPopupEditor<DTO extends CdmBase, P extends AbstractCdmEditorPresenter<DTO, ? extends ApplicationView>>
    extends AbstractPopupEditor<DTO, P> implements PerEntityAuthorityGrantingEditor {

    private static final long serialVersionUID = -5025937489746256070L;

    private boolean isAdvancedMode = false;

    private List<Component> advancedModeComponents = new ArrayList<>();

    private Button advancedModeButton;

    /**
     * The supplied layout will be set to full size, to avoid problems with
     * automatic resizing of the inner content.
     *
     *
     * @param layout
     * @param dtoType
     */
    public AbstractCdmPopupEditor(Layout layout, Class<DTO> dtoType) {
        super(layout, dtoType);
        if(MarginHandler.class.isAssignableFrom(getFieldLayout().getClass())){
            ((MarginHandler)getFieldLayout()).setMargin(new MarginInfo(false, true, true, true));
        }
    }

    /**
     * @return the isAdvancedMode
     */
    public boolean isAdvancedMode() {
        return isAdvancedMode;
    }

    /**
     * @param isAdvancedMode the isAdvancedMode to set
     */
    public void setAdvancedMode(boolean isAdvancedMode) {
        this.isAdvancedMode = isAdvancedMode;
        advancedModeComponents.forEach(c -> c.setVisible(isAdvancedMode));
    }

    public void setAdvancedModeEnabled(boolean activate){
        if(activate && advancedModeButton == null){
            advancedModeButton = new Button(FontAwesome.WRENCH); // FontAwesome.FLASK
            advancedModeButton.setIconAlternateText("Advanced mode");
            advancedModeButton.addStyleName(ValoTheme.BUTTON_TINY);
            toolBarButtonGroupAdd(advancedModeButton);
            advancedModeButton.addClickListener(e -> {
                setAdvancedMode(!isAdvancedMode);
                }
            );

        } else if(advancedModeButton != null) {
            toolBarButtonGroupRemove(advancedModeButton);
            advancedModeButton = null;
        }
    }

    public void registerAdvancedModeComponents(Component ... c){
        advancedModeComponents.addAll(Arrays.asList(c));
    }


    @Override
    public void grantToCurrentUser(EnumSet<CRUD> crud){
        getPresenter().setGrantsForCurrentUser(crud);
    }



}
