/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.distributionStatus;

import com.vaadin.navigator.View;

import eu.etaxonomy.vaadin.mvp.ApplicationView;

/**
 * @author freimeier
 * @since 18.10.2017
 *
 */
public interface DistributionTableView extends ApplicationView<DistributionTablePresenter>, View {

    /**
     * Updates Distribution Table.
     */
	public void update();

	/**
	 * Opens the setting window to change available distribution status.
	 */
	public void openSettings();

	/**
	 * Opens the distribution-setting window to change available areas and chose classification.
	 */
	public void openDistributionSettings();

}