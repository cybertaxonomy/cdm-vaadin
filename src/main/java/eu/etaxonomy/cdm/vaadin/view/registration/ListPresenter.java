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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.Navigator;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.api.service.dto.RegistrationDTO;
import eu.etaxonomy.cdm.api.service.pager.Pager;
import eu.etaxonomy.cdm.model.common.DefinedTermBase;
import eu.etaxonomy.cdm.model.common.User;
import eu.etaxonomy.cdm.model.name.NameTypeDesignationStatus;
import eu.etaxonomy.cdm.model.name.RegistrationStatus;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignationStatus;
import eu.etaxonomy.cdm.model.name.TypeDesignationStatusBase;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.service.IRegistrationWorkingSetService;
import eu.etaxonomy.cdm.vaadin.component.CdmBeanItemContainerFactory;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItem;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent;
import eu.etaxonomy.cdm.vaadin.event.PagingEvent;
import eu.etaxonomy.cdm.vaadin.event.ShowDetailsEvent;
import eu.etaxonomy.cdm.vaadin.event.UpdateResultsEvent;
import eu.etaxonomy.cdm.vaadin.model.registration.RegistrationTermLists;
import eu.etaxonomy.vaadin.mvp.AbstractPresenter;
import eu.etaxonomy.vaadin.ui.navigation.NavigationEvent;

/**
 *
 * @author a.kohlbecker
 * @since Mar 3, 2017
 *
 */
@SpringComponent
@ViewScope
public class ListPresenter extends AbstractPresenter<ListView> {

    /**
     *
     */
    private static final String REGISTRATION_LIST_PRESENTER_SEARCH_FILTER = "registration.listPresenter.searchFilter";

    private static final EnumSet<RegistrationStatus> inProgressStatus = EnumSet.of(
            RegistrationStatus.PREPARATION,
            RegistrationStatus.CURATION,
            RegistrationStatus.READY
            );

    private static final long serialVersionUID = 5419947244621450665L;

    protected TypeDesignationStatusBase<?> NULL_TYPE_STATUS = NameTypeDesignationStatus.NewInstance("- none -", "- none -", "- none -");

    @Autowired
    private IRegistrationWorkingSetService workingSetService;

    private Integer pageIndex = 0;
    private Integer pageSize = null;

    /**
     * @return the workingSetService
     */
    public IRegistrationWorkingSetService getWorkingSetService() {
        return workingSetService;
    }


