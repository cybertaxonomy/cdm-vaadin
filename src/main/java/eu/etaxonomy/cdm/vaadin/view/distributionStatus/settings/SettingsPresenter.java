/**
* Copyright (C) 2015 EDIT
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

import eu.etaxonomy.cdm.model.common.DefinedTermBase;
import eu.etaxonomy.cdm.model.common.TermType;
import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.taxon.Classification;
import eu.etaxonomy.cdm.model.taxon.TaxonNode;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.util.DistributionEditorUtil;
import eu.etaxonomy.cdm.vaadin.view.distributionStatus.DistributionSettingsConfigWindow;
import eu.etaxonomy.cdm.vaadin.view.distributionStatus.SettingsConfigWindow;

/**
 * Note: This presenter is used for {@link SettingsConfigWindow} AND {@link DistributionSettingsConfigWindow}
 *
 * @author alex
 * @date 22.04.2015
 *
 *
 */
public class SettingsPresenter {

    private Container distributionContainer;
    private Container distributionStatusContainer;
    private UUID vocUUID;



    public SettingsPresenter(){
		Object selectedVocabularyUuidString = VaadinSession.getCurrent().getAttribute(DistributionEditorUtil.SATTR_SELECTED_AREA_VOCABULARY_UUID);
		if(selectedVocabularyUuidString!=null){
			vocUUID = UUID.fromString(selectedVocabularyUuidString.toString());
		}
		distributionContainer = new IndexedContainer(getNamedAreaList());
		distributionStatusContainer = new IndexedContainer(getPresenceAbsenceVocabulary());
    }

    public List<TaxonNode> getChosenTaxonNodes(){
    	List<UUID> nodeUuids = (List<UUID>) VaadinSession.getCurrent().getAttribute(DistributionEditorUtil.SATTR_TAXON_NODES_UUID);
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

    public TermVocabulary getChosenAreaVoc(){
        return CdmSpringContextHelper.getVocabularyService().load(vocUUID);
    }

    public Container getDistributionContainer() {
        return distributionContainer;
    }

    public Container getDistributionStatusContainer() {
        return distributionStatusContainer;
    }

    private List<TermVocabulary<DefinedTermBase>> getNamedAreaList() {
        List<TermVocabulary<DefinedTermBase>> termList = CdmSpringContextHelper.getVocabularyService().findByTermType(TermType.NamedArea, VOCABULARY_INIT_STRATEGY);
        return termList;
    }

    private List<DefinedTermBase<?>> getPresenceAbsenceVocabulary(){
        return CdmSpringContextHelper.getTermService().listByTermType(TermType.PresenceAbsenceTerm, null, null, null, DESCRIPTION_INIT_STRATEGY);
    }

    protected static final List<String> VOCABULARY_INIT_STRATEGY = Arrays.asList(new String []{
    		"$",
    		"terms",
    		"terms.*",
    });

    protected static final List<String> DESCRIPTION_INIT_STRATEGY = Arrays.asList(new String []{
            "$",
            "annotations",
            "markers",
            "sources.citation.authorship",
            "sources.nameUsedInSource",
            "media",
    });

}
