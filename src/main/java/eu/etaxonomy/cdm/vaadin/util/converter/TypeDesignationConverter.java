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
import java.util.Optional;
import java.util.Set;

import eu.etaxonomy.cdm.api.facade.DerivedUnitFacadeCacheStrategy;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.common.Language;
import eu.etaxonomy.cdm.model.name.NameTypeDesignation;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignation;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignationStatus;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.name.TypeDesignationBase;
import eu.etaxonomy.cdm.model.name.TypeDesignationStatusBase;
import eu.etaxonomy.cdm.model.occurrence.DerivedUnit;
import eu.etaxonomy.cdm.model.occurrence.FieldUnit;
import eu.etaxonomy.cdm.model.occurrence.SpecimenOrObservationBase;
import eu.etaxonomy.cdm.vaadin.model.EntityReference;
import eu.etaxonomy.cdm.vaadin.model.TypedEntityReference;
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


    private static final String TYPE_STATUS_SEPARATOR = "; ";

    private static final String TYPE_SEPARATOR = "; ";

    private static final String TYPE_DESIGNATION_SEPARATOR = ", ";

    private Collection<TypeDesignationBase> typeDesignations;

    private int workingSetIdAutoIncrement = 0;

    /**
     * Groups the EntityReferences for each of the TypeDesignations by the according TypeDesignationStatus.
     * The TypeDesignationStatusBase keys are already ordered by the term order defined in the vocabulary.
     */
    private LinkedHashMap<TypedEntityReference, TypeDesignationWorkingSet> orderedByTypesByBaseEntity;


    private EntityReference typifiedName;

    private String finalString = null;

    /**
     * @param taxonName
     * @throws RegistrationValidationException
     *
     */
    public TypeDesignationConverter(CdmBase containgEntity, Collection<TypeDesignationBase> typeDesignations) throws RegistrationValidationException {
        this.typeDesignations = typeDesignations;
        Map<TypedEntityReference, TypeDesignationWorkingSet> byBaseEntityByTypeStatus = new HashMap<>();
        typeDesignations.forEach(td -> mapTypeDesignation(containgEntity, byBaseEntityByTypeStatus, td));
        orderedByTypesByBaseEntity = orderByTypeByBaseEntity(byBaseEntityByTypeStatus);
        this.typifiedName = findTypifiedName();
    }


    private void mapTypeDesignation(CdmBase containgEntity, Map<TypedEntityReference, TypeDesignationWorkingSet> byBaseEntityByTypeStatus,
            TypeDesignationBase<?> td){

        TypeDesignationStatusBase<?> status = td.getTypeStatus();

        final TypedEntityReference baseEntityReference = baseEntityReference(td);

        EntityReference typeDesignationEntityReference = new EntityReference(td.getId(), stringify(td));

        TypeDesignationWorkingSet typedesignationWorkingSet;
        if(!byBaseEntityByTypeStatus.containsKey(baseEntityReference)){
            TypedEntityReference containigEntityReference = new TypedEntityReference(containgEntity.getClass(), containgEntity.getId(), containgEntity.toString());
            byBaseEntityByTypeStatus.put(baseEntityReference, new TypeDesignationWorkingSet(containigEntityReference, baseEntityReference));
        }

        typedesignationWorkingSet = byBaseEntityByTypeStatus.get(baseEntityReference);
        typedesignationWorkingSet.insert(status, typeDesignationEntityReference);
    }

    /**
     * @param td
     * @return
     */
    protected TypedEntityReference baseEntityReference(TypeDesignationBase<?> td) {

        CdmBase baseEntity = null;
        String label = "";
        if(td  instanceof SpecimenTypeDesignation){
            SpecimenTypeDesignation std = (SpecimenTypeDesignation) td;
            FieldUnit fu = findFieldUnit(std);
            if(fu != null){
                baseEntity = fu;
                label = fu.getTitleCache();
            } else if(((SpecimenTypeDesignation) td).getTypeSpecimen() != null){
                baseEntity = ((SpecimenTypeDesignation) td).getTypeSpecimen();
                label = ""; // empty label to avoid repeating the DerivedUnit details
            }
        } else if(td instanceof NameTypeDesignation){
            baseEntity = ((NameTypeDesignation)td).getTypeName();
            label = "";
        }
        if(baseEntity == null) {
            baseEntity = td;
            label = "INCOMPLETE DATA";
        }

        TypedEntityReference baseEntityReference = new TypedEntityReference(baseEntity.getClass(), baseEntity.getId(), label);

        return baseEntityReference;
    }


    private LinkedHashMap<TypedEntityReference, TypeDesignationWorkingSet> orderByTypeByBaseEntity(
            Map<TypedEntityReference, TypeDesignationWorkingSet> stringsByTypeByBaseEntity){

       // order the FieldUnit TypeName keys
       List<TypedEntityReference> baseEntityKeyList = new LinkedList<>(stringsByTypeByBaseEntity.keySet());
       Collections.sort(baseEntityKeyList, new Comparator<TypedEntityReference>(){
        /**
         * Sorts the base entitoes (TypedEntityReference) in nthe following order:
         *
         * 1. FieldUnits
         * 2. DerivedUnit (in case of missing FieldUnit we expect the base type to be DerivedUnit)
         * 3. NameType
         *
         * {@inheritDoc}
         */
        @Override
        public int compare(TypedEntityReference o1, TypedEntityReference o2) {
            if(!o1.getType().equals(o2.getType())) {
                if(o1.equals(FieldUnit.class) || o2.equals(FieldUnit.class)){
                    // FieldUnits first
                    return o1.getType().equals(FieldUnit.class) ? -1 : 1;
                } else {
                    // name types last (in case of missing FieldUnit we expect the base type to be DerivedUnit which comes into the middle)
                    return o2.getType().equals(TaxonName.class) ? -1 : 1;
                }
            } else {
                return o1.getLabel().compareTo(o2.getLabel());
            }
        }});

       // new LinkedHashMap for the ordered FieldUnitOrTypeName keys
       LinkedHashMap<TypedEntityReference, TypeDesignationWorkingSet> stringsOrderedbyBaseEntityOrderdByType = new LinkedHashMap<>(stringsByTypeByBaseEntity.size());

       for(TypedEntityReference baseEntityRef : baseEntityKeyList){

           TypeDesignationWorkingSet typeDesignationWorkingSet = stringsByTypeByBaseEntity.get(baseEntityRef);
           // order the TypeDesignationStatusBase keys
            List<TypeDesignationStatusBase<?>> keyList = new LinkedList<>(typeDesignationWorkingSet.keySet());
            Collections.sort(keyList, new Comparator<TypeDesignationStatusBase>() {
                @SuppressWarnings("unchecked")
                @Override
                public int compare(TypeDesignationStatusBase o1, TypeDesignationStatusBase o2) {
                    // fix inverted order of cdm terms by -1*
                    return -1 * o1.compareTo(o2);
                }
            });
            // new LinkedHashMap for the ordered TypeDesignationStatusBase keys
            TypeDesignationWorkingSet orderedStringsByOrderedTypes = new TypeDesignationWorkingSet(typeDesignationWorkingSet.getContainigEntityReference(), baseEntityRef);
            orderedStringsByOrderedTypes.setWorkingSetId(typeDesignationWorkingSet.workingSetId); // preserve original workingSetId
            keyList.forEach(key -> orderedStringsByOrderedTypes.put(key, typeDesignationWorkingSet.get(key)));
            stringsOrderedbyBaseEntityOrderdByType.put(baseEntityRef, orderedStringsByOrderedTypes);
       }

        return stringsOrderedbyBaseEntityOrderdByType;
    }

    /*
    private LinkedHashMap<TypedEntityReference, LinkedHashMap<String, Collection<EntityReference>>> buildOrderedRepresentations(){

        orderedStringsByOrderedTypes.keySet().forEach(
                key -> orderedRepresentations.put(
                        getTypeDesignationStytusLabel(key),
                        orderedStringsByOrderedTypes.get(key))
                );
        return orderedRepresentations;
    }
*/

    public TypeDesignationConverter buildString(){

        if(finalString == null){

            finalString = "";
            if(getTypifiedNameCache() != null){
                finalString += getTypifiedNameCache() + " ";
            }

            int typeCount = 0;
            for(TypedEntityReference baseEntityRef : orderedByTypesByBaseEntity.keySet()) {
                StringBuilder sb = new StringBuilder();
                if(typeCount++ > 0){
                    sb.append(TYPE_SEPARATOR);
                }
                boolean isNameTypeDesignation = false;
                if(SpecimenOrObservationBase.class.isAssignableFrom(baseEntityRef.getType())){
                    sb.append("Type: ");
                } else {
                    sb.append("NameType: ");
                    isNameTypeDesignation = true;
                }
                if(!baseEntityRef.getLabel().isEmpty()){
                    sb.append(baseEntityRef.getLabel()).append(" ");
                }
                TypeDesignationWorkingSet typeDesignationWorkingSet = orderedByTypesByBaseEntity.get(baseEntityRef);
                if(!isNameTypeDesignation ){
                    sb.append("(");
                }
                int typeStatusCount = 0;
                for(TypeDesignationStatusBase<?> typeStatus : typeDesignationWorkingSet.keySet()) {
                    if(typeStatusCount++  > 0){
                        sb.append(TYPE_STATUS_SEPARATOR);
                    }
                    boolean isPlural = typeDesignationWorkingSet.get(typeStatus).size() > 1;
                    sb.append(typeStatus.getLabel());
                    if(isPlural){
                        sb.append("s: ");
                    } else {
                        sb.append(", ");
                    }
                    int typeDesignationCount = 0;
                    for(EntityReference typeDesignationEntityReference : typeDesignationWorkingSet.get(typeStatus)) {
                        if(typeDesignationCount++  > 0){
                            sb.append(TYPE_DESIGNATION_SEPARATOR);
                        }
                        sb.append(typeDesignationEntityReference.getLabel());
                    }
                }
                if(!isNameTypeDesignation ){
                    sb.append(")");
                }
                typeDesignationWorkingSet.setRepresentation(sb.toString());
                finalString += typeDesignationWorkingSet.getRepresentation();
            }

        }
        return this;
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
     * @return
     */
    public Collection<TypeDesignationBase> getTypeDesignations() {
        return typeDesignations;
    }

    /**
     * @param ref
     * @return
     */
    public TypeDesignationBase findTypeDesignation(EntityReference typeDesignationRef) {
        for(TypeDesignationBase td : typeDesignations){
            if(td.getId() == typeDesignationRef.getId()){
                return td;
            }
        }
        // TODO Auto-generated method stub
        return null;
    }


    public LinkedHashMap<TypedEntityReference, TypeDesignationWorkingSet> getOrderdTypeDesignationWorkingSets() {
        return orderedByTypesByBaseEntity;
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
            return stringify((SpecimenTypeDesignation)td, false);
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
    private String stringify(SpecimenTypeDesignation td, boolean useFullTitleCache) {
        String  result = "";

        if(useFullTitleCache){
            if(td.getTypeSpecimen() != null){
                String nameTitleCache = td.getTypeSpecimen().getTitleCache();
                if(getTypifiedNameCache() != null){
                    nameTitleCache = nameTitleCache.replace(getTypifiedNameCache(), "");
                }
                result += nameTitleCache;
            }
        } else {
            if(td.getTypeSpecimen() != null){
                DerivedUnit du = td.getTypeSpecimen();
                if(du.isProtectedTitleCache()){
                    result += du.getTitleCache();
                } else {
                    DerivedUnitFacadeCacheStrategy cacheStrategy = new DerivedUnitFacadeCacheStrategy();
                    result += cacheStrategy.getTitleCache(du, true);
                }
            }
        }

        if(td.getCitation() != null){
            result += " " + td.getCitation().getTitleCache();
            if(td.getCitationMicroReference() != null){
                result += " :" + td.getCitationMicroReference();
            }
        }
        if(td.isNotDesignated()){
            result += " not designated";
        }

        return result;
    }

    /**
     * @param td
     * @return
     * @deprecated
     */
    @Deprecated
    private FieldUnit findFieldUnit(SpecimenTypeDesignation td) {

        DerivedUnit du = td.getTypeSpecimen();
        return findFieldUnit(du);
    }

    private FieldUnit findFieldUnit(DerivedUnit du) {

        if(du == null || du.getOriginals() == null){
            return null;
        }
        @SuppressWarnings("rawtypes")
        Set<SpecimenOrObservationBase> originals = du.getDerivedFrom().getOriginals();
        @SuppressWarnings("rawtypes")
        Optional<SpecimenOrObservationBase> fieldUnit = originals.stream()
                .filter(original -> original instanceof FieldUnit).findFirst();
        if (fieldUnit.isPresent()) {
            return (FieldUnit) fieldUnit.get();
        } else {
            for (@SuppressWarnings("rawtypes")
            SpecimenOrObservationBase sob : originals) {
                if (sob instanceof DerivedUnit) {
                    FieldUnit fu = findFieldUnit((DerivedUnit) sob);
                    if (fu != null) {
                        return fu;
                    }
                }
            }
        }

        return null;
    }

    public String print() {
        return finalString;
    }

    /**
     * Groups the EntityReferences for TypeDesignations by the according TypeDesignationStatus.
     * The TypeDesignationStatusBase keys can be ordered by the term order defined in the vocabulary.
     */
    public class TypeDesignationWorkingSet extends LinkedHashMap<TypeDesignationStatusBase<?>, Collection<EntityReference>> {

        String workingSetRepresentation = null;

        TypedEntityReference containigEntityReference;

        TypedEntityReference baseEntityReference;

        int workingSetId = workingSetIdAutoIncrement++;

        private static final long serialVersionUID = -1329007606500890729L;

        /**
         * @param baseEntityReference
         */
        public TypeDesignationWorkingSet(TypedEntityReference containigEntityReference, TypedEntityReference baseEntityReference) {
            this.containigEntityReference = containigEntityReference;
            this.baseEntityReference = baseEntityReference;
        }

        public List<EntityReference> getTypeDesignations() {
            List<EntityReference> typeDesignations = new ArrayList<>();
            this.values().forEach(typeDesignationReferences -> typeDesignationReferences.forEach(td -> typeDesignations.add(td)));
            return typeDesignations;
        }

        /**
         * @param status
         * @param typeDesignationEntityReference
         */
        public void insert(TypeDesignationStatusBase<?> status, EntityReference typeDesignationEntityReference) {

            if(status == null){
                status = SpecimenTypeDesignationStatus.TYPE();
            }
            if(!containsKey(status)){
                put(status, new ArrayList<EntityReference>());
            }
            get(status).add(typeDesignationEntityReference);
        }

        /**
         * @return the workingSetId
         */
        public int getWorkingSetId() {
            return workingSetId;
        }

        /**
         * @param workingSetId the workingSetId to set
         */
        public void setWorkingSetId(int workingSetId) {
            this.workingSetId = workingSetId;
        }

        public String getRepresentation() {
            return workingSetRepresentation;
        }

        public void setRepresentation(String representation){
            this.workingSetRepresentation = representation;
        }

        /**
         * A reference to the entity which is the common base entity for all TypeDesignations in this workingset.
         * For a {@link SpecimenTypeDesignation} this is usually the {@link FieldUnit} if it is present. Otherwise it can also be
         * a {@link DerivedUnit} or something else depending on the specific use case.
         *
         * @return the baseEntityReference
         */
        public TypedEntityReference getBaseEntityReference() {
            return baseEntityReference;
        }

        /**
         * A reference to the entity which contains the TypeDesignations bundled in this working set.
         * This can be for example a {@link TaxonName} or a {@link Registration} entity.
         *
         * @return the baseEntityReference
         */
        public TypedEntityReference getContainigEntityReference() {
            return containigEntityReference;
        }

        @Override
        public String toString(){
            if(workingSetRepresentation != null){
                return workingSetRepresentation;
            } else {
                return super.toString();
            }
        }

    }

}
