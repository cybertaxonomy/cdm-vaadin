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
import org.vaadin.viritin.fields.CaptionGenerator;
import org.vaadin.viritin.fields.LazyComboBox.FilterableCountProvider;
import org.vaadin.viritin.fields.LazyComboBox.FilterablePagingProvider;

import eu.etaxonomy.cdm.api.service.DeleteResult;
import eu.etaxonomy.cdm.api.service.pager.Pager;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferenceFactory;
import eu.etaxonomy.cdm.persistence.query.MatchMode;
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
        /*
        ListSelect select = getView().getInReferenceSelect().getSelect();
        BeanItemContainer<Reference> inReferenceSelectContainer = (BeanItemContainer<Reference>) select.getContainerDataSource();
        List<Reference> references = getRepo().getCommonService().list(Reference.class, (Integer)null, (Integer)null,
                OrderHint.ORDER_BY_TITLE_CACHE.asList(),
                Arrays.asList(new String[]{"$"}));
        inReferenceSelectContainer.addAll(references);
        select.setItemCaptionPropertyId("titleCache");
        select.markAsDirty();
        */

        getView().getInReferenceCombobox().getSelect().setCaptionGenerator(new CaptionGenerator<Reference>(){

            @Override
            public String getCaption(Reference option) {
                return option.getTitleCache();
            }

        });
        getView().getInReferenceCombobox().loadFrom(new FilterablePagingProvider<Reference>(){

            @Override
            public List<Reference> findEntities(int firstRow, String filter) {
                Pager<Reference> page = getRepo().getReferenceService().findByTitle(
                        null,
                        filter,
                        MatchMode.ANYWHERE,
                        null,
                        20,
                        firstRow,
                        OrderHint.ORDER_BY_TITLE_CACHE.asList(),
                        Arrays.asList("$")
                      );
                return page.getRecords();
            }},
            new FilterableCountProvider(){
                @Override
                public int size(String filter) {
                    Pager<Reference> page = getRepo().getReferenceService().findByTitle(
                            null,
                            filter,
                            MatchMode.ANYWHERE,
                            null,
                            1,
                            0,
                            null,
                            null
                          );
                    return page.getCount().intValue();
                }}
            , 20);
    }

    /**
    *
    * @param editorAction
    */
   @EventListener(condition = "#editorAction.sourceComponent != null")
   public void onReferenceEditorAction(ReferenceEditorAction editorAction){
       if(!isFromOwnView(editorAction)){
           return;
       }
       if(ToOneRelatedEntityField.class.isAssignableFrom(editorAction.getSourceComponent().getClass())){
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
               getView().getInReferenceCombobox().selectNewItem(bean);
           }
           if(event.getReason().equals(Reason.DELETE)){
               getView().getInReferenceCombobox().selectNewItem(null);
           }
           inReferencePopup = null;
       }

   }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DeleteResult executeServiceDeleteOperation(Reference bean) {
        return getRepo().getReferenceService().delete(bean);
    }

}
