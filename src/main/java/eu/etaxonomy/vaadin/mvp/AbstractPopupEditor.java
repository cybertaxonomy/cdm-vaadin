/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.mvp;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.vaadin.ui.view.DoneWithPopupEvent;
import eu.etaxonomy.vaadin.ui.view.DoneWithPopupEvent.Reason;

/**
 * @author a.kohlbecker
 * @since Apr 5, 2017
 *
 */
public abstract class AbstractPopupEditor<DTO extends Object, P extends AbstractEditorPresenter<DTO>>
    extends AbstractPopupView<P> {

    private BeanFieldGroup<DTO> fieldGroup;

    private VerticalLayout mainLayout;

    private Layout fieldLayout;

    private HorizontalLayout buttonLayout;

    private Button save;

    private Button cancel;

    public AbstractPopupEditor(Layout layout, Class<DTO> dtoType) {

        setWidthUndefined();

        mainLayout = new VerticalLayout();
        mainLayout.setWidthUndefined();

        fieldGroup = new BeanFieldGroup<>(dtoType);
        fieldGroup.addCommitHandler(new SaveHandler());

        setCompositionRoot(mainLayout);

        fieldLayout = layout;
        fieldLayout.setWidthUndefined();
        if(fieldLayout instanceof AbstractOrderedLayout){
            ((AbstractOrderedLayout)fieldLayout).setSpacing(true);
        }

        buttonLayout = new HorizontalLayout();
        buttonLayout.setStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        buttonLayout.setWidth(100, Unit.PERCENTAGE);
        buttonLayout.setSpacing(true);

        save = new Button("Save", FontAwesome.SAVE);
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.addClickListener(e -> onSaveClicked());

        cancel = new Button("Cancel", FontAwesome.TRASH);
        cancel.addClickListener(e -> onCancelClicked());

        buttonLayout.addComponents(save, cancel);
        buttonLayout.setExpandRatio(save, 1);
        buttonLayout.setComponentAlignment(save, Alignment.TOP_RIGHT);
        buttonLayout.setComponentAlignment(cancel, Alignment.TOP_RIGHT);

        mainLayout.addComponents(fieldLayout, buttonLayout);
    }

    protected VerticalLayout getMainLayout() {
        return mainLayout;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        save.setVisible(!readOnly);
        cancel.setCaption(readOnly ? "Close" : "Cancel");
    }

    // ------------------------ event handler ------------------------ //

    private class SaveHandler implements CommitHandler {
        private static final long serialVersionUID = 2047223089707080659L;

        @Override
        public void preCommit(CommitEvent commitEvent) throws CommitException {
        }

        @Override
        public void postCommit(CommitEvent commitEvent) throws CommitException {
            try {
                // notify the presenter to persist the bean
                eventBus.publishEvent(new EditorSaveEvent(commitEvent));

                // notify the NavigationManagerBean to close the window and to dispose the view
                eventBus.publishEvent(new DoneWithPopupEvent(AbstractPopupEditor.this, Reason.SAVE));
            } catch (Exception e) {
                throw new CommitException("Failed to store data to backend", e);
            }
        }
    }

    protected void addCommitHandler(CommitHandler commitHandler) {
        fieldGroup.addCommitHandler(commitHandler);
    }


    private void onCancelClicked() {
        fieldGroup.discard();
        eventBus.publishEvent(new DoneWithPopupEvent(this, Reason.CANCEL));
    }

    private void onSaveClicked() {
        try {
            fieldGroup.commit();
        } catch (CommitException e) {
            Notification.show("Error saving", Type.ERROR_MESSAGE);
        }
    }

    // ------------------------ field adding methods ------------------------ //

    protected TextField addTextField(String caption, String propertyId) {
        return addField(new TextField(caption), propertyId);
    }

    protected PopupDateField addDateField(String caption, String propertyId) {
        return addField(new PopupDateField(caption), propertyId);
    }

    protected CheckBox addCheckBox(String caption, String propertyId) {
        return addField(new CheckBox(caption), propertyId);
    }

    protected <T extends Field> T addField(T field, String propertyId) {
        fieldGroup.bind(field, propertyId);
        fieldLayout.addComponent(field);
        return field;
    }

    protected void addComponent(Component component) {
        fieldLayout.addComponent(component);
    }

    // ------------------------ data binding ------------------------ //

    protected void bindDesign(Component component) {
        fieldLayout.removeAllComponents();
        fieldGroup.bindMemberFields(component);
        fieldLayout.addComponent(component);
    }

    public void showInEditor(DTO beanToEdit) {
        fieldGroup.setItemDataSource(beanToEdit);
    }
}
