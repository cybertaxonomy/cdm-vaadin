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
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.server.SystemError;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import eu.etaxonomy.cdm.api.service.INameService;
import eu.etaxonomy.cdm.api.service.IRegistrationService;
import eu.etaxonomy.cdm.api.service.config.RegistrationStatusTransitions;
import eu.etaxonomy.cdm.api.service.dto.RegistrationDTO;
import eu.etaxonomy.cdm.api.service.dto.RegistrationWorkingSet;
import eu.etaxonomy.cdm.api.service.exception.RegistrationValidationException;
import eu.etaxonomy.cdm.api.service.name.TypeDesignationSetManager.TypeDesignationWorkingSetType;
import eu.etaxonomy.cdm.api.service.registration.IRegistrationWorkingSetService;
import eu.etaxonomy.cdm.api.utility.RoleProber;
import eu.etaxonomy.cdm.api.utility.UserHelper;
import eu.etaxonomy.cdm.cache.CdmTransientEntityAndUuidCacher;
import eu.etaxonomy.cdm.database.PermissionDeniedException;
import eu.etaxonomy.cdm.ext.common.ExternalServiceException;
import eu.etaxonomy.cdm.ext.registration.messages.IRegistrationMessageService;
import eu.etaxonomy.cdm.model.ICdmEntityUuidCacher;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.common.User;
import eu.etaxonomy.cdm.model.name.NameTypeDesignation;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.RegistrationStatus;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.name.TaxonNameFactory;
import eu.etaxonomy.cdm.model.name.TypeDesignationBase;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferenceType;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.ref.EntityReference;
import eu.etaxonomy.cdm.ref.TypedEntityReference;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProvider;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProviderFactory;
import eu.etaxonomy.cdm.service.CdmStore;
import eu.etaxonomy.cdm.service.UserHelperAccess;
import eu.etaxonomy.cdm.vaadin.component.CdmBeanItemContainerFactory;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItem;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationStatusFieldInstantiator;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationStatusSelect;
import eu.etaxonomy.cdm.vaadin.event.EditorActionContext;
import eu.etaxonomy.cdm.vaadin.event.EditorActionTypeFilter;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEditorAction;
import eu.etaxonomy.cdm.vaadin.event.RegistrationEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ShowDetailsEvent;
import eu.etaxonomy.cdm.vaadin.event.ShowDetailsEventEntityTypeFilter;
import eu.etaxonomy.cdm.vaadin.event.TaxonNameEditorAction;
import eu.etaxonomy.cdm.vaadin.event.TypeDesignationWorkingsetEditorAction;
import eu.etaxonomy.cdm.vaadin.event.registration.RegistrationWorkingsetAction;
import eu.etaxonomy.cdm.vaadin.permission.AccessRestrictedView;
import eu.etaxonomy.cdm.vaadin.permission.RolesAndPermissions;
import eu.etaxonomy.cdm.vaadin.theme.EditValoTheme;
import eu.etaxonomy.cdm.vaadin.ui.RegistrationUIDefaults;
import eu.etaxonomy.cdm.vaadin.ui.config.TaxonNamePopupEditorConfig;
import eu.etaxonomy.cdm.vaadin.util.CdmTitleCacheCaptionGenerator;
import eu.etaxonomy.cdm.vaadin.view.name.CachingPresenter;
import eu.etaxonomy.cdm.vaadin.view.name.NameTypeDesignationPopupEditor;
import eu.etaxonomy.cdm.vaadin.view.name.SpecimenTypeDesignationWorkingsetPopupEditor;
import eu.etaxonomy.cdm.vaadin.view.name.TaxonNameEditorPresenter;
import eu.etaxonomy.cdm.vaadin.view.name.TaxonNamePopupEditor;
import eu.etaxonomy.cdm.vaadin.view.name.TypeDesignationWorkingsetEditorIdSet;
import eu.etaxonomy.cdm.vaadin.view.reference.ReferencePopupEditor;
import eu.etaxonomy.vaadin.mvp.AbstractPopupEditor;
import eu.etaxonomy.vaadin.mvp.AbstractPresenter;
import eu.etaxonomy.vaadin.mvp.AbstractView;
import eu.etaxonomy.vaadin.mvp.BeanInstantiator;
import eu.etaxonomy.vaadin.ui.navigation.NavigationEvent;
import eu.etaxonomy.vaadin.ui.view.DoneWithPopupEvent;
import eu.etaxonomy.vaadin.ui.view.DoneWithPopupEvent.Reason;

