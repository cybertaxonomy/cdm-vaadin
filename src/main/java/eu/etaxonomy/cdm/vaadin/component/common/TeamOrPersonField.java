/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.common;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.vaadin.viritin.fields.LazyComboBox;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.UserError;
import com.vaadin.shared.AbstractFieldState;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.api.utility.UserHelper;
import eu.etaxonomy.cdm.hibernate.HibernateProxyHelper;
import eu.etaxonomy.cdm.model.agent.AgentBase;
import eu.etaxonomy.cdm.model.agent.Person;
import eu.etaxonomy.cdm.model.agent.Team;
import eu.etaxonomy.cdm.model.agent.TeamOrPersonBase;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProvider;
import eu.etaxonomy.cdm.service.UserHelperAccess;
import eu.etaxonomy.cdm.vaadin.component.ButtonFactory;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityReloader;
import eu.etaxonomy.cdm.vaadin.util.TeamOrPersonBaseCaptionGenerator;
import eu.etaxonomy.cdm.vaadin.util.converter.CdmBaseDeproxyConverter;
import eu.etaxonomy.cdm.vaadin.view.name.CachingPresenter;
import eu.etaxonomy.vaadin.component.CompositeCustomField;
import eu.etaxonomy.vaadin.component.EntityFieldInstantiator;
import eu.etaxonomy.vaadin.component.ReloadableLazyComboBox;
import eu.etaxonomy.vaadin.component.SwitchableTextField;
import eu.etaxonomy.vaadin.component.ToManyRelatedEntitiesListSelect;

/**
 * @author a.kohlbecker
 * @since May 11, 2017
 *
 */
public class TeamOrPersonField extends CompositeCustomField<TeamOrPersonBase<?>> {

    private static final long serialVersionUID = 660806402243118112L;

    private static final Logger logger = Logger.getLogger(TeamOrPersonField.class);

    private static final String PRIMARY_STYLE = "v-team-or-person-field";

    private CssLayout root = new CssLayout();
    private CssLayout toolBar= new CssLayout();
    private CssLayout compositeWrapper = new CssLayout();

    private ReloadableLazyComboBox<TeamOrPersonBase> teamOrPersonSelect = new ReloadableLazyComboBox<TeamOrPersonBase>(TeamOrPersonBase.class);

    private Button removeButton = ButtonFactory.REMOVE_ALL_ITEMS.createButton();
    private Button personButton = new Button(FontAwesome.USER);
    private Button teamButton = new Button(FontAwesome.USERS);

    // Fields for case when value is a Person
    private PersonField personField = new PersonField();

    // Fields for case when value is a Team
    private SwitchableTextField titleField = new SwitchableTextField("Team (bibliographic)");
    private SwitchableTextField nomenclaturalTitleField = new SwitchableTextField("Team (nomenclatural)");
    private ToManyRelatedEntitiesListSelect<Person, PersonField> personsListEditor = new ToManyRelatedEntitiesListSelect<Person, PersonField>(Person.class, PersonField.class, "Team members");

    private BeanFieldGroup<Team> fieldGroup  = new BeanFieldGroup<>(Team.class);

    private TeamOrPersonBaseCaptionGenerator.CacheType cacheType;

    protected List<Component> editorComponents = Arrays.asList(removeButton, personButton, teamButton, teamOrPersonSelect);

    public TeamOrPersonField(String caption, TeamOrPersonBaseCaptionGenerator.CacheType cacheType){

        setCaption(caption);

        this.cacheType = cacheType;
        teamOrPersonSelect.setCaptionGenerator(new TeamOrPersonBaseCaptionGenerator<TeamOrPersonBase>(cacheType));


        addStyledComponent(teamOrPersonSelect);
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

        teamOrPersonSelect.addValueChangeListener(e -> {
            setValue(teamOrPersonSelect.getValue(), false, true);
        });
        teamOrPersonSelect.setWidthUndefined();

        removeButton.addClickListener(e -> {
            teamOrPersonSelect.clear();
            setValue(null, false, true);
        });
        removeButton.setDescription("Remove");

        personButton.addClickListener(e -> {
            setValue(Person.NewInstance(), false, true);
            resetReadOnlyComponents();

        });
        personButton.setDescription("Add person");
        teamButton.addClickListener(e -> {
            setValue(Team.NewInstance(), false, true);
            resetReadOnlyComponents();
        });
        teamButton.setDescription("Add team");

        toolBar.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP + " toolbar");
        toolBar.addComponents(teamOrPersonSelect,  removeButton, personButton, teamButton);

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
        boolean userCanCreate = UserHelperAccess.userHelper().userHasPermission(Person.class, "CREATE");

