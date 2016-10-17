package eu.etaxonomy.cdm.vaadin.util;

import java.util.Set;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.location.NamedArea;
import eu.etaxonomy.cdm.model.taxon.TaxonNode;

public class DistributionEditorUtil {

	public static final String VIEW_TABLE = "table";

	public static final String SATTR_SELECTED_AREAS = "selectedAreas";

    public static final String SATTR_SELECTED_VOCABULARY_UUID = "selectedVocabularyUuid";

	public static final String SATTR_TAXON_NODE_UUID = "taxonNodeUUID";

    public static final String SATTR_ABBREVIATED_LABELS = "abbreviatedLabels";

    public static final String SATTR_DISTRIBUTION_STATUS = "distributionStatus";

    public static final String SEPARATOR = ";;";

    public static void openDistributionView(TaxonNode taxonNode, TermVocabulary<NamedArea> term, Set<NamedArea> selectedAreas) {
	    VaadinSession.getCurrent().setAttribute(SATTR_TAXON_NODE_UUID, taxonNode.getUuid());
	    VaadinSession.getCurrent().setAttribute(SATTR_SELECTED_VOCABULARY_UUID, term.getUuid());
	    VaadinSession.getCurrent().setAttribute(SATTR_SELECTED_AREAS, selectedAreas);

	    //navigate to table view
	    UI.getCurrent().getNavigator().navigateTo(VIEW_TABLE);
	}

    public static boolean isAbbreviatedLabels(){
    	Object isAbbreviated = VaadinSession.getCurrent().getAttribute(DistributionEditorUtil.SATTR_ABBREVIATED_LABELS);
		return (isAbbreviated==null || (boolean) isAbbreviated);
    }

	public static final String SATTR_CONVERSATION = "conversation";


}
