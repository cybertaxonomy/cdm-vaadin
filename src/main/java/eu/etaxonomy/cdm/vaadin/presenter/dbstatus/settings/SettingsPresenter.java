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

import eu.etaxonomy.cdm.api.service.IClassificationService;
import eu.etaxonomy.cdm.api.service.ITermService;
import eu.etaxonomy.cdm.api.service.IVocabularyService;
import eu.etaxonomy.cdm.model.common.DefinedTermBase;
import eu.etaxonomy.cdm.model.common.TermType;
import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.taxon.Classification;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;

/**
 * @author alex
 * @date 22.04.2015
 *
 */
public class SettingsPresenter {

    private Container classificationContainer;
    private Container distributionContainer;
    private Container distributionStatusContainer;
    private IVocabularyService vocabularyService;
    private ITermService termService;
    private IClassificationService classificationService;
    private UUID clUUID;
    private UUID termUUID;



    public SettingsPresenter(){
        init();

    }

    /**
     *
     */
    private void init() {
        clUUID = UUID.fromString(VaadinSession.getCurrent().getAttribute("classificationUUID").toString());
        termUUID = UUID.fromString(VaadinSession.getCurrent().getAttribute("selectedTerm").toString());
        classificationContainer = new IndexedContainer(getClassificationList());
        distributionContainer = new IndexedContainer(getNamedAreaList());
        distributionStatusContainer = new IndexedContainer(getPresenceAbsenceVocabulary());
    }

    public Classification getChosenClassification(){
        return classificationService.load(clUUID);
    }

    public TermVocabulary getChosenArea(){
        return vocabularyService.load(termUUID);
    }

    public Container getClassificationContainer() {
        return classificationContainer;
    }
    public void setClassificationContainer(Container classificationContainer) {
        this.classificationContainer = classificationContainer;
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



    private List<Classification> getClassificationList() {
        classificationService = CdmSpringContextHelper.getClassificationService();
        List<Classification> classificationList = classificationService.listClassifications(null, null, null, NODE_INIT_STRATEGY());
        return classificationList;
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

     private List<String> NODE_INIT_STRATEGY(){
            return Arrays.asList(new String[]{
                "taxon.sec",
                "taxon.name",
                "classification"
     });}

     protected static final List<String> DESCRIPTION_INIT_STRATEGY = Arrays.asList(new String []{
             "$",
             "elements.*",
             "elements.sources.citation.authorship.$",
             "elements.sources.nameUsedInSource.originalNameString",
             "elements.area.level",
             "elements.modifyingText",
             "elements.states.*",
             "elements.media",
             "elements.multilanguageText",
             "multilanguageText",
             "stateData.$",
             "annotations",
             "markers",
             "sources.citation.authorship",
             "sources.nameUsedInSource",
             "multilanguageText",
             "media",
             "name.$",
             "name.rank.representations",
             "name.status.type.representations",
             "taxon2.name"
     });


}
