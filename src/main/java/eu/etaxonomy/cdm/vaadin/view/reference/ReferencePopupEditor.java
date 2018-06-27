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
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.security.core.GrantedAuthority;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferencePropertyDefinitions;
import eu.etaxonomy.cdm.model.reference.ReferencePropertyDefinitions.UnimplemetedCaseException;
import eu.etaxonomy.cdm.model.reference.ReferenceType;
import eu.etaxonomy.cdm.vaadin.component.TextFieldNFix;
import eu.etaxonomy.cdm.vaadin.component.common.TeamOrPersonField;
import eu.etaxonomy.cdm.vaadin.component.common.VerbatimTimePeriodField;
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

    private final static int GRID_COLS = 4; // 12 would fits for 2,3, and 4 Components per row

    private final static int GRID_ROWS = 10;

    private ListSelect typeSelect;

    private ToOneRelatedEntityCombobox<Reference> inReferenceCombobox;

    private TeamOrPersonField authorshipField;

    private EnumSet<ReferenceType> referenceTypes = EnumSet.allOf(ReferenceType.class);

    private static Map<String,String> propertyNameLabelMap = new HashMap<>();

    static {
        propertyNameLabelMap.put("inReference", "In reference");
        propertyNameLabelMap.put("inJournal", "In journal");
        propertyNameLabelMap.put("inSeries", "In series");
        propertyNameLabelMap.put("inBook", "In book");
    }

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
        VerbatimTimePeriodField timePeriodField = new VerbatimTimePeriodField("Date published");
        addField(timePeriodField, "datePublished", 0, row, 1, row);
        typeSelect = new ListSelect("Reference type");
        typeSelect.addItems(referenceTypes);
        typeSelect.setNullSelectionAllowed(false);
        typeSelect.setRows(1);
        typeSelect.addValueChangeListener(e -> updateFieldVisibility((ReferenceType)e.getProperty().getValue()));
        addField(typeSelect, "type", GRID_COLS - 1, row);
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
        addField(authorshipField, "authorship", 0, row, GRID_COLS -1, row);
        row++;

        inReferenceCombobox = new ToOneRelatedEntityCombobox<Reference>("In-reference", Reference.class);
        inReferenceCombobox.setWidth(100, Unit.PERCENTAGE);
        inReferenceCombobox.addClickListenerAddEntity(e -> getViewEventBus().publish(this,
                new ReferenceEditorAction(EditorActionType.ADD, e.getButton(), inReferenceCombobox, this)
                ));
        inReferenceCombobox.addClickListenerEditEntity(e -> {
            if(inReferenceCombobox.getValue() != null){
                getViewEventBus().publish(this,
                    new ReferenceEditorAction(
                            EditorActionType.EDIT,
                            inReferenceCombobox.getValue().getUuid(),
                            e.getButton(),
                            inReferenceCombobox,
                            this)
                );
            }
            });
        addField(inReferenceCombobox, "inReference", 0, row, GRID_COLS -1, row);
        row++;

        addTextField("Series", "seriesPart", 0, row).setWidth(100, Unit.PERCENTAGE);
        addTextField("Volume", "volume", 1, row).setWidth(100, Unit.PERCENTAGE);
        addTextField("Pages", "pages", 2, row).setWidth(100, Unit.PERCENTAGE);
        addTextField("Edition", "edition", 3, row).setWidth(100, Unit.PERCENTAGE);
        row++;

        addTextField("Place published", "placePublished", 0, row, 0, row).setWidth(100, Unit.PERCENTAGE);
        TextField publisherField = addTextField("Publisher", "publisher", 1, row, 1, row);
        publisherField.setWidth(100, Unit.PERCENTAGE);
        addTextField("Editor", "editor", 2, row).setWidth(100, Unit.PERCENTAGE);
        row++;

        addTextField("ISSN", "issn", 0, row).setWidth(100, Unit.PERCENTAGE);
        addTextField("ISBN", "isbn", 1, row).setWidth(100, Unit.PERCENTAGE);
        TextFieldNFix doiField = new TextFieldNFix("DOI");
        doiField.setConverter(new DoiConverter());
        doiField.setWidth(100, Unit.PERCENTAGE);
        addField(doiField, "doi", 2, row);
        TextFieldNFix uriField = new TextFieldNFix("Uri");
        uriField.setConverter(new UriConverter());
        uriField.setWidth(100, Unit.PERCENTAGE);
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

        try {
            Map<String, String> fieldPropertyDefinition = ReferencePropertyDefinitions.fieldPropertyDefinition(value);
            setAllFieldsVisible(false);
            for(String fieldName : fieldPropertyDefinition.keySet()){
                Field<?> field = getField(fieldName);
                if(field == null){
                    continue;
                }
                field.setVisible(true);
                String propertyName = fieldPropertyDefinition.get(fieldName);
                if(propertyName != fieldName){
                        field.setCaption(propertyNameLabelMap.get(propertyName));
                }
            }
        } catch (UnimplemetedCaseException e) {
            logger.error(e);
            // enable all fields
            setAllFieldsVisible(true);
            // fix inReference label
            getField("inReference").setCaption(propertyNameLabelMap.get("inReference"));
        }



        EnumSet<ReferenceType> hideNomTitle = EnumSet.of(ReferenceType.Article, ReferenceType.Section, ReferenceType.BookSection, ReferenceType.InProceedings, ReferenceType.PrintSeries);
        EnumSet<ReferenceType> hideTitle = EnumSet.of(ReferenceType.Section, ReferenceType.BookSection);
        getField("abbrevTitle").setVisible(!hideNomTitle.contains(value));
        getField("title").setVisible(!hideTitle.contains(value));

        return null;
    }

    protected void setAllFieldsVisible(boolean visible){
        GridLayout grid = (GridLayout)getFieldLayout();
        for(Component c : grid){
            if(AbstractField.class.isAssignableFrom(c.getClass())){
                c.setVisible(visible);
            }
        }
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
