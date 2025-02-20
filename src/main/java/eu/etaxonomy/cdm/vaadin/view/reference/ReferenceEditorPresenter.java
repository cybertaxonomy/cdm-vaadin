/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.reference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Scope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.viritin.fields.CaptionGenerator;

import com.vaadin.spring.annotation.SpringComponent;

import eu.etaxonomy.cdm.api.service.IService;
import eu.etaxonomy.cdm.format.reference.ReferenceEllypsisFormatter;
import eu.etaxonomy.cdm.format.reference.ReferenceEllypsisFormatter.LabelType;
import eu.etaxonomy.cdm.model.agent.AgentBase;
import eu.etaxonomy.cdm.model.agent.Institution;
import eu.etaxonomy.cdm.model.agent.Person;
import eu.etaxonomy.cdm.model.agent.TeamOrPersonBase;
import eu.etaxonomy.cdm.model.common.Annotation;
import eu.etaxonomy.cdm.model.common.AnnotationType;
import eu.etaxonomy.cdm.model.permission.CRUD;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferenceFactory;
import eu.etaxonomy.cdm.model.reference.ReferenceType;
import eu.etaxonomy.cdm.persistence.dao.common.Restriction;
import eu.etaxonomy.cdm.persistence.dao.common.Restriction.Operator;
import eu.etaxonomy.cdm.persistence.dao.initializer.EntityInitStrategy;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProvider;
import eu.etaxonomy.cdm.service.UserHelperAccess;
import eu.etaxonomy.cdm.vaadin.event.EditorActionTypeFilter;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent.Type;
import eu.etaxonomy.cdm.vaadin.event.InstitutionEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityButtonUpdater;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityReloader;
import eu.etaxonomy.cdm.vaadin.util.ReferenceEllypsisCaptionGenerator;
import eu.etaxonomy.cdm.vaadin.view.common.InstitutionPopupEditor;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityField;
import eu.etaxonomy.vaadin.mvp.AbstractCdmEditorPresenter;
import eu.etaxonomy.vaadin.mvp.BeanInstantiator;
import eu.etaxonomy.vaadin.mvp.BoundField;
import eu.etaxonomy.vaadin.ui.view.PopupView;

/**
 * @author a.kohlbecker
 * @since Apr 5, 2017
 */
