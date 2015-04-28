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
import java.util.UUID;

import org.apache.log4j.Logger;

import com.google.gwt.json.client.JSONException;
import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.JavaScriptFunction;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.model.taxon.TaxonRelationship;
import eu.etaxonomy.cdm.model.taxon.TaxonRelationshipType;
import eu.etaxonomy.cdm.vaadin.component.ConceptRelationshipComposite;

/**
 * @author cmathew
 * @date 8 Apr 2015
 *
 */
@StyleSheet({"css/d3.conceptrelationshiptree.css"})
@JavaScript({"extlib/d3.min.js", "lib/d3.conceptrelationshiptree_connector.js"})
public class D3ConceptRelationshipTree extends AbstractJavaScriptComponent {

    private static final Logger logger = Logger.getLogger(D3ConceptRelationshipTree.class);

    public enum Mode {
        OneToOne,
        Group
    }

    private Mode mode;

    public enum Direction {
        LEFT_RIGHT("left-right"),
        RIGHT_LEFT("right-left");

        private final String name;

        private Direction(String s) {
            name = s;
        }

        @Override
        public String toString(){
           return name;
        }
    }

    private Direction direction;

    private ConceptRelationshipComposite conceptRelComposite;

    public D3ConceptRelationshipTree() {
        this(Mode.OneToOne, Direction.LEFT_RIGHT);
    }

    public D3ConceptRelationshipTree(Direction direction) {
        this(Mode.OneToOne, direction);
    }

    public D3ConceptRelationshipTree(Mode mode, Direction direction) {
        this.mode = mode;
        this.direction = direction;
        addFunction("select", new JavaScriptFunction() {

            @Override
            public void call(JsonArray arguments) throws JSONException {
                //Notification.show("Store selected","uuid : " + arguments.getJSONObject(0).getString("uuid"), Type.WARNING_MESSAGE);
                if(conceptRelComposite != null) {
                    UUID relUuid = UUID.fromString(arguments.getString(0));
                    conceptRelComposite.setSelectedTaxonRelUuid(relUuid);
                }
            }
        });
        setConceptRelationshipTree("");

    }

    public void setConceptRelComposite(ConceptRelationshipComposite conceptRelComposite) {
        this.conceptRelComposite = conceptRelComposite;
    }


    public void update(Taxon fromTaxon, Direction direction) {
        this.direction = direction;
        switch(mode) {
        case OneToOne:
            updateForOneToOne(fromTaxon);
            break;
        case Group:
            updateForGroup(fromTaxon);
            break;
        default:
            updateForOneToOne(fromTaxon);
        }
    }

    private void updateForOneToOne(Taxon fromTaxon){
        Set<TaxonRelationship> relationsFromThisTaxon = fromTaxon.getRelationsFromThisTaxon();

        Map<TaxonRelationshipType, List<Taxon>> relToTaxonMap = new HashMap<TaxonRelationshipType, List<Taxon>>();


        JsonObject fromTaxonJO = Json.createObject();
        fromTaxonJO.put("name", fromTaxon.getName().getTitleCache());
        fromTaxonJO.put("uuid", fromTaxon.getUuid().toString());
        fromTaxonJO.put("type", "taxon");
        fromTaxonJO.put("direction", direction.toString());

        JsonArray ftChildren = Json.createArray();
        fromTaxonJO.put("children", ftChildren);

        int typeIndex = 0;
        if(relationsFromThisTaxon !=null && !relationsFromThisTaxon.isEmpty()) {
            for(TaxonRelationship tr : relationsFromThisTaxon) {
                if(tr != null && fromTaxon.equals(tr.getFromTaxon())) {


                    JsonObject crJO = Json.createObject();
                    crJO.put("name", tr.getType().getTitleCache());
                    crJO.put("uuid", tr.getUuid().toString());
                    crJO.put("type", "conceptr");

                    ftChildren.set(typeIndex, crJO);

                    JsonArray crChildrenJA = Json.createArray();
                    crJO.put("children", crChildrenJA);

                    Taxon toTaxon = tr.getToTaxon();

                    JsonObject toTaxonJO = Json.createObject();
                    toTaxonJO.put("name", toTaxon.getName().getTitleCache());
                    toTaxonJO.put("uuid", toTaxon.getUuid().toString());
                    toTaxonJO.put("type", "taxon");

                    crChildrenJA.set(0, toTaxonJO);
                    typeIndex++;
                }
            }
        }
        setConceptRelationshipTree(fromTaxonJO.toString());
    }

    private void updateForGroup(Taxon fromTaxon) {
        Set<TaxonRelationship> relationsFromThisTaxon = fromTaxon.getRelationsFromThisTaxon();

        Map<TaxonRelationshipType, List<Taxon>> relToTaxonMap = new HashMap<TaxonRelationshipType, List<Taxon>>();


        JsonObject fromTaxonJO = Json.createObject();
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
            JsonArray ftChildren = Json.createArray();
            fromTaxonJO.put("children", ftChildren);

            for (Map.Entry<TaxonRelationshipType, List<Taxon>> entry : relToTaxonMap.entrySet()) {

                JsonObject crJO = Json.createObject();
                crJO.put("name", entry.getKey().getTitleCache());
                crJO.put("uuid", entry.getKey().getUuid().toString());

                JsonArray crChildrenJA = Json.createArray();
                crJO.put("children", crChildrenJA);

                int toTaxonIndex = 0;
                for(Taxon toTaxon: entry.getValue()) {
                    JsonObject toTaxonJO = Json.createObject();
                    toTaxonJO.put("name", toTaxon.getTitleCache());
                    toTaxonJO.put("uuid", toTaxon.getUuid().toString());
                    crChildrenJA.set(toTaxonIndex, toTaxonJO);
                    toTaxonIndex++;
                }

                ftChildren.set(typeIndex, crJO);
                typeIndex++;
            }
        }
        setConceptRelationshipTree(fromTaxonJO.toString());
    }


    public void setConceptRelationshipTree(String conceptRelationshipTree) {
        getState().setConceptRelationshipTree(conceptRelationshipTree);;
    }

    @Override
    public D3ConceptRelationshipTreeState getState() {
        return (D3ConceptRelationshipTreeState) super.getState();
    }

}
