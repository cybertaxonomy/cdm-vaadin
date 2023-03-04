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
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.vaadin.viritin.fields.LazyComboBox;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.persistence.query.MatchMode;
import eu.etaxonomy.cdm.ref.TypedEntityReference;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEditorAction;
import eu.etaxonomy.cdm.vaadin.event.RegistrationEditorAction;
import eu.etaxonomy.cdm.vaadin.permission.AccessRestrictedView;
import eu.etaxonomy.cdm.vaadin.theme.EditValoTheme;
import eu.etaxonomy.cdm.vaadin.view.AbstractPageView;
import eu.etaxonomy.vaadin.event.EditorActionType;

/**
 * @author a.kohlbecker
 * @since Mar 2, 2017
 */
@SpringView(name=StartRegistrationViewBean.NAME)
public class StartRegistrationViewBean
        extends AbstractPageView<StartRegistrationView,StartRegistrationPresenter>
        implements StartRegistrationView, AccessRestrictedView, View {

    private static final long serialVersionUID = -9055865292188732909L;

    public static final String NAME = "regStart";

    public static final String SUBHEADER_DEEFAULT = "Any valid nomenclatural act can only be etablished in a publication. "
            + "To start a new registration process, please choose an existing one or create a new publication.";

    private LazyComboBox<TypedEntityReference<Reference>> referenceCombobox;

    private OptionGroup searchModeOptions = new OptionGroup("Search mode");

    private Button newPublicationButton;

    private Button removeNewPublicationButton;

    private Label newPublicationLabel;

    private Button continueButton;

    private String accessDeniedMessage;

    private static final String ELEMENT_WIDTH = "330px";


    public StartRegistrationViewBean() {
        super();
    }

    @Override
    protected void initContent() {

        getLayout().setId(NAME);

        VerticalLayout vlayout = new VerticalLayout();
        vlayout.setSpacing(true);
        vlayout.setMargin(true);

        HorizontalLayout publicationLayout = new HorizontalLayout();
        publicationLayout.setSpacing(true);

        @SuppressWarnings({ "unchecked", "rawtypes" })
        Class<TypedEntityReference<Reference>> type = (Class)TypedEntityReference.class;
        referenceCombobox = new LazyComboBox<TypedEntityReference<Reference>>(type) {

            private static final long serialVersionUID = 4209637913208388691L;

            @Override
            protected void setSelectInstance(AbstractSelect select) {
                if(select instanceof ComboBox){
                    ComboBox combobox = (ComboBox)select;
                    // uncomment the below line to set a defined width for the ComboBox popup
                    // combobox.setPopupWidth("200%");
                }
                super.setSelectInstance(select);
            }
        };
        referenceCombobox.setWidth(ELEMENT_WIDTH);
        referenceCombobox.setBuffered(false);
        referenceCombobox.addValueChangeListener( e -> {
            boolean isValueSelected = e.getProperty().getValue() != null;
            continueButton.setEnabled(isValueSelected);
        });

        searchModeOptions.addItem(MatchMode.BEGINNING);
        searchModeOptions.setItemCaption(MatchMode.BEGINNING, "Begins with");
        searchModeOptions.addItem(MatchMode.ANYWHERE);
        searchModeOptions.setItemCaption(MatchMode.ANYWHERE, "Contains");
        searchModeOptions.setValue(MatchMode.BEGINNING);
        searchModeOptions.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        searchModeOptions.addStyleName(EditValoTheme.OPTIONGROUP_CAPTION_FIX);
        searchModeOptions.addValueChangeListener(e -> getPresenter().updateReferenceSearchMode((MatchMode)e.getProperty().getValue()));

        newPublicationButton = new Button("New");
        newPublicationButton.addClickListener( e -> getViewEventBus().publish(this,
                new ReferenceEditorAction(EditorActionType.ADD, newPublicationButton, null, this)
                ));
        newPublicationButton.setCaption("New");
        newPublicationButton.setWidth(ELEMENT_WIDTH);

        newPublicationLabel = new Label();
        newPublicationLabel.setVisible(false);

        removeNewPublicationButton = new Button("Delete");
        removeNewPublicationButton.setStyleName(ValoTheme.BUTTON_DANGER);
        removeNewPublicationButton.setWidth(ELEMENT_WIDTH);
        removeNewPublicationButton.addClickListener( e -> getViewEventBus().publish(this,
                new ReferenceEditorAction(EditorActionType.REMOVE, removeNewPublicationButton, null, this)
                ));

        removeNewPublicationButton.setVisible(false);

        Label labelLeft = new Label("Choose from existing publications");
        Label labelRight = new Label("Create a new publication");
        labelLeft.setWidth(ELEMENT_WIDTH);
        labelRight.setWidth(ELEMENT_WIDTH);

        CssLayout leftContainer = new CssLayout(labelLeft, referenceCombobox, searchModeOptions);
        CssLayout rightContainer = new CssLayout(labelRight, newPublicationButton, removeNewPublicationButton, newPublicationLabel);
        leftContainer.setWidth(ELEMENT_WIDTH);
        rightContainer.setWidth(ELEMENT_WIDTH);

        publicationLayout.addComponents(
                leftContainer,
                rightContainer
                );
        publicationLayout.setComponentAlignment(leftContainer, Alignment.TOP_RIGHT);
        publicationLayout.setComponentAlignment(rightContainer, Alignment.TOP_LEFT);

        continueButton = new Button("Continue");
        continueButton.setStyleName(ValoTheme.BUTTON_PRIMARY + " " + ValoTheme.BUTTON_HUGE);
        continueButton.setEnabled(false);
        continueButton.addClickListener(e -> {

            UUID refUuid = null;
            referenceCombobox.commit();
            if(referenceCombobox.getValue() != null){
                refUuid = referenceCombobox.getValue().getUuid();
            }
            getViewEventBus().publish(this,
                new RegistrationEditorAction(EditorActionType.ADD,
                        // passing the refId is hack, bit for some reason the presenter is always referring to the wrong view
                        refUuid,
                        continueButton,
                        null,
                        StartRegistrationViewBean.this)
                );
              }
            );

        vlayout.addComponents(publicationLayout, continueButton);
        vlayout.setComponentAlignment(publicationLayout, Alignment.TOP_CENTER);
        vlayout.setComponentAlignment(continueButton, Alignment.TOP_CENTER);

        addContentComponent(vlayout, 1f);
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

    @Override
    public String getAccessDeniedMessage() {
        return accessDeniedMessage;
    }

    @Override
    public void setAccessDeniedMessage(String accessDeniedMessage) {
        this.accessDeniedMessage = accessDeniedMessage;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected String getHeaderText() {
        return "New Registration";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getSubHeaderText() {
        return SUBHEADER_DEEFAULT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enter(ViewChangeEvent event) {

        getPresenter().handleViewEntered();

    }

    // ------- StartRegistrationView interface methods ----- //

    /**
     * @return the referenceCombobox
     */
    @Override
    public LazyComboBox<TypedEntityReference<Reference>> getReferenceCombobox() {
        return referenceCombobox;
    }

    /**
     * @return the newPublicationButton
     */
    @Override
    public Button getNewPublicationButton() {
        return newPublicationButton;
    }

    /**
     * @return the newPublicationButton
     */
    @Override
    public Button getRemoveNewPublicationButton() {
        return removeNewPublicationButton;
    }

    /**
     * @return the newPublicationButton
     */
    @Override
    public Button getContinueButton() {
        return continueButton;
    }

    /**
     * @return the newPublicationLabel
     */
    @Override
    public Label getNewPublicationLabel() {
        return newPublicationLabel;
    }



}