        teamOrPersonSelect.setVisible(val == null);
        removeButton.setVisible(val != null);
        personButton.setEnabled(userCanCreate && val == null);
        teamButton.setEnabled(userCanCreate && val == null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setInternalValue(TeamOrPersonBase<?> newValue) {

        TeamOrPersonBase<?> oldValue = getValue();
        super.setInternalValue(newValue);

        newValue = HibernateProxyHelper.deproxy(newValue);

        compositeWrapper.removeAllComponents();
        compositeWrapper.addComponent(toolBar);

        if(newValue != null) {

            if(Person.class.isAssignableFrom(newValue.getClass())){
                // value is a Person
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
                boolean readonlyState = personsListEditor.isReadOnly();
                fieldGroup.bind(personsListEditor, "teamMembers"); // here personField is set readonly since setTeamMembers does not exist
                personsListEditor.setReadOnly(readonlyState); // fixing the readonly state

                personsListEditor.registerParentFieldGroup(fieldGroup);

            } else {
                UserError usererror = new UserError("TeamOrPersonField Error: Unsupported value type: " + newValue.getClass().getName());
                setComponentError(usererror);
                logger.error(usererror.getMessage());
            }
        } else {
            if(oldValue != null){
                // value is null --> clean up all nested fields
                // allow replacing old content in the editor by null
                setReadOnlyComponents(false);
                if(Person.class.isAssignableFrom(oldValue.getClass())){
                    personField.unregisterParentFieldGroup(fieldGroup);
                    personField.setReadOnly(false);
                    personField.setValue((Person) null);
                } else if(Team.class.isAssignableFrom(oldValue.getClass())){
                    titleField.unbindFrom(fieldGroup);
                    nomenclaturalTitleField.unbindFrom(fieldGroup);
                    fieldGroup.unbind(personsListEditor);
                    fieldGroup.setItemDataSource((Team)null);
                    personsListEditor.registerParentFieldGroup(null);
                    personsListEditor.setReadOnly(false);
                    personsListEditor.setValue(null);
                    personsListEditor.registerParentFieldGroup(null);
                } else {
                    UserError usererror = new UserError("TeamOrPersonField Error: Unsupported value type in oldValue: " + oldValue.getClass().getName());
                    setComponentError(usererror);
                    logger.error(usererror.getMessage());
                }
            }
        }
        adaptToUserPermissions(newValue);
        updateToolBarButtonStates();
    }


    private void adaptToUserPermissions(TeamOrPersonBase teamOrPerson) {

        UserHelper userHelper = UserHelperAccess.userHelper();
        boolean canEdit = teamOrPerson == null || !teamOrPerson.isPersited() || userHelper.userHasPermission(teamOrPerson, CRUD.UPDATE);
        if(!canEdit){
            setReadOnlyComponents(true);
        }
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
        if(!getState(false).readOnly && getPropertyDataSource().isReadOnly()){
            // the TeamOrPersonBase Editor (remove, addPerson, addTeam) is not readonly
            // thus removing the TeamOrPerson is allowed. In case the datasource is readonly
            // due to missing user grants for the TeamOrPerson it must be set to readWrite to
            // make it possible to change the property of the parent
            getPropertyDataSource().setReadOnly(false);
        }

        super.commit();

        if(hasNullContent()){
            getPropertyDataSource().setValue(null);
            setValue(null);
        }

        TeamOrPersonBase<?> bean = getValue();
        if(bean != null && bean instanceof Team){
            boolean isUnsaved = bean.getId() == 0;
            if(isUnsaved){
                UserHelperAccess.userHelper().createAuthorityForCurrentUser(bean, EnumSet.of(CRUD.UPDATE, CRUD.DELETE), null);
            }
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
        // NOTE:
        //   it is important to add the ToOneRelatedEntityReloader to the TeamOrPersonField directly
        //   since the value of the select will be immediately passed to the TeamOrPersonField
        ToOneRelatedEntityReloader<TeamOrPersonBase<?>> teamOrPersonReloader = new ToOneRelatedEntityReloader<TeamOrPersonBase<?>>(this, cachingPresenter);
        this.addValueChangeListener(teamOrPersonReloader);
    }

    public void setFilterablePersonPagingProvider(CdmFilterablePagingProvider<AgentBase, Person> pagingProvider, CachingPresenter cachingPresenter){

        personsListEditor.setEntityFieldInstantiator(new EntityFieldInstantiator<PersonField>() {

            @Override
            public PersonField createNewInstance() {
                PersonField f = new PersonField();
                f.setAllowNewEmptyEntity(true); // otherwise new entities can not be added to the personsListEditor
                f.getPersonSelect().loadFrom(pagingProvider, pagingProvider, pagingProvider.getPageSize());
                f.getPersonSelect().setCaptionGenerator(new TeamOrPersonBaseCaptionGenerator<Person>(cacheType));
                // NOTE:
                //   it is important to add the ToOneRelatedEntityReloader to the PersonField directly
                //   since the value of the select will be immediately passed to the PersonField:
                f.addValueChangeListener(new ToOneRelatedEntityReloader<Person>(f, cachingPresenter));
                return f;
            }
        });
    }

    @Override
    public void setValue(TeamOrPersonBase<?> newFieldValue) {
        // ignore readonly states of the datasource
        setValue(newFieldValue, false, !hasSharedStateReadOnly());
    }

    protected boolean hasSharedStateReadOnly(){
        AbstractFieldState sharedState = getState(false);
        return sharedState.readOnly;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setReadOnly(boolean readOnly) {
//        super.setReadOnly(readOnly); // moved into setEditorReadOnly()
        setReadOnlyComponents(readOnly);
    }

    public void setEditorReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        for(Component c : editorComponents){
            applyReadOnlyState(c, readOnly);
        }

    }

    /**
     * Reset the readonly state of nested components to <code>false</code>.
     */
    protected void resetReadOnlyComponents() {
        if(!isReadOnly()){
            setReadOnlyComponents(false);
        }
    }

    /**
     * Set the nested components (team or person fields) to read only but
     * keep the state of the <code>TeamOrPersonField</code> untouched so
     * that the <code>teamOrPersonSelect</code>, <code>removeButton</code>,
     * <code>personButton</code> and <code>teamButton</code> stay operational.
     *
     * @param readOnly
     */
    protected void setReadOnlyComponents(boolean readOnly) {
        setDeepReadOnly(readOnly, getContent(), editorComponents);
        updateCaptionReadonlyNotice(readOnly);
    }



}
