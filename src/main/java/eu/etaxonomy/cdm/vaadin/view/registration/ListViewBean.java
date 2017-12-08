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
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.api.service.pager.Pager;
import eu.etaxonomy.cdm.model.name.RegistrationStatus;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItem;
import eu.etaxonomy.cdm.vaadin.event.UpdateResultsEvent;
import eu.etaxonomy.cdm.vaadin.security.AccessRestrictedView;
import eu.etaxonomy.cdm.vaadin.security.UserHelper;
import eu.etaxonomy.cdm.vaadin.view.AbstractPageView;

/**
 * @author a.kohlbecker
 * @since Mar 2, 2017
 *
 */
@SpringView(name=ListViewBean.NAME)
public class ListViewBean extends AbstractPageView<ListPresenter> implements ListView, View, AccessRestrictedView {


    private static final long serialVersionUID = 3543300933072824713L;

    public static final String NAME = "list";

    Mode viewMode = Mode.all;

    private CssLayout listContainer;

    private HorizontalLayout toolBar;

    private Label filterInstructionLabel = new Label("Filter the registrations by");

    private ListSelect statusFilter = null;

    private ListSelect submitterFilter = null; // must be null, the presenter relies on this

    private TextField identifierFilter = new TextField("Identifier");

    private TextField taxonNameFilter = new TextField("Name");

    private TextField referenceFilter = new TextField("Publication");

    public ListViewBean() {
        super();
    }

    @Override
    protected void initContent() {

        getLayout().setId(NAME);
        toolBar = new HorizontalLayout();

        toolBar.addComponent(filterInstructionLabel);

        if(UserHelper.fromSession().userIsRegistrationCurator() || UserHelper.fromSession().userIsAdmin()){

            submitterFilter = new ListSelect("Submitter");
            submitterFilter.setRows(1);
            submitterFilter.addValueChangeListener(e -> updateResults());
            toolBar.addComponent(submitterFilter);
        }

        if(viewMode.equals(Mode.all)){
            statusFilter = new ListSelect("Status", Arrays.asList(RegistrationStatus.values()));
            statusFilter.setNullSelectionAllowed(true);
            statusFilter.setRows(1);
            statusFilter.addValueChangeListener(e -> updateResults());
            toolBar.addComponent(statusFilter);
        }

        toolBar.addComponents(identifierFilter, taxonNameFilter);
        identifierFilter.addValueChangeListener(e -> updateResults());
        taxonNameFilter.addValueChangeListener(e -> updateResults());

        toolBar.setSpacing(true);
        addContentComponent(toolBar, null);

        listContainer = new CssLayout();
        listContainer.setId("registration-list");
        listContainer.setWidth(100, Unit.PERCENTAGE);
        addContentComponent(listContainer, 1f);

    }

    /**
     * @return
     */
    private void updateResults() {
        eventBus.publishEvent(new UpdateResultsEvent(this));
    }

    @Override
    protected String getHeaderText() {
        return "Registrations";
    }

    @Override
    protected String getSubHeaderText() {
        return "This is the list of all your registrations in progress.";
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void enter(ViewChangeEvent event) {
        getPresenter().onViewEnter();
    }

    @Override
    public void populate(Pager<RegistrationDTO> regDtoPager) {

        ArrayList<RegistrationDTO> regDtos = new ArrayList<RegistrationDTO>(regDtoPager.getRecords());

        populateList(regDtos);
    }

    public void populateList(Collection<RegistrationDTO> registrations) {

        listContainer.removeAllComponents();
        boolean isCurator = UserHelper.fromSession().userIsRegistrationCurator() || UserHelper.fromSession().userIsAdmin();
        for(RegistrationDTO regDto : registrations) {
            RegistrationItem item = new RegistrationItem(regDto, this);
            item.getSubmitterLabel().setVisible(isCurator);
            item.setWidth(100, Unit.PERCENTAGE);
            listContainer.addComponent(item);
        }
    }

    @Override
    public void openDetailsPopup(String caption, List<String> messages){
        StringBuffer sb = new StringBuffer();
        sb.append("<div class=\"details-popup-content\">");
        messages.forEach(s -> sb.append(s).append("</br>"));
        sb.append("</div>");
        new Notification(caption, sb.toString(), Notification.Type.HUMANIZED_MESSAGE, true).show(Page.getCurrent());
    }

    /**
     * @param registrationItems
     * @return
     */
    @Deprecated
    private GeneratedPropertyContainer buildGeneratedProperties(
            BeanContainer<String, RegistrationDTO> registrationItems) {
        GeneratedPropertyContainer gpContainer = new GeneratedPropertyContainer(registrationItems);

        gpContainer.addGeneratedProperty("operation", new PropertyValueGenerator<String>() {

            @Override
            public String getValue(Item item, Object itemId, Object propertyId) {
                return "Open";
            }

            @Override
            public Class<String> getType() {
                return String.class;
            }
          });

        return gpContainer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean allowAnonymousAccess() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Collection<GrantedAuthority>> allowedGrantedAuthorities() {
        return null;
    }

    /**
     * @return the identifierFilter
     */
    @Override
    public TextField getIdentifierFilter() {
        return identifierFilter;
    }

    /**
     * @return the taxonNameFilter
     */
    @Override
    public TextField getTaxonNameFilter() {
        return taxonNameFilter;
    }

    /**
     * @return the referenceFilter
     */
    @Override
    public TextField getReferenceFilter() {
        return referenceFilter;
    }

    /**
     * @return the statusFilter
     */
    @Override
    public ListSelect getStatusFilter() {
        return statusFilter;
    }

    /**
     * @return the submitterFilter
     */
    @Override
    public ListSelect getSubmitterFilter() {
        return submitterFilter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setViewMode(Mode mode) {
        viewMode = mode;
    }


    @Override
    public Mode getViewMode() {
        return viewMode;
    }







}
