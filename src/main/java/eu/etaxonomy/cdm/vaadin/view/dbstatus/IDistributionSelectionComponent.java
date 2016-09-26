package eu.etaxonomy.cdm.vaadin.view.dbstatus;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import eu.etaxonomy.cdm.model.common.DefinedTermBase;
import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.location.NamedArea;
import eu.etaxonomy.cdm.model.taxon.TaxonNode;

public interface IDistributionSelectionComponent {
	public interface DistributionSelectionComponentListener{
		void buttonClick(TaxonNode classification, TermVocabulary<DefinedTermBase> term, Set<NamedArea> selectedAreas) throws SQLException;
		
		List<TaxonNode> getTaxonNodeList();
		
		List<TermVocabulary<DefinedTermBase>> getNamedAreaList();
		
	}
	public void addListener(DistributionSelectionComponentListener listener);
}
