/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.name;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.context.annotation.Scope;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.model.agent.TeamOrPersonBase;
import eu.etaxonomy.cdm.model.common.AnnotationType;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.common.RelationshipBase.Direction;
import eu.etaxonomy.cdm.model.name.NameRelationshipType;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.name.RankClass;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.vaadin.component.TextFieldNFix;
import eu.etaxonomy.cdm.vaadin.component.common.FilterableAnnotationsField;
import eu.etaxonomy.cdm.vaadin.component.common.TeamOrPersonField;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEditorAction;
import eu.etaxonomy.cdm.vaadin.event.TaxonNameEditorAction;
import eu.etaxonomy.cdm.vaadin.event.TaxonNameEditorActionStrRep;
import eu.etaxonomy.cdm.vaadin.model.name.NameRelationshipDTO;
import eu.etaxonomy.cdm.vaadin.model.name.TaxonNameDTO;
import eu.etaxonomy.cdm.vaadin.permission.CdmEditDeletePermissionTester;
import eu.etaxonomy.cdm.vaadin.ui.RegistrationUIDefaults;
import eu.etaxonomy.cdm.vaadin.util.TeamOrPersonBaseCaptionGenerator;
import eu.etaxonomy.cdm.vaadin.util.converter.SetToListConverter;
import eu.etaxonomy.vaadin.component.NameRelationField;
import eu.etaxonomy.vaadin.component.ReloadableLazyComboBox;
import eu.etaxonomy.vaadin.component.SwitchableTextField;
import eu.etaxonomy.vaadin.component.ToManyRelatedEntitiesComboboxSelect;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityCombobox;
import eu.etaxonomy.vaadin.component.WeaklyRelatedEntityCombobox;
import eu.etaxonomy.vaadin.event.EditorActionType;
import eu.etaxonomy.vaadin.mvp.AbstractCdmDTOPopupEditor;

/**
 * @author a.kohlbecker
 * @since May 22, 2017
 *
 */
