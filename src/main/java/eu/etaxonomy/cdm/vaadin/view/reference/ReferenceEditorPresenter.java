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
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.viritin.fields.CaptionGenerator;

import com.vaadin.spring.annotation.SpringComponent;

import eu.etaxonomy.cdm.api.service.IService;
import eu.etaxonomy.cdm.format.ReferenceEllypsisFormatter.LabelType;
import eu.etaxonomy.cdm.model.agent.AgentBase;
import eu.etaxonomy.cdm.model.agent.Institution;
import eu.etaxonomy.cdm.model.agent.Person;
import eu.etaxonomy.cdm.model.agent.TeamOrPersonBase;
import eu.etaxonomy.cdm.model.common.Annotation;
import eu.etaxonomy.cdm.model.common.AnnotationType;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferenceFactory;
import eu.etaxonomy.cdm.model.reference.ReferenceType;
import eu.etaxonomy.cdm.persistence.dao.common.Restriction;
import eu.etaxonomy.cdm.persistence.dao.common.Restriction.Operator;
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
 *
 */
@SpringComponent
@Scope("prototype")
public class ReferenceEditorPresenter extends AbstractCdmEditorPresenter<Reference, ReferencePopupEditorView> {

    private static final long serialVersionUID = -7926116447719010837L;

    private static final Logger logger = Logger.getLogger(ReferenceEditorPresenter.class);

    ReferencePopupEditor inReferencePopup = null;

    CdmFilterablePagingProvider<Reference, Reference> inReferencePagingProvider;

    Restriction<UUID> includeCurrentInReference;

    public ReferenceEditorPresenter() {

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void handleViewEntered() {
        super.handleViewEntered();

        getView().getTypeSelect().addValueChangeListener(e -> updateInReferencePageProvider());
        getView().getInReferenceCombobox().getSelect().setCaptionGenerator(new CaptionGenerator<Reference>(){

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

        CdmFilterablePagingProvider<AgentBase, TeamOrPersonBase> teamOrPersonPagingProvider = pagingProviderFactory.teamOrPersonPagingProvider();
        CdmFilterablePagingProvider<AgentBase, Person> personPagingProvider = pagingProviderFactory.personPagingProvider();
        getView().getAuthorshipField().setFilterableTeamPagingProvider(teamOrPersonPagingProvider, this);
        getView().getAuthorshipField().setFilterablePersonPagingProvider(personPagingProvider, this);

        CdmFilterablePagingProvider<AgentBase, Institution> institutionPagingProvider = new CdmFilterablePagingProvider<AgentBase, Institution>(getRepo().getAgentService(), Institution.class);
        getView().getInstitutionCombobox().getSelect().loadFrom(institutionPagingProvider, institutionPagingProvider, institutionPagingProvider.getPageSize());
        getView().getInstitutionCombobox().getSelect().addValueChangeListener(new ToOneRelatedEntityReloader<Institution>(getView().getInstitutionCombobox(), this));

        getView().getSchoolCombobox().getSelect().loadFrom(institutionPagingProvider, institutionPagingProvider, institutionPagingProvider.getPageSize());
        getView().getSchoolCombobox().getSelect().addValueChangeListener(new ToOneRelatedEntityReloader<Institution>(getView().getSchoolCombobox(), this));

        getView().getAnnotationsField().setAnnotationTypeItemContainer(cdmBeanItemContainerFactory.buildTermItemContainer(
                AnnotationType.EDITORIAL().getUuid(), AnnotationType.TECHNICAL().getUuid()));
    }


    /**
     * @param inReferencePagingProvider
     */
    public void updateInReferencePageProvider() {

        inReferencePagingProvider = pagingProviderFactory.inReferencePagingProvider((ReferenceType) getView().getTypeSelect().getValue(), false);
        Reference inReference = getView().getInReferenceCombobox().getValue();
        if(inReference != null){
            if(includeCurrentInReference == null){
                includeCurrentInReference = new Restriction<UUID>("uuid", Operator.OR, null, inReference.getUuid());
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


    /**
     * {@inheritDoc}
     */
    @Override
    protected Reference loadCdmEntity(UUID identifier) {

        List<String> initStrategy = Arrays.asList(new String []{
                "$",
                "annotations.*", // needed as log as we are using a table in FilterableAnnotationsField
                }
        );

        Reference reference;
        if(identifier != null){
            reference = getRepo().getReferenceService().load(identifier, initStrategy);
        } else {
            reference = createNewBean();
        }
        return reference;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void guaranteePerEntityCRUDPermissions(UUID identifier) {
        if(crud != null){
            newAuthorityCreated = UserHelperAccess.userHelper().createAuthorityForCurrentUser(Reference.class, identifier, crud, null);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void guaranteePerEntityCRUDPermissions(Reference bean) {
        if(crud != null){
            newAuthorityCreated = UserHelperAccess.userHelper().createAuthorityForCurrentUser(bean, crud, null);
        }
    }

    /**
    *
    * @param editorAction
    * @throws EditorEntityBeanException
    */
   @EventBusListenerMethod
   public void onReferenceEditorAction(ReferenceEditorAction editorAction) {

       if(!isFromOwnView(editorAction) || editorAction.getTarget() == null){
           return;
       }

       if(ToOneRelatedEntityField.class.isAssignableFrom(editorAction.getTarget().getClass())){
           Set<ReferenceType> applicableTypes = ReferenceType.inReferenceContraints((ReferenceType) getView().getTypeSelect().getValue());
           if(editorAction.isAddAction()){
               inReferencePopup = openPopupEditor(ReferencePopupEditor.class, editorAction);
               if(!applicableTypes.isEmpty()){
                   inReferencePopup.withReferenceTypes(EnumSet.copyOf(applicableTypes));
               }
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected IService<Reference> getService() {
        return getRepo().getReferenceService();
    }


    /**
     * {@inheritDoc}
     */
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
            if(!annotationsSeen.contains(a)){
                bean.removeAnnotation(a);
            }
        }


        return bean;
    }




}
