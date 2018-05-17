/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.event;

import java.util.EnumSet;

import com.vaadin.ui.AbstractField;

/**
 * An Event which represents the request to start an editor to enable the
 * user to perform the <code>action</code> transported with this event.
 *
 * @author a.kohlbecker
 *
 */
public class EntityEditorActionEvent<T>  {

    private Class<T> beanType;
    private EditorActionType action;
    private AbstractField<T> field;
    private T bean;

    /**
     * @param type
     * @param entityId
     * @param field
     */
    public EntityEditorActionEvent(EditorActionType action, Class<T> beanType, AbstractField<T> field) {
        this.action = action;
        this.beanType = beanType;
        this.field = field;
    }

    /**
     *
     * @param type
     * @param entityId
     * @param field
     */
    public EntityEditorActionEvent(EditorActionType action, Class<T> beanType, T bean, AbstractField<T> field) {
        this.action = action;
        this.beanType = beanType;
        if(EnumSet.of(EditorActionType.REMOVE, EditorActionType.EDIT).contains(action) && bean == null){
            throw new NullPointerException("bean must not be null when creating an event with " + action);
        }
        this.bean = bean;
        this.field = field;
    }

    /**
     * @return the beanType
     */
    public Class<?> getBeanType() {
        return beanType;
    }

    /**
     * @return the action
     */
    public EditorActionType getAction() {
        return action;
    }

    /**
     * @return the field which contains the bean
     */
    public AbstractField<T> getSource() {
        return field;
    }

    /**
     * @return the bean
     */
    public T getBean() {
        return bean;
    }

}
