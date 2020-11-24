/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.name;

import java.util.ArrayList;
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
import eu.etaxonomy.cdm.api.service.name.TypeDesignationWorkingSet;
import eu.etaxonomy.cdm.api.service.registration.IRegistrationWorkingSetService;
import eu.etaxonomy.cdm.format.reference.ReferenceEllypsisFormatter;
import eu.etaxonomy.cdm.format.reference.ReferenceEllypsisFormatter.LabelType;
import eu.etaxonomy.cdm.model.common.Annotation;
import eu.etaxonomy.cdm.model.common.AnnotationType;
import eu.etaxonomy.cdm.model.name.NameTypeDesignation;
import eu.etaxonomy.cdm.model.name.NameTypeDesignationStatus;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.permission.CRUD;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.persistence.dao.initializer.EntityInitStrategy;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProvider;
import eu.etaxonomy.cdm.service.CdmStore;
import eu.etaxonomy.cdm.service.UserHelperAccess;
import eu.etaxonomy.cdm.vaadin.event.EditorActionTypeFilter;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent.Type;
import eu.etaxonomy.cdm.vaadin.event.TaxonNameEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityButtonUpdater;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityReloader;
import eu.etaxonomy.cdm.vaadin.ui.config.TaxonNamePopupEditorConfig;
import eu.etaxonomy.cdm.vaadin.util.ReferenceEllypsisCaptionGenerator;
import eu.etaxonomy.vaadin.mvp.AbstractCdmEditorPresenter;
import eu.etaxonomy.vaadin.mvp.AbstractView;
import eu.etaxonomy.vaadin.mvp.BeanInstantiator;
import eu.etaxonomy.vaadin.mvp.BoundField;
import eu.etaxonomy.vaadin.ui.view.PopupView;

/**
 * @author a.kohlbecker
 * @since Jan 26, 2018
 *
 */
