/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.ui;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.etaxonomy.cdm.model.common.AnnotationType;
import eu.etaxonomy.cdm.model.name.NomenclaturalCode;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.reference.ReferenceType;
import eu.etaxonomy.cdm.persistence.permission.Role;
import eu.etaxonomy.cdm.vaadin.permission.RolesAndPermissions;

/**
 * @author a.kohlbecker
 * @since Jan 12, 2018
 *
 */
public class RegistrationUIDefaults {

    public static final EnumSet<ReferenceType> PRINTPUB_REFERENCE_TYPES = EnumSet.of(
            ReferenceType.Article,
            ReferenceType.Book,
            ReferenceType.InProceedings,
            ReferenceType.Journal,
            ReferenceType.PrintSeries,
            ReferenceType.Proceedings,
            ReferenceType.BookSection,
            ReferenceType.Section,
            ReferenceType.Thesis);

    public static final EnumSet<ReferenceType> NOMECLATURAL_PUBLICATION_UNIT_TYPES = EnumSet.of(
            ReferenceType.Article,
            ReferenceType.Book,
            ReferenceType.BookSection,
            ReferenceType.Thesis,
            ReferenceType.Section
            );


    public static final EnumSet<ReferenceType> MEDIA_REFERENCE_TYPES = EnumSet.of(
            // same as for print
            ReferenceType.Article,
            ReferenceType.Book,
            ReferenceType.InProceedings,
            ReferenceType.Journal,
            ReferenceType.PrintSeries,
            ReferenceType.Proceedings,
            ReferenceType.Section,
            ReferenceType.Thesis,
            // additional
            ReferenceType.WebPage,
            ReferenceType.Database,
            ReferenceType.CdDvd,
            ReferenceType.Report
            );

    public static final Set<Rank> RANK_SET_COMPACT = new HashSet<>(Arrays.asList(
            // see https://dev.e-taxonomy.eu/redmine/issues/6732
            Rank.GENUS(),
            Rank.SPECIES(),
            Rank.SUBSPECIES(),
            Rank.VARIETY(),
            Rank.FORM()
            ));

    /**
     *  TODO make configurable as preset and in TaxonNameEditor
     */
    public static final NomenclaturalCode NOMENCLATURAL_CODE = NomenclaturalCode.ICNAFP;

    /**
     * should be configurable per UI
     */
    public static final AnnotationType[] EDITABLE_ANOTATION_TYPES = new AnnotationType[]{AnnotationType.INTERNAL()};

    public static final String ERROR_CONTACT_MESSAGE_LINE = "Please contact <a href=\"mailto:editsupport@bgbm.org\">editsupport@bgbm.org</a> for support and more information.";

    public static final List<Role> COLLECTION_EDITOR_SUB_COLLECTION_VISIBILITY_RESTRICTION = Arrays.asList(Role.ROLE_ADMIN, RolesAndPermissions.ROLE_CURATION);

    public static NumberFormat NUMBER_FORMAT_OVERRIDE = new DecimalFormat("#");

}
