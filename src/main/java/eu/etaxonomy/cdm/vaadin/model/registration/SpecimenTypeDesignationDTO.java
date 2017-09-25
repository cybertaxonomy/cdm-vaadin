/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.model.registration;

import java.net.URI;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;

import eu.etaxonomy.cdm.api.utility.DerivedUnitConversionException;
import eu.etaxonomy.cdm.api.utility.DerivedUnitConverter;
import eu.etaxonomy.cdm.hibernate.HibernateProxyHelper;
import eu.etaxonomy.cdm.model.common.IdentifiableSource;
import eu.etaxonomy.cdm.model.media.Media;
import eu.etaxonomy.cdm.model.media.MediaRepresentation;
import eu.etaxonomy.cdm.model.media.MediaRepresentationPart;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignation;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignationStatus;
import eu.etaxonomy.cdm.model.occurrence.Collection;
import eu.etaxonomy.cdm.model.occurrence.DerivationEvent;
import eu.etaxonomy.cdm.model.occurrence.DerivationEventType;
import eu.etaxonomy.cdm.model.occurrence.DerivedUnit;
import eu.etaxonomy.cdm.model.occurrence.MediaSpecimen;
import eu.etaxonomy.cdm.model.occurrence.SpecimenOrObservationType;
import eu.etaxonomy.cdm.model.reference.Reference;

/**
 * A DTO which is use in the context of the {@link SpecimenTypeDesignationWorkingSetDTO} which is backed up
 * <code>SpecimenTypeDesignation.typeSpecimen.derivedFrom.type</code> object graph.
 * <p>
 * The contained {@link DerivedUnit} either is a {@link MediaSpecimen} with or a {@link DerivedUnit}, depending on the
 * "kind of unit" which is defined by the associated <code>DerivationEvent.type</code>:
 *
 * <ul>
 * <li>{@link DerivationEventType#GATHERING_IN_SITU()} -&gt; {@link DerivedUnit}</li>
 * <li>{@link DerivationEventTypes#CULTURE_METABOLIC_INACTIVE()} -&gt; {@link DerivedUnit}</li>
 * <li>{@link DerivationEventTypes#UNPUBLISHED_IMAGE()} -&gt; {@link MediaSpecimen}</li>
 * <li>{@link DerivationEventTypes#PUBLISHED_IMAGE()} -&gt; {@link MediaSpecimen}</li>
 * </ul>
 *
 * @author a.kohlbecker
 * @since Jun 22, 2017
 *
 */
public class SpecimenTypeDesignationDTO {

    SpecimenTypeDesignation std;

    /**
     * @return the std
     */
    public SpecimenTypeDesignation asSpecimenTypeDesignation() {
        return std;
    }

    /**
     * Creates an new new instance of SpecimenTypeDesignationDTO which is backed up
     * by an newly instantiated <code>SpecimenTypeDesignation.typeSpecimen.derivedFrom.type</code> object graph.
     */
    public SpecimenTypeDesignationDTO(){
        this(SpecimenTypeDesignation.NewInstance());
    }

    /**
     * Creates a new instance of SpecimenTypeDesignationDTO.
     *
     * The constructor assures that <code>SpecimenTypeDesignation.typeSpecimen.derivedFrom.type</code> are never null.
     * That is in case the supplied std parameter or if any of the above listed properties is null the missing instances
     * are created and added to the object graph.
     *
     * @param std
     */
    public SpecimenTypeDesignationDTO(SpecimenTypeDesignation std) {
        this.std = std;
        if(std.getTypeSpecimen() == null){
            DerivedUnit derivedUnit = DerivedUnit.NewInstance(SpecimenOrObservationType.PreservedSpecimen);
            std.setTypeSpecimen(derivedUnit);
        }
        if(std.getTypeSpecimen().getDerivedFrom() == null){
            DerivationEvent derivedFrom = DerivationEvent.NewInstance(DerivationEventType.GATHERING_IN_SITU());
            std.getTypeSpecimen().setDerivedFrom(derivedFrom);
        }
    }

