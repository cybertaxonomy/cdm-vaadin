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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinSession;

import eu.etaxonomy.cdm.model.common.TermType;
import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.location.NamedArea;
import eu.etaxonomy.cdm.model.metadata.CdmPreference;
import eu.etaxonomy.cdm.model.metadata.PreferencePredicate;
import eu.etaxonomy.cdm.model.taxon.Classification;
import eu.etaxonomy.cdm.model.taxon.TaxonNode;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.util.DistributionEditorUtil;

/**
 * @author a.mueller
 * @date 22.10.2017
 *
 */
public class AreaAndTaxonSettingsPresenter extends SettingsPresenterBase {

    private Container areaContainer;
    private UUID areaVocabUUID;

    /**
     * @param distributionContainer
     * @param vocUUID
     */
    public AreaAndTaxonSettingsPresenter() {
        super();
        Object selectedVocabularyUuidString = VaadinSession.getCurrent()
                .getAttribute(DistributionEditorUtil.SATTR_SELECTED_AREA_VOCABULARY_UUID);
        if(selectedVocabularyUuidString!=null){
            areaVocabUUID = UUID.fromString(selectedVocabularyUuidString.toString());
        }
        areaContainer = new IndexedContainer(getNamedAreaList());
    }



    public Container getAreaContainer() {
        return areaContainer;
    }

    private List<TermVocabulary<? extends NamedArea>> getNamedAreaList() {
        CdmPreference areaVocPref = CdmSpringContextHelper.getPreferenceService()
                .findVaadin(PreferencePredicate.AvailableDistributionAreaVocabularies);
        if (areaVocPref != null){
            List<UUID> uuidList = areaVocPref.getValueUuidList();
            return (List)CdmSpringContextHelper.getVocabularyService().load(uuidList, VOCABULARY_INIT_STRATEGY);
        }else{
            return (List)CdmSpringContextHelper.getVocabularyService().findByTermType(TermType.NamedArea, VOCABULARY_INIT_STRATEGY);
        }
    }


    public TermVocabulary<NamedArea> getChosenAreaVoc(){
        return CdmSpringContextHelper.getVocabularyService().load(areaVocabUUID);
    }


    public List<TaxonNode> getChosenTaxonNodes(){
        List<UUID> nodeUuids = (List<UUID>) VaadinSession.getCurrent()
                .getAttribute(DistributionEditorUtil.SATTR_TAXON_NODES_UUID);
        if(nodeUuids!=null){
            return CdmSpringContextHelper.getTaxonNodeService().load(nodeUuids, null);
        }
        return Collections.emptyList();
    }

    public Classification getChosenClassification(){
        UUID uuid = (UUID) VaadinSession.getCurrent().getAttribute(DistributionEditorUtil.SATTR_CLASSIFICATION);
        if(uuid!=null){
            return CdmSpringContextHelper.getClassificationService().load(uuid);
        }
        return null;
    }


    protected static final List<String> VOCABULARY_INIT_STRATEGY = Arrays.asList(new String []{
            "$",
            "terms",
            "terms.*",
    });

}
