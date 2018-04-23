/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.reference;

import java.util.Collection;
import java.util.EnumSet;

import org.springframework.context.annotation.Scope;
import org.springframework.security.core.GrantedAuthority;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferenceType;
import eu.etaxonomy.cdm.vaadin.component.TextFieldNFix;
import eu.etaxonomy.cdm.vaadin.component.common.TeamOrPersonField;
import eu.etaxonomy.cdm.vaadin.component.common.TimePeriodField;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEditorAction;
import eu.etaxonomy.cdm.vaadin.permission.AccessRestrictedView;
import eu.etaxonomy.cdm.vaadin.util.TeamOrPersonBaseCaptionGenerator;
import eu.etaxonomy.cdm.vaadin.util.converter.DoiConverter;
import eu.etaxonomy.cdm.vaadin.util.converter.UriConverter;
import eu.etaxonomy.vaadin.component.SwitchableTextField;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityCombobox;
import eu.etaxonomy.vaadin.event.EditorActionType;
import eu.etaxonomy.vaadin.mvp.AbstractCdmPopupEditor;

/**
 * @author a.kohlbecker
 * @since Apr 4, 2017
 *
 */
@SpringComponent
@Scope("prototype")
public class ReferencePopupEditor extends AbstractCdmPopupEditor<Reference, ReferenceEditorPresenter> implements ReferencePopupEditorView, AccessRestrictedView {

    private static final long serialVersionUID = -4347633563800758815L;

    private TextField titleField;

    private final static int GRID_COLS = 4;

    private final static int GRID_ROWS = 10;

    private ListSelect typeSelect;

    private ToOneRelatedEntityCombobox<Reference> inReferenceCombobox;

    private TeamOrPersonField authorshipField;

    private EnumSet<ReferenceType> referenceTypes = EnumSet.allOf(ReferenceType.class);

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
        typeSelect = new ListSelect("Reference type");
        typeSelect.addItems(referenceTypes);
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
        addTextField("Nomenclatural title", "abbrevTitle", 0, row, GRID_COLS-1, row).setWidth(100, Unit.PERCENTAGE);
        row++;
        authorshipField = new TeamOrPersonField("Author(s)", TeamOrPersonBaseCaptionGenerator.CacheType.BIBLIOGRAPHIC_TITLE);
        authorshipField.setWidth(100,  Unit.PERCENTAGE);
        addField(authorshipField, "authorship", 0, row, 3, row);
        row++;
        addTextField("Series", "seriesPart", 0, row);
        addTextField("Volume", "volume", 1, row);
        addTextField("Pages", "pages", 2, row);
        addTextField("Editor", "editor", 3, row).setWidth(100, Unit.PERCENTAGE);
        row++;

        inReferenceCombobox = new ToOneRelatedEntityCombobox<Reference>("In-reference", Reference.class);
        inReferenceCombobox.setWidth(100, Unit.PERCENTAGE);
        inReferenceCombobox.addClickListenerAddEntity(e -> getViewEventBus().publish(this,
                new ReferenceEditorAction(EditorActionType.ADD, null, inReferenceCombobox, this)
                ));
        inReferenceCombobox.addClickListenerEditEntity(e -> {
            if(inReferenceCombobox.getValue() != null){
                getViewEventBus().publish(this,
                    new ReferenceEditorAction(
                            EditorActionType.EDIT,
                            inReferenceCombobox.getValue().getUuid(),
                            inReferenceCombobox,
                            this)
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
        TextFieldNFix doiField = new TextFieldNFix("DOI");
        doiField.setConverter(new DoiConverter());
        addField(doiField, "doi", 2, row);
        TextFieldNFix uriField = new TextFieldNFix("Uri");
        uriField.setConverter(new UriConverter());
        addField(uriField, "uri", 3, row);

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

        EnumSet<ReferenceType> hideNomTitle = EnumSet.of(ReferenceType.Article, ReferenceType.Section, ReferenceType.BookSection, ReferenceType.InProceedings, ReferenceType.PrintSeries);
        EnumSet<ReferenceType> hideTitle = EnumSet.of(ReferenceType.Section, ReferenceType.BookSection);
        getField("abbrevTitle").setVisible(!hideNomTitle.contains(value));
        getField("title").setVisible(!hideTitle.contains(value));

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

    /**
     * {@inheritDoc}
     */
    @Override
    public ToOneRelatedEntityCombobox<Reference> getInReferenceCombobox() {
        return inReferenceCombobox;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TeamOrPersonField getAuthorshipField() {
        return authorshipField;
    }

    public void withReferenceTypes(EnumSet<ReferenceType> types){
        this.referenceTypes = types;
        if(typeSelect != null){
            typeSelect.removeAllItems();
            typeSelect.addItems(referenceTypes);
        }
    }


}
