/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.vaadin.mvp.AbstractPopupEditor;

/**
 * @author a.kohlbecker
 * @since Apr 4, 2017
 *
 */

@SpringComponent
@Scope("prototype")
public class ReferencePopupEditor extends AbstractPopupEditor<Reference, ReferenceEditorPresenter> implements ReferencePopupEditorView {

    private static final long serialVersionUID = -4347633563800758815L;

    private TextField titleField;

    private final static int GRID_COLS = 4;

    private final static int GRID_ROWS = 8;

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
        //addField(new Select("Reference type", new String[]{}), "type");
        addTextField("Reference cache", "titleCache", 0, 0, GRID_COLS-1, 0).setWidth(100, Unit.PERCENTAGE);
        addTextField("Abbrev. cache", "abbrevTitleCache", 0, 1, GRID_COLS-1, 1).setWidth(100, Unit.PERCENTAGE);
        titleField = addTextField("Title", "title", 0, 2, GRID_COLS-1, 2);
        titleField.setRequired(true);
        titleField.setWidth(100, Unit.PERCENTAGE);
        addTextField("NomenclaturalTitle", "abbrevTitle", 0, 3, GRID_COLS-1, 3).setWidth(100, Unit.PERCENTAGE);

        // addTextField("Author(s)", "authorship", 0, 4, 1, 4)).setRequired(true);
        addTextField("Editor", "editor", 2, 4, 3, 4).setWidth(100, Unit.PERCENTAGE);

        addTextField("Series", "seriesPart", 0, 5, 0, 5);
        addTextField("Volume", "volume", 1, 5, 1, 5);
        addTextField("Pages", "pages", 2, 5, 2, 5);

        addTextField("Place published", "placePublished", 0, 6, 1, 6).setWidth(100, Unit.PERCENTAGE);
        TextField publisherField = addTextField("Publisher", "publisher", 2, 6, 3, 6);
        publisherField.setRequired(true);
        publisherField.setWidth(100, Unit.PERCENTAGE);


        // TODO implement a TimePeriod component addDateField("DatePublished", "datePublished") // .setConverter(new JodaDateTimeConverter());
        addTextField("ISSN", "issn", 0, 7, 0, 7);
        addTextField("ISBN", "isbn", 1, 7, 1, 7);
        addTextField("DOI", "doi", 2, 7, 2, 7);
        addTextField("Uri", "uri", 3, 7, 3, 7);
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
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Autowired
    @Override
    protected void injectPresenter(ReferenceEditorPresenter presenter) {
        setPresenter(presenter);
    }



}
