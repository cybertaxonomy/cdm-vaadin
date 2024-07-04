/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.model.name;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import eu.etaxonomy.cdm.model.agent.TeamOrPersonBase;
import eu.etaxonomy.cdm.model.common.Annotation;
import eu.etaxonomy.cdm.model.common.Credit;
import eu.etaxonomy.cdm.model.common.Extension;
import eu.etaxonomy.cdm.model.common.Identifier;
import eu.etaxonomy.cdm.model.common.RelationshipBase.Direction;
import eu.etaxonomy.cdm.model.name.HomotypicalGroup;
import eu.etaxonomy.cdm.model.name.NameRelationship;
import eu.etaxonomy.cdm.model.name.NameRelationshipType;
import eu.etaxonomy.cdm.model.name.NomenclaturalCode;
import eu.etaxonomy.cdm.model.name.NomenclaturalSource;
import eu.etaxonomy.cdm.model.name.NomenclaturalStatus;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.permission.User;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.vaadin.model.CdmEntityAdapterDTO;

/**
 * @author a.kohlbecker
 * @since Apr 23, 2018
 *
 */
public class TaxonNameDTO extends CdmEntityAdapterDTO<TaxonName> {

    private static final long serialVersionUID = -8018109905949198530L;

    private TaxonName name;

    private TaxonName persistedValidatedName;

    private TaxonName persistedOrthographicVariant;

    private Set<NomenclaturalStatusDTO> nomenclaturalStatusDTOs = new HashSet<>();

    public TaxonNameDTO(TaxonName entity) {
        super(entity);
        name = entity;
        for(NomenclaturalStatus status : name.getStatus()) {
            nomenclaturalStatusDTOs.add(NomenclaturalStatusDTO.from(status));
        }
    }

    public String getAcronym() {
        return name.getAcronym();
    }

    public Set<Annotation> getAnnotations() {
        return name.getAnnotations();
    }

    public void setAnnotations(Set<Annotation> annotations) {
        List<Annotation> currentAnnotations = new ArrayList<>(name.getAnnotations());
        List<Annotation> annotationsSeen = new ArrayList<>();
        for(Annotation a : annotations){
            if(a == null){
                continue;
            }
            if(!currentAnnotations.contains(a)){
                name.addAnnotation(a);
            }
            annotationsSeen.add(a);
        }
        for(Annotation a : currentAnnotations){
            if(!annotationsSeen.contains(a)){
                name.removeAnnotation(a);
            }
        }
    }

    public String getAppendedPhrase() {
        return name.getAppendedPhrase();
    }

    public String getAuthorshipCache() {

        return name.getAuthorshipCache();
    }

    public TeamOrPersonBase<?> getBasionymAuthorship() {
        return name.getBasionymAuthorship();
    }

    public Set<TaxonName> getBasionyms() {
        Set<TaxonName> basionyms = name.getRelatedNames(Direction.relatedTo, NameRelationshipType.BASIONYM());
        return basionyms;
    }

    public Set<TaxonName> getReplacedSynonyms() {
        Set<TaxonName> replacedSynonyms = name.getRelatedNames(Direction.relatedTo, NameRelationshipType.REPLACED_SYNONYM());
        return replacedSynonyms;
    }

    public NameRelationshipDTO getValidationFor() {
        NameRelationshipDTO nameRelDto  = null;
        NameRelationship validatingRelationship = uniqueNameRelationship(NameRelationshipType.VALIDATED_BY_NAME(), Direction.relatedTo);
        if(validatingRelationship != null){
            nameRelDto = new NameRelationshipDTO(Direction.relatedTo, validatingRelationship);
            if(persistedValidatedName == null){
               persistedValidatedName = nameRelDto.getOtherName();
            }
        }
        return nameRelDto;
    }

    public void setValidationFor(NameRelationshipDTO nameRelDto) {
        setUniqeNameRelationDTO(nameRelDto, NameRelationshipType.VALIDATED_BY_NAME(), Direction.relatedTo, persistedValidatedName);
    }


