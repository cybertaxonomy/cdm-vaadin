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

import org.springframework.security.core.GrantedAuthority;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.persistence.query.OrderHint;
import eu.etaxonomy.cdm.vaadin.component.common.TeamOrPersonField;
import eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEditorAction;
import eu.etaxonomy.cdm.vaadin.security.AccessRestrictedView;
import eu.etaxonomy.cdm.vaadin.util.converter.SetToListConverter;
import eu.etaxonomy.vaadin.component.SwitchableTextField;
import eu.etaxonomy.vaadin.component.ToManyRelatedEntitiesComboboxSelect;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityCombobox;
import eu.etaxonomy.vaadin.mvp.AbstractCdmPopupEditor;

/**
 * @author a.kohlbecker
 * @since May 22, 2017
 *
 */
public class TaxonNamePopupEditor extends AbstractCdmPopupEditor<TaxonName, TaxonNameEditorPresenter> implements TaxonNamePopupEditorView, AccessRestrictedView {

    private static final long serialVersionUID = -7037436241474466359L;

    private final static int GRID_COLS = 4;

    private final static int GRID_ROWS = 12;

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

    private ToManyRelatedEntitiesComboboxSelect<TaxonName> basionymCombobox;

    private CheckBox basionymToggle;

    private ListSelect rankSelect;

    private TeamOrPersonField combinationAuthorshipField;

    private TeamOrPersonField exCombinationAuthorshipField;


