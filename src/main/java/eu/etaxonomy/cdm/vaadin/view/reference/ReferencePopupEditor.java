/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.reference;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferenceType;
import eu.etaxonomy.cdm.vaadin.component.common.TeamOrPersonField;
import eu.etaxonomy.cdm.vaadin.component.common.TimePeriodField;
import eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEditorAction;
import eu.etaxonomy.cdm.vaadin.security.AccessRestrictedView;
import eu.etaxonomy.vaadin.component.SwitchableTextField;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityCombobox;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityListSelect;
import eu.etaxonomy.vaadin.mvp.AbstractCdmPopupEditor;

/**
 * @author a.kohlbecker
 * @since Apr 4, 2017
 *
 */
public class ReferencePopupEditor extends AbstractCdmPopupEditor<Reference, ReferenceEditorPresenter> implements ReferencePopupEditorView, AccessRestrictedView {

    private static final long serialVersionUID = -4347633563800758815L;

    private TextField titleField;

    private final static int GRID_COLS = 4;

    private final static int GRID_ROWS = 10;

    private ListSelect typeSelect;

    private ToOneRelatedEntityListSelect<Reference> inReferenceSelect;

    private ToOneRelatedEntityCombobox<Reference> inReferenceCombobox;

    /**
     * @param layout
     * @param dtoType
     */
    public ReferencePopupEditor() {
        super(new GridLayout(GRID_COLS, GRID_ROWS), Reference.class);
    }

