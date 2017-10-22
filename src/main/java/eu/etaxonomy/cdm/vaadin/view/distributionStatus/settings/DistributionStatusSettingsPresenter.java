/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.distributionStatus.settings;

import java.util.Arrays;
import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;

import eu.etaxonomy.cdm.model.common.DefinedTermBase;
import eu.etaxonomy.cdm.model.common.TermType;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;

/**
 * @author a.mueller
 * @date 22.10.2017
 *
 */
public class DistributionStatusSettingsPresenter extends SettingsPresenterBase {

    private Container distributionStatusContainer;

    /**
     * @param distributionStatusContainer
     */
    public DistributionStatusSettingsPresenter() {
        super();
        distributionStatusContainer = new IndexedContainer(getPresenceAbsenceVocabulary());
    }

    private List<DefinedTermBase<?>> getPresenceAbsenceVocabulary(){
        return CdmSpringContextHelper.getTermService().listByTermType(
                TermType.PresenceAbsenceTerm, null, null, null, DESCRIPTION_INIT_STRATEGY);
    }


    public Container getDistributionStatusContainer() {
        return distributionStatusContainer;
    }

    protected static final List<String> DESCRIPTION_INIT_STRATEGY = Arrays.asList(new String []{
            "$",
            "representations",
    });
}
