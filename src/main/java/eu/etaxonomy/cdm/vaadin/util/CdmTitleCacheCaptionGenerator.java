/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util;

import org.vaadin.viritin.fields.CaptionGenerator;

import eu.etaxonomy.cdm.model.common.IdentifiableEntity;

/**
 * @author a.kohlbecker
 * @since Jun 7, 2017
 *
 */
public final class CdmTitleCacheCaptionGenerator<T extends IdentifiableEntity> implements CaptionGenerator<T> {

    private static final long serialVersionUID = 3151383366731447990L;

    public CdmTitleCacheCaptionGenerator(){
    }

    @Override
    public String getCaption(T option) {
        return option.getTitleCache();
    }
}