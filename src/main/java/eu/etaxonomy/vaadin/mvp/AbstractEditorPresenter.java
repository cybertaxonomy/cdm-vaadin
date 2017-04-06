/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.mvp;

import org.springframework.context.event.EventListener;

import com.vaadin.data.fieldgroup.BeanFieldGroup;

/**
 * @author a.kohlbecker
 * @since Apr 5, 2017
 *
 */
public abstract class AbstractEditorPresenter<DTO extends Object> extends AbstractPresenter {


    @SuppressWarnings("unchecked")
    @EventListener
    public void onEditorSaveEvent(EditorSaveEvent saveEvent){
        // casting to BeanFieldGroup<DTO> must be possible here!
        DTO bean = ((BeanFieldGroup<DTO>)saveEvent.getCommitEvent().getFieldBinder()).getItemDataSource().getBean();
        saveBean(bean);
    }

    protected abstract void saveBean(DTO bean);

}
