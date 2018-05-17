/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.model.name;

import java.io.Serializable;

import eu.etaxonomy.cdm.model.common.RelationshipBase.Direction;
import eu.etaxonomy.cdm.model.name.NameRelationship;
import eu.etaxonomy.cdm.model.name.NameRelationshipType;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.reference.Reference;

/**
 * @author a.kohlbecker
 * @since May 3, 2018
 *
 */
public class NameRelationshipDTO implements Serializable {

    private static final long serialVersionUID = 966322755160849163L;

    NameRelationship nameRel;

    TaxonName otherName;
    NameRelationshipType type;
    Direction direction;
    Reference citation;
    String citationMicroReference;
    String ruleConsidered;

    /**
     * @param entity
     */
    public NameRelationshipDTO(Direction direction, NameRelationshipType type) {
        this.direction = direction;
        this.type = type;
    }

    public NameRelationshipDTO(Direction direction, NameRelationship nameRel) {
        this.direction = direction;
        otherName = otherNameFrom(nameRel);
        type = nameRel.getType();
        citation = nameRel.getCitation();
        citationMicroReference = nameRel.getCitationMicroReference();
        ruleConsidered = nameRel.getRuleConsidered();
    }

    /**
     * @return
     */
    protected TaxonName otherNameFrom(NameRelationship nameRel) {
        return this.direction.equals(Direction.relatedTo) ?  nameRel.getFromName() : nameRel.getToName();
    }

    protected TaxonName thisNameFrom(NameRelationship nameRel) {
        return this.direction.equals(Direction.relatedTo) ?  nameRel.getToName() : nameRel.getFromName();
    }

    /**
     * @return the otherName
     */
    public TaxonName getOtherName() {
        return otherName;
    }

    /**
     * @param otherName the otherName to set
     */
    public void setOtherName(TaxonName otherName) {
        this.otherName = otherName;
    }

    /**
     * @return the type
     */
    public NameRelationshipType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(NameRelationshipType type) {
        this.type = type;
    }

    /**
     * @return the direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * @param direction the direction to set
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
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
     * @return the citationMicroReference
     */
    public String getCitationMicroReference() {
        return citationMicroReference;
    }

    /**
     * @param citationMicroReference the citationMicroReference to set
     */
    public void setCitationMicroReference(String citationMicroReference) {
        this.citationMicroReference = citationMicroReference;
    }

    /**
     * @return the ruleConsidered
     */
    public String getRuleConsidered() {
        return ruleConsidered;
    }

    /**
     * @param ruleConsidered the ruleConsidered to set
     */
    public void setRuleConsidered(String ruleConsidered) {
        this.ruleConsidered = ruleConsidered;
    }



}
