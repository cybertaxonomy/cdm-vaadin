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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import eu.etaxonomy.cdm.api.service.dto.TypeDesignationStatusFilter;
import eu.etaxonomy.cdm.api.service.pager.Pager;
import eu.etaxonomy.cdm.api.service.registration.IRegistrationWorkingSetService;
import eu.etaxonomy.cdm.model.common.Language;
import eu.etaxonomy.cdm.model.name.RegistrationStatus;
import eu.etaxonomy.cdm.model.permission.User;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.service.CdmBeanItemContainerFactory;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItem;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent;
import eu.etaxonomy.cdm.vaadin.event.PagingEvent;
import eu.etaxonomy.cdm.vaadin.event.ShowDetailsEvent;
import eu.etaxonomy.cdm.vaadin.event.UpdateResultsEvent;
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

    private final static Logger logger = LogManager.getLogger();

    private static final String REGISTRATION_LIST_PRESENTER_SEARCH_FILTER = "registration.listPresenter.searchFilter";

    private static final EnumSet<RegistrationStatus> inProgressStatus = EnumSet.of(
            RegistrationStatus.PREPARATION,
            RegistrationStatus.CURATION,
            RegistrationStatus.READY
            );

    private static final long serialVersionUID = 5419947244621450665L;

//    protected TypeDesignationStatusBase<?> NULL_TYPE_STATUS = NameTypeDesignationStatus.NewInstance("- none -", "- none -", "- none -");

    @Autowired
    private IRegistrationWorkingSetService workingSetService;

    @Autowired
    protected CdmBeanItemContainerFactory cdmBeanItemContainerFactory;

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

        if(getView().getSubmitterFilter() != null){
            getView().getSubmitterFilter().setContainerDataSource(cdmBeanItemContainerFactory.buildBeanItemContainer(User.class));
            getView().getSubmitterFilter().setItemCaptionPropertyId("username");
        }

        BeanItemContainer<TypeDesignationStatusFilter> buildTermItemContainer = new BeanItemContainer<>(TypeDesignationStatusFilter.class);
        // TODO use UI.getCurrent().getPage().getWebBrowser().getLocale() or the LocaleContext component to get the preferredLanguage?
        Collection<TypeDesignationStatusFilter> statusFilterTerms = new ArrayList<>(getRepo().getNameService().getTypeDesignationStatusFilterTerms(Arrays.asList(Language.DEFAULT())));
        statusFilterTerms.add(TypeDesignationStatusFilter.NULL_ELEMENT);
        buildTermItemContainer.addAll(statusFilterTerms);
        getView().getStatusTypeFilter().setContainerDataSource(buildTermItemContainer);
        for(TypeDesignationStatusFilter dt : buildTermItemContainer.getItemIds()){
            getView().getStatusTypeFilter().setItemCaption(dt, dt.toString());
        }
        buildTermItemContainer.sort(new String[]{"label"}, new boolean[]{true});

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

        // prepare the filters
        RegistrationSearchFilter filter = loadFilterFromView();
        if(textFieldOverride != null && textFieldOverride == getView().getIdentifierFilter()){
            filter.setIdentifierPattern(alternativeText);
        }

        if(textFieldOverride != null && textFieldOverride == getView().getTaxonNameFilter()){
            filter.setNamePattern(alternativeText);
        }
        if(textFieldOverride != null && textFieldOverride == getView().getReferenceFilter()){
            filter.setReferencePattern(alternativeText);
        }


        if(filter.getTypeStatus().contains(TypeDesignationStatusFilter.NULL_ELEMENT)){
           Set<TypeDesignationStatusFilter> tmpSet = new HashSet<>();
           tmpSet.addAll(filter.getTypeStatus());
           tmpSet.remove(TypeDesignationStatusFilter.NULL_ELEMENT);
           tmpSet.add(null);
           filter.setTypeStatus(tmpSet);
        }


        if(getView().getViewMode().equals(ListView.Mode.inProgress)){
            filter.setRegistrationStatus(inProgressStatus);
        }

        List<UUID> typeDesignationStatus = new ArrayList<>(TypeDesignationStatusFilter.toTypeDesignationStatusUuids(filter.getTypeStatus()));

        Pager<RegistrationDTO> dtoPager = getWorkingSetService().pageDTOs(
                filter.getSubmitter() != null ? filter.getSubmitter().getUuid() : null,
                filter.getRegistrationStatus() != null ? new ArrayList<>(filter.getRegistrationStatus()): null,
                StringUtils.trimToNull(filter.getIdentifierPattern()),
                StringUtils.trimToNull(filter.getNamePattern()),
                StringUtils.trimToNull(filter.getReferencePattern()),
                typeDesignationStatus,
                pageSize,
                pageIndex,
                null);
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
        if(event.getProperty().equals(RegistrationItem.VALIDATION_PROBLEMS)){

            getView().openDetailsPopup("Validation Problems", regDto.getValidationProblems());

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

        RegistrationSearchFilter filter = loadFilterFromView();
        UI.getCurrent().getSession().setAttribute(REGISTRATION_LIST_PRESENTER_SEARCH_FILTER, filter);
    }


    /**
     *
     */
    @SuppressWarnings("unchecked")
    public RegistrationSearchFilter loadFilterFromView() {


        RegistrationSearchFilter filter = new RegistrationSearchFilter();
        filter.setIdentifierPattern(getView().getIdentifierFilter().getValue());
        filter.setNamePattern(getView().getTaxonNameFilter().getValue());
        if(getView().getSubmitterFilter() != null){
            Object o = getView().getSubmitterFilter().getValue();
            if(o != null){
                filter.setSubmitter((User)o);
            }
        } else {
            Authentication authentication = currentSecurityContext().getAuthentication();
            if(authentication != null && authentication.getPrincipal() != null && authentication.getPrincipal() instanceof User){
                filter.setSubmitter((User) authentication.getPrincipal());
            }
        }
        if(getView().getStatusTypeFilter().getValue() != null) {
            filter.setTypeStatus((Set<TypeDesignationStatusFilter>) getView().getStatusTypeFilter().getValue());
        }
        Object o = getView().getRegistrationStatusFilter().getValue();
        if(o != null){
            filter.setRegistrationStatus(EnumSet.of((RegistrationStatus)o));
        }
        return filter;
    }


    private void loadSearchFilterFromSession() {
        Object o = UI.getCurrent().getSession().getAttribute(REGISTRATION_LIST_PRESENTER_SEARCH_FILTER);
        if(o != null){
            RegistrationSearchFilter filter = (RegistrationSearchFilter)o;
            getView().getIdentifierFilter().setValue(filter.getIdentifierPattern());
            getView().getTaxonNameFilter().setValue(filter.getNamePattern());
            if(getView().getSubmitterFilter() != null){
                getView().getSubmitterFilter().setValue(filter.getSubmitter());
            }
            setSelectValue(getView().getStatusTypeFilter(), filter.getTypeStatus());
            setSelectValue(getView().getRegistrationStatusFilter(), filter.getRegistrationStatus());
        }

    }

    /**
     * @param statusTypeFilter
     * @param typeStatus
     */
    private void setSelectValue(AbstractSelect select, Set<?> itemsToChoose) {

        if(itemsToChoose != null){
            for(Object item : select.getContainerDataSource().getItemIds()){
                if(item != null){
                    if(itemsToChoose.contains(item)){
                        select.select(item);
                    }
                }
            }
        }

    }

}
