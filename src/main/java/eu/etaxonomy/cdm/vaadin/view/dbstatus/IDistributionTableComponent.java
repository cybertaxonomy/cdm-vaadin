package eu.etaxonomy.cdm.vaadin.view.dbstatus;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ComboBox;

import eu.etaxonomy.cdm.model.common.DefinedTermBase;
import eu.etaxonomy.cdm.model.description.DescriptionElementBase;
import eu.etaxonomy.cdm.model.description.Distribution;
import eu.etaxonomy.cdm.model.description.PresenceAbsenceTerm;
import eu.etaxonomy.cdm.model.taxon.Taxon;

public interface IDistributionTableComponent {

	public interface DistributionTableComponentListener{
		Set<DefinedTermBase> getChosenTerms();

		List<PresenceAbsenceTerm> getPresenceAbsenceTerms();

		ComboBox updateDistributionField(DescriptionElementBase deb, Distribution db, BeanItemContainer<PresenceAbsenceTerm> termContainer, ComboBox box, Taxon taxon);

		HashMap<DescriptionElementBase, Distribution> getDistribution(DefinedTermBase dt, Taxon taxon);

	}
	public void addListener(DistributionTableComponentListener listener);
}
