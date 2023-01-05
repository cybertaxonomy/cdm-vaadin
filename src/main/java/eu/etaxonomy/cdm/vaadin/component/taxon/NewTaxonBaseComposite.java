/**
 * Copyright (C) 2015 EDIT
 * European Distributed Institute of Taxonomy
 * http://www.e-taxonomy.eu
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * See LICENSE.TXT at the top of this package for the full license terms.
 */
package eu.etaxonomy.cdm.vaadin.component.taxon;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Validator.EmptyValueException;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import eu.etaxonomy.cdm.vaadin.component.CdmProgressComponent;
import eu.etaxonomy.cdm.vaadin.component.TextFieldNFix;
import eu.etaxonomy.cdm.vaadin.container.IdUuidName;
import eu.etaxonomy.cdm.vaadin.session.CdmChangeEvent;
import eu.etaxonomy.cdm.vaadin.session.CdmChangeEvent.Action;
import eu.etaxonomy.cdm.vaadin.util.CdmVaadinOperation;
import eu.etaxonomy.cdm.vaadin.util.CdmVaadinUtilities;

/**
 * @author cmathew
 * @since 2 Apr 2015
 */
public class NewTaxonBaseComposite extends CustomComponent implements INewTaxonBaseComposite {

    @SuppressWarnings("unused")
    private static final Logger logger = LogManager.getLogger();

    /*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

    @AutoGenerated
    private VerticalLayout mainLayout;
    @AutoGenerated
    private CdmProgressComponent cdmProgressComponent;
    @AutoGenerated
    private HorizontalLayout horizontalLayout;
    @AutoGenerated
    private Button cancelButton;
    @AutoGenerated
    private Button saveButton;
    @AutoGenerated
    private GridLayout gridLayout;
    @AutoGenerated
    private ComboBox synComboBox;
    @AutoGenerated
    private Label synSecLabel;
    @AutoGenerated
    private ComboBox accTaxonSecComboBox;
    @AutoGenerated
    private Label accTaxonSecLabel;
    @AutoGenerated
    private TextField nameTextField;
    @AutoGenerated
    private Label nameLabel;
    @AutoGenerated
    private Label accTaxonNameValue;
    @AutoGenerated
    private Label accTaxonLabel;
    private INewTaxonBaseComponentListener listener;


    private final Window dialog;
    private final IdUuidName accTaxonIun;
    private final IdUuidName classificationIun;
    private static final String CHOOSE_SECUNDUM_PROMPT = "Choose Secundum ....";

    /**
     * The constructor should first build the main layout, set the
     * composition root and then do any custom initialization.
     *
     * The constructor will not be automatically regenerated by the
     * visual editor.
     */
    public NewTaxonBaseComposite(Window dialog,
            INewTaxonBaseComponentListener listener,
            IdUuidName accTaxonIun,
            String accTaxonName,
            IdUuidName classificationIun) {
        buildMainLayout();
        setCompositionRoot(mainLayout);

        this.listener = listener;
        this.dialog = dialog;
        this.accTaxonIun = accTaxonIun;
        this.classificationIun = classificationIun;

        addUIListeners();

        if(accTaxonName == null || accTaxonName.isEmpty()) {
            // this is the case where we create a new taxon
            accTaxonLabel.setVisible(false);
            accTaxonNameValue.setVisible(false);
            synSecLabel.setVisible(false);
            synComboBox.setVisible(false);
        } else {
            // this is the case where we create a new synonym
            accTaxonNameValue.setValue(accTaxonName);
        }
        init();
    }

    public void init() {
        initAccTaxonSecComboBox();
        if(accTaxonIun != null) {
            initSynSecComboBox();
        }
    }

    private void  initAccTaxonSecComboBox() {
        accTaxonSecComboBox.setNullSelectionAllowed(false);
        accTaxonSecComboBox.setItemCaptionPropertyId("titleCache");
        accTaxonSecComboBox.setImmediate(true);
        if(listener != null) {
            accTaxonSecComboBox.setContainerDataSource(listener.getAccTaxonSecRefContainer());
            Object selectedSecItemId = listener.getClassificationRefId(classificationIun.getUuid());
            if(selectedSecItemId != null) {
                accTaxonSecComboBox.setValue(selectedSecItemId);
            } else {
                accTaxonSecComboBox.setInputPrompt(CHOOSE_SECUNDUM_PROMPT);
            }
        }
    }

