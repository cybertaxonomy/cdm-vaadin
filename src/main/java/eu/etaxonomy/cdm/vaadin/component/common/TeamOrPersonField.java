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

import org.vaadin.viritin.fields.LazyComboBox;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.hibernate.HibernateProxyHelper;
import eu.etaxonomy.cdm.model.agent.AgentBase;
import eu.etaxonomy.cdm.model.agent.Person;
import eu.etaxonomy.cdm.model.agent.Team;
import eu.etaxonomy.cdm.model.agent.TeamOrPersonBase;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProvider;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityReloader;
import eu.etaxonomy.cdm.vaadin.security.UserHelper;
import eu.etaxonomy.cdm.vaadin.util.CdmTitleCacheCaptionGenerator;
import eu.etaxonomy.cdm.vaadin.util.converter.CdmBaseDeproxyConverter;
import eu.etaxonomy.cdm.vaadin.view.name.CachingPresenter;
import eu.etaxonomy.vaadin.component.CompositeCustomField;
import eu.etaxonomy.vaadin.component.EntityFieldInstantiator;
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

    private LazyComboBox<TeamOrPersonBase> teamOrPersonSelect = new LazyComboBox<TeamOrPersonBase>(TeamOrPersonBase.class);

    private Button selectConfirmButton = new Button("OK");
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

    private CdmFilterablePagingProvider<AgentBase, Person> pagingProviderPerson;

    public TeamOrPersonField(String caption){

        setCaption(caption);

        teamOrPersonSelect.setCaptionGenerator(new CdmTitleCacheCaptionGenerator<TeamOrPersonBase>());


        addStyledComponent(teamOrPersonSelect);
        addStyledComponent(personField);
        addStyledComponent(titleField);
        addStyledComponent(nomenclaturalTitleField);
        addStyledComponent(personsListEditor);
        addStyledComponents(selectConfirmButton, removeButton, personButton, teamButton);


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

        teamOrPersonSelect.addValueChangeListener(e -> {
            selectConfirmButton.setEnabled(teamOrPersonSelect.getValue() != null);
        });
        teamOrPersonSelect.setWidthUndefined();

        selectConfirmButton.setEnabled(teamOrPersonSelect.getValue() != null);
        selectConfirmButton.addClickListener(e -> {
            setValue(teamOrPersonSelect.getValue());
            updateToolBarButtonStates();
        });
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
        toolBar.addComponents(teamOrPersonSelect, selectConfirmButton,  removeButton, personButton, teamButton);

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

        teamOrPersonSelect.setVisible(val == null);
        selectConfirmButton.setVisible(val == null);
        removeButton.setVisible(val != null);
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

        if(newValue != null) {

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
                fieldGroup.setItemDataSource(new BeanItem<Team>((Team)newValue));
                fieldGroup.bind(personsListEditor, "teamMembers"); // here personField is set readonly since setTeamMembers does not exist
                personsListEditor.setReadOnly(false); // fixing the readonly state

                personsListEditor.registerParentFieldGroup(fieldGroup);

            } else {
                setComponentError(new UserError("TeamOrPersonField Error: Unsupported value type: " + newValue.getClass().getName()));
            }

        }

        updateToolBarButtonStates();
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

    /**
     * @return the teamOrPersonSelect
     */
    public LazyComboBox<TeamOrPersonBase> getTeamOrPersonSelect() {
        return teamOrPersonSelect;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void commit() throws SourceException, InvalidValueException {

        //need to commit the subfields propagation through the fielGroups is not enough
        personField.commit();
        personsListEditor.commit();
        super.commit();

        TeamOrPersonBase<?> bean = getValue();
        if(bean != null && bean instanceof Team){

            boolean isUnsaved = bean.getId() == 0;
            if(isUnsaved){
                UserHelper.fromSession().createAuthorityForCurrentUser(bean, EnumSet.of(CRUD.UPDATE, CRUD.DELETE), null);
            }
        }

        if(hasNullContent()){
            getPropertyDataSource().setValue(null);
            setValue(null);

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Field> nullValueCheckIgnoreFields() {
        List<Field> ignoreFields =  super.nullValueCheckIgnoreFields();
        ignoreFields.add(personField);
        ignoreFields.add(nomenclaturalTitleField.getUnlockSwitch());
        if(nomenclaturalTitleField.getUnlockSwitch().getValue().booleanValue() == false){
            ignoreFields.add(nomenclaturalTitleField.getTextField());
        }
        ignoreFields.add(titleField.getUnlockSwitch());
        if(titleField.getUnlockSwitch().getValue().booleanValue() == false){
            ignoreFields.add(titleField.getTextField());
        }
        return ignoreFields;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNullContent() {

        TeamOrPersonBase<?> bean = getValue();
        if(bean == null) {
            return true;
        }
        if(bean instanceof Team){
            // --- Team
            return super.hasNullContent();
        } else {
            // --- Person
            return personField.hasNullContent();
        }
    }

    public void setFilterableTeamPagingProvider(CdmFilterablePagingProvider<AgentBase, TeamOrPersonBase> pagingProvider, CachingPresenter cachingPresenter){
        teamOrPersonSelect.loadFrom(pagingProvider, pagingProvider, pagingProvider.getPageSize());
        ToOneRelatedEntityReloader<TeamOrPersonBase> teamOrPersonReloader = new ToOneRelatedEntityReloader<TeamOrPersonBase>(teamOrPersonSelect, cachingPresenter);
        teamOrPersonSelect.addValueChangeListener(teamOrPersonReloader );
    }

    public void setFilterablePersonPagingProvider(CdmFilterablePagingProvider<AgentBase, Person> pagingProvider, CachingPresenter cachingPresenter){

        teamOrPersonSelect.addValueChangeListener(new ToOneRelatedEntityReloader<TeamOrPersonBase>(teamOrPersonSelect, cachingPresenter));

        personsListEditor.setEntityFieldInstantiator(new EntityFieldInstantiator<PersonField>() {

            @Override
            public PersonField createNewInstance() {
                PersonField f = new PersonField();
                f.setAllowNewEmptyEntity(true); // otherwise new entities can not be added to the personsListEditor
                f.getPersonSelect().loadFrom(pagingProvider, pagingProvider, pagingProvider.getPageSize());
                f.getPersonSelect().setCaptionGenerator(new CdmTitleCacheCaptionGenerator<Person>());
                f.getPersonSelect().addValueChangeListener(new ToOneRelatedEntityReloader<Person>(f.getPersonSelect(), cachingPresenter));
                return f;
            }
        });
    }

}
