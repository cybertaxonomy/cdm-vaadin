// $Id$
/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.i10n;

import org.eclipse.osgi.util.NLS;

/**
 * @author freimeier
 * @date 27.11.2017
 *
 */
public class Messages extends NLS {
    private static final String BUNDLE_NAME = "eu.etaxonomy.cdm.i10n.messages"; //$NON-NLS-1$
    public static String AreaAndTaxonSettingsConfigWindow_AREAS;
    public static String AreaAndTaxonSettingsConfigWindow_CLASSIFICATION;
    public static String AreaAndTaxonSettingsConfigWindow_DISTRIBUTION_AREA;
    public static String AreaAndTaxonSettingsConfigWindow_FILTER;
    public static String AreaAndTaxonSettingsConfigWindow_FILTER_TAXA_BY_NAME;
    public static String AreaAndTaxonSettingsConfigWindow_LOADING_COMPLETE;
    public static String AreaAndTaxonSettingsConfigWindow_LOADING_TAXA;
    public static String AreaAndTaxonSettingsConfigWindow_SELECT_CLASSIFICATION;
    public static String AreaAndTaxonSettingsConfigWindow_SELECT_DISTRIBUTION_AREA;
    public static String AreaAndTaxonSettingsConfigWindow_TAXONOMY;
    public static String DetailWindow_NO_DESCRIPTIVE_DATA_FOUND;
    public static String DistributionStatusSettingsConfigWindow_DISTRIBUTION_STATUS;
    public static String DistributionStatusSettingsConfigWindow_SHOW_ABBREVIATED_LABELS;
    public static String DistributionTablePresenter_ERROR_UPDATE_DISTRIBUTION_TERM;
    public static String DistributionTableViewBean_AREAS_AND_TAXA;
    public static String DistributionTableViewBean_SELECT_TAXON;
    public static String DistributionTableViewBean_STATUS;
    public static String DistributionTableViewBean_TAXON;
    public static String DistributionTableViewBean_TAXON_DETAILS;
    public static String DistributionTableViewBean_TOTAL_TAXA;
    public static String DistributionToolbar_AREAS_AND_TAXA;
    public static String DistributionToolbar_DETAIL;
    public static String DistributionToolbar_EDIT;
    public static String DistributionToolbar_LOGIN;
    public static String DistributionToolbar_LOGOUT;
    public static String DistributionToolbar_SAVE;
    public static String DistributionToolbar_STATUS;
    public static String SettingsDialogWindowBase_CANCEL;
    public static String SettingsDialogWindowBase_OK;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
