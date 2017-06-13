/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event;

import java.util.Set;

import com.vaadin.ui.Component;

import eu.etaxonomy.cdm.model.name.TypeDesignationBase;
import eu.etaxonomy.vaadin.mvp.AbstractView;

/**
 * @author a.kohlbecker
 * @since Mar 22, 2017
 *
 */
public class TypeDesignationWorkingsetEditorAction extends AbstractEditorAction {

    private Set<Integer> entityIds;

    private Class<? extends TypeDesignationBase<?>> newEntityType;

    /**
     * @param edit
     * @param ids
     */
    public TypeDesignationWorkingsetEditorAction(Action action, Set<Integer> ids, Component source, AbstractView sourceView) {
        super(action, null, source, sourceView);
        this.entityIds = ids;
    }

    /**
     * Constructor which is mainly suitable for ADD actions.
     * @param
     */
    public TypeDesignationWorkingsetEditorAction(Action action, Class<? extends TypeDesignationBase<?>> newEntityType, Component source, AbstractView sourceView) {
        super(action, null, source, sourceView);
        this.newEntityType = newEntityType;
    }

    public Set<Integer> getEntityIds() {
        return entityIds;
    }


    /**
     * In case of an ADD action the receiver of the event needs to know the specific type of the
     * TypeDesignationBase instance to be created.
     *
     * @return the newEntityType
     */
    protected Class<? extends TypeDesignationBase<?>> getNewEntityType() {
        return newEntityType;
    }

    /**
     * {@inheritDoc}
     * @deprecated this method must not be used in TypeDesignationSetEditorAction
     */
    @Deprecated
    @Override
    public Integer getEntityId() {
        throw new RuntimeException("getEntityId() is not supported, use getEntityIds() instead.");
    }






}
