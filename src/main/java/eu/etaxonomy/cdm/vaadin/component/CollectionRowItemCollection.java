/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component;

import java.util.ArrayList;

import com.vaadin.ui.Component;

/**
 * @author a.kohlbecker
 * @since Dec 21, 2017
 *
 */
public class CollectionRowItemCollection extends ArrayList<Component> {

    private static final long serialVersionUID = -5604424783816645364L;

    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(Class<T> type, int rowposition){
        Component c = get(rowposition);
        if(type.equals(c.getClass())){
            return (T)c;
        } else {
            return null;
        }
    }

}
