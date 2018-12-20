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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
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

import eu.etaxonomy.cdm.api.utility.RoleProber;
import eu.etaxonomy.cdm.model.agent.Institution;
import eu.etaxonomy.cdm.model.common.AnnotationType;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferencePropertyDefinitions;
import eu.etaxonomy.cdm.model.reference.ReferencePropertyDefinitions.UnimplemetedCaseException;
import eu.etaxonomy.cdm.model.reference.ReferenceType;
import eu.etaxonomy.cdm.service.UserHelperAccess;
import eu.etaxonomy.cdm.vaadin.component.TextFieldNFix;
import eu.etaxonomy.cdm.vaadin.component.common.FilterableAnnotationsField;
import eu.etaxonomy.cdm.vaadin.component.common.TeamOrPersonField;
import eu.etaxonomy.cdm.vaadin.component.common.VerbatimTimePeriodField;
import eu.etaxonomy.cdm.vaadin.data.validator.InReferenceTypeValidator;
import eu.etaxonomy.cdm.vaadin.event.InstitutionEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEditorAction;
import eu.etaxonomy.cdm.vaadin.permission.AccessRestrictedView;
import eu.etaxonomy.cdm.vaadin.permission.RolesAndPermissions;
import eu.etaxonomy.cdm.vaadin.ui.RegistrationUIDefaults;
import eu.etaxonomy.cdm.vaadin.util.CdmTitleCacheCaptionGenerator;
import eu.etaxonomy.cdm.vaadin.util.TeamOrPersonBaseCaptionGenerator;
import eu.etaxonomy.cdm.vaadin.util.converter.DoiConverter;
import eu.etaxonomy.cdm.vaadin.util.converter.UriConverter;
import eu.etaxonomy.vaadin.component.SwitchableTextField;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityCombobox;
import eu.etaxonomy.vaadin.event.EditorActionType;
import eu.etaxonomy.vaadin.mvp.AbstractCdmPopupEditor;
import eu.etaxonomy.vaadin.util.PropertyIdPath;

/**
 * @author a.kohlbecker
 * @since Apr 4, 2017
 *
 */
@SpringComponent
@Scope("prototype")
public class ReferencePopupEditor extends AbstractCdmPopupEditor<Reference, ReferenceEditorPresenter> implements ReferencePopupEditorView, AccessRestrictedView {

    private static final long serialVersionUID = -4347633563800758815L;

    private static final Logger logger = Logger.getLogger(ReferencePopupEditor.class);

    private TextField titleField;

    private final static int GRID_COLS = 4; // 12 would fits for 2,3, and 4 Components per row

    private final static int GRID_ROWS = 14;

    private ListSelect typeSelect;

    private ToOneRelatedEntityCombobox<Reference> inReferenceCombobox;

    private TeamOrPersonField authorshipField;

    private ToOneRelatedEntityCombobox<Institution> institutionCombobox;
    private ToOneRelatedEntityCombobox<Institution> schoolCombobox;

    private FilterableAnnotationsField annotationsListField;

    private AnnotationType[] editableAnotationTypes = RegistrationUIDefaults.EDITABLE_ANOTATION_TYPES;

    private EnumSet<ReferenceType> referenceTypes = EnumSet.allOf(ReferenceType.class);

    private static Map<String,String> propertyNameLabelMap = new HashMap<>();

    private int variableGridStartRow;

    private int variableGridLastRow;

    /**
     * Used to record the fields from the variable grid part in their original order.
     */
    private LinkedHashMap<String, Field<?>> adaptiveFields = new LinkedHashMap<>();

    private VerbatimTimePeriodField datePublishedField;

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
        datePublishedField = new VerbatimTimePeriodField("Date published");
        addField(datePublishedField, "datePublished", 0, row, 1, row);
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

