package eu.etaxonomy.cdm.vaadin.view.dbstatus;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.vaadin.data.Container;

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
import eu.etaxonomy.cdm.vaadin.container.CdmSQLContainer;
import eu.etaxonomy.cdm.vaadin.model.LazyLoadedContainer;

public interface IDistributionTableComponent {

	public interface DistributionTableComponentListener{
		Set<DefinedTermBase> getChosenTerms();

		List<PresenceAbsenceTerm> getPresenceAbsenceTerms();

		HashMap<DescriptionElementBase, Distribution> getDistribution(DefinedTermBase dt, Taxon taxon);

		LazyLoadedContainer getTableContainer();

		int getSizeOfClassification();

		List<Distribution> getDistribution(Taxon taxon);

		IClassificationService getClassificationService();

		IVocabularyService getVocabularyService();

		IDescriptionService getDescriptionService();

		ITaxonNodeService getTaxonNodeService();

		ITermService getTermService();

		ITaxonService getTaxonService();

		List<DescriptionElementBase> listDescriptionElementsForTaxon(
				Taxon taxon, Set<Feature> setFeature);

		Classification getChosenClassification();

		List<TaxonNode> getAllNodes(int start, int end);

		CdmSQLContainer getSQLContainer() throws SQLException;

		List<String> getTermList();

		Container getPresenceAbsenceContainer();

        /**
         * @return
         */
        List<String> getAbbreviatedTermList();

        /**
         * @param distributionArea
         * @param comboValue
         * @param taxon
         * @return
         */
        int updateDistributionField(String distributionArea, Object comboValue, Taxon taxon);

        /**
         * @param taxon
         * @param comboboxValue
         * @param area
         */
        void createDistributionField(Taxon taxon, Object comboboxValue, String area);

	}
	public void addListener(DistributionTableComponentListener listener);
}
