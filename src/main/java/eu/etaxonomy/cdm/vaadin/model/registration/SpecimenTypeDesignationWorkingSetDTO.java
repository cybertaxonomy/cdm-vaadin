/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.model.registration;

import java.util.List;
import java.util.Set;

import org.joda.time.Partial;

import eu.etaxonomy.cdm.model.agent.Person;
import eu.etaxonomy.cdm.model.common.LanguageString;
import eu.etaxonomy.cdm.model.common.VersionableEntity;
import eu.etaxonomy.cdm.model.location.NamedArea;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignation;
import eu.etaxonomy.cdm.model.occurrence.FieldUnit;

/**
 * @author a.kohlbecker
 * @since Jun 16, 2017
 *
 */
public class SpecimenTypeDesignationWorkingSetDTO {

    FieldUnit fieldUnit;

    VersionableEntity baseEntity;

    List<SpecimenTypeDesignation> specimenTypeDesignations;

    VersionableEntity owner;

    /**
     * @param fieldUnit
     * @param derivedUnits
     */
    public SpecimenTypeDesignationWorkingSetDTO(VersionableEntity owner, VersionableEntity baseEntity, List<SpecimenTypeDesignation> specimenTypeDesignations) {
        super();
        this.owner = owner;
        this.baseEntity = baseEntity;
        if(baseEntity instanceof FieldUnit){
            this.fieldUnit = (FieldUnit) baseEntity;
        }
        this.specimenTypeDesignations = specimenTypeDesignations;
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
     * @return the derivedUnits
     */
    public List<SpecimenTypeDesignation> getSpecimenTypeDesignations() {
        return specimenTypeDesignations;
    }

    /**
     * The IdentifiableEntity which contains the DerivedUnit in this working set.
     *
     *
     * @return
     */
    public VersionableEntity getOwner() {
        return owner;
    }

    // ====== FieldUnit Wrapper methods ====== //
    public Person getPrimaryCollector() {
        return fieldUnit.getPrimaryCollector();
    }

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

    public LanguageString getLocality(){
        return fieldUnit.getGatheringEvent().getLocality();
    }

    public void setLocality(LanguageString locality){
        fieldUnit.getGatheringEvent().setLocality(locality);
    }
    public NamedArea getCountry() {
        return fieldUnit.getGatheringEvent().getCountry();
    }

    public void setCountry(NamedArea country) {
        fieldUnit.getGatheringEvent().setCountry(country);
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

    public void getGatheringDate(Partial gatheringDate){
        fieldUnit.getGatheringEvent().setGatheringDate(gatheringDate);
    }

}
