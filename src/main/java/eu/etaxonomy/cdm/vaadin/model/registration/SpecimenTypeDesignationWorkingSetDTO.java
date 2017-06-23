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
import java.util.List;
import java.util.Set;

import org.joda.time.Partial;

import eu.etaxonomy.cdm.model.agent.AgentBase;
import eu.etaxonomy.cdm.model.common.Language;
import eu.etaxonomy.cdm.model.common.LanguageString;
import eu.etaxonomy.cdm.model.common.VersionableEntity;
import eu.etaxonomy.cdm.model.location.NamedArea;
import eu.etaxonomy.cdm.model.location.Point;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignation;
import eu.etaxonomy.cdm.model.occurrence.FieldUnit;
import eu.etaxonomy.cdm.model.occurrence.GatheringEvent;

/**
 * @author a.kohlbecker
 * @since Jun 16, 2017
 *
 */
public class SpecimenTypeDesignationWorkingSetDTO {

    FieldUnit fieldUnit;

    VersionableEntity baseEntity;

    List<SpecimenTypeDesignation> specimenTypeDesignations;

    List<SpecimenTypeDesignationDTO> specimenTypeDesignationsDTOs = new ArrayList<>();

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
            if(fieldUnit.getGatheringEvent() == null){
                fieldUnit.setGatheringEvent(GatheringEvent.NewInstance());
            }
        }
        this.specimenTypeDesignations = specimenTypeDesignations;
        specimenTypeDesignations.forEach(std -> specimenTypeDesignationsDTOs.add(new SpecimenTypeDesignationDTO(std)));
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

    public List<SpecimenTypeDesignationDTO> getSpecimenTypeDesignationDTOs(){
        return specimenTypeDesignationsDTOs;
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

    public void getGatheringDate(Partial gatheringDate){
        fieldUnit.getGatheringEvent().setGatheringDate(gatheringDate);
    }

}
