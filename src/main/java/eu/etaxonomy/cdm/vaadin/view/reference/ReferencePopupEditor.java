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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.GrantedAuthority;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferenceType;
import eu.etaxonomy.cdm.vaadin.component.common.TeamOrPersonField;
import eu.etaxonomy.cdm.vaadin.component.common.TimePeriodField;
import eu.etaxonomy.cdm.vaadin.security.AccessRestrictedView;
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

    /**
     * @param layout
     * @param dtoType
     */
    public ReferencePopupEditor() {
        super(new GridLayout(GRID_COLS, GRID_ROWS), Reference.class);
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
        ListSelect typeSelect = new ListSelect("Reference type", Arrays.asList(ReferenceType.values()));
        typeSelect.setNullSelectionAllowed(false);
        typeSelect.setRows(1);
        addField(typeSelect, "type", 3, row);
        row++;
        addSwitchableTextField("Reference cache", "titleCache", "protectedTitleCache", 0, row, GRID_COLS-1, row).setWidth(100, Unit.PERCENTAGE);
        row++;
        addSwitchableTextField("Abbrev. cache", "abbrevTitleCache", "protectedAbbrevTitleCache", 0, row, GRID_COLS-1, row).setWidth(100, Unit.PERCENTAGE);
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
    @Autowired
    @Override
    protected void injectPresenter(ReferenceEditorPresenter presenter) {
        setPresenter(presenter);
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
