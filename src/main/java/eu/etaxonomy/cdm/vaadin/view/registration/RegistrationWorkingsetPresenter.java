/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.TransactionStatus;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import eu.etaxonomy.cdm.api.service.INameService;
import eu.etaxonomy.cdm.api.service.IRegistrationService;
import eu.etaxonomy.cdm.api.service.idminter.IdentifierMinter.Identifier;
import eu.etaxonomy.cdm.api.service.idminter.RegistrationIdentifierMinter;
import eu.etaxonomy.cdm.model.common.User;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.RegistrationStatus;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.name.TaxonNameFactory;
import eu.etaxonomy.cdm.model.name.TypeDesignationBase;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.persistence.hibernate.permission.Operation;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProvider;
import eu.etaxonomy.cdm.service.CdmStore;
import eu.etaxonomy.cdm.service.IRegistrationWorkingSetService;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEditorAction;
import eu.etaxonomy.cdm.vaadin.event.RegistrationEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ShowDetailsEvent;
import eu.etaxonomy.cdm.vaadin.event.TaxonNameEditorAction;
import eu.etaxonomy.cdm.vaadin.event.TypeDesignationWorkingsetEditorAction;
import eu.etaxonomy.cdm.vaadin.event.registration.RegistrationWorkingsetAction;
import eu.etaxonomy.cdm.vaadin.model.EntityReference;
import eu.etaxonomy.cdm.vaadin.model.registration.RegistrationWorkingSet;
import eu.etaxonomy.cdm.vaadin.security.UserHelper;
import eu.etaxonomy.cdm.vaadin.util.CdmTitleCacheCaptionGenerator;
import eu.etaxonomy.cdm.vaadin.util.converter.TypeDesignationSetManager.TypeDesignationWorkingSetType;
import eu.etaxonomy.cdm.vaadin.view.name.SpecimenTypeDesignationWorkingsetPopupEditor;
import eu.etaxonomy.cdm.vaadin.view.name.TaxonNamePopupEditor;
import eu.etaxonomy.cdm.vaadin.view.name.TypeDesignationWorkingsetEditorIdSet;
import eu.etaxonomy.cdm.vaadin.view.reference.ReferencePopupEditor;
import eu.etaxonomy.vaadin.mvp.AbstractPresenter;
import eu.etaxonomy.vaadin.ui.view.DoneWithPopupEvent;
import eu.etaxonomy.vaadin.ui.view.DoneWithPopupEvent.Reason;

/**
 * @author a.kohlbecker
 * @since Mar 3, 2017
 *
 */
@SpringComponent
@ViewScope
public class RegistrationWorkingsetPresenter extends AbstractPresenter<RegistrationWorkingsetView> {

    private static final long serialVersionUID = 1L;

    @Autowired
    private IRegistrationWorkingSetService workingSetService;

    @Autowired
    private RegistrationIdentifierMinter minter;

    /**
     * @return the workingSetService
     */
    public IRegistrationWorkingSetService getWorkingSetService() {
        return workingSetService;
    }

    private RegistrationWorkingSet workingset;

    private TaxonName newTaxonNameForRegistration = null;

    private RegistrationDTO newRegistrationDTOWithExistingName;


    /**
     *
     */
    public RegistrationWorkingsetPresenter() {
    }


    /**
     * Always create a new Store
     *
     * @return
     */
    protected CdmStore<Registration, IRegistrationService> getRegistrationStore(){
        return new CdmStore<Registration, IRegistrationService>(getRepo(), getRepo().getRegistrationService());
    }

    /**
     * Always create a new Store
     *
     * @return
     */
    protected CdmStore<TaxonName, INameService> getTaxonNameStore(){
        return new  CdmStore<TaxonName, INameService>(getRepo(), getRepo().getNameService());
    }


    /**
     * @param taxonNameId
     * @return
     */
    protected Registration createNewRegistrationForName(Integer taxonNameId) {
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // move into RegistrationWorkflowStateMachine
        TransactionStatus txStatus = getRepo().startTransaction();
        long identifier = System.currentTimeMillis();

        Identifier<String> identifiers = minter.mint();
        if(identifiers.getIdentifier() == null){
            throw new RuntimeException("RegistrationIdentifierMinter configuration incomplete.");
        }
        Registration reg = Registration.NewInstance(
                identifiers.getIdentifier(),
                identifiers.getLocalId(),
                taxonNameId != null ? getRepo().getNameService().find(taxonNameId) : null,
                null);
        Authentication authentication = currentSecurityContext().getAuthentication();
        reg.setSubmitter((User)authentication.getPrincipal());
        EntityChangeEvent event = getRegistrationStore().saveBean(reg);
        UserHelper.fromSession().createAuthorityForCurrentUser(Registration.class, event.getEntityId(), Operation.UPDATE, RegistrationStatus.PREPARATION.name());
        getRepo().commitTransaction(txStatus);
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        return getRepo().getRegistrationService().find(event.getEntityId());
    }


