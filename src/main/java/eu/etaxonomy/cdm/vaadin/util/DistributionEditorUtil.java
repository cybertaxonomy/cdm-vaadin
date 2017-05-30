package eu.etaxonomy.cdm.vaadin.util;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.location.NamedArea;
import eu.etaxonomy.cdm.vaadin.view.distributionStatus.DistributionTableView;

public class DistributionEditorUtil {

	public static final String VIEW_TABLE = "table";

	public static final String SATTR_SELECTED_AREAS = "selectedAreas";

    public static final String SATTR_SELECTED_VOCABULARY_UUID = "selectedVocabularyUuid";

	public static final String SATTR_TAXON_NODES_UUID = "taxonNodesUUID";

	public static final String SATTR_CLASSIFICATION = "classificationUUID";

    public static final String SATTR_ABBREVIATED_LABELS = "abbreviatedLabels";

    public static final String SATTR_DISTRIBUTION_STATUS = "distributionStatus";

    public static final String SEPARATOR = ";;";

    public static void updateDistributionView(DistributionTableView distributionTableView, List<UUID> taxonNodes, TermVocabulary<NamedArea> term, Set<NamedArea> selectedAreas, UUID classificationUuid) {
	    VaadinSession.getCurrent().setAttribute(SATTR_TAXON_NODES_UUID, taxonNodes);
	    VaadinSession.getCurrent().setAttribute(SATTR_SELECTED_VOCABULARY_UUID, term.getUuid());
	    VaadinSession.getCurrent().setAttribute(SATTR_SELECTED_AREAS, selectedAreas);
	    VaadinSession.getCurrent().setAttribute(SATTR_CLASSIFICATION, classificationUuid);
	    distributionTableView.update();
	}

    public static void clearSessionAttributes(){
    	VaadinSession.getCurrent().setAttribute(SATTR_TAXON_NODES_UUID, null);
    	VaadinSession.getCurrent().setAttribute(SATTR_SELECTED_VOCABULARY_UUID, null);
    	VaadinSession.getCurrent().setAttribute(SATTR_SELECTED_AREAS, null);
    	VaadinSession.getCurrent().setAttribute(SATTR_CLASSIFICATION, null);
    }

    public static boolean isAbbreviatedLabels(){
    	Object isAbbreviated = VaadinSession.getCurrent().getAttribute(DistributionEditorUtil.SATTR_ABBREVIATED_LABELS);
		return (isAbbreviated==null || (boolean) isAbbreviated);
    }

	public static void showSqlError(SQLException e) {
		Notification.show("Error while accessing data base.","Cause: "+e.getMessage(), Type.ERROR_MESSAGE);
		e.printStackTrace();
	}

	public static final String SATTR_CONVERSATION = "conversation";


}
