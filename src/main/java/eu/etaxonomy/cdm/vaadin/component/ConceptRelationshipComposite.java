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
import java.util.UUID;

import org.json.JSONException;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;

import eu.etaxonomy.cdm.vaadin.container.IdUuidName;
import eu.etaxonomy.cdm.vaadin.jscomponent.D3ConceptRelationshipTree;
import eu.etaxonomy.cdm.vaadin.jscomponent.D3ConceptRelationshipTree.Direction;
import eu.etaxonomy.cdm.vaadin.presenter.ConceptRelationshipPresenter;
import eu.etaxonomy.cdm.vaadin.session.BasicEvent;
import eu.etaxonomy.cdm.vaadin.session.CdmChangeEvent;
import eu.etaxonomy.cdm.vaadin.session.CdmChangeEvent.Action;
import eu.etaxonomy.cdm.vaadin.session.IBasicEventListener;
import eu.etaxonomy.cdm.vaadin.session.ICdmChangeListener;
import eu.etaxonomy.cdm.vaadin.session.ISelectionListener;
import eu.etaxonomy.cdm.vaadin.session.SelectionEvent;
import eu.etaxonomy.cdm.vaadin.util.CdmVaadinSessionUtilities;
import eu.etaxonomy.cdm.vaadin.util.CdmVaadinUtilities;
import eu.etaxonomy.cdm.vaadin.view.ConceptRelationshipView;
import eu.etaxonomy.cdm.vaadin.view.IConceptRelationshipComponentListener;

/**
 * @author cmathew
 * @date 9 Apr 2015
 *
 */
public class ConceptRelationshipComposite extends CustomComponent implements ISelectionListener, ICdmChangeListener, IBasicEventListener {

    /*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

    @AutoGenerated
    private VerticalLayout mainLayout;
    @AutoGenerated
    private D3ConceptRelationshipTree d3ConceptRelationShipTree;
    @AutoGenerated
    private HorizontalLayout updateHorizontalLayout;
    @AutoGenerated
    private Button deleteButton;
    @AutoGenerated
    private Button editButton;
    @AutoGenerated
    private Button newButton;
    private final IConceptRelationshipComponentListener listener;

    private IdUuidName fromTaxonIun;
    private UUID selectedTaxonRelUuid;

    private ConceptRelationshipView view;

    public static final String CREATE_NEW_CR_TITLE = "Create New Concept Relationship";
    public static final String EDIT_CR_TITLE = "Edit Concept Relationship";
    public static final String DELETE_CR_TITLE = "Delete Concept Relationship";

    public static final String UPDATE_START_ID = "cr-update-start";
    public static final String UPDATE_END_ID = "cr-update-end";

    /**
     * The constructor should first build the main layout, set the
     * composition root and then do any custom initialization.
     *
     * The constructor will not be automatically regenerated by the
     * visual editor.
     */
    public ConceptRelationshipComposite() {
        buildMainLayout();
        setCompositionRoot(mainLayout);

        CdmVaadinSessionUtilities.getCurrentSelectionService().register(this);
        CdmVaadinSessionUtilities.getCurrentCdmDataChangeService().register(this);
        CdmVaadinSessionUtilities.getCurrentBasicEventService().register(this);
        listener = new ConceptRelationshipPresenter(d3ConceptRelationShipTree);

        addUIListeners();
        init();
    }

    public void setView(ConceptRelationshipView view) {
        this.view = view;
    }

    private void init() {
        enableControls(false);
        initD3ConceptRelationShipTree();
    }

    private void initD3ConceptRelationShipTree() {
        d3ConceptRelationShipTree.setImmediate(true);
        d3ConceptRelationShipTree.setConceptRelComposite(this);
    }

    private void addUIListeners() {
        addNewButtonListener();
        addEditButtonListener();
        addDeleteButtonListener();
    }

