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
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.api.service.dto.RegistrationDTO;
import eu.etaxonomy.cdm.api.service.pager.Pager;
import eu.etaxonomy.cdm.api.utility.RoleProber;
import eu.etaxonomy.cdm.model.ICdmUuidCacher;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.RegistrationStatus;
import eu.etaxonomy.cdm.service.UserHelperAccess;
import eu.etaxonomy.cdm.vaadin.component.PagerComponent;
import eu.etaxonomy.cdm.vaadin.component.TextFieldNFix;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItem;
import eu.etaxonomy.cdm.vaadin.event.PagingEvent;
import eu.etaxonomy.cdm.vaadin.event.ShowDetailsEvent;
import eu.etaxonomy.cdm.vaadin.event.UpdateResultsEvent;
import eu.etaxonomy.cdm.vaadin.permission.AccessRestrictedView;
import eu.etaxonomy.cdm.vaadin.permission.RolesAndPermissions;
import eu.etaxonomy.cdm.vaadin.view.AbstractPageView;
import eu.etaxonomy.cdm.vaadin.view.name.CachingPresenter;

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

    private PagerComponent pagerTop;

    private Label filterInstructionLabel = new Label("Filter the registrations by");

    private ListSelect registrationStatusFilter = null;

    private ListSelect submitterFilter = null; // must be null, the presenter relies on this

    private TextField identifierFilter = new TextFieldNFix("Identifier");

    private TextField taxonNameFilter = new TextFieldNFix("Name");

    private TextField referenceFilter = new TextFieldNFix("Publication");

    private AbstractSelect statusTypeFilter;

    public ListViewBean() {
        super();
    }

    @Override
    protected void initContent() {

        getLayout().setId(NAME);
        toolBar = new HorizontalLayout();

        toolBar.addComponent(filterInstructionLabel);

        boolean userIsCurator = UserHelperAccess.userHelper().userIs(new RoleProber(RolesAndPermissions.ROLE_CURATION));
        boolean userIsAdmin = UserHelperAccess.userHelper().userIsAdmin();
        if(userIsCurator || userIsAdmin){
            submitterFilter = new ListSelect("Submitter");
            submitterFilter.setRows(1);
            submitterFilter.addValueChangeListener(e -> updateResults(null, null));
            toolBar.addComponent(submitterFilter);
        }

        if(viewMode.equals(Mode.all)){
            registrationStatusFilter = new ListSelect("Registration status", Arrays.asList(RegistrationStatus.values()));
            registrationStatusFilter.setNullSelectionAllowed(true);
            registrationStatusFilter.setRows(1);
            registrationStatusFilter.addValueChangeListener(e -> updateResults(null, null));
            toolBar.addComponent(registrationStatusFilter);
        }

        statusTypeFilter = new ListSelect("Type Status");
        ((ListSelect)statusTypeFilter).setRows(3);
        statusTypeFilter.setMultiSelect(true);
        statusTypeFilter.setNullSelectionAllowed(true);
        statusTypeFilter.addValueChangeListener(e -> updateResults(null, null));
        statusTypeFilter.setDescription("Strg + Click to unselect");

        toolBar.addComponents(identifierFilter, taxonNameFilter, statusTypeFilter);
        int textChangeTimeOut = 200;
        identifierFilter.addTextChangeListener(e -> updateResults(identifierFilter, e.getText()));
        identifierFilter.setTextChangeTimeout(textChangeTimeOut);
        taxonNameFilter.addTextChangeListener(e -> updateResults(taxonNameFilter, e.getText()));
        identifierFilter.setTextChangeTimeout(textChangeTimeOut);

        toolBar.setSpacing(true);
        toolBar.iterator().forEachRemaining( c -> c.addStyleName(ValoTheme.LABEL_TINY));
        addContentComponent(toolBar, null);

        pagerTop =  new PagerComponent(new PagerComponent.PagerClickListener() {

            @Override
            public void pageIndexClicked(Integer index) {
                getViewEventBus().publish(ListViewBean.this, new PagingEvent(ListViewBean.this, index));

            }
        });
        addContentComponent(pagerTop, null);
        ((VerticalLayout)getLayout()).setComponentAlignment(pagerTop, Alignment.MIDDLE_CENTER);

        listContainer = new CssLayout();
        listContainer.setId("registration-list");
        listContainer.setWidth(100, Unit.PERCENTAGE);
        addContentComponent(listContainer, 1f);

    }

    /**
     * @return
     */
    private void updateResults(TextField field, String newText) {
        getViewEventBus().publish(this, new UpdateResultsEvent(field, newText, this));
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

        pagerTop.updatePager(regDtoPager);

        populateList(regDtos);
    }

    public void populateList(Collection<RegistrationDTO> registrations) {

        listContainer.removeAllComponents();
        boolean isCurator = UserHelperAccess.userHelper().userIs(new RoleProber(RolesAndPermissions.ROLE_CURATION)) || UserHelperAccess.userHelper().userIsAdmin();
        for(RegistrationDTO regDto : registrations) {
            RegistrationItem item = new RegistrationItem(regDto, this, (ICdmUuidCacher)((CachingPresenter)getPresenter()).getCache());
            item.getSubmitterLabel().setVisible(isCurator);
            item.setWidth(100, Unit.PERCENTAGE);
            item.getBlockedByButton().addClickListener(e -> getViewEventBus().publish(
                    this,
                    new ShowDetailsEvent<Registration, UUID>(
                            e, Registration.class, regDto.getUuid(), "blockedBy"
                            )
                    ));
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
     * @return the registrationStatusFilter
     */
    @Override
    public ListSelect getRegistrationStatusFilter() {
        return registrationStatusFilter;
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

    @Override
    public RegistrationItem getRegistrationItem(UUID registrationUuid){
        for(Component c : listContainer){
            RegistrationItem item = (RegistrationItem)c;
            if(registrationUuid.equals(item.getRegistrationUuid())){
                return item;
            }
        }
        return null;
    }

    /**
     * @return the statusTypeFilter for the TypeDesignation.statusType
     */
    @Override
    public AbstractSelect getStatusTypeFilter() {
        return statusTypeFilter;
    }
}
