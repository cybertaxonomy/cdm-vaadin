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
import com.vaadin.ui.Notification;

import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItem;
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

    public static final String OPTION_ALL = "all";

    public static final String OPTION_IN_PROGRESS = "inprogress";

    private CssLayout listContainer;

    private CssLayout toolBar;

    public ListViewBean() {
        super();
    }

    @Override
    protected void initContent() {
        toolBar = new CssLayout();
        toolBar.setWidth(100, Unit.PERCENTAGE);
        // toolBar.addComponent();
        getLayout().addComponent(toolBar);

        buildList();
        getLayout().addComponent(listContainer);

    }

    @Override
    protected String getHeaderText() {
        return "Registrations";
    }

    @Override
    protected String getSubHeaderText() {
        return "This is the list of all your registrations in progress.";
    }

    private void buildList() {
        listContainer = new CssLayout();
        listContainer.setId("registration-list");
        listContainer.setWidth(100, Unit.PERCENTAGE);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void enter(ViewChangeEvent event) {
        getPresenter().onViewEnter();
    }

    @Override
    public void populate(Collection<RegistrationDTO> registrations) {

        registrations = new ArrayList<RegistrationDTO>(registrations);

        populateList(registrations);
    }

    public void populateList(Collection<RegistrationDTO> registrations) {

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



}