    public NameRelationshipDTO getOrthographicVariant() {
        NameRelationshipDTO nameRelDto  = null;
        NameRelationship nameRelationship = uniqueNameRelationship(NameRelationshipType.ORTHOGRAPHIC_VARIANT(), Direction.relatedTo);
        if(nameRelationship != null){
            nameRelDto = new NameRelationshipDTO(Direction.relatedTo, nameRelationship);
            if(persistedOrthographicVariant == null){
               persistedOrthographicVariant = nameRelDto.getOtherName();
            }
        }
        return nameRelDto;
    }

    public void setOrthographicVariant(NameRelationshipDTO nameRelDto) {
        setUniqeNameRelationDTO(nameRelDto, NameRelationshipType.ORTHOGRAPHIC_VARIANT(), Direction.relatedTo, persistedOrthographicVariant);
    }

    /**
     * @param nameRelDto
     * @param nameRelationshipType
     * @param direction
     * @param persistedRelatedName
     */
    public void setUniqeNameRelationDTO(NameRelationshipDTO nameRelDto, NameRelationshipType nameRelationshipType,
            Direction direction, TaxonName persistedRelatedName) {
        if(nameRelDto != null && nameRelDto.getOtherName() == null){
            // treat as if there is no related name
            nameRelDto = null;
        }

        NameRelationship relationship = uniqueNameRelationship(nameRelationshipType, direction);

        if(nameRelDto != null){
            // add or update ...
            boolean currentNameIsTarget = false;
            if(relationship != null && persistedRelatedName != null){
                if(direction == Direction.relatedTo){
                    currentNameIsTarget = relationship.getFromName().equals(persistedRelatedName);
                } else {
                    currentNameIsTarget = relationship.getToName().equals(persistedRelatedName);
                }
            }
            if(relationship != null && currentNameIsTarget){
                // related name has not changed, so we can update the relation
                relationship.setCitation(nameRelDto.getCitation());
                relationship.setCitationMicroReference(nameRelDto.getCitationMicroReference());
                relationship.setRuleConsidered(nameRelDto.getRuleConsidered());
                relationship.setCodeEdition(nameRelDto.getCodeEdition());
            } else {
                // need to remove the old relationship and to create a new one.
                // the actual removal will take place ....
                if(direction == Direction.relatedTo){
                    name.addRelationshipFromName(nameRelDto.getOtherName(), nameRelationshipType,
                            nameRelDto.getCitation(), nameRelDto.getCitationMicroReference(), nameRelDto.getRuleConsidered(), nameRelDto.getCodeEdition());
                } else {
                    name.addRelationshipToName(nameRelDto.getOtherName(), nameRelationshipType,
                            nameRelDto.getCitation(), nameRelDto.getCitationMicroReference(), nameRelDto.getRuleConsidered(), nameRelDto.getCodeEdition());
                }
                if(persistedRelatedName != null){
                    name.removeRelationWithTaxonName(persistedRelatedName, direction, nameRelationshipType);
                }
            }
        } else {
            // remove ...
            if(persistedRelatedName != null && relationship != null){
                name.removeRelationWithTaxonName(persistedRelatedName, direction, nameRelationshipType);
            }
        }
    }

    public void setBasionyms(Set<TaxonName> basionyms) {
        setRelatedNames(Direction.relatedTo, NameRelationshipType.BASIONYM(), basionyms);
    }

    public void setReplacedSynonyms(Set<TaxonName> replacedSynonyms) {
        setRelatedNames(Direction.relatedTo, NameRelationshipType.REPLACED_SYNONYM(), replacedSynonyms);
    }

