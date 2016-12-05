// $Id$
/**
* Copyright (C) 2016 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util;

import java.util.HashMap;
import java.util.Map;

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

    public NamedArea getNamedArea(String title){
        return titleToNamedAreaMap.get(title);
    }

    public void addPresenceAbsenceTerm(PresenceAbsenceTerm presenceAbsenceTerm){
        titleToPresenceAbsenceTermMap.put(presenceAbsenceTerm.getTitleCache(), presenceAbsenceTerm);
    }

    public PresenceAbsenceTerm getPresenceAbsenceTerm(String title){
        return titleToPresenceAbsenceTermMap.get(title);
    }
}
