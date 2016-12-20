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

import elemental.json.JsonArray;

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
        addFunction("select", new JavaScriptFunction() {

            @Override
			public void call(JsonArray arguments) {
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
            //String conceptRelationshipTree = taxonFrom.toString();
            String conceptRelationshipTree = "{\"direction\":\"right-left\",\"name\":\"Taxon d\",\"children\":[{\"name\":\"Congruent to\",\"children\":[{\"name\":\"Taxon e\",\"uuid\":\"84e99e24-f50a-4726-92d0-6088430c492a\",\"type\":\"taxon\"}],\"uuid\":\"511f504b-ae3b-4f04-b7b9-35c222f06e10\",\"type\":\"conceptr\"},{\"name\":\"Includes\",\"children\":[{\"name\":\"Taxon a\",\"uuid\":\"eaac797e-cac7-4649-97cf-c7b580076895\",\"type\":\"taxon\"}],\"uuid\":\"0e8b7922-974d-4389-b71e-af6fc9f98c56\",\"type\":\"conceptr\"},{\"name\":\"Includes\",\"children\":[{\"name\":\"Taxon b\",\"uuid\":\"5004a8e7-b907-4744-b67e-44ccb057ab3b\",\"type\":\"taxon\"}],\"uuid\":\"6fd9947e-21c3-4190-8748-57d9661e8659\",\"type\":\"conceptr\"},{\"name\":\"Excludes\",\"children\":[{\"name\":\"Taxon c\",\"uuid\":\"3d71c8b8-3bec-4f5f-ba23-6f9d55ef84e9\",\"type\":\"taxon\"}],\"uuid\":\"cc761030-38d2-4b5d-954d-32329c0ea106\",\"type\":\"conceptr\"}],\"uuid\":\"5f713f69-e03e-4a11-8a55-700fbbf44805\",\"type\":\"taxon\"}";
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