    private void initSynSecComboBox() {
        synComboBox.setNullSelectionAllowed(false);
        synComboBox.setItemCaptionPropertyId("titleCache");
        synComboBox.setImmediate(true);
        if(listener != null) {
            synComboBox.setContainerDataSource(listener.getSynSecRefContainer());
            Object selectedSecItemId = listener.getClassificationRefId(classificationIun.getUuid());
            if(selectedSecItemId != null) {
                synComboBox.setValue(selectedSecItemId);
            } else {
                synComboBox.setInputPrompt(CHOOSE_SECUNDUM_PROMPT);
            }
        }
    }



    private void addUIListeners() {
        addSaveButtonListener();
        addCancelButtonListener();
    }

    private void addSaveButtonListener() {
        saveButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    nameTextField.validate();
                    accTaxonSecComboBox.validate();
                    if(accTaxonIun != null) {
                        synComboBox.validate();
                    }
                } catch (EmptyValueException e) {
                    Notification notification = new Notification("Invalid input", "Neither Name or Secundum can be empty", Type.WARNING_MESSAGE);
                    notification.show(Page.getCurrent());
                    return;
                }

                CdmVaadinUtilities.setEnabled(mainLayout, false, null);

                CdmVaadinUtilities.exec(new CdmVaadinOperation(500, cdmProgressComponent) {
                    @Override
                    public boolean execute() {
                        setProgress("Saving Taxon " + nameTextField.getValue());
                        IdUuidName taxonBaseIdUuid;
                        boolean newTaxon = false;
                        try {
                            if(accTaxonIun == null) {
                                taxonBaseIdUuid = listener.newTaxon(nameTextField.getValue(),accTaxonSecComboBox.getValue(), classificationIun.getUuid());
                                newTaxon = true;
                            } else {
                                taxonBaseIdUuid = listener.newSynonym(nameTextField.getValue(),
                                        accTaxonSecComboBox.getValue(),
                                        accTaxonSecComboBox.getValue(),
                                        accTaxonIun.getUuid());
                                newTaxon = false;
                            }
                            Object rowId = new RowId(taxonBaseIdUuid.getId());
                            registerDelayedEvent(new CdmChangeEvent(Action.Create, Arrays.asList(rowId, newTaxon), NewTaxonBaseComposite.this));
                        } catch (IllegalArgumentException iae) {
                            setException(iae);
                            return false;
                        }
                        return true;
                    }

                    @Override
                    public void postOpUIUpdate(boolean success) {
                        if(success) {
                            UI.getCurrent().removeWindow(dialog);
                        } else {
                            CdmVaadinUtilities.setEnabled(mainLayout, true, null);
                        }
                    }
                });
            }
        });
    }

    private void addCancelButtonListener() {
        cancelButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                UI.getCurrent().removeWindow(dialog);
            }

        });
    }

    /* (non-Javadoc)
     * @see eu.etaxonomy.cdm.vaadin.view.INewTaxonComposite#setListener(eu.etaxonomy.cdm.vaadin.view.INewTaxonBaseComponentListener)
     */
    @Override
    public void setListener(INewTaxonBaseComponentListener listener) {
        this.listener = listener;

    }

    @AutoGenerated
    private VerticalLayout buildMainLayout() {
        // common part: create layout
        mainLayout = new VerticalLayout();
        mainLayout.setImmediate(false);
        mainLayout.setWidth("420px");
        mainLayout.setHeight("240px");
        mainLayout.setMargin(true);

        // top-level component properties
        setWidth("420px");
        setHeight("240px");

        // gridLayout
        gridLayout = buildGridLayout();
        mainLayout.addComponent(gridLayout);
        mainLayout.setExpandRatio(gridLayout, 1.0f);

        // horizontalLayout
        horizontalLayout = buildHorizontalLayout();
        mainLayout.addComponent(horizontalLayout);
        mainLayout.setComponentAlignment(horizontalLayout, new Alignment(20));

        // cdmProgressComponent
        cdmProgressComponent = new CdmProgressComponent();
        cdmProgressComponent.setImmediate(false);
        cdmProgressComponent.setWidth("-1px");
        cdmProgressComponent.setHeight("-1px");
        mainLayout.addComponent(cdmProgressComponent);
        mainLayout.setComponentAlignment(cdmProgressComponent, new Alignment(48));

        return mainLayout;
    }

    @AutoGenerated
    private GridLayout buildGridLayout() {
        // common part: create layout
        gridLayout = new GridLayout();
        gridLayout.setImmediate(false);
        gridLayout.setWidth("-1px");
        gridLayout.setHeight("140px");
        gridLayout.setMargin(false);
        gridLayout.setSpacing(true);
        gridLayout.setColumns(2);
        gridLayout.setRows(4);

        // accTaxonLabel
        accTaxonLabel = new Label();
        accTaxonLabel.setImmediate(false);
        accTaxonLabel.setWidth("-1px");
        accTaxonLabel.setHeight("-1px");
        accTaxonLabel.setValue("Acc. Taxon : ");
        gridLayout.addComponent(accTaxonLabel, 0, 0);
        gridLayout.setComponentAlignment(accTaxonLabel, new Alignment(34));

        // accTaxonNameValue
        accTaxonNameValue = new Label();
        accTaxonNameValue.setImmediate(false);
        accTaxonNameValue.setWidth("-1px");
        accTaxonNameValue.setHeight("-1px");
        accTaxonNameValue.setValue("Taxon Name");
        gridLayout.addComponent(accTaxonNameValue, 1, 0);
        gridLayout.setComponentAlignment(accTaxonNameValue, new Alignment(33));

        // nameLabel
        nameLabel = new Label();
        nameLabel.setImmediate(false);
        nameLabel.setWidth("-1px");
        nameLabel.setHeight("-1px");
        nameLabel.setValue("Name : ");
        gridLayout.addComponent(nameLabel, 0, 1);
        gridLayout.setComponentAlignment(nameLabel, new Alignment(34));

        // nameTextField
        nameTextField = new TextFieldNFix();
        nameTextField.setImmediate(false);
        nameTextField.setWidth("190px");
        nameTextField.setHeight("-1px");
        nameTextField.setInvalidAllowed(false);
        nameTextField.setRequired(true);
        gridLayout.addComponent(nameTextField, 1, 1);
        gridLayout.setComponentAlignment(nameTextField, new Alignment(33));

        // accTaxonSecLabel
        accTaxonSecLabel = new Label();
        accTaxonSecLabel.setImmediate(false);
        accTaxonSecLabel.setWidth("-1px");
        accTaxonSecLabel.setHeight("-1px");
        accTaxonSecLabel.setValue("Acc. Taxon Secundum : ");
        gridLayout.addComponent(accTaxonSecLabel, 0, 2);
        gridLayout.setComponentAlignment(accTaxonSecLabel, new Alignment(34));

        // accTaxonSecComboBox
        accTaxonSecComboBox = new ComboBox();
        accTaxonSecComboBox.setImmediate(false);
        accTaxonSecComboBox.setWidth("190px");
        accTaxonSecComboBox.setHeight("-1px");
        accTaxonSecComboBox.setInvalidAllowed(false);
        accTaxonSecComboBox.setRequired(true);
        gridLayout.addComponent(accTaxonSecComboBox, 1, 2);
        gridLayout.setComponentAlignment(accTaxonSecComboBox, new Alignment(33));

        // synSecLabel
        synSecLabel = new Label();
        synSecLabel.setImmediate(false);
        synSecLabel.setWidth("-1px");
        synSecLabel.setHeight("-1px");
        synSecLabel.setValue("Synonym Secundum : ");
        gridLayout.addComponent(synSecLabel, 0, 3);

        // synComboBox
        synComboBox = new ComboBox();
        synComboBox.setImmediate(false);
        synComboBox.setWidth("190px");
        synComboBox.setHeight("-1px");
        synComboBox.setInvalidAllowed(false);
        synComboBox.setRequired(true);
        gridLayout.addComponent(synComboBox, 1, 3);

        return gridLayout;
    }

    @AutoGenerated
    private HorizontalLayout buildHorizontalLayout() {
        // common part: create layout
        horizontalLayout = new HorizontalLayout();
        horizontalLayout.setImmediate(false);
        horizontalLayout.setWidth("-1px");
        horizontalLayout.setHeight("-1px");
        horizontalLayout.setMargin(false);
        horizontalLayout.setSpacing(true);

        // saveButton
        saveButton = new Button();
        saveButton.setCaption("Save");
        saveButton.setImmediate(true);
        saveButton.setWidth("-1px");
        saveButton.setHeight("-1px");
        horizontalLayout.addComponent(saveButton);
        horizontalLayout.setComponentAlignment(saveButton, new Alignment(20));

        // cancelButton
        cancelButton = new Button();
        cancelButton.setCaption("Cancel");
        cancelButton.setImmediate(true);
        cancelButton.setWidth("-1px");
        cancelButton.setHeight("-1px");
        horizontalLayout.addComponent(cancelButton);
        horizontalLayout.setComponentAlignment(cancelButton, new Alignment(20));

        return horizontalLayout;
    }


}
