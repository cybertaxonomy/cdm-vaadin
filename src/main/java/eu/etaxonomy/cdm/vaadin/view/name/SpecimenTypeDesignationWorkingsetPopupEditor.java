/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.name;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import com.vaadin.ui.GridLayout;

import eu.etaxonomy.cdm.vaadin.security.AccessRestrictedView;
import eu.etaxonomy.cdm.vaadin.util.converter.TypeDesignationConverter.TypeDesignationWorkingSet;
import eu.etaxonomy.vaadin.mvp.AbstractPopupEditor;

/**
 * @author a.kohlbecker
 * @since May 15, 2017
 *
 */
public class SpecimenTypeDesignationWorkingsetPopupEditor extends AbstractPopupEditor<TypeDesignationWorkingSet, SpecimenTypeDesignationWorkingsetEditorPresenter>
    implements SpecimenTypeDesignationWorkingsetPopupEditorView, AccessRestrictedView {

    private static final long serialVersionUID = 5418275817834009509L;

    public SpecimenTypeDesignationWorkingsetPopupEditor() {
        super(new GridLayout(), TypeDesignationWorkingSet.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initContent() {

        GridLayout grid = (GridLayout)getFieldLayout();
        grid.setSpacing(true);
        grid.setMargin(true);

        //TODO typifyingAuthors

        // FieldUnit


        //


    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getWindowCaption() {
        return "Specimen typedesignations editor";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void focusFirst() {
        // none
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDefaultComponentStyles() {
        return "tiny";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean allowAnonymousAccess() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Collection<GrantedAuthority>> allowedGrantedAuthorities() {
        return null;
    }
}