    /**
     * @return
     */
    protected NameRelationship uniqueNameRelationship(NameRelationshipType relationShipType, Direction direction) {

        Set<NameRelationship> relations;

        if(direction == Direction.relatedTo){
            relations = name.getRelationsToThisName();
        } else {
            relations = name.getRelationsFromThisName();
        }
        Set<NameRelationship> nameRelations = relations.stream().filter(
                    nr -> nr.getType().equals(relationShipType)
                ).collect(Collectors.toSet());
        if(nameRelations.size() > 1){
            // TODO use non RuntimeException
            throw new RuntimeException("More than one relationship of type " + relationShipType.getLabel() + " found.");
        } else if(nameRelations.size() == 0) {
            return null;
        }
        return nameRelations.iterator().next();
    }

    /**
     * @param basionyms
     * @param relType
     * @param direction
     */
    protected void setRelatedNames(Direction direction, NameRelationshipType relType, Set<TaxonName> relatedNames) {
        Set<TaxonName> currentRelatedNames = new HashSet<>();
        Set<TaxonName> namesSeen = new HashSet<>();

        for(TaxonName tn : name.getRelatedNames(direction, relType)){
            currentRelatedNames.add(tn);
        }
        for(TaxonName tn : relatedNames){
            if(tn == null){
                continue;
            }
            if(!currentRelatedNames.contains(tn)){
                if(direction.equals(Direction.relatedTo)){
                    tn.addRelationshipToName(name, relType, null, null);
                } else {
                    tn.addRelationshipFromName(name, relType, null, null);
                }
            }
            namesSeen.add(tn);
        }
        for(TaxonName tn : currentRelatedNames){
            if(!namesSeen.contains(tn)){
                name.removeRelationWithTaxonName(tn, direction, relType);
            }
        }
    }

    public TeamOrPersonBase<?> getCombinationAuthorship() {
        return name.getCombinationAuthorship();
    }

    public List<Credit> getCredits() {
        return name.getCredits();
    }

    public String getCultivarName() {
        return name.getCultivarEpithet();
    }

    public TeamOrPersonBase<?> getExBasionymAuthorship() {
        return name.getExBasionymAuthorship();
    }

    public TeamOrPersonBase<?> getExCombinationAuthorship() {
        return name.getExCombinationAuthorship();
    }

    public Set<Extension> getExtensions() {
        return name.getExtensions();
    }

    public String getFullTitleCache() {
        return name.getFullTitleCache();
    }

    public String getGenusOrUninomial() {
        return name.getGenusOrUninomial();
    }

    public HomotypicalGroup getHomotypicalGroup() {
        return name.getHomotypicalGroup();
    }

    public List<Identifier> getIdentifiers() {
        return name.getIdentifiers();
    }

    public String getInfraGenericEpithet() {
        return name.getInfraGenericEpithet();
    }

    public String getInfraSpecificEpithet() {
        return name.getInfraSpecificEpithet();
    }

    public String getSpecificEpithet() {
        return name.getSpecificEpithet();
    }

    public String getNameCache() {
        return name.getNameCache();
    }

    public String getNomenclaturalMicroReference() {
        return name.getNomenclaturalMicroReference();
    }

    public Reference getNomenclaturalReference() {
        return name.getNomenclaturalReference();
    }

    public Rank getRank() {
        return name.getRank();
    }

    public Set<NomenclaturalStatusDTO> getStatus() {
        return nomenclaturalStatusDTOs;
    }

    public void setStatus(Set<NomenclaturalStatusDTO> status) {
        nomenclaturalStatusDTOs = status;
    }

    public boolean isProtectedAuthorshipCache() {
        return name.isProtectedAuthorshipCache();
    }

    public boolean isProtectedFullTitleCache() {
        return name.isProtectedFullTitleCache();
    }

    public boolean isProtectedNameCache() {
        return name.isProtectedNameCache();
    }

    public boolean isProtectedTitleCache() {
        return name.isProtectedTitleCache();
    }

    public void setAcronym(String acronym) {
        name.setAcronym(acronym);
    }

    public void setAppendedPhrase(String appendedPhrase) {
        name.setAppendedPhrase(appendedPhrase);
    }

