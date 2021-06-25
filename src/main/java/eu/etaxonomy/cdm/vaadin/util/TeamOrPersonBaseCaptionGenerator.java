/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util;

import org.vaadin.viritin.fields.CaptionGenerator;

import eu.etaxonomy.cdm.model.agent.TeamOrPersonBase;

/**
 * @author a.kohlbecker
 * @since Jun 7, 2017
 */
public final class TeamOrPersonBaseCaptionGenerator<T extends TeamOrPersonBase> implements CaptionGenerator<T> {

    public enum CacheType {
        NOMENCLATURAL_TITLE,
        COLLECTOR_TITLE,
        BIBLIOGRAPHIC_TITLE;
    }

    private CacheType cacheType;

    private static final long serialVersionUID = 116448502301429773L;

    public TeamOrPersonBaseCaptionGenerator(CacheType cacheType){
        this.cacheType = cacheType;
    }

    @Override
    public String getCaption(T option) {
        String caption = chooseTitle(option);
        if(caption == null){
            caption = option.getTitleCache();
        }
        return caption;
    }

    protected String chooseTitle(T option) {
        switch(cacheType){
            case NOMENCLATURAL_TITLE:
                return option.getNomenclaturalTitle();
            case COLLECTOR_TITLE:
                // return option.getCollectorTitle(); // enable once #4311 is solved
            case BIBLIOGRAPHIC_TITLE:
            default:
                return null;
        }
    }
}