@SpringComponent
@Scope("prototype")
public class NameTypeDesignationPresenter
        extends AbstractCdmEditorPresenter<NameTypeDesignation, NameTypeDesignationEditorView> {

    private static final long serialVersionUID = 896305051895903033L;

    @Autowired
    private IRegistrationWorkingSetService registrationWorkingSetService;

    HashSet<TaxonName> typifiedNamesAsLoaded;

    private TaxonName typifiedNameInContext;

    protected static BeanInstantiator<NameTypeDesignation> defaultBeanInstantiator = new BeanInstantiator<NameTypeDesignation>() {

        @Override
        public NameTypeDesignation createNewBean() {
            return NameTypeDesignation.NewInstance();
        }
    };


    @Override
    protected BeanInstantiator<NameTypeDesignation> defaultBeanInstantiator(){
       return defaultBeanInstantiator;
    }

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
        EntityInitStrategy initStrategy = new EntityInitStrategy(Arrays.asList(new String []{
                "$",
                "annotations.*", // * is needed as log as we are using a table in FilterableAnnotationsField
                "typifiedNames.typeDesignations", // important !!
                "typeName.$",
                "source.citation",
                "source.annotations",
                "source.markers",
                }
        ));

        initStrategy.extend("citation", ReferenceEllypsisFormatter.INIT_STRATEGY, false);
        NameTypeDesignation typeDesignation;
        if(uuid != null){
            typeDesignation = (NameTypeDesignation) getRepo().getNameService().loadTypeDesignation(uuid, initStrategy.getPropertyPaths());
        } else {
            typeDesignation = createNewBean();
        }

        typifiedNamesAsLoaded = new HashSet<>(typeDesignation.getTypifiedNames());

        return typeDesignation;
    }




    /**
     * {@inheritDoc}
     */
    @Override
    public void handleViewEntered() {

        getView().getTypeStatusSelect().setContainerDataSource(cdmBeanItemContainerFactory.buildBeanItemContainer(NameTypeDesignationStatus.class));
        getView().getTypeStatusSelect().setItemCaptionPropertyId("description");

        getView().getCitationCombobox().getSelect().setCaptionGenerator(
                new ReferenceEllypsisCaptionGenerator(LabelType.BIBLIOGRAPHIC, getView().getCitationCombobox().getSelect())
                );
        CdmFilterablePagingProvider<Reference,Reference> referencePagingProvider = pagingProviderFactory.referencePagingProvider();
        getView().getCitationCombobox().loadFrom(referencePagingProvider, referencePagingProvider, referencePagingProvider.getPageSize());
        getView().getCitationCombobox().setNestedButtonStateUpdater(new ToOneRelatedEntityButtonUpdater<Reference>(getView().getCitationCombobox()));
        getView().getCitationCombobox().getSelect().addValueChangeListener(new ToOneRelatedEntityReloader<>(getView().getCitationCombobox(), this));

        CdmFilterablePagingProvider<TaxonName,TaxonName> namePagingProvider = pagingProviderFactory.taxonNamesWithoutOrthophicIncorrect();
        getView().getTypeNameField().loadFrom(namePagingProvider, namePagingProvider, namePagingProvider.getPageSize());
        getView().getTypeNameField().setNestedButtonStateUpdater(new ToOneRelatedEntityButtonUpdater<TaxonName>(getView().getTypeNameField()));
        getView().getTypeNameField().getSelect().addValueChangeListener(new ToOneRelatedEntityReloader<>(getView().getTypeNameField(), this));

        getView().getTypifiedNamesComboboxSelect().setPagingProviders(namePagingProvider, namePagingProvider, namePagingProvider.getPageSize(), this);

        getView().getAnnotationsField().setAnnotationTypeItemContainer(cdmBeanItemContainerFactory.buildVocabularyTermsItemContainer(
                AnnotationType.EDITORIAL().getVocabulary().getUuid()));

    }

    @Override
    protected void guaranteePerEntityCRUDPermissions(UUID identifier) {
        if(crud != null){
            newAuthorityCreated = UserHelperAccess.userHelper().createAuthorityForCurrentUser(NameTypeDesignation.class, identifier, crud, null);
        }
    }

    @Override
    protected void guaranteePerEntityCRUDPermissions(NameTypeDesignation bean) {
        // TODO Auto-generated method stub

    }


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
    protected NameTypeDesignation preSaveBean(NameTypeDesignation bean) {

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

        // handle annotation changes
        List<Annotation> annotations = getView().getAnnotationsField().getValue();
        List<Annotation> currentAnnotations = new ArrayList<>(bean.getAnnotations());
        List<Annotation> annotationsSeen = new ArrayList<>();
        for(Annotation a : annotations){
            if(a == null){
                continue;
            }
            if(!currentAnnotations.contains(a)){
                bean.addAnnotation(a);
            }
            annotationsSeen.add(a);
        }
        for(Annotation a : currentAnnotations){
            if(!annotationsSeen.contains(a)){
                bean.removeAnnotation(a);
            }
        }


        return bean;
    }



    @EventBusListenerMethod(filter = EditorActionTypeFilter.Add.class)
    public void onTaxonNameEditorActionAdd(TaxonNameEditorAction action){

        if(!isFromOwnView(action)){
            return;
        }

        TaxonNamePopupEditor typeNamePopup = openPopupEditor(TaxonNamePopupEditor.class, action);
        typeNamePopup.grantToCurrentUser(EnumSet.of(CRUD.UPDATE, CRUD.DELETE));
        typeNamePopup.withDeleteButton(true);
        TaxonNamePopupEditorConfig.configure(typeNamePopup);
        typeNamePopup.loadInEditor(null);

    }


    @EventBusListenerMethod(filter = EditorActionTypeFilter.Edit.class)
    public void onTaxonNameEditorActionEdit(TaxonNameEditorAction action){

        if(!isFromOwnView(action)){
            return;
        }

        TaxonNamePopupEditor typeNamePopup = openPopupEditor(TaxonNamePopupEditor.class, action);
        typeNamePopup.grantToCurrentUser(EnumSet.of(CRUD.UPDATE, CRUD.DELETE));
        typeNamePopup.withDeleteButton(true);
        TaxonNamePopupEditorConfig.configure(typeNamePopup);
        typeNamePopup.loadInEditor(action.getEntityUuid());

    }

    @EventBusListenerMethod
    public void onEntityChangeEvent(EntityChangeEvent<?>event){

        BoundField boundTargetField = boundTargetField((PopupView) event.getSourceView());

        if(boundTargetField != null){
            if(boundTargetField.matchesPropertyIdPath("typeName")){
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

            }
        }
    }

}
