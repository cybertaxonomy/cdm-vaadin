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

import eu.etaxonomy.cdm.model.description.PresenceAbsenceTerm;
import eu.etaxonomy.cdm.vaadin.container.PresenceAbsenceTermContainer;

/**
 * @author a.mueller
 * @since 22.10.2017
 *
 */
public class DistributionStatusSettingsPresenter extends SettingsPresenterBase {

    private Container distributionStatusContainer;

    /**
     * @param distributionStatusContainer
     */
    public DistributionStatusSettingsPresenter() {
        super();
        distributionStatusContainer = new IndexedContainer(getDistributionStatusList());
    }

    private List<PresenceAbsenceTerm> getDistributionStatusList(){
        return PresenceAbsenceTermContainer.getDistributionStatusList(TERMS_INIT_STRATEGY);
    }



    public Container getDistributionStatusContainer() {
        return distributionStatusContainer;
    }

    protected static final List<String> TERMS_INIT_STRATEGY = Arrays.asList(new String []{
            "$",
            "representations",
    });
}
