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
import org.springframework.security.core.GrantedAuthority;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.model.occurrence.Collection;
import eu.etaxonomy.cdm.vaadin.event.CollectionEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityButtonUpdater;
import eu.etaxonomy.cdm.vaadin.security.AccessRestrictedView;
import eu.etaxonomy.cdm.vaadin.util.CdmTitleCacheCaptionGenerator;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityCombobox;
import eu.etaxonomy.vaadin.event.EditorActionType;
import eu.etaxonomy.vaadin.mvp.AbstractCdmPopupEditor;

/**
 * @author a.kohlbecker
 * @since Dec 21, 2017
 *
 */
@SpringComponent
@Scope("prototype")
public class CollectionPopupEditor extends AbstractCdmPopupEditor<Collection, CollectionEditorPresenter> implements CollectionPopupEditorView, AccessRestrictedView {

    private static final long serialVersionUID = 2019724189877425882L;

    private static final int GRID_COLS = 3;

    private static final int GRID_ROWS = 3;

    TextField codeField;
    TextField codeStandardField;
    TextField townOrLocationField;
    ToOneRelatedEntityCombobox<Collection> superCollectionCombobox;


    /**
     * @param layout
     * @param dtoType
     */
    public CollectionPopupEditor() {
        super(new GridLayout(GRID_COLS, GRID_ROWS), Collection.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getWindowCaption() {
        return "Collection editor";
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public int getWindowWidth() {
        return 500;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void focusFirst() {
        codeField.focus();
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
    public java.util.Collection<java.util.Collection<GrantedAuthority>> allowedGrantedAuthorities() {
        return null;
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
        /*
        code : String
        codeStandard : String
        name : String
        townOrLocation : String
        institute : Institution
        superCollection : Collection
         */
        GridLayout grid = (GridLayout)getFieldLayout();
        grid.setSizeFull();
        grid.setSpacing(true);
//        grid.setColumnExpandRatio(0, 0.3f);
//        grid.setColumnExpandRatio(1, 0.3f);
//        grid.setColumnExpandRatio(2, 0.3f);
//        grid.setColumnExpandRatio(3, 0.0f);

        int row = 0;
        codeField = addTextField("Code", "code", 0, row, 0, row);
        codeField.setWidth(100, Unit.PIXELS);

        codeStandardField = addTextField("Code standard", "codeStandard", 1, row, 1, row);
        codeStandardField.setWidth(100, Unit.PIXELS);

        townOrLocationField = addTextField("Town or location", "townOrLocation", 2, row, 2, row);
        townOrLocationField.setWidth(100, Unit.PIXELS);

        row++;

        superCollectionCombobox = new ToOneRelatedEntityCombobox<Collection>("Super-collection", Collection.class);
        superCollectionCombobox.addClickListenerAddEntity(e -> getViewEventBus().publish(this,
                new CollectionEditorAction(EditorActionType.ADD, null, superCollectionCombobox, this)
                ));
        superCollectionCombobox.addClickListenerEditEntity(e -> {
            if(superCollectionCombobox.getValue() != null){
                getViewEventBus().publish(this,
                    new CollectionEditorAction(
                            EditorActionType.EDIT,
                            superCollectionCombobox.getValue().getId(),
                            superCollectionCombobox,
                            this)
                );
            }
            });
        superCollectionCombobox.setWidth(300, Unit.PIXELS);
        addField(superCollectionCombobox, "superCollection", 0, row, 1, row);

        superCollectionCombobox.getSelect().setCaptionGenerator(
                new CdmTitleCacheCaptionGenerator<Collection>()
                );
        superCollectionCombobox.getSelect().addValueChangeListener(
                new ToOneRelatedEntityButtonUpdater<Collection>(superCollectionCombobox)
                );

        superCollectionCombobox.addClickListenerAddEntity( e -> getViewEventBus().publish(this,
                new CollectionEditorAction(
                        EditorActionType.ADD,
                        null,
                        superCollectionCombobox,
                        this)
                ));
        superCollectionCombobox.addClickListenerEditEntity(e -> {
                if(superCollectionCombobox.getValue() != null){
                    getViewEventBus().publish(this,
                            new CollectionEditorAction(
                                EditorActionType.EDIT,
                                superCollectionCombobox.getValue().getId(),
                                superCollectionCombobox,
                                this
                            )
                    );
                }
            });
    }

    /* ------------------ View Interface methods -------------------- */
    @Override
    public ToOneRelatedEntityCombobox<Collection> getSuperCollectionCombobox() {
        return superCollectionCombobox;
    }

}
