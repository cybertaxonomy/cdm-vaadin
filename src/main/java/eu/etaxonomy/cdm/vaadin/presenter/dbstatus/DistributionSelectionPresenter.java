package eu.etaxonomy.cdm.vaadin.presenter.dbstatus;

import java.util.Arrays;
import java.util.List;

import eu.etaxonomy.cdm.api.service.IClassificationService;
import eu.etaxonomy.cdm.api.service.IVocabularyService;
import eu.etaxonomy.cdm.model.common.DefinedTermBase;
import eu.etaxonomy.cdm.model.common.TermType;
import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.taxon.Classification;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.view.dbstatus.DistributionSelectionView;
import eu.etaxonomy.cdm.vaadin.view.dbstatus.IDistributionSelectionComponent;

public class DistributionSelectionPresenter implements IDistributionSelectionComponent.DistributionSelectionComponentListener {

	DistributionSelectionView view;
	
	public DistributionSelectionPresenter(DistributionSelectionView dsv) {
		this.view = dsv;
		view.addListener(this);
		view.dataBinding();
	}
	
	@Override
	public void buttonClick(Classification classification, TermVocabulary<DefinedTermBase> term) {
		// TODO retrieve classification.UUID and term.UUID and save this in the vaadinSession
		// TODO move on the final table and load it
	}

	@Override
	public List<Classification> getClassificationList() {
		IClassificationService classificationService = (IClassificationService)CdmSpringContextHelper.newInstance().getBean("classificationServiceImpl");
		//TODO replace the list by UUID and TITLECACHE 
		//classificationService.getUuidAndTitleCache();
		List<Classification> classificationList = classificationService.listClassifications(null, null, null, NODE_INIT_STRATEGY());
		return classificationList;
	}

	@Override
	public List<TermVocabulary<DefinedTermBase>> getNamedAreaList() {
		
		IVocabularyService vocabularyService = (IVocabularyService)CdmSpringContextHelper.newInstance().getBean("vocabularyServiceImpl");
		List<TermVocabulary<DefinedTermBase>> termList = vocabularyService.findByTermType(TermType.NamedArea);
		return termList;
	}

	 private List<String> NODE_INIT_STRATEGY(){
	        return Arrays.asList(new String[]{
	            "taxon.sec",
	            "taxon.name",
	            "classification"
	    });}
}
