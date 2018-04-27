/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.name;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.log4j.Level;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.GrantedAuthority;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.model.agent.TeamOrPersonBase;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.vaadin.component.common.TeamOrPersonField;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEditorAction;
import eu.etaxonomy.cdm.vaadin.event.TaxonNameEditorAction;
import eu.etaxonomy.cdm.vaadin.model.name.TaxonNameDTO;
import eu.etaxonomy.cdm.vaadin.permission.AccessRestrictedView;
import eu.etaxonomy.cdm.vaadin.permission.CdmEditDeletePermissionTester;
import eu.etaxonomy.cdm.vaadin.util.TeamOrPersonBaseCaptionGenerator;
import eu.etaxonomy.cdm.vaadin.util.converter.SetToListConverter;
import eu.etaxonomy.vaadin.component.ReloadableLazyComboBox;
import eu.etaxonomy.vaadin.component.SwitchableTextField;
import eu.etaxonomy.vaadin.component.ToManyRelatedEntitiesComboboxSelect;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityCombobox;
import eu.etaxonomy.vaadin.event.EditorActionType;
import eu.etaxonomy.vaadin.mvp.AbstractCdmDTOPopupEditor;

/**
 * @author a.kohlbecker
 * @since May 22, 2017
 *
 */
@SpringComponent
@Scope("prototype")
public class TaxonNamePopupEditor extends AbstractCdmDTOPopupEditor<TaxonNameDTO, TaxonName, TaxonNameEditorPresenter> implements TaxonNamePopupEditorView, AccessRestrictedView {

    private static final long serialVersionUID = -7037436241474466359L;

    private final static int GRID_COLS = 4;

    private final static int GRID_ROWS = 13;

    private static final boolean HAS_BASIONYM_DEFAULT = false;

    private TextField genusOrUninomialField;

    private TextField infraGenericEpithetField;

    private TextField specificEpithetField;

    private TextField infraSpecificEpithetField;

    private SwitchableTextField fullTitleCacheFiled;

    private SwitchableTextField protectedNameCacheField;

    private ToOneRelatedEntityCombobox<Reference> nomReferenceCombobox;

    private TextField nomenclaturalReferenceDetail;

    private TeamOrPersonField exBasionymAuthorshipField;

    private TeamOrPersonField basionymAuthorshipField;

    private ToManyRelatedEntitiesComboboxSelect<TaxonName> basionymsComboboxSelect;

    private CheckBox basionymToggle;

    private CheckBox validationToggle;

    private ListSelect rankSelect;

    private TeamOrPersonField combinationAuthorshipField;

    private TeamOrPersonField exCombinationAuthorshipField;

    private EnumSet<TaxonNamePopupEditorMode> modesActive = EnumSet.noneOf(TaxonNamePopupEditorMode.class);

    private Boolean isInferredCombinationAuthorship = null;

    private Boolean isInferredBasionymAuthorship = null;

    private Boolean isInferredExBasionymAuthorship = null;

    private Map<AbstractField, Property.ValueChangeListener> authorshipUpdateListeners = new HashMap<>();

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

        rankSelect = new ListSelect("Rank");
        rankSelect.setNullSelectionAllowed(false);
        rankSelect.setRows(1);
        rankSelect.setWidth(100, Unit.PERCENTAGE);
        rankSelect.addValueChangeListener(e -> updateFieldVisibility());
        addField(rankSelect, "rank", 0, row, 1, row);
        grid.setComponentAlignment(rankSelect, Alignment.TOP_RIGHT);

        basionymToggle = new CheckBox("With basionym");
        basionymToggle.setValue(HAS_BASIONYM_DEFAULT);

        basionymToggle.setStyleName(getDefaultComponentStyles());
        grid.addComponent(basionymToggle, 2, row, 3, row);
        grid.setComponentAlignment(basionymToggle, Alignment.BOTTOM_LEFT);

        row++;
        validationToggle = new CheckBox("Validation");
        validationToggle.addValueChangeListener(e -> {
                boolean enable = e.getProperty().getValue() != null && (Boolean)e.getProperty().getValue();
                exCombinationAuthorshipField.setVisible(enable);
            });
        grid.addComponent(validationToggle, 2, row, 3, row);
        grid.setComponentAlignment(validationToggle, Alignment.BOTTOM_LEFT);

