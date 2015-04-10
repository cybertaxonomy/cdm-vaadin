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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.model.taxon.TaxonRelationship;
import eu.etaxonomy.cdm.model.taxon.TaxonRelationshipType;

/**
 * @author cmathew
 * @date 8 Apr 2015
 *
 */
@StyleSheet({"css/d3.conceptrelationshiptree.css"})
@JavaScript({"extlib/d3.min.js", "lib/d3.conceptrelationshiptree_connector.js"})
public class D3ConceptRelationshipTree extends AbstractJavaScriptComponent {

    private static final Logger logger = Logger.getLogger(D3ConceptRelationshipTree.class);

    public D3ConceptRelationshipTree() {
        addFunction("test", new JavaScriptFunction() {

            @Override
            public void call(JSONArray arguments) throws JSONException {
                Notification.show("Store selected","uuid : " + arguments.getString(0), Type.WARNING_MESSAGE);
            }
        });
        //String expected = "{\"name\":\"Taxon D sec. ???\",\"children\":[{\"name\":\"Includes\",\"children\":[{\"name\":\"Taxon A sec. Journal Reference 1\",\"uuid\":\"eaac797e-cac7-4649-97cf-c7b580076895\"},{\"name\":\"Taxon B sec. ???\",\"uuid\":\"77e7d93e-75c6-4dd4-850d-7b5809654378\"}],\"uuid\":\"0501c385-cab1-4fbe-b945-fc747419bb13\"},{\"name\":\"Excludes\",\"children\":[{\"name\":\"Taxon C sec. ???\",\"uuid\":\"3d71c8b8-3bec-4f5f-ba23-6f9d55ef84e9\"}],\"uuid\":\"4535a63c-4a3f-4d69-9350-7bf02e2c23be\"}],\"uuid\":\"5f713f69-e03e-4a11-8a55-700fbbf44805\"}";
        //setConceptRelationshipTree(expected);
        setConceptRelationshipTree("");

    }

    public void updateConceptRelationshipTree(Taxon fromTaxon) throws JSONException {
        Set<TaxonRelationship> relationsFromThisTaxon = fromTaxon.getRelationsFromThisTaxon();

        Map<TaxonRelationshipType, List<Taxon>> relToTaxonMap = new HashMap<TaxonRelationshipType, List<Taxon>>();


        JSONObject fromTaxonJO = new JSONObject();
        fromTaxonJO.put("name", fromTaxon.getTitleCache());
        fromTaxonJO.put("uuid", fromTaxon.getUuid().toString());

        if(relationsFromThisTaxon !=null && !relationsFromThisTaxon.isEmpty()) {
            for(TaxonRelationship tr : relationsFromThisTaxon) {
                if(fromTaxon.equals(tr.getFromTaxon())) {
                    if(relToTaxonMap.containsKey(tr.getType())) {
                        relToTaxonMap.get(tr.getType()).add(tr.getToTaxon());
                    } else {
                        List<Taxon> toTaxonList = new ArrayList<Taxon>();
                        toTaxonList.add(tr.getToTaxon());
                        relToTaxonMap.put(tr.getType(), toTaxonList);
                    }
                }
            }

            int typeIndex = 0;
            JSONArray ftChildren = new JSONArray();
            fromTaxonJO.put("children", ftChildren);

            for (Map.Entry<TaxonRelationshipType, List<Taxon>> entry : relToTaxonMap.entrySet()) {

                JSONObject crJO = new JSONObject();
                crJO.put("name", entry.getKey().getTitleCache());
                crJO.put("uuid", entry.getKey().getUuid());

                JSONArray crChildrenJA = new JSONArray();
                crJO.put("children", crChildrenJA);

                int toTaxonIndex = 0;
                for(Taxon toTaxon: entry.getValue()) {
                    JSONObject toTaxonJO = new JSONObject();
                    toTaxonJO.put("name", toTaxon.getTitleCache());
                    toTaxonJO.put("uuid", toTaxon.getUuid());
                    crChildrenJA.put(toTaxonIndex, toTaxonJO);
                    toTaxonIndex++;
                }

                ftChildren.put(typeIndex, crJO);
                typeIndex++;
            }
        }


        setConceptRelationshipTree(fromTaxonJO.toString());;
    }

    public void setConceptRelationshipTree(String conceptRelationshipTree) {
        getState().setConceptRelationshipTree(conceptRelationshipTree);;
    }

    @Override
    public D3ConceptRelationshipTreeState getState() {
        return (D3ConceptRelationshipTreeState) super.getState();
    }

}
