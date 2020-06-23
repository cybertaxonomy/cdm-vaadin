/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.model.registration;

import java.util.UUID;

import eu.etaxonomy.cdm.model.occurrence.SpecimenOrObservationBase;
import eu.etaxonomy.cdm.model.term.DefinedTerm;
import eu.etaxonomy.cdm.model.term.TermType;
import eu.etaxonomy.cdm.model.term.TermVocabulary;

/**
 * Terms to be used as kindOfUnit for {@link SpecimenOrObservationBase#setKindOfUnit()}.
 * These terms are specific to the phycobank project but are temporarily necessary in
 * cdmlib-services for {@link SpecimenTypeDesignationDTO}
 *
 *
 * @author a.kohlbecker
 * @since Jun 20, 2017
 *
 */
public class KindOfUnitTerms {

    private static final UUID UUID_SPECIMEN = UUID.fromString("0944290a-1ac1-479b-9826-22d68d9418d1");
    private static final UUID UUID_PUBLISHED_IMAGE = UUID.fromString("2f1aaff2-0af5-468a-890f-6de219f6c6fd");
    private static final UUID UUID_UNPUBLISHED_IMAGE = UUID.fromString("87adcd8d-ce69-4d16-ac35-4e77fb698e00");
    private static final UUID UUID_CULTURE_METABOLIC_INACTIVE = UUID.fromString("fc1ac3b2-fde4-43c0-a100-48053b88c550");

    private static final UUID UUID_KIND_OF_UNIT_VOCAB = UUID.fromString("88b7e7ab-ab99-4760-9b66-c070b98148fc");

    private static DefinedTerm specimen = null;
    private static DefinedTerm publishedImage = null;
    private static DefinedTerm unpublishedImage = null;
    private static DefinedTerm cultureMetabolicInactive = null;

    private static TermVocabulary<DefinedTerm> kindOfUnitVocabulary = null;

    public static DefinedTerm SPECIMEN() {
        if(specimen == null){
            specimen = DefinedTerm.NewInstance(TermType.KindOfUnit, "Specimen", "Specimen", "");
            specimen.setUuid(UUID_SPECIMEN);
        }
        return specimen;
    }

    public static DefinedTerm PUBLISHED_IMAGE() {
        if(publishedImage == null){
            publishedImage = DefinedTerm.NewInstance(TermType.KindOfUnit, "Published image", "Published image", "");
            publishedImage.setUuid(UUID_PUBLISHED_IMAGE);
        }
        return publishedImage;
    }

    public static DefinedTerm UNPUBLISHED_IMAGE() {
        if(unpublishedImage == null){
            unpublishedImage = DefinedTerm.NewInstance(TermType.KindOfUnit, "Unpublished image", "Unpublished image", "");
            unpublishedImage.setUuid(UUID_UNPUBLISHED_IMAGE);
        }
        return unpublishedImage;
    }

    public static DefinedTerm CULTURE_METABOLIC_INACTIVE() {
        if(cultureMetabolicInactive == null){
            cultureMetabolicInactive = DefinedTerm.NewInstance(TermType.KindOfUnit, "Metabolic inactive culture", "Metabolic inactive culture", "");
            cultureMetabolicInactive.setUuid(UUID_CULTURE_METABOLIC_INACTIVE);
        }
        return cultureMetabolicInactive;
    }

    public static TermVocabulary<DefinedTerm> KIND_OF_UNIT_VOCABULARY() {
        if(kindOfUnitVocabulary == null){
            kindOfUnitVocabulary = TermVocabulary.NewInstance(TermType.KindOfUnit);
            kindOfUnitVocabulary.setTitleCache("Registration Kind-of-Units", true);
            kindOfUnitVocabulary.setUuid(UUID_KIND_OF_UNIT_VOCAB);
        }
        return kindOfUnitVocabulary;
    }

}
