/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.jscomponent;

import com.vaadin.shared.ui.JavaScriptComponentState;

/**
 * @author cmathew
 * @date 8 Apr 2015
 *
 */
public class D3ConceptRelationshipTreeState extends JavaScriptComponentState {

    private String conceptRelationshipTree;


    public String getConceptRelationshipTree() {
        return conceptRelationshipTree;
    }

    public void setConceptRelationshipTree(String conceptRelationshipTree) {
        this.conceptRelationshipTree = conceptRelationshipTree;
    }
}
