/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.name;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.spring.annotation.SpringComponent;

import eu.etaxonomy.cdm.api.service.DeleteResult;
import eu.etaxonomy.cdm.api.service.IService;
import eu.etaxonomy.cdm.api.service.dto.RegistrationDTO;
import eu.etaxonomy.cdm.api.service.name.TypeDesignationSetManager.TypeDesignationWorkingSet;
import eu.etaxonomy.cdm.model.name.NameTypeDesignation;
import eu.etaxonomy.cdm.model.name.NameTypeDesignationStatus;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProvider;
import eu.etaxonomy.cdm.service.CdmStore;
import eu.etaxonomy.cdm.service.IRegistrationWorkingSetService;
import eu.etaxonomy.cdm.vaadin.component.CdmBeanItemContainerFactory;
import eu.etaxonomy.cdm.vaadin.event.EditorActionTypeFilter;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent.Type;
import eu.etaxonomy.cdm.vaadin.event.TaxonNameEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityButtonUpdater;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityReloader;
import eu.etaxonomy.cdm.vaadin.security.UserHelper;
import eu.etaxonomy.cdm.vaadin.util.CdmTitleCacheCaptionGenerator;
import eu.etaxonomy.vaadin.mvp.AbstractCdmEditorPresenter;
import eu.etaxonomy.vaadin.mvp.AbstractView;

/**
 * @author a.kohlbecker
 * @since Jan 26, 2018
 *
 */
