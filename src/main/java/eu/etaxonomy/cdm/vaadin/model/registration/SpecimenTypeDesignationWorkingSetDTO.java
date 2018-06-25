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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.Partial;

import eu.etaxonomy.cdm.model.agent.AgentBase;
import eu.etaxonomy.cdm.model.common.Annotation;
import eu.etaxonomy.cdm.model.common.Language;
import eu.etaxonomy.cdm.model.common.LanguageString;
import eu.etaxonomy.cdm.model.common.VersionableEntity;
import eu.etaxonomy.cdm.model.location.NamedArea;
import eu.etaxonomy.cdm.model.location.Point;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignation;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.occurrence.FieldUnit;
import eu.etaxonomy.cdm.model.occurrence.GatheringEvent;
import eu.etaxonomy.cdm.model.reference.Reference;

/**
 * @author a.kohlbecker
 * @since Jun 16, 2017
 *
 */
public class SpecimenTypeDesignationWorkingSetDTO<OWNER extends VersionableEntity> {

    FieldUnit fieldUnit;

    VersionableEntity baseEntity;

    /**
     * List of all SpecimenTypeDesignation that have been loaded into the
     * DTO. By comparing this list with <code>specimenTypeDesignations</code>
     * it is possible to find those that have been deleted.
     */
    List<SpecimenTypeDesignation> specimenTypeDesignationsLoaded = new ArrayList<>();

    List<SpecimenTypeDesignationDTO> specimenTypeDesignationsDTOs = new ArrayList<>();

    OWNER owner;

    private Reference citation;

    private TaxonName typifiedName;

    /**
     *
     * @param owner
     * @param baseEntity
     * @param specimenTypeDesignations can be <code>null</code>
     */
    public SpecimenTypeDesignationWorkingSetDTO(OWNER owner, VersionableEntity baseEntity, List<SpecimenTypeDesignation> specimenTypeDesignations, Reference citation, TaxonName typifiedName) {
        super();
        this.owner = owner;
        this.baseEntity = baseEntity;
        if(citation == null){
            throw new NullPointerException("citation must not be null");
        }
        if(typifiedName == null){
            throw new NullPointerException("typifiedName must not be null");
        }
        this.citation = citation;
        this.typifiedName = typifiedName;
        if(baseEntity instanceof FieldUnit){
            this.fieldUnit = (FieldUnit) baseEntity;
            if(fieldUnit.getGatheringEvent() == null){
                fieldUnit.setGatheringEvent(GatheringEvent.NewInstance());
            }
        }
        if(specimenTypeDesignations != null){
            specimenTypeDesignationsLoaded = specimenTypeDesignations;
            specimenTypeDesignations.forEach(std -> specimenTypeDesignationsDTOs.add(new SpecimenTypeDesignationDTO(std)));
        }
    }

    /**
     * @param reg
     * @param newfieldUnit
     * @param citationEntityID
     * @param typifiedNameEntityID
     */
    public SpecimenTypeDesignationWorkingSetDTO(OWNER reg, FieldUnit newfieldUnit, Reference citation, TaxonName typifiedName) {
        this(reg, newfieldUnit, null, citation, typifiedName);
    }

    /**
     * @return the fieldUnit
     *      <code>null</code> if the base baseEntity is a not a fieldUnit
     */
    public FieldUnit getFieldUnit() {
        return fieldUnit;
    }


    /**
     *
     * @return the baseEntity
     *   <code>null</code> if the base baseEntity is a fieldUnit
     */
    public VersionableEntity getBaseEntity() {
        return baseEntity;
    }

    /**
     * @return the typeDesignation entities managed in this workingset
     */
    protected List<SpecimenTypeDesignation> getSpecimenTypeDesignations() {
        List<SpecimenTypeDesignation> specimenTypeDesignations = new ArrayList(specimenTypeDesignationsDTOs.size());
        for(SpecimenTypeDesignationDTO dto : specimenTypeDesignationsDTOs){
            specimenTypeDesignations.add(dto.asSpecimenTypeDesignation());
        }
        return specimenTypeDesignations;
    }