        row++;
        // fullTitleCache
        fullTitleCacheFiled = addSwitchableTextField("Full title cache", "fullTitleCache", "protectedFullTitleCache", 0, row, GRID_COLS-1, row);
        fullTitleCacheFiled.setWidth(100, Unit.PERCENTAGE);
        row++;
        protectedNameCacheField = addSwitchableTextField("Name cache", "nameCache", "protectedNameCache", 0, row, GRID_COLS-1, row);
        protectedNameCacheField.setWidth(100, Unit.PERCENTAGE);
        row++;
        genusOrUninomialField = addTextField("Genus or uninomial", "genusOrUninomial", 0, row, 1, row);
        genusOrUninomialField.setWidth(200, Unit.PIXELS);
        infraGenericEpithetField = addTextField("Infrageneric epithet", "infraGenericEpithet", 2, row, 3, row);
        infraGenericEpithetField.setWidth(200, Unit.PIXELS);
        row++;
        specificEpithetField = addTextField("Specific epithet", "specificEpithet", 0, row, 1, row);
        specificEpithetField.setWidth(200, Unit.PIXELS);
        infraSpecificEpithetField = addTextField("Infraspecific epithet", "infraSpecificEpithet", 2, row, 3, row);
        infraSpecificEpithetField.setWidth(200, Unit.PIXELS);

        row++;
        grid.addComponent(new Label("Hint: <i>Edit nomenclatural authors in the nomenclatural reference.</i>", ContentMode.HTML), 0, row, 3, row);

        row++;
        combinationAuthorshipField = new TeamOrPersonField("Combination author(s)", TeamOrPersonBaseCaptionGenerator.CacheType.NOMENCLATURAL_TITLE);
        combinationAuthorshipField.setWidth(100,  Unit.PERCENTAGE);
        addField(combinationAuthorshipField, "combinationAuthorship", 0, row, GRID_COLS-1, row);

