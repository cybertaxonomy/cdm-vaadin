/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.occurrence;

import org.springframework.context.annotation.Scope;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.model.occurrence.Collection;
import eu.etaxonomy.vaadin.mvp.AbstractCdmPopupEditor;

/**
 * @author a.kohlbecker
 * @since Dec 21, 2017
 */
@SpringComponent
@Scope("prototype")
public class CollectionPopupEditor
        extends AbstractCdmPopupEditor<Collection, CollectionEditorPresenter,CollectionPopupEditorView>
        implements CollectionPopupEditorView {

    private static final long serialVersionUID = 2019724189877425882L;

    private static final int GRID_COLS = 3;

    private static final int GRID_ROWS = 3;

    private TextField nameField;
    private TextField codeField;
    private TextField codeStandardField;
    private TextField townOrLocationField;

    public CollectionPopupEditor() {
        super(new GridLayout(GRID_COLS, GRID_ROWS), Collection.class);
    }

    @Override
    public String getWindowCaption() {
        return "Collection editor";
    }

    @Override
    public int getWindowWidth() {
        return 500;
    }

    @Override
    public void focusFirst() {
        codeField.focus();
    }

    @Override
    protected String getDefaultComponentStyles() {
        return "tiny";
    }

    @Override
    protected void initContent() {
        /*
        code : String
        codeStandard : String
        name : String
        townOrLocation : String
         */
        GridLayout grid = (GridLayout)getFieldLayout();
        grid.setSizeFull();
        grid.setSpacing(true);
//        grid.setColumnExpandRatio(0, 0.3f);
//        grid.setColumnExpandRatio(1, 0.3f);
//        grid.setColumnExpandRatio(2, 0.3f);
//        grid.setColumnExpandRatio(3, 0.0f);

        int row = 0;
        int col = 0;
        codeField = addTextField("Code", "code", col, row, col++, row);
        codeField.setWidth(100, Unit.PERCENTAGE);

        codeStandardField = addTextField("Code standard", "codeStandard", col, row, col+1, row);
        codeStandardField.setWidth(100, Unit.PERCENTAGE);

        row++;
        col = 0;
        townOrLocationField = addTextField("Town or location", "townOrLocation", col, row, col+1, row);
        townOrLocationField.setWidth(100, Unit.PERCENTAGE);

        row++;
        col = 0;
        nameField = addTextField("Name", "name", col, row, col + 2, row);
        nameField.setWidth(100, Unit.PERCENTAGE);
    }
}