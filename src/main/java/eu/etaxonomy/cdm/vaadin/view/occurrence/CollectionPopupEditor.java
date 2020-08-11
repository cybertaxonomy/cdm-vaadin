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

import eu.etaxonomy.cdm.api.utility.RoleProber;
import eu.etaxonomy.cdm.api.utility.UserHelper;
import eu.etaxonomy.cdm.model.agent.Institution;
import eu.etaxonomy.cdm.model.occurrence.Collection;
import eu.etaxonomy.cdm.service.UserHelperAccess;
import eu.etaxonomy.cdm.vaadin.event.CollectionEditorAction;
import eu.etaxonomy.cdm.vaadin.event.InstitutionEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityButtonUpdater;
import eu.etaxonomy.cdm.vaadin.ui.RegistrationUIDefaults;
import eu.etaxonomy.cdm.vaadin.util.CdmTitleCacheCaptionGenerator;
import eu.etaxonomy.cdm.vaadin.util.CollectionCaptionGenerator;
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
public class CollectionPopupEditor extends AbstractCdmPopupEditor<Collection, CollectionEditorPresenter> implements CollectionPopupEditorView {

    private static final long serialVersionUID = 2019724189877425882L;

    private static final int GRID_COLS = 3;

    private static final int GRID_ROWS = 4;

    TextField nameField;
    TextField codeField;
    TextField codeStandardField;
    TextField townOrLocationField;
    ToOneRelatedEntityCombobox<Collection> superCollectionCombobox;
    ToOneRelatedEntityCombobox<Institution> institutionCombobox;


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
        codeField.setWidth(100, Unit.PERCENTAGE);

        codeStandardField = addTextField("Code standard", "codeStandard", 1, row, 1, row);
        codeStandardField.setWidth(100, Unit.PERCENTAGE);

        townOrLocationField = addTextField("Town or location", "townOrLocation", 2, row, 2, row);
        townOrLocationField.setWidth(100, Unit.PERCENTAGE);

        row++;
        nameField = addTextField("Name", "name", 0, row, 2, row);
        nameField.setWidth(100, Unit.PERCENTAGE);

        row++;
        superCollectionCombobox = new ToOneRelatedEntityCombobox<Collection>("In collection", Collection.class);

        addField(superCollectionCombobox, "superCollection", 0, row, 2, row);

        superCollectionCombobox.getSelect().setCaptionGenerator(
                new CollectionCaptionGenerator()
                );
        superCollectionCombobox.setNestedButtonStateUpdater(new ToOneRelatedEntityButtonUpdater<Collection>(superCollectionCombobox));

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
                                superCollectionCombobox.getValue().getUuid(),
                                e.getButton(),
                                superCollectionCombobox,
                                this
                            )
                    );
                }
            });

        UserHelper userHelper = UserHelperAccess.userHelper();
        superCollectionCombobox.setVisible(RegistrationUIDefaults.COLLECTION_EDITOR_SUB_COLLECTION_VISIBILITY_RESTRICTION.stream().anyMatch( role -> userHelper.userIs(new RoleProber(role))));

        row++;
        institutionCombobox  = new ToOneRelatedEntityCombobox<Institution>("Institute", Institution.class);
        addField(institutionCombobox, "institute", 0, row, 2, row);

        institutionCombobox.getSelect().setCaptionGenerator(
                new CdmTitleCacheCaptionGenerator<Institution>()
                );
        institutionCombobox.setNestedButtonStateUpdater(new ToOneRelatedEntityButtonUpdater<Institution>(institutionCombobox));

        institutionCombobox.addClickListenerAddEntity( e -> getViewEventBus().publish(this,
                new InstitutionEditorAction(
                        EditorActionType.ADD,
                        null,
                        institutionCombobox,
                        this)
                ));
        institutionCombobox.addClickListenerEditEntity(e -> {
                if(institutionCombobox.getValue() != null){
                    getViewEventBus().publish(this,
                            new InstitutionEditorAction(
                                EditorActionType.EDIT,
                                institutionCombobox.getValue().getUuid(),
                                e.getButton(),
                                institutionCombobox,
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

    @Override
    public ToOneRelatedEntityCombobox<Institution> getInstitutionCombobox() {
        return institutionCombobox;
    }

}
