/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.reference;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.TransactionStatus;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ListSelect;

import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferenceFactory;
import eu.etaxonomy.cdm.persistence.query.OrderHint;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEditorAction;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityField;
import eu.etaxonomy.vaadin.mvp.AbstractCdmEditorPresenter;
import eu.etaxonomy.vaadin.ui.view.DoneWithPopupEvent;
import eu.etaxonomy.vaadin.ui.view.DoneWithPopupEvent.Reason;

/**
 * @author a.kohlbecker
 * @since Apr 5, 2017
 *
 */
public class ReferenceEditorPresenter extends AbstractCdmEditorPresenter<Reference, ReferencePopupEditorView> {

    private static final long serialVersionUID = -7926116447719010837L;

    private static final Logger logger = Logger.getLogger(ReferenceEditorPresenter.class);

    ReferencePopupEditor inReferencePopup = null;

    public ReferenceEditorPresenter() {
        logger.trace("CONTRUCTOR");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleViewEntered() {
        super.handleViewEntered();
        ListSelect select = getView().getInReferenceSelect().getSelect();
        BeanItemContainer<Reference> inReferenceSelectContainer = (BeanItemContainer<Reference>) select.getContainerDataSource();
        List<Reference> references = getRepo().getCommonService().list(Reference.class, (Integer)null, (Integer)null,
                OrderHint.ORDER_BY_TITLE_CACHE.asList(),
                Arrays.asList(new String[]{"$"}));
        inReferenceSelectContainer.addAll(references);
        select.setItemCaptionPropertyId("titleCache");
        select.markAsDirty();
    }

    /**
    *
    * @param editorAction
    */
   @EventListener(condition = "#editorAction.source != null")
   public void onReferenceEditorAction(ReferenceEditorAction editorAction){
       if(ToOneRelatedEntityField.class.isAssignableFrom(editorAction.getSource().getClass())){
           if(editorAction.isAddAction()){
               Reference reference = ReferenceFactory.newGeneric();
               getView().getTypeSelect().getValue();
               inReferencePopup = getNavigationManager().showInPopup(ReferencePopupEditor.class);
               inReferencePopup.showInEditor(reference);
           }
           if(editorAction.isEditAction()){
               TransactionStatus tx = getRepo().startTransaction(false);
               Reference reference = getRepo().getReferenceService().find(editorAction.getEntityId());
               ReferencePopupEditor popup = getNavigationManager().showInPopup(ReferencePopupEditor.class);
               popup.showInEditor(reference);
               getRepo().commitTransaction(tx);
           }
       }
   }

   @EventListener
   public void doDoneWithPopupEvent(DoneWithPopupEvent event){

       if(event.getPopup().equals(inReferencePopup)){
           if(event.getReason().equals(Reason.SAVE)){
               Reference bean = inReferencePopup.getBean();
               // TODO update items from db instead of just adding the new item
               Item selectItem = getView().getInReferenceSelect().getSelect().addItem(bean);
               getView().getInReferenceSelect().getSelect().select(selectItem);
               getView().getInReferenceSelect().getSelect().markAsDirty();
           }
           inReferencePopup = null;
       }

   }

}
