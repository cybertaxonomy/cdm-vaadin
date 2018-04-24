/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.common;

import java.util.EnumSet;
import java.util.List;

import org.vaadin.teemu.switchui.Switch;
import org.vaadin.viritin.fields.LazyComboBox;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.model.agent.Person;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.vaadin.component.TextFieldNFix;
import eu.etaxonomy.cdm.vaadin.permission.UserHelper;
import eu.etaxonomy.vaadin.component.CompositeCustomField;
import eu.etaxonomy.vaadin.component.SwitchButton;

/**
 * @author a.kohlbecker
 * @since May 11, 2017
 *
 */
public class PersonField extends CompositeCustomField<Person> {

    private static final long serialVersionUID = 8346575511284469356L;

    private static final String PRIMARY_STYLE = "v-person-field";

    /**
     * do not allow entities which are having only <code>null</code> values in all fields
     * {@link #getValue()} would return <code>null</code> in this case.
     */
    boolean allowNewEmptyEntity = true;

    private LazyComboBox<Person> personSelect = new LazyComboBox<Person>(Person.class);

    private Button personSelectConfirmButton = new Button("OK");
    private Button newPersonButton = new Button("New");

    private BeanFieldGroup<Person> fieldGroup = new BeanFieldGroup<>(Person.class);

    enum Mode {
        CACHE_MODE, DETAILS_MODE;

        public String toCssClass() {
            return name().toLowerCase().replace("_", "-");
        }
    }

    private Mode currentMode = null;

    private float baseWidth = 100 / 9;

    private CssLayout root = new CssLayout();
    private CssLayout selectOrNewContainer = new CssLayout();

    private TextField titleCacheField = new TextFieldNFix();
    private TextField nomenclaturalTitleField = new TextFieldNFix();
    private Button nomenclaturalTitleButton = new Button();
    private CssLayout detailsContainer = new CssLayout();
    private TextField initialsField = new TextFieldNFix();
    private TextField firstNameField = new TextFieldNFix();
    private TextField lastNameField = new TextFieldNFix();
    private TextField prefixField = new TextFieldNFix();
    private TextField suffixField = new TextFieldNFix();
    private SwitchButton unlockSwitch = new SwitchButton();

    private boolean onCommit = false;



    /**
     * @param caption
     */
    public PersonField(String caption) {

        this();
        setCaption(caption);
    }

    /**
     * @param caption
     */
    public PersonField() {

        root.setPrimaryStyleName(PRIMARY_STYLE);

        // select existing or create new person
        addStyledComponents(personSelect, personSelectConfirmButton, newPersonButton);
        personSelect.addValueChangeListener(e -> {
            if(personSelect.getValue() != null){
                personSelectConfirmButton.setEnabled(true);
            }
        });
        personSelectConfirmButton.setEnabled(false);
        personSelectConfirmButton.addClickListener(e -> {
            setValue(personSelect.getValue());
            personSelect.clear();
        });
        selectOrNewContainer.addComponents(personSelect, personSelectConfirmButton, newPersonButton);
        newPersonButton.addClickListener(e -> createNewPerson());

        // edit person
        addStyledComponent(titleCacheField);
        addStyledComponents(initialsField);
        addStyledComponent(firstNameField);
        addStyledComponent(lastNameField);
        addStyledComponent(prefixField);
        addStyledComponent(suffixField);
        addStyledComponent(unlockSwitch);
        addStyledComponent(nomenclaturalTitleField);
        addStyledComponent(nomenclaturalTitleButton);

        addSizedComponent(root);
    }

    /**
     *
     */
    private void checkUserPermissions(Person newValue) {
        boolean userCanEdit = newValue == null || !newValue.isPersited() || UserHelper.fromSession().userHasPermission(newValue, "DELETE", "UPDATE");
        setEnabled(userCanEdit);
    }