@SpringComponent
@Scope("prototype")
public class TaxonNamePopupEditor extends AbstractCdmDTOPopupEditor<TaxonNameDTO, TaxonName, TaxonNameEditorPresenter>
    implements TaxonNamePopupEditorView{

    private static final long serialVersionUID = -7037436241474466359L;

    private final static int GRID_COLS = 4;

    private final static int GRID_ROWS = 19;

    private static final boolean HAS_BASIONYM_DEFAULT = false;

    private AbstractField<String> genusOrUninomialField;

    private AbstractField<String> infraGenericEpithetField;

    private AbstractField<String> specificEpithetField;

    private AbstractField<String> infraSpecificEpithetField;

    private SwitchableTextField fullTitleCacheFiled;

    private SwitchableTextField protectedNameCacheField;

    private ToOneRelatedEntityCombobox<Reference> nomReferenceCombobox;

    private TextField nomenclaturalReferenceDetail;

    private TeamOrPersonField exBasionymAuthorshipField;

    private TeamOrPersonField basionymAuthorshipField;

    private ToManyRelatedEntitiesComboboxSelect<TaxonName> basionymsComboboxSelect;

    private ToManyRelatedEntitiesComboboxSelect<TaxonName> replacedSynonymsComboboxSelect;

    private NameRelationField validationField;

    private NameRelationField orthographicVariantField;

    private CheckBox basionymToggle;

    private CheckBox replacedSynonymsToggle;

    private CheckBox validationToggle;

    private CheckBox orthographicVariantToggle;

    private NativeSelect rankSelect;

    private TeamOrPersonField combinationAuthorshipField;

    private TeamOrPersonField exCombinationAuthorshipField;

    private EnumSet<TaxonNamePopupEditorMode> modesActive = EnumSet.noneOf(TaxonNamePopupEditorMode.class);

    private Boolean isInferredCombinationAuthorship = null;

    private Boolean isInferredBasionymAuthorship = null;

    private Boolean isInferredExBasionymAuthorship = null;

    private Map<AbstractField, Property.ValueChangeListener> authorshipUpdateListeners = new HashMap<>();

    private Boolean isInferredExCombinationAuthorship;

    private int specificEpithetFieldRow;

    private ValueChangeListener updateFieldVisibilityListener = e -> updateFieldVisibility();

    private FilterableAnnotationsField annotationsListField;

    private AnnotationType[] editableAnotationTypes = RegistrationUIDefaults.EDITABLE_ANOTATION_TYPES;

    private int genusOrUninomialRow;

    private OrthographicCorrectionReferenceValidator orthographicCorrectionValidator;

    /**
     * By default  AnnotationType.EDITORIAL() is enabled.
     *
     * @return the editableAnotationTypes
     */
    @Override
    public AnnotationType[] getEditableAnotationTypes() {
        return editableAnotationTypes;
    }

    /**
     * By default  AnnotationType.EDITORIAL() is enabled.
     *
     *
     * @param editableAnotationTypes the editableAnotationTypes to set
     */
    @Override
    public void setEditableAnotationTypes(AnnotationType ... editableAnotationTypes) {
        this.editableAnotationTypes = editableAnotationTypes;
    }

    /**
     * @param layout
     * @param dtoType
     */
    public TaxonNamePopupEditor() {
        super(new GridLayout(GRID_COLS, GRID_ROWS), TaxonNameDTO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getWindowCaption() {
        return "Name editor";
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getWindowWidth() {
        return 800;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void focusFirst() {
        // titleField.focus();

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
    protected void initContent() {

        GridLayout grid = (GridLayout)getFieldLayout();
        grid.setSizeFull();
        grid.setHideEmptyRowsAndColumns(true);
        grid.setSpacing(true);
        grid.setColumnExpandRatio(0, 0.3f);
        grid.setColumnExpandRatio(1, 0.3f);
        grid.setColumnExpandRatio(2, 0.3f);
        grid.setColumnExpandRatio(3, 0.0f);

        /*
         - nameType: preset, needs to be set in the presenter for new names
         - appendedPhrase: -> TODO field
         - nomenclaturalMicroReference:  -> TODO field
         - nomenclaturalReference ->  field but disabled for REGISTRY
         - rank -> SelectField which determines the visiblity of the other fields

         - fullTitleCache + protectedFullTitleCache -> SwitchableTextField : ADVANCED_MODE
         - nameCache + protectedNameCache -> SwitchableTextField : ADVANCED_MODE

         - homotypicalGroup -> hidden
         - typeDesignations -> hidden
         - descriptions -> hidden
         - taxonBases -> hidden
         - registrations -> hidden

         - relationsFromThisName-> TODO implement later
         - relationsToThisName -> TODO implement later

         - genusOrUninomial -> textField
         - infraGenericEpithet  -> textField
         - specificEpithet  -> textField
         - infraSpecificEpithet  -> textField

         - authorshipCache + protectedAuthorshipCache -> SwitchableTextField : only ADVANCED_MODE and disabled for REGISTRY
         - basionymAuthorship -> field but disabled for REGISTRY, basionym is set as nameRelationShip
         - combinationAuthorship -> field but disabled for REGISTRY author team of the reference
         - exCombinationAuthorship -> textField
         - exBasionymAuthorship -> textField

         - status -> TODO field
         - monomHybrid -> TODO implement hybrids later
         - binomHybrid -> TODO implement hybrids later
         - trinomHybrid -> TODO implement hybrids later

         - hybridParentRelations -> TODO implement hybrids later
         - hybridChildRelations -> TODO implement hybrids later
         - hybridFormula -> TODO implement hybrids later

         ** ViralName attributes **
         - acronym

         ** BacterialName attributes **
         - subGenusAuthorship
         - nameApprobation
         - breed
         - publicationYear
         - originalPublicationYear
         - cultivarName
        */

        int row = 0;

        rankSelect = new NativeSelect("Rank");
        rankSelect.setNullSelectionAllowed(false);
        rankSelect.setWidth(100, Unit.PERCENTAGE);
        addField(rankSelect, "rank", 0, row, 1, row);
        grid.setComponentAlignment(rankSelect, Alignment.TOP_RIGHT);

        row++;
        basionymToggle = new CheckBox("With basionym");
        basionymToggle.setValue(HAS_BASIONYM_DEFAULT);
        basionymToggle.setStyleName(getDefaultComponentStyles());
        grid.addComponent(basionymToggle, 0, row);
        grid.setComponentAlignment(basionymToggle, Alignment.BOTTOM_LEFT);

        replacedSynonymsToggle = new CheckBox("With replaced synonym");
        grid.addComponent(replacedSynonymsToggle, 1, row);
        grid.setComponentAlignment(replacedSynonymsToggle, Alignment.BOTTOM_LEFT);

        validationToggle = new CheckBox("Validation");
        grid.addComponent(validationToggle, 2, row);
        grid.setComponentAlignment(validationToggle, Alignment.BOTTOM_LEFT);

        orthographicVariantToggle = new CheckBox("Orthographical variant");
        grid.addComponent(orthographicVariantToggle, 3, row);
        grid.setComponentAlignment(orthographicVariantToggle, Alignment.BOTTOM_LEFT);

        row++;
        // fullTitleCache
        fullTitleCacheFiled = addSwitchableTextField("Full title cache", "fullTitleCache", "protectedFullTitleCache", 0, row, GRID_COLS-1, row);
        fullTitleCacheFiled.setWidth(100, Unit.PERCENTAGE);
        row++;
        protectedNameCacheField = addSwitchableTextField("Name cache", "nameCache", "protectedNameCache", 0, row, GRID_COLS-1, row);
        protectedNameCacheField.setWidth(100, Unit.PERCENTAGE);
        row++;
        genusOrUninomialRow = row;
        genusOrUninomialField = addTextField("Genus or uninomial", "genusOrUninomial", 0, row, 1, row);
        genusOrUninomialField.setWidth(200, Unit.PIXELS);
        infraGenericEpithetField = addTextField("Infrageneric epithet", "infraGenericEpithet", 2, row, 3, row);
        infraGenericEpithetField.setWidth(200, Unit.PIXELS);
        row++;
        specificEpithetFieldRow = row;
        specificEpithetField = addTextField("Specific epithet", "specificEpithet", 0, row, 1, row);
        specificEpithetField.setWidth(200, Unit.PIXELS);
        infraSpecificEpithetField = addTextField("Infraspecific epithet", "infraSpecificEpithet", 2, row, 3, row);
        infraSpecificEpithetField.setWidth(200, Unit.PIXELS);

        row++;
        combinationAuthorshipField = new TeamOrPersonField("Combination author(s)", TeamOrPersonBaseCaptionGenerator.CacheType.NOMENCLATURAL_TITLE);
        combinationAuthorshipField.setWidth(100,  Unit.PERCENTAGE);
        addField(combinationAuthorshipField, "combinationAuthorship", 0, row, GRID_COLS-1, row);

        row++;
        nomReferenceCombobox = new ToOneRelatedEntityCombobox<Reference>("Nomenclatural reference", Reference.class);
        // nomReferenceCombobox.setImmediate(true);
        nomReferenceCombobox.addClickListenerAddEntity(e -> getViewEventBus().publish(
                this,
                new ReferenceEditorAction(EditorActionType.ADD, null, nomReferenceCombobox, this)
                ));
        nomReferenceCombobox.addClickListenerEditEntity(e -> {
            if(nomReferenceCombobox.getValue() != null){
                getViewEventBus().publish(this,
                    new ReferenceEditorAction(
                            EditorActionType.EDIT,
                            nomReferenceCombobox.getValue().getUuid(),
                            e.getButton(),
                            nomReferenceCombobox,
                            this)
                );
            }
            });


        // nomReferenceCombobox.getSelect().addValueChangeListener(e -> logger.debug("nomReferenceCombobox value changed #1"));
        // nomReferenceCombobox.setWidth(300, Unit.PIXELS);
        nomReferenceCombobox.setWidth("100%");
        addField(nomReferenceCombobox, "nomenclaturalReference", 0, row, 3, row);

        row++;
        nomenclaturalReferenceDetail = addTextField("Reference detail", "nomenclaturalMicroReference", 0, row, 2, row);
        nomenclaturalReferenceDetail.setWidth(100, Unit.PERCENTAGE);

        // --------------- Basionyms
        row++;
        basionymsComboboxSelect = new ToManyRelatedEntitiesComboboxSelect<TaxonName>(TaxonName.class, "Basionym");
        basionymsComboboxSelect.setConverter(new SetToListConverter<TaxonName>());
        addField(basionymsComboboxSelect, "basionyms", 0, row, 3, row);
        basionymsComboboxSelect.setWidth(100, Unit.PERCENTAGE);
        basionymsComboboxSelect.withEditButton(true);
        basionymsComboboxSelect.setEditPermissionTester(new CdmEditDeletePermissionTester());
        basionymsComboboxSelect.setEditActionListener(e -> {

            Object fieldValue = e.getSource().getValue();
            UUID beanUuid = null;
            if(fieldValue != null){
                beanUuid = ((CdmBase)fieldValue).getUuid();

            }
            ReloadableLazyComboBox<TaxonName>  lazyCombobox = (ReloadableLazyComboBox<TaxonName>) e.getSource();
            getViewEventBus().publish(this, new TaxonNameEditorAction(e.getAction(), beanUuid, null, lazyCombobox, this));
        });
        grid.setComponentAlignment(basionymsComboboxSelect, Alignment.TOP_RIGHT);

        row++;
        basionymAuthorshipField = new TeamOrPersonField("Basionym author(s)", TeamOrPersonBaseCaptionGenerator.CacheType.NOMENCLATURAL_TITLE);
        basionymAuthorshipField.setWidth(100,  Unit.PERCENTAGE);
        addField(basionymAuthorshipField, "basionymAuthorship", 0, row, GRID_COLS-1, row);
        row++;
        exBasionymAuthorshipField = new TeamOrPersonField("Ex-basionym author(s)", TeamOrPersonBaseCaptionGenerator.CacheType.NOMENCLATURAL_TITLE);
        exBasionymAuthorshipField.setWidth(100,  Unit.PERCENTAGE);
        addField(exBasionymAuthorshipField, "exBasionymAuthorship", 0, row, GRID_COLS-1, row);

        // --------------- ReplacedSynonyms
        row++;
        replacedSynonymsComboboxSelect = new ToManyRelatedEntitiesComboboxSelect<TaxonName>(TaxonName.class, "Replaced synonyms");
        replacedSynonymsComboboxSelect.setConverter(new SetToListConverter<TaxonName>());
        addField(replacedSynonymsComboboxSelect, "replacedSynonyms", 0, row, 3, row);
        replacedSynonymsComboboxSelect.setWidth(100, Unit.PERCENTAGE);
        replacedSynonymsComboboxSelect.withEditButton(true);
        replacedSynonymsComboboxSelect.setEditPermissionTester(new CdmEditDeletePermissionTester());
        replacedSynonymsComboboxSelect.setEditActionListener(e -> {

            Object fieldValue = e.getSource().getValue();
            UUID beanUuid = null;
            if(fieldValue != null){
                beanUuid = ((CdmBase)fieldValue).getUuid();

            }
            ReloadableLazyComboBox<TaxonName>  lazyCombobox = (ReloadableLazyComboBox<TaxonName>) e.getSource();
            getViewEventBus().publish(this, new TaxonNameEditorAction(e.getAction(), beanUuid, null, lazyCombobox, this));
        });
        grid.setComponentAlignment(replacedSynonymsComboboxSelect, Alignment.TOP_RIGHT);

        // --------------- Validation
        row++;
        validationField = new NameRelationField("Validation", "Validated name", Direction.relatedTo, NameRelationshipType.VALIDATED_BY_NAME());
        validationField.setWidth(100, Unit.PERCENTAGE);
        ToOneRelatedEntityCombobox<TaxonName> validatedNameComboBox = validationField.getRelatedNameComboBox();
        validatedNameComboBox.addClickListenerAddEntity(e -> getViewEventBus().publish(
                this,
                new TaxonNameEditorAction(EditorActionType.ADD, null, validatedNameComboBox, this)
                ));
        validatedNameComboBox.addClickListenerEditEntity(e -> {
            if(validatedNameComboBox.getValue() != null){
                getViewEventBus().publish(this,
                    new TaxonNameEditorAction(
                            EditorActionType.EDIT,
                            validatedNameComboBox.getValue().getUuid(),
                            e.getButton(),
                            validatedNameComboBox,
                            this)
                );
            }
        });
        ToOneRelatedEntityCombobox<Reference> validationCitatonComboBox = validationField.getCitatonComboBox();
        validationCitatonComboBox.addClickListenerAddEntity(e -> getViewEventBus().publish(
                // NOTE: adding new references is currently not allowed for name relations, see NameRelationField!!
                this,
                new ReferenceEditorAction(EditorActionType.ADD, null, validationCitatonComboBox, this)
                ));
        validationCitatonComboBox.addClickListenerEditEntity(e -> {
            if(validationCitatonComboBox.getValue() != null){
                getViewEventBus().publish(this,
                    new ReferenceEditorAction(
                            EditorActionType.EDIT,
                            validationCitatonComboBox.getValue().getUuid(),
                            e.getButton(),
                            validationCitatonComboBox,
                            this)
                );
            }
        });
        addField(validationField, "validationFor", 0, row, 3, row);
        grid.setComponentAlignment(validationField, Alignment.TOP_RIGHT);

        // ------- Orthographic Variant (Correction)
        row++;
        orthographicVariantField = new NameRelationField("Orthographical variant", "Name variant", Direction.relatedTo, NameRelationshipType.ORTHOGRAPHIC_VARIANT());
        orthographicVariantField.setWidth(100, Unit.PERCENTAGE);
        // corrected name must have same
        ToOneRelatedEntityCombobox<TaxonName> orthographicVariantCombobox = orthographicVariantField.getRelatedNameComboBox();
        orthographicVariantCombobox.addClickListenerAddEntity(e -> getViewEventBus().publish(
                this,
                new TaxonNameEditorAction(EditorActionType.ADD, null, orthographicVariantCombobox, this)
                ));
        orthographicVariantCombobox.addClickListenerEditEntity(e -> {
            if(orthographicVariantCombobox.getValue() != null){
                getViewEventBus().publish(this,
                    new TaxonNameEditorAction(
                            EditorActionType.EDIT,
                            orthographicVariantCombobox.getValue().getUuid(),
                            e.getButton(),
                            orthographicVariantCombobox,
                            this)
                );
            }
        });
        ToOneRelatedEntityCombobox<Reference> orthographicCorrectionCitatonComboBox = orthographicVariantField.getCitatonComboBox();
        orthographicCorrectionCitatonComboBox.addClickListenerAddEntity(e -> getViewEventBus().publish(
                // NOTE: adding new references is currently not allowed for name relations, see NameRelationField!!
                this,
                new ReferenceEditorAction(EditorActionType.ADD, null, orthographicCorrectionCitatonComboBox, this)
                ));
        orthographicCorrectionCitatonComboBox.addClickListenerEditEntity(e -> {
            if(orthographicCorrectionCitatonComboBox.getValue() != null){
                getViewEventBus().publish(this,
                    new ReferenceEditorAction(
                            EditorActionType.EDIT,
                            orthographicCorrectionCitatonComboBox.getValue().getUuid(),
                            e.getButton(),
                            orthographicCorrectionCitatonComboBox,
                            this)
                );
            }
        });
        addField(orthographicVariantField, "orthographicVariant", 0, row, 3, row);
        grid.setComponentAlignment(orthographicVariantField, Alignment.TOP_RIGHT);

        row++;
        exCombinationAuthorshipField = new TeamOrPersonField("Ex-combination author(s)", TeamOrPersonBaseCaptionGenerator.CacheType.NOMENCLATURAL_TITLE);
        exCombinationAuthorshipField.setWidth(100,  Unit.PERCENTAGE);
        addField(exCombinationAuthorshipField, "exCombinationAuthorship", 0, row, GRID_COLS-1, row);

        row++;
        annotationsListField = new FilterableAnnotationsField("Editorial notes");
        annotationsListField.setWidth(100, Unit.PERCENTAGE);
        annotationsListField.setAnnotationTypesVisible(editableAnotationTypes);
        addField(annotationsListField, "annotations", 0, row, GRID_COLS-1, row);

        // -----------------------------------------------------------------------------

        setAdvancedModeEnabled(true);
        registerAdvancedModeComponents(fullTitleCacheFiled, protectedNameCacheField);

        registerAdvancedModeComponents(combinationAuthorshipField);
        registerAdvancedModeComponents(basionymAuthorshipField);
        registerAdvancedModeComponents(exBasionymAuthorshipField);
        registerAdvancedModeComponents(exCombinationAuthorshipField);

        registerAdvancedModeComponents(combinationAuthorshipField.getCachFields());
        registerAdvancedModeComponents(exCombinationAuthorshipField.getCachFields());
        registerAdvancedModeComponents(basionymAuthorshipField.getCachFields());
        registerAdvancedModeComponents(exBasionymAuthorshipField.getCachFields());

        setAdvancedMode(false);

        //TODO remove below line once #7858 is fixed
        withDeleteButton(false);

    }

    protected TeamOrPersonBase inferBasiomynAuthors() {
        List<TaxonName> basionyms = basionymsComboboxSelect.getValue();
        if(!basionyms.isEmpty() && basionyms.get(0) != null){
            TaxonName basionym = basionyms.get(0);
            if(basionym.getCombinationAuthorship() != null){
                return basionym.getCombinationAuthorship();
            } else if(basionym.getNomenclaturalReference() != null){
                return basionym.getNomenclaturalReference().getAuthorship();
            }
        }
        return null;
    }

    protected TeamOrPersonBase inferExBasiomynAuthors() {
        List<TaxonName> basionyms = basionymsComboboxSelect.getValue();
        if(!basionyms.isEmpty() && basionyms.get(0) != null){
            TaxonName basionym = basionyms.get(0);
                return basionym.getExCombinationAuthorship();
        }
        return null;
    }

    protected TeamOrPersonBase inferCombinationAuthors() {
        Reference nomRef = nomReferenceCombobox.getValue();
        if(nomRef != null) {
            return nomRef.getAuthorship();
        }
        return null;
    }

    protected TeamOrPersonBase inferExCombinationAuthors() {
        NameRelationshipDTO nameRelationDTO = validationField.getValue();
        if(nameRelationDTO != null && nameRelationDTO.getOtherName() != null){
            TaxonName validatedName = nameRelationDTO.getOtherName();
            if(validatedName.getCombinationAuthorship() != null) {
                return validatedName.getCombinationAuthorship();
            } else if(validatedName.getNomenclaturalReference() != null){
                return validatedName.getNomenclaturalReference().getAuthorship();
            }
        }
        return null;
    }

    @Override
    protected void afterItemDataSourceSet() {


        rankSelect.addValueChangeListener(updateFieldVisibilityListener);
        basionymToggle.addValueChangeListener(e -> {
            updateAuthorshipFields();
        });
        validationToggle.addValueChangeListener(e -> {
            updateAuthorshipFields();
            });
        replacedSynonymsToggle.addValueChangeListener(e -> {
            boolean enable = e.getProperty().getValue() != null && (Boolean)e.getProperty().getValue();
            replacedSynonymsComboboxSelect.setVisible(enable);
        });
        orthographicVariantToggle.addValueChangeListener(e -> {
            boolean enable = e.getProperty().getValue() != null && (Boolean)e.getProperty().getValue();
            orthographicVariantField.setVisible(enable);
        });

        TaxonNameDTO taxonNameDTO = getBean();
        boolean showBasionymSection = taxonNameDTO.getBasionyms().size() > 0
                || taxonNameDTO.getBasionymAuthorship() != null
                || taxonNameDTO.getExBasionymAuthorship() != null;
        basionymToggle.setValue(showBasionymSection);
        basionymToggle.setReadOnly(showBasionymSection);

        boolean showReplacedSynonyms = taxonNameDTO.getReplacedSynonyms().size() > 0;
        replacedSynonymsToggle.setValue(showReplacedSynonyms);
        replacedSynonymsToggle.setReadOnly(showReplacedSynonyms);
        replacedSynonymsComboboxSelect.setVisible(showReplacedSynonyms);

        boolean showValidationSection = taxonNameDTO.getValidationFor() != null || taxonNameDTO.getExCombinationAuthorship() != null;
        validationToggle.setValue(showValidationSection);
        validationToggle.setReadOnly(showValidationSection);

        boolean showOrthographicCorrectionSection = taxonNameDTO.getOrthographicVariant() != null;
        orthographicVariantToggle.setValue(showOrthographicCorrectionSection);
        orthographicVariantToggle.setReadOnly(showOrthographicCorrectionSection);

        if(isModeEnabled(TaxonNamePopupEditorMode.AUTOFILL_AUTHORSHIP_DATA)){
            updateAuthorshipFields();
        }
        if(isModeEnabled(TaxonNamePopupEditorMode.NOMENCLATURALREFERENCE_SECTION_EDITING_ONLY) && getBean().getNomenclaturalReference() != null) {
            nomReferenceCombobox.setDescription("Selection limited to nomenclatural reference and parts of it.");
        }
        if(isModeEnabled(TaxonNamePopupEditorMode.REQUIRE_NOMENCLATURALREFERENCE)) {
            if(combinationAuthorshipField.getValue() == null){
                nomReferenceCombobox.setRequired(true);
                nomReferenceCombobox.setImmediate(true);
            } else {
                combinationAuthorshipField.addValueChangeListener(e -> {
                    if(e.getProperty().getValue() == null){
                        nomReferenceCombobox.setRequired(true);
                        nomReferenceCombobox.setImmediate(true);
                    }
                });
            }
        }
    }

    /**
     * Updates all authorship fields if the an authorship field is empty this method attempts to infer the
     * authors from the related nomenclatural reference or taxon name.
     * <p>
     * Finally the {@link #updateFieldVisibility()} is invoked.
     *
     * @param taxonName
     */
    @Override
    public void updateAuthorshipFields() {

        TaxonNameDTO taxonName = getBean();

        // ------------- CombinationAuthors
        isInferredCombinationAuthorship = updateAuthorshipFieldData(
                taxonName.getCombinationAuthorship(),
                inferCombinationAuthors(),
                combinationAuthorshipField,
                nomReferenceCombobox.getSelect(),
                isInferredCombinationAuthorship);


        // ------------- Basionym and ExBasionymAuthors
        if(BooleanUtils.isTrue(basionymToggle.getValue())){

            isInferredBasionymAuthorship = updateAuthorshipFieldData(
                    taxonName.getBasionymAuthorship(),
                    inferBasiomynAuthors(),
                    basionymAuthorshipField,
                    basionymsComboboxSelect,
                    isInferredBasionymAuthorship
                    );

            isInferredExBasionymAuthorship = updateAuthorshipFieldData(
                    taxonName.getExBasionymAuthorship(),
                    inferExBasiomynAuthors(),
                    exBasionymAuthorshipField,
                    basionymsComboboxSelect,
                    isInferredExBasionymAuthorship
                    );

        }

        // ------------- Validation and ExCombinationAuthors
        isInferredExCombinationAuthorship = updateAuthorshipFieldData(
                taxonName.getExCombinationAuthorship(),
                inferExCombinationAuthors(),
                exCombinationAuthorshipField,
                validationField.getRelatedNameComboBox(),
                isInferredExCombinationAuthorship
                );

        updateFieldVisibility();
    }


    /**
     *
     * @param authorship
     *    the value of the taxonName authorship field
     * @param inferredAuthors
     *    the value inferred from other fields which may be set as authorship to the taxon name
     * @param authorshipField
     *    the ui element to edit the taxonName authorship field
     * @param updateTriggerField
     * @param lastInferredAuthorshipState
     * @return
     */
    protected Boolean updateAuthorshipFieldData(TeamOrPersonBase<?> authorship, TeamOrPersonBase inferredAuthors,
            TeamOrPersonField authorshipField, AbstractField updateTriggerField,
            Boolean lastInferredAuthorshipState) {

        if(authorship == null){
            authorshipField.setValue(inferredAuthors);
            lastInferredAuthorshipState = true;
        } else {
            boolean authorshipMatch = authorship == inferredAuthors;
            if(lastInferredAuthorshipState == null){
                // initialization of authorshipState, this comes only into account when the editor is just being initialized
                lastInferredAuthorshipState = authorshipMatch;
            }
            if(!authorshipMatch && lastInferredAuthorshipState){
                // update the combinationAuthorshipField to follow changes of the nomenclatural reference in case it was autofilled before
                authorshipField.setValue(inferredAuthors);
                lastInferredAuthorshipState = true;
            }
        }

        if(updateTriggerField != null){
            // IMPORTANT!
            // this ChangeListener must be added at this very late point in the editor lifecycle so that it is called after
            // the ToOneRelatedEntityReloader which may have been added to the updateTriggerField in the presenters handleViewEntered() method.
            // Otherwise we risk multiple representation problems in the hibernate session
            if(!authorshipUpdateListeners.containsKey(updateTriggerField)){
                ValueChangeListener listener = e ->  {
                    logger.debug(" value changed #2");
                    updateAuthorshipFields();
                };
                updateTriggerField.addValueChangeListener(listener);
                authorshipUpdateListeners.put(updateTriggerField, listener);
            }
        }

        return lastInferredAuthorshipState;
    }

    /**
     * @param rank
     * @return
     */
    private void updateFieldVisibility() {

        // TODO use getField() instead and remove field references
        Rank rank = (Rank) rankSelect.getValue();

        boolean isSpeciesOrBelow = !rank.isHigher(Rank.SPECIES()) && !rank.getRankClass().equals(RankClass.Unknown);
        Boolean withBasionymSection = BooleanUtils.isTrue(basionymToggle.getValue());
        Boolean withValidationSection = BooleanUtils.isTrue(validationToggle.getValue());
        Boolean withOrthographicCorrectionSection = BooleanUtils.isTrue(orthographicVariantToggle.getValue());

        if(isModeEnabled(TaxonNamePopupEditorMode.VALIDATE_AGAINST_HIGHER_NAME_PART)){
            if(isSpeciesOrBelow) {
                if(TextField.class.isAssignableFrom(genusOrUninomialField.getClass())){
                    WeaklyRelatedEntityCombobox<TaxonName> combobox = new WeaklyRelatedEntityCombobox<TaxonName>("-> this caption will be replaced <-", TaxonName.class);
                    combobox.addClickListenerAddEntity(e -> getViewEventBus().publish(
                            this,
                            new TaxonNameEditorActionStrRep(
                                    EditorActionType.ADD,
                                    e.getButton(),
                                    combobox,
                                    this)
                        ));
                    combobox.addClickListenerEditEntity(e -> {
                        if(combobox.getValue() != null){
                            getViewEventBus().publish(this,
                                new TaxonNameEditorActionStrRep(
                                        EditorActionType.EDIT,
                                        combobox.getIdForValue(),
                                        e.getButton(),
                                        combobox,
                                        this)
                            );
                        }
                    });
                    genusOrUninomialField = replaceComponent("genusOrUninomial", genusOrUninomialField, combobox, 0, genusOrUninomialRow, 1, genusOrUninomialRow);
                }
            } else {
                if(WeaklyRelatedEntityCombobox.class.isAssignableFrom(genusOrUninomialField.getClass())) {
                    genusOrUninomialField = replaceComponent("genusOrUninomial", genusOrUninomialField, new TextFieldNFix(), 0, genusOrUninomialRow, 1, genusOrUninomialRow);
                }
            }
        }

        if(isModeEnabled(TaxonNamePopupEditorMode.VALIDATE_AGAINST_HIGHER_NAME_PART)){
            if(rank.isInfraSpecific()) {
                if(TextField.class.isAssignableFrom(specificEpithetField.getClass())) {
                    WeaklyRelatedEntityCombobox<TaxonName> combobox = new WeaklyRelatedEntityCombobox<TaxonName>("-> this caption will be replaced <-", TaxonName.class);
                    specificEpithetField = replaceComponent("specificEpithet", specificEpithetField, combobox, 0, specificEpithetFieldRow, 1, specificEpithetFieldRow);
                    combobox.addClickListenerAddEntity(e -> getViewEventBus().publish(
                            this,
                            new TaxonNameEditorActionStrRep(EditorActionType.ADD, e.getButton(), combobox, this)
                        ));
                    combobox.addClickListenerEditEntity(e -> {
                        if(combobox.getValue() != null){
                            getViewEventBus().publish(this,
                                new TaxonNameEditorActionStrRep(
                                        EditorActionType.EDIT,
                                        combobox.getIdForValue(),
                                        e.getButton(),
                                        combobox,
                                        this)
                            );
                        }
                    });
                }
            } else {
                if(WeaklyRelatedEntityCombobox.class.isAssignableFrom(specificEpithetField.getClass())) {
                    specificEpithetField = replaceComponent("specificEpithet", specificEpithetField, new TextFieldNFix(), 0, specificEpithetFieldRow, 1, specificEpithetFieldRow);
               }
            }
        }

        if(isModeEnabled(TaxonNamePopupEditorMode.ORTHOGRAPHIC_CORRECTION)){
            orthographicVariantField.setCaption("Orthographical correction");
            orthographicVariantField.getRelatedNameComboBox().setCaption("Incorrect name");
            orthographicVariantToggle.setCaption("Orthographical correction");
        } else {
            orthographicVariantField.setCaption("Orthographical variant");
            orthographicVariantField.getRelatedNameComboBox().setCaption("Name variant");
            orthographicVariantToggle.setCaption("Orthographical variant");
        }

        genusOrUninomialField.setRequired(true);
        specificEpithetField.setVisible(isSpeciesOrBelow);
        specificEpithetField.setRequired(isSpeciesOrBelow);
        infraSpecificEpithetField.setVisible(rank.isInfraSpecific());
        infraSpecificEpithetField.setRequired(rank.isInfraSpecific());
        infraGenericEpithetField.setVisible(rank.isInfraGeneric());
        infraGenericEpithetField.setRequired(rank.isInfraGeneric());

        basionymsComboboxSelect.setVisible(withBasionymSection);

        combinationAuthorshipField.setVisible(isInferredCombinationAuthorship != null && !isInferredCombinationAuthorship);
        basionymAuthorshipField.setVisible(withBasionymSection && isInferredBasionymAuthorship != null && !isInferredBasionymAuthorship);
        exBasionymAuthorshipField.setVisible(withBasionymSection && isInferredExBasionymAuthorship != null && !isInferredExBasionymAuthorship);

        validationField.setVisible(withValidationSection);
        exCombinationAuthorshipField.setVisible(withValidationSection && isInferredExCombinationAuthorship != null && !isInferredExCombinationAuthorship);

        orthographicVariantField.setVisible(withOrthographicCorrectionSection);
        if(withOrthographicCorrectionSection){
            orthographicCorrectionValidator = new OrthographicCorrectionReferenceValidator(nomReferenceCombobox);
            orthographicVariantField.addValidator(orthographicCorrectionValidator);
        } else {
            if(orthographicCorrectionValidator  != null){
                orthographicVariantField.removeValidator(orthographicCorrectionValidator);
                orthographicVariantField = null;
            }
        }

        infraSpecificEpithetField.setVisible(rank.isInfraSpecific());
        specificEpithetField.setVisible(isSpeciesOrBelow);
        infraGenericEpithetField.setVisible(rank.isInfraGenericButNotSpeciesGroup());
        genusOrUninomialField.setCaption(isSpeciesOrBelow ? "Genus" : "Uninomial");
    }


    @Override
    public void cancel() {
        authorshipUpdateListeners.keySet().forEach(field -> field.removeValueChangeListener(authorshipUpdateListeners.get(field)));
        rankSelect.removeValueChangeListener(updateFieldVisibilityListener);
        super.cancel();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ToOneRelatedEntityCombobox<Reference> getNomReferenceCombobox() {
        return nomReferenceCombobox;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ToManyRelatedEntitiesComboboxSelect<TaxonName> getBasionymComboboxSelect() {
        return basionymsComboboxSelect;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ToManyRelatedEntitiesComboboxSelect<TaxonName> getReplacedSynonymsComboboxSelect() {
        return replacedSynonymsComboboxSelect;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NativeSelect getRankSelect() {
        return rankSelect;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractField<String> getGenusOrUninomialField(){
        return genusOrUninomialField;
    }

    /**
     * @return the exBasionymAuthorshipField
     */
    @Override
    public TeamOrPersonField getExBasionymAuthorshipField() {
        return exBasionymAuthorshipField;
    }

    /**
     * @return the basionymAuthorshipField
     */
    @Override
    public TeamOrPersonField getBasionymAuthorshipField() {
        return basionymAuthorshipField;
    }

    /**
     * @return the combinationAuthorshipField
     */
    @Override
    public TeamOrPersonField getCombinationAuthorshipField() {
        return combinationAuthorshipField;
    }

    /**
     * @return the exCombinationAuthorshipField
     */
    @Override
    public TeamOrPersonField getExCombinationAuthorshipField() {
        return exCombinationAuthorshipField;
    }

    @Override
    public NameRelationField getValidationField(){
        return validationField;
    }

    @Override
    public NameRelationField getOrthographicVariantField() {
        return orthographicVariantField;
    }

    @Override
    public void enableMode(TaxonNamePopupEditorMode mode){
            modesActive.add(mode);
    }

    @Override
    public boolean isModeEnabled(TaxonNamePopupEditorMode mode){
        return modesActive.contains(mode);
    }

    @Override
    public void disableMode(TaxonNamePopupEditorMode mode){
        modesActive.remove(mode);
    }

    @Override
    public EnumSet<TaxonNamePopupEditorMode> getModesActive(){
        return modesActive;
    }

    @Override
    public CheckBox getBasionymToggle() {
        return basionymToggle;
    }

    @Override
    public FilterableAnnotationsField getAnnotationsField() {
        return annotationsListField;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReadOnly(boolean readOnly) {
        boolean basionymToggleReadonly = basionymToggle.isReadOnly();
        boolean validationToggleReadonly = validationToggle.isReadOnly();
        super.setReadOnly(readOnly);
        combinationAuthorshipField.setEditorReadOnly(readOnly);
        exCombinationAuthorshipField.setEditorReadOnly(readOnly);
        basionymAuthorshipField.setEditorReadOnly(readOnly);
        exBasionymAuthorshipField.setEditorReadOnly(readOnly);
        // preserve old readonly states if they were true
        if(basionymToggleReadonly){
            basionymToggle.setReadOnly(true);
        }
        if(validationToggleReadonly){
            validationToggle.setReadOnly(true);
        }
    }



    /**
     * @return the infraGenericEpithetField
     */
    @Override
    public AbstractField<String> getInfraGenericEpithetField() {
        return infraGenericEpithetField;
    }

    /**
     * @return the specificEpithetField
     */
    @Override
    public AbstractField<String> getSpecificEpithetField() {
        return specificEpithetField;
    }

    /**
     * @return the infraSpecificEpithetField
     */
    @Override
    public AbstractField<String> getInfraSpecificEpithetField() {
        return infraSpecificEpithetField;
    }

    @Override
    public CheckBox getOrthographicVariantToggle() {
        return orthographicVariantToggle;
    }


}
