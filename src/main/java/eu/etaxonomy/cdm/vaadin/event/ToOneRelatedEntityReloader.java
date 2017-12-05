/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event;

import org.vaadin.viritin.fields.LazyComboBox;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;

import eu.etaxonomy.cdm.cache.EntityCache;
import eu.etaxonomy.cdm.hibernate.HibernateProxyHelper;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.vaadin.view.name.CachingPresenter;

/**
 *
 * @author a.kohlbecker
 * @since 19.10.2017
 *
 */
public class ToOneRelatedEntityReloader<CDM extends CdmBase> implements ValueChangeListener {

    private static final long serialVersionUID = -1141764783149024788L;

    LazyComboBox<CDM>  toOneRelatedEntityField;

    CachingPresenter cachingPresenter;

    public ToOneRelatedEntityReloader( LazyComboBox<CDM> toOneRelatedEntityField, CachingPresenter entityCache){
        this.toOneRelatedEntityField = toOneRelatedEntityField;
        this.cachingPresenter = entityCache;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void valueChange(ValueChangeEvent event) {


        CDM value = (CDM)event.getProperty().getValue();
        if(value == null) {
            return;
        }
        value = HibernateProxyHelper.deproxy(value);

        EntityCache cache = cachingPresenter.getCache();
        if(cache != null){
            cache.update();
            CDM cachedEntity = cache.find(value);
            if(cachedEntity == null){
                cache.add(value);
            } else if(cachedEntity != value){
                toOneRelatedEntityField.removeValueChangeListener(this);
                toOneRelatedEntityField.setValue(null); // reset to trick equals check in vaadin
                toOneRelatedEntityField.setValue(cachedEntity);
                toOneRelatedEntityField.addValueChangeListener(this);

            }
        }
    }

}