    private void addNewButtonListener() {
        newButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
               EditConceptRelationshipComposite.showInDialog(CREATE_NEW_CR_TITLE,
                       fromTaxonIun,
                       null,
                       null,
                       Action.Create,
                       view.getDirection());
               setSelectedTaxonRelUuid(null);
            }
        });
    }

    private void addEditButtonListener() {
        editButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                EditConceptRelationshipComposite.showInDialog(EDIT_CR_TITLE,
                        fromTaxonIun,
                        selectedTaxonRelUuid,
                        Action.Update,
                        view.getDirection());
                setSelectedTaxonRelUuid(null);
            }
        });
    }

    private void addDeleteButtonListener() {
        deleteButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                EditConceptRelationshipComposite.showInDialog(DELETE_CR_TITLE,
                        fromTaxonIun,
                        selectedTaxonRelUuid,
                        Action.Delete,
                        view.getDirection());
                setSelectedTaxonRelUuid(null);
            }
        });
    }


    private void refreshRelationshipView(Direction direction) {
        if(fromTaxonIun != null) {
            try {
                listener.refreshRelationshipView(fromTaxonIun, direction);
            } catch (JSONException e) {
                Notification.show("Error generating concept relation JSON",  e.getMessage(), Type.WARNING_MESSAGE);
            }
        }
    }

    public void setSelectedTaxonRelUuid(UUID selectedTaxonRelUuid) {
        this.selectedTaxonRelUuid = selectedTaxonRelUuid;
        updateControls();
    }

    private void enableControls(boolean enabled) {
        CdmVaadinUtilities.setEnabled(this, enabled, Arrays.asList(d3ConceptRelationShipTree));
    }

    private void updateControls() {
        enableControls(false);
        if(fromTaxonIun != null) {
            newButton.setEnabled(true);
        }
        if(selectedTaxonRelUuid != null) {
            editButton.setEnabled(true);
            deleteButton.setEnabled(true);
        }
    }

    /* (non-Javadoc)
     * @see eu.etaxonomy.cdm.vaadin.session.ISelectionListener#onSelect(eu.etaxonomy.cdm.vaadin.session.SelectionEvent)
     */
    @Override
    public void onSelect(SelectionEvent event) {
        if(event.getSourceType().equals(StatusComposite.class)) {
            fromTaxonIun = (IdUuidName)event.getSelectedObjects().get(0);
            if(fromTaxonIun != null) {
                view.setPrimaryStatusCompositeUuid((StatusComposite)event.getSource());
                refreshRelationshipView(view.getDirection());
                setSelectedTaxonRelUuid(null);
            } else {
                listener.clearRelationshipView();
            }
            updateControls();

        }
    }



    /* (non-Javadoc)
     * @see eu.etaxonomy.cdm.vaadin.session.ICdmChangeListener#onCreate(eu.etaxonomy.cdm.vaadin.session.CdmChangeEvent)
     */
    @Override
    public void onCreate(CdmChangeEvent event) {
        if(event.getSourceType().equals(EditConceptRelationshipComposite.class)) {
            setSelectedTaxonRelUuid(null);
            refreshRelationshipView(view.getDirection());

        }

    }


    /* (non-Javadoc)
     * @see eu.etaxonomy.cdm.vaadin.session.ICdmChangeListener#onUpdate(eu.etaxonomy.cdm.vaadin.session.CdmChangeEvent)
     */
    @Override
    public void onUpdate(CdmChangeEvent event) {
        if(event.getSourceType().equals(EditConceptRelationshipComposite.class)) {
            setSelectedTaxonRelUuid(null);
            refreshRelationshipView(view.getDirection());
        }
    }


    /* (non-Javadoc)
     * @see eu.etaxonomy.cdm.vaadin.session.ICdmChangeListener#onDelete(eu.etaxonomy.cdm.vaadin.session.CdmChangeEvent)
     */
    @Override
    public void onDelete(CdmChangeEvent event) {
        if(event.getSourceType().equals(EditConceptRelationshipComposite.class)) {
            setSelectedTaxonRelUuid(null);
            refreshRelationshipView(view.getDirection());
        }

    }


    /* (non-Javadoc)
     * @see eu.etaxonomy.cdm.vaadin.session.IBasicEventListener#onAction(eu.etaxonomy.cdm.vaadin.session.BasicEvent)
     */
    @Override
    public void onAction(BasicEvent event) {
        if(ConceptRelationshipComposite.UPDATE_START_ID.equals(event.getEventId())) {
            enableControls(false);
        }
        if(ConceptRelationshipComposite.UPDATE_END_ID.equals(event.getEventId())) {
            updateControls();
        }

    }

    @AutoGenerated
    private VerticalLayout buildMainLayout() {
        // common part: create layout
        mainLayout = new VerticalLayout();
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);

        // top-level component properties
        setWidth("100.0%");
        setHeight("100.0%");

        // updateHorizontalLayout
        updateHorizontalLayout = buildUpdateHorizontalLayout();
        mainLayout.addComponent(updateHorizontalLayout);
        mainLayout.setComponentAlignment(updateHorizontalLayout, new Alignment(20));

        // d3ConceptRelationShipTree
        d3ConceptRelationShipTree = new D3ConceptRelationshipTree();
        d3ConceptRelationShipTree.setImmediate(false);
        d3ConceptRelationShipTree.setWidth("100.0%");
        d3ConceptRelationShipTree.setHeight("-1px");
        mainLayout.addComponent(d3ConceptRelationShipTree);
        mainLayout.setExpandRatio(d3ConceptRelationShipTree, 1.0f);
        mainLayout.setComponentAlignment(d3ConceptRelationShipTree, new Alignment(20));

        return mainLayout;
    }

    @AutoGenerated
    private HorizontalLayout buildUpdateHorizontalLayout() {
        // common part: create layout
        updateHorizontalLayout = new HorizontalLayout();
        updateHorizontalLayout.setImmediate(false);
        updateHorizontalLayout.setWidth("-1px");
        updateHorizontalLayout.setHeight("-1px");
        updateHorizontalLayout.setMargin(true);
        updateHorizontalLayout.setSpacing(true);

        // newButton
        newButton = new Button();
        newButton.setCaption("new");
        newButton.setImmediate(true);
        newButton.setWidth("-1px");
        newButton.setHeight("-1px");
        updateHorizontalLayout.addComponent(newButton);

        // editButton
        editButton = new Button();
        editButton.setCaption("edit");
        editButton.setImmediate(true);
        editButton.setWidth("-1px");
        editButton.setHeight("-1px");
        updateHorizontalLayout.addComponent(editButton);

        // deleteButton
        deleteButton = new Button();
        deleteButton.setCaption("delete");
        deleteButton.setImmediate(true);
        deleteButton.setWidth("-1px");
        deleteButton.setHeight("-1px");
        updateHorizontalLayout.addComponent(deleteButton);

        return updateHorizontalLayout;
    }



}