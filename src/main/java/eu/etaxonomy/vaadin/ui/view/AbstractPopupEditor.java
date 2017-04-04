package eu.etaxonomy.vaadin.ui.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

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
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.vaadin.ui.view.DoneWithPopupEvent.Reason;

//FIXME this pop editor is not following the MVP pattern, the com.vaadin.devday.mvp.ui.AbstractPopupView is designed better in this sense!!
public abstract class AbstractPopupEditor<DTO extends Object> extends CustomComponent implements PopupView {

	private static final long serialVersionUID = 1441816620197127918L;

	private BeanFieldGroup<DTO> fieldGroup;

	private VerticalLayout mainLayout;

	private AbstractOrderedLayout fieldLayout;

    @Autowired
    ApplicationEventPublisher eventBus;

    @Autowired
    CdmRepository cdmRepository;

	private HorizontalLayout buttonLayout;

	private Button save;

	private Button cancel;

	private class SaveHandler implements CommitHandler {
		private static final long serialVersionUID = 2047223089707080659L;

		@Override
		public void preCommit(CommitEvent commitEvent) throws CommitException {
		}

		@Override
		public void postCommit(CommitEvent commitEvent) throws CommitException {
			try {
			    AbstractPopupEditor.this.storeDto(getBean());
				eventBus.publishEvent(new DoneWithPopupEvent(AbstractPopupEditor.this, Reason.SAVE));
			} catch (Exception e) {
				throw new CommitException("Failed to store data to backend", e);
			}
		}
	}

	protected CdmRepository getRepsitory() {
	    return cdmRepository;
	}

	public abstract void storeDto(DTO bean) throws CommitException;

	public AbstractPopupEditor(Class<DTO> dtoType) {
		this(new FormLayout(), dtoType);
		fieldLayout.setMargin(true);
	}

	public AbstractPopupEditor(AbstractOrderedLayout layout, Class<DTO> dtoType) {
		setWidthUndefined();

		mainLayout = new VerticalLayout();
		mainLayout.setWidthUndefined();

		fieldGroup = new BeanFieldGroup<>(dtoType);
		fieldGroup.addCommitHandler(new SaveHandler());

		setCompositionRoot(mainLayout);

		fieldLayout = layout;
		fieldLayout.setWidthUndefined();
		fieldLayout.setSpacing(true);

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

	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		save.setVisible(!readOnly);
		cancel.setCaption(readOnly ? "Close" : "Cancel");
	}

	protected VerticalLayout getMainLayout() {
		return mainLayout;
	}

	protected void addCommitHandler(CommitHandler commitHandler) {
		fieldGroup.addCommitHandler(commitHandler);
	}

	protected DTO getBean() {
		if (fieldGroup.getItemDataSource() != null) {
			return fieldGroup.getItemDataSource().getBean();
		}

		return null;
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

	public void showInEditor(DTO beanToEdit) {
		fieldGroup.setItemDataSource(beanToEdit);
	}

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

	protected void bindDesign(Component component) {
		fieldLayout.removeAllComponents();
		fieldGroup.bindMemberFields(component);
		fieldLayout.addComponent(component);
	}
}
