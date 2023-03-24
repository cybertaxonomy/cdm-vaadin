/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.common;

import org.springframework.context.annotation.Scope;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.model.agent.Institution;
import eu.etaxonomy.cdm.vaadin.event.InstitutionEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityButtonUpdater;
import eu.etaxonomy.cdm.vaadin.model.common.InstitutionDTO;
import eu.etaxonomy.cdm.vaadin.util.CdmTitleCacheCaptionGenerator;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityCombobox;
import eu.etaxonomy.vaadin.event.EditorActionType;
import eu.etaxonomy.vaadin.mvp.AbstractCdmDTOPopupEditor;

/**
 * @author a.kohlbecker
 * @since Dec 21, 2017
 */
@SpringComponent
@Scope("prototype")
public class InstitutionPopupEditor
        extends AbstractCdmDTOPopupEditor<InstitutionDTO, Institution, InstitutionEditorPresenter,InstitutionPopupEditorView>
        implements InstitutionPopupEditorView {

    private static final long serialVersionUID = 2019724189877425882L;

    private static final int GRID_COLS = 3;

    private static final int GRID_ROWS = 3;

    private TextField codeField;
    private TextField codeStandardField;
    private TextField townOrLocationField;
    private ToOneRelatedEntityCombobox<Institution> partOfCombobox;

    public InstitutionPopupEditor() {
        super(new GridLayout(GRID_COLS, GRID_ROWS), InstitutionDTO.class);
    }

    @Override
    public String getWindowCaption() {
        return "Institution editor";
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

        GridLayout grid = (GridLayout)getFieldLayout();
        grid.setSizeFull();
        grid.setSpacing(true);

        int row = 0;
        codeField = addTextField("Code", "code", 0, row, 0, row);
        codeField.setWidth(100, Unit.PIXELS);

        townOrLocationField = addTextField("Name", "name", 1, row, 2, row);
        townOrLocationField.setWidth(200, Unit.PIXELS);

        row++;

        partOfCombobox = new ToOneRelatedEntityCombobox<>("Part of", Institution.class);


        partOfCombobox.setWidth(300, Unit.PIXELS);
        addField(partOfCombobox, "isPartOf", 0, row, 1, row);

        partOfCombobox.getSelect().setCaptionGenerator(
                new CdmTitleCacheCaptionGenerator<Institution>()
                );
        partOfCombobox.setNestedButtonStateUpdater(new ToOneRelatedEntityButtonUpdater<Institution>(partOfCombobox));

        partOfCombobox.addClickListenerAddEntity( e -> getViewEventBus().publish(this,
                new InstitutionEditorAction(
                        EditorActionType.ADD,
                        null,
                        partOfCombobox,
                        this)
                ));
        partOfCombobox.addClickListenerEditEntity(e -> {
                if(partOfCombobox.getValue() != null){
                    getViewEventBus().publish(this,
                            new InstitutionEditorAction(
                                EditorActionType.EDIT,
                                partOfCombobox.getValue().getUuid(),
                                e.getButton(),
                                partOfCombobox,
                                this
                            )
                    );
                }
            });
    }

    /* ------------------ View Interface methods -------------------- */

    @Override
    public ToOneRelatedEntityCombobox<Institution> getPartOfCombobox(){
        return partOfCombobox;
    }
}