        institutionCombobox = new ToOneRelatedEntityCombobox<Institution>("Institution", Institution.class);
        institutionCombobox.getSelect().setCaptionGenerator(
                new CdmTitleCacheCaptionGenerator<Institution>()
                );
        institutionCombobox.setWidth(100, Unit.PERCENTAGE);
        institutionCombobox.addClickListenerAddEntity(e -> getViewEventBus().publish(this,
                new InstitutionEditorAction(EditorActionType.ADD, e.getButton(), institutionCombobox, this)
                ));
        institutionCombobox.addClickListenerEditEntity(e -> {
            if(institutionCombobox.getValue() != null){
                getViewEventBus().publish(this,
                    new InstitutionEditorAction(
                            EditorActionType.EDIT,
                            institutionCombobox.getValue().getUuid(),
                            e.getButton(),
                            institutionCombobox,
                            this)
                );
            }
         });

        schoolCombobox = new ToOneRelatedEntityCombobox<Institution>("School", Institution.class);
        schoolCombobox.getSelect().setCaptionGenerator(
                new CdmTitleCacheCaptionGenerator<Institution>()
                );
        schoolCombobox.addClickListenerAddEntity(e -> getViewEventBus().publish(this,
                new InstitutionEditorAction(EditorActionType.ADD, e.getButton(), schoolCombobox, this)
                ));
        schoolCombobox.addClickListenerEditEntity(e -> {
            if(schoolCombobox.getValue() != null){
                getViewEventBus().publish(this,
                    new InstitutionEditorAction(
                            EditorActionType.EDIT,
                            schoolCombobox.getValue().getUuid(),
                            e.getButton(),
                            schoolCombobox,
                            this)
                );
            }
         });
        row++;
        addField(institutionCombobox, "institution", 0, row, GRID_COLS -1, row);
        row++;
        addField(schoolCombobox, "school", 0, row, GRID_COLS -1, row);
        row++;

        variableGridStartRow = row;

        addTextField("Organization", "organization", 0, row).setWidth(100, Unit.PERCENTAGE);
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


        variableGridLastRow = row;

        row++;
        annotationsListField = new FilterableAnnotationsField("Editorial notes");
        annotationsListField.setWidth(100, Unit.PERCENTAGE);
        annotationsListField.setAnnotationTypesVisible(editableAnotationTypes);
        addField(annotationsListField, "annotations", 0, row, GRID_COLS-1, row);


//        titleField.setRequired(true);
//        publisherField.setRequired(true);

