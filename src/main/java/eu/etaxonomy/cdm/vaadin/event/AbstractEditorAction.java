/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event;

/**
 * @author a.kohlbecker
 * @since Mar 22, 2017
 *
 */
public abstract class AbstractEditorAction extends AbstractEntityEvent<AbstractEditorAction.Type> {

    public enum Type {
        ADD,
        EDIT,
        REMOVE;
    }

    public AbstractEditorAction(Type type) {
        super(type, null);
    }

    /**
     * @param type
     * @param citationId
     */
    public AbstractEditorAction(Type type, Integer entityId) {
        super(type, entityId);
    }

    public Type getActionType() {
        return type;
    }

    public boolean isAddAction() {
        return type.equals(Type.ADD);
    }
    public boolean isEditAction() {
        return type.equals(Type.EDIT);
    }
    public boolean isRemoveAction() {
        return type.equals(Type.REMOVE);
    }


}
