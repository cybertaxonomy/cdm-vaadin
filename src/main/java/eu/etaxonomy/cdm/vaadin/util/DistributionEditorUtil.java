package eu.etaxonomy.cdm.vaadin.util;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.location.NamedArea;
import eu.etaxonomy.cdm.model.taxon.Classification;
import eu.etaxonomy.cdm.model.taxon.TaxonNode;
import eu.etaxonomy.cdm.vaadin.view.distributionStatus.IDistributionTableView;

/**
 * A utility class for the distribution status editor.
 *
 */
public class DistributionEditorUtil {

	public static final String VIEW_TABLE = "table";

	public static final String SATTR_SELECTED_AREAS = "selectedAreas";

    public static final String SATTR_SELECTED_AREA_VOCABULARY_UUID = "selectedVocabularyUuid";

	public static final String SATTR_TAXON_NODES_UUID = "taxonNodesUUID";

	public static final String SATTR_CLASSIFICATION = "classificationUUID";

    public static final String SATTR_ABBREVIATED_LABELS = "abbreviatedLabels";

    public static final String SATTR_DISTRIBUTION_STATUS = "distributionStatus";

    public static final String SEPARATOR = ";;";

    /**
     * Updates the vaadin session attributes related to the chosen 
     * {@link TaxonNode}s, {@link NamedArea}s and {@link Classification}
     * and refreshes the given {@code distributionStatusTableView}.
     * @param distributionTableView The view to refresh after updating the session variables.
     * @param taxonNodes The taxa to be shown in the {@code distributionTableView}.
     * @param areaVoc The {@link TermVocabulary} of {@link NamedArea}s to be used.
     * @param selectedAreas The {@link NamedArea}s to be availbale in the {@code distributionTableView}.
     * @param classificationUuid The {@link UUID} of the {@link Classification} the {@code taxonNodes} belong to.
     */
    public static void updateDistributionView(IDistributionTableView distributionTableView, List<UUID> taxonNodes, TermVocabulary<NamedArea> areaVoc,
            List<NamedArea> selectedAreas, UUID classificationUuid) {
	    VaadinSession.getCurrent().setAttribute(SATTR_TAXON_NODES_UUID, taxonNodes);
	    VaadinSession.getCurrent().setAttribute(SATTR_SELECTED_AREA_VOCABULARY_UUID, areaVoc.getUuid());
	    VaadinSession.getCurrent().setAttribute(SATTR_SELECTED_AREAS, selectedAreas);
	    VaadinSession.getCurrent().setAttribute(SATTR_CLASSIFICATION, classificationUuid);
	    distributionTableView.update();
	}

    /**
     * Clears the session attributes related to the chosen 
     * {@link TaxonNode}s, {@link TermVocabulary} of {@link NamedArea}s,
     * {@link NamedArea}s and {@link Classification}.
     */
    public static void clearSessionAttributes(){
    	VaadinSession.getCurrent().setAttribute(SATTR_TAXON_NODES_UUID, null);
    	VaadinSession.getCurrent().setAttribute(SATTR_SELECTED_AREA_VOCABULARY_UUID, null);
    	VaadinSession.getCurrent().setAttribute(SATTR_SELECTED_AREAS, null);
    	VaadinSession.getCurrent().setAttribute(SATTR_CLASSIFICATION, null);
    }

    /**
     * Returns {@code true} if abbreviated labels should be used.
     * @return {@code true} if abbreviated labels should be used.
     */
    public static boolean isAbbreviatedLabels(){
    	Object isAbbreviated = VaadinSession.getCurrent().getAttribute(DistributionEditorUtil.SATTR_ABBREVIATED_LABELS);
		return (isAbbreviated==null || (boolean) isAbbreviated);
    }

    /**
     * Shows an {@link SQLException} to the user.
     * @param e The exception to show.
     */
	public static void showSqlError(SQLException e) {
		Notification.show("Error while accessing data base.","Cause: "+e.getMessage(), Type.ERROR_MESSAGE);
		e.printStackTrace();
	}

	public static final String SATTR_CONVERSATION = "conversation";


}
