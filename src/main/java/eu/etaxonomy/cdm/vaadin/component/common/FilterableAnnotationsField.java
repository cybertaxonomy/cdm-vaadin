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

import org.vaadin.viritin.FilterableListContainer;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.TextArea;

import eu.etaxonomy.cdm.model.common.Annotation;
import eu.etaxonomy.cdm.model.common.AnnotationType;
import eu.etaxonomy.cdm.model.common.DefinedTermBase;
import eu.etaxonomy.cdm.model.common.Language;
import eu.etaxonomy.cdm.vaadin.util.converter.SetToListConverter;
import eu.etaxonomy.cdm.vaadin.util.filter.CdmTermFilter;
import eu.etaxonomy.vaadin.component.CompositeCustomField;

/**
 * @author a.kohlbecker
 * @since Jun 20, 2018
 *
 */
public class FilterableAnnotationsField extends CompositeCustomField<List<Annotation>> {

    private static final long serialVersionUID = -8258550787601028813L;

    Class<List<Annotation>> type = (Class<List<Annotation>>)new ArrayList<Annotation>().getClass();

    private CssLayout root = new CssLayout();

    private Table table = new Table();

    private List<AnnotationType> typesFilter = null;

    private Annotation emptyDefaultAnnotation = Annotation.NewInstance(null, Language.DEFAULT());

    private BeanItemContainer<DefinedTermBase> typeSelectItemContainer;

    private FilterableListContainer<Annotation> container;

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
                    ListSelect select = new ListSelect();
                    select.setContainerDataSource(typeSelectItemContainer);
                    select.setWidth(100, Unit.PIXELS);
                    select.setRows(1);
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
        // holds a Container instead // TODO can this cause a NPE?
        return null;
    }

    @Override
    public void commit() throws SourceException, InvalidValueException {
        table.commit();
        Collection<Filter> filters = container.getContainerFilters();
        super.commit();
        for(Filter filter : filters){
            container.addContainerFilter(filter);
        }
        System.err.println(container.size());
    }



    /**
     * {@inheritDoc}
     */
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
            newValue.add(emptyDefaultAnnotation);
            if(onlyOneType){
                emptyDefaultAnnotation.setAnnotationType(typesFilter.get(0));
            }
        }
        container = new FilterableListContainer<Annotation>(newValue);
        if(hasIncludeFilter){
            container.addContainerFilter(new CdmTermFilter<AnnotationType>("annotationType", typesFilter));
        }
        table.setContainerDataSource(container);
        if(onlyOneType){
            table.setVisibleColumns("text");
        } else {
            table.setVisibleColumns("text", "annotationType");
        }
        table.setEditable(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Component initContent() {
        root.addComponent(table);
        return root;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Class<List<Annotation>> getType() {
        return type;
    }

    /**
     * @param buildTermItemContainer
     */
    public void setAnnotationTypeItemContainer(BeanItemContainer<DefinedTermBase> typeSelectItemContainer) {
        this.typeSelectItemContainer = typeSelectItemContainer;
    }


}
