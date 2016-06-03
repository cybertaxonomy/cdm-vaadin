// $Id$
/**
* Copyright (C) 2016 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.container;

import java.util.List;

import com.vaadin.data.util.HierarchicalContainer;

import eu.etaxonomy.cdm.model.taxon.TaxonNode;

/**
 * @author pplitzner
 * @date 03.06.2016
 *
 */
public class TaxonNodeContainer extends HierarchicalContainer {

    public TaxonNodeContainer(List<TaxonNode> rootNodes) {
        for(TaxonNode rootNode:rootNodes){
            addItem(rootNode);
            //        setItemCaption(rootNode, rootNode.getClassification().getName().toString());
            setParent(rootNode, null);
            addChildNodes(rootNode);
        }
    }

    private void addChildNodes(TaxonNode parentNode) {
        List<TaxonNode> childNodes = parentNode.getChildNodes();
        if(childNodes.size()==0){
            //set node to be a leaf
            setChildrenAllowed(parentNode, false);
        }
        for (TaxonNode taxonNode : childNodes) {
            if(taxonNode!=null){//TODO when does this happen?? orphaned taxa??
                addItem(taxonNode);
                //            setItemCaption(taxonNode, taxonNode.getTaxon().getName().getTitleCache());
                setParent(taxonNode, parentNode);
                addChildNodes(taxonNode);
            }
        }
    }

}
