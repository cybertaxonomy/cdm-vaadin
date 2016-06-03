package eu.etaxonomy.cdm.vaadin.view.dbstatus;

import java.sql.SQLException;
import java.util.List;

import eu.etaxonomy.cdm.model.common.DefinedTermBase;
import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.taxon.Classification;
import eu.etaxonomy.cdm.model.taxon.TaxonNode;

public interface IDistributionSelectionComponent {
	public interface DistributionSelectionComponentListener{
		void buttonClick(TermVocabulary<DefinedTermBase> term, TaxonNode taxonNode) throws SQLException;

		List<Classification> getClassificationList();

		List<TermVocabulary<DefinedTermBase>> getNamedAreaList();

	}
	public void addListener(DistributionSelectionComponentListener listener);
}
