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
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.TransactionStatus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.server.SystemError;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import eu.etaxonomy.cdm.api.service.INameService;
import eu.etaxonomy.cdm.api.service.IRegistrationService;
import eu.etaxonomy.cdm.api.service.idminter.IdentifierMinter.Identifier;
import eu.etaxonomy.cdm.api.service.idminter.RegistrationIdentifierMinter;
import eu.etaxonomy.cdm.ext.common.ExternalServiceException;
import eu.etaxonomy.cdm.ext.registration.messages.IRegistrationMessageService;
import eu.etaxonomy.cdm.model.common.User;
import eu.etaxonomy.cdm.model.name.NameTypeDesignation;
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
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItem;
import eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.EditorActionContext;
import eu.etaxonomy.cdm.vaadin.event.EditorActionTypeFilter;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEditorAction;
import eu.etaxonomy.cdm.vaadin.event.RegistrationEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ShowDetailsEvent;
import eu.etaxonomy.cdm.vaadin.event.ShowDetailsEventEntityTypeFilter;
import eu.etaxonomy.cdm.vaadin.event.TaxonNameEditorAction;
import eu.etaxonomy.cdm.vaadin.event.TypeDesignationWorkingsetEditorAction;
import eu.etaxonomy.cdm.vaadin.event.registration.RegistrationWorkingsetAction;
import eu.etaxonomy.cdm.vaadin.model.EntityReference;
import eu.etaxonomy.cdm.vaadin.model.TypedEntityReference;
import eu.etaxonomy.cdm.vaadin.model.registration.RegistrationWorkingSet;
import eu.etaxonomy.cdm.vaadin.security.UserHelper;
import eu.etaxonomy.cdm.vaadin.theme.EditValoTheme;
import eu.etaxonomy.cdm.vaadin.ui.RegistrationUIDefaults;
import eu.etaxonomy.cdm.vaadin.util.CdmTitleCacheCaptionGenerator;
import eu.etaxonomy.cdm.vaadin.util.converter.TypeDesignationSetManager.TypeDesignationWorkingSetType;
import eu.etaxonomy.cdm.vaadin.view.name.NameTypeDesignationPopupEditor;
import eu.etaxonomy.cdm.vaadin.view.name.SpecimenTypeDesignationWorkingsetPopupEditor;
import eu.etaxonomy.cdm.vaadin.view.name.TaxonNamePopupEditor;
import eu.etaxonomy.cdm.vaadin.view.name.TaxonNamePopupEditorMode;
import eu.etaxonomy.cdm.vaadin.view.name.TypeDesignationWorkingsetEditorIdSet;
import eu.etaxonomy.cdm.vaadin.view.reference.ReferencePopupEditor;
import eu.etaxonomy.vaadin.mvp.AbstractPopupEditor;
import eu.etaxonomy.vaadin.mvp.AbstractPresenter;
import eu.etaxonomy.vaadin.mvp.AbstractView;
import eu.etaxonomy.vaadin.mvp.BeanInstantiator;
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
    private IRegistrationWorkingSetService regWorkingSetService;

    @Autowired
    private RegistrationIdentifierMinter minter;

    @Autowired
    private IRegistrationMessageService messageService;

    /**
     * @return the regWorkingSetService
     */
    public IRegistrationWorkingSetService getWorkingSetService() {
        return regWorkingSetService;
    }

    private RegistrationWorkingSet workingset;

    private TaxonName newTaxonNameForRegistration = null;

    private RegistrationDTO newRegistrationDTOWithExistingName;

    private RegistrationDTO newNameTypeDesignationTarget;


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
        EntityChangeEvent event = getRegistrationStore().saveBean(reg, (AbstractView) getView());
        UserHelper.fromSession().createAuthorityForCurrentUser(Registration.class, event.getEntityId(), Operation.UPDATE, RegistrationStatus.PREPARATION.name());
        getRepo().commitTransaction(txStatus);
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        return getRepo().getRegistrationService().load(event.getEntityId(), Arrays.asList(new String []{"blockedBy"}));
    }


    /**
     * @param doReload TODO
     *
     */
    protected void refreshView(boolean doReload) {
        if(workingset == null){
            return; // nothing to do
        }
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

        // PagingProviders and CacheGenerator for the existingNameCombobox
        CdmFilterablePagingProvider<TaxonName, TaxonName> pagingProvider = new CdmFilterablePagingProvider<TaxonName, TaxonName>(
                getRepo().getNameService());
        CdmTitleCacheCaptionGenerator<TaxonName> titleCacheGenerator = new CdmTitleCacheCaptionGenerator<TaxonName>();
        getView().getAddExistingNameCombobox().setCaptionGenerator(titleCacheGenerator);
        getView().getAddExistingNameCombobox().loadFrom(pagingProvider, pagingProvider, pagingProvider.getPageSize());

        // update the messages
        User user = UserHelper.fromSession().user();
        for (Integer registrationId : getView().getRegistrationItemMap().keySet()) {
            Button messageButton = getView().getRegistrationItemMap().get(registrationId).regItemButtons.getMessagesButton();

            RegistrationDTO regDto = workingset.getRegistrationDTO(registrationId).get();
            try {
                int messageCount = messageService.countActiveMessagesFor(regDto.registration(), user);

                boolean activeMessages = messageCount > 0;
                boolean currentUserIsSubmitter = regDto.getSubmitterUserName().equals(UserHelper.fromSession().userName());
                boolean currentUserIsCurator = UserHelper.fromSession().userIsRegistrationCurator();
                messageButton.setEnabled(false);
                if(currentUserIsCurator){
                    if(currentUserIsSubmitter){
                        messageButton.setDescription("No point sending messages to your self.");
                    } else {
                        messageButton.setEnabled(true);
                        messageButton.setDescription("Open the messages dialog.");
                    }
                } else {
                    messageButton.setDescription("Sorry, only a curator can start a conversation.");
                }
                if(activeMessages){
                    messageButton.setEnabled(true);
                    messageButton.addStyleName(EditValoTheme.BUTTON_HIGHLITE);
                    String who = currentUserIsSubmitter ? "curator" : "submitter";
                    messageButton.setDescription("The " + who + " is looking forward to your reply.");

                }
            } catch (ExternalServiceException e) {
                messageButton.setComponentError(new SystemError(e.getMessage(), e));
            }
        }

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


    @EventBusListenerMethod(filter = EditorActionTypeFilter.Add.class)
    public void onReferenceEditorActionAdd(ReferenceEditorAction event) {

        if(!checkFromOwnView(event)){
            return;
        }

        ReferencePopupEditor popup = getNavigationManager().showInPopup(ReferencePopupEditor.class, getView());
        popup.withReferenceTypes(RegistrationUIDefaults.PRINTPUB_REFERENCE_TYPES);
        popup.loadInEditor(null);
    }

    @EventBusListenerMethod(filter = EditorActionTypeFilter.Edit.class)
    public void onReferenceEditorActionEdit(ReferenceEditorAction event) {

        if(!checkFromOwnView(event)){
            return;
        }
        ReferencePopupEditor popup = getNavigationManager().showInPopup(ReferencePopupEditor.class, getView());
        popup.withReferenceTypes(RegistrationUIDefaults.PRINTPUB_REFERENCE_TYPES);
        popup.withDeleteButton(true);
        popup.loadInEditor(event.getEntityId());
    }

    @EventBusListenerMethod
    public void onDoneWithReferencePopupEditor(DoneWithPopupEvent event) throws RegistrationValidationException{
        if(event.getPopup() instanceof ReferencePopupEditor){
            if(event.getReason().equals(Reason.SAVE)){
                refreshView(true);
            }
        }
    }

    @EventBusListenerMethod
    public void onDoneWithSpecimenTypeDesignationWorkingsetPopupEditor(DoneWithPopupEvent event) throws RegistrationValidationException{
        if(event.getPopup() instanceof SpecimenTypeDesignationWorkingsetPopupEditor){
            if(event.getReason().equals(Reason.SAVE)){
                refreshView(true);
            }
        }
    }

    @EventBusListenerMethod(filter = EditorActionTypeFilter.Edit.class)
    public void onRegistrationEditorAction(RegistrationEditorAction event) {

        if(!checkFromOwnView(event)){
            return;
        }

        RegistrationPopupEditor popup = getNavigationManager().showInPopup(RegistrationPopupEditor.class, getView());
        popup.loadInEditor(event.getEntityId());
    }

    @EventBusListenerMethod(filter = EditorActionTypeFilter.Edit.class)
    public void onTaxonNameEditorActionEdit(TaxonNameEditorAction event) {

        if(!checkFromOwnView(event)){
            return;
        }

        TaxonNamePopupEditor popup = getNavigationManager().showInPopup(TaxonNamePopupEditor.class, getView());
        popup.setParentEditorActionContext(event.getContext());
        popup.withDeleteButton(true);
        configureTaxonNameEditor(popup);
        popup.loadInEditor(event.getEntityId());
        if(event.getSourceComponent() != null){
            popup.setReadOnly(event.getSourceComponent().isReadOnly());
        }

    }


    @EventBusListenerMethod(filter = EditorActionTypeFilter.Add.class)
    public void onTaxonNameEditorActionAdd(TaxonNameEditorAction event) {

        if(!checkFromOwnView(event)){
            return;
        }

        newTaxonNameForRegistration = TaxonNameFactory.NewNameInstance(RegistrationUIDefaults.NOMENCLATURAL_CODE, Rank.SPECIES());
        newTaxonNameForRegistration.setNomenclaturalReference(getRepo().getReferenceService().find(workingset.getCitationId()));
        EntityChangeEvent nameSaveEvent = getTaxonNameStore().saveBean(newTaxonNameForRegistration, (AbstractView) getView());
        newTaxonNameForRegistration = getRepo().getNameService().find(nameSaveEvent.getEntityId());
        TaxonNamePopupEditor popup = getNavigationManager().showInPopup(TaxonNamePopupEditor.class, getView());
        popup.setParentEditorActionContext(event.getContext());
        popup.grantToCurrentUser(EnumSet.of(CRUD.UPDATE,CRUD.DELETE));
        popup.withDeleteButton(true);
        configureTaxonNameEditor(popup);
        popup.loadInEditor(newTaxonNameForRegistration.getId());
    }

    /**
     * TODO consider putting this into a Configurer Bean per UIScope.
     * In the configurator bean this methods popup papamerter should be of the type
     * AbstractPopupEditor
     *
     * @param popup
     */
    protected void configureTaxonNameEditor(TaxonNamePopupEditor popup) {
        popup.enableMode(TaxonNamePopupEditorMode.suppressReplacementAuthorshipData);
        popup.enableMode(TaxonNamePopupEditorMode.nomenclaturalReferenceSectionEditingOnly);
        popup.enableMode(TaxonNamePopupEditorMode.requireNomenclaturalReference);
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
    @EventBusListenerMethod
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
                    getTaxonNameStore().deleteBean(newTaxonNameForRegistration, (AbstractView) getView());
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
    @EventBusListenerMethod
    public void onRegistrationWorkflowEventActionStart(RegistrationWorkingsetAction event) throws RegistrationValidationException {

        if(!event.isStart()){
            return;
        }

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


    @EventBusListenerMethod(filter = EditorActionTypeFilter.Edit.class)
    public void onTypeDesignationsEditorActionEdit(TypeDesignationWorkingsetEditorAction event) {

        if(!checkFromOwnView(event)){
            return;
        }

        if(event.getWorkingSetType() == TypeDesignationWorkingSetType.SPECIMEN_TYPE_DESIGNATION_WORKINGSET ){
            SpecimenTypeDesignationWorkingsetPopupEditor popup = getNavigationManager().showInPopup(SpecimenTypeDesignationWorkingsetPopupEditor.class, getView());
            popup.setParentEditorActionContext(event.getContext());
            popup.withDeleteButton(true);
            popup.loadInEditor(new TypeDesignationWorkingsetEditorIdSet(event.getRegistrationId(), event.getBaseEntityRef()));
            if(event.getSourceComponent() != null){
                // propagate readonly state from source component to popup
                popup.setReadOnly(event.getSourceComponent().isReadOnly());
            }
        } else {
            NameTypeDesignationPopupEditor popup = getNavigationManager().showInPopup(NameTypeDesignationPopupEditor.class, getView());
            popup.setParentEditorActionContext(event.getContext());
            popup.withDeleteButton(true);
            popup.loadInEditor(new TypeDesignationWorkingsetEditorIdSet(event.getRegistrationId(), event.getBaseEntityRef()));

            popup.getCitationCombobox().setEnabled(false);
            popup.getTypifiedNamesComboboxSelect().setEnabled(false);

            if(event.getSourceComponent() != null){
                // propagate readonly state from source component to popup
                popup.setReadOnly(event.getSourceComponent().isReadOnly());
            }
            newNameTypeDesignationTarget = workingset.getRegistrationDTO(event.getRegistrationId()).get();
        }
    }

    @EventBusListenerMethod(filter = EditorActionTypeFilter.Add.class)
    public void onTypeDesignationWorkingsetAdd(TypeDesignationWorkingsetEditorAction event) {

        if(event.getSourceComponent() != null){
            return;
        }

        if(event.getWorkingSetType() == TypeDesignationWorkingSetType.SPECIMEN_TYPE_DESIGNATION_WORKINGSET){
            SpecimenTypeDesignationWorkingsetPopupEditor popup = getNavigationManager().showInPopup(SpecimenTypeDesignationWorkingsetPopupEditor.class, getView());
            popup.setParentEditorActionContext(event.getContext());
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
            if(event.getSourceComponent() != null){
                // propagate readonly state from source component to popup
                popup.setReadOnly(event.getSourceComponent().isReadOnly());
            }
        } else {
            NameTypeDesignationPopupEditor popup = getNavigationManager().showInPopup(NameTypeDesignationPopupEditor.class, getView());
            popup.setParentEditorActionContext(event.getContext());
            popup.withDeleteButton(true);
            popup.grantToCurrentUser(EnumSet.of(CRUD.UPDATE, CRUD.DELETE));
            newNameTypeDesignationTarget = workingset.getRegistrationDTO(event.getRegistrationId()).get();
            popup.setBeanInstantiator(new BeanInstantiator<NameTypeDesignation>() {

                @Override
                public NameTypeDesignation createNewBean() {

                    TaxonName typifiedName = getRepo().getNameService().load(event.getTypifiedNameId(), Arrays.asList(new String[]{"typeDesignations", "homotypicalGroup"}));
                    NameTypeDesignation nameTypeDesignation  = NameTypeDesignation.NewInstance();
                    nameTypeDesignation.setCitation(newNameTypeDesignationTarget.getCitation());
                    nameTypeDesignation.getTypifiedNames().add(typifiedName);
                    return nameTypeDesignation;
                }
            });
            popup.loadInEditor(null);
            popup.getCitationCombobox().setEnabled(false);
            popup.getTypifiedNamesComboboxSelect().setEnabled(false);
            if(event.getSourceComponent() != null){
                // propagate readonly state from source component to popup
                popup.setReadOnly(event.getSourceComponent().isReadOnly());
            }
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
    @EventBusListenerMethod
    public void onDoneWithTypeDesignationEditor(DoneWithPopupEvent event) throws RegistrationValidationException{
        if(event.getPopup() instanceof SpecimenTypeDesignationWorkingsetPopupEditor){
            if(event.getReason().equals(Reason.SAVE)){
                refreshView(true);
            } else if(event.getReason().equals(Reason.CANCEL)){
                // noting to do
            }
            newRegistrationDTOWithExistingName = null;
        } else if(event.getPopup() instanceof NameTypeDesignationPopupEditor){
            if(event.getReason().equals(Reason.SAVE)){
                UUID uuid = ((NameTypeDesignationPopupEditor)event.getPopup()).getBean().getUuid();

                Session session = getRepo().getSessionFactory().openSession();
                Transaction txstate = session.beginTransaction();
                TypeDesignationBase<?> nameTypeDesignation = getRepo().getNameService().loadTypeDesignation(uuid, Arrays.asList(""));
                // only load the typeDesignations with the registration so that the typified name can  not be twice in the session
                // otherwise multiple representation problems might occur
                Registration registration = getRepo().getRegistrationService().load(newNameTypeDesignationTarget.getUuid(), Arrays.asList("typeDesignations"));
                registration.getTypeDesignations().add(nameTypeDesignation);
                session.merge(registration);
                txstate.commit();
                session.close();

                refreshView(true);
            } else if(event.getReason().equals(Reason.CANCEL)){
                // noting to do
            }
            newNameTypeDesignationTarget = null;
        }
        // ignore other editors
    }


    @EventBusListenerMethod(filter = ShowDetailsEventEntityTypeFilter.RegistrationWorkingSet.class)
    public void onShowDetailsEventForRegistrationWorkingSet(ShowDetailsEvent<RegistrationWorkingSet,?> event) {

        if(event.getProperty().equals(RegistrationItem.VALIDATION_PROBLEMS)){
            List<String> messages = new ArrayList<>();
            for(RegistrationDTO dto : workingset.getRegistrationDTOs()){
                dto.getValidationProblems().forEach(m -> messages.add(dto.getSummary() + ": " + m));
            }
            getView().openDetailsPopup("Validation Problems", messages);
        }
    }

    @EventBusListenerMethod
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
            if(event.getType().equals(EntityChangeEvent.Type.CREATED)){
                // new name! create a blocking registration
                Stack<EditorActionContext>context = ((AbstractPopupEditor)event.getSourceView()).getEditorActionContext();
                EditorActionContext rootContext = context.get(0);
                if(rootContext.getParentView().equals(getView())){
                    Registration blockingRegistration = createNewRegistrationForName(event.getEntityId());
                    TypedEntityReference<Registration> regReference = (TypedEntityReference<Registration>)rootContext.getParentEntity();
                    Registration registration = getRepo().getRegistrationService().load(regReference.getId(), Arrays.asList("$", "blockedBy"));
                    registration.getBlockedBy().add(blockingRegistration);
                    getRepo().getRegistrationService().saveOrUpdate(registration);
                    logger.debug("Blocking registration created");
                } else {
                    logger.debug("Nn blocking registration, since a new name for a new registration has been created");
                }
            }
            if(workingset.getRegistrationDTOs().stream().anyMatch(reg ->
                reg.getTypifiedNameRef() != null
                && reg.getTypifiedNameRef().getId() == event.getEntityId())){
                    refreshView(true);
            }
        } else
        if(TypeDesignationBase.class.isAssignableFrom(event.getEntityType())){
            if(workingset.getRegistrationDTOs().stream().anyMatch(
                    reg -> reg.getTypeDesignations() != null && reg.getTypeDesignations().stream().anyMatch(
                            td -> td.getId() == event.getEntityId()
                            )
                        )
                    ){
                refreshView(true);
            }
        }
    }


    @EventBusListenerMethod(filter = ShowDetailsEventEntityTypeFilter.RegistrationDTO.class)
    public void onShowDetailsEventForRegistrationDTO(ShowDetailsEvent<RegistrationDTO, Integer> event) {

        // FIXME check from own view!!!
        if(getView() == null){
            return;
        }

        Integer registrationId = event.getIdentifier();

        RegistrationDTO regDto = getWorkingSetService().loadDtoById(registrationId);
        if(event.getProperty().equals(RegistrationItem.BLOCKED_BY)){

            Set<RegistrationDTO> blockingRegs = getWorkingSetService().loadBlockingRegistrations(registrationId);
            getView().setBlockingRegistrations(registrationId, blockingRegs);
        } else if(event.getProperty().equals(RegistrationItem.MESSAGES)){

            RegistrationMessagesPopup popup = getNavigationManager().showInPopup(RegistrationMessagesPopup.class, getView());
            popup.loadMessagesFor(regDto.getId());

        } else if(event.getProperty().equals(RegistrationItem.VALIDATION_PROBLEMS)){
            getView().openDetailsPopup("Validation Problems", regDto.getValidationProblems());
        }


    }

}
