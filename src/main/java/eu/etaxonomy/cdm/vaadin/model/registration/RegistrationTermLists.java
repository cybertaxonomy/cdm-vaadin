/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.model.registration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import eu.etaxonomy.cdm.model.name.NameTypeDesignationStatus;
import eu.etaxonomy.cdm.model.name.NomenclaturalStatusType;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignationStatus;
import eu.etaxonomy.cdm.model.term.DefinedTerm;
/**
 * @author a.kohlbecker
 * @since Jun 23, 2017
 *
 */
public class RegistrationTermLists {

    public static final List<DefinedTerm> KIND_OF_UNIT_TERMS = Arrays.asList(new DefinedTerm[]{
            KindOfUnitTerms.SPECIMEN(),
            KindOfUnitTerms.UNPUBLISHED_IMAGE(),
            KindOfUnitTerms.PUBLISHED_IMAGE(),
            KindOfUnitTerms.CULTURE_METABOLIC_INACTIVE()
        });

    public static final List<SpecimenTypeDesignationStatus> SPECIMEN_TYPE_DESIGNATION_STATUS = Arrays.asList(new SpecimenTypeDesignationStatus[]{
            SpecimenTypeDesignationStatus.EPITYPE(),
            SpecimenTypeDesignationStatus.HOLOTYPE(),
            SpecimenTypeDesignationStatus.ISOEPITYPE(),
            SpecimenTypeDesignationStatus.ISOLECTOTYPE(),
            SpecimenTypeDesignationStatus.ISONEOTYPE(),
            SpecimenTypeDesignationStatus.ISOPARATYPE(),
            SpecimenTypeDesignationStatus.ISOSYNTYPE(),
            SpecimenTypeDesignationStatus.ISOTYPE(),
            SpecimenTypeDesignationStatus.LECTOTYPE(),
            SpecimenTypeDesignationStatus.NEOTYPE(),
            SpecimenTypeDesignationStatus.PARALECTOTYPE(),
            SpecimenTypeDesignationStatus.PARANEOTYPE(),
            SpecimenTypeDesignationStatus.PARATYPE(),
            SpecimenTypeDesignationStatus.SECOND_STEP_LECTOTYPE(),
            SpecimenTypeDesignationStatus.SECOND_STEP_NEOTYPE(),
            SpecimenTypeDesignationStatus.SYNTYPE(),
            SpecimenTypeDesignationStatus.TYPE(), // Unknown type category
            SpecimenTypeDesignationStatus.UNSPECIFIC(),

            // TODO add Typus consservandus
            // TODO add Typ. cons. prop.
    });

    public static final List<NameTypeDesignationStatus> NAME_TYPE_DESIGNATION_STATUS = Arrays.asList(new NameTypeDesignationStatus[]{
            NameTypeDesignationStatus.LECTOTYPE()
            // TODO add more see https://dev.e-taxonomy.eu/redmine/issues/6193
    });

    public static final List<NomenclaturalStatusType> NOMENCLATURAL_STATUS_TYPES = Arrays.asList(new NomenclaturalStatusType[]{
            NomenclaturalStatusType.CONSERVED(),
            NomenclaturalStatusType.CONSERVED_PROP(),
            NomenclaturalStatusType.ILLEGITIMATE(),
            NomenclaturalStatusType.INVALID(),
            NomenclaturalStatusType.REJECTED(),
            NomenclaturalStatusType.REJECTED_PROP(),
            NomenclaturalStatusType.UTIQUE_REJECTED(),
            NomenclaturalStatusType.UTIQUE_REJECTED_PROP(),
            NomenclaturalStatusType.ORTHOGRAPHY_CONSERVED(),
            NomenclaturalStatusType.ORTHOGRAPHY_CONSERVED_PROP(),
            NomenclaturalStatusType.ORTHOGRAPHY_REJECTED()
    });


    public static List<UUID> KIND_OF_UNIT_TERM_UUIDS(){
        List<UUID> uuids = new ArrayList<>();
        KIND_OF_UNIT_TERMS.forEach(t -> uuids.add(t.getUuid()));
        return uuids;
    }

    public static List<UUID> SPECIMEN_TYPE_DESIGNATION_STATUS_UUIDS(){
        List<UUID> uuids = new ArrayList<>();
        SPECIMEN_TYPE_DESIGNATION_STATUS.forEach(t -> uuids.add(t.getUuid()));
        return uuids;
    }
    public static List<UUID> NAME_TYPE_DESIGNATION_STATUS_UUIDS(){
        List<UUID> uuids = new ArrayList<>();
        NAME_TYPE_DESIGNATION_STATUS.forEach(t -> uuids.add(t.getUuid()));
        return uuids;
    }
    public static List<UUID> NOMENCLATURAL_STATUS_TYPE_UUIDS(){
        List<UUID> uuids = new ArrayList<>();
        NOMENCLATURAL_STATUS_TYPES.forEach(t -> uuids.add(t.getUuid()));
        return uuids;
    }


}
