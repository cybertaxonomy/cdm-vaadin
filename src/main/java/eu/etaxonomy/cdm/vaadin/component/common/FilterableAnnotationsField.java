/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.viritin.FilterableListContainer;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.TextArea;

import eu.etaxonomy.cdm.model.common.Annotation;
import eu.etaxonomy.cdm.model.common.AnnotationType;
import eu.etaxonomy.cdm.model.common.Language;
import eu.etaxonomy.cdm.model.term.DefinedTermBase;
import eu.etaxonomy.cdm.vaadin.component.ButtonFactory;
import eu.etaxonomy.cdm.vaadin.util.converter.SetToListConverter;
import eu.etaxonomy.cdm.vaadin.util.filter.CdmTermFilter;
import eu.etaxonomy.vaadin.component.CompositeCustomField;

/**
 * @author a.kohlbecker
 * @since Jun 20, 2018
 */
public class FilterableAnnotationsField extends CompositeCustomField<List<Annotation>> {

    private static final long serialVersionUID = -8258550787601028813L;
    protected static final Logger logger = LogManager.getLogger();

    private CssLayout root = new CssLayout();

    private Table table = new Table();

    private Button newButton = ButtonFactory.CREATE_NEW.createButton();

    private List<AnnotationType> typesFilter = null;

    private BeanItemContainer<DefinedTermBase> typeSelectItemContainer;

    private FilterableListContainer<Annotation> container;

    private boolean withNewButton;

    public FilterableAnnotationsField() {
        this(null);
    }

    public FilterableAnnotationsField(String caption) {

        setCaption(caption);
        // annotations are always sets
        setConverter(new SetToListConverter<Annotation>());

        root.setWidth(100, Unit.PERCENTAGE);

        // setup table
        table.setPageLength(1);
        table.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
        table.setWidth(100,  Unit.PERCENTAGE);
        table.setTableFieldFactory(new DefaultFieldFactory() {

            private static final long serialVersionUID = 5437750882205859178L;

            @Override
            public Field<?> createField(Item item, Object propertyId, Component uiContext) {

                Field<?> field = createField(propertyId);
                if(field == null) {
                    field = super.createField(item, propertyId, uiContext);
                }
                return field;

            }

            @Override
            public Field<?> createField(Container container, Object itemId, Object propertyId, Component uiContext) {

                Field<?> field = createField(propertyId);

                if(field == null) {
                    field = super.createField(container, itemId, propertyId, uiContext);
                }
                return field;
            }

            protected Field<?> createField(Object propertyId) {
                Field<?> field = null;
                if(propertyId.equals("text")){
                    TextArea ta = new TextArea();
                    ta.setNullRepresentation("");
                    ta.setWidth(100,  Unit.PERCENTAGE);
                    field = ta;
                } else if(propertyId.equals("annotationType")) {
                    NativeSelect select = new NativeSelect();
                    select.setNullSelectionAllowed(false); //#10538
                    select.setContainerDataSource(typeSelectItemContainer);
                    select.setWidth(100, Unit.PIXELS);
                    field = select;
                }
                field.setStyleName(table.getStyleName());
                return field;
            }
        });

        addStyledComponent(table);

    }

    public void setAnnotationTypesVisible(AnnotationType ... types){
        typesFilter = Arrays.asList(types);
    }

    @Override
    protected void addDefaultStyles() {
        // no default styles here
    }

    @Override
    public Optional<FieldGroup> getFieldGroup() {
        // holds a Container instead
        return Optional.empty();
    }

    @Override
    public void commit() throws SourceException, InvalidValueException {
        table.commit();
        Collection<Filter> filters = container.getContainerFilters();
        super.commit();
        for(Filter filter : filters){
            container.addContainerFilter(filter);
        }
        logger.debug("container.size: " + container.size());
    }

    @Override
    protected List<Annotation> getInternalValue() {
        if(container == null || container.getItemIds() == null){
            return null;
        }
        return new ArrayList<>(container.getItemIds());
    }


    @Override
    protected void setInternalValue(List<Annotation> newValue) {

        boolean hasIncludeFilter = typesFilter != null && !typesFilter.isEmpty();
        boolean onlyOneType = hasIncludeFilter && typesFilter.size() == 1;

        if(newValue.isEmpty()){
            Annotation emptyDefaultAnnotation = newInstance();
            newValue.add(emptyDefaultAnnotation );
            if(onlyOneType){
                emptyDefaultAnnotation.setAnnotationType(typesFilter.get(0));
            }
        }
        container = new FilterableListContainer<>(newValue);
        if(hasIncludeFilter){
            container.addContainerFilter(new CdmTermFilter<>("annotationType", typesFilter));
        }
        table.setContainerDataSource(container);
        if(onlyOneType){
            table.setVisibleColumns("text");
        } else {
            table.setVisibleColumns("text", "annotationType");
        }
        table.setEditable(true);
        if(newValue.size() > 1){
            table.setPageLength(2);
        }
    }

    @Override
    protected Component initContent() {
        root.addComponentAsFirst(table);

        newButton.addClickListener(e -> addAnnotation());
        withNewButton(true);
        return root;
    }

    private void addAnnotation() {
        container.addItem(newInstance());
        if(container.size() > 1){
            table.setPageLength(2);
        }
    }

    private Annotation newInstance() {
        return Annotation.NewInstance(null, Language.DEFAULT());
    }

    @Override
    public Class<? extends List<Annotation>> getType() {
        return (Class<? extends List<Annotation>>) new ArrayList<>().getClass();
    }

    public void setAnnotationTypeItemContainer(BeanItemContainer<DefinedTermBase> typeSelectItemContainer) {
        this.typeSelectItemContainer = typeSelectItemContainer;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        setDeepReadOnly(readOnly, table, null);
    }

    public void withNewButton(boolean withNewButton) {
        if(this.withNewButton != withNewButton){
            if(!this.withNewButton){
                root.addComponent(newButton);
            } else {
                root.removeComponent(newButton);
            }
            this.withNewButton = withNewButton;
        }
    }
}