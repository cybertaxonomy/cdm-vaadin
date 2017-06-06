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
import eu.etaxonomy.vaadin.component.SwitchableTextField;
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

    private final static int GRID_ROWS = 10;

    private TextField genusOrUninomialField;

    private TextField infraGenericEpithetField;

    private TextField specificEpithetField;

    private TextField infraSpecificEpithetField;

    private SwitchableTextField fullTitleCacheFiled;

    private SwitchableTextField protectedNameCacheField;

    private ToOneRelatedEntityCombobox<Reference> nomReferenceCombobox;

    private TextField nomenclaturalReferenceDetail;


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
        ListSelect rankSelect = selectFieldFactory.createListSelect("Rank", Rank.class, OrderHint.BY_ORDER_INDEX.asList(), "label");
        rankSelect.setNullSelectionAllowed(false);
        rankSelect.setRows(1);
        rankSelect.addValueChangeListener(e -> updateFieldVisibility((Rank)e.getProperty().getValue()));
        addField(rankSelect, "rank", 3, row);
        grid.setComponentAlignment(rankSelect, Alignment.TOP_RIGHT);
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
        TeamOrPersonField combinationAuthorshipField = new TeamOrPersonField("combination author(s)");
        combinationAuthorshipField.setWidth(100,  Unit.PERCENTAGE);
        addField(combinationAuthorshipField, "combinationAuthorship", 0, row, GRID_COLS-1, row);
        row++;
        TeamOrPersonField exCombinationAuthorshipField = new TeamOrPersonField("Ex-combination author(s)");
        exCombinationAuthorshipField.setWidth(100,  Unit.PERCENTAGE);
        addField(exCombinationAuthorshipField, "exCombinationAuthorship", 0, row, GRID_COLS-1, row);
        row++;
        TeamOrPersonField exBasionymAuthorshipField = new TeamOrPersonField("Ex-basionym author(s)");
        exBasionymAuthorshipField.setWidth(100,  Unit.PERCENTAGE);
        addField(exBasionymAuthorshipField, "exBasionymAuthorship", 0, row, GRID_COLS-1, row);
        row++;

        // nomenclaturalReference
        nomReferenceCombobox = new ToOneRelatedEntityCombobox<Reference>("Nomenclatural reference", Reference.class);
        nomReferenceCombobox.setWidth(100, Unit.PERCENTAGE);
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
        addField(nomReferenceCombobox, "nomenclaturalReference", 0, row, 3, row);
        row++;
        nomenclaturalReferenceDetail = addTextField("Reference detail", "nomenclaturalMicroReference", 0, row, 1, row);

        setAdvancedModeEnabled(true);
        registerAdvancedModeComponents(fullTitleCacheFiled, protectedNameCacheField);
        setAdvancedMode(false);

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


}