        setAdvancedModeEnabled(true);
        registerAdvancedModeComponents(titleCacheField, abbrevTitleCacheField);
        registerAdvancedModeComponents(authorshipField.getCachFields());
        setAdvancedMode(false);

    }



    @Override
    protected void afterItemDataSourceSet() {
        super.afterItemDataSourceSet();
        inReferenceCombobox.getSelect().addValidator(new InReferenceTypeValidator(typeSelect));
    }

    /**
     * @param value
     * @return
     */
    private Object updateFieldVisibility(ReferenceType referenceType) {

        GridLayout grid = (GridLayout)getFieldLayout();

        initAdaptiveFields();

        // clear the variable grid part
        for(int row = variableGridStartRow; row <= variableGridLastRow; row++){
            for(int x=0; x < grid.getColumns(); x++){
                grid.removeComponent(x, row);
            }
        }

        // set cursor at the beginning of the variable grid part
        grid.setCursorY(variableGridStartRow);
        grid.setCursorX(0);

        // place the fields which are required for the given referenceType in the variable grid part while
        // and retain the original order which is recorded in the adaptiveFields
        try {
            Map<String, String> fieldPropertyDefinition = ReferencePropertyDefinitions.fieldPropertyDefinition(referenceType);

            datePublishedField.setVisible(fieldPropertyDefinition.containsKey("datePublished"));
            authorshipField.setVisible(fieldPropertyDefinition.containsKey("authorship"));
            String inRefCaption = fieldPropertyDefinition.get("inReference");
            inReferenceCombobox.setVisible(inRefCaption != null);
            if(inRefCaption != null){
                inReferenceCombobox.setCaption(propertyNameLabelMap.get(inRefCaption));
            }
            getField("title").setVisible(fieldPropertyDefinition.containsKey("title"));

            EnumSet<ReferenceType> hideNomTitle = EnumSet.of(ReferenceType.Article, ReferenceType.Section, ReferenceType.BookSection, ReferenceType.InProceedings);
            getField("abbrevTitle").setVisible(!hideNomTitle.contains(referenceType));
            institutionCombobox.setVisible(fieldPropertyDefinition.containsKey("institution"));
            schoolCombobox.setVisible(fieldPropertyDefinition.containsKey("school"));

            for(String fieldName : adaptiveFields.keySet()){ // iterate over the LinkedHashMap to retain the original order of the fields
                if(fieldPropertyDefinition.containsKey(fieldName)){
                    Field<?> field = adaptiveFields.get(fieldName);
                    grid.addComponent(field);
                    String propertyName = fieldPropertyDefinition.get(fieldName);
                    if(propertyName != fieldName){
                        field.setCaption(propertyNameLabelMap.get(propertyName));
                    }
                }
            }
        } catch (UnimplemetedCaseException e) {
            logger.error(e);
            // enable all fields
            setAllFieldsVisible(true);
            // fix inReference label
            getField("inReference").setCaption(propertyNameLabelMap.get("inReference"));
        }



        return null;
    }

    /**
     * @param grid
     */
    protected void initAdaptiveFields() {
        GridLayout grid = (GridLayout)getFieldLayout();
        // initialize the map of adaptive fields
        if(adaptiveFields.isEmpty()){
            try{
                Map<String, String> fieldPropertyDefinition = ReferencePropertyDefinitions.fieldPropertyDefinition(null);
                Set<String> fieldNames = fieldPropertyDefinition.keySet();
                for(int row = variableGridStartRow; row <= variableGridLastRow; row++){
                    for(int x=0; x < grid.getColumns(); x++){
                        Component c = grid.getComponent(x, row);
                        logger.trace("initAdaptiveFields() - y: " + row + " x: " + x + "  component:" + (c != null ? c.getClass().getSimpleName(): "NULL"));
                        if(c != null && c instanceof Field){
                            Field<?> field = (Field<?>)c;
                            PropertyIdPath propertyIdPath = boundPropertyIdPath(field);
                            logger.trace("initAdaptiveFields() - " + field.getCaption() + " -> " + propertyIdPath);
                            if(propertyIdPath != null && fieldNames.contains(propertyIdPath.toString())){
                                adaptiveFields.put(propertyIdPath.toString(), field);
                            }
                        }
                    }
                }
            } catch (UnimplemetedCaseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected void setAllFieldsVisible(boolean visible){
        GridLayout grid = (GridLayout)getFieldLayout();
        for(Component c : grid){
            if(AbstractField.class.isAssignableFrom(c.getClass())){
                c.setVisible(visible);
            }
        }
    }


    @Override
    public void setAdvancedMode(boolean isAdvancedMode) {
        boolean isCurator = UserHelperAccess.userHelper().userIs(new RoleProber(RolesAndPermissions.ROLE_CURATION));
        boolean isAdmin = UserHelperAccess.userHelper().userIsAdmin();

        boolean canEditAllCaches = isAdmin || isCurator;
        super.setAdvancedMode(isAdvancedMode);
        if(!canEditAllCaches){
            advancedModeComponents.forEach(c -> c.setReadOnly(true));
            Arrays.asList(authorshipField.getCachFields()).forEach(c -> c.setReadOnly(false));
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
     * {@inheritDoc}            // TODO Auto-generated method stub
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

    @Override
    public FilterableAnnotationsField getAnnotationsField() {
        return annotationsListField;
    }

    public void withReferenceTypes(EnumSet<ReferenceType> types){
        this.referenceTypes = types;
        if(typeSelect != null){
            typeSelect.removeAllItems();
            typeSelect.addItems(referenceTypes);
        }
    }

    @Override
    public ToOneRelatedEntityCombobox<Institution> getInstitutionCombobox() {
        return institutionCombobox;
    }

    @Override
    public ToOneRelatedEntityCombobox<Institution> getSchoolCombobox() {
        return schoolCombobox;
    }


}
