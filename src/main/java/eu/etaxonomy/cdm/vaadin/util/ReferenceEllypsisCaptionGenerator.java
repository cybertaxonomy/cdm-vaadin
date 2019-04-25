/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util;

import org.vaadin.viritin.fields.CaptionGenerator;
import org.vaadin.viritin.fields.LazyComboBox;

import eu.etaxonomy.cdm.format.ReferenceEllypsisFormatter;
import eu.etaxonomy.cdm.format.ReferenceEllypsisFormatter.LabelType;
import eu.etaxonomy.cdm.model.reference.Reference;

/**
 * A <code>CaptionGenerator</code> for {@link Reference References} which creates ellypsed captions.
 *
 * @author a.kohlbecker
 */
public class ReferenceEllypsisCaptionGenerator implements CaptionGenerator<Reference> {

    private static final long serialVersionUID = -5820263463222845068L;

    private LazyComboBox<Reference> combobox;

    private ReferenceEllypsisFormatter ellypsisFormatter;

    public ReferenceEllypsisCaptionGenerator(LabelType labelType, LazyComboBox<Reference> combobox){
        ellypsisFormatter = new ReferenceEllypsisFormatter(labelType);
        this.combobox = combobox;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getCaption(Reference reference) {
        String preserveString = combobox.getCurrentFilter();
        if(preserveString == null){
            preserveString = "";
        }
        return ellypsisFormatter.ellypsis(reference, preserveString);
    }

}
