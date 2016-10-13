// $Id$
/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.presenter.dbstatus.settings;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinSession;

import eu.etaxonomy.cdm.api.service.ITaxonNodeService;
import eu.etaxonomy.cdm.api.service.ITermService;
import eu.etaxonomy.cdm.api.service.IVocabularyService;
import eu.etaxonomy.cdm.model.common.DefinedTermBase;
import eu.etaxonomy.cdm.model.common.TermType;
import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.taxon.TaxonNode;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.util.DistributionEditorUtil;

/**
 * @author alex
 * @date 22.04.2015
 *
 */
public class SettingsPresenter {

    private Container distributionContainer;
    private Container distributionStatusContainer;
    private IVocabularyService vocabularyService;
    private ITermService termService;
    private ITaxonNodeService taxonNodeService;
    private UUID taxonNodeUuid;
    private UUID termUUID;



    public SettingsPresenter(){
        taxonNodeService = CdmSpringContextHelper.getTaxonNodeService();
		Object taxonNodeUuidString = VaadinSession.getCurrent().getAttribute(DistributionEditorUtil.SATTR_TAXON_NODE_UUID);
		Object selectedVocabularyUuidString = VaadinSession.getCurrent().getAttribute(DistributionEditorUtil.SATTR_SELECTED_VOCABULARY_UUID);
		if(taxonNodeUuidString!=null){
			taxonNodeUuid = UUID.fromString(taxonNodeUuidString.toString());
		}
		if(selectedVocabularyUuidString!=null){
			termUUID = UUID.fromString(selectedVocabularyUuidString.toString());
		}
		distributionContainer = new IndexedContainer(getNamedAreaList());
		distributionStatusContainer = new IndexedContainer(getPresenceAbsenceVocabulary());
    }

    public TaxonNode getChosenTaxonNode(){
        return taxonNodeService.load(taxonNodeUuid);
    }

    public TermVocabulary getChosenArea(){
        return vocabularyService.load(termUUID);
    }

    public Container getDistributionContainer() {
        return distributionContainer;
    }
    
    public void setDistributionContainer(Container distributionContainer) {
        this.distributionContainer = distributionContainer;
    }
    
    public Container getDistributionStatusContainer() {
        return distributionStatusContainer;
    }
    
    public void setDistributionStatusContainer(Container distributionStatusContainer) {
        this.distributionStatusContainer = distributionStatusContainer;
    }

    private List<TermVocabulary<DefinedTermBase>> getNamedAreaList() {
        vocabularyService = CdmSpringContextHelper.getVocabularyService();
        List<TermVocabulary<DefinedTermBase>> termList = vocabularyService.findByTermType(TermType.NamedArea);
        return termList;
    }
    
    private List<DefinedTermBase<?>> getPresenceAbsenceVocabulary(){
        termService = CdmSpringContextHelper.getTermService();
        return termService.listByTermType(TermType.PresenceAbsenceTerm, null, null, null, DESCRIPTION_INIT_STRATEGY);
    }

    protected static final List<String> DESCRIPTION_INIT_STRATEGY = Arrays.asList(new String []{
    		"$",
    		"annotations",
    		"markers",
    		"sources.citation.authorship",
    		"sources.nameUsedInSource",
    		"media",
    });

}