    @Override
    public void handleViewEntered() {

        List<String> viewParameters = getNavigationManager().getCurrentViewParameters();
        if(viewParameters.get(0).equals(ListView.Mode.inProgress.name())){
            getView().setViewMode(ListViewBean.Mode.inProgress);
            getView().getRegistrationStatusFilter().setVisible(false);
        } else {
            getView().setViewMode(ListViewBean.Mode.all);
        }
        if(viewParameters.size() > 1){
            // expecting the second param to be the page index
            try {
                pageIndex = Integer.parseInt(viewParameters.get(1));
            } catch (NumberFormatException e) {
                // only log and display the page 0
                logger.error("Invalid page index parameter " + viewParameters.get(1) + " in " + ((Navigator)getNavigationManager()).getState());
            }
        }

        CdmBeanItemContainerFactory selectFieldFactory = new CdmBeanItemContainerFactory(getRepo());

        if(getView().getSubmitterFilter() != null){
            getView().getSubmitterFilter().setContainerDataSource(selectFieldFactory.buildBeanItemContainer(User.class));
            getView().getSubmitterFilter().setItemCaptionPropertyId("username");
        }

        List<UUID> typeDesignationStatusUUIDS = new ArrayList<>();
        typeDesignationStatusUUIDS.addAll(RegistrationTermLists.NAME_TYPE_DESIGNATION_STATUS_UUIDS());
        typeDesignationStatusUUIDS.addAll(RegistrationTermLists.SPECIMEN_TYPE_DESIGNATION_STATUS_UUIDS());
        BeanItemContainer<DefinedTermBase> buildTermItemContainer = selectFieldFactory.buildTermItemContainer(typeDesignationStatusUUIDS);
        buildTermItemContainer.addItem(NULL_TYPE_STATUS);
        getView().getStatusTypeFilter().setContainerDataSource(buildTermItemContainer);
        for(DefinedTermBase dt : buildTermItemContainer.getItemIds()){
            String caption;
            if(dt == NULL_TYPE_STATUS){
                caption = "- NONE -";
            } else {
                caption = (dt instanceof SpecimenTypeDesignationStatus ? "ST" : "NT") + " - " + dt.getLabel();
            }
            getView().getStatusTypeFilter().setItemCaption(dt, caption);
        }

        loadSearchFilterFromSession();

        getView().populate(pageRegistrations(null, null));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewExit() {
        preserveSearchFilterInSession();
        super.onViewExit();
    }

    /**
     * FIXME write test !!!!!!!!!!!!!!!!!
     *
     * @return
     */
    private Pager<RegistrationDTO> pageRegistrations(TextField textFieldOverride, String alternativeText) {

        // list all if the authenticated user is having the role CURATION of if it is an admin
        Authentication authentication = currentSecurityContext().getAuthentication();

        // prepare the filters
        SearchFilter filter = loadFilterFromView();
        if(textFieldOverride != null && textFieldOverride == getView().getIdentifierFilter()){
            filter.identifierPattern = alternativeText;
        }

        if(textFieldOverride != null && textFieldOverride == getView().getTaxonNameFilter()){
            filter.namePattern = alternativeText;
        }

       if(filter.typeStatus.isEmpty()){
           filter.typeStatus = null;
        } else {
            if(filter.typeStatus.contains(NULL_TYPE_STATUS)){
               Set<TypeDesignationStatusBase> tmpSet = new HashSet<>();
               tmpSet.addAll(filter.typeStatus);
               tmpSet.remove(NULL_TYPE_STATUS);
               tmpSet.add(null);
               filter.typeStatus = tmpSet;
            }
        }


        if(getView().getViewMode().equals(ListView.Mode.inProgress)){
            filter.registrationStatus = inProgressStatus;
        }

        Pager<RegistrationDTO> dtoPager = getWorkingSetService().pageDTOs(
                filter.submitter,
                filter.registrationStatus,
                StringUtils.trimToNull(filter.identifierPattern),
                StringUtils.trimToNull(filter.namePattern),
                filter.typeStatus ,
                pageSize,
                pageIndex);
        return dtoPager;
    }

    @EventBusListenerMethod
    public void onShowDetailsEvent(ShowDetailsEvent<RegistrationDTO, UUID> event) {

        // FIXME check from own view!!!
        if(getView() == null){
            return;
        }

        UUID registrationUuid = event.getIdentifier();

        RegistrationDTO regDto = getWorkingSetService().loadDtoByUuid(registrationUuid);
        if(event.getProperty().equals("messages")){

            getView().openDetailsPopup("Messages", regDto.getValidationProblems());

        } else if(event.getProperty().equals("blockedBy")){

            Set<RegistrationDTO> blockingRegs = getWorkingSetService().loadBlockingRegistrations(registrationUuid);
            RegistrationItem regItem = getView().getRegistrationItem(registrationUuid);
            regItem.showBlockingRegistrations(blockingRegs);
        }

    }

    @EventBusListenerMethod
    public void onEntityChangeEvent(EntityChangeEvent event){
        if(event.getEntityType().isAssignableFrom(Reference.class)){
            // TODO update component showing the according reference, is there a Vaadin event supporting this?
        }
    }

    @EventBusListenerMethod
    public void onUpdateResultsEvent(UpdateResultsEvent event){
        getView().populate(pageRegistrations(event.getField(), event.getNewText()));
    }

    @EventBusListenerMethod
    public void onPagingEvent(PagingEvent event){

        if(!event.getSourceView().equals(getView())){
            return;
        }
        String viewName = getNavigationManager().getCurrentViewName();
        List<String> viewNameParams = new ArrayList(getNavigationManager().getCurrentViewParameters());
        if(viewNameParams.size() > 1){
            viewNameParams.set(1, event.getPageIndex().toString());
        } else {
            viewNameParams.add(event.getPageIndex().toString());
        }
        viewEventBus.publish(EventScope.UI, this, new NavigationEvent(viewName, viewNameParams.toArray(new String[viewNameParams.size()])));
    }


    /**
     *
     */
    private void preserveSearchFilterInSession() {

        SearchFilter filter = loadFilterFromView();
        UI.getCurrent().getSession().setAttribute(REGISTRATION_LIST_PRESENTER_SEARCH_FILTER, filter);
    }


    /**
     *
     */
    public SearchFilter loadFilterFromView() {
        SearchFilter filter = new SearchFilter();
        filter.identifierPattern = getView().getIdentifierFilter().getValue();
        filter.namePattern = getView().getTaxonNameFilter().getValue();
        if(getView().getSubmitterFilter() != null){
            Object o = getView().getSubmitterFilter().getValue();
            if(o != null){
                filter.submitter = (User)o;
            }
        }
        filter.typeStatus = (Set<TypeDesignationStatusBase>) getView().getStatusTypeFilter().getValue();
        EnumSet<RegistrationStatus> registrationStatusFilter = null;
        Object o = getView().getRegistrationStatusFilter().getValue();
        if(o != null){
            filter.registrationStatus = EnumSet.of((RegistrationStatus)o);
        }
        return filter;
    }


    /**
     *
     */
    private void loadSearchFilterFromSession() {
        Object o = UI.getCurrent().getSession().getAttribute(REGISTRATION_LIST_PRESENTER_SEARCH_FILTER);
        if(o != null){
            SearchFilter filter = (SearchFilter)o;
            getView().getIdentifierFilter().setValue(filter.identifierPattern);
            getView().getTaxonNameFilter().setValue(filter.namePattern);
            if(getView().getSubmitterFilter() != null){
                getView().getSubmitterFilter().setValue(filter.submitter);
            }
            setSelectValue(getView().getStatusTypeFilter(), filter.typeStatus);
            setSelectValue(getView().getRegistrationStatusFilter(), filter.registrationStatus);
        }

    }

    /**
     * @param statusTypeFilter
     * @param typeStatus
     */
    private void setSelectValue(AbstractSelect select, Set<?> itemsToChoose) {

        for(Object item : select.getContainerDataSource().getItemIds()){
            if(item != null){
                if(itemsToChoose.contains(item)){
                    select.select(item);
                }
            }
        }

    }

    class SearchFilter {
        String identifierPattern;
        String namePattern;
        User submitter;
        Set<TypeDesignationStatusBase> typeStatus;
        Set<RegistrationStatus> registrationStatus;
    }

}
