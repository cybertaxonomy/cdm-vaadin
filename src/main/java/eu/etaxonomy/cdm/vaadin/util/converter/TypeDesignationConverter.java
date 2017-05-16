/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import eu.etaxonomy.cdm.model.common.Language;
import eu.etaxonomy.cdm.model.name.NameTypeDesignation;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignation;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignationStatus;
import eu.etaxonomy.cdm.model.name.TaxonNameBase;
import eu.etaxonomy.cdm.model.name.TypeDesignationBase;
import eu.etaxonomy.cdm.model.name.TypeDesignationStatusBase;

/**
 * Converts a collection of TypeDesignations, which should belong to the
 * same name of course, into a string representation.
 *
 * Order of TypeDesignations in the resulting string:
 *  Type, Holotype, Lectotype, Epitypes
 * @author a.kohlbecker
 * @since Mar 10, 2017
 *
 */
public class TypeDesignationConverter {


    private final String separator = ", ";

    private Collection<TypeDesignationBase> typeDesignations;
    private Map<TypeDesignationStatusBase<?>, Collection<String>> orderedStrings;

    private String finalString = null;

    private String typifiedNameCache = null;

    /**
     * @param taxonNameBase
     *
     */
    public TypeDesignationConverter(Collection<TypeDesignationBase> typeDesignations, TaxonNameBase taxonNameBase) {
        this.typeDesignations = typeDesignations;
        orderedStrings = new HashMap<>(typeDesignations.size());
        if(taxonNameBase != null){
            this.typifiedNameCache = taxonNameBase.getTitleCache();
        }
    }

    private void putString(TypeDesignationStatusBase<?> status, String string){
        // the cdm orderd term bases are ordered invers, fixing this for here
        if(status == null){
            status = SpecimenTypeDesignationStatus.TYPE();
        }
        if(!orderedStrings.containsKey(status)){
            orderedStrings.put(status, new ArrayList<String>());
        }
        orderedStrings.get(status).add(string);
    }


    public TypeDesignationConverter buildString(){

        typeDesignations.forEach(td -> putString(td.getTypeStatus(), stringify(td)));

        StringBuilder sb = new StringBuilder();

        if(typifiedNameCache != null){
            sb.append(typifiedNameCache).append(": ");
        }
        List<TypeDesignationStatusBase<?>> keyList = new LinkedList<>(orderedStrings.keySet());

        Collections.sort(keyList, new Comparator<TypeDesignationStatusBase>() {
            @Override
            public int compare(TypeDesignationStatusBase o1, TypeDesignationStatusBase o2) {
                // fix inverted order of cdm terms by -1*
                return -1 * o1.compareTo(o2);
            }
        });

        keyList.forEach(key -> {
            if(key.equals( SpecimenTypeDesignationStatus.TYPE())){
                sb.append("Type");
            } else {
                sb.append(key.getPreferredRepresentation(Language.DEFAULT()));
            }
            sb.append(": ");
            orderedStrings.get(key).forEach(str -> {
                sb.append(str);
                if(sb.length() > 0){
                    sb.append(separator);
                }
            });
        });

        finalString  = sb.toString();
        return this;
    }

    /**
     * @param td
     * @return
     */
    private String stringify(TypeDesignationBase td) {

        if(td instanceof NameTypeDesignation){
            return stringify((NameTypeDesignation)td);
        } else {
            return stringify((SpecimenTypeDesignation)td);
        }
    }


    /**
     * @param td
     * @return
     */
    protected String stringify(NameTypeDesignation td) {

        StringBuffer sb = new StringBuffer();

        if(td.getTypeName() != null){
            sb.append(td.getTypeName().getTitleCache());
        }
        if(td.getCitation() != null){
            sb.append(" ").append(td.getCitation().getTitleCache());
            if(td.getCitationMicroReference() != null){
                sb.append(":").append(td.getCitationMicroReference());
            }
        }
        if(td.isNotDesignated()){
            sb.append(" not designated");
        }
        if(td.isRejectedType()){
            sb.append(" rejected");
        }
        if(td.isConservedType()){
            sb.append(" conserved");
        }
        return sb.toString();
    }

    /**
     * @param td
     * @return
     */
    private String stringify(SpecimenTypeDesignation td) {
        StringBuffer sb = new StringBuffer();

        if(td.getTypeSpecimen() != null){
            String nameTitleCache = td.getTypeSpecimen().getTitleCache();
            if(typifiedNameCache != null){
                nameTitleCache = nameTitleCache.replace(typifiedNameCache, "");
            }
            sb.append(nameTitleCache);
        }

        if(td.getCitation() != null){
            sb.append(" ").append(td.getCitation().getTitleCache());
            if(td.getCitationMicroReference() != null){
                sb.append(" :").append(td.getCitationMicroReference());
            }
        }
        if(td.isNotDesignated()){
            sb.append(" not designated");
        }

        return sb.toString();
    }

    public String print(){
        return finalString;
    }
}
