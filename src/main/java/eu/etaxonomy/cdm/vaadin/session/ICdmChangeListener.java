/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.session;

/**
 * @author cmathew
 * @since 7 Apr 2015
 *
 */
public interface ICdmChangeListener {

    public void onCreate(CdmChangeEvent event);
    public void onUpdate(CdmChangeEvent event);
    public void onDelete(CdmChangeEvent event);

}
