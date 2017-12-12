/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import org.springframework.context.event.EventListener;
import org.vaadin.viritin.fields.LazyComboBox;

import com.vaadin.server.SystemError;
import com.vaadin.server.UserError;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

import eu.etaxonomy.cdm.api.service.DeleteResult;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProvider;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEditorAction;
import eu.etaxonomy.cdm.vaadin.event.RegistrationEditorAction;
import eu.etaxonomy.cdm.vaadin.util.CdmTitleCacheCaptionGenerator;
import eu.etaxonomy.cdm.vaadin.view.reference.ReferencePopupEditor;
import eu.etaxonomy.vaadin.mvp.AbstractEditorPresenter;
import eu.etaxonomy.vaadin.ui.navigation.NavigationEvent;
import eu.etaxonomy.vaadin.ui.view.DoneWithPopupEvent;
import eu.etaxonomy.vaadin.ui.view.DoneWithPopupEvent.Reason;

/**
 * @author a.kohlbecker
 * @since Jul 11, 2017
 *
 */
@SpringComponent
@ViewScope
public class StartRegistrationPresenter extends AbstractEditorPresenter<RegistrationDTO, StartRegistrationView> {

    private static final long serialVersionUID = 2283189121081612574L;

    private ReferencePopupEditor newReferencePopup;

    private Reference newReference;

    private boolean registrationInProgress;

    public StartRegistrationPresenter (){
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPresenterReady() {

        super.onPresenterReady();

        CdmFilterablePagingProvider<Reference, Reference> pagingProvider = new CdmFilterablePagingProvider<Reference, Reference>(
                getRepo().getReferenceService());
        CdmTitleCacheCaptionGenerator<Reference> titleCacheGenrator = new CdmTitleCacheCaptionGenerator<Reference>();
        getView().getReferenceCombobox().setCaptionGenerator(titleCacheGenrator);
        getView().getReferenceCombobox().loadFrom(pagingProvider, pagingProvider, pagingProvider.getPageSize());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleViewExit() {
        if(!registrationInProgress && newReference != null){
            logger.info("Deleting newly created Reference due to canceled registration");
            getRepo().getReferenceService().delete(newReference);
        }
        super.handleViewExit();
    }

    @EventListener(condition = "#event.type == T(eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.Action).ADD")
    public void onReferenceEditorActionAdd(ReferenceEditorAction event) {

        if(getView().getNewPublicationButton() != event.getSourceComponent()){
            return;
        }
        newReferencePopup = getNavigationManager().showInPopup(ReferencePopupEditor.class);

        newReferencePopup.withDeleteButton(true);
        newReferencePopup.loadInEditor(null);
    }

    @EventListener(condition = "#event.type == T(eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.Action).REMOVE")
    public void onReferenceEditorActionRemove(ReferenceEditorAction event) {

        if(getView().getRemoveNewPublicationButton() != event.getSourceComponent()){
            return;
        }
        DeleteResult result = getRepo().getReferenceService().delete(newReference);
        if(!result.isOk()){
            String message = "";
            for(Exception e : result.getExceptions()){
                message += e.getMessage() + "\n" + e.getStackTrace().toString() + "\n";
            }
            getView().getRemoveNewPublicationButton().setComponentError(new SystemError(message));
        }

        getView().getReferenceCombobox().setEnabled(false);

        getView().getRemoveNewPublicationButton().setVisible(false);

        getView().getNewPublicationButton().setVisible(true);
        getView().getNewPublicationLabel().setCaption(null);
        getView().getNewPublicationLabel().setVisible(false);
    }

    @EventListener
    public void onDoneWithPopupEvent(DoneWithPopupEvent event){
        if(event.getPopup() == newReferencePopup){
            if(event.getReason() == Reason.SAVE){

                newReference = newReferencePopup.getBean();

                // TODO the bean contained in the popup editor is not yet updated at this point.
                //      so re reload it using the uuid since new beans will not have an Id at this point.
                newReference = getRepo().getReferenceService().find(newReference.getUuid());

                getView().getReferenceCombobox().setValue(null);  // deselect
                getView().getReferenceCombobox().setEnabled(false);

                getView().getContinueButton().setEnabled(true);

                getView().getNewPublicationButton().setVisible(false);

                getView().getRemoveNewPublicationButton().setVisible(true);
                getView().getNewPublicationLabel().setCaption(newReference.getTitleCache());
                getView().getNewPublicationLabel().setVisible(true);
            }

            newReferencePopup = null;
        }
    }

    @EventListener(condition = "#event.type == T(eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.Action).ADD")
    public void onRegistrationEditorActionAdd(RegistrationEditorAction event) {

        if(getView().getContinueButton() != event.getSourceComponent()){
            return;
        }
        Integer referenceId = null;
        LazyComboBox<Reference> referenceCombobox = getView().getReferenceCombobox();
        referenceCombobox.commit();
        if(newReference != null){
            referenceId = newReference.getId();
       // } else if(referenceCombobox.getValue() != null) {
        } else if ( event.getEntityId() != null) { // HACKED, see view implementation
            referenceId = event.getEntityId();
        }
        if(referenceId == null){
            getView().getContinueButton().setComponentError(new UserError("Can't continue. No Reference is chosen."));
            getView().getContinueButton().setEnabled(false);
        }
        registrationInProgress = true;
        eventBus.publishEvent(new NavigationEvent(RegistrationWorksetViewBean.NAME, Integer.toString(referenceId)));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RegistrationDTO loadBeanById(Object identifier) {
        // not needed //
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveBean(RegistrationDTO bean) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void deleteBean(RegistrationDTO bean) {
        // TODO Auto-generated method stub

    }

}