/**
 * @author a.kohlbecker
 * @since Mar 3, 2017
 *
 */
@SpringComponent
@ViewScope
public class RegistrationWorkingsetPresenter extends AbstractPresenter<RegistrationWorkingsetView> implements CachingPresenter {

    private static final Logger logger = Logger.getLogger(RegistrationWorkingsetPresenter.class);

    private static final long serialVersionUID = 1L;

    @Autowired
    private IRegistrationWorkingSetService regWorkingSetService;

    @Autowired
    private IRegistrationMessageService messageService;

    @Autowired
    private CdmFilterablePagingProviderFactory pagingProviderFactory;


    /**
     * @return the regWorkingSetService
     */
    public IRegistrationWorkingSetService getWorkingSetService() {
        return regWorkingSetService;
    }

    private RegistrationWorkingSet workingset;

    /**
     * Contains the poupeditor which has neen opend to start the registration of a new name as long as it has not been saved or canceled.
     * There can always only be one popup editor for this purpose.
     */
    private TaxonNamePopupEditor newNameForRegistrationPopupEditor = null;

    /**
     * Contains
     */
    private List<Registration> newNameBlockingRegistrations = new ArrayList<>();

    private Map<NameTypeDesignationPopupEditor, UUID> nameTypeDesignationPopupEditorRegistrationUUIDMap = new HashMap<>();


    private ICdmEntityUuidCacher cache;

    private Collection<CdmBase> rootEntities = new HashSet<>();

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
     * Checks
     * <ol>
     * <li>if there is NOT any registration for this name created in the current registration system</li>
     * <li>Checks if the name belongs to the current workingset</li>
     * </ol>
     * If both checks are successful the method returns <code>true</code>.
     */
    public boolean canCreateNameRegistrationFor(TaxonName name) {
        return !getRepo().getRegistrationService().checkRegistrationExistsFor(name) && checkWokingsetContainsProtologe(name);
    }

    /**
     * @param name
     * @return
     */
    public boolean checkWokingsetContainsProtologe(TaxonName name) {
        Reference nomRef = name.getNomenclaturalReference();
        UUID citationUuid = workingset.getCitationUuid();
        // @formatter:off
        return nomRef != null && (
                // nomref matches
                nomRef.getUuid().equals(citationUuid) ||
                // nomref.inreference matches
                (nomRef.getType() != null && nomRef.getType() == ReferenceType.Section && nomRef.getInReference() != null && nomRef.getInReference().getUuid().equals(citationUuid))
                );
        // @formatter:on
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
            loadWorkingSet(workingset.getCitationUuid());
        }
        applyWorkingset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleViewEntered() {
        super.handleViewEntered();
        // TODO currently cannot specify type more precisely, see AbstractSelect
        // FIXME externalize into class file!!!!!!!!!!!!
        getView().setStatusComponentInstantiator(new RegistrationStatusFieldInstantiator<Object>(){

            private static final long serialVersionUID = 7099181280977511048L;

            @Override
            public AbstractField<Object> create(RegistrationDTO regDto) {

                CdmBeanItemContainerFactory selectFieldFactory = new CdmBeanItemContainerFactory(getRepo());
                // submitters have GrantedAuthorities like REGISTRATION(PREPARATION).[UPDATE]{ab4459eb-3b96-40ba-bfaa-36915107d59e}
                UserHelper userHelper = UserHelperAccess.userHelper().withCache(getCache());
                Set<RegistrationStatus> availableStatus = new HashSet<>();

                boolean canChangeStatus = userHelper.userHasPermission(regDto.registration(), CRUD.UPDATE);
                availableStatus.add(regDto.getStatus());
                if(canChangeStatus){
                    if(userHelper.userIsAdmin()){
                        availableStatus.addAll(Arrays.asList(RegistrationStatus.values()));
                    } else {
                        availableStatus.addAll(RegistrationStatusTransitions.possibleTransitions(regDto.getStatus()));
                    }
                }

                RegistrationStatusSelect select = new RegistrationStatusSelect(null, selectFieldFactory.buildBeanItemContainer(
                        RegistrationStatus.class,
                        availableStatus.toArray(new RegistrationStatus[availableStatus.size()]))
                        );
                select.setValue(regDto.getStatus());
                select.addValueChangeListener(e -> saveRegistrationStatusChange(regDto.getUuid(), e.getProperty().getValue()));
                select.setEnabled(canChangeStatus);
                select.setNullSelectionAllowed(false);
                return select;
            }


        });
        loadWorkingSet(getView().getCitationUuid());
        applyWorkingset();

    }

