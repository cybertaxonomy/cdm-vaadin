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
import eu.etaxonomy.cdm.api.service.ITaxonService;
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
import eu.etaxonomy.cdm.vaadin.model.CdmTaxonTableCollection;
import eu.etaxonomy.cdm.vaadin.model.DbTableDTO;
import eu.etaxonomy.cdm.vaadin.model.DbTableDTOS;
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
	private ITaxonService taxonService;

	public DistributionTablePresenter(DistributionTableView dtv){
	    this.view = dtv;
	    view.addListener(this);
	    taxonService = (ITaxonService)CdmSpringContextHelper.newInstance().getBean("taxonServiceImpl");
	    classificationService = (IClassificationService)CdmSpringContextHelper.newInstance().getBean("classificationServiceImpl");
	    taxonNodeService = (ITaxonNodeService)CdmSpringContextHelper.newInstance().getBean("taxonNodeServiceImpl");
		vocabularyService = (IVocabularyService)CdmSpringContextHelper.newInstance().getBean("vocabularyServiceImpl");
		descriptionService = (IDescriptionService)CdmSpringContextHelper.newInstance().getBean("descriptionServiceImpl");
		termService = (ITermService)CdmSpringContextHelper.newInstance().getBean("termServiceImpl");
		view.dataBinding();
	}


	
	//for sql container
    public static final String NAME_ID = "Name";
    public static final String PB_ID = "Pb";
    public static final String FN_ID = "Fn";
    public static final String UNP_ID = "Unp";
    public static final String UNR_ID = "Unr";
    
    public static String TERM = null;

//    private static final String SELECT_QUERY="SELECT tb.id as taxon_id, tnb.titleCache as " + NAME_ID + " , tb.publish as " + PB_ID + " , tb.unplaced as " +  UNP_ID + FROM_QUERY;
    private static final String SELECT_QUERY="Select tb.DTYPE, tb.id, tb.titleCache AS Taxon, deb.DTYPE, deb.id, deb.area_id, dtb.vocabulary_id, dtb1.vocabulary_id, ";
    
    private static final String PIVOT_QUERY = "MAX( IF(dtb1.titleCache = '"+ TERM +"', dtb.titleCache, NULL) ) as '"+ TERM +"',";
    
    private static final String FROM_QUERY = " From TaxonBase tb JOIN DescriptionBase db ON db.taxon_id=tb.id JOIN DescriptionElementBase deb ON deb.indescription_id=db.id Join DefinedTermBase dtb on deb.status_id=dtb.id Join DefinedTermBase dtb1 on deb.area_id=dtb1.id WHERE deb.DTYPE LIKE 'Distribution' GROUP BY tb.id";
		
    private static final String COUNT_QUERY = "SELECT count(*) " + FROM_QUERY;

	
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
	public List<DescriptionElementBase> listDescriptionElementsForTaxon(Taxon taxon, Set<Feature> setFeature){
		return descriptionService.listDescriptionElementsForTaxon(taxon, setFeature, null, null, null, DESCRIPTION_INIT_STRATEGY);
	}
	
	@Override
	public List<Distribution> getDistribution(Taxon taxon) {
		Set<Feature> setFeature = new HashSet<Feature>(Arrays.asList(Feature.DISTRIBUTION()));
		List<Distribution> listTaxonDescription = descriptionService.listDescriptionElementsForTaxon(taxon, setFeature, null, null, null, DESCRIPTION_INIT_STRATEGY);
		return listTaxonDescription;
		
	}
	
	@Override
	public List<TaxonNode> getAllNodes(int start, int end){
		Classification classification = getChosenClassification();
		List<TaxonNode> nodesForClassification = taxonNodeService.listAllNodesForClassification(classification, start, end);
		return nodesForClassification;
	}


	@Override
	public Classification getChosenClassification() {
		VaadinSession session = VaadinSession.getCurrent();
		UUID classificationUUID = (UUID) session.getAttribute("classificationUUID");
		Classification classification = classificationService.load(classificationUUID);
		return classification;
	}
	
	@Override
	public int getSizeOfClassification(){
		Classification classification = getChosenClassification();
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
	
	
	
	@Override
	public LazyLoadedContainer getLazyLoadedContainer(){
		LazyLoadedContainer lz = new LazyLoadedContainer(CdmTaxonTableCollection.class);
	    lz.addListener(this);
		return lz;
		
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
	public IClassificationService getClassificationService() {
		return classificationService;
	}


	@Override
	public IVocabularyService getVocabularyService() {
		return vocabularyService;
	}


	@Override
	public IDescriptionService getDescriptionService() {
		return descriptionService;
	}


	@Override
	public ITaxonNodeService getTaxonNodeService() {
		return taxonNodeService;
	}


	@Override
	public ITermService getTermService() {
		return termService;
	}
	
	@Override
	public LazyLoadedContainer getTableContainer() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public ITaxonService getTaxonService() {
		return taxonService;
	}


}