    public List<SpecimenTypeDesignationDTO> getSpecimenTypeDesignationDTOs(){
        return specimenTypeDesignationsDTOs;
    }

    /**
     * The {@link VersionableEntity} which contains the DerivedUnit in this working set.
     * This can be for example a {@link Registration} entity
     *
     * @return
     */
    public OWNER getOwner() {
        return owner;
    }

    // ====== FieldUnit Wrapper methods ====== //


    public String getFieldNumber() {
        return fieldUnit.getFieldNumber();
    }

    public void setFieldNumber(String fieldNumber) {
        this.fieldUnit.setFieldNumber(fieldNumber);
    }

    public String getFieldNotes() {
        return fieldUnit.getFieldNotes();
    }

    // ====== GateringEvent Wrapper methods ====== //

    public String getLocality(){
        if(fieldUnit.getGatheringEvent().getLocality() != null){
            return fieldUnit.getGatheringEvent().getLocality().getText();
        }
        return null;
    }

    public void setLocality(String locality){
        fieldUnit.getGatheringEvent().setLocality(
                LanguageString.NewInstance(locality, Language.DEFAULT())
                );
    }
    public NamedArea getCountry() {
        return fieldUnit.getGatheringEvent().getCountry();
    }

    public void setCountry(NamedArea country) {
        fieldUnit.getGatheringEvent().setCountry(country);
    }

    public Point getExactLocation() {
        return fieldUnit.getGatheringEvent().getExactLocation();
    }

    public void setExactLocation(Point exactLocation) {
        fieldUnit.getGatheringEvent().setExactLocation(exactLocation);
    }

    public Integer getAbsoluteElevation() {
        return fieldUnit.getGatheringEvent().getAbsoluteElevation();
    }

    public void setAbsoluteElevation(Integer absoluteElevation) {
        fieldUnit.getGatheringEvent().setAbsoluteElevation(absoluteElevation);
    }

    public Integer getAbsoluteElevationMax() {
        return fieldUnit.getGatheringEvent().getAbsoluteElevationMax();
    }

    public void setAbsoluteElevationMax(Integer absoluteElevationMax) {
        fieldUnit.getGatheringEvent().setAbsoluteElevationMax(absoluteElevationMax);
    }

    public String getAbsoluteElevationText() {
        return fieldUnit.getGatheringEvent().getAbsoluteElevationText();
    }

    public void setAbsoluteElevationText(String absoluteElevationText) {
        fieldUnit.getGatheringEvent().setAbsoluteElevationText(absoluteElevationText);
    }

    public Double getDistanceToWaterSurface() {
        return fieldUnit.getGatheringEvent().getDistanceToWaterSurface();
    }

    public void setDistanceToWaterSurface(Double distanceToWaterSurface) {
        fieldUnit.getGatheringEvent().setDistanceToWaterSurface(distanceToWaterSurface);
    }

    public Double getDistanceToWaterSurfaceMax() {
        return fieldUnit.getGatheringEvent().getDistanceToWaterSurfaceMax();
    }

    public void setDistanceToWaterSurfaceMax(Double distanceToWaterSurfaceMax) {
        fieldUnit.getGatheringEvent().setDistanceToWaterSurfaceMax(distanceToWaterSurfaceMax);
    }

    public String getDistanceToWaterSurfaceText() {
        return fieldUnit.getGatheringEvent().getDistanceToWaterSurfaceText();
    }

    public void setDistanceToWaterSurfaceText(String distanceToWaterSurfaceText) {
        fieldUnit.getGatheringEvent().setDistanceToWaterSurfaceText(distanceToWaterSurfaceText);
    }


    public Double getDistanceToGround() {
        return fieldUnit.getGatheringEvent().getDistanceToGround();
    }