    public DerivationEventType getDerivationEventType(){
        return std.getTypeSpecimen().getDerivedFrom().getType();
    }

    public void setDerivationEventType(DerivationEventType derivationEventType) throws DerivedUnitConversionException{

        std.getTypeSpecimen().getDerivedFrom().setType(derivationEventType);

        Class<? extends DerivedUnit> requiredSpecimenType = specimenTypeFor(derivationEventType);

        if(!requiredSpecimenType.equals(std.getTypeSpecimen())){

            DerivedUnit convertedSpecimen;

            if(requiredSpecimenType.equals(MediaSpecimen.class)){
                DerivedUnitConverter<MediaSpecimen> converter = new DerivedUnitConverter<MediaSpecimen> (std.getTypeSpecimen());
                convertedSpecimen = converter.convertTo((Class<MediaSpecimen>)requiredSpecimenType, specimenOrObservationTypeFor(derivationEventType));
            } else {
                DerivedUnitConverter<DerivedUnit> converter = new DerivedUnitConverter<DerivedUnit> (std.getTypeSpecimen());
                convertedSpecimen = converter.convertTo((Class<DerivedUnit>)requiredSpecimenType, specimenOrObservationTypeFor(derivationEventType));
            }

            std.setTypeSpecimen(convertedSpecimen);
        }
    }


    /**
     * See constructor doc.
     *
     * @param derivationEventType
     * @return
     *      either <code>DerivedUnit</code> or <code>MediaSpecimen</code> never null.
     *  <code>DerivedUnit</code> is the fall back return value.
     */
    private Class<?  extends DerivedUnit> specimenTypeFor(DerivationEventType derivationEventType) {
        UUID detUuid = derivationEventType.getUuid();

        if(detUuid.equals(DerivationEventType.GATHERING_IN_SITU().getUuid())) {
            return DerivedUnit.class;
        }
        if(detUuid.equals(DerivationEventTypes.CULTURE_METABOLIC_INACTIVE().getUuid())) {
            return DerivedUnit.class;
        }
        if(detUuid.equals(DerivationEventTypes.PUBLISHED_IMAGE().getUuid())) {
            return MediaSpecimen.class;
        }
        if(detUuid.equals(DerivationEventTypes.UNPUBLISHED_IMAGE().getUuid())) {
            return MediaSpecimen.class;
        }
        return DerivedUnit.class;
    }

    private SpecimenOrObservationType specimenOrObservationTypeFor(DerivationEventType derivationEventType) {

        UUID detUuid = derivationEventType.getUuid();

        if(detUuid.equals(DerivationEventType.GATHERING_IN_SITU().getUuid())) {
            return SpecimenOrObservationType.PreservedSpecimen;
        }
        if(detUuid.equals(DerivationEventTypes.CULTURE_METABOLIC_INACTIVE().getUuid())) {
            return SpecimenOrObservationType.LivingSpecimen;
        }
        if(detUuid.equals(DerivationEventTypes.PUBLISHED_IMAGE().getUuid())) {
            return SpecimenOrObservationType.StillImage;
        }
        if(detUuid.equals(DerivationEventTypes.UNPUBLISHED_IMAGE().getUuid())) {
            return SpecimenOrObservationType.StillImage;
        }
       return SpecimenOrObservationType.PreservedSpecimen;

    }

    public SpecimenTypeDesignationStatus getTypeStatus(){
        return HibernateProxyHelper.deproxy(std.getTypeStatus(), SpecimenTypeDesignationStatus.class);
    }

    public void setTypeStatus(SpecimenTypeDesignationStatus typeStatus){
        std.setTypeStatus(typeStatus);
    }

    public Collection getCollection(){
        return std.getTypeSpecimen().getCollection();
    }

    public void setCollection(Collection collection){
        std.getTypeSpecimen().setCollection(collection);
    }

    public String getAccessionNumber(){
        return std.getTypeSpecimen().getAccessionNumber();
    }

    public void setAccessionNumber(String accessionNumber){
        std.getTypeSpecimen().setAccessionNumber(accessionNumber);
    }

