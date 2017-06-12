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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import eu.etaxonomy.cdm.model.common.Language;
import eu.etaxonomy.cdm.model.name.NameTypeDesignation;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignation;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignationStatus;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.name.TypeDesignationBase;
import eu.etaxonomy.cdm.model.name.TypeDesignationStatusBase;
import eu.etaxonomy.cdm.vaadin.model.EntityReference;
import eu.etaxonomy.cdm.vaadin.view.registration.RegistrationValidationException;

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
    private Map<TypeDesignationStatusBase<?>, Collection<EntityReference>> orderedStringsByType;
    private LinkedHashMap<String, Collection<EntityReference>> orderedRepresentations = new LinkedHashMap<>();
    private EntityReference typifiedName;

    private String finalString = null;



    /**
     * @param taxonName
     * @throws RegistrationValidationException
     *
     */
    public TypeDesignationConverter(Collection<TypeDesignationBase> typeDesignations) throws RegistrationValidationException {
        this.typeDesignations = typeDesignations;
        orderedStringsByType = new HashMap<>();
        typeDesignations.forEach(td -> putString(td.getTypeStatus(), new EntityReference(td.getId(), stringify(td))));
        orderedRepresentations = buildOrderedRepresentations();
        this.typifiedName = findTypifiedName();
    }

    private LinkedHashMap buildOrderedRepresentations(){

        // 1. order by SpecimenType, NameType

        // SpecimenTypes.........
        // Order SpecimenTypes by GatheringEvent

        List<TypeDesignationStatusBase<?>> keyList = new LinkedList<>(orderedStringsByType.keySet());

        Collections.sort(keyList, new Comparator<TypeDesignationStatusBase>() {
            @Override
            public int compare(TypeDesignationStatusBase o1, TypeDesignationStatusBase o2) {
                // fix inverted order of cdm terms by -1*
                return -1 * o1.compareTo(o2);
            }
        });
        // NameTypes.........

        keyList.forEach(key -> orderedRepresentations.put(getTypeDesignationStytusLabel(key), orderedStringsByType.get(key)));
        return orderedRepresentations;
    }

    public TypeDesignationConverter buildString(){

        StringBuilder sb = new StringBuilder();

        if(getTypifiedNameCache() != null){
            sb.append(getTypifiedNameCache()).append(": ");
        }

        List<String> keyList = new LinkedList<>(orderedRepresentations.keySet());


        keyList.forEach(key -> {
            sb.append(key).append(": ");
            orderedRepresentations.get(key).forEach(isAndString -> {
                sb.append(isAndString.getLabel());
                if(sb.length() > 0){
                    sb.append(separator);
                }
            });
        });

        finalString  = sb.toString();
        return this;
    }

    public Map<String, Collection<EntityReference>> getOrderedTypeDesignationRepresentations() {
        return orderedRepresentations;
    }

    /**
     * FIXME use the validation framework validators and to store the validation problems!!!
     *
     * @return
     * @throws RegistrationValidationException
     */
    private EntityReference findTypifiedName() throws RegistrationValidationException {

        List<String> problems = new ArrayList<>();

        TaxonName typifiedName = null;

        for(TypeDesignationBase<?> typeDesignation : typeDesignations){
            typeDesignation.getTypifiedNames();
            if(typeDesignation.getTypifiedNames().isEmpty()){

                //TODO instead throw RegistrationValidationException()
                problems.add("Missing typifiedName in " + typeDesignation.toString());
                continue;
            }
            if(typeDesignation.getTypifiedNames().size() > 1){
              //TODO instead throw RegistrationValidationException()
                problems.add("Multiple typifiedName in " + typeDesignation.toString());
                continue;
            }
            if(typifiedName == null){
                // remember
                typifiedName = typeDesignation.getTypifiedNames().iterator().next();
            } else {
                // compare
                TaxonName otherTypifiedName = typeDesignation.getTypifiedNames().iterator().next();
                if(typifiedName.getId() != otherTypifiedName.getId()){
                  //TODO instead throw RegistrationValidationException()
                    problems.add("Multiple typifiedName in " + typeDesignation.toString());
                }
            }

        }
        if(!problems.isEmpty()){
            // FIXME use the validation framework
            throw new RegistrationValidationException("Inconsistent type designations", problems);
        }

        if(typifiedName != null){
            return new EntityReference(typifiedName.getId(), typifiedName.getTitleCache());
        }
        return null;
    }


    /**
     * @return the title cache of the typifying name or <code>null</code>
     */
    public String getTypifiedNameCache() {
        if(typifiedName != null){
            return typifiedName.getLabel();
        }
        return null;
    }

    /**
     * @return the title cache of the typifying name or <code>null</code>
     */
    public EntityReference getTypifiedName() {

       return typifiedName;

    }

    /**
     * @param key
     * @return
     */
    protected String getTypeDesignationStytusLabel(TypeDesignationStatusBase<?> key) {
        String typeLable;
        if(key.equals( SpecimenTypeDesignationStatus.TYPE())){
            typeLable = "Type";
        } else {
            typeLable = key.getPreferredRepresentation(Language.DEFAULT()).getLabel();
        }
        return typeLable;
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
            if(getTypifiedNameCache() != null){
                nameTitleCache = nameTitleCache.replace(getTypifiedNameCache(), "");
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

    private void putString(TypeDesignationStatusBase<?> status, EntityReference idAndString){
        // the cdm orderd term bases are ordered invers, fixing this for here
        if(status == null){
            status = SpecimenTypeDesignationStatus.TYPE();
        }
        if(!orderedStringsByType.containsKey(status)){
            orderedStringsByType.put(status, new ArrayList<EntityReference>());
        }
        orderedStringsByType.get(status).add(idAndString);
    }

    public String print(){
        return finalString;
    }
}
