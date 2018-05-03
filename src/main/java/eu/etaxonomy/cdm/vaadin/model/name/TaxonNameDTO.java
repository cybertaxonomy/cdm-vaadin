/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.model.name;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;

import eu.etaxonomy.cdm.model.agent.TeamOrPersonBase;
import eu.etaxonomy.cdm.model.common.Annotation;
import eu.etaxonomy.cdm.model.common.Credit;
import eu.etaxonomy.cdm.model.common.Extension;
import eu.etaxonomy.cdm.model.common.Identifier;
import eu.etaxonomy.cdm.model.common.RelationshipBase.Direction;
import eu.etaxonomy.cdm.model.common.User;
import eu.etaxonomy.cdm.model.name.HomotypicalGroup;
import eu.etaxonomy.cdm.model.name.NameRelationshipType;
import eu.etaxonomy.cdm.model.name.NomenclaturalCode;
import eu.etaxonomy.cdm.model.name.NomenclaturalStatus;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.reference.INomenclaturalReference;
import eu.etaxonomy.cdm.vaadin.model.CdmEntityDecoraterDTO;

/**
 * @author a.kohlbecker
 * @since Apr 23, 2018
 *
 */
public class TaxonNameDTO extends CdmEntityDecoraterDTO<TaxonName> {

    class TN extends TaxonName {

    }

    private static final long serialVersionUID = -8018109905949198530L;

    private TaxonName name;

    private Set<TaxonName> persistedBasionyms;

    private Set<TaxonName> persistedReplacedSynonyms;

    /**
     * @param entity
     */
    public TaxonNameDTO(TaxonName entity) {
        super(entity);
        name = entity;
    }

    public String getAcronym() {
        return name.getAcronym();
    }

    public Set<Annotation> getAnnotations() {
        return name.getAnnotations();
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
        if(persistedBasionyms == null){
            // remember the persisted state before starting to operate on the DTO
            persistedBasionyms = basionyms;
        }
        return basionyms;
    }

    public Set<TaxonName> getReplacedSynonyms() {
        Set<TaxonName> replacedSynonyms = name.getRelatedNames(Direction.relatedTo, NameRelationshipType.REPLACED_SYNONYM());
        if(persistedReplacedSynonyms == null){
            // remember the persisted state before starting to operate on the DTO
            persistedReplacedSynonyms = replacedSynonyms;
        }
        return replacedSynonyms;
    }

    public void setBasionyms(Set<TaxonName> basionyms) {
        setRelatedNames(Direction.relatedTo, NameRelationshipType.BASIONYM(), basionyms);
    }

    public void setReplacedSynonyms(Set<TaxonName> replacedSynonyms) {
        setRelatedNames(Direction.relatedTo, NameRelationshipType.REPLACED_SYNONYM(), replacedSynonyms);
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
                    tn.addRelationshipToName(name, relType, null);
                } else {
                    tn.addRelationshipFromName(name, relType, null);
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

    public Set<TaxonName> persistedBasionyms(){
        return persistedBasionyms;
    }

    public TeamOrPersonBase<?> getCombinationAuthorship() {
        return name.getCombinationAuthorship();
    }

    public List<Credit> getCredits() {
        return name.getCredits();
    }

    public String getCultivarName() {
        return name.getCultivarName();
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

    public INomenclaturalReference getNomenclaturalReference() {
        return name.getNomenclaturalReference();
    }

    public Rank getRank() {
        return name.getRank();
    }

    public Set<NomenclaturalStatus> getStatus() {
        return name.getStatus();
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
        name.setCultivarName(cultivarName);
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
        name.setNomenclaturalMicroReference(nomenclaturalMicroReference);
    }

    public void setNomenclaturalReference(INomenclaturalReference nomenclaturalReference) {
        name.setNomenclaturalReference(nomenclaturalReference);
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
