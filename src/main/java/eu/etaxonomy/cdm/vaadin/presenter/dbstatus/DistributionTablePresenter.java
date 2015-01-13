package eu.etaxonomy.cdm.vaadin.presenter.dbstatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.ComboBox;

import eu.etaxonomy.cdm.api.service.IClassificationService;
import eu.etaxonomy.cdm.api.service.IDescriptionService;
import eu.etaxonomy.cdm.api.service.ITaxonNodeService;
import eu.etaxonomy.cdm.api.service.ITermService;
import eu.etaxonomy.cdm.api.service.IVocabularyService;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.common.DefinedTermBase;
import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.description.DescriptionElementBase;
import eu.etaxonomy.cdm.model.description.Distribution;
import eu.etaxonomy.cdm.model.description.Feature;
import eu.etaxonomy.cdm.model.description.PresenceAbsenceTerm;
import eu.etaxonomy.cdm.model.taxon.Classification;
import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.model.taxon.TaxonNode;
import eu.etaxonomy.cdm.vaadin.model.DbTableDTO;
import eu.etaxonomy.cdm.vaadin.model.DbTableDTOS;
import eu.etaxonomy.cdm.vaadin.model.DistributionDTO;
import eu.etaxonomy.cdm.vaadin.model.LazyLoadedContainer;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.view.dbstatus.DistributionTableView;
import eu.etaxonomy.cdm.vaadin.view.dbstatus.IDistributionTableComponent;


public class DistributionTablePresenter implements IDistributionTableComponent.DistributionTableComponentListener{

	private final IClassificationService classificationService;
	private final IVocabularyService vocabularyService;
	private final IDescriptionService descriptionService;
	private final ITaxonNodeService taxonNodeService;
	private final ITermService termService;
	private final DistributionTableView view;

	public DistributionTablePresenter(DistributionTableView dtv){
	    this.view = dtv;
	    view.addListener(this);
	    view.dataBinding();
	    classificationService = (IClassificationService)CdmSpringContextHelper.newInstance().getBean("classificationServiceImpl");
	    taxonNodeService = (ITaxonNodeService)CdmSpringContextHelper.newInstance().getBean("taxonNodeServiceImpl");
		vocabularyService = (IVocabularyService)CdmSpringContextHelper.newInstance().getBean("vocabularyServiceImpl");
		descriptionService = (IDescriptionService)CdmSpringContextHelper.newInstance().getBean("descriptionServiceImpl");
		termService = (ITermService)CdmSpringContextHelper.newInstance().getBean("termServiceImpl");
	}


	
	public ComboBox updateDistributionField(DescriptionElementBase deb,
			Distribution db,
			BeanItemContainer<PresenceAbsenceTerm> termContainer, ComboBox box,
			Taxon taxon) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<DefinedTermBase> getChosenTerms() {
		VaadinSession session = VaadinSession.getCurrent();
		UUID termUUID = (UUID) session.getAttribute("selectedTerm");
		TermVocabulary<DefinedTermBase> term = vocabularyService.load(termUUID);
		term = CdmBase.deproxy(term, TermVocabulary.class);
		return term.getTerms();
	}

	@Override
	public HashMap<DescriptionElementBase, Distribution> getDistribution(DefinedTermBase dt, Taxon taxon) {
		Set<Feature> setFeature = new HashSet<Feature>(Arrays.asList(Feature.DISTRIBUTION()));
		List<DescriptionElementBase> listTaxonDescription = descriptionService.listDescriptionElementsForTaxon(taxon, setFeature, null, null, null, DESCRIPTION_INIT_STRATEGY);
		HashMap<DescriptionElementBase, Distribution> map = null;
		for(DescriptionElementBase deb : listTaxonDescription){
			if(deb instanceof Distribution){
				Distribution db = (Distribution)deb;
				String titleCache = dt.getTitleCache();
				if(db.getArea().getTitleCache().equalsIgnoreCase(titleCache)){
					map = new HashMap<DescriptionElementBase, Distribution>();
					map.put(deb, db);
				}
			}
		}
		return map;
	}

	
	@Override
	public List<Distribution> getDistribution(Taxon taxon) {
		Set<Feature> setFeature = new HashSet<Feature>(Arrays.asList(Feature.DISTRIBUTION()));
		List<Distribution> listTaxonDescription = descriptionService.listDescriptionElementsForTaxon(taxon, setFeature, null, null, null, DESCRIPTION_INIT_STRATEGY);
		return listTaxonDescription;
		
	}
	
	
	public List<TaxonNode> getAllNodes(int start, int end){
		Classification classification = loadClassification();
		List<TaxonNode> nodesForClassification = taxonNodeService.listAllNodesForClassification(classification, start, end);
		return nodesForClassification;
	}



	private Classification loadClassification() {
		VaadinSession session = VaadinSession.getCurrent();
		UUID classificationUUID = (UUID) session.getAttribute("classificationUUID");
		Classification classification = classificationService.load(classificationUUID);
		return classification;
	}
	
	@Override
	public int getSizeOfClassification(){
		Classification classification = loadClassification();
		return taxonNodeService.countAllNodesForClassification(classification);
	}
	
	@Override
	public DbTableDTOS getDataList(int start, int end){
		List<TaxonNode> nodes = getAllNodes(start, end);
		DbTableDTOS items = new DbTableDTOS();
		for(TaxonNode tn: nodes){
			Taxon taxon = tn.getTaxon();
			DbTableDTO dbTableDTO = new DbTableDTO(taxon);

			Set<DefinedTermBase> terms = getChosenTerms();
			List<Distribution> distribution = getDistribution(taxon);
			for(DefinedTermBase dt: terms){
				for(Distribution db : distribution){
					if(dt.getTitleCache().equalsIgnoreCase(db.getArea().getTitleCache())){
//						DistributionDTO distributionDTO = new DistributionDTO(db.getStatus().getTitleCache());
//						dbTableDTO.setdDTO(distributionDTO);
					}
					
				}
			}	
			items.add(dbTableDTO);
		}
		return items;
	}
	
	void nestedContainer(){
		BeanItemContainer<DbTableDTO> container = new BeanItemContainer<DbTableDTO>(DbTableDTO.class);
		
	}
	
	@Override
	public List<PresenceAbsenceTerm> getPresenceAbsenceTerms() {
		//TODO Better to use TermType instead of class to get the list
		return termService.list(PresenceAbsenceTerm.class, null, null, null, DESCRIPTION_INIT_STRATEGY);
	}

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
            "stateData.$"
    });


	@Override
	public LazyLoadedContainer getTableContainer() {
		// TODO Auto-generated method stub
		return null;
	}



}