@SpringComponent
@Scope("prototype")
public class NameTypeDesignationPresenter
        extends AbstractCdmEditorPresenter<NameTypeDesignation, NameTypeDesignationEditorView> {

    @Autowired
    private IRegistrationWorkingSetService registrationWorkingSetService;

    HashSet<TaxonName> typifiedNamesAsLoaded;

    private TaxonNamePopupEditor typeNamePopup;

    private TaxonName typifiedNameInContext;


    /**
     * {@inheritDoc}
     */
    @Override
    protected NameTypeDesignation loadBeanById(Object identifier) {
        if(identifier instanceof Integer || identifier == null){
            return super.loadBeanById(identifier);
//        } else if(identifier instanceof TypedEntityReference && ((TypedEntityReference)identifier).getType().equals(TaxonName.class)) {
//            typifiedNameInContext = getRepo().getNameService().find(((TypedEntityReference)identifier).getUuid());
//            bean = super.loadBeanById(null);
        } else {
            TypeDesignationWorkingsetEditorIdSet idset = (TypeDesignationWorkingsetEditorIdSet)identifier;
            RegistrationDTO regDTO = registrationWorkingSetService.loadDtoByUuid(idset.registrationUuid);
            typifiedNameInContext = regDTO.getTypifiedName();
            // find the working set
            TypeDesignationWorkingSet typeDesignationWorkingSet = regDTO.getTypeDesignationWorkingSet(idset.baseEntityRef);

            // NameTypeDesignation bameTypeDesignation = regDTO.getNameTypeDesignation(typeDesignationWorkingSet.getBaseEntityReference());
            if(!typeDesignationWorkingSet.getBaseEntityReference().getType().equals(NameTypeDesignation.class)){
                throw new RuntimeException("TypeDesignationWorkingsetEditorIdSet references not a NameTypeDesignation");
            }
            // TypeDesignationWorkingSet for NameTyped only contain one item!!!
            UUID nameTypeDesignationUuid = typeDesignationWorkingSet.getTypeDesignations().get(0).getUuid();
            return super.loadBeanById(nameTypeDesignationUuid);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected NameTypeDesignation loadCdmEntity(UUID uuid) {
        List<String> initStrategy = Arrays.asList(new String []{
                "$",
                "typifiedNames.typeDesignations", // important !!
                "typeName.$",
                "citation.authorship.$",
                }
        );

        NameTypeDesignation typeDesignation;
        if(uuid != null){
            typeDesignation = (NameTypeDesignation) getRepo().getNameService().loadTypeDesignation(uuid, initStrategy);
        } else {
            if(beanInstantiator != null){
                typeDesignation = beanInstantiator.createNewBean();
            } else {
                typeDesignation = NameTypeDesignation.NewInstance();
            }
        }

        typifiedNamesAsLoaded = new HashSet<>(typeDesignation.getTypifiedNames());

        return typeDesignation;
    }




    /**
     * {@inheritDoc}
     */
    @Override
    public void handleViewEntered() {

        CdmBeanItemContainerFactory selectFactory = new CdmBeanItemContainerFactory(getRepo());
        getView().getTypeStatusSelect().setContainerDataSource(selectFactory.buildBeanItemContainer(NameTypeDesignationStatus.class));
        getView().getTypeStatusSelect().setItemCaptionPropertyId("description");

        getView().getCitationCombobox().getSelect().setCaptionGenerator(new CdmTitleCacheCaptionGenerator<Reference>());
        CdmFilterablePagingProvider<Reference,Reference> referencePagingProvider = new CdmFilterablePagingProvider<Reference, Reference>(getRepo().getReferenceService());
        getView().getCitationCombobox().loadFrom(referencePagingProvider, referencePagingProvider, referencePagingProvider.getPageSize());
        getView().getCitationCombobox().getSelect().addValueChangeListener(new ToOneRelatedEntityButtonUpdater<Reference>(getView().getCitationCombobox()));
        getView().getCitationCombobox().getSelect().addValueChangeListener(new ToOneRelatedEntityReloader<>(getView().getCitationCombobox(), this));

        CdmFilterablePagingProvider<TaxonName,TaxonName> namePagingProvider = new CdmFilterablePagingProvider<TaxonName, TaxonName>(getRepo().getNameService());
        getView().getTypeNameField().loadFrom(namePagingProvider, namePagingProvider, namePagingProvider.getPageSize());
        getView().getTypeNameField().getSelect().addValueChangeListener(new ToOneRelatedEntityButtonUpdater<TaxonName>(getView().getTypeNameField()));
        getView().getTypeNameField().getSelect().addValueChangeListener(new ToOneRelatedEntityReloader<>(getView().getTypeNameField(), this));

        getView().getTypifiedNamesComboboxSelect().setPagingProviders(namePagingProvider, namePagingProvider, namePagingProvider.getPageSize(), this);

    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void guaranteePerEntityCRUDPermissions(UUID identifier) {
        if(crud != null){
            newAuthorityCreated = UserHelper.fromSession().createAuthorityForCurrentUser(NameTypeDesignation.class, identifier, crud, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void guaranteePerEntityCRUDPermissions(NameTypeDesignation bean) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IService<NameTypeDesignation> getService() {
        // No TypeDesignationService :( so I need override the generic save and delete methods
        return null;
    }

    @Override
    protected void deleteBean(NameTypeDesignation bean){
        // deleteTypedesignation(uuid, uuid) needs to be called so the name is loaded in the transaction of the method and is saved.
        DeleteResult deletResult = getRepo().getNameService().deleteTypeDesignation(typifiedNameInContext.getUuid(), bean.getUuid());
        if(deletResult.isOk()){
            EntityChangeEvent changeEvent = new EntityChangeEvent(bean, Type.REMOVED, (AbstractView) getView());
            viewEventBus.publish(this, changeEvent);
        } else {
            CdmStore.handleDeleteresultInError(deletResult);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected NameTypeDesignation handleTransientProperties(NameTypeDesignation bean) {

        // the typifiedNames can only be set on the name side, so we need to
        // handle changes explicitly here
        HashSet<TaxonName> typifiedNames = new HashSet<>(bean.getTypifiedNames());

        // handle adds
        for(TaxonName name : typifiedNames){
            if(name == null){
                throw new NullPointerException("typifiedName must not be null");
            }
            if(!name.getTypeDesignations().contains(bean)){
                name.addTypeDesignation(bean, false);
            }
        }
        // handle removed
        for(TaxonName name : typifiedNamesAsLoaded){
            if(!typifiedNames.contains(name)){
                name.removeTypeDesignation(bean);
            }
            // FIXME do we need to save the names here or is the delete cascaded from the typedesignation to the name?
        }

        return bean;
    }



    @EventBusListenerMethod(filter = EditorActionTypeFilter.Add.class)
    public void onTaxonNameEditorActionAdd(TaxonNameEditorAction action){

        if(!isFromOwnView(action)){
            return;
        }

        typeNamePopup = getNavigationManager().showInPopup(TaxonNamePopupEditor.class, getView());
        typeNamePopup.grantToCurrentUser(EnumSet.of(CRUD.UPDATE, CRUD.DELETE));
        typeNamePopup.withDeleteButton(true);
        // TODO configure Modes???
        typeNamePopup.loadInEditor(null);

    }


    @EventBusListenerMethod(filter = EditorActionTypeFilter.Edit.class)
    public void onTaxonNameEditorActionEdit(TaxonNameEditorAction action){

        if(!isFromOwnView(action)){
            return;
        }

        //  basionymSourceField = (AbstractField<TaxonName>)event.getSourceComponent();

        typeNamePopup = getNavigationManager().showInPopup(TaxonNamePopupEditor.class, getView());
        typeNamePopup.grantToCurrentUser(EnumSet.of(CRUD.UPDATE, CRUD.DELETE));
        typeNamePopup.withDeleteButton(true);
        // TODO configure Modes???
        typeNamePopup.loadInEditor(action.getEntityUuid());

    }

    @EventBusListenerMethod
    public void onEntityChangeEvent(EntityChangeEvent<?>event){

        if(event.getSourceView() == typeNamePopup){
            if(event.isCreateOrModifiedType()){
                getCache().load(event.getEntity());
                if(event.isCreatedType()){
                    getView().getTypeNameField().setValue((TaxonName) event.getEntity());
                } else {
                    getView().getTypeNameField().reload();
                }
            }
            if(event.isRemovedType()){
                getView().getTypeNameField().selectNewItem(null);
            }
            typeNamePopup = null;

        }
    }

}
