package eu.etaxonomy.cdm.vaadin.presenter.dbstatus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.UUID;

import com.vaadin.server.VaadinSession;

import eu.etaxonomy.cdm.api.service.IClassificationService;
import eu.etaxonomy.cdm.api.service.IDescriptionService;
import eu.etaxonomy.cdm.api.service.ITaxonNodeService;
import eu.etaxonomy.cdm.api.service.ITaxonService;
import eu.etaxonomy.cdm.api.service.ITermService;
import eu.etaxonomy.cdm.api.service.IVocabularyService;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.common.DefinedTermBase;
import eu.etaxonomy.cdm.model.common.Language;
import eu.etaxonomy.cdm.model.common.Representation;
import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.description.DescriptionElementBase;
import eu.etaxonomy.cdm.model.description.Distribution;
import eu.etaxonomy.cdm.model.description.Feature;
import eu.etaxonomy.cdm.model.description.PresenceAbsenceTerm;
import eu.etaxonomy.cdm.model.description.TaxonDescription;
import eu.etaxonomy.cdm.model.location.NamedArea;
import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.model.taxon.TaxonNode;
import eu.etaxonomy.cdm.vaadin.container.CdmSQLContainer;
import eu.etaxonomy.cdm.vaadin.model.LazyLoadedContainer;
import eu.etaxonomy.cdm.vaadin.util.CdmQueryFactory;
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
	private final ITaxonService taxonService;

	public DistributionTablePresenter(DistributionTableView dtv) throws SQLException{
	    this.view = dtv;
	    view.addListener(this);
	    taxonService = CdmSpringContextHelper.getTaxonService();
	    classificationService = CdmSpringContextHelper.getClassificationService();
	    taxonNodeService = CdmSpringContextHelper.getTaxonNodeService();
		vocabularyService = CdmSpringContextHelper.getVocabularyService();
		descriptionService = CdmSpringContextHelper.getDescriptionService();
		termService = CdmSpringContextHelper.getTermService();
		view.dataBinding();
	}


	@Override
    public int updateDistributionField(String distributionAreaString, Object comboValue, Taxon taxon) {
	    Set<DefinedTermBase> chosenTerms = getChosenTerms();
	    NamedArea namedArea = null;
	    for(DefinedTermBase term:chosenTerms){
	        if(term.getTitleCache().equalsIgnoreCase(distributionAreaString)){
	            namedArea = (NamedArea) term;
	            break;
	        }
	    }
	    List<Distribution> distributions = getDistributions(taxon);
	    Distribution distribution = null;
	    for(Distribution dist : distributions){
	        if(dist.getArea().equals(namedArea)){
	            distribution = dist;
	            break;
	        }
	    }
	    if(distribution==null){
	    	//create new distribution
	    	distribution = Distribution.NewInstance(namedArea, (PresenceAbsenceTerm) comboValue);
			Set<TaxonDescription> descriptions = taxon.getDescriptions();
			if (descriptions != null && !descriptions.isEmpty()) {
			    for (TaxonDescription desc : descriptions) {
			        // add to first taxon description
			        desc.addElement(distribution);
				    getTaxonService().saveOrUpdate(taxon);
			        return 0;
			    }
			} else {// there are no TaxonDescription yet.
			    TaxonDescription taxonDescription = TaxonDescription.NewInstance(taxon);
			    taxonDescription.addElement(distribution);
			    taxon.addDescription(taxonDescription);
			    getTaxonService().saveOrUpdate(taxon);
			    return 0;
			}
	    }
	    else if(comboValue == null){//delete descriptionElementBase
	    	distribution.getInDescription().removeElement(distribution);
            getTaxonService().saveOrUpdate(taxon);
            return 1;
	    }
	    else{
           distribution.setStatus((PresenceAbsenceTerm)comboValue);
           getTaxonService().saveOrUpdate(taxon);
           return 0;
        }
	    return -1;
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
	public List<String> getAbbreviatedTermList() {
		SortedSet<DefinedTermBase> terms = getTermSet();
		List<String> list = new ArrayList<String>();
		for(DefinedTermBase dtb: terms){
		    for(Representation r : dtb.getRepresentations()){
		        list.add(r.getAbbreviatedLabel());
		    }
		}
//		Collections.sort(list);
		return list;
	}

    @Override
    public List<String> getTermList() {
        SortedSet<DefinedTermBase> terms = getTermSet();
        List<String> list = new ArrayList<String>();
        for(DefinedTermBase dtb: terms){
           list.add(dtb.getTitleCache());
        }
//      Collections.sort(list);
        return list;
    }


	private SortedSet<DefinedTermBase> getTermSet(){
	    VaadinSession session = VaadinSession.getCurrent();
	    UUID termUUID = (UUID) session.getAttribute("selectedTerm");
	    TermVocabulary<DefinedTermBase> term = vocabularyService.load(termUUID);
	    term = CdmBase.deproxy(term, TermVocabulary.class);
	    return term.getTermsOrderedByLabels(Language.DEFAULT());
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
		List<DescriptionElementBase> listDescriptionElementsForTaxon = descriptionService.listDescriptionElementsForTaxon(taxon, setFeature, null, null, null, DESCRIPTION_INIT_STRATEGY);
		sort(listDescriptionElementsForTaxon);
		return listDescriptionElementsForTaxon;
	}

	@Override
	public List<Distribution> getDistributions(Taxon taxon) {
		Set<Feature> setFeature = new HashSet<Feature>(Arrays.asList(Feature.DISTRIBUTION()));
		List<Distribution> listTaxonDescription = descriptionService.listDescriptionElementsForTaxon(taxon, setFeature, null, null, null, DESCRIPTION_INIT_STRATEGY);
		return listTaxonDescription;

	}

	@Override
	public List<TaxonNode> getAllNodes(){
		TaxonNode taxonNode = getChosenTaxonNode();
		List<TaxonNode> nodes = new ArrayList<TaxonNode>();
		if(taxonNode.getTaxon()!=null){
			nodes.add(taxonNode);
		}
		nodes.addAll(taxonNodeService.loadChildNodesOfTaxonNode(taxonNode, null, true, null));
		return nodes;
	}


	@Override
	public TaxonNode getChosenTaxonNode() {
		VaadinSession session = VaadinSession.getCurrent();
		UUID taxonNodeUUID = (UUID) session.getAttribute("taxonNodeUUID");
		TaxonNode classificationNode = taxonNodeService.load(taxonNodeUUID);
		return classificationNode;
	}

	@Override
	public int getSizeOfTaxonNode(){
		TaxonNode taxonNode = getChosenTaxonNode();
		return taxonNodeService.loadChildNodesOfTaxonNode(taxonNode, null, true, null).size();
	}


	@Override
	public CdmSQLContainer getSQLContainer() throws SQLException{
		List<Integer> nodeIds = new ArrayList<Integer>();
		for (TaxonNode taxonNode : getAllNodes()) {
			nodeIds.add(taxonNode.getId());
		}
		List<String> termList = getTermList();
		CdmSQLContainer container = new CdmSQLContainer(CdmQueryFactory.generateTaxonDistributionQuery(termList, nodeIds));
		return container;
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
		return null;
	}



	@Override
	public ITaxonService getTaxonService() {
		return taxonService;
	}


	/**Helper Methods*/

	private void sort(List<DescriptionElementBase> list){
		Collections.sort(list, new Comparator<DescriptionElementBase>() {

			@Override
			public int compare(DescriptionElementBase o1, DescriptionElementBase o2) {
				String feature1 = o1.getFeature().getTitleCache();
				String feature2 = o2.getFeature().getTitleCache();
				if(feature1 !=null && feature2 !=null){
					return feature1.compareTo(feature2);
				}else{
					return 0;

				}
			}
		});

	}
}
