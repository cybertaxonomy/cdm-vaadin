/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.event;

import com.vaadin.ui.AbstractField;

import eu.etaxonomy.vaadin.mvp.AbstractView;

/**
 * @author a.kohlbecker
 * @since Jun 13, 2018
 *
 */
public class FieldReplaceEvent<T> {

    private AbstractView sourceView;

    private AbstractField<T> oldField;
    private AbstractField<T> newField;

    /**
     * @param oldFiled
     * @param newField
     */
    public FieldReplaceEvent(AbstractView sourceView, AbstractField<T> oldField, AbstractField<T> newField) {
        this.sourceView = sourceView;
        this.oldField = oldField;
        this.newField = newField;
    }

    /**
     * @return the sourceView
     */
    public AbstractView getSourceView() {
        return sourceView;
    }

    /**
     * @return the oldField
     */
    public AbstractField<T> getOldField() {
        return oldField;
    }

    /**
     * @return the newField
     */
    public AbstractField<T> getNewField() {
        return newField;
    }


}
