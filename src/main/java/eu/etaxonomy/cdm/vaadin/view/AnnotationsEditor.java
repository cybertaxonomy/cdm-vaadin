/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view;

import eu.etaxonomy.cdm.model.common.AnnotationType;
import eu.etaxonomy.cdm.vaadin.component.common.FilterableAnnotationsField;

/**
 * @author a.kohlbecker
 * @since Jun 25, 2018
 *
 */
public interface AnnotationsEditor {

    /**
     *
     * @return the EDITABLE_ANOTATION_TYPES
     */
    public AnnotationType[] getEditableAnotationTypes();

    /**
     *
     * @param EDITABLE_ANOTATION_TYPES the EDITABLE_ANOTATION_TYPES to set
     */
    public void setEditableAnotationTypes(AnnotationType... editableAnotationTypes);

    /**
     * @return
     */
    public FilterableAnnotationsField getAnnotationsField();

}