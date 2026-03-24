/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util.formatter;

import java.util.List;

import org.vaadin.viritin.fields.CaptionGenerator;

import eu.etaxonomy.cdm.common.CdmUtils;
import eu.etaxonomy.cdm.format.agent.AgentSearchFormatter;
import eu.etaxonomy.cdm.model.agent.TeamOrPersonBase;

/**
 * @author a.kohlbecker
 * @since Jun 7, 2017
 */
public final class TeamOrPersonBaseCaptionGenerator<T extends TeamOrPersonBase>
            implements CaptionGenerator<T> {

    private static final long serialVersionUID = 116448502301429773L;

    private List<AgentSearchFormatter.CacheType> cacheTypes;
    private int maxLength = 150;

    public TeamOrPersonBaseCaptionGenerator(List<AgentSearchFormatter.CacheType> cacheTypes){
        this.cacheTypes = cacheTypes;
    }

    @Override
    public String getCaption(T option) {
        String result = AgentSearchFormatter.INSTANCE().format(option, cacheTypes);
        return CdmUtils.truncateWithEllipsis(result, maxLength);
    }
}