/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.ui;

import java.util.EnumSet;

import eu.etaxonomy.cdm.model.reference.ReferenceType;

/**
 * @author a.kohlbecker
 * @since Jan 12, 2018
 *
 */
public class RegistrationUIDefaults {

    public static final EnumSet<ReferenceType> REFERENCE_TYPES = EnumSet.of(
            ReferenceType.Article,
            ReferenceType.Book,
            ReferenceType.InProceedings,
            ReferenceType.Journal,
            ReferenceType.PrintSeries,
            ReferenceType.Proceedings,
            ReferenceType.Section,
            ReferenceType.Thesis);

}
