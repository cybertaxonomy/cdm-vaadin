// $Id$
/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component;

import java.util.Arrays;

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
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import eu.etaxonomy.cdm.vaadin.container.IdAndUuid;
import eu.etaxonomy.cdm.vaadin.session.CdmChangeEvent;
import eu.etaxonomy.cdm.vaadin.session.CdmChangeEvent.Action;
import eu.etaxonomy.cdm.vaadin.util.CdmVaadinSessionUtilities;
import eu.etaxonomy.cdm.vaadin.view.INewTaxonBaseComponentListener;
import eu.etaxonomy.cdm.vaadin.view.INewTaxonBaseComposite;

/**
 * @author cmathew
 * @date 2 Apr 2015
 *
 */
public class NewTaxonBaseComposite extends CustomComponent implements INewTaxonBaseComposite {

    /*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

    @AutoGenerated
    private GridLayout mainLayout;
    @AutoGenerated
    private Button cancelButton;
    @AutoGenerated
    private Button saveButton;
    @AutoGenerated
    private ComboBox secComboBox;
    @AutoGenerated
    private Label secLabel;
    @AutoGenerated
    private TextField nameTextField;
    @AutoGenerated
    private Label nameLabel;
    private INewTaxonBaseComponentListener listener;


    private final Window dialog;
    private final IdAndUuid accTaxonIdUuid;
    private final IdAndUuid classificationIdUuid;

    /**
     * The constructor should first build the main layout, set the
     * composition root and then do any custom initialization.
     *
     * The constructor will not be automatically regenerated by the
     * visual editor.
     */
    public NewTaxonBaseComposite(Window dialog,
            INewTaxonBaseComponentListener listener,
            IdAndUuid accTaxonIdUuid,
            IdAndUuid classificationIdUuid) {
        buildMainLayout();
        setCompositionRoot(mainLayout);

        this.listener = listener;
        this.dialog = dialog;
        this.accTaxonIdUuid = accTaxonIdUuid;
        this.classificationIdUuid = classificationIdUuid;

        addUIListeners();

        init();
    }

    public void init() {
        initNameTextField();
        initSecComboBox();
    }

    private void initNameTextField() {
        //nameTextField.addValidator(new StringLengthValidator("Name cannot be empty", 1,100, false));
    }

    private void initSecComboBox() {

        secComboBox.setNullSelectionAllowed(false);
        secComboBox.setItemCaptionPropertyId("titleCache");
        secComboBox.setImmediate(true);
        if(listener != null) {
            secComboBox.setContainerDataSource(listener.getSecRefContainer());
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
                    secComboBox.validate();

                    IdAndUuid taxonIdUuid;
                    if(accTaxonIdUuid == null) {
                        taxonIdUuid = listener.newTaxon(nameTextField.getValue(),secComboBox.getValue(), classificationIdUuid.getUuid());
                    } else {
                        listener.newSynonym(nameTextField.getValue(),secComboBox.getValue(), accTaxonIdUuid.getUuid());
                        taxonIdUuid = accTaxonIdUuid;
                    }
                    Object rowId = new RowId(taxonIdUuid.getId());
                    CdmVaadinSessionUtilities.getCurrentCdmDataChangeService()
                        .fireChangeEvent(new CdmChangeEvent(Action.Create, Arrays.asList(rowId), NewTaxonBaseComposite.class), true);
                    UI.getCurrent().removeWindow(dialog);
                } catch (EmptyValueException e) {
                    Notification notification = new Notification("Invalid input", "Neither Name or Secundum can be empty", Type.WARNING_MESSAGE);
                    notification.setDelayMsec(2000);
                    notification.show(Page.getCurrent());
                }
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
    private GridLayout buildMainLayout() {
        // common part: create layout
        mainLayout = new GridLayout();
        mainLayout.setImmediate(false);
        mainLayout.setWidth("320px");
        mainLayout.setHeight("120px");
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);
        mainLayout.setColumns(2);
        mainLayout.setRows(3);

        // top-level component properties
        setWidth("320px");
        setHeight("120px");

        // nameLabel
        nameLabel = new Label();
        nameLabel.setImmediate(false);
        nameLabel.setWidth("-1px");
        nameLabel.setHeight("-1px");
        nameLabel.setValue("Name : ");
        mainLayout.addComponent(nameLabel, 0, 0);
        mainLayout.setComponentAlignment(nameLabel, new Alignment(34));

        // nameTextField
        nameTextField = new TextField();
        nameTextField.setImmediate(false);
        nameTextField.setWidth("190px");
        nameTextField.setHeight("-1px");
        nameTextField.setInvalidAllowed(false);
        nameTextField.setRequired(true);
        mainLayout.addComponent(nameTextField, 1, 0);
        mainLayout.setComponentAlignment(nameTextField, new Alignment(33));

        // secLabel
        secLabel = new Label();
        secLabel.setImmediate(false);
        secLabel.setWidth("-1px");
        secLabel.setHeight("-1px");
        secLabel.setValue("Secundum : ");
        mainLayout.addComponent(secLabel, 0, 1);
        mainLayout.setComponentAlignment(secLabel, new Alignment(34));

        // secComboBox
        secComboBox = new ComboBox();
        secComboBox.setImmediate(false);
        secComboBox.setWidth("190px");
        secComboBox.setHeight("-1px");
        secComboBox.setInvalidAllowed(false);
        secComboBox.setRequired(true);
        mainLayout.addComponent(secComboBox, 1, 1);
        mainLayout.setComponentAlignment(secComboBox, new Alignment(33));

        // saveButton
        saveButton = new Button();
        saveButton.setCaption("Save");
        saveButton.setImmediate(true);
        saveButton.setWidth("-1px");
        saveButton.setHeight("-1px");
        mainLayout.addComponent(saveButton, 0, 2);
        mainLayout.setComponentAlignment(saveButton, new Alignment(34));

        // cancelButton
        cancelButton = new Button();
        cancelButton.setCaption("Cancel");
        cancelButton.setImmediate(true);
        cancelButton.setWidth("-1px");
        cancelButton.setHeight("-1px");
        mainLayout.addComponent(cancelButton, 1, 2);
        mainLayout.setComponentAlignment(cancelButton, new Alignment(33));

        return mainLayout;
    }


}
