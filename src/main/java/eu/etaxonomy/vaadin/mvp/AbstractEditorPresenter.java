/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.mvp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

import com.vaadin.data.fieldgroup.BeanFieldGroup;

import eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction;
import eu.etaxonomy.vaadin.mvp.event.EditorPreSaveEvent;
import eu.etaxonomy.vaadin.mvp.event.EditorSaveEvent;
import eu.etaxonomy.vaadin.mvp.event.EditorViewEvent;

/**
 * @author a.kohlbecker
 * @since Apr 5, 2017
 *
 */
public abstract class AbstractEditorPresenter<DTO extends Object, V extends ApplicationView<?>> extends AbstractPresenter<V> {


    private static final long serialVersionUID = -6677074110764145236L;

    @Autowired
    protected ApplicationEventPublisher eventBus;

    @EventListener
    public void onEditorPreSaveEvent(EditorPreSaveEvent preSaveEvent){
        if(!isFromOwnView(preSaveEvent)){
            return;
        }
    }

    /**
     *
     * @param saveEvent
     */
    @EventListener
    public void onEditorSaveEvent(EditorSaveEvent saveEvent){
        if(!isFromOwnView(saveEvent)){
            return;
        }
        DTO bean = ((BeanFieldGroup<DTO>)saveEvent.getCommitEvent().getFieldBinder()).getItemDataSource().getBean();
        saveBean(bean);
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

    protected abstract void saveBean(DTO bean);

}