    /**
     * @param doReload TODO
     *
     */
    protected void refreshView(boolean doReload) {
        if(doReload){
            loadWorkingSet(workingset.getCitationId());
        }
        getView().setWorkingset(workingset);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void handleViewEntered() {

        super.handleViewEntered();

        loadWorkingSet(getView().getCitationID());
        getView().setWorkingset(workingset);

        CdmFilterablePagingProvider<TaxonName, TaxonName> pagingProvider = new CdmFilterablePagingProvider<TaxonName, TaxonName>(
                getRepo().getNameService());
        CdmTitleCacheCaptionGenerator<TaxonName> titleCacheGenrator = new CdmTitleCacheCaptionGenerator<TaxonName>();
        getView().getAddExistingNameCombobox().setCaptionGenerator(titleCacheGenrator);
        getView().getAddExistingNameCombobox().loadFrom(pagingProvider, pagingProvider, pagingProvider.getPageSize());
    }


    /**
     * @param referenceID
     */
    protected void loadWorkingSet(Integer referenceID) {
        try {
            workingset = getWorkingSetService().loadWorkingSetByReferenceID(referenceID);
        } catch (RegistrationValidationException error) {
            logger.error(error);
            Window errorDialog = new Window("Validation Error");
            errorDialog.setModal(true);
            VerticalLayout subContent = new VerticalLayout();
            subContent.setMargin(true);
            errorDialog.setContent(subContent);
            subContent.addComponent(new Label(error.getMessage()));
            UI.getCurrent().addWindow(errorDialog);
        }
        if(workingset == null || workingset.getCitationId() == null){
            Reference citation = getRepo().getReferenceService().find(referenceID);
            workingset = new RegistrationWorkingSet(citation);
        }
    }

    @EventListener(condition = "#event.type == T(eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.Action).ADD && #event.sourceComponent == null")
    public void onReferenceEditorActionAdd(ReferenceEditorAction event) {
        ReferencePopupEditor popup = getNavigationManager().showInPopup(ReferencePopupEditor.class);
        popup.loadInEditor(null);
    }

    @EventListener(condition = "#event.type == T(eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.Action).EDIT && #event.sourceComponent == null")
    public void onReferenceEditorActionEdit(ReferenceEditorAction event) {
        ReferencePopupEditor popup = getNavigationManager().showInPopup(ReferencePopupEditor.class);
        popup.withDeleteButton(true);
        popup.loadInEditor(event.getEntityId());
    }

    @EventListener(condition = "#event.type == T(eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.Action).EDIT && #event.sourceComponent == null")
    public void onRegistrationEditorAction(RegistrationEditorAction event) {
        RegistrationPopupEditor popup = getNavigationManager().showInPopup(RegistrationPopupEditor.class);
        popup.loadInEditor(event.getEntityId());
    }

    @EventListener(condition = "#event.type == T(eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.Action).EDIT")
    public void onTaxonNameEditorActionEdit(TaxonNameEditorAction event) {

        if(!checkFromOwnView(event)){
            return;
        }

        TaxonNamePopupEditor popup = getNavigationManager().showInPopup(TaxonNamePopupEditor.class);
        popup.withDeleteButton(true);
        // disable NomReferenceCombobox:
        // the in the registration application inReferences should only edited centrally
        popup.getNomReferenceCombobox().setEnabled(false);
        popup.loadInEditor(event.getEntityId());
        if(event.getSourceComponent() != null){
            popup.setReadOnly(event.getSourceComponent().isReadOnly());
        }
    }


    @EventListener(condition = "#event.type == T(eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.Action).ADD")
    public void onTaxonNameEditorActionAdd(TaxonNameEditorAction event) {

        newTaxonNameForRegistration = TaxonNameFactory.NewBotanicalInstance(Rank.SPECIES());
        newTaxonNameForRegistration.setNomenclaturalReference(getRepo().getReferenceService().find(workingset.getCitationId()));
        EntityChangeEvent nameSaveEvent = getTaxonNameStore().saveBean(newTaxonNameForRegistration);
        newTaxonNameForRegistration = getRepo().getNameService().find(nameSaveEvent.getEntityId());
        TaxonNamePopupEditor popup = getNavigationManager().showInPopup(TaxonNamePopupEditor.class);
        popup.grantToCurrentUser(EnumSet.of(CRUD.UPDATE,CRUD.DELETE));
        popup.withDeleteButton(true);
        popup.loadInEditor(newTaxonNameForRegistration.getId());
        // disable NomReferenceCombobox:
        // the in the registration application inReferences should only edited centrally
        popup.getNomReferenceCombobox().setEnabled(false);
    }

    /**
     * Creates a new <code>Registration</code> for a new name that has just been edited
     * using the <code>TaxonNamePopupEditor</code>. The new name was previously created
     * in this presenter as <code>newTaxonNameForRegistration</code> (see {@link #onTaxonNameEditorActionAdd(TaxonNameEditorAction)})
     * and is either filled with data (<code>Reason.SAVE</code>) or it is still empty
     * (<code>Reason.CANCEL</code>).
     *
     *
     * @param event
     * @throws RegistrationValidationException
     */
    @EventListener
    public void onDoneWithTaxonnameEditor(DoneWithPopupEvent event) throws RegistrationValidationException{
        if(event.getPopup() instanceof TaxonNamePopupEditor){
            TransactionStatus txStatus = getRepo().startTransaction();
            if(event.getReason().equals(Reason.SAVE)){
                if(newTaxonNameForRegistration != null){
                    int taxonNameId = newTaxonNameForRegistration.getId();
                    getRepo().getSession().refresh(newTaxonNameForRegistration);
                    Registration reg = createNewRegistrationForName(taxonNameId);
                    // reload workingset into current session
                    loadWorkingSet(workingset.getCitationId());
                    workingset.add(reg);
                }
                refreshView(true);
            } else if(event.getReason().equals(Reason.CANCEL)){
                if(newTaxonNameForRegistration != null){
                    // clean up
                    getTaxonNameStore().deleteBean(newTaxonNameForRegistration);
                }
            }
            getRepo().commitTransaction(txStatus);
            newTaxonNameForRegistration = null;
        }
    }


    /**
     * Creates a new Registration for an exiting (previously published name).
     *
     * @param event
     * @throws RegistrationValidationException
     */
    @EventListener(condition = "#event.action == T(eu.etaxonomy.cdm.vaadin.event.registration.RegistrationWorkingsetAction.Action).start")
    public void onRegistrationWorkflowEventActionStart(RegistrationWorkingsetAction event) throws RegistrationValidationException {

        getView().getAddExistingNameCombobox().commit();
        TaxonName typifiedName = getView().getAddExistingNameCombobox().getValue();
        if(typifiedName != null){
            Registration newRegistrationWithExistingName = createNewRegistrationForName(null);
            Reference citation = getRepo().getReferenceService().find(workingset.getCitationId());
            newRegistrationDTOWithExistingName = new RegistrationDTO(newRegistrationWithExistingName, typifiedName, citation);
            workingset.add(newRegistrationDTOWithExistingName);
            // tell the view to update the workingset
            getView().setWorkingset(workingset);
            getView().getAddExistingNameRegistrationButton().setEnabled(false);
            getView().getAddExistingNameRegistrationButton().setDescription("You first need to add a type designation to the previously created registration.");
        } else {
            logger.error("Seletced name is NULL");
        }

    }

    @EventListener(condition = "#event.type == T(eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.Action).EDIT")
    public void onTypeDesignationsEditorActionEdit(TypeDesignationWorkingsetEditorAction event) {

        if(!checkFromOwnView(event)){
            return;
        }

        if(event.getWorkingSetType() == TypeDesignationWorkingSetType.SPECIMEN_TYPE_DESIGNATION_WORKINGSET ){
            SpecimenTypeDesignationWorkingsetPopupEditor popup = getNavigationManager().showInPopup(SpecimenTypeDesignationWorkingsetPopupEditor.class);
            popup.withDeleteButton(true);
            popup.loadInEditor(new TypeDesignationWorkingsetEditorIdSet(event.getRegistrationId(), event.getEntityId()));
            if(event.getSourceComponent() != null){
                popup.setReadOnly(event.getSourceComponent().isReadOnly());
            }
        } else {
            // TypeDesignationWorkingSetType.NAME_TYPE_DESIGNATION_WORKINGSET
            // FIXME implement NameTypeDesignationWorkingsetPopupEditor
        }
    }

    @EventListener(condition = "#event.type == T(eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.Action).ADD && #event.sourceComponent == null")
    public void onAddNewTypeDesignationWorkingset(TypeDesignationWorkingsetEditorAction event) {

        if(event.getWorkingSetType() == TypeDesignationWorkingSetType.SPECIMEN_TYPE_DESIGNATION_WORKINGSET){
            SpecimenTypeDesignationWorkingsetPopupEditor popup = getNavigationManager().showInPopup(SpecimenTypeDesignationWorkingsetPopupEditor.class);
            TypeDesignationWorkingsetEditorIdSet identifierSet;
            Integer typifiedNameId;
            if(newRegistrationDTOWithExistingName != null){
                typifiedNameId = newRegistrationDTOWithExistingName.getTypifiedNameRef().getId();
            } else {
                RegistrationDTO registrationDTO = workingset.getRegistrationDTO(event.getRegistrationId()).get();
                EntityReference typifiedNameRef = registrationDTO.getTypifiedNameRef();
                if(typifiedNameRef != null){
                    // case for registrations without name, in which case the typifiedName is only defined via the typedesignations
                    typifiedNameId = typifiedNameRef.getId();
                } else {
                    // case of registrations with a name in the nomenclatural act.
                    typifiedNameId = registrationDTO.getNameRef().getId();
                }
            }
            identifierSet = new TypeDesignationWorkingsetEditorIdSet(
                    event.getRegistrationId(),
                    getView().getCitationID(),
                    typifiedNameId
                    );
            popup.grantToCurrentUser(EnumSet.of(CRUD.UPDATE, CRUD.DELETE));
            popup.loadInEditor(identifierSet);
            popup.withDeleteButton(true);
        } else {
            // TypeDesignationWorkingSetType.NAME_TYPE_DESIGNATION_WORKINGSET
            // FIXME implement NameTypeDesignationWorkingsetPopupEditor
        }
    }

    /**
     * Performs final actions after a TypeDesignationEditor which has been
     * opened to add a TypeDesignation to a Registration object which was
     * created for an previously published name. Prior adding a typedesignation,
     * the according Registration object is dangling, that has no association to
     * any entity denoting an nomenclatural act which has a reference to a
     * publication. This means that the registration object is not in the
     * working set.
     *
     *
     * @param event
     * @throws RegistrationValidationException
     */
    @EventListener
    public void onDoneWithTypeDesignationEditor(DoneWithPopupEvent event) throws RegistrationValidationException{
        if(event.getPopup() instanceof SpecimenTypeDesignationWorkingsetPopupEditor){
            if(event.getReason().equals(Reason.SAVE)){
                refreshView(true);
            } else if(event.getReason().equals(Reason.CANCEL)){
                // clean up
                if(newRegistrationDTOWithExistingName != null){
                    getRegistrationStore().deleteBean(newRegistrationDTOWithExistingName.registration());
                }
            }
            // set newRegistrationDTOWithExistingName NULL in any case
            newRegistrationDTOWithExistingName = null;
        }
    }


    @EventListener(classes=ShowDetailsEvent.class, condition = "#event.type == T(eu.etaxonomy.cdm.vaadin.model.registration.RegistrationWorkingSet)")
    public void onShowRegistrationWorkingSetMessages(ShowDetailsEvent<?,?> event) { // WARNING don't use more specific generic type arguments
        List<String> messages = new ArrayList<>();
        for(RegistrationDTO dto : workingset.getRegistrationDTOs()){
            dto.getMessages().forEach(m -> messages.add(dto.getSummary() + ": " + m));
        }
        if(event.getProperty().equals("messages")){
            getView().openDetailsPopup("Messages", messages);
        }
    }

    @EventListener(classes=ShowDetailsEvent.class, condition = "#event.type == T(eu.etaxonomy.cdm.vaadin.view.registration.RegistrationDTO)")
    public void onShowRegistrationMessages(ShowDetailsEvent<?,?> event) { // WARNING don't use more specific generic type arguments
        RegistrationDTO regDto = workingSetService.loadDtoById((Integer)event.getIdentifier());
        if(event.getProperty().equals("messages")){
            if(getView() != null){
                getView().openDetailsPopup("Messages", regDto.getMessages());
            }
        }
    }

    @EventListener
    public void onEntityChangeEvent(EntityChangeEvent event){
        if(workingset == null){
            return;
        }
        if(Reference.class.isAssignableFrom(event.getEntityType())){
            if(workingset.getCitationId().equals(event.getEntityId())){
                refreshView(true);
            }
        } else
        if(Registration.class.isAssignableFrom(event.getEntityType())){
            if(workingset.getRegistrations().stream().anyMatch(reg -> reg.getId() == event.getEntityId())){
                refreshView(true);
            }
        } else
        if(TaxonName.class.isAssignableFrom(event.getEntityType())){
            if(workingset.getRegistrationDTOs().stream().anyMatch(reg ->
                reg.getTypifiedNameRef() != null
                && reg.getTypifiedNameRef().getId() == event.getEntityId())){
                    refreshView(true);
            }
        } else
        if(TypeDesignationBase.class.isAssignableFrom(event.getEntityType())){
            if(workingset.getRegistrationDTOs().stream().anyMatch(
                    reg -> reg.getTypeDesignations().stream().anyMatch(
                            td -> td.getId() == event.getEntityId()
                            )
                        )
                    ){
                refreshView(true);
            }
        }

    }

}
