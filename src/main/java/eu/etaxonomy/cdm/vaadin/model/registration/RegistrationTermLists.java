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

import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignationStatus;
import eu.etaxonomy.cdm.model.occurrence.DerivationEventType;
/**
 * @author a.kohlbecker
 * @since Jun 23, 2017
 *
 */
public class RegistrationTermLists {

    public static final List<DerivationEventType> DERIVATION_EVENT_TYPE = Arrays.asList(new DerivationEventType[]{
            DerivationEventType.GATHERING_IN_SITU(),
            DerivationEventTypes.UNPUBLISHED_IMAGE(),
            DerivationEventTypes.PUBLISHED_IMAGE(),
            DerivationEventTypes.CULTURE_METABOLIC_INACTIVE()
        });

    public static final List<SpecimenTypeDesignationStatus> SPECIMEN_TYPE_DESIGNATION_STATUS = Arrays.asList(new SpecimenTypeDesignationStatus[]{
            SpecimenTypeDesignationStatus.SYNTYPE(),
            SpecimenTypeDesignationStatus.TYPE(),
            SpecimenTypeDesignationStatus.HOLOTYPE(),
            SpecimenTypeDesignationStatus.LECTOTYPE(),
            SpecimenTypeDesignationStatus.SECOND_STEP_LECTOTYPE(),
            SpecimenTypeDesignationStatus.NEOTYPE(),
            SpecimenTypeDesignationStatus.EPITYPE()
            // TODO add Typus consservandus
            // TODO add Typ. cons. prop.
    });



    public static List<UUID> DERIVATION_EVENT_TYPE_UUIDS(){
        List<UUID> uuids = new ArrayList<>();
        DERIVATION_EVENT_TYPE.forEach(t -> uuids.add(t.getUuid()));
        return uuids;
    }

    public static List<UUID> SPECIMEN_TYPE_DESIGNATION_STATUS_UUIDS(){
        List<UUID> uuids = new ArrayList<>();
        SPECIMEN_TYPE_DESIGNATION_STATUS.forEach(t -> uuids.add(t.getUuid()));
        return uuids;
    }


}
