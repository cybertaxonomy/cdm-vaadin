package eu.etaxonomy.cdm.vaadin.view.dbstatus;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ComboBox;

import eu.etaxonomy.cdm.api.service.IClassificationService;
import eu.etaxonomy.cdm.api.service.IDescriptionService;
import eu.etaxonomy.cdm.api.service.ITaxonNodeService;
import eu.etaxonomy.cdm.api.service.ITaxonService;
import eu.etaxonomy.cdm.api.service.ITermService;
import eu.etaxonomy.cdm.api.service.IVocabularyService;
import eu.etaxonomy.cdm.model.common.DefinedTermBase;
import eu.etaxonomy.cdm.model.description.DescriptionElementBase;
import eu.etaxonomy.cdm.model.description.Distribution;
import eu.etaxonomy.cdm.model.description.Feature;
import eu.etaxonomy.cdm.model.description.PresenceAbsenceTerm;
import eu.etaxonomy.cdm.model.taxon.Classification;
import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.model.taxon.TaxonNode;
import eu.etaxonomy.cdm.vaadin.model.DbTableDTOS;
import eu.etaxonomy.cdm.vaadin.model.LazyLoadedContainer;

public interface IDistributionTableComponent {

	public interface DistributionTableComponentListener{
		Set<DefinedTermBase> getChosenTerms();

		List<PresenceAbsenceTerm> getPresenceAbsenceTerms();

		ComboBox updateDistributionField(DescriptionElementBase deb, Distribution db, BeanItemContainer<PresenceAbsenceTerm> termContainer, ComboBox box, Taxon taxon);

		HashMap<DescriptionElementBase, Distribution> getDistribution(DefinedTermBase dt, Taxon taxon);
		
		LazyLoadedContainer getTableContainer();
		
		int getSizeOfClassification();
		
		DbTableDTOS getDataList(int start, int end);

		List<Distribution> getDistribution(Taxon taxon);

		IClassificationService getClassificationService();

		IVocabularyService getVocabularyService();

		IDescriptionService getDescriptionService();

		ITaxonNodeService getTaxonNodeService();

		ITermService getTermService();

		ITaxonService getTaxonService();

		List<DescriptionElementBase> listDescriptionElementsForTaxon(
				Taxon taxon, Set<Feature> setFeature);

		LazyLoadedContainer getLazyLoadedContainer();

		Classification getChosenClassification();

		List<TaxonNode> getAllNodes(int start, int end);

	}
	public void addListener(DistributionTableComponentListener listener);
}
