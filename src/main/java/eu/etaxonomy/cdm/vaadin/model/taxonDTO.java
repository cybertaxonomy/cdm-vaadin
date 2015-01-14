package eu.etaxonomy.cdm.vaadin.model;

import java.io.Serializable;

import eu.etaxonomy.cdm.model.taxon.Taxon;

public class taxonDTO implements Serializable {
	
	private String taxon;
	private DistributionDTO distribution;
	
	public taxonDTO(String taxon, DistributionDTO distribution){
		this.taxon = taxon;
		this.distribution = distribution;
	}

	public String getTaxon() {
		return taxon;
	}

	public void setTaxon(String taxon) {
		this.taxon = taxon;
	}

	public DistributionDTO getDistribution() {
		return distribution;
	}

	public void setDistribution(DistributionDTO distribution) {
		this.distribution = distribution;
	}

}
