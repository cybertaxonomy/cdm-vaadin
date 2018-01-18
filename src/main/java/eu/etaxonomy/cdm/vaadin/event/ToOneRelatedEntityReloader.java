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

import eu.etaxonomy.cdm.cache.EntityCache;
import eu.etaxonomy.cdm.hibernate.HibernateProxyHelper;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.vaadin.view.name.CachingPresenter;

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

        if(!cachingPresenter.isCacheInitialized()){
            // skips as long as the view has not completely loaded the bean
            return;
        }

        EntityCache cache = cachingPresenter.getCache();
        if(cache != null){
            cache.update();
            CDM cachedEntity = cache.findAndUpdate(value);
            if(cachedEntity == null){
                cache.add(value);
            } else if(
                // pure object comparison is not reliable since the entity may have been changed
                cachedEntity.getId() == value.getId() && cachedEntity.getClass() == value.getClass()
                ){
                    onSettingReloadedEntity = true;
                    toOneRelatedEntityField.removeValueChangeListener(this);
                    toOneRelatedEntityField.setValue(null); // reset to trick equals check in vaadin
                    toOneRelatedEntityField.setValue(cachedEntity);
                    toOneRelatedEntityField.addValueChangeListener(this);
                    onSettingReloadedEntity = false;
            }
        } else {
            throw new RuntimeException("The cache must not be null. See loadBeanById() in AbstractCdmEditorPresenter");
        }
    }

}
