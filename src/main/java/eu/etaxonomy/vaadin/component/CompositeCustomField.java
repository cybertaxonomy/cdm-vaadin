/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Field;
import com.vaadin.ui.HasComponents;

import eu.etaxonomy.cdm.vaadin.event.NestedButtonStateUpdater;

/**
 * TODO implement height methods for full component size support
 *
 * Implementations need to override {@link  AbstractField#setInternalValue(TeamOrPersonBase<?> newValue)} in order to
 * to set the item datasource of the fieldGroup for example:
 * <pre>
 * @Override
   protected void setInternalValue(TeamOrPersonBase<?> newValue) {
     ...
     fieldGroup.setItemDataSource(new BeanItem<Team>((Team)newValue));
     ...
   }
 * </pre>
 *
 * @author a.kohlbecker
 * @since May 12, 2017
 *
 * IMPORTANT see also {@link CompositeStyledComponent} which has almost the same functionality.
 *
 */
@SuppressWarnings("serial")
public abstract class CompositeCustomField<T> extends CustomField<T> implements NestedFieldGroup {

    protected static final String READ_ONLY_CAPTION_SUFFIX = " (read only)";

    private List<Component> styledComponents = new ArrayList<>();

    private List<Component> sizedComponents = new ArrayList<>();

    private CommitHandler commitHandler = new CommitHandler() {

        @Override
        public void preCommit(CommitEvent commitEvent) throws CommitException {
            // commit the nested bean(s) first
            if(getFieldGroup().isPresent()){
                getFieldGroup().get().commit();
            }
        }

        @Override
        public void postCommit(CommitEvent commitEvent) throws CommitException {
            // noting to do
        }};

    protected List<Component> getStyledComponents() {
        if(styledComponents == null){
            styledComponents = new ArrayList<>();
        }
        return styledComponents;
    }

    /**
     * Implementations preferably call this method in the constructor
     *
     * @param component
     * @return
     */
    protected boolean addStyledComponent(Component component){
        applyCurrentStyleNames(component);
        return styledComponents.add(component);
    }

    /**
     * Implementations preferably call this method in the constructor
     *
     * @param component
     * @return
     */
    protected boolean addStyledComponents(Component ... component){
        List<Component> componentList = Arrays.asList(component);
        componentList.forEach(c -> applyCurrentStyleNames(c));
        return styledComponents.addAll(componentList);
    }

    protected List<Component> getSizedComponents() {
        if(sizedComponents == null){
            sizedComponents = new ArrayList<>();
        }
        return sizedComponents;
    }

    /**
     * Implementations preferably call this method in the constructor
     *
     * @param component
     * @return
     */
    protected boolean addSizedComponent(Component component){
        applyCurrentSize(component);
        return sizedComponents.add(component);
    }

    /**
     * Implementations preferably call this method in the constructor
     *
     * @param component
     * @return
     */
    protected boolean addSizedComponents(Component ... component){
        List<Component> componentList = Arrays.asList(component);
        componentList.forEach(c -> applyCurrentSize(c));
        return sizedComponents.addAll(componentList);
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        getSizedComponents().forEach(c -> {if(c != null) {c.setWidth(width);}});
    }

    @Override
    public void setWidth(float width, Unit unit){
        super.setWidth(width, unit);
        getSizedComponents().forEach(c -> {if(c != null) {c.setWidth(width, unit);}});
    }

    @Override
    public void setWidthUndefined() {
        super.setWidthUndefined();
        getSizedComponents().forEach(c -> {if(c != null) {c.setWidthUndefined();}});
    }

    @Override
    public void setStyleName(String style) {
        super.setStyleName(style);
        getStyledComponents().forEach(c -> c.setStyleName(style));
        addDefaultStyles();
    }

    @Override
    public void addStyleName(String style) {
        super.addStyleName(style);
        getStyledComponents().forEach(c -> c.addStyleName(style));
    }

    protected void applyCurrentStyleNames(Component newSubComponent){
        newSubComponent.setStyleName(getStyleName());
    }

