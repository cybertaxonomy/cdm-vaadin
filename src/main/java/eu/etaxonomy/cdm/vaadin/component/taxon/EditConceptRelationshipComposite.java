/**
 * Copyright (C) 2015 EDIT
 * European Distributed Institute of Taxonomy
 * http://www.e-taxonomy.eu
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * See LICENSE.TXT at the top of this package for the full license terms.
 */
package eu.etaxonomy.cdm.vaadin.component.taxon;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Container.Hierarchical;
import com.vaadin.data.Validator.EmptyValueException;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import eu.etaxonomy.cdm.vaadin.component.CdmProgressComponent;
import eu.etaxonomy.cdm.vaadin.component.TextFieldNFix;
import eu.etaxonomy.cdm.vaadin.container.IdUuidName;
import eu.etaxonomy.cdm.vaadin.container.LeafNodeTaxonContainer;
import eu.etaxonomy.cdm.vaadin.jscomponent.D3ConceptRelationshipTree.Direction;
import eu.etaxonomy.cdm.vaadin.session.BasicEvent;
import eu.etaxonomy.cdm.vaadin.session.CdmChangeEvent;
import eu.etaxonomy.cdm.vaadin.session.CdmChangeEvent.Action;
import eu.etaxonomy.cdm.vaadin.util.CdmVaadinOperation;
import eu.etaxonomy.cdm.vaadin.util.CdmVaadinSessionUtilities;
import eu.etaxonomy.cdm.vaadin.util.CdmVaadinUtilities;
;

/**
 * @author cmathew
 * @date 13 Apr 2015
 *
 */
public class EditConceptRelationshipComposite extends CustomComponent {

    /*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

    @AutoGenerated
    private VerticalLayout mainLayout;
    @AutoGenerated
    private CdmProgressComponent cdmProgressComponent;
    @AutoGenerated
    private HorizontalLayout saveCancelHLayout;
    @AutoGenerated
    private Button cancelButton;
    @AutoGenerated
    private Button saveButton;
    @AutoGenerated
    private HorizontalLayout horizontalLayout;
    @AutoGenerated
    private Label rightLabel;
    @AutoGenerated
    private VerticalLayout typeVLayout;
    @AutoGenerated
    private ComboBox conceptRComboBox;
    @AutoGenerated
    private Label typeLabel;
    @AutoGenerated
    private Label leftLabel;
    private TextField leftTaxonTextField, rightTaxonTextField;

    private Component leftTaxonComponent, rightTaxonComponent;
    private TextField fromTaxonTextField, toTaxonTextField;
    private Label leftTaxonLabel, rightTaxonLabel;


    private final EditConceptRelationshipPresenter presenter;

    private IdUuidName fromTaxonIun, taxonRTypeIun, toTaxonIun;
    private UUID relUuid;
    private Window window;

    private final static String CHOOSE_TREL_TYPE = "Choose Type ...";
    private final static String DRAG_TAXON_HINT = "Drag Taxon here ...";


    private Action action;
    private Direction direction;

    public EditConceptRelationshipComposite(IdUuidName fromTaxonIdUuidName,
            IdUuidName taxonRTypeIdUuidName,
            IdUuidName toTaxonIdUuidName,
            Action action,
            Direction direction) {
        this(direction);

        init(fromTaxonIdUuidName, taxonRTypeIdUuidName, toTaxonIdUuidName, action);

    }

    public EditConceptRelationshipComposite(IdUuidName fromTaxonIun,
            UUID relUuid,
            Action action,
            Direction direction) {
        this(direction);
        this.relUuid = relUuid;

        Map<String, IdUuidName> map = presenter.getRelTypeToTaxonIunMap(fromTaxonIun.getUuid(), relUuid);
        taxonRTypeIun = map.get(EditConceptRelationshipPresenter.REL_TYPE_KEY);
        toTaxonIun = map.get(EditConceptRelationshipPresenter.TO_TAXON_KEY);
        init(fromTaxonIun, taxonRTypeIun, toTaxonIun, action);

    }


    /**
     * The constructor should first build the main layout, set the
     * composition root and then do any custom initialization.
     *
     * The constructor will not be automatically regenerated by the
     * visual editor.
     */
    private EditConceptRelationshipComposite(Direction direction) {
        this.direction = direction;

        buildMainLayout();
        initDirectionComponents();
        setCompositionRoot(mainLayout);

        this.presenter = new EditConceptRelationshipPresenter();
        addUIListeners();
    }


