/**
* Copyright (C) 2016 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.etaxonomy.cdm.model.common.TermType;
import eu.etaxonomy.cdm.model.description.PresenceAbsenceTerm;
import eu.etaxonomy.cdm.model.location.NamedArea;

/**
 * @author pplitzner
 * @date 10.10.2016
 *
 */
public class TermCacher {

    private static final Map<String, NamedArea> titleToNamedAreaMap = new HashMap<>();

    private static final Map<String, PresenceAbsenceTerm> titleToPresenceAbsenceTermMap = new HashMap<>();

    private static TermCacher instance;

    private TermCacher() {
        // TODO Auto-generated constructor stub
    }

    public static TermCacher getInstance(){
        if(instance==null){
            instance = new TermCacher();
        }
        return instance;
    }


    public void addNamedArea(NamedArea namedArea){
        titleToNamedAreaMap.put(namedArea.getTitleCache(), namedArea);
    }

//    private void loadNamedAreaTerms() {
//        List<NamedArea> naTerms = CdmSpringContextHelper.getTermService().listByTermType(TermType.NamedArea,
//                                                                     null, null, null, TERMS_INIT_STRATEGY);
//        for(NamedArea naTerm : naTerms) {
//            this.addNamedArea(naTerm);
//        }
//    }

    public NamedArea getNamedArea(String title){
//        // TODO: Only load single area if not already cached.
//        if(titleToNamedAreaMap.isEmpty()) {
//            loadNamedAreaTerms();
//        }
        return titleToNamedAreaMap.get(title);
    }

//    // Performance Issue
//    public List<NamedArea> getNamedAreaTermList(){
//        if (titleToNamedAreaMap.isEmpty()) {
//            loadNamedAreaTerms();
//        }
//        List<NamedArea> naList = new ArrayList<>();
//        naList.addAll(titleToNamedAreaMap.values());
//        return naList;
//    }

    public void addPresenceAbsenceTerm(PresenceAbsenceTerm presenceAbsenceTerm){
        titleToPresenceAbsenceTermMap.put(presenceAbsenceTerm.getTitleCache(), presenceAbsenceTerm);
    }

    private void loadDistributionStatusTerms() {
        List<PresenceAbsenceTerm> paTerms = CdmSpringContextHelper.getTermService().listByTermType(
                                                TermType.PresenceAbsenceTerm, null, null, null,
                                                TERMS_INIT_STRATEGY);
        for(PresenceAbsenceTerm paTerm : paTerms) {
            this.addPresenceAbsenceTerm(paTerm);
        }
    }

    public PresenceAbsenceTerm getPresenceAbsenceTerm(String title){
        if(titleToPresenceAbsenceTermMap.isEmpty()) {
            loadDistributionStatusTerms();
        }
        return titleToPresenceAbsenceTermMap.get(title);
    }

    public List<PresenceAbsenceTerm> getDistributionStatusTermList(){
        if (titleToPresenceAbsenceTermMap.isEmpty()) {
            loadDistributionStatusTerms();
        }
        List<PresenceAbsenceTerm> paList = new ArrayList<>();
        paList.addAll(titleToPresenceAbsenceTermMap.values());
        return paList;
    }

    protected static final List<String> TERMS_INIT_STRATEGY = Arrays.asList(new String []{
            "$",
            "representations",
    });
}