    /**
     * @param layout
     * @param dtoType
     */
    public TaxonNamePopupEditor() {
        super(new GridLayout(GRID_COLS, GRID_ROWS), TaxonName.class);
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

        rankSelect = selectFieldFactory.createListSelect("Rank", Rank.class, OrderHint.BY_ORDER_INDEX.asList(), "label");
        rankSelect.setNullSelectionAllowed(false);
        rankSelect.setRows(1);
        rankSelect.setWidth(100, Unit.PERCENTAGE);
        rankSelect.addValueChangeListener(e -> updateFieldVisibility((Rank)e.getProperty().getValue()));
        addField(rankSelect, "rank", 0, row, 1, row);
        grid.setComponentAlignment(rankSelect, Alignment.TOP_RIGHT);

        basionymToggle = new CheckBox("With basionym");
        basionymToggle.setValue(HAS_BASIONYM_DEFAULT);
        basionymToggle.addValueChangeListener(e -> {
                boolean enable = e.getProperty().getValue() != null && (Boolean)e.getProperty().getValue();
                enableBasionymFields(enable);
            });
        basionymToggle.setStyleName(getDefaultComponentStyles());
        grid.addComponent(basionymToggle, 2, row, 3, row);
        grid.setComponentAlignment(basionymToggle, Alignment.BOTTOM_LEFT);
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
        combinationAuthorshipField = new TeamOrPersonField("combination author(s)");
        combinationAuthorshipField.setWidth(100,  Unit.PERCENTAGE);
        addField(combinationAuthorshipField, "combinationAuthorship", 0, row, GRID_COLS-1, row);
        row++;
        exCombinationAuthorshipField = new TeamOrPersonField("Ex-combination author(s)");
        exCombinationAuthorshipField.setWidth(100,  Unit.PERCENTAGE);
        addField(exCombinationAuthorshipField, "exCombinationAuthorship", 0, row, GRID_COLS-1, row);

        // nomenclaturalReference
        row++;
        nomReferenceCombobox = new ToOneRelatedEntityCombobox<Reference>("Nomenclatural reference", Reference.class);
        nomReferenceCombobox.addClickListenerAddEntity(e -> getEventBus().publishEvent(
                new ReferenceEditorAction(AbstractEditorAction.Action.ADD, null, nomReferenceCombobox, this)
                ));
        nomReferenceCombobox.addClickListenerEditEntity(e -> {
            if(nomReferenceCombobox.getValue() != null){
                getEventBus().publishEvent(
                    new ReferenceEditorAction(
                            AbstractEditorAction.Action.EDIT,
                            nomReferenceCombobox.getValue().getId(),
                            nomReferenceCombobox,
                            this)
                );
            }
            });
        nomReferenceCombobox.setWidth(300, Unit.PIXELS);
        addField(nomReferenceCombobox, "nomenclaturalReference", 0, row, 2, row);
        nomenclaturalReferenceDetail = addTextField("Reference detail", "nomenclaturalMicroReference", 3, row, 3, row);
        nomenclaturalReferenceDetail.setWidth(100, Unit.PIXELS);

        // Basionym
        row++;
        basionymCombobox = new ToManyRelatedEntitiesComboboxSelect<TaxonName>(TaxonName.class, "Basionym");
        /**
        basionymCombobox.newAdd(e -> getEventBus().publishEvent(
                new TaxonNameEditorAction(AbstractEditorAction.Action.ADD, null, basionymCombobox, this)
                ));
        basionymCombobox.addClickListenerEditEntity(e -> {
            if(basionymCombobox.getValue() != null){
                getEventBus().publishEvent(
                    new TaxonNameEditorAction(
                            AbstractEditorAction.Action.EDIT,
                            basionymCombobox.getValue().getId(),
                            basionymCombobox,
                            this)
                );
            }
            });
         **/
        basionymCombobox.setConverter(new SetToListConverter<TaxonName>());
        addField(basionymCombobox, "basionyms", 0, row, 3, row);
        basionymCombobox.setWidth(100, Unit.PERCENTAGE);
        grid.setComponentAlignment(basionymCombobox, Alignment.TOP_RIGHT);
        row++;
        basionymAuthorshipField = new TeamOrPersonField("Basionym author(s)");
        basionymAuthorshipField.setWidth(100,  Unit.PERCENTAGE);
        addField(basionymAuthorshipField, "basionymAuthorship", 0, row, GRID_COLS-1, row);
        row++;
        exBasionymAuthorshipField = new TeamOrPersonField("Ex-basionym author(s)");
        exBasionymAuthorshipField.setWidth(100,  Unit.PERCENTAGE);
        addField(exBasionymAuthorshipField, "exBasionymAuthorship", 0, row, GRID_COLS-1, row);



        setAdvancedModeEnabled(true);
        enableBasionymFields(HAS_BASIONYM_DEFAULT);
        registerAdvancedModeComponents(fullTitleCacheFiled, protectedNameCacheField);
        registerAdvancedModeComponents(basionymAuthorshipField.getCachFields());
        registerAdvancedModeComponents(exBasionymAuthorshipField.getCachFields());
        registerAdvancedModeComponents(combinationAuthorshipField.getCachFields());
        registerAdvancedModeComponents(exCombinationAuthorshipField.getCachFields());
        setAdvancedMode(false);

    }

    /**
     * @param value
     * @return
     */
    private void enableBasionymFields(boolean enable) {
        basionymAuthorshipField.setVisible(enable);
        exBasionymAuthorshipField.setVisible(enable);
        basionymCombobox.setVisible(enable);
    }

    /**
     * @param rank
     * @return
     */
    private void updateFieldVisibility(Rank rank) {
        boolean isSpeciesOrBelow = !rank.isHigher(Rank.SPECIES());
        // TODO use getField() instead and remove field references
        infraSpecificEpithetField.setVisible(rank.isInfraSpecific());
        specificEpithetField.setVisible(isSpeciesOrBelow);
        infraGenericEpithetField.setVisible(rank.isInfraGenericButNotSpeciesGroup());
        genusOrUninomialField.setCaption(isSpeciesOrBelow ? "Genus" : "Uninomial");
    }

    @Override
    protected void afterItemDataSourceSet() {
        TaxonName taxonName = getBean();
        boolean showBasionymSection = taxonName.getBasionyms().size() > 0
                || taxonName.getBasionymAuthorship() != null
                || taxonName.getExBasionymAuthorship() != null;
        basionymToggle.setValue(showBasionymSection);

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
    public ToManyRelatedEntitiesComboboxSelect<TaxonName> getBasionymCombobox() {
        return basionymCombobox;
    }


}