    public void init(IdUuidName fromTaxonIdUuidName,
            IdUuidName taxonRTypeIdUuidName,
            IdUuidName toTaxonIdUuidName,
            Action action) {

        this.fromTaxonIun = fromTaxonIdUuidName;
        this.taxonRTypeIun = taxonRTypeIdUuidName;
        this.toTaxonIun = toTaxonIdUuidName;
        this.action = action;

        initFromTaxon();
        initConceptRComboBox();
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    public boolean canCreateRelationship() {
        return presenter.canCreateRelationship(fromTaxonIun.getUuid());
    }

    private void initFromTaxon() {
        if(fromTaxonIun != null) {
            fromTaxonTextField.setReadOnly(false);
            fromTaxonTextField.setValue(fromTaxonIun.getName());
            fromTaxonTextField.setReadOnly(true);
        }
        if(toTaxonIun != null) {
            toTaxonTextField.setReadOnly(false);
            toTaxonTextField.setValue(toTaxonIun.getName());
            toTaxonTextField.setReadOnly(true);
        }
    }

    private void initDirectionComponents() {

        initTaxonComponents();

        leftLabel.addStyleName("cr-arrow");
        leftLabel.setContentMode(ContentMode.HTML);

        rightLabel.addStyleName("cr-arrow");
        rightLabel.setContentMode(ContentMode.HTML);

        rightTaxonTextField.setReadOnly(false);
        leftTaxonTextField.setReadOnly(false);
        switch(direction) {
        case LEFT_RIGHT:
            leftLabel.setValue(FontAwesome.CARET_RIGHT.getHtml());
            rightLabel.setValue(FontAwesome.CARET_RIGHT.getHtml());
            leftTaxonLabel.setValue("From Taxon");
            rightTaxonLabel.setValue("To Taxon");
            fromTaxonTextField = leftTaxonTextField;
            toTaxonTextField = rightTaxonTextField;
            rightTaxonTextField.setValue(DRAG_TAXON_HINT);
            rightTaxonComponent = intiDragDropWrapper(rightTaxonComponent, rightTaxonTextField);
            break;
        case RIGHT_LEFT:
            leftLabel.setValue(FontAwesome.CARET_LEFT.getHtml());
            rightLabel.setValue(FontAwesome.CARET_LEFT.getHtml());
            leftTaxonLabel.setValue("To Taxon");
            rightTaxonLabel.setValue("From Taxon");
            leftTaxonTextField.setValue(DRAG_TAXON_HINT);
            fromTaxonTextField = rightTaxonTextField;
            toTaxonTextField = leftTaxonTextField;
            leftTaxonComponent = intiDragDropWrapper(leftTaxonComponent, leftTaxonTextField);
            break;
        }

        rightTaxonTextField.setReadOnly(true);
        leftTaxonTextField.setReadOnly(true);

        horizontalLayout.addComponent(leftTaxonComponent,0);
        horizontalLayout.setComponentAlignment(leftTaxonComponent, new Alignment(48));


        horizontalLayout.addComponent(rightTaxonComponent);
        horizontalLayout.setComponentAlignment(rightTaxonComponent, new Alignment(48));

        leftLabel.setSizeUndefined();
        rightLabel.setSizeUndefined();

    }

    private void initConceptRComboBox() {
        conceptRComboBox.setImmediate(true);
        conceptRComboBox.setItemCaptionPropertyId("titleCache");
        try {
            conceptRComboBox.setContainerDataSource(presenter.loadTaxonRelationshipTypeContainer());
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(taxonRTypeIun == null) {
            conceptRComboBox.setInputPrompt(CHOOSE_TREL_TYPE);
        } else {
            conceptRComboBox.setValue(new RowId(taxonRTypeIun.getId()));
        }

        if(action == Action.Delete) {
            conceptRComboBox.setReadOnly(true);
            saveButton.setCaption("ok");
        }
    }

    private void initTaxonComponents() {
        // init left taxon layout
        leftTaxonLabel = new Label();
        leftTaxonTextField = new TextFieldNFix();
        leftTaxonComponent = buildTaxonVLayout(leftTaxonLabel, leftTaxonTextField);

        // init right taxon layout
        rightTaxonLabel = new Label();
        rightTaxonTextField = new TextFieldNFix();
        rightTaxonComponent = buildTaxonVLayout(rightTaxonLabel, rightTaxonTextField);
    }

    private DragAndDropWrapper intiDragDropWrapper(Component toTaxonLayout, final TextField toTaxonTextField) {

        DragAndDropWrapper toTaxonLayoutWrapper = new DragAndDropWrapper(toTaxonLayout);
        toTaxonLayoutWrapper.setImmediate(false);
        toTaxonLayoutWrapper.setWidth("-1px");
        toTaxonLayoutWrapper.setHeight("-1px");

        toTaxonLayoutWrapper.setDropHandler(new DropHandler() {

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

            @Override
            public void drop(DragAndDropEvent event) {
                // Wrapper for the object that is dragged
                Transferable t = event.getTransferable();

                // Make sure the drag source is a status composite tree table
                if (action != Action.Delete && t.getSourceComponent() instanceof TreeTable) {
                    TreeTable table = (TreeTable)t.getSourceComponent();
                    Hierarchical containerDataSource = table.getContainerDataSource();
                    if(containerDataSource instanceof LeafNodeTaxonContainer) {
                        LeafNodeTaxonContainer lntc = (LeafNodeTaxonContainer)containerDataSource;
                        Object sourceItemId = t.getData("itemId");
                        String toName = (String)lntc.getProperty(sourceItemId, LeafNodeTaxonContainer.NAME_ID).getValue();
                        toTaxonIun = new IdUuidName(sourceItemId,
                                lntc.getUuid(sourceItemId),
                                toName);
                        toTaxonTextField.setReadOnly(false);
                        toTaxonTextField.setValue(toName);
                        toTaxonTextField.setReadOnly(true);
                    }
                }
            }
        });
        return toTaxonLayoutWrapper;

    }


    private void addUIListeners() {
        addSaveButtonListener();
        addCancelButtonListener();

    }

    private void addSaveButtonListener() {
        saveButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {


                try {
                    conceptRComboBox.validate();
                    rightTaxonTextField.validate();
                    if(toTaxonIun == null) {
                        // FIXME: Not efficient - figure out a way
                        // of validation including the null check
                        throw new EmptyValueException("");
                    }
                } catch (EmptyValueException e) {
                    Notification notification = new Notification("Invalid input", "Neither Relationship Type nor To Taxon can be empty", Type.WARNING_MESSAGE);
                    notification.setDelayMsec(2000);
                    notification.show(Page.getCurrent());
                    return;
                }

                CdmVaadinUtilities.setEnabled(mainLayout, false, null);

                try {
                    CdmVaadinUtilities.exec(new CdmVaadinOperation(1000, cdmProgressComponent) {
                        @Override
                        public boolean execute() {
                            UUID relTypeUuid = presenter.getTaxonRTypeContainer().getUuid(conceptRComboBox.getValue());
                            switch(action) {
                            case Create:
                                setProgress("Saving New Concept Relationship");
                                presenter.createRelationship(fromTaxonIun.getUuid(), relTypeUuid, toTaxonIun.getUuid());
                                registerDelayedEvent(new CdmChangeEvent(Action.Create, Arrays.asList((Object)relTypeUuid), EditConceptRelationshipComposite.class));
                                break;
                            case Update:
                                setProgress("Update Concept Relationship");
                                presenter.updateRelationship(fromTaxonIun.getUuid(), relUuid, relTypeUuid, toTaxonIun.getUuid());
                                registerDelayedEvent(new CdmChangeEvent(Action.Update, Arrays.asList((Object)relTypeUuid), EditConceptRelationshipComposite.class));
                                break;
                            case Delete:
                                setProgress("Deleting Concept Relationship");
                                presenter.deleteRelationship(fromTaxonIun.getUuid(), relUuid);
                                registerDelayedEvent(new CdmChangeEvent(Action.Delete, Arrays.asList((Object)relTypeUuid), EditConceptRelationshipComposite.class));
                                break;
                            default:

                            }
                            return true;
                        }

                        @Override
                        public void postOpUIUpdate(boolean success) {
                            if(success) {
                                if(window != null) {
                                    UI.getCurrent().removeWindow(window);
                                }
                            } else {
                                CdmVaadinUtilities.setEnabled(mainLayout, true, null);
                            }
                        }
                    });
                } finally {
                    CdmVaadinSessionUtilities.getCurrentBasicEventService()
                    .fireBasicEvent(new BasicEvent(ConceptRelationshipComposite.UPDATE_END_ID, EditConceptRelationshipComposite.class), false);
                }
            }
        });
    }

