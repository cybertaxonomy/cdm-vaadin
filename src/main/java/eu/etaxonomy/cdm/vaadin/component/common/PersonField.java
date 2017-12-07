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

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.model.agent.Person;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.vaadin.security.UserHelper;
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
    boolean allowNewEmptyEntity = false;

    private BeanFieldGroup<Person> fieldGroup = new BeanFieldGroup<>(Person.class);

    enum Mode {
        CACHE_MODE, DETAILS_MODE;

        public String toCssClass() {
            return name().toLowerCase().replace("_", "-");
        }
    }

    private Mode currentMode = null;

    private float baseWidth = 100 / 8;

    private CssLayout root = new CssLayout();
    private TextField cacheField = new TextField();
    private CssLayout detailsContainer = new CssLayout();
    private TextField firstNameField = new TextField();
    private TextField lastNameField = new TextField();
    private TextField prefixField = new TextField();
    private TextField suffixField = new TextField();
    private SwitchButton unlockSwitch = new SwitchButton();

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

        addStyledComponent(cacheField);
        addStyledComponent(firstNameField);
        addStyledComponent(lastNameField);
        addStyledComponent(prefixField);
        addStyledComponent(suffixField);
        addStyledComponent(unlockSwitch);

        addSizedComponent(root);

    }

    /**
     *
     */
    private void checkUserPermissions(Person newValue) {
        boolean userCanEdit = UserHelper.fromSession().userHasPermission(newValue, "DELETE", "UPDATE");
        boolean isUnsavedEnitity = newValue.getId() == 0;
        setEnabled(isUnsavedEnitity || userCanEdit);
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

        root.addComponent(cacheField);
        root.addComponent(unlockSwitch);

        cacheField.setWidth(100, Unit.PERCENTAGE);

        prefixField.setWidth(baseWidth, Unit.PERCENTAGE);
        prefixField.setInputPrompt("Prefix");

        firstNameField.setWidth(baseWidth * 3, Unit.PERCENTAGE);
        firstNameField.setInputPrompt("First Name");

        lastNameField.setWidth(baseWidth * 3, Unit.PERCENTAGE);
        lastNameField.setInputPrompt("Last Name");

        suffixField.setWidth(baseWidth, Unit.PERCENTAGE);
        suffixField.setInputPrompt("Suffix");

        detailsContainer.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        detailsContainer.addComponent(prefixField);
        detailsContainer.addComponent(firstNameField);
        detailsContainer.addComponent(lastNameField);
        detailsContainer.addComponent(suffixField);
        root.addComponent(detailsContainer);

        unlockSwitch.addValueChangeListener(e -> {
            if(refreshMode()){
                switch (currentMode) {
                    case CACHE_MODE:
                        cacheField.focus();
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

        fieldGroup.bind(cacheField, "titleCache");
        fieldGroup.bind(prefixField, "prefix");
        fieldGroup.bind(firstNameField, "firstname");
        fieldGroup.bind(lastNameField, "lastname");
        fieldGroup.bind(suffixField, "suffix");
        fieldGroup.bind(unlockSwitch, "protectedTitleCache");
        fieldGroup.setBuffered(false);

        return root;
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

    @Override
    public void setValue(Person person){
        if(person == null){
            person = Person.NewInstance();
        }
        super.setValue(person);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setInternalValue(Person newValue) {
        super.setInternalValue(newValue);
        fieldGroup.setItemDataSource(newValue);
        checkUserPermissions(newValue);
    }

    @Override
    protected void addDefaultStyles(){
        cacheField.addStyleName("cache-field");
        detailsContainer.addStyleName("details-fields");
        unlockSwitch.addStyleName(Switch.DOM_STYLE);
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
            ignoreFields.add(cacheField);
            cacheField.setValue(null);
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
        boolean isUnsaved = bean.getId() == 0;
        if(isUnsaved && hasNullContent() && !allowNewEmptyEntity) {
            return null;
        }
        return bean;
    }

}
