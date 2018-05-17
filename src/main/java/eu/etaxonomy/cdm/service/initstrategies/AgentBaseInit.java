/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.service.initstrategies;

import java.util.Arrays;
import java.util.List;

/**
 * @author a.kohlbecker
 * @since Apr 17, 2018
 *
 */
public class AgentBaseInit {

    public static final List<String> TEAM_OR_PERSON_INIT_STRATEGY = Arrays.asList(
            "$",
            "teamMembers.$"
            );

}