    private void addCancelButtonListener() {
        cancelButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if(window != null) {
                    UI.getCurrent().removeWindow(window);
                }
                CdmVaadinSessionUtilities.getCurrentBasicEventService()
                .fireBasicEvent(new BasicEvent(ConceptRelationshipComposite.UPDATE_END_ID, EditConceptRelationshipComposite.class), true);
            }
        });
    }


    private static void showInDialog(String windowTitle,
            EditConceptRelationshipComposite ecrc) {
        //FIXME : hack for the moment to demonstrate checking of concept relationship rules
        if(ecrc.action.equals(Action.Create) && !ecrc.canCreateRelationship()) {
            Notification.show("Cannot create relationship for a taxon which is already congruent to another taxon", Type.WARNING_MESSAGE);
            return;
        }
        Window dialog = new Window(windowTitle);
        dialog.setModal(false);
        dialog.setClosable(false);
        dialog.setResizable(false);
        UI.getCurrent().addWindow(dialog);
        ecrc.setWindow(dialog);
        dialog.setContent(ecrc);
        CdmVaadinSessionUtilities.getCurrentBasicEventService()
        .fireBasicEvent(new BasicEvent(ConceptRelationshipComposite.UPDATE_START_ID, ConceptRelationshipComposite.class), false);
    }

    public static void showInDialog(String windowTitle,
            IdUuidName fromTaxonIun,
            IdUuidName taxonRTypeIun,
            IdUuidName toTaxonIun,
            Action action,
            Direction direction) {
        EditConceptRelationshipComposite ecrc = new EditConceptRelationshipComposite(fromTaxonIun, taxonRTypeIun, toTaxonIun,action, direction);
        showInDialog(windowTitle, ecrc);
    }

    public static void showInDialog(String windowTitle,
            IdUuidName fromTaxonIun,
            UUID relUuid,
            Action action,
            Direction direction) {
        EditConceptRelationshipComposite ecrc = new EditConceptRelationshipComposite(fromTaxonIun, relUuid, action, direction);
        showInDialog(windowTitle, ecrc);
    }


    private VerticalLayout buildTaxonVLayout(Label taxonDirLabel, TextField taxonTextField) {
        // common part: create layout
        VerticalLayout vLayout = new VerticalLayout();
        vLayout.setImmediate(false);
        vLayout.setWidth("-1px");
        vLayout.setHeight("-1px");
        vLayout.setMargin(false);
        vLayout.setSpacing(true);


        taxonDirLabel.setImmediate(false);
        taxonDirLabel.setWidth("-1px");
        taxonDirLabel.setHeight("-1px");

        vLayout.addComponent(taxonDirLabel);
        vLayout.setComponentAlignment(taxonDirLabel, new Alignment(48));


        taxonTextField.setImmediate(false);
        taxonTextField.setWidth("-1px");
        taxonTextField.setHeight("-1px");
        taxonTextField.setInvalidAllowed(false);
        taxonTextField.setRequired(true);
        taxonTextField.setReadOnly(true);
        vLayout.addComponent(taxonTextField);
        vLayout.setComponentAlignment(taxonTextField, new Alignment(48));

        return vLayout;
    }

    @AutoGenerated
    private VerticalLayout buildMainLayout() {
        // common part: create layout
        mainLayout = new VerticalLayout();
        mainLayout.setImmediate(false);
        mainLayout.setWidth("740px");
        mainLayout.setHeight("170px");
        mainLayout.setMargin(false);
        mainLayout.setSpacing(true);

        // top-level component properties
        setWidth("740px");
        setHeight("170px");

        // horizontalLayout
        horizontalLayout = buildHorizontalLayout();
        mainLayout.addComponent(horizontalLayout);
        mainLayout.setExpandRatio(horizontalLayout, 1.0f);
        mainLayout.setComponentAlignment(horizontalLayout, new Alignment(48));

        // saveCancelHLayout
        saveCancelHLayout = buildSaveCancelHLayout();
        mainLayout.addComponent(saveCancelHLayout);
        mainLayout.setComponentAlignment(saveCancelHLayout, new Alignment(48));

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
    private HorizontalLayout buildHorizontalLayout() {
        // common part: create layout
        horizontalLayout = new HorizontalLayout();
        horizontalLayout.setImmediate(false);
        horizontalLayout.setWidth("-1px");
        horizontalLayout.setHeight("-1px");
        horizontalLayout.setMargin(true);
        horizontalLayout.setSpacing(true);

        // leftLabel
        leftLabel = new Label();
        leftLabel.setImmediate(false);
        leftLabel.setWidth("-1px");
        leftLabel.setHeight("30px");
        leftLabel.setValue("Label");
        horizontalLayout.addComponent(leftLabel);
        horizontalLayout.setComponentAlignment(leftLabel, new Alignment(24));

        // typeVLayout
        typeVLayout = buildTypeVLayout();
        horizontalLayout.addComponent(typeVLayout);
        horizontalLayout.setExpandRatio(typeVLayout, 1.0f);
        horizontalLayout.setComponentAlignment(typeVLayout, new Alignment(48));

        // rightLabel
        rightLabel = new Label();
        rightLabel.setImmediate(false);
        rightLabel.setWidth("-1px");
        rightLabel.setHeight("30px");
        rightLabel.setValue("Label");
        horizontalLayout.addComponent(rightLabel);
        horizontalLayout.setComponentAlignment(rightLabel, new Alignment(24));

        return horizontalLayout;
    }

    @AutoGenerated
    private VerticalLayout buildTypeVLayout() {
        // common part: create layout
        typeVLayout = new VerticalLayout();
        typeVLayout.setImmediate(false);
        typeVLayout.setWidth("-1px");
        typeVLayout.setHeight("-1px");
        typeVLayout.setMargin(false);
        typeVLayout.setSpacing(true);

        // typeLabel
        typeLabel = new Label();
        typeLabel.setImmediate(false);
        typeLabel.setWidth("-1px");
        typeLabel.setHeight("-1px");
        typeLabel.setValue("Type");
        typeVLayout.addComponent(typeLabel);
        typeVLayout.setComponentAlignment(typeLabel, new Alignment(48));

        // conceptRComboBox
        conceptRComboBox = new ComboBox();
        conceptRComboBox.setImmediate(false);
        conceptRComboBox.setWidth("260px");
        conceptRComboBox.setHeight("-1px");
        conceptRComboBox.setRequired(true);
        typeVLayout.addComponent(conceptRComboBox);
        typeVLayout.setExpandRatio(conceptRComboBox, 1.0f);
        typeVLayout.setComponentAlignment(conceptRComboBox, new Alignment(48));

        return typeVLayout;
    }

    @AutoGenerated
    private HorizontalLayout buildSaveCancelHLayout() {
        // common part: create layout
        saveCancelHLayout = new HorizontalLayout();
        saveCancelHLayout.setImmediate(false);
        saveCancelHLayout.setWidth("-1px");
        saveCancelHLayout.setHeight("-1px");
        saveCancelHLayout.setMargin(false);
        saveCancelHLayout.setSpacing(true);

        // saveButton
        saveButton = new Button();
        saveButton.setCaption("save");
        saveButton.setImmediate(true);
        saveButton.setWidth("-1px");
        saveButton.setHeight("-1px");
        saveCancelHLayout.addComponent(saveButton);

        // cancelButton
        cancelButton = new Button();
        cancelButton.setCaption("cancel");
        cancelButton.setImmediate(true);
        cancelButton.setWidth("-1px");
        cancelButton.setHeight("-1px");
        saveCancelHLayout.addComponent(cancelButton);

        return saveCancelHLayout;
    }

}