    private void applyWorkingset(){
         getView().setWorkingset(workingset);
        // PagingProviders and CacheGenerator for the existingNameCombobox
        activateComboboxes();
        // update the messages
        // updateMessages(); // disabled see  #7908
    }

    protected void activateComboboxes() {
        CdmTitleCacheCaptionGenerator<TaxonName> titleCacheGenerator = new CdmTitleCacheCaptionGenerator<TaxonName>();
        getView().getAddExistingNameCombobox().setCaptionGenerator(titleCacheGenerator);
        CdmFilterablePagingProvider<TaxonName, TaxonName> pagingProvider = pagingProviderFactory.taxonNamesWithoutOrthophicIncorrect();
        getView().getAddExistingNameCombobox().loadFrom(pagingProvider, pagingProvider, pagingProvider.getPageSize());
    }

    protected void updateMessages() {
        User user = UserHelperAccess.userHelper().user();
        for (UUID registrationUuid : getView().getRegistrationItemMap().keySet()) {
            Button messageButton = getView().getRegistrationItemMap().get(registrationUuid).regItemButtons.getMessagesButton();

            RegistrationDTO regDto = workingset.getRegistrationDTO(registrationUuid).get();
            try {
                int messageCount = messageService.countActiveMessagesFor(regDto.registration(), user);

                boolean activeMessages = messageCount > 0;
                boolean currentUserIsSubmitter = regDto.getSubmitterUserName() != null && regDto.getSubmitterUserName().equals(UserHelperAccess.userHelper().userName());
                boolean currentUserIsCurator = UserHelperAccess.userHelper().userIs(new RoleProber(RolesAndPermissions.ROLE_CURATION));
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
    protected void loadWorkingSet(UUID referenceUuid) {

        try {
            workingset = getWorkingSetService().loadWorkingSetByReferenceUuid(referenceUuid, true);
        } catch (RegistrationValidationException error) {
            logger.error(error);
            showErrorDialog("Validation Error", error.getMessage());
        } catch(PermissionDeniedException e){
            logger.info(e);
            ((AccessRestrictedView)getView()).setAccessDeniedMessage(e.getMessage());
        }
        if(workingset == null || workingset.getCitationUuid() == null){
            Reference citation = getRepo().getReferenceService().find(referenceUuid);
            workingset = new RegistrationWorkingSet(citation);
        }
        cache = new CdmTransientEntityAndUuidCacher(this);
        for(Registration registration : workingset.getRegistrations()) {
            addRootEntity(registration);
        }
    }

    /**
     * @param errorDialogCaption
     * @param errorMessage
     */
    public void showErrorDialog(String errorDialogCaption, String errorMessage) {
        Window errorDialog = new Window(errorDialogCaption);
        errorDialog.setModal(true);
        VerticalLayout subContent = new VerticalLayout();
        subContent.setMargin(true);
        errorDialog.setContent(subContent);
        subContent.addComponent(new Label(errorMessage));
        UI.getCurrent().addWindow(errorDialog);
    }

    private void saveRegistrationStatusChange(UUID uuid, Object value) {
        Registration reg = getRepo().getRegistrationService().load(uuid);
        if(reg == null){
            // registration was not yet persisted, ignore
            return;
        }
        if(value != null && value instanceof RegistrationStatus){
            if(!Objects.equals(value, reg.getStatus())){
                reg.setStatus((RegistrationStatus)value);
                getRegistrationStore().saveBean(reg, (AbstractView)getView());
                refreshView(true);
            }
        } else {
            // only log here as error
            logger.error("Ivalid attempt to set RegistrationStatus to " + Objects.toString(value.toString(), "NULL"));
        }
    }


    @EventBusListenerMethod(filter = EditorActionTypeFilter.Add.class)
    public void onReferenceEditorActionAdd(ReferenceEditorAction event) {

        if(!checkFromOwnView(event)){
            return;
        }

        ReferencePopupEditor popup = openPopupEditor(ReferencePopupEditor.class, event);
        popup.withReferenceTypes(RegistrationUIDefaults.PRINTPUB_REFERENCE_TYPES);
        popup.loadInEditor(null);
    }

    @EventBusListenerMethod(filter = EditorActionTypeFilter.Edit.class)
    public void onReferenceEditorActionEdit(ReferenceEditorAction event) {

        if(!checkFromOwnView(event)){
            return;
        }
        ReferencePopupEditor popup = openPopupEditor(ReferencePopupEditor.class, event);
        popup.withReferenceTypes(RegistrationUIDefaults.PRINTPUB_REFERENCE_TYPES);
        popup.withDeleteButton(true);
        popup.loadInEditor(event.getEntityUuid());
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

        RegistrationPopupEditor popup = openPopupEditor(RegistrationPopupEditor.class, event);
        popup.loadInEditor(event.getEntityUuid());
    }

    @EventBusListenerMethod(filter = EditorActionTypeFilter.Edit.class)
    public void onTaxonNameEditorActionEdit(TaxonNameEditorAction event) {

        if(!checkFromOwnView(event)){
            return;
        }

        TaxonNamePopupEditor popup = openPopupEditor(TaxonNamePopupEditor.class, event);
        popup.setParentEditorActionContext(event.getContext(), event.getTarget());
        popup.withDeleteButton(true);
        TaxonNamePopupEditorConfig.configureForNomenclaturalAct(popup);
        popup.loadInEditor(event.getEntityUuid());
        if(event.hasSource() && event.getSource().isReadOnly()){
            // avoid resetting readonly to false
            popup.setReadOnly(true);
        }

    }


    @EventBusListenerMethod(filter = EditorActionTypeFilter.Add.class)
    public void onTaxonNameEditorActionAdd(TaxonNameEditorAction event) {

        if(!checkFromOwnView(event)){
            return;
        }

        getView().getAddNewNameRegistrationButton().setEnabled(false);
        if(newNameForRegistrationPopupEditor == null){
            TaxonNamePopupEditor popup = openPopupEditor(TaxonNamePopupEditor.class, event);
            newNameForRegistrationPopupEditor = popup;
            popup.setParentEditorActionContext(event.getContext(), event.getTarget());
            popup.grantToCurrentUser(EnumSet.of(CRUD.UPDATE,CRUD.DELETE));
            popup.withDeleteButton(true);
            popup.setCdmEntityInstantiator(new BeanInstantiator<TaxonName>() {

                @Override
                public TaxonName createNewBean() {
                    TaxonName newTaxonName = TaxonNameFactory.NewNameInstance(RegistrationUIDefaults.NOMENCLATURAL_CODE, Rank.SPECIES());
                    newTaxonName.setNomenclaturalReference(getRepo().getReferenceService().load(workingset.getCitationUuid(), TaxonNameEditorPresenter.REFERENCE_INIT_STRATEGY ));
                    return newTaxonName;
                }
            });
            TaxonNamePopupEditorConfig.configureForNomenclaturalAct(popup);
            popup.loadInEditor(null);
        }
    }

    /**
     * Creates a new <code>Registration</code> for a new name that has just been edited
     * using a <code>TaxonNamePopupEditor</code>. The popup editor which has been opened to
     * edit the new name was remembered in <code>newNameForRegistrationPopupEditor</code>.
     * Any blocking registrations which have been created while editing the new name are
     * temporarily stored in <code>newNameBlockingRegistrations</code> until the registration
     * for the first name has been created. Additional new names are created for example
     * when a new name as basionym, replaced synonym, etc to the new name is created.
     * <p>
     * See also {@link #onTaxonNameEditorActionAdd(TaxonNameEditorAction)}).
     *
     * @param event
     * @throws RegistrationValidationException
     */
    @EventBusListenerMethod
    public void onDoneWithTaxonnameEditor(DoneWithPopupEvent event) throws RegistrationValidationException{
        if(event.getPopup() instanceof TaxonNamePopupEditor){
            if(newNameForRegistrationPopupEditor != null && event.getPopup().equals(newNameForRegistrationPopupEditor)){
                if(event.getReason().equals(Reason.SAVE)){
                    try {
                        // TODO move into a service class --------------
                        TransactionStatus txStatus = getRepo().startTransaction();
                        UUID taxonNameUuid = newNameForRegistrationPopupEditor.getBean().getUuid();
                        if(newNameForRegistrationPopupEditor.getBean().cdmEntity().isPersited()){
                            getRepo().getSession().refresh(newNameForRegistrationPopupEditor.getBean().cdmEntity());
                        }
                        Registration reg = getRepo().getRegistrationService().createRegistrationForName(taxonNameUuid);
                        if(!newNameBlockingRegistrations.isEmpty()){
                            for(Registration blockingReg : newNameBlockingRegistrations){
                                blockingReg = getRepo().getRegistrationService().load(blockingReg.getUuid());
                                reg.getBlockedBy().add(blockingReg);
                            }
                            getRepo().getRegistrationService().saveOrUpdate(reg);
                            newNameBlockingRegistrations.clear();
                        }
                        getRepo().commitTransaction(txStatus);
                        // --------------------------------------------------
                        // reload workingset into current session
                        loadWorkingSet(workingset.getCitationUuid());
                    } finally {
                        getRepo().getSession().clear(); // #7702
                        refreshView(true);
                        getView().getAddNewNameRegistrationButton().setEnabled(true);
                    }
                }
                // nullify and clear the memory on this popup editor in any case (SAVE, CANCEL, DELETE)
                newNameForRegistrationPopupEditor = null;
                newNameBlockingRegistrations.clear();
                getView().getAddNewNameRegistrationButton().setEnabled(true);
            } else {
                refreshView(true);
            }
        }
    }


    /**
     * Creates a new Registration for an exiting (previously published) name.
     *
     * @param event
     * @throws RegistrationValidationException
     */
    @EventBusListenerMethod
    public void onRegistrationWorkflowEventActionStart(RegistrationWorkingsetAction event) throws RegistrationValidationException {

        if(!event.isStart()){
            return;
        }

        getView().getAddExistingNameCombobox().commit(); // update the chosen value in the datasource
        TaxonName typifiedName = getView().getAddExistingNameCombobox().getValue();
        if(typifiedName != null){
            boolean doReloadWorkingSet = false;
            Reference citation = getRepo().getReferenceService().find(workingset.getCitationUuid());
            // here we completely ignore the ExistingNameRegistrationType since the user should not have the choice
            // to create a typification only registration in the working (publication) set which contains
            // the protologe. This is known from the nomenclatural reference.
            if(canCreateNameRegistrationFor(typifiedName)){
                // the citation which is the base for workingset contains the protologe of the name and the name has not
                // been registered before:
                // create a registration for the name and the first typifications
                Registration newRegistrationWithExistingName = getRepo().getRegistrationService().createRegistrationForName(typifiedName.getUuid());
                workingset.add(new RegistrationDTO(newRegistrationWithExistingName, typifiedName, citation));
                doReloadWorkingSet = true;
            } else {
                if(!checkWokingsetContainsProtologe(typifiedName)){
                    // create a typification only registration
                    Registration typificationOnlyRegistration = getRepo().getRegistrationService().newRegistration();
                    if(!getRepo().getRegistrationService().checkRegistrationExistsFor(typifiedName)){
                        // oops, yet no registration for this name, so we create it as blocking registration:
                        Registration blockingNameRegistration = getRepo().getRegistrationService().createRegistrationForName(typifiedName.getUuid());
                        typificationOnlyRegistration.getBlockedBy().add(blockingNameRegistration);
                    }
                    RegistrationDTO regDTO = new RegistrationDTO(typificationOnlyRegistration, typifiedName, citation);
                    workingset.add(regDTO);
                }
            }
            // tell the view to update the workingset
            refreshView(doReloadWorkingSet);
            getView().getAddExistingNameRegistrationButton().setEnabled(false);
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
            SpecimenTypeDesignationWorkingsetPopupEditor popup = openPopupEditor(SpecimenTypeDesignationWorkingsetPopupEditor.class, event);
            popup.setParentEditorActionContext(event.getContext(), event.getTarget());
            popup.withDeleteButton(true);
            popup.loadInEditor(new TypeDesignationWorkingsetEditorIdSet(event.getRegistrationUuid(), event.getBaseEntityRef()));
            if(event.hasSource()){
                // propagate readonly state from source button to popup
                popup.setReadOnly(event.getSource().isReadOnly());
            }
        } else {
            NameTypeDesignationPopupEditor popup = openPopupEditor(NameTypeDesignationPopupEditor.class, event);
            popup.setParentEditorActionContext(event.getContext(), event.getTarget());
            popup.withDeleteButton(true);
            popup.loadInEditor(new TypeDesignationWorkingsetEditorIdSet(event.getRegistrationUuid(), event.getBaseEntityRef()));

            popup.getCitationCombobox().setEnabled(false);
            popup.getTypifiedNamesComboboxSelect().setEnabled(false);

            if(event.hasSource()){
                // propagate readonly state from source button to popup
                popup.setReadOnly(event.getSource().isReadOnly());
            }
            nameTypeDesignationPopupEditorRegistrationUUIDMap.put(popup, event.getRegistrationUuid());
        }
    }

    @EventBusListenerMethod(filter = EditorActionTypeFilter.Add.class)
    public void onTypeDesignationWorkingsetAdd(TypeDesignationWorkingsetEditorAction event) {

        if(!event.hasSource()){
            return;
        }

        if(event.getWorkingSetType() == TypeDesignationWorkingSetType.SPECIMEN_TYPE_DESIGNATION_WORKINGSET){
            SpecimenTypeDesignationWorkingsetPopupEditor popup = openPopupEditor(SpecimenTypeDesignationWorkingsetPopupEditor.class, event);
            popup.setParentEditorActionContext(event.getContext(), event.getTarget());
            TypeDesignationWorkingsetEditorIdSet identifierSet;
            UUID typifiedNameUuid;

            RegistrationDTO registrationDTO = workingset.getRegistrationDTO(event.getRegistrationUuid()).get();
            EntityReference typifiedNameRef = registrationDTO.getTypifiedNameRef();
            if(typifiedNameRef != null){
                // case for registrations without name, in which case the typifiedName is only defined via the typedesignations
                typifiedNameUuid = typifiedNameRef.getUuid();
            } else {
                // case of registrations with a name in the nomenclatural act.
                typifiedNameUuid = registrationDTO.getNameRef().getUuid();
            }

            identifierSet = new TypeDesignationWorkingsetEditorIdSet(
                    event.getRegistrationUuid(),
                    getView().getCitationUuid(),
                    typifiedNameUuid
                    );
            popup.grantToCurrentUser(EnumSet.of(CRUD.UPDATE, CRUD.DELETE));
            popup.loadInEditor(identifierSet);
            popup.withDeleteButton(true);
            if(event.hasSource()){
                // propagate readonly state from source component to popup
                popup.setReadOnly(event.getSource().isReadOnly());
            }
        } else {
            NameTypeDesignationPopupEditor popup = openPopupEditor(NameTypeDesignationPopupEditor.class, event);
            popup.setParentEditorActionContext(event.getContext(), event.getTarget());
            popup.withDeleteButton(true);
            popup.grantToCurrentUser(EnumSet.of(CRUD.UPDATE, CRUD.DELETE));
            RegistrationDTO regDto = workingset.getRegistrationDTO(event.getRegistrationUuid()).get();
            Reference citation = regDto.getCitation();
            nameTypeDesignationPopupEditorRegistrationUUIDMap.put(popup, event.getRegistrationUuid());
            popup.setBeanInstantiator(new BeanInstantiator<NameTypeDesignation>() {

                @Override
                public NameTypeDesignation createNewBean() {

                    TaxonName typifiedName = getRepo().getNameService().load(event.getTypifiedNameUuid(), Arrays.asList(new String[]{"typeDesignations", "homotypicalGroup"}));
                    NameTypeDesignation nameTypeDesignation  = NameTypeDesignation.NewInstance();
                    nameTypeDesignation.setCitation(citation);
                    nameTypeDesignation.getTypifiedNames().add(typifiedName);
                    return nameTypeDesignation;
                }
            });
            popup.loadInEditor(null);
            popup.getCitationCombobox().setEnabled(false);
            popup.getTypifiedNamesComboboxSelect().setEnabled(false);
            if(event.hasSource()){
                // propagate readonly state from source component to popup
                popup.setReadOnly(event.getSource().isReadOnly());
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
                // NOTE: adding the SpecimenTypeDesignations to the registration is done in the
                // SpecimenTypeDesignationWorkingSetServiceImpl.save(SpecimenTypeDesignationWorkingSetDTO dto) method
                refreshView(true);
            } else if(event.getReason().equals(Reason.CANCEL)){
                // noting to do
            }
        } else if(event.getPopup() instanceof NameTypeDesignationPopupEditor){
            if(event.getReason().equals(Reason.SAVE)){
                UUID typeDesignationUuid = ((NameTypeDesignationPopupEditor)event.getPopup()).getBean().getUuid();
                getRepo().getSession().clear();
                UUID regUUID = nameTypeDesignationPopupEditorRegistrationUUIDMap.get(event.getPopup());
                getRepo().getRegistrationService().addTypeDesignation(regUUID, typeDesignationUuid);
                getRepo().getSession().clear();
                nameTypeDesignationPopupEditorRegistrationUUIDMap.remove(event.getPopup());
                refreshView(true);
            } else if(event.getReason().equals(Reason.CANCEL)){
                // noting to do
            }

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

            if(workingset.getCitationUuid().equals(event.getEntityUuid())){
                if(event.isRemovedType()){
                    viewEventBus.publish(EventScope.UI, this, new NavigationEvent(StartRegistrationViewBean.NAME));
                } else {
                    refreshView(true);
                }
            }

        } else
        if(Registration.class.isAssignableFrom(event.getEntityType())){
            if(workingset.getRegistrations().stream().anyMatch(reg -> reg.getUuid() == event.getEntityUuid())){
                refreshView(true);
            }
        } else
        if(TaxonName.class.isAssignableFrom(event.getEntityType())){
            if(event.getType().equals(EntityChangeEvent.Type.CREATED)){
                Stack<EditorActionContext>context = ((AbstractPopupEditor)event.getSourceView()).getEditorActionContext();
                EditorActionContext rootContext = context.get(0);
                if(rootContext.getParentView().equals(getView()) && event.getSourceView() != newNameForRegistrationPopupEditor){

                    try {
                        getRepo().getSession().clear();
                        TransactionStatus txStatus = getRepo().startTransaction();
                        // create a blocking registration, the new Registration will be persisted
                        UUID taxonNameUUID = event.getEntityUuid();
                        Registration blockingRegistration = getRepo().getRegistrationService().createRegistrationForName(taxonNameUUID);

                        if(context.get(1).getParentView() instanceof TaxonNamePopupEditor && !((TaxonNamePopupEditor)context.get(1).getParentView()).getBean().cdmEntity().isPersited()){
                            // Oha!! The event came from a popup editor and the
                            // first popup in the context is a TaxonNameEditor with un-persisted name
                            // This is a name for a new registration which has not yet been created.
                            // It is necessary to store blocking registrations in the newNameBlockingRegistrations
                            newNameBlockingRegistrations.add(blockingRegistration);
                            logger.debug("Blocking registration created and memorized");
                        } else {
                            // some new name related somehow to an existing registration
                            TypedEntityReference<Registration> regReference = (TypedEntityReference<Registration>)rootContext.getParentEntity();
                            RegistrationDTO registrationDTO = workingset.getRegistrationDTO(regReference.getUuid()).get();
                            Registration registration = registrationDTO.registration();

                                registration = getRepo().getRegistrationService().load(registration.getUuid());
                                if(registration == null){
                                    throw new NullPointerException("Registration not found for " + regReference + " which has been hold in the rootContext");
                                }
                                registration.getBlockedBy().add(blockingRegistration);
                                getRepo().getRegistrationService().saveOrUpdate(registration);
                                getRepo().commitTransaction(txStatus);
                            logger.debug("Blocking registration created and added to registion");
                        }
                    } finally {
                        getRepo().getSession().clear();
                    }
                } else {
                    // in case of creating a new name for a registration the parent view is the TaxonNamePopupEditor
                    // this is set
                    logger.debug("Non blocking registration, since a new name for a new registration has been created");
                }
            }
            if(workingset.getRegistrationDTOs().stream().anyMatch(reg ->
                reg.getTypifiedNameRef() != null
                && reg.getTypifiedNameRef().getUuid().equals(event.getEntityUuid()))){
                    refreshView(true);
            }
        } else
        if(TypeDesignationBase.class.isAssignableFrom(event.getEntityType())){
            if(workingset.getRegistrationDTOs().stream().anyMatch(
                    reg -> reg.typeDesignations() != null && reg.typeDesignations().stream().anyMatch(
                            td -> td.getUuid() == event.getEntityUuid()
                            )
                        )
                    ){
                refreshView(true);
            }
        }
    }

    @EventBusListenerMethod(filter = ShowDetailsEventEntityTypeFilter.RegistrationDTO.class)
    public void onShowDetailsEventForRegistrationDTO(ShowDetailsEvent<RegistrationDTO, UUID> event) {

        // FIXME check from own view!!!
        if(getView() == null){
            return;
        }

        UUID registrationUuid = event.getIdentifier();

        RegistrationDTO regDto = getWorkingSetService().loadDtoByUuid(registrationUuid);
        if(event.getProperty().equals(RegistrationItem.BLOCKED_BY)){

            Set<RegistrationDTO> blockingRegs = getWorkingSetService().loadBlockingRegistrations(registrationUuid);
            getView().setBlockingRegistrations(registrationUuid, blockingRegs);
        } else if(event.getProperty().equals(RegistrationItem.MESSAGES)){

            RegistrationMessagesPopup popup = openPopupEditor(RegistrationMessagesPopup.class, null);
            popup.loadMessagesFor(regDto.getUuid());

        } else if(event.getProperty().equals(RegistrationItem.VALIDATION_PROBLEMS)){
            getView().openDetailsPopup("Validation Problems", regDto.getValidationProblems());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICdmEntityUuidCacher getCache() {
        return cache;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addRootEntity(CdmBase entity) {
        rootEntities.add(entity);
        cache.load(entity);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<CdmBase> getRootEntities() {
        return rootEntities;
    }

    @Override
    public void destroy() throws Exception {
        super.destroy();
        disposeCache();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disposeCache() {
        cache.dispose();
    }

}
