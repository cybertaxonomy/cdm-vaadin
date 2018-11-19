/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import java.util.EnumSet;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.viritin.fields.LazyComboBox;

import com.vaadin.server.SystemError;
import com.vaadin.server.UserError;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

import eu.etaxonomy.cdm.api.service.DeleteResult;
import eu.etaxonomy.cdm.api.service.dto.RegistrationDTO;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferenceType;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProvider;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProviderFactory;
import eu.etaxonomy.cdm.vaadin.event.EditorActionTypeFilter;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEditorAction;
import eu.etaxonomy.cdm.vaadin.event.RegistrationEditorAction;
import eu.etaxonomy.cdm.vaadin.ui.RegistrationUIDefaults;
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

    @Autowired
    protected CdmFilterablePagingProviderFactory pagingProviderFactory;

    public StartRegistrationPresenter (){
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onPresenterReady() {

        super.onPresenterReady();

        CdmFilterablePagingProvider<Reference, Reference> pagingProvider = pagingProviderFactory.referencePagingProvider();
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


    @EventBusListenerMethod(filter = EditorActionTypeFilter.Add.class)
    public void onReferenceEditorActionAdd(ReferenceEditorAction event) {

        if(getView() == null || getView().getNewPublicationButton() != event.getSource()){
            return;
        }

        newReferencePopup = openPopupEditor(ReferencePopupEditor.class, event);
        EnumSet<ReferenceType> refTypes = RegistrationUIDefaults.PRINTPUB_REFERENCE_TYPES.clone();
        refTypes.remove(ReferenceType.Section);
        newReferencePopup.withReferenceTypes(refTypes);

        newReferencePopup.grantToCurrentUser(EnumSet.of(CRUD.UPDATE, CRUD.DELETE));
        newReferencePopup.withDeleteButton(true);
        newReferencePopup.loadInEditor(null);
        newReferencePopup.getTypeSelect().setValue(ReferenceType.Article);
    }

    @EventBusListenerMethod(filter = EditorActionTypeFilter.Remove.class)
    public void onReferenceEditorActionRemove(ReferenceEditorAction event) {

        if(getView().getRemoveNewPublicationButton() != event.getSource()){
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

    @EventBusListenerMethod
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

    @SuppressWarnings("null")
    @EventBusListenerMethod(filter = EditorActionTypeFilter.Add.class)
    public void onRegistrationEditorActionAdd(RegistrationEditorAction event) {

        if(getView().getContinueButton() != event.getSource()){
            return;
        }

        UUID referenceUuid = null;
        LazyComboBox<Reference> referenceCombobox = getView().getReferenceCombobox();
        referenceCombobox.commit();
        if(newReference != null){
            referenceUuid = newReference.getUuid();
       // } else if(referenceCombobox.getValue() != null) {
        } else if ( event.getEntityUuid() != null) { // HACKED, see view implementation
            referenceUuid = event.getEntityUuid();
        }
        if(referenceUuid == null){
            getView().getContinueButton().setComponentError(new UserError("Can't continue. No Reference is chosen."));
            getView().getContinueButton().setEnabled(false);
        }
        registrationInProgress = true;
        viewEventBus.publish(EventScope.UI, this, new NavigationEvent(RegistrationWorksetViewBean.NAME, referenceUuid.toString()));

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
        // not needed //
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void deleteBean(RegistrationDTO bean) {
        // not needed //
    }

}