    protected void applyCurrentSize(Component newSubComponent){
        newSubComponent.setWidth(this.getWidth(), this.getWidthUnits());
    }

    /**
     * Implementations can may apply default styles to components added to <code>StyledComponents</code>
     * to prevent these styles from being overwritten when setStyleName() id called on the composite field.
     */
    protected abstract void addDefaultStyles();

    /**
     * Implementations return the local fieldGroup
     *
     * @return
     */
    @Override
    public abstract Optional<FieldGroup> getFieldGroup();

    /**
     * @return true if all fields having the value <code>null</code> and if there is no fieldgroup at all for this component.
     */
    @SuppressWarnings("rawtypes")
    public boolean hasNullContent() {
        Collection<Field> nullValueCheckIgnore = nullValueCheckIgnoreFields();
        if(!getFieldGroup().isPresent()){
            return true;
        }
        return getFieldGroup().get().getFields().stream()
                .filter(
                        f -> !nullValueCheckIgnore.contains(f)
                )
                //.peek( f -> System.out.println("###> " + f.getCaption() + ": " + f.getValue()))
                .allMatch(
                        f -> {
                            if(f instanceof CompositeCustomField){
                                return ((CompositeCustomField)f).hasNullContent();
                            } else {
                                if(f.getValue() == null) {
                                    return true;
                                } else {
                                    return false;
                                }
                            }
                        }
                );
    }

    /**
     * @return
     */
    protected List<Field> nullValueCheckIgnoreFields() {
        // TODO Auto-generated method stub
        return new ArrayList<Field>(0);
    }

    @Override
    public void registerParentFieldGroup(FieldGroup parent) {
        parent.addCommitHandler(commitHandler);
    }

    @Override
    public void unregisterParentFieldGroup(FieldGroup parent) {
        parent.removeCommitHandler(commitHandler);
    }

    /**
     *
     * @param readOnly
     *            the readonly state
     * @param component
     *            the component to process on
     * @param ignore
     *            can be <code>null</code>
     */
    protected void setDeepReadOnly(boolean readOnly, Component component, Collection<Component> ignore) {

        if(ignore != null && ignore.contains(component)){
            return;
        }

        applyReadOnlyState(component, readOnly);
        if(HasComponents.class.isAssignableFrom(component.getClass())){
            for(Component nestedComponent : ((HasComponents)component)){
                setDeepReadOnly(readOnly, nestedComponent, ignore);
            }
        }
    }

    /**
     * Sets the readonly state for the component but treats Buttons differently.
     * For nested Buttons the readonly state is projected to enabled state to
     * make them inactive. Finally NestedButtonStateUpdaters are triggered to
     * allow to disable buttons accordingly to user permissions.
     *
     * @param readOnly
     * @param component
     */
    protected void applyReadOnlyState(Component component, boolean readOnly) {
        component.setReadOnly(readOnly);
        if(Button.class.isAssignableFrom(component.getClass())){
            component.setEnabled(!readOnly);
        }
        triggerNestedButtonStateUpdaters();
    }



    /**
     *
     */
    protected void triggerNestedButtonStateUpdaters() {
        for(Object l : getListeners(AbstractField.ValueChangeEvent.class)){
           if(NestedButtonStateUpdater.class.isAssignableFrom(l.getClass())){
               // trigger a fake ValueChangeEvent to let the ToOneRelatedEntityButtonUpdater fix the states
               // of nested buttons
               ((NestedButtonStateUpdater)l).valueChange(new AbstractField.ValueChangeEvent(this));
           }
        }

    }

    @Override
    public String toString(){
        return this.getClass().getSimpleName() + ": " +
                ( getValue() != null ? getValue() : "null");
    }

    protected void updateCaptionReadonlyNotice(boolean readOnly) {
        if(readOnly){
            if(!getCaption().contains(READ_ONLY_CAPTION_SUFFIX)){
                setCaption(getCaption() + READ_ONLY_CAPTION_SUFFIX);
            }
        } else {
            setCaption(getCaption().replace(READ_ONLY_CAPTION_SUFFIX, ""));
        }
    }

}
