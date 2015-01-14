package eu.etaxonomy.cdm.vaadin.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.description.DescriptionElementBase;
import eu.etaxonomy.cdm.model.description.Distribution;
import eu.etaxonomy.cdm.model.description.PresenceAbsenceTerm;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.name.TaxonNameBase;
import eu.etaxonomy.cdm.model.taxon.Taxon;


public class DbTableDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Taxon taxon;

	public Taxon getTaxon() {
		return taxon;
	}

	public void setTaxon(Taxon taxon) {
		this.taxon = taxon;
	}
	private Rank rank;

	private DistributionDTO dDTO;

	

	public DbTableDTO(Taxon taxon){
		this.taxon = CdmBase.deproxy(taxon, Taxon.class);
	}

	//----Getter - Setter - methods ----//
	
	public DistributionDTO getdDTO() {
		return dDTO;
	}

	public void setdDTO(DistributionDTO dDTO) {
		this.dDTO = dDTO;
	}

	/**
	 * 
	 * @return
	 */
	public String getFullTitleCache() {
		TaxonNameBase name = taxon.getName();
		name = CdmBase.deproxy(name, TaxonNameBase.class);
		if(name ==  null){
			return "-";
		}
		return name.getFullTitleCache();
	}
	/**
	 * 
	 * @param fullTitleCache
	 */
	public void setFullTitleCache(String fullTitleCache) {
		taxon.getName().setFullTitleCache(fullTitleCache, true);
		taxon.setTitleCache(fullTitleCache, true);
	}

	/**
	 * Returns the taxonomic {@link Rank rank} of <i>this</i> taxon name.
	 *
	 * @see 	Rank
	 */
	public String getRank(){
		rank = taxon.getName().getRank();
		if(rank == null){
			return "-";
		}
		return rank.toString();
	}

	public UUID getUUID(){
		return taxon.getUuid();
	}

	/**
	 * @see  #getRank()
	 */
	public void setRank(Rank rank){
		taxon.getName().setRank(rank);
	}
	/**
	 * 
	 * @return
	 */




	//----------- Detail View ------------------//

	/**
	 * 
	 * @return
	 */
	public String getTaxonNameCache(){
		return taxon.getName().getTitleCache();
	}

	public void setTaxonNameCache(String titlecache){
		taxon.getName().setTitleCache(titlecache, true);
	}
	/**
	 * 
	 * @return
	 */
	public String getNomenclaturalCode(){
		return taxon.getName().getNomenclaturalCode().getTitleCache();
	}
	/**
	 * 
	 * @return
	 */
	public String getSecundum(){
		return taxon.getSec().toString();
	}

}
