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

/**
 * @author a.kohlbecker
 * @since Apr 5, 2017
 *
 */
public abstract class AbstractEditorPresenter<DTO extends Object> extends AbstractPresenter {


    private static final long serialVersionUID = -6677074110764145236L;

    @Autowired
    protected ApplicationEventPublisher eventBus;

    @EventListener
    public void onEditorPreSaveEvent(EditorPreSaveEvent preSaveEvent){
        if(!preSaveEvent.getView().equals(getView())){
            return;
        }
    }

    /**
     *
     * @param saveEvent
     */
    @EventListener
    public void onEditorSaveEvent(EditorSaveEvent saveEvent){
        if(!saveEvent.getView().equals(getView())){
            return;
        }
        DTO bean = ((BeanFieldGroup<DTO>)saveEvent.getCommitEvent().getFieldBinder()).getItemDataSource().getBean();
        saveBean(bean);
    }

    protected abstract void saveBean(DTO bean);

}
