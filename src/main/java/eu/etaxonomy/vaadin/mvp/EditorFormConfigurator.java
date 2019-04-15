/**
* Copyright (C) 2019 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.mvp;

/**
 * @author a.kohlbecker
 * @since Mar 22, 2019
 *
 */
public interface EditorFormConfigurator<T extends AbstractPopupEditor<?,?>> {

    public void updateComponentStates(AbstractPopupEditor<?, ?> abstractPopupEditor);
}
