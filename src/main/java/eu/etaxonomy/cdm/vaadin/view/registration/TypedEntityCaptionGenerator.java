/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import org.vaadin.viritin.fields.CaptionGenerator;

import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.ref.TypedEntityReference;

/**
 * @author a.kohlbecker
 * @since Dec 12, 2018
 *
 */
public class TypedEntityCaptionGenerator<T extends CdmBase> implements CaptionGenerator<TypedEntityReference<T>> {

    private static final long serialVersionUID = 1312587195614966511L;

    @Override
    public String getCaption(TypedEntityReference<T> option) {
        return option.getLabel();
    }

}
