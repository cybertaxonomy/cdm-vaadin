// $Id$
/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.i18n;

import java.io.Serializable;
import java.util.ResourceBundle;

import com.vaadin.server.VaadinSession;

/**
 * @author freimeier
 * @date 07.12.2017
 *
 */
public class Messages implements Serializable {
    public static final String BUNDLE_NAME = "eu.etaxonomy.cdm.i18n.messages";
    public static String AreaAndTaxonSettingsConfigWindow_AREAS = "AreaAndTaxonSettingsConfigWindow_AREAS";
    public static String AreaAndTaxonSettingsConfigWindow_CLASSIFICATION = "AreaAndTaxonSettingsConfigWindow_CLASSIFICATION";
    public static String AreaAndTaxonSettingsConfigWindow_DISTRIBUTION_AREA = "AreaAndTaxonSettingsConfigWindow_DISTRIBUTION_AREA";
    public static String AreaAndTaxonSettingsConfigWindow_FILTER = "AreaAndTaxonSettingsConfigWindow_FILTER";
    public static String AreaAndTaxonSettingsConfigWindow_FILTER_TAXA_BY_NAME = "AreaAndTaxonSettingsConfigWindow_FILTER_TAXA_BY_NAME";
    public static String AreaAndTaxonSettingsConfigWindow_LOADING_COMPLETE = "AreaAndTaxonSettingsConfigWindow_LOADING_COMPLETE";
    public static String AreaAndTaxonSettingsConfigWindow_LOADING_TAXA = "AreaAndTaxonSettingsConfigWindow_LOADING_TAXA";
    public static String AreaAndTaxonSettingsConfigWindow_SELECT_CLASSIFICATION = "AreaAndTaxonSettingsConfigWindow_SELECT_CLASSIFICATION";
    public static String AreaAndTaxonSettingsConfigWindow_SELECT_DISTRIBUTION_AREA = "AreaAndTaxonSettingsConfigWindow_SELECT_DISTRIBUTION_AREA";
    public static String AreaAndTaxonSettingsConfigWindow_TAXONOMY = "AreaAndTaxonSettingsConfigWindow_TAXONOMY";
    public static String DetailWindow_NO_DESCRIPTIVE_DATA_FOUND = "DetailWindow_NO_DESCRIPTIVE_DATA_FOUND";
    public static String DistributionStatusSettingsConfigWindow_DISTRIBUTION_STATUS = "DistributionStatusSettingsConfigWindow_DISTRIBUTION_STATUS";
    public static String DistributionStatusSettingsConfigWindow_SHOW_ABBREVIATED_LABELS = "DistributionStatusSettingsConfigWindow_SHOW_ABBREVIATED_LABELS";
    public static String DistributionTablePresenter_ERROR_UPDATE_DISTRIBUTION_TERM = "DistributionTablePresenter_ERROR_UPDATE_DISTRIBUTION_TERM";
    public static String DistributionTableViewBean_AREAS_AND_TAXA = "DistributionTableViewBean_AREAS_AND_TAXA";
    public static String DistributionTableViewBean_CHOOSE_DISTRIBUTION_STATUS = "DistributionTableViewBean_CHOOSE_DISTRIBUTION_STATUS";
    public static String DistributionTableViewBean_NO_STATUS_SELECT = "DistributionTableViewBean_NO_STATUS_SELECT";
    public static String DistributionTableViewBean_SELECT_TAXON = "DistributionTableViewBean_SELECT_TAXON";
    public static String DistributionTableViewBean_STATUS = "DistributionTableViewBean_STATUS";
    public static String DistributionTableViewBean_TAXON = "DistributionTableViewBean_TAXON";
    public static String DistributionTableViewBean_TAXON_DETAILS = "DistributionTableViewBean_TAXON_DETAILS";
    public static String DistributionTableViewBean_TOTAL_TAXA = "DistributionTableViewBean_TOTAL_TAXA";
    public static String DistributionToolbar_AREAS_AND_TAXA = "DistributionToolbar_AREAS_AND_TAXA";
    public static String DistributionToolbar_DETAIL = "DistributionToolbar_DETAIL";
    public static String DistributionToolbar_EDIT = "DistributionToolbar_EDIT";
    public static String DistributionToolbar_LOGIN = "DistributionToolbar_LOGIN";
    public static String DistributionToolbar_LOGOUT = "DistributionToolbar_LOGOUT";
    public static String DistributionToolbar_SAVE = "DistributionToolbar_SAVE";
    public static String DistributionToolbar_STATUS = "DistributionToolbar_STATUS";
    public static String SettingsDialogWindowBase_CANCEL = "SettingsDialogWindowBase_CANCEL";
    public static String SettingsDialogWindowBase_OK = "SettingsDialogWindowBase_OK";

    public static String getLocalizedString(String key) {
        return ResourceBundle.getBundle(BUNDLE_NAME, VaadinSession.getCurrent().getLocale()).getString(key);
    }
}
