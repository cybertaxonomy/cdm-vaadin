// $Id$
/**
 * Copyright (C) 2015 EDIT
 * European Distributed Institute of Taxonomy
 * http://www.e-taxonomy.eu
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * See LICENSE.TXT at the top of this package for the full license terms.
 */
package eu.etaxonomy.cdm.vaadin.jscomponent;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.Notification;

/**
 * @author cmathew
 * @date 8 Apr 2015
 *
 */
@StyleSheet({"css/d3.ctree.css"})
@JavaScript({"extlib/d3.min.js", "lib/d3ctree_connector.js"})
public class D3CTree extends AbstractJavaScriptComponent {

    private static final Logger logger = Logger.getLogger(D3CTree.class);

    public D3CTree() {
        addFunction("test", new JavaScriptFunction() {

            @Override
            public void call(JSONArray arguments) throws JSONException {
                Notification.show("JS Rpc call : click on " + arguments.getString(0));
            }
        });

        try {
            JSONObject taxonFrom = new JSONObject();
            taxonFrom.put("name", "taxon from");


            JSONArray tfChildren = new JSONArray();
            taxonFrom.put("children", tfChildren);

            JSONObject conceptRelationship1 = new JSONObject();
            conceptRelationship1.put("name", "concept relationship 1");

            JSONArray cr1Children = new JSONArray();
            conceptRelationship1.put("children", cr1Children);

            JSONObject conceptRelationship2 = new JSONObject();
            conceptRelationship2.put("name", "concept relationship 2");

            JSONArray cr2Children = new JSONArray();
            conceptRelationship2.put("children", cr2Children);

            tfChildren.put(0, conceptRelationship1);
            tfChildren.put(1, conceptRelationship2);

            JSONObject taxonTo = new JSONObject();
            taxonTo.put("name", "taxon to");

            cr1Children.put(0, taxonTo);
            cr2Children.put(0, taxonTo);
            String conceptRelationshipTree = taxonFrom.toString();

            logger.warn("conceptRelationshipTree : " + conceptRelationshipTree);

            setConceptRelationshipTree(conceptRelationshipTree);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void setConceptRelationshipTree(String conceptRelationshipTree) {
        getState().setConceptRelationshipTree(conceptRelationshipTree);;
    }

    @Override
    public D3CTreeState getState() {
        return (D3CTreeState) super.getState();
    }

}