    public void setDistanceToGround(Double distanceToGround) {
        fieldUnit.getGatheringEvent().setDistanceToGround(distanceToGround);
    }

    public Double getDistanceToGroundMax() {
        return fieldUnit.getGatheringEvent().getDistanceToGroundMax();
    }

    public void setDistanceToGroundMax(Double distanceToGroundMax) {
        fieldUnit.getGatheringEvent().setDistanceToGroundMax(distanceToGroundMax);
    }

    public String getDistanceToGroundText() {
        return fieldUnit.getGatheringEvent().getDistanceToGroundText();
    }

    public AgentBase getCollector(){
        return fieldUnit.getGatheringEvent().getActor();
    }

    public void setCollector(AgentBase collector){
        fieldUnit.getGatheringEvent().setActor(collector);
    }

    public void setDistanceToGroundText(String distanceToGroundText) {
        fieldUnit.getGatheringEvent().setDistanceToWaterSurfaceText(distanceToGroundText);
    }

    /**
     * WARNING: This method returns only one of the possibly multiple areas which can
     * be hold by the GatheringEvent.
     *
     * @return
     */
    public NamedArea getCollectingArea() {
        try {
            return fieldUnit.getGatheringEvent().getCollectingAreas().iterator().next();
        } catch (Exception e){
            return null;
        }
    }

    public void setCollectingArea(NamedArea collectingArea) throws Exception {
        if(fieldUnit.getGatheringEvent().getCollectingAreas().size() > 1){
            throw new Exception("The GatheringEvent has multiple collectingAreas, use addCollectingArea() instead");
        }
        fieldUnit.getGatheringEvent().getCollectingAreas().clear();
        fieldUnit.getGatheringEvent().addCollectingArea(collectingArea);

    }

    public Set<NamedArea> getCollectingAreas() {
       return fieldUnit.getGatheringEvent().getCollectingAreas();
    }

    public Partial getGatheringDate(){
        return fieldUnit.getGatheringEvent().getGatheringDate();
    }

    public void setGatheringDate(Partial gatheringDate){
        fieldUnit.getGatheringEvent().setGatheringDate(gatheringDate);
    }

    /**
     * @return the citation
     */
    public Reference getCitation() {
        return citation;
    }

    /**
     * @param citation the citation to set
     */
    public void setCitation(Reference citation) {
        this.citation = citation;
    }

    /**
     * @return the typifiedName
     */
    public TaxonName getTypifiedName() {
        return typifiedName;
    }

    /**
     * @param typifiedName the typifiedName to set
     */
    public void setTypifiedName(TaxonName typifiedName) {
        this.typifiedName = typifiedName;
    }

    /**
     *
     * @return the set of SpecimenTypeDesignation that haven been deleted from the <code>SpecimenTypeDesignationWorkingSetDTO</code>.
     */
    public Set<SpecimenTypeDesignation> deletedSpecimenTypeDesignations() {
        Set<SpecimenTypeDesignation> deletedEntities = new HashSet<>(specimenTypeDesignationsLoaded);
        deletedEntities.removeAll(getSpecimenTypeDesignations());
        return deletedEntities;
    }

    public Set<Annotation> getAnnotations() {
        if(fieldUnit != null){
            return fieldUnit.getAnnotations();
        } else {
            return null;
        }
    }

    public void setAnnotations(Set<Annotation> annotations) {

        if(fieldUnit != null){
            List<Annotation> currentAnnotations = new ArrayList<>(fieldUnit.getAnnotations());
            List<Annotation> annotationsSeen = new ArrayList<>();
            for(Annotation a : annotations){
                if(a == null){
                    continue;
                }
                if(!currentAnnotations.contains(a)){
                    fieldUnit.addAnnotation(a);
                }
                annotationsSeen.add(a);
            }
            for(Annotation a : currentAnnotations){
                if(!annotationsSeen.contains(a)){
                    fieldUnit.removeAnnotation(a);
                }
            }
        }
    }

}
