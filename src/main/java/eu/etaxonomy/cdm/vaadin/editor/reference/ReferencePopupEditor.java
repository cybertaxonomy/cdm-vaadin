/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.editor.reference;

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

    /**
     * @param layout
     * @param dtoType
     */
    public ReferencePopupEditor() {
        super(new GridLayout(), Reference.class);
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
        addTextField("Reference cache", "titleCache");
        addTextField("Abbrev. cache", "abbrevTitleCache");
        titleField = addTextField("Title", "title");
        titleField.setRequired(true);
        addTextField("NomenclaturalTitle", "abbrevTitle");
        // addTextField("Author(s)", "authorship").setRequired(true);
        addTextField("Editor", "editor");
        addTextField("Series", "seriesPart");
        addTextField("Volume", "volume");
        addTextField("Pages", "pages");
        addTextField("Place published", "placePublished");
        addTextField("Publisher", "publisher").setRequired(true);
        // TODO implement a TimePeriod component addDateField("DatePublished", "datePublished") // .setConverter(new JodaDateTimeConverter());
        addTextField("ISSN", "issn");
        addTextField("ISBN", "isbn");
        addTextField("DOI", "doi");

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
