/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
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
import eu.etaxonomy.cdm.format.reference.ReferenceEllypsisFormatter;
import eu.etaxonomy.cdm.model.permission.CRUD;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferenceType;
import eu.etaxonomy.cdm.persistence.permission.CdmAuthority;
import eu.etaxonomy.cdm.persistence.permission.Operation;
import eu.etaxonomy.cdm.persistence.query.MatchMode;
import eu.etaxonomy.cdm.ref.TypedEntityReference;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProviderFactory;
import eu.etaxonomy.cdm.service.TypifiedEntityFilterablePagingProvider;
import eu.etaxonomy.cdm.service.UserHelperAccess;
import eu.etaxonomy.cdm.vaadin.event.EditorActionTypeFilter;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEditorAction;
import eu.etaxonomy.cdm.vaadin.event.RegistrationEditorAction;
import eu.etaxonomy.cdm.vaadin.ui.RegistrationUIDefaults;
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
public class StartRegistrationPresenter
        extends AbstractEditorPresenter<RegistrationDTO, StartRegistrationPresenter, StartRegistrationView> {

    private static final long serialVersionUID = 2283189121081612574L;

    private static final Logger logger = LogManager.getLogger();

    private ReferencePopupEditor newReferencePopup;

    private Reference newReference;

    private boolean registrationInProgress;

    @Autowired
    protected CdmFilterablePagingProviderFactory pagingProviderFactory;

    private TypifiedEntityFilterablePagingProvider<Reference> referencePagingProvider;

    public StartRegistrationPresenter (){
        super();
    }

    @Override
    public void handleViewEntered() {

        super.handleViewEntered();

        referencePagingProvider = pagingProviderFactory.referenceEntityReferencePagingProvider(
                new ReferenceEllypsisFormatter(ReferenceEllypsisFormatter.LabelType.BIBLIOGRAPHIC),
                ReferenceEllypsisFormatter.INIT_STRATEGY
                );
        TypedEntityCaptionGenerator<Reference> titleCacheGenrator = new TypedEntityCaptionGenerator<>();
        // referencePagingProvider.addRestriction(new Restriction("type", Operator.AND_NOT, null, ReferenceType.Section, ReferenceType.Journal, ReferenceType.PrintSeries));
        Criterion criterion = Restrictions.not(Restrictions.or(Restrictions.in("type", new ReferenceType[]{ReferenceType.Section, ReferenceType.Journal, ReferenceType.PrintSeries})));

        if(!UserHelperAccess.userHelper().userIsAdmin()){
            Collection<CdmAuthority> referencePermissions = UserHelperAccess.userHelper().findUserPermissions(Reference.class, Operation.UPDATE);
            boolean generalUpdatePermission = referencePermissions.stream().anyMatch(p -> p.getTargetUUID() == null);
            if(!generalUpdatePermission){
                // exclude unpublished publications
                DateTime nowLocal = new DateTime();
                String dateString = nowLocal.toString("yyyyMMdd");
                logger.debug("dateString:" + dateString);
                Criterion pulishedOnly = Restrictions.or(
                        Restrictions.and(Restrictions.isNull("datePublished.start"), Restrictions.isNull("datePublished.end"), Restrictions.isNull("datePublished.freeText")),
                        Restrictions.and(Restrictions.isNotNull("datePublished.start"), Restrictions.sqlRestriction("datePublished_start < " + dateString)),
                        Restrictions.and(Restrictions.isNull("datePublished.start"), Restrictions.isNotNull("datePublished.end"), Restrictions.sqlRestriction("datePublished_end < " + dateString))
                        );
                // restrict by allowed reference uuids
                Set<UUID> allowedUuids = referencePermissions.stream().filter(p -> p.getTargetUUID() != null).map(CdmAuthority::getTargetUUID).collect(Collectors.toSet());
                if(!allowedUuids.isEmpty()){
                    Criterion uuidRestriction = Restrictions.in("uuid", allowedUuids);
                    criterion = Restrictions.and(criterion, Restrictions.or(pulishedOnly, uuidRestriction));
                } else {
                    criterion = Restrictions.and(criterion, pulishedOnly);
                }
            }
        }
        referencePagingProvider.addCriterion(criterion);
        getView().getReferenceCombobox().setCaptionGenerator(titleCacheGenrator);
        getView().getReferenceCombobox().loadFrom(referencePagingProvider, referencePagingProvider, referencePagingProvider.getPageSize());
    }

    public void updateReferenceSearchMode(MatchMode value) {
        if(referencePagingProvider != null && value != null){
            referencePagingProvider.setMatchMode(value);
            getView().getReferenceCombobox().refresh();
        }
    }

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
                //      so we reload it using the uuid since new beans will not have an Id at this point.
                newReference = getRepo().getReferenceService().load(newReference.getUuid());

                getView().getReferenceCombobox().setValue(null);  // de-select
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
        LazyComboBox<TypedEntityReference<Reference>> referenceCombobox = getView().getReferenceCombobox();
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

    @Override
    protected RegistrationDTO loadBeanById(Object identifier) {
        // not needed //
        return null;
    }

    @Override
    protected void saveBean(RegistrationDTO bean) {
        // not needed //
    }

    @Override
    protected void deleteBean(RegistrationDTO bean) {
        // not needed //
    }
}