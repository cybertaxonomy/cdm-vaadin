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
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.server.ClientConnector.DetachEvent;
import com.vaadin.server.ClientConnector.DetachListener;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import eu.etaxonomy.cdm.api.service.config.RegistrationStatusTransitions;
import eu.etaxonomy.cdm.api.service.dto.RegistrationDTO;
import eu.etaxonomy.cdm.api.service.dto.RegistrationWorkingSet;
import eu.etaxonomy.cdm.api.service.exception.RegistrationValidationException;
import eu.etaxonomy.cdm.api.service.name.TypeDesignationWorkingSet.TypeDesignationWorkingSetType;
import eu.etaxonomy.cdm.api.service.registration.IRegistrationWorkingSetService;
import eu.etaxonomy.cdm.api.util.UserHelper;
import eu.etaxonomy.cdm.cache.CdmTransientEntityAndUuidCacher;
import eu.etaxonomy.cdm.model.ICdmEntityUuidCacher;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.name.NameTypeDesignation;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.RegistrationStatus;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.name.TaxonNameFactory;
import eu.etaxonomy.cdm.model.name.TypeDesignationBase;
import eu.etaxonomy.cdm.model.occurrence.FieldUnit;
import eu.etaxonomy.cdm.model.permission.CRUD;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.ref.TypedEntityReference;
import eu.etaxonomy.cdm.service.CdmBeanItemContainerFactory;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProvider;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProviderFactory;
import eu.etaxonomy.cdm.service.CdmStore;
import eu.etaxonomy.cdm.service.IRegistrationWorkflowService;
import eu.etaxonomy.cdm.service.UserHelperAccess;
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
import eu.etaxonomy.cdm.vaadin.ui.RegistrationUIDefaults;
import eu.etaxonomy.cdm.vaadin.ui.config.TaxonNamePopupEditorConfig;
import eu.etaxonomy.cdm.vaadin.util.CdmTitleCacheCaptionGenerator;
import eu.etaxonomy.cdm.vaadin.view.name.CachingPresenter;
import eu.etaxonomy.cdm.vaadin.view.name.NameTypeDesignationPopupEditor;
import eu.etaxonomy.cdm.vaadin.view.name.NameTypeDesignationWorkingsetIds;
import eu.etaxonomy.cdm.vaadin.view.name.SpecimenTypeDesignationWorkingsetIds;
import eu.etaxonomy.cdm.vaadin.view.name.SpecimenTypeDesignationWorkingsetPopupEditor;
import eu.etaxonomy.cdm.vaadin.view.name.TaxonNameEditorPresenter;
import eu.etaxonomy.cdm.vaadin.view.name.TaxonNamePopupEditor;
import eu.etaxonomy.cdm.vaadin.view.name.TaxonNamePopupEditorMode;
import eu.etaxonomy.cdm.vaadin.view.reference.ReferencePopupEditor;
import eu.etaxonomy.vaadin.mvp.AbstractPopupEditor;
import eu.etaxonomy.vaadin.mvp.AbstractPresenter;
import eu.etaxonomy.vaadin.mvp.AbstractView;
import eu.etaxonomy.vaadin.mvp.BeanInstantiator;
import eu.etaxonomy.vaadin.ui.navigation.NavigationEvent;
import eu.etaxonomy.vaadin.ui.view.DoneWithPopupEvent;
import eu.etaxonomy.vaadin.ui.view.DoneWithPopupEvent.Reason;
import eu.etaxonomy.vaadin.ui.view.PopupView;

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
    private IRegistrationWorkflowService registrationWorkflowService;

    @Autowired
    private CdmFilterablePagingProviderFactory pagingProviderFactory;

    @Autowired
    private CdmBeanItemContainerFactory selectFieldFactory;

    @Autowired
    private CdmStore cdmStore;

    /**
     * @return the regWorkingSetService
     */
    public IRegistrationWorkingSetService getWorkingSetService() {
        return regWorkingSetService;
    }

    private RegistrationWorkingSet workingset;

    /**
     * Contains the poupeditor which has been opened to start the registration of a new name as long as it has not been saved or canceled.
     * There can always only be one popup editor for this purpose.
     */
    private TaxonNamePopupEditor newNameForRegistrationPopupEditor = null;

    /**
     * Contains
     */
    private List<Registration> newNameBlockingRegistrations = new ArrayList<>();

    /**
     * TODO is this still needed? The registration UUID should be accessible in the popup editor context,
     * see findRegistrationInContext()
     */
    private Map<NameTypeDesignationPopupEditor, UUID> nameTypeDesignationPopupEditorRegistrationUUIDMap = new HashMap<>();


    private ICdmEntityUuidCacher cache;

    private Collection<CdmBase> rootEntities = new HashSet<>();


    public RegistrationWorkingsetPresenter() {
    }

    /**
     * @param doReload reload the workingset from the persistent storage.
     *  Workingsets which are not yet persisted are preserved.
     */
    protected void refreshView(boolean doReload) {

        if(workingset == null){
            return; // nothing to do
        }
        if(doReload){
            if(logger.isDebugEnabled()){
                logger.debug("refreshView() - workingset:\n" + workingset.toString());
            }
            List<RegistrationDTO> unpersisted = new ArrayList<>();
            for(RegistrationDTO regDto : workingset.getRegistrationDTOs()){
                if(!regDto.registration().isPersited()){
                    unpersisted.add(regDto);
                }
            }
            loadWorkingSet(workingset.getCitationUuid());
            for(RegistrationDTO regDtoUnpersisted : unpersisted){
                if(!workingset.getRegistrationDTOs().stream().anyMatch(dto -> dto.getUuid().equals(regDtoUnpersisted.getUuid()))){
                    // only add if the regDtoUnpersisted has not been persisted meanwhile
                    try {
                        workingset.add(regDtoUnpersisted);
                    } catch (RegistrationValidationException e) {
                        // would never happen here //
                    }
                }
            }
            if(logger.isDebugEnabled()){
                logger.debug("refreshView() - workingset reloaded:\n" + workingset.toString());
            }
        }
        applyWorkingset();
    }

    @Override
    public void handleViewEntered() {
        super.handleViewEntered();
        // TODO currently cannot specify type more precisely, see AbstractSelect
        // FIXME externalize into class file!!!!!!!!!!!!
        getView().setStatusComponentInstantiator(new RegistrationStatusFieldInstantiator<Object>(){

            private static final long serialVersionUID = 7099181280977511048L;

            @Override
            public AbstractField<Object> create(RegistrationDTO regDto) {

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

                RegistrationStatusSelect select = new RegistrationStatusSelect(null, selectFieldFactory.buildEnumTermItemContainer(
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
    }

    protected void activateComboboxes() {
        CdmTitleCacheCaptionGenerator<TaxonName> titleCacheGenerator = new CdmTitleCacheCaptionGenerator<TaxonName>();
        getView().getAddExistingNameCombobox().setCaptionGenerator(titleCacheGenerator);
        CdmFilterablePagingProvider<TaxonName, TaxonName> pagingProvider = pagingProviderFactory.taxonNamesWithoutOrthophicIncorrect();
        getView().getAddExistingNameCombobox().loadFrom(pagingProvider, pagingProvider, pagingProvider.getPageSize());
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
                reg.updateStatusAndDate((RegistrationStatus)value);
                cdmStore.saveBean(reg, (AbstractView)getView());
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

        boolean isAddExistingNameRegistration = event.getTarget() != null && event.getTarget().equals(getView().getAddExistingNameCombobox());

        TaxonNamePopupEditor popup = openPopupEditor(TaxonNamePopupEditor.class, event);

        popup.setParentEditorActionContext(event.getContext(), event.getTarget());
        popup.withDeleteButton(!isAddExistingNameRegistration);
        TaxonNamePopupEditorConfig.configureForNomenclaturalAct(popup);
        if(isAddExistingNameRegistration){
            // allow saving even if the name parts are not valid
            // the user will need to fix this in a later step
            popup.disableMode(TaxonNamePopupEditorMode.VALIDATE_AGAINST_HIGHER_NAME_PART);
            getView().getAddExistingNameRegistrationButton().setEnabled(false);
            popup.addDetachListener(new DetachListener() {

                @Override
                public void detach(DetachEvent event) {
                    getView().getAddExistingNameRegistrationButton().setEnabled(true);

                }
            });
        }
        popup.loadInEditor(event.getEntityUuid());
        if(event.hasSource() && event.getSource().isReadOnly()){
            // avoid resetting readonly to false
            popup.setReadOnly(true);
        }

        boolean hasNomRef = popup.getBean().getNomenclaturalReference() != null;
        if(isAddExistingNameRegistration){
            popup.setAllFieldsReadOnly(true);
            if(!hasNomRef){
                // only allow editing the nomenclatural reference, all other
                // editing need to be done another way.
                // Otherwise we would need to be prepared for creating blocking registrations
                // in turn of creation, modification of related taxon names.
                popup.disableMode(TaxonNamePopupEditorMode.NOMENCLATURALREFERENCE_SECTION_EDITING_ONLY);
                popup.getNomReferenceCombobox().setReadOnly(false);
                popup.getNomenclaturalReferenceDetail().setReadOnly(false);
                popup.addStatusMessage("The chosen name needs to be completed before it can be used. "
                        + "Please add the nomenclatural reference and click on \"Save\" to proceed with entering the type of this name.");
            } else {
                popup.addStatusMessage("You are about to create a registration for this name. "
                        + "This editor is for reviewing the name only. Therefore, all fields have been switched to readonly state. "
                        + "Click on \"Save\" to proceed with entering the type of this name.");
            }
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
     *      passes on the Exception which may come from onRegistrationWorkflowEventActionStart()
     */
    @EventBusListenerMethod
    public void onDoneWithTaxonnameEditor(DoneWithPopupEvent event) throws RegistrationValidationException {

        if(!isFromOwnView(event)){
            return;
        }

        if(event.getPopup() instanceof TaxonNamePopupEditor){

                EditorActionContext rootContext = editorActionContextRoot(event.getPopup());
                boolean isAddExistingNameRegistration = rootContext.getTargetField() != null && rootContext.getTargetField().equals(getView().getAddExistingNameCombobox());

                if(isAddExistingNameRegistration){
                    if(event.getReason().equals(Reason.SAVE)){
                    onRegistrationWorkflowEventActionStart(new RegistrationWorkingsetAction(workingset.getCitationUuid(),
                            RegistrationWorkingsetAction.Action.start));
                    }
                    // just ignore on CANCEL
                } else {
                    Optional<Registration> registrationOpt = Optional.ofNullable(null);
                    if(newNameForRegistrationPopupEditor != null && event.getPopup().equals(newNameForRegistrationPopupEditor)){
                        if(event.getReason().equals(Reason.SAVE)){
                            try {
                                TaxonName taxonName = newNameForRegistrationPopupEditor.getBean().cdmEntity();
                                registrationOpt = Optional.of(registrationWorkflowService.createRegistration(taxonName, newNameBlockingRegistrations));
                                loadWorkingSet(workingset.getCitationUuid());
                            } finally {
                                clearSession();
                                getView().getAddNewNameRegistrationButton().setEnabled(true);
                            }
                        }
                        // nullify and clear the memory on this popup editor in any case (SAVE, DELETE, CANCEL)
                        newNameForRegistrationPopupEditor = null;
                        newNameBlockingRegistrations.clear();
                        getView().getAddNewNameRegistrationButton().setEnabled(true);
                    }

                    if(event.getReason().equals(Reason.SAVE)){
                        if(!registrationOpt.isPresent()){
                            // no new registration has been created above, so there must be an existing one.
                            registrationOpt = findRegistrationInContext(event.getPopup());
                        }

                        // Check if the other names used in the context of the name are registered yet.
                        TaxonNamePopupEditor nameEditor = (TaxonNamePopupEditor)event.getPopup();
                        Set<TaxonName> namesToCheck = new HashSet<>();

                        namesToCheck.addAll(nameEditor.getBasionymComboboxSelect().getValue());
                        namesToCheck.addAll(nameEditor.getReplacedSynonymsComboboxSelect().getValue());
                        namesToCheck.add(nameEditor.getValidationField().getRelatedNameComboBox().getValue());
                        namesToCheck.add(nameEditor.getOrthographicVariantField().getRelatedNameComboBox().getValue());
                        // NOTE: according to https://dev.e-taxonomy.eu/redmine/issues/8049#note-2 we will not create blocking
                        // registrations for names in WeaklyRelatedEntityFields

                        for(TaxonName name : namesToCheck){
                            if(name != null){
                                clearSession();
                                assocciateOrQueueBlockingRegistration(registrationOpt, name.getUuid());
                            }
                        }
                    } else if (event.getReason().equals(Reason.DELETE)){
                        //FIXME handle delete: need to remove blocking registrations?
                    }
                    // always reload if the first editor is closed as the data might have been changed through any other sub-popupeditor
                    refreshView(isAtContextRoot(event.getPopup()));
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
            try {
                doReloadWorkingSet = registrationWorkflowService.createRegistrationforExistingName(workingset, typifiedName);
            } finally {
                clearSession();
                refreshView(doReloadWorkingSet);
                getView().getAddExistingNameRegistrationButton().setEnabled(false);
            }
        } else {
            logger.error("Seletced name is NULL");
        }

    }

    @EventBusListenerMethod(filter = EditorActionTypeFilter.Edit.class)
    public void onTypeDesignationsEditorActionEdit(TypeDesignationWorkingsetEditorAction event) {

        if(!checkFromOwnView(event)){
            return;
        }

        RegistrationDTO registrationDTO = workingset.getRegistrationDTO(event.getRegistrationUuid()).get();

        if(event.getWorkingSetType() == TypeDesignationWorkingSetType.SPECIMEN_TYPE_DESIGNATION_WORKINGSET ){
            SpecimenTypeDesignationWorkingsetPopupEditor popup = openPopupEditor(SpecimenTypeDesignationWorkingsetPopupEditor.class, event);
            popup.setParentEditorActionContext(event.getContext(), event.getTarget());
            popup.withDeleteButton(true);
            popup.loadInEditor(new SpecimenTypeDesignationWorkingsetIds(
                    workingset.getCitationUuid(),
                    event.getRegistrationUuid(),
                    event.getBaseEntityRef().castTo(FieldUnit.class), null));
            if(event.hasSource()){
                // propagate readonly state from source button to popup
                popup.setReadOnly(event.getSource().isReadOnly());
            }
        } else {
            NameTypeDesignationPopupEditor popup = openPopupEditor(NameTypeDesignationPopupEditor.class, event);
            popup.setParentEditorActionContext(event.getContext(), event.getTarget());
            popup.withDeleteButton(true);
            popup.loadInEditor(NameTypeDesignationWorkingsetIds.forExistingTypeDesignation(
                    registrationDTO.getCitationUuid(),
                    event.getBaseEntityRef().castTo(NameTypeDesignation.class))
                    );
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

        RegistrationDTO registrationDTO = workingset.getRegistrationDTO(event.getRegistrationUuid()).get();

        if(event.getWorkingSetType() == TypeDesignationWorkingSetType.SPECIMEN_TYPE_DESIGNATION_WORKINGSET){
            SpecimenTypeDesignationWorkingsetPopupEditor popup = openPopupEditor(SpecimenTypeDesignationWorkingsetPopupEditor.class, event);
            popup.setParentEditorActionContext(event.getContext(), event.getTarget());
            TypedEntityReference<TaxonName> typifiedNameRef;
            if(registrationDTO.getTypifiedNameRef() != null){
                // case for registrations without name, in which case the typifiedName is only defined via the typedesignations
                typifiedNameRef = new TypedEntityReference(TaxonName.class, registrationDTO.getTypifiedNameRef().getUuid());
            } else {
                // case of registrations with a name in the nomenclatural act.
                typifiedNameRef = new TypedEntityReference(TaxonName.class, registrationDTO.getNameRef().getUuid());
            }

            popup.grantToCurrentUser(EnumSet.of(CRUD.UPDATE, CRUD.DELETE));
            popup.withDeleteButton(false);
            popup.loadInEditor(new SpecimenTypeDesignationWorkingsetIds(
                        workingset.getCitationUuid(),
                        event.getRegistrationUuid(),
                        null,
                        typifiedNameRef.getUuid()
                        )
                    );
            if(event.hasSource()){
                // propagate readonly state from source component to popup
                popup.setReadOnly(event.getSource().isReadOnly());
            }
        } else {
            NameTypeDesignationPopupEditor popup = openPopupEditor(NameTypeDesignationPopupEditor.class, event);
            popup.setParentEditorActionContext(event.getContext(), event.getTarget());
            popup.grantToCurrentUser(EnumSet.of(CRUD.UPDATE, CRUD.DELETE));
            nameTypeDesignationPopupEditorRegistrationUUIDMap.put(popup, event.getRegistrationUuid());
            popup.setBeanInstantiator(new BeanInstantiator<NameTypeDesignation>() {

                @Override
                public NameTypeDesignation createNewBean() {

                    TaxonName typifiedName = getRepo().getNameService().load(event.getTypifiedNameUuid(), Arrays.asList(new String[]{
                            "typeDesignations",
                            "homotypicalGroup",
                            "nomenclaturalSource.citation"
                            }));
                    NameTypeDesignation nameTypeDesignation  = NameTypeDesignation.NewInstance();
                    nameTypeDesignation.getTypifiedNames().add(typifiedName);
                    return nameTypeDesignation;
                }
            });
            popup.withDeleteButton(false);
            popup.loadInEditor(NameTypeDesignationWorkingsetIds.forNewTypeDesignation(
                    registrationDTO.getCitationUuid(),
                    event.getTypifiedNameUuid()
                    )
                );
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
    public void onDoneWithTypeDesignationEditor(DoneWithPopupEvent event) {

        if(!isFromOwnView(event)){
            return;
        }

        if(event.getPopup() instanceof SpecimenTypeDesignationWorkingsetPopupEditor){
            if(event.getReason().equals(Reason.SAVE)){
                // NOTE: adding the SpecimenTypeDesignations to the registration is done in the
                // SpecimenTypeDesignationWorkingSetServiceImpl.save(SpecimenTypeDesignationWorkingSetDTO dto) method
            }
            // always reload if the first editor is closed as the data might have been changed through any other sub-popupeditor
            refreshView(isAtContextRoot(event.getPopup()));
        } else if(event.getPopup() instanceof NameTypeDesignationPopupEditor){
            if(event.getReason().equals(Reason.SAVE)){

                Optional<Registration> registrationOpt = Optional.ofNullable(null);
                UUID typeDesignationUuid = ((NameTypeDesignationPopupEditor)event.getPopup()).getBean().getUuid();
                try {
                    clearSession();
                    registrationOpt = findRegistrationInContext(event.getPopup());
                    registrationOpt.ifPresent(reg -> {
                        registrationWorkflowService.addTypeDesignation(typeDesignationUuid, reg);
                        nameTypeDesignationPopupEditorRegistrationUUIDMap.remove(event.getPopup());
                        });

                } finally {
                    clearSession();
                }

                // Check if other names used in the context of the name are registered yet.
                NameTypeDesignationPopupEditor nameTypeDesignationEditor = (NameTypeDesignationPopupEditor)event.getPopup();
                Set<TaxonName> namesToCheck = new HashSet<>();

                namesToCheck.add(nameTypeDesignationEditor.getTypeNameField().getValue());

                for(TaxonName name : namesToCheck){
                    if(name != null){
                        assocciateOrQueueBlockingRegistration(registrationOpt, name.getUuid());
                    }

                }

            } else if(event.getReason().equals(Reason.CANCEL)){
                // noting to do
            }
            // always reload if the first editor is closed as the data might have been changed through any other sub-popupeditor
            refreshView(isAtContextRoot(event.getPopup()));

        }
        // ignore other editors
    }

    private void assocciateOrQueueBlockingRegistration(Optional<Registration> registrationOpt, UUID nameUuid) {
        registrationOpt.ifPresent(reg -> registrationWorkflowService.addBlockingRegistration(nameUuid, reg));
        if(!registrationOpt.isPresent()){
            // not present!
            Registration blockingRegistration = registrationWorkflowService.prepareBlockingRegistration(nameUuid);
            if(blockingRegistration != null){
                newNameBlockingRegistrations.add(blockingRegistration);
                logger.debug("Blocking registration created and queued for later association with the main registration.");
            }
        }
    }


    public void clearSession() {
        getRepo().clearSession();
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

        if(!isFromOwnView(event)){
            return;
        }

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
                        clearSession();
                        // create a blocking registration
                        UUID taxonNameUUID = event.getEntityUuid();
                        Optional<Registration> registrationOpt = findRegistrationInContext(context);
                        assocciateOrQueueBlockingRegistration(registrationOpt, taxonNameUUID);
                    } finally {
                        clearSession();
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
                    //refreshView(true);
            }
        } else
        if(TypeDesignationBase.class.isAssignableFrom(event.getEntityType())){
            if(workingset.getRegistrationDTOs().stream().anyMatch(
                    reg -> reg.typeDesignations() != null && reg.typeDesignations().stream().anyMatch(
                            td -> td.getUuid() == event.getEntityUuid()
                            )
                        )
                    ){
                //refreshView(true);
            }
        }
    }

    public Optional<Registration> findRegistrationInContext(PopupView popupView) {
        Stack<EditorActionContext>context = ((AbstractPopupEditor)popupView).getEditorActionContext();
        return findRegistrationInContext(context);
    }

    /**
     * Finds the Registration in the EditorContext stack
     */
    public Optional<Registration> findRegistrationInContext(Stack<EditorActionContext> context) {
        EditorActionContext rootCtx = context.get(0);
        TypedEntityReference<Registration> regReference = (TypedEntityReference<Registration>)rootCtx.getParentEntity();
        Optional<RegistrationDTO> registrationDTOOptional = workingset.getRegistrationDTO(regReference.getUuid());
        Optional<Registration> registrationOptional;
        if(!registrationDTOOptional.isPresent()){
            logger.debug("No RegistrationDTO in found rootCtx -> user is about to create a registration for a new name.");
            registrationOptional = Optional.ofNullable(null);
        }

        Optional<Registration> regOpt;
        if(registrationDTOOptional.isPresent()){
            regOpt = Optional.of(registrationDTOOptional.get().registration());
        } else {
            regOpt = Optional.ofNullable(null);
        }

        return regOpt;
    }

    @EventBusListenerMethod(filter = ShowDetailsEventEntityTypeFilter.RegistrationDTO.class)
    public void onShowDetailsEventForRegistrationDTO(ShowDetailsEvent<RegistrationDTO, UUID> event) {

        // FIXME check from own view!!!
        if(getView() == null){
            return;
        }

        UUID registrationUuid = event.getIdentifier();

        RegistrationDTO regDto = workingset.getRegistrationDTO(registrationUuid).get();
        if(event.getProperty().equals(RegistrationItem.BLOCKED_BY)){

            Set<RegistrationDTO> blockingRegs;
            if(regDto.registration().isPersited()){
                blockingRegs = getWorkingSetService().loadBlockingRegistrations(registrationUuid);
            } else {
                blockingRegs = new HashSet<RegistrationDTO>(getWorkingSetService().makeDTOs(regDto.registration().getBlockedBy()));
            }
            getView().setBlockingRegistrations(registrationUuid, blockingRegs);
        } else if(event.getProperty().equals(RegistrationItem.VALIDATION_PROBLEMS)){
            getView().openDetailsPopup("Validation Problems", regDto.getValidationProblems());
        }
    }

    @Override
    public ICdmEntityUuidCacher getCache() {
        return cache;
    }

    @Override
    public void addRootEntity(CdmBase entity) {
        rootEntities.add(entity);
        cache.load(entity);
    }

    @Override
    public Collection<CdmBase> getRootEntities() {
        return rootEntities;
    }

    @Override
    public void destroy() throws Exception {
        super.destroy();
        disposeCache();
    }

    @Override
    public void disposeCache() {
        cache.dispose();
    }

    public boolean canCreateNameRegistrationFor(TaxonName name) {
        return registrationWorkflowService.canCreateNameRegistrationFor(workingset, name);
    }

    public boolean checkWokingsetContainsProtologe(TaxonName name) {
        return registrationWorkflowService.checkWokingsetContainsProtologe(workingset, name);
    }
}