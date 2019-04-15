/**
* Copyright (C) 2019 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.reference;

import java.util.EnumSet;

import eu.etaxonomy.cdm.model.reference.ReferenceType;
import eu.etaxonomy.vaadin.mvp.AbstractPopupEditor;
import eu.etaxonomy.vaadin.mvp.EditorFormConfigurator;

/**
 * @author a.kohlbecker
 * @since Mar 22, 2019
 *
 */
public class RegistrationUiReferenceEditorFormConfigurator implements EditorFormConfigurator<ReferencePopupEditor> {

    boolean newReferenceInstantiatorExists;

    EnumSet<ReferenceType> sectionTypes = EnumSet.of(ReferenceType.Section, ReferenceType.BookSection);

    public RegistrationUiReferenceEditorFormConfigurator(boolean newReferenceInstantiatorExists){
        this.newReferenceInstantiatorExists = newReferenceInstantiatorExists;
    }

    @Override
    public void updateComponentStates(AbstractPopupEditor<?, ?> popupEditor) {
        if(newReferenceInstantiatorExists){
            ReferencePopupEditor refEditor = (ReferencePopupEditor)popupEditor;
            boolean isSection = sectionTypes.contains(refEditor.getBean().getType());
            // editing of the inRefernce should be allowed if the reference in the editor is not a section
            refEditor.getInReferenceCombobox().setAddButtonEnabled(!isSection);
            refEditor.getInReferenceCombobox().getSelect().setEnabled(!isSection);
            // the user should be able to edit the inRefernce of a section from here
            refEditor.getInReferenceCombobox().setEditButtonEnabled(true);
            refEditor.getTypeSelect().setEnabled(false);
        }
    }

}