    public void setBasionymAuthorship(TeamOrPersonBase<?> basionymAuthorship) {
        name.setBasionymAuthorship(basionymAuthorship);
    }

    public void setBinomHybrid(boolean binomHybrid) {
        name.setBinomHybrid(binomHybrid);
    }

    public void setBreed(String breed) {
        name.setBreed(breed);
    }

    public void setCombinationAuthorship(TeamOrPersonBase<?> combinationAuthorship) {
        name.setCombinationAuthorship(combinationAuthorship);
    }

    public void setCultivarName(String cultivarName) {
        name.setCultivarEpithet(cultivarName);
    }

    public void setExBasionymAuthorship(TeamOrPersonBase<?> exBasionymAuthorship) {
        name.setExBasionymAuthorship(exBasionymAuthorship);
    }

    public void setExCombinationAuthorship(TeamOrPersonBase<?> exCombinationAuthorship) {
        name.setExCombinationAuthorship(exCombinationAuthorship);
    }

    public void setFullTitleCache(String fullTitleCache) {
        name.setFullTitleCache(fullTitleCache);
    }

    public void setGenusOrUninomial(String genusOrUninomial) {
        name.setGenusOrUninomial(genusOrUninomial);
    }

    public void setHybridFormula(boolean hybridFormula) {
        name.setHybridFormula(hybridFormula);
    }

    public void setInfraGenericEpithet(String infraGenericEpithet) {
        name.setInfraGenericEpithet(infraGenericEpithet);
    }

    public void setInfraSpecificEpithet(String infraSpecificEpithet) {
        name.setInfraSpecificEpithet(infraSpecificEpithet);
    }

    public void setMonomHybrid(boolean monomHybrid) {
        name.setMonomHybrid(monomHybrid);
    }

    public void setNameApprobation(String nameApprobation) {
        name.setNameApprobation(nameApprobation);
    }

    public void setNameCache(String nameCache) {
        name.setNameCache(nameCache);
    }

    public void setNameType(NomenclaturalCode nameType) {
        name.setNameType(nameType);
    }

    public void setNomenclaturalMicroReference(String nomenclaturalMicroReference) {
        assureNomenclaturalSource().setCitationMicroReference(nomenclaturalMicroReference);
    }

    public void setNomenclaturalReference(Reference nomenclaturalReference) {
        assureNomenclaturalSource().setCitation(nomenclaturalReference);
    }

    protected NomenclaturalSource assureNomenclaturalSource() {
        NomenclaturalSource nomSource = name.getNomenclaturalSource();
        if(nomSource == null) {
            nomSource = NomenclaturalSource.NewNomenclaturalInstance(name);
            name.setNomenclaturalSource(nomSource);
        }
        return nomSource;
    }

    public void setProtectedAuthorshipCache(boolean protectedAuthorshipCache) {
        name.setProtectedAuthorshipCache(protectedAuthorshipCache);
    }

    public void setProtectedFullTitleCache(boolean protectedFullTitleCache) {
        name.setProtectedFullTitleCache(protectedFullTitleCache);
    }

    public void setProtectedNameCache(boolean protectedNameCache) {
        name.setProtectedNameCache(protectedNameCache);
    }

    public void setProtectedTitleCache(boolean protectedTitleCache) {
        name.setProtectedTitleCache(protectedTitleCache);
    }

    public void setRank(Rank rank) {
        name.setRank(rank);
    }

    public void setSpecificEpithet(String specificEpithet) {
        name.setSpecificEpithet(specificEpithet);
    }

    public void setTitleCache(String titleCache) {
        name.setTitleCache(titleCache);
    }

    public void setTrinomHybrid(boolean trinomHybrid) {
        name.setTrinomHybrid(trinomHybrid);
    }

    public void setUpdated(DateTime updated) {
        name.setUpdated(updated);
    }

    public void setUpdatedBy(User updatedBy) {
        name.setUpdatedBy(updatedBy);
    }

}