    private void setMode(Mode mode){
        if(mode.equals(currentMode)){
            return;
        }
        if(currentMode != null){
            String removeMode = currentMode.toCssClass();
            root.removeStyleName(removeMode);
        }
        String newMode = mode.toCssClass();
        root.addStyleName(newMode);
        currentMode = mode;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected Component initContent() {

        selectOrNewContainer.setWidth(100, Unit.PERCENTAGE);
        personSelect.setWidthUndefined();

        root.addComponent(titleCacheField);
        root.addComponent(unlockSwitch);
        root.addComponent(selectOrNewContainer);

        titleCacheField.setWidth(100, Unit.PERCENTAGE);

        prefixField.setWidth(baseWidth, Unit.PERCENTAGE);
        prefixField.setInputPrompt("Prefix");

        initialsField.setWidth(baseWidth, Unit.PERCENTAGE);
        initialsField.setInputPrompt("Initials");

        firstNameField.setWidth(baseWidth * 3, Unit.PERCENTAGE);
        firstNameField.setInputPrompt("Other/given names");

        lastNameField.setWidth(baseWidth * 3, Unit.PERCENTAGE);
        lastNameField.setInputPrompt("Family name");

        suffixField.setWidth(baseWidth, Unit.PERCENTAGE);
        suffixField.setInputPrompt("Suffix");

        detailsContainer.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        detailsContainer.addComponent(prefixField);
        detailsContainer.addComponent(initialsField);
        detailsContainer.addComponent(firstNameField);
        detailsContainer.addComponent(lastNameField);
        detailsContainer.addComponent(suffixField);
        root.addComponent(detailsContainer);

        nomenclaturalTitleButton.setHeight(22, Unit.PIXELS);
        nomenclaturalTitleButton.setDescription("Show the nomenclatural title cache.");
        nomenclaturalTitleButton.addClickListener( e -> {
            nomenclaturalTitleField.setVisible(!nomenclaturalTitleField.isVisible());
            nomenclaturalTitleButtonChooseIcon();
            if(nomenclaturalTitleField.isVisible()){
                nomenclaturalTitleField.focus();
            }
        });
        // nomenclaturalTitleField.setCaption("Nomenclatural title");
        nomenclaturalTitleField.setWidth(100, Unit.PERCENTAGE);

        root.addComponent(nomenclaturalTitleField);
        root.addComponent(nomenclaturalTitleButton);

        unlockSwitch.addValueChangeListener(e -> {
            if(refreshMode()){
                switch (currentMode) {
                    case CACHE_MODE:
                        titleCacheField.focus();
                        break;
                    case DETAILS_MODE:
                        firstNameField.focus();
                        break;
                    default:
                        break;

                }
            }
        });
        unlockSwitch.setValueSetLister(e -> {
            refreshMode();
        });

        addDefaultStyles();
        setMode(Mode.DETAILS_MODE);

        fieldGroup.bind(titleCacheField, "titleCache");
        fieldGroup.bind(prefixField, "prefix");
        fieldGroup.bind(initialsField, "initials");
        fieldGroup.bind(firstNameField, "firstname");
        fieldGroup.bind(lastNameField, "lastname");
        fieldGroup.bind(suffixField, "suffix");
        fieldGroup.bind(unlockSwitch, "protectedTitleCache");
        fieldGroup.bind(nomenclaturalTitleField, "nomenclaturalTitle");
        fieldGroup.setBuffered(false);

        updateVisibilities(getValue());

        return root;
    }

    /**
     *
     */
    protected void nomenclaturalTitleButtonChooseIcon() {
        nomenclaturalTitleButton.setIcon(nomenclaturalTitleField.isVisible() ? FontAwesome.ANGLE_UP : FontAwesome.ELLIPSIS_H);
    }

    /**
     *
     * @return true if the mode has changed
     */
    protected boolean refreshMode() {
        Mode lastMode = currentMode;
        setMode(unlockSwitch.getValue() ? Mode.CACHE_MODE: Mode.DETAILS_MODE);
        return !lastMode.equals(currentMode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends Person> getType() {
        return Person.class;
    }

    private void createNewPerson(){
        Person p = Person.NewInstance();
        setValue(p);
    }

    @Override
    public void setValue(Person person){
        super.setValue(person);
        if(person != null && person.getId() != 0){
            personSelect.setValue(person);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setInternalValue(Person newValue) {

        super.setInternalValue(newValue);
        fieldGroup.setItemDataSource(newValue);
        checkUserPermissions(newValue);
        updateVisibilities(newValue);
    }

    /**
     *
     */
    private void updateVisibilities(Person person) {

        selectOrNewContainer.setVisible(person == null);

        detailsContainer.setVisible(person != null);
        unlockSwitch.setVisible(person != null);
        titleCacheField.setVisible(person != null);
        String nomTitle = nomenclaturalTitleField.getValue();
        nomenclaturalTitleField.setVisible(nomTitle != null && nomTitle.equals(titleCacheField.getValue()));
        nomenclaturalTitleButtonChooseIcon();

    }

    @Override
    protected void addDefaultStyles(){
        titleCacheField.addStyleName("cache-field");
        detailsContainer.addStyleName("details-fields");
        unlockSwitch.addStyleName(Switch.DOM_STYLE);
        nomenclaturalTitleButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED + " center-h");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldGroup getFieldGroup() {
        return fieldGroup;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Field> nullValueCheckIgnoreFields() {

        List<Field>ignoreFields = super.nullValueCheckIgnoreFields();
        ignoreFields.add(unlockSwitch);

        if(unlockSwitch.getValue().booleanValue() == false){
            Person value = getValue();
            if(value != null && value.getId() == 0){
                // only if the entity is unsaved!
                ignoreFields.add(titleCacheField);
                titleCacheField.setValue(null);
            }
        }
        return ignoreFields;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commit() throws SourceException, InvalidValueException {

        super.commit();

        Person bean =  getValue();
        if(bean != null){
            boolean isUnsaved = bean.getId() == 0;
            if(isUnsaved && !(hasNullContent() && !allowNewEmptyEntity)){
                UserHelper.fromSession().createAuthorityForCurrentUser(bean, EnumSet.of(CRUD.UPDATE, CRUD.DELETE), null);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return returns <code>null</code> in case the edited entity is unsaved and if
     * it only has null values.
     */
    @Override
    public Person getValue() {
        Person bean = super.getValue();
        if(bean == null){
            return null;
        }
       // boolean isUnsaved = bean.getId() == 0;
//        if(isUnsaved && hasNullContent() && !allowNewEmptyEntity) {
//            return null;
//        }
        return bean;
    }

    /**
     * @return the personSelect
     */
    public LazyComboBox<Person> getPersonSelect() {
        return personSelect;
    }

    /**
     * @param personSelect the personSelect to set
     */
    public void setPersonSelect(LazyComboBox<Person> personSelect) {
        this.personSelect = personSelect;
    }

    /**
     * @return the allowNewEmptyEntity
     */
    public boolean isAllowNewEmptyEntity() {
        return allowNewEmptyEntity;
    }

    /**
     * @param allowNewEmptyEntity the allowNewEmptyEntity to set
     */
    public void setAllowNewEmptyEntity(boolean allowNewEmptyEntity) {
        this.allowNewEmptyEntity = allowNewEmptyEntity;
    }



}
