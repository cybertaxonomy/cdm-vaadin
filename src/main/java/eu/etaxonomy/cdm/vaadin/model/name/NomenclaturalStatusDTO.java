/**
* Copyright (C) 2020 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.model.name;

import java.io.Serializable;

import eu.etaxonomy.cdm.model.description.DescriptionElementSource;
import eu.etaxonomy.cdm.model.name.NomenclaturalCodeEdition;
import eu.etaxonomy.cdm.model.name.NomenclaturalStatus;
import eu.etaxonomy.cdm.model.name.NomenclaturalStatusType;
import eu.etaxonomy.cdm.model.reference.OriginalSourceType;
import eu.etaxonomy.cdm.model.reference.Reference;

/**
 * @author a.kohlbecker
 * @since Sep 17, 2020
 */
public class NomenclaturalStatusDTO implements Serializable {

    private static final long serialVersionUID = -7019899466081978199L;

    private Integer id = 0;

    NomenclaturalStatusType type;

    Reference citation;

    NomenclaturalCodeEdition codeEdition;

    String citationMicroReference;

    String ruleConsidered;

    public static NomenclaturalStatusDTO from(NomenclaturalStatus nomStatus) {
        return new NomenclaturalStatusDTO(nomStatus.getId(), nomStatus.getType(), nomStatus.getCitation(),
                nomStatus.getCitationMicroReference(), nomStatus.getRuleConsidered(), nomStatus.getCodeEdition());

    }

    public static NomenclaturalStatusDTO newInstance() {
        return new NomenclaturalStatusDTO();
    }


    /**
     * Update an existing or create a new {@link NomenclaturalStatus} for this
     * DTO.
     *
     * @param nomStatus
     *            the {@link NomenclaturalStatus} to update or <code>null</code>
     *            in which case a new entity instance will be created.
     * @return the new or updated entity
     */
    public NomenclaturalStatus update(NomenclaturalStatus nomStatus) {
        if (nomStatus == null) {
            nomStatus = NomenclaturalStatus.NewInstance(type);
        } else {
            nomStatus.setType(type);
        }
        nomStatus.setRuleConsidered(ruleConsidered);
        if (citation != null || citationMicroReference != null) {
            if (nomStatus.getSource() == null) {
                // below line as in
                // DescriptionElementSource.NewInstance(OriginalSourceType.PrimaryTaxonomicSource)
                nomStatus.setSource(DescriptionElementSource.NewInstance(OriginalSourceType.PrimaryTaxonomicSource));
            }
            nomStatus.getSource().setCitation(citation);
            nomStatus.getSource().setCitationMicroReference(citationMicroReference);
        }
        nomStatus.setCodeEdition(codeEdition);
        return nomStatus;

    }

    public NomenclaturalStatusDTO(Integer id, NomenclaturalStatusType type, Reference citation,
            String citationMicroReference, String ruleConsidered, NomenclaturalCodeEdition codeEdition) {
        this.id = id;
        this.type = type;
        this.citation = citation;
        this.citationMicroReference = citationMicroReference;
        this.ruleConsidered = ruleConsidered;
        this.codeEdition = codeEdition;
    }

    /**
     *
     */
    public NomenclaturalStatusDTO() {
        // TODO Auto-generated constructor stub
    }

    public NomenclaturalStatusType getType() {
        return type;
    }

    public void setType(NomenclaturalStatusType type) {
        this.type = type;
    }

    public Reference getCitation() {
        return citation;
    }

    public void setCitation(Reference citation) {
        this.citation = citation;
    }

    public String getCitationMicroReference() {
        return citationMicroReference;
    }

    public NomenclaturalCodeEdition getCodeEdition() {
        return codeEdition;
    }

    public void setCodeEdition(NomenclaturalCodeEdition codeEdition) {
        this.codeEdition = codeEdition;
    }

    public void setCitationMicroReference(String citationMicroReference) {
        this.citationMicroReference = citationMicroReference;
    }

    public String getRuleConsidered() {
        return ruleConsidered;
    }

    public void setRuleConsidered(String ruleConsidered) {
        this.ruleConsidered = ruleConsidered;
    }

    /**
     * The {@link NomenclaturalStatus#getId()} of the original status entity for
     * which this DTO has been created.
     * <p>
     * When the DTO has no corresponding entity in the db the id has the value
     * of <code>0</code>
     */
    public Integer getId() {
        return id;
    }
}
