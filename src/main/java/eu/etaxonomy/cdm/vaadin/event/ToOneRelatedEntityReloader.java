/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Field;

import eu.etaxonomy.cdm.hibernate.HibernateProxyHelper;
import eu.etaxonomy.cdm.model.ICdmCacher;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.vaadin.view.name.CachingPresenter;
import eu.etaxonomy.vaadin.component.EntitySupport;

/**
 * The <code>ToOneRelatedEntityReloader</code> helps avoiding <i>java.lang.IllegalStateException: Multiple representations of the same entity</i>
 * in Hibernate sessions.
 *
 *
 * @author a.kohlbecker
 * @since 19.10.2017
 *
 */
public class ToOneRelatedEntityReloader<CDM extends CdmBase> implements ValueChangeListener {

    private static final long serialVersionUID = -1141764783149024788L;

    Field<CDM>  toOneRelatedEntityField;

    CachingPresenter cachingPresenter;

    boolean onSettingReloadedEntity;

    public ToOneRelatedEntityReloader( Field<CDM> toOneRelatedEntityField, CachingPresenter entityCache){
        this.toOneRelatedEntityField = toOneRelatedEntityField;
        this.cachingPresenter = entityCache;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void valueChange(ValueChangeEvent event) {

        // TODO during the view initialization this method is called twice with the same value.
        // for faster view initialization it might make sense to reduce this to only one call.
        // only one call should be sufficient since the same value object is use in both calls
        // whereas i observed that it is a hibnerate proxy during the first call,
        // the second time it is the de-proxied entity which was during the first call inside the proxy.
        // Since both cdm enties are the same object
        // a reduction to one call should not break anything, but at least one call during the initialization
        // is required!

        if(onSettingReloadedEntity){
            // avoid potential loops caused by setValue() below
            return;
        }

        @SuppressWarnings("unchecked")
        CDM value = (CDM)event.getProperty().getValue();
        if(value == null) {
            return;
        }
        value = HibernateProxyHelper.deproxy(value);

        ICdmCacher cache = cachingPresenter.getCache();
        if(cache != null){
            CDM cachedEntity = cache.load(value);
            if(// pure object comparison is not reliable since the entity may have been changed
                cachedEntity.getId() == value.getId() && cachedEntity.getClass() == value.getClass()
                ){
                    onSettingReloadedEntity = true;
                    if(EntitySupport.class.isAssignableFrom(toOneRelatedEntityField.getClass())){
                        ((EntitySupport)toOneRelatedEntityField).replaceEntityValue(cachedEntity);
                    } else {
                        toOneRelatedEntityField.removeValueChangeListener(this);
                        // TODO it would be better to add this as ValueChangeListener to the PropertyDataSource directly instead of listenting at the Field
                        boolean readOnly = toOneRelatedEntityField.getPropertyDataSource().isReadOnly();
                        toOneRelatedEntityField.getPropertyDataSource().setReadOnly(false);
                        toOneRelatedEntityField.setValue(null); // reset to trick equals check in vaadin
                        toOneRelatedEntityField.setValue(cachedEntity);
                        toOneRelatedEntityField.getPropertyDataSource().setReadOnly(readOnly);
                        toOneRelatedEntityField.addValueChangeListener(this);
                    }
                    onSettingReloadedEntity = false;
            }
        } else {
            throw new RuntimeException("The cache must not be null. See loadBeanById() in AbstractCdmEditorPresenter");
        }
    }

}