@SpringComponent
@Scope("prototype")
public class ReferenceEditorPresenter
        extends AbstractCdmEditorPresenter<Reference, ReferenceEditorPresenter, ReferencePopupEditorView> {

    private static final long serialVersionUID = -7926116447719010837L;

    private ReferencePopupEditor inReferencePopup = null;

    private CdmFilterablePagingProvider<Reference, Reference> inReferencePagingProvider;

    private Restriction<UUID> includeCurrentInReference;

    public ReferenceEditorPresenter() {}

    @Override
    public void handleViewEntered() {
        super.handleViewEntered();

        getView().getTypeSelect().addValueChangeListener(e -> updateInReferencePageProvider());
        getView().getInReferenceCombobox().getSelect().setCaptionGenerator(new CaptionGenerator<Reference>(){
            private static final long serialVersionUID = -8320063953595684391L;

            @Override
            public String getCaption(Reference option) {
                return option.getTitleCache();
            }
        });

        CdmFilterablePagingProvider<Reference, Reference> collectionPagingProvider = pagingProviderFactory.referencePagingProvider();
        getView().getInReferenceCombobox().loadFrom(collectionPagingProvider, collectionPagingProvider, collectionPagingProvider.getPageSize());
        getView().getInReferenceCombobox().setNestedButtonStateUpdater(new ToOneRelatedEntityButtonUpdater<Reference>(getView().getInReferenceCombobox()));
        getView().getInReferenceCombobox().getSelect().addValueChangeListener(new ToOneRelatedEntityReloader<Reference>(getView().getInReferenceCombobox(),this));
        getView().getInReferenceCombobox().getSelect().setCaptionGenerator(new ReferenceEllypsisCaptionGenerator(LabelType.BIBLIOGRAPHIC, getView().getInReferenceCombobox().getSelect()));

        @SuppressWarnings("rawtypes")
        CdmFilterablePagingProvider<AgentBase, TeamOrPersonBase> teamOrPersonPagingProvider = pagingProviderFactory.teamOrPersonPagingProvider();
        @SuppressWarnings("rawtypes")
        CdmFilterablePagingProvider<AgentBase, Person> personPagingProvider = pagingProviderFactory.personPagingProvider();
        getView().getAuthorshipField().setFilterableTeamPagingProvider(teamOrPersonPagingProvider, this);
        getView().getAuthorshipField().setFilterablePersonPagingProvider(personPagingProvider, this);

        @SuppressWarnings("rawtypes")
        CdmFilterablePagingProvider<AgentBase, Institution> institutionPagingProvider = new CdmFilterablePagingProvider<AgentBase, Institution>(getRepo().getAgentService(), Institution.class);
        getView().getInstitutionCombobox().getSelect().loadFrom(institutionPagingProvider, institutionPagingProvider, institutionPagingProvider.getPageSize());
        getView().getInstitutionCombobox().getSelect().addValueChangeListener(new ToOneRelatedEntityReloader<Institution>(getView().getInstitutionCombobox(), this));

        getView().getSchoolCombobox().getSelect().loadFrom(institutionPagingProvider, institutionPagingProvider, institutionPagingProvider.getPageSize());
        getView().getSchoolCombobox().getSelect().addValueChangeListener(new ToOneRelatedEntityReloader<Institution>(getView().getSchoolCombobox(), this));

        getView().getAnnotationsField().setAnnotationTypeItemContainer(cdmBeanItemContainerFactory.buildTermItemContainer(
                AnnotationType.EDITORIAL().getUuid(), AnnotationType.INTERNAL().getUuid()));
    }

    public void updateInReferencePageProvider() {

        inReferencePagingProvider = pagingProviderFactory.inReferencePagingProvider((ReferenceType) getView().getTypeSelect().getValue(), false);
        Reference inReference = getView().getInReferenceCombobox().getValue();
        if(inReference != null){
            if(includeCurrentInReference == null){
                includeCurrentInReference = new Restriction<>("uuid", Operator.OR, null, inReference.getUuid());
            }
            inReferencePagingProvider.addRestriction(includeCurrentInReference);
        } else {
            inReferencePagingProvider.getRestrictions().remove(includeCurrentInReference);
            includeCurrentInReference = null;
        }
        getView().getInReferenceCombobox().reload();
        getView().getInReferenceCombobox().loadFrom(inReferencePagingProvider, inReferencePagingProvider, inReferencePagingProvider.getPageSize());
    }

    protected static BeanInstantiator<Reference> defaultBeanInstantiator = new BeanInstantiator<Reference>() {

        @Override
        public Reference createNewBean() {
            return ReferenceFactory.newGeneric();
        }
    };

    @Override
    protected BeanInstantiator<Reference> defaultBeanInstantiator(){
       return defaultBeanInstantiator;
    }

    @Override
    protected Reference loadCdmEntity(UUID identifier) {

        EntityInitStrategy initStrategy = new EntityInitStrategy(Arrays.asList(new String []{
                "$",
                "annotations.*", // needed as log as we are using a table in FilterableAnnotationsField
                "inReference"
                }
        ));
        initStrategy.extend("", ReferenceEllypsisFormatter.INIT_STRATEGY, false);
        initStrategy.extend("inReference", ReferenceEllypsisFormatter.INIT_STRATEGY, false);

        Reference reference;
        if(identifier != null){
            reference = getRepo().getReferenceService().load(identifier, initStrategy.getPropertyPaths());
        } else {
            reference = createNewBean();
        }
        return reference;
    }

    @Override
    protected void guaranteePerEntityCRUDPermissions(UUID identifier) {
        if(crud != null){
            newAuthorityCreated = UserHelperAccess.userHelper().createAuthorityForCurrentUser(Reference.class, identifier, crud, null);
        }
    }

    @Override
    protected void guaranteePerEntityCRUDPermissions(Reference bean) {
        if(crud != null){
            newAuthorityCreated = UserHelperAccess.userHelper().createAuthorityForCurrentUser(bean, crud, null);
        }
    }

    @EventBusListenerMethod
    public void onReferenceEditorAction(ReferenceEditorAction editorAction) {

        if(!isFromOwnView(editorAction) || editorAction.getTarget() == null){
            return;
        }

        if(ToOneRelatedEntityField.class.isAssignableFrom(editorAction.getTarget().getClass())){
            List<ReferenceType> applicableTypes = ReferenceType.inReferenceContraints((ReferenceType) getView().getTypeSelect().getValue());
            if(editorAction.isAddAction()){
                inReferencePopup = openPopupEditor(ReferencePopupEditor.class, editorAction);
                if(!applicableTypes.isEmpty()){
                    inReferencePopup.withReferenceTypes(EnumSet.copyOf(applicableTypes));
                }
                inReferencePopup.grantToCurrentUser(EnumSet.of(CRUD.UPDATE, CRUD.DELETE));
                inReferencePopup.withDeleteButton(true);

                inReferencePopup.loadInEditor(null);
                if(!applicableTypes.isEmpty()){
                    inReferencePopup.getTypeSelect().setValue(applicableTypes.iterator().next());
                }
            }
            if(editorAction.isEditAction()){
                inReferencePopup = openPopupEditor(ReferencePopupEditor.class, editorAction);
                if(!applicableTypes.isEmpty()){
                    inReferencePopup.withReferenceTypes(EnumSet.copyOf(applicableTypes));
                }
                inReferencePopup.withDeleteButton(true);
                inReferencePopup.loadInEditor(editorAction.getEntityUuid());
            }
        }
    }

    @EventBusListenerMethod(filter = EditorActionTypeFilter.Edit.class)
    public void onInstitutionEditorActionEdit(InstitutionEditorAction event) {

        if(!checkFromOwnView(event)){
            return;
        }

        InstitutionPopupEditor institutionPopuEditor = openPopupEditor(InstitutionPopupEditor.class, event);

        institutionPopuEditor.grantToCurrentUser(this.crud);
        institutionPopuEditor.withDeleteButton(true);
        institutionPopuEditor.loadInEditor(event.getEntityUuid());
    }

    @EventBusListenerMethod(filter = EditorActionTypeFilter.Add.class)
    public void onInstitutionEditorActionAdd(InstitutionEditorAction event) {

        if(!checkFromOwnView(event)){
            return;
        }

        InstitutionPopupEditor institutionPopuEditor = openPopupEditor(InstitutionPopupEditor.class, event);

        institutionPopuEditor.grantToCurrentUser(this.crud);
        institutionPopuEditor.withDeleteButton(false);
        institutionPopuEditor.loadInEditor(null);
    }

    @EventBusListenerMethod
    public void onEntityChangeEvent(EntityChangeEvent<?> event){

        BoundField boundTargetField = boundTargetField((PopupView) event.getSourceView());

        if(boundTargetField != null){
            if(boundTargetField.matchesPropertyIdPath("inReference")){
                if(event.isCreateOrModifiedType()){
                    Reference inReference = (Reference)getCache().load(event.getEntity());
                    getView().getInReferenceCombobox().reload();
                    if(event.getType() == Type.CREATED){
                        getView().getInReferenceCombobox().setValue(inReference);
                    }
                }
                if(event.isRemovedType()){
                    getView().getInReferenceCombobox().selectNewItem(null);
                }
                inReferencePopup = null;
            }
            else if(boundTargetField.matchesPropertyIdPath("institute")){
                if(event.isCreateOrModifiedType()){
                    Institution newInstitution = (Institution) event.getEntity();
                    getCache().load(newInstitution);
                    if(event.isCreatedType()){
                        getView().getInstitutionCombobox().setValue(newInstitution);
                    } else {
                        getView().getInstitutionCombobox().reload();
                    }
                }
            } else if(boundTargetField.matchesPropertyIdPath("school")){
                if(event.isCreateOrModifiedType()){
                    Institution newInstitution = (Institution) event.getEntity();
                    getCache().load(newInstitution);
                    if(event.isCreatedType()){
                        getView().getSchoolCombobox().setValue(newInstitution);
                    } else {
                        getView().getSchoolCombobox().reload();
                    }
                }
            }
        }
    }

    @Override
    protected IService<Reference> getService() {
        return getRepo().getReferenceService();
    }

    @Override
    protected Reference preSaveBean(Reference bean) {

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
            if(!annotationsSeen.contains(a) || a.checkEmpty(true)){
                bean.removeAnnotation(a);
            }
        }
        return bean;
    }
}