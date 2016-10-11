package eu.etaxonomy.cdm.vaadin.util;

import java.util.Set;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.location.NamedArea;
import eu.etaxonomy.cdm.model.taxon.TaxonNode;

public class DistributionEditorUtil {

    public static final String SEPARATOR = ";;";

    public static void openDistributionView(TaxonNode taxonNode, TermVocabulary<NamedArea> term, Set<NamedArea> selectedAreas) {
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
//	    String selectedAreaUuids = "";
//	    for (NamedArea namedArea : selectedAreas) {
//			selectedAreaUuids += namedArea.getTitleCache()+SEPARATOR;
//		}
//	    selectedAreaUuids = StringUtils.stripEnd(selectedAreaUuids, SEPARATOR);
	    VaadinSession.getCurrent().setAttribute("selectedAreas", selectedAreas);

	    //navigate to table view
	    UI.getCurrent().getNavigator().navigateTo("table");
	}

}
