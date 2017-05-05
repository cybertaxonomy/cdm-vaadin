/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.mvp;

import org.hibernate.Session;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.TransactionStatus;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.util.BeanItem;

import eu.etaxonomy.cdm.model.common.CdmBase;

/**
 * @author a.kohlbecker
 * @since Apr 5, 2017
 *
 */
public abstract class AbstractCdmEditorPresenter<DTO extends CdmBase> extends AbstractEditorPresenter<DTO> {

    private static final long serialVersionUID = 2218185546277084261L;

    TransactionStatus tx = null;

    @Override
    @EventListener
    public void onEditorPreSaveEvent(EditorPreSaveEvent preSaveEvent){
        tx = getRepo().startTransaction(true);
        // merge the bean and update the fieldGroup with the merged bean, so that updating
        // of field values in turn of the commit are can not cause LazyInitializytionExeptions
        // the bean still has the original values at this point
        mergedBean(preSaveEvent.getCommitEvent());

    }

    @Override
    @EventListener
    public void onEditorSaveEvent(EditorSaveEvent saveEvent){
        // the bean is now updated with the changes made by the user
        // merge the bean into the session, ...
        DTO bean = mergedBean(saveEvent.getCommitEvent());
        getRepo().getCommonService().saveOrUpdate(bean);
        getSession().flush();
        getRepo().commitTransaction(tx);
        tx = null;
    }

    /**
     * Obtains the bean from the fieldGroup, merges the bean into the session and
     * updates the fieldGroup with the merged bean.
     *
     * @param CommitEvent
     * @return The bean merged to the session
     */
    private DTO mergedBean(CommitEvent commitEvent) {
        // using just some service to get hold of the session
        Session session = getSession();
        @SuppressWarnings("unchecked")
        BeanItem<DTO> itemDataSource = ((BeanFieldGroup<DTO>)commitEvent.getFieldBinder()).getItemDataSource();
        DTO bean = itemDataSource.getBean();
        @SuppressWarnings("unchecked")
        DTO mergedBean = (DTO) session.merge(bean);
        itemDataSource.setBean(mergedBean);
        return mergedBean;
    }

    /**
     * @return
     */
    private Session getSession() {
        return getRepo().getUserService().getSession();
    }

    @Override
    protected final void saveBean(DTO bean){
        // blank implementation, since this is not needed in this or any sub class
    }

}
