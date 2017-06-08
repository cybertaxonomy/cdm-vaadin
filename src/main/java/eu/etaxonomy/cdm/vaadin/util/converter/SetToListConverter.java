/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util.converter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.vaadin.data.util.converter.Converter;

/**
 * @author a.kohlbecker
 * @since Jun 7, 2017
 *
 */
public final class SetToListConverter<V> implements Converter<List<V>, Set<V>> {

    private static final long serialVersionUID = -4453200532452354378L;

    @Override
    public Set<V> convertToModel(List<V> value, Class<? extends Set<V>> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        if(value != null){
            Set<V> set = new HashSet<>(value.size());
            set.addAll(value);
            return set;
        }
        return null;
    }

    @Override
    public List<V> convertToPresentation(Set<V> value, Class<? extends List<V>> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        if(value != null){
            List<V> list = new ArrayList<V>(value.size());
            list.addAll(value);
            return list;
        }
        return null;
    }

    @Override
    public Class<Set<V>> getModelType() {
        return ((Class)Set.class);
    }

    @Override
    public Class<List<V>> getPresentationType() {
        return ((Class)List.class);
    }
}