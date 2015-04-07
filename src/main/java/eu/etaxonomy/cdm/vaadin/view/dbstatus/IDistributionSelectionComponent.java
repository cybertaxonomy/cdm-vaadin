package eu.etaxonomy.cdm.vaadin.view.dbstatus;

import java.sql.SQLException;
import java.util.List;

import eu.etaxonomy.cdm.model.common.DefinedTermBase;
import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.taxon.Classification;

public interface IDistributionSelectionComponent {
	public interface DistributionSelectionComponentListener{
		void buttonClick(Classification classification, TermVocabulary<DefinedTermBase> term) throws SQLException;
		
		List<Classification> getClassificationList();
		
		List<TermVocabulary<DefinedTermBase>> getNamedAreaList();
		
	}
	public void addListener(DistributionSelectionComponentListener listener);
}
