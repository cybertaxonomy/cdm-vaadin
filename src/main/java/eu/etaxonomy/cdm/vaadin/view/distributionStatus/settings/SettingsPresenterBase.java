/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.distributionStatus.settings;

import eu.etaxonomy.cdm.vaadin.component.distributionStatus.AreaAndTaxonSettingsConfigWindow;
import eu.etaxonomy.cdm.vaadin.component.distributionStatus.DistributionStatusSettingsConfigWindow;

/**
 * Note: This presenter is used for {@link DistributionStatusSettingsConfigWindow} AND {@link AreaAndTaxonSettingsConfigWindow}
 *
 * @author alex
 * @since 22.04.2015
 *
 *
 */
public abstract class SettingsPresenterBase {

//    private Container distributionContainer;
//    private UUID vocUUID;



    public SettingsPresenterBase(){
//		Object selectedVocabularyUuidString = VaadinSession.getCurrent().getAttribute(DistributionEditorUtil.SATTR_SELECTED_AREA_VOCABULARY_UUID);
//		if(selectedVocabularyUuidString!=null){
//			vocUUID = UUID.fromString(selectedVocabularyUuidString.toString());
//		}
//		distributionContainer = new IndexedContainer(getNamedAreaList());
//		distributionStatusContainer = new IndexedContainer(getPresenceAbsenceVocabulary());
    }
//
//    public List<TaxonNode> getChosenTaxonNodes(){
//    	List<UUID> nodeUuids = (List<UUID>) VaadinSession.getCurrent().getAttribute(DistributionEditorUtil.SATTR_TAXON_NODES_UUID);
//    	if(nodeUuids!=null){
//    		return CdmSpringContextHelper.getTaxonNodeService().load(nodeUuids, null);
//    	}
//    	return Collections.emptyList();
//    }
//
//    public Classification getChosenClassification(){
//    	UUID uuid = (UUID) VaadinSession.getCurrent().getAttribute(DistributionEditorUtil.SATTR_CLASSIFICATION);
//    	if(uuid!=null){
//    		return CdmSpringContextHelper.getClassificationService().load(uuid);
//    	}
//    	return null;
//    }
//
//    public TermVocabulary getChosenAreaVoc(){
//        return CdmSpringContextHelper.getVocabularyService().load(vocUUID);
//    }







}