    @Override
    protected void initContent() {
        GridLayout grid = (GridLayout)getFieldLayout();
        grid.setSpacing(true);
        grid.setMargin(true);

        /*
        "type",
        "uri",
        "abbrevTitleCache",
        "protectedAbbrevTitleCache",
        "nomenclaturallyRelevant",
        "authorship",
        "referenceAbstract",
        "title",
        "abbrevTitle",
        "editor",
        "volume",
        "pages",
        "edition",
        "isbn",
        "issn",
        "doi",
        "seriesPart",
        "datePublished",
        "publisher",
        "placePublished",
        "institution",
        "school",
        "organization",
        "inReference"
         */
        int row = 0;
        typeSelect = new ListSelect("Reference type", Arrays.asList(ReferenceType.values()));
        typeSelect.setNullSelectionAllowed(false);
        typeSelect.setRows(1);
        typeSelect.addValueChangeListener(e -> updateFieldVisibility((ReferenceType)e.getProperty().getValue()));
        addField(typeSelect, "type", 3, row);
        grid.setComponentAlignment(typeSelect, Alignment.TOP_RIGHT);
        row++;
        SwitchableTextField titleCacheField = addSwitchableTextField("Reference cache", "titleCache", "protectedTitleCache", 0, row, GRID_COLS-1, row);
        titleCacheField.setWidth(100, Unit.PERCENTAGE);
        row++;
        SwitchableTextField abbrevTitleCacheField = addSwitchableTextField("Abbrev. cache", "abbrevTitleCache", "protectedAbbrevTitleCache", 0, row, GRID_COLS-1, row);
        abbrevTitleCacheField.setWidth(100, Unit.PERCENTAGE);
        row++;
        titleField = addTextField("Title", "title", 0, row, GRID_COLS-1, row);
        titleField.setWidth(100, Unit.PERCENTAGE);
        row++;
        addTextField("NomenclaturalTitle", "abbrevTitle", 0, row, GRID_COLS-1, row).setWidth(100, Unit.PERCENTAGE);
        row++;
        TeamOrPersonField authorshipField = new TeamOrPersonField("Author(s)");
        authorshipField.setWidth(100,  Unit.PERCENTAGE);
        addField(authorshipField, "authorship", 0, row, 3, row);
        row++;
        addTextField("Series", "seriesPart", 0, row);
        addTextField("Volume", "volume", 1, row);
        addTextField("Pages", "pages", 2, row);
        addTextField("Editor", "editor", 3, row).setWidth(100, Unit.PERCENTAGE);
        row++;
        /*
        inReferenceSelect = new ToOneRelatedEntityListSelect<Reference>("In-reference", Reference.class, new BeanItemContainer<>(Reference.class));
        inReferenceSelect.setWidth(100, Unit.PERCENTAGE);
        inReferenceSelect.getSelect().setRows(1);
        inReferenceSelect.addClickListenerAddEntity(e -> getEventBus().publishEvent(
                new ReferenceEditorAction(AbstractEditorAction.Action.ADD, null, inReferenceSelect, this)
                ));
        inReferenceSelect.addClickListenerEditEntity(e -> {
            if(inReferenceSelect.getSelect().getValue() != null){
                getEventBus().publishEvent(
                    new ReferenceEditorAction(AbstractEditorAction.Action.EDIT, ((Reference)inReferenceSelect.getSelect().getValue()).getId(), inReferenceSelect, this)
                );
            }
            });
        addField(inReferenceSelect, "inReference", 0, row, 3, row);
        */
        inReferenceCombobox = new ToOneRelatedEntityCombobox<Reference>("In-reference", Reference.class);
        inReferenceCombobox.setWidth(100, Unit.PERCENTAGE);
        inReferenceCombobox.addClickListenerAddEntity(e -> getEventBus().publishEvent(
                new ReferenceEditorAction(AbstractEditorAction.Action.ADD, null, inReferenceSelect, this)
                ));
        inReferenceCombobox.addClickListenerEditEntity(e -> {
            if(inReferenceCombobox.getSelect().getValue() != null){
                getEventBus().publishEvent(
                    new ReferenceEditorAction(AbstractEditorAction.Action.EDIT, ((Reference)inReferenceSelect.getSelect().getValue()).getId(), inReferenceSelect, this)
                );
            }
            });
        addField(inReferenceCombobox, "inReference", 0, row, 3, row);
        row++;
        addTextField("Place published", "placePublished", 0, row, 1, row).setWidth(100, Unit.PERCENTAGE);
        TextField publisherField = addTextField("Publisher", "publisher", 2, row, 3, row);
        publisherField.setWidth(100, Unit.PERCENTAGE);
        TimePeriodField timePeriodField = new TimePeriodField("Date published");
        addField(timePeriodField, "datePublished");
        row++;
        addTextField("ISSN", "issn", 0, row);
        addTextField("ISBN", "isbn", 1, row);
        addTextField("DOI", "doi", 2, row);
        addTextField("Uri", "uri", 3, row);

//        titleField.setRequired(true);
//        publisherField.setRequired(true);

        setAdvancedModeEnabled(true);
        registerAdvancedModeComponents(titleCacheField, abbrevTitleCacheField);
        registerAdvancedModeComponents(authorshipField.getCachFields());
        setAdvancedMode(false);

    }

    /**
     * @param value
     * @return
     */
    private Object updateFieldVisibility(ReferenceType value) {
        getField("volume").setVisible(value.isVolumeReference());

        getField("placePublished").setVisible(value.isPublication());
        getField("publisher").setVisible(value.isPublication());

        getField("editor").setVisible(value.isPrintedUnit());
        getField("seriesPart").setVisible(value.isPrintedUnit());

        getField("inReference").setVisible(value.isPrintedUnit() || value.isSection());
        getField("pages").setVisible(value.isSection());

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getWindowCaption() {
        return "Reference editor";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDefaultComponentStyles() {
        return "tiny";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void focusFirst() {
        titleField.focus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isResizable() {
        return false;
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
    public ListSelect getTypeSelect() {
        return typeSelect;
    }

    @Override
    public ToOneRelatedEntityListSelect<Reference> getInReferenceSelect() {
        return inReferenceSelect;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ToOneRelatedEntityCombobox<Reference> getInReferenceCombobox() {
        return inReferenceCombobox;
    }

}
