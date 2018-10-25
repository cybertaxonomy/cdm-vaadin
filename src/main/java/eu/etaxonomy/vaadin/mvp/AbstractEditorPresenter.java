/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.mvp;

import org.hibernate.FlushMode;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.data.Property;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;

import eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction;
import eu.etaxonomy.vaadin.event.FieldReplaceEvent;
import eu.etaxonomy.vaadin.mvp.event.EditorDeleteEvent;
import eu.etaxonomy.vaadin.mvp.event.EditorPreSaveEvent;
import eu.etaxonomy.vaadin.mvp.event.EditorSaveEvent;
import eu.etaxonomy.vaadin.mvp.event.EditorViewEvent;
import eu.etaxonomy.vaadin.ui.view.PopupView;
import eu.etaxonomy.vaadin.util.PropertyIdPath;

/**
 * Presenters of this type are usually be used in conjunction with a  {@link AbstractPopupEditor}.
 * The presenter automatically handles save and delete operations. The methods {@link #saveBean(Object)} and
 * {@link AbstractEditorPresenter#deleteBean(Object)} are executed internally in turn of an
 * {@link EditorSaveEvent} or {@link EditorDeleteEvent} which are send by the {@link AbstractPopupEditor#save()}
 * or {@link AbstractPopupEditor#delete()} method.
 *
 * @author a.kohlbecker
 * @since Apr 5, 2017
 *
 */
public abstract class AbstractEditorPresenter<DTO extends Object, V extends ApplicationView<?>> extends AbstractPresenter<V> {


    private static final long serialVersionUID = -6677074110764145236L;

    FlushMode previousPreSaveEvenFlushMode = null;

    /**
     * Load the bean to be edited in the editor freshly from the persistent storage.
     * Ore create an new empty instance in case the supplied <code>identifier</code> is <code>null</code>.
     *
     * @param identifier
     * @return
     */
    protected abstract DTO loadBeanById(Object identifier);

    /**
     * Set ui elements to readonly or disabled to adapt the editor to
     * the permissions that are given to the current user etc.
     *
     * @param beanToEdit
     */
    protected void adaptToUserPermission(DTO beanToEdit) {

    }


    @EventBusListenerMethod
    public void onEditorPreSaveEvent(EditorPreSaveEvent<DTO> preSaveEvent){
        if(!isFromOwnView(preSaveEvent)){
            return;
        }
    }

    @EventBusListenerMethod
    public void onEditorSaveEvent(EditorSaveEvent<DTO> saveEvent){
        if(!isFromOwnView(saveEvent)){
            return;
        }
        DTO bean = saveEvent.getBean();
        saveBean(bean);
    }

   /**
    * @param saveEvent
    */
   @EventBusListenerMethod
   public void onEditorDeleteEvent(EditorDeleteEvent<DTO> deleteEvent){
       if(!isFromOwnView(deleteEvent)){
           return;
       }
       deleteBean(deleteEvent.getBean());
   }

    /**
     * @param saveEvent
     * @return
     */
    protected boolean isFromOwnView(EditorViewEvent saveEvent) {
        return saveEvent.getView().equals(getView());
    }

    protected Class<V> getViewType() {
        return (Class<V>) super.getView().getClass();
    }

    protected boolean isFromOwnView(AbstractEditorAction action){
        return action.getSourceView() != null && getView().equals(action.getSourceView());
    }

    protected boolean isFromOwnView(FieldReplaceEvent event){
        return event.getSourceView() != null && getView().equals(event.getSourceView());
    }

    protected abstract void saveBean(DTO bean);

    /**
     * @param bean
     */
    protected abstract void deleteBean(DTO bean);

    /**
     *
     * @param popupView
     * @return <code>null</code> in case no target field has been found for the supplied <code>popupView</code>.
     */
    protected BoundField boundTargetField(PopupView popupView) {
        Field<?> field = getNavigationManager().targetFieldOf(popupView);
        PropertyIdPath propertyIdPath = boundPropertyIdPath(field);
        if(field != null){
            return new BoundField(field, propertyIdPath);
        } else {
            return null;
        }
    }

    protected PropertyIdPath boundPropertyIdPath(Field<?> targetField){

        if(targetField == null || getView() == null){
            return null;
        }

        Field<?> boundField  = findBoundField(targetField);
        if(boundField != null) {
            return ((AbstractPopupEditor)getView()).boundPropertyIdPath(boundField);
        }
        return null;
    }

    /**
     * @param targetField
     * @return
     */
    protected Field<?> findBoundField(Field<?> targetField) {

        Component parentComponent = targetField;
        Property<?> p = null;
        while(parentComponent != null){
            if(Field.class.isAssignableFrom(parentComponent.getClass())){
                Field<?> parentField = (Field<?>)parentComponent;
                if(parentField.getPropertyDataSource() != null){
                    return parentField;
                }
            }
            parentComponent = parentComponent.getParent();
        }
        return null;
    }

}
