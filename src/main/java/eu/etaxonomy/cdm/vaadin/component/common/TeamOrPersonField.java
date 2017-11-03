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
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.hibernate.HibernateProxyHelper;
import eu.etaxonomy.cdm.model.agent.Person;
import eu.etaxonomy.cdm.model.agent.Team;
import eu.etaxonomy.cdm.model.agent.TeamOrPersonBase;
import eu.etaxonomy.cdm.vaadin.security.UserHelper;
import eu.etaxonomy.cdm.vaadin.util.converter.CdmBaseDeproxyConverter;
import eu.etaxonomy.vaadin.component.CompositeCustomField;
import eu.etaxonomy.vaadin.component.SwitchableTextField;
import eu.etaxonomy.vaadin.component.ToManyRelatedEntitiesListSelect;

/**
 * @author a.kohlbecker
 * @since May 11, 2017
 *
 */
public class TeamOrPersonField extends CompositeCustomField<TeamOrPersonBase<?>> {

    private static final long serialVersionUID = 660806402243118112L;

    private static final String PRIMARY_STYLE = "v-team-or-person-field";

    private CssLayout root = new CssLayout();
    private CssLayout toolBar= new CssLayout();
    private CssLayout compositeWrapper = new CssLayout();

    private Button removeButton = new Button(FontAwesome.REMOVE);
    private Button personButton = new Button(FontAwesome.USER);
    private Button teamButton = new Button(FontAwesome.USERS);

    // Fields for case when value is a Person
    private PersonField personField = new PersonField();

    // Fields for case when value is a Team
    private SwitchableTextField titleField = new SwitchableTextField("Team (bibliographic)");
    private SwitchableTextField nomenclaturalTitleField = new SwitchableTextField("Team (nomenclatural)");
    private ToManyRelatedEntitiesListSelect<Person, PersonField> personsListEditor = new ToManyRelatedEntitiesListSelect<Person, PersonField>(Person.class, PersonField.class, "Teammembers");

    private BeanFieldGroup<Team> fieldGroup  = new BeanFieldGroup<>(Team.class);

    public TeamOrPersonField(String caption){

        setCaption(caption);

        addStyledComponent(personField);
        addStyledComponent(titleField);
        addStyledComponent(nomenclaturalTitleField);
        addStyledComponent(personsListEditor);
        addStyledComponents(removeButton, personButton, teamButton);


        addSizedComponent(root);
        addSizedComponent(compositeWrapper);
        addSizedComponent(personField);
        addSizedComponent(titleField);
        addSizedComponent(nomenclaturalTitleField);
        addSizedComponent(personsListEditor);

        setConverter(new CdmBaseDeproxyConverter<TeamOrPersonBase<?>>());

        updateToolBarButtonStates();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Component initContent() {

        removeButton.addClickListener(e -> {
            setValue(null);
            updateToolBarButtonStates();
        });
        removeButton.setDescription("Remove");

        personButton.addClickListener(e -> {
            setValue(Person.NewInstance()); // FIXME add SelectField or open select dialog, use ToOneSelect field!!
            updateToolBarButtonStates();
        });
        personButton.setDescription("Add person");
        teamButton.addClickListener(e -> {
            setValue(Team.NewInstance()); // FIXME add SelectField or open select dialog, use ToOneSelect field!!
            updateToolBarButtonStates();
        });
        teamButton.setDescription("Add team");

        toolBar.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP + " toolbar");
        toolBar.addComponents(removeButton, personButton, teamButton);

        compositeWrapper.setStyleName("margin-wrapper");
        compositeWrapper.addComponent(toolBar);

        root.setPrimaryStyleName(PRIMARY_STYLE);
        root.addComponent(compositeWrapper);
        return root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class getType() {
        return TeamOrPersonBase.class;
    }

    private void updateToolBarButtonStates(){
        TeamOrPersonBase<?> val = getInternalValue();
        boolean userCanCreate = UserHelper.fromSession().userHasPermission(Person.class, "CREATE");
        removeButton.setEnabled(val != null);
        personButton.setEnabled(userCanCreate && val == null);
        teamButton.setEnabled(userCanCreate && val == null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setInternalValue(TeamOrPersonBase<?> newValue) {

        super.setInternalValue(newValue);

        newValue = HibernateProxyHelper.deproxy(newValue);

        compositeWrapper.removeAllComponents();
        compositeWrapper.addComponent(toolBar);

        if(newValue == null) {
            return;
        }

        if(Person.class.isAssignableFrom(newValue.getClass())){
            // value is a Person:
            compositeWrapper.addComponent(personField);

            personField.setValue((Person) newValue);
            personField.registerParentFieldGroup(fieldGroup);

        }
        else if(Team.class.isAssignableFrom(newValue.getClass())){
            // otherwise it a Team

            compositeWrapper.addComponents(titleField, nomenclaturalTitleField, personsListEditor);

            titleField.bindTo(fieldGroup, "titleCache", "protectedTitleCache");
            nomenclaturalTitleField.bindTo(fieldGroup, "nomenclaturalTitle", "protectedNomenclaturalTitleCache");
            fieldGroup.bind(personsListEditor, "teamMembers");

            fieldGroup.setItemDataSource(new BeanItem<Team>((Team)newValue));
            personsListEditor.registerParentFieldGroup(fieldGroup);


        } else {
            setComponentError(new UserError("TeamOrPersonField Error: Unsupported value type: " + newValue.getClass().getName()));
        }

        updateToolBarButtonStates();
        checkUserPermissions(newValue);
    }

    private void checkUserPermissions(TeamOrPersonBase<?> newValue) {
        boolean userCanEdit = UserHelper.fromSession().userHasPermission(newValue, "DELETE", "UPDATE");
        setEnabled(userCanEdit);
        personsListEditor.setEnabled(userCanEdit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addDefaultStyles() {
        // no default styles here
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldGroup getFieldGroup() {
        return fieldGroup;
    }

    public Component[] getCachFields(){
        return new Component[]{titleField, nomenclaturalTitleField};
    }



}
