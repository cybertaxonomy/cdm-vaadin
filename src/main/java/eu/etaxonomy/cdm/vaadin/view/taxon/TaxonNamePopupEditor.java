/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.taxon;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.GrantedAuthority;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.name.TaxonNameBase;
import eu.etaxonomy.cdm.persistence.query.OrderHint;
import eu.etaxonomy.cdm.vaadin.component.SelectFieldFactory;
import eu.etaxonomy.cdm.vaadin.component.common.TeamOrPersonField;
import eu.etaxonomy.cdm.vaadin.security.AccessRestrictedView;
import eu.etaxonomy.vaadin.mvp.AbstractCdmPopupEditor;

/**
 * @author a.kohlbecker
 * @since May 22, 2017
 *
 */
@SpringComponent
@Scope("prototype")
public class TaxonNamePopupEditor extends AbstractCdmPopupEditor<TaxonNameBase, TaxonNameEditorPresenter> implements TaxonNamePopupEditorView, AccessRestrictedView {

    private static final long serialVersionUID = -7037436241474466359L;

    @Autowired
    private SelectFieldFactory selectFieldFactory;

    private final static int GRID_COLS = 4;

    private final static int GRID_ROWS = 7;

    private TextField titleField;

    private TextField genusOrUninomialField;

    private TextField infraGenericEpithetField;

    private TextField specificEpithetField;

    private TextField infraSpecificEpithetField;



    /**
     * @param layout
     * @param dtoType
     */
    public TaxonNamePopupEditor() {
        super(new GridLayout(GRID_COLS, GRID_ROWS), TaxonNameBase.class);
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
        grid.setMargin(true);

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
        rankSelect.addValidator(e -> updateFieldVisibility());
        addField(rankSelect, "rank", 3, row);
        grid.setComponentAlignment(rankSelect, Alignment.TOP_RIGHT);
        row++;
        // fullTitleCache
        addSwitchableTextField("Full title cache", "fullTitleCache", "protectedFullTitleCache", 0, row, GRID_COLS-1, row).setWidth(100, Unit.PERCENTAGE);
        row++;
        addSwitchableTextField("Name cache", "nameCache", "protectedNameCache", 0, row, GRID_COLS-1, row).setWidth(100, Unit.PERCENTAGE);
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
        TeamOrPersonField exCombinationAuthorshipField = new TeamOrPersonField("Ex-combination author(s)");
        exCombinationAuthorshipField.setWidth(100,  Unit.PERCENTAGE);
        addField(exCombinationAuthorshipField, "exCombinationAuthorship", 0, row, GRID_COLS-1, row);
        row++;
        TeamOrPersonField exBasionymAuthorshipField = new TeamOrPersonField("Ex-basionym author(s)");
        exBasionymAuthorshipField.setWidth(100,  Unit.PERCENTAGE);
        addField(exBasionymAuthorshipField, "exBasionymAuthorship", 0, row, GRID_COLS-1, row);
        row++;



    }

    /**
     * @return
     */
    private Object updateFieldVisibility() {
        // TODO Auto-generated method stub
        // TODO change label of
        // - genusOrUninomialField
        return null;
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

}