    public URI getMediaUri(){
        if(checkMediaSpecimen()){
            MediaRepresentationPart part = findMediaRepresentationPart();
            if(part != null){
                return part.getUri();
            }
        }
        return null;
    }

    public void setMediaUri(URI mediaUri){
        if(checkMediaSpecimen()){
            MediaRepresentationPart part = findOrMakeMediaRepresentationPart();
            part.setUri(mediaUri);
        }
    }

    public Reference getMediaSpecimenReference(){
        if(checkMediaSpecimen()){
            IdentifiableSource source = findMediaSpecimenIdentifieableSource();
            if(source != null){
                return source.getCitation();
            }
        }
        return null;
    }

    public void setMediaSpecimenReference(Reference reference){
        if(checkMediaSpecimen()){
            IdentifiableSource source = findOrMakeMediaSpecimenIdentifieableSource();
            source.setCitation(reference);
        }
    }

    public String getMediaSpecimenReferenceDetail(){
        if(checkMediaSpecimen()){
            IdentifiableSource source = findMediaSpecimenIdentifieableSource();
            if(source != null){
                return source.getCitationMicroReference();
            }
        }
        return null;
    }

    public void setMediaSpecimenReferenceDetail(String referenceDetail){
        if(checkMediaSpecimen()){
            IdentifiableSource source = findOrMakeMediaSpecimenIdentifieableSource();
            source.setCitationMicroReference(referenceDetail);
        }
    }

    /**
     * @return
     */
    private IdentifiableSource findMediaSpecimenIdentifieableSource() {
        IdentifiableSource source = null;
        Media media = findMediaSpecimen().getMediaSpecimen();
        if(media != null){
            // FIXME use marker type to tag the MediaSpecimenReference and make sure that there is always only one.
            if(!CollectionUtils.isEmpty(media.getSources())){
                source = media.getSources().iterator().next();
            }
        }
        return source;
    }

    /**
     *
     */
    private MediaRepresentationPart findMediaRepresentationPart() {

        Media media = findMediaSpecimen().getMediaSpecimen();
        if(media != null){
            if(!CollectionUtils.isEmpty(media.getRepresentations())){
                MediaRepresentation repr = media.getRepresentations().iterator().next();
                if(!CollectionUtils.isEmpty(repr.getParts())) {
                    MediaRepresentationPart part = repr.getParts().iterator().next();
                    return part;
                }
            }
        }
        return null;
    }

    private MediaRepresentationPart findOrMakeMediaRepresentationPart() {

        if(findMediaSpecimen().getMediaSpecimen() == null){
            findMediaSpecimen().setMediaSpecimen(Media.NewInstance());
        }
        Media media = findMediaSpecimen().getMediaSpecimen();
        if(CollectionUtils.isEmpty(media.getRepresentations())){
            media.addRepresentation(MediaRepresentation.NewInstance());
        }
        MediaRepresentation repr = media.getRepresentations().iterator().next();
        if(CollectionUtils.isEmpty(repr.getParts())) {
            repr.addRepresentationPart(MediaRepresentationPart.NewInstance(null, null));
        }
        MediaRepresentationPart part = repr.getParts().iterator().next();

        return part;
    }

    /**
     * @return
     */
    private IdentifiableSource findOrMakeMediaSpecimenIdentifieableSource() {
        IdentifiableSource source ;
        Media media = findMediaSpecimen().getMediaSpecimen();
        if(media == null){
            findMediaSpecimen().setMediaSpecimen(Media.NewInstance());
        }
        if(CollectionUtils.isEmpty(media.getSources())){
           source = IdentifiableSource.NewPrimaryMediaSourceInstance(null, null);
           media.addSource(source);
        } else {
            source = media.getSources().iterator().next();
        }
        return source;
    }

    /**
     * @return
     */
    public boolean checkMediaSpecimen() {
        return findMediaSpecimen() != null;
    }

    /**
     * @return
     */
    private MediaSpecimen findMediaSpecimen() {
        DerivedUnit sp = std.getTypeSpecimen();
        if(MediaSpecimen.class.isAssignableFrom(sp.getClass())){
            return (MediaSpecimen) sp;
        }
        return null;
    }

}
