/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.common;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.model.agent.Person;
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
        super.setValue(person);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setInternalValue(Person newValue) {
        super.setInternalValue(newValue);
        fieldGroup.setItemDataSource(newValue);
        // refreshMode();
    }

    @Override
    protected void addDefaultStyles(){
        cacheField.addStyleName("cache-field");
        detailsContainer.addStyleName("details-fields");
    }
}
