/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.distributionStatus;

import com.vaadin.data.fieldgroup.FieldGroup;

/**
 * @author freimeier
 * @since 18.10.2017
 *
 */
public class NoCommitFieldGroup extends FieldGroup{
	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Override
	public void commit() {}
}
