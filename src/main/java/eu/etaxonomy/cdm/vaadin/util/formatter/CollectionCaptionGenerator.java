/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util.formatter;

import org.vaadin.viritin.fields.CaptionGenerator;

import eu.etaxonomy.cdm.model.occurrence.Collection;

/**
 * @author a.kohlbecker
 */
public final class CollectionCaptionGenerator implements CaptionGenerator<Collection> {

    private static final long serialVersionUID = 3151383366731447990L;

    public CollectionCaptionGenerator(){
    }

    @Override
    public String getCaption(Collection option) {
        //since #10848 we only use titleCache
        return option.getTitleCache();
    }
}