        row++;
        nomReferenceCombobox = new ToOneRelatedEntityCombobox<Reference>("Nomenclatural reference", Reference.class);
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
                            nomReferenceCombobox,
                            this)
                );
            }
            });
        logger.setLevel(Level.DEBUG);
        nomReferenceCombobox.getSelect().addValueChangeListener(e -> logger.debug("nomReferenceCombobox value changed #1"));
        // nomReferenceCombobox.setWidth(300, Unit.PIXELS);
        nomReferenceCombobox.setWidth("100%");
        addField(nomReferenceCombobox, "nomenclaturalReference", 0, row, 2, row);
        nomenclaturalReferenceDetail = addTextField("Reference detail", "nomenclaturalMicroReference", 3, row, 3, row);
        nomenclaturalReferenceDetail.setWidth(100, Unit.PIXELS);

        row++;
        exCombinationAuthorshipField = new TeamOrPersonField("Ex-combination author(s)", TeamOrPersonBaseCaptionGenerator.CacheType.NOMENCLATURAL_TITLE);
        exCombinationAuthorshipField.setWidth(100,  Unit.PERCENTAGE);
        addField(exCombinationAuthorshipField, "exCombinationAuthorship", 0, row, GRID_COLS-1, row);

        // Basionym
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
            getViewEventBus().publish(this, new TaxonNameEditorAction(e.getAction(), beanUuid, lazyCombobox, this));
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



        setAdvancedModeEnabled(true);
        registerAdvancedModeComponents(fullTitleCacheFiled, protectedNameCacheField);

        registerAdvancedModeComponents(combinationAuthorshipField);
        registerAdvancedModeComponents(basionymAuthorshipField);
        registerAdvancedModeComponents(exBasionymAuthorshipField);

        registerAdvancedModeComponents(combinationAuthorshipField.getCachFields());
        registerAdvancedModeComponents(exCombinationAuthorshipField.getCachFields());
        registerAdvancedModeComponents(basionymAuthorshipField.getCachFields());
        registerAdvancedModeComponents(exBasionymAuthorshipField.getCachFields());

        setAdvancedMode(false);

    }

    protected TeamOrPersonBase inferBasiomynAuthors() {
        List<TaxonName> basionyms = basionymsComboboxSelect.getValue();
        if(!basionyms.isEmpty()){
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
        if(!basionyms.isEmpty()){
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

    @Override
    protected void afterItemDataSourceSet() {
        TaxonNameDTO taxonName = getBean();
        boolean showBasionymSection = taxonName.getBasionyms().size() > 0
                || taxonName.getBasionymAuthorship() != null
                || taxonName.getExBasionymAuthorship() != null;
        basionymToggle.setValue(showBasionymSection);
        basionymToggle.setReadOnly(showBasionymSection);
        basionymToggle.addValueChangeListener(e -> {
            updateAuthorshipFields();
            updateFieldVisibility();
        });

        boolean showExAuthors = taxonName.getExCombinationAuthorship() != null;
        validationToggle.setValue(showExAuthors);
        validationToggle.setReadOnly(showExAuthors);
        exCombinationAuthorshipField.setVisible(showExAuthors);

        if(isModeEnabled(TaxonNamePopupEditorMode.AUTOFILL_AUTHORSHIP_DATA)){
            updateAuthorshipFields();
        }
        if(isModeEnabled(TaxonNamePopupEditorMode.NOMENCLATURALREFERENCE_SECTION_EDITING_ONLY) && getBean().getNomenclaturalReference() != null) {
            nomReferenceCombobox.setCaption("Selection limited to nomenclatural reference and sections");
        }
        if(isModeEnabled(TaxonNamePopupEditorMode.REQUIRE_NOMENCLATURALREFERENCE)) {
            if(combinationAuthorshipField.getValue() == null){
                nomReferenceCombobox.setRequired(true);
            } else {
                combinationAuthorshipField.addValueChangeListener(e -> {
                    if(e.getProperty().getValue() == null){
                        nomReferenceCombobox.setRequired(true);
                    }
                });
            }
        }

    }

    /**
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

        updateFieldVisibility();

    }

//    /**
//     *
//     */
//    protected void updateAuthorshipFieldsVisibility() {
//        combinationAuthorshipField.setVisible(!isInferredCombinationAuthorship);
//        if(BooleanUtils.isTrue(basionymToggle.getValue())){
//            basionymAuthorshipField.setVisible(!isInferredBasionymAuthorship);
//            exBasionymAuthorshipField.setVisible(!isInferredExBasionymAuthorship);
//        }
//    }

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

        boolean isSpeciesOrBelow = !rank.isHigher(Rank.SPECIES());
        Boolean withBasionym = BooleanUtils.isTrue(basionymToggle.getValue());
        Boolean withValidation = BooleanUtils.isTrue(validationToggle.getValue());

        specificEpithetField.setVisible(isSpeciesOrBelow);
        infraSpecificEpithetField.setVisible(rank.isInfraSpecific());
        infraGenericEpithetField.setVisible(rank.isInfraGeneric());


        basionymAuthorshipField.setVisible(withBasionym != null && withBasionym);
        exBasionymAuthorshipField.setVisible(withBasionym);
        basionymsComboboxSelect.setVisible(withBasionym);

        combinationAuthorshipField.setVisible(isInferredCombinationAuthorship != null && !isInferredCombinationAuthorship);
        if(BooleanUtils.isTrue(basionymToggle.getValue())){
            basionymAuthorshipField.setVisible(isInferredBasionymAuthorship != null && !isInferredBasionymAuthorship);
            exBasionymAuthorshipField.setVisible(isInferredExBasionymAuthorship != null && !isInferredExBasionymAuthorship);
        }

//        if(taxonName != null){
//            if(modesActive.contains(TaxonNamePopupEditorMode.AUTOFILL_AUTHORSHIP_DATA)){
//            }
//        }

        infraSpecificEpithetField.setVisible(rank.isInfraSpecific());
        specificEpithetField.setVisible(isSpeciesOrBelow);
        infraGenericEpithetField.setVisible(rank.isInfraGenericButNotSpeciesGroup());
        genusOrUninomialField.setCaption(isSpeciesOrBelow ? "Genus" : "Uninomial");
        exCombinationAuthorshipField.setVisible(isSpeciesOrBelow && withValidation);
    }

    @Override
    public void cancel() {
        authorshipUpdateListeners.keySet().forEach(field -> field.removeValueChangeListener(authorshipUpdateListeners.get(field)));
        super.cancel();
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
    public ListSelect getRankSelect() {
        return rankSelect;
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




}
