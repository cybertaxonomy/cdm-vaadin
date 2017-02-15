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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringComponent;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.model.common.DefinedTermBase;
import eu.etaxonomy.cdm.model.common.TermType;
import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.taxon.Classification;
import eu.etaxonomy.cdm.model.taxon.TaxonNode;
import eu.etaxonomy.cdm.vaadin.util.DistributionEditorUtil;

/**
 * @author alex
 * @date 22.04.2015
 *
 */
@SpringComponent
@Scope("prototype")
public class SettingsPresenter implements InitializingBean {

    @Autowired
    private CdmRepository cdmRepo = null;

    private Container distributionContainer;
    private Container distributionStatusContainer;
    private UUID termUUID;


    @Override
    public void afterPropertiesSet() throws Exception
    {
        if(VaadinSession.getCurrent() != null){
            Object selectedVocabularyUuidString = VaadinSession.getCurrent().getAttribute(DistributionEditorUtil.SATTR_SELECTED_VOCABULARY_UUID);
            if(selectedVocabularyUuidString != null){
                termUUID = UUID.fromString(selectedVocabularyUuidString.toString());
            }
        }
        distributionContainer = new IndexedContainer(getNamedAreaList());
        distributionStatusContainer = new IndexedContainer(getPresenceAbsenceVocabulary());
    }

    public List<TaxonNode> getChosenTaxonNodes(){
    	List<UUID> nodeUuids = (List<UUID>) VaadinSession.getCurrent().getAttribute(DistributionEditorUtil.SATTR_TAXON_NODES_UUID);
    	if(nodeUuids!=null){
    		return cdmRepo.getTaxonNodeService().load(nodeUuids, null);
    	}
    	return Collections.emptyList();
    }

    public Classification getChosenClassification(){
    	UUID uuid = (UUID) VaadinSession.getCurrent().getAttribute(DistributionEditorUtil.SATTR_CLASSIFICATION);
    	if(uuid!=null){
    		return cdmRepo.getClassificationService().load(uuid);
    	}
    	return null;
    }

    public TermVocabulary getChosenArea(){
        return cdmRepo.getVocabularyService().load(termUUID);
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

        List<TermVocabulary<DefinedTermBase>> termList = cdmRepo.getVocabularyService().findByTermType(TermType.NamedArea);
        //FIXME: is this necessary??
        for (TermVocabulary<DefinedTermBase> termVocabulary : termList) {
			termVocabulary.setTitleCache(null);
		}
        return termList;
    }

    private List<DefinedTermBase<?>> getPresenceAbsenceVocabulary(){
        return cdmRepo.getTermService().listByTermType(TermType.PresenceAbsenceTerm, null, null, null, DESCRIPTION_INIT_STRATEGY);
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
