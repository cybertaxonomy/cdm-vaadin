package eu.etaxonomy.cdm.vaadin.util;

import java.util.Set;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.model.common.DefinedTermBase;
import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.location.NamedArea;
import eu.etaxonomy.cdm.model.taxon.TaxonNode;

public class DistributionEditorUtil {

	public static void openDistributionView(TaxonNode taxonNode, TermVocabulary<DefinedTermBase> term, Set<NamedArea> selectedAreas) {
		if(taxonNode==null){
			Notification.show("Please choose a classification and/or taxon", Notification.Type.HUMANIZED_MESSAGE);
			return;
		}
		if(term==null){
			Notification.show("Please choose a distribution area", Notification.Type.HUMANIZED_MESSAGE);
			return;
		}
	    VaadinSession.getCurrent().setAttribute("taxonNodeUUID", taxonNode.getUuid());
	    VaadinSession.getCurrent().setAttribute("selectedTerm", term.getUuid());
	    String selectedAreaUuids = "";
	    for (NamedArea namedArea : selectedAreas) {
			selectedAreaUuids += namedArea.getUuid().toString()+",";
		}
	    VaadinSession.getCurrent().setAttribute("selectedAreas", selectedAreaUuids);
	
	    //navigate to table view
	    UI.getCurrent().getNavigator().navigateTo("table");
	}

}
