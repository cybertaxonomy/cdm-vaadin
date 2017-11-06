/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.distributionStatus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.TransactionStatus;

import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Notification;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.common.DefinedTermBase;
import eu.etaxonomy.cdm.model.common.Language;
import eu.etaxonomy.cdm.model.common.Representation;
import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.description.DescriptionBase;
import eu.etaxonomy.cdm.model.description.DescriptionElementBase;
import eu.etaxonomy.cdm.model.description.Distribution;
import eu.etaxonomy.cdm.model.description.Feature;
import eu.etaxonomy.cdm.model.description.PresenceAbsenceTerm;
import eu.etaxonomy.cdm.model.description.TaxonDescription;
import eu.etaxonomy.cdm.model.location.NamedArea;
import eu.etaxonomy.cdm.model.taxon.Classification;
import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.model.taxon.TaxonNode;
import eu.etaxonomy.cdm.service.CdmUserHelper;
import eu.etaxonomy.cdm.vaadin.container.CdmSQLContainer;
import eu.etaxonomy.cdm.vaadin.container.PresenceAbsenceTermContainer;
import eu.etaxonomy.cdm.vaadin.util.CdmQueryFactory;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.util.DistributionEditorUtil;
import eu.etaxonomy.vaadin.mvp.AbstractPresenter;

/**
 * @author freimeier
 * @since 18.10.2017
 *
 */
@SpringComponent
@ViewScope
public class DistributionTablePresenter extends AbstractPresenter<IDistributionTableView> {

	private static final long serialVersionUID = 3313043335587777217L;

    @Autowired
    private CdmUserHelper userHelper;

    @Autowired
    @Qualifier("cdmRepository")
    private CdmRepository repo;


	public int updateDistributionField(String distributionAreaString, Object comboValue, Taxon taxon) {
	    TransactionStatus tx = repo.startTransaction();
	    taxon = (Taxon)repo.getTaxonService().find(taxon.getUuid());
	    Set<DefinedTermBase> chosenTerms = getChosenTerms();
	    NamedArea namedArea = null;
	    for(DefinedTermBase term:chosenTerms){
	    	Representation representation = term.getRepresentation(Language.DEFAULT());
	    	if(representation!=null){
	    		if(DistributionEditorUtil.isAbbreviatedLabels()){
	    			String label = representation.getLabel();
	    			String abbreviatedLabel = representation.getAbbreviatedLabel();
					if(abbreviatedLabel!=null && abbreviatedLabel.equalsIgnoreCase(distributionAreaString)){
	    				namedArea = (NamedArea) term;
	    				break;
	    			}
					else if(label!=null && label.equalsIgnoreCase(distributionAreaString)){
						namedArea = (NamedArea) term;
						break;
					}
	    		}
	    	}
	        if(term.getTitleCache().equalsIgnoreCase(distributionAreaString)){
	        	namedArea = (NamedArea) term;
	        	break;
	        }
	    }
	    if(namedArea==null){
	    	Notification.show("Error during update of distribution term!");
	    	repo.commitTransaction(tx);
	    	return -1;
	    }
	    List<Distribution> distributions = getDistributions(taxon);
	    Distribution distribution = null;
	    for(Distribution dist : distributions){
	        if(dist.getArea()!=null && dist.getArea().equals(namedArea)){
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
			        repo.commitTransaction(tx);
			        return 0;
			    }
			} else {// there are no TaxonDescription yet.
			    TaxonDescription taxonDescription = TaxonDescription.NewInstance(taxon);
			    taxonDescription.addElement(distribution);
			    repo.commitTransaction(tx);
			    return 0;
			}
	    }
	    else if(comboValue == null){//delete descriptionElementBase
	        DescriptionBase<?> desc = distribution.getInDescription();
	        desc.removeElement(distribution);
	    	repo.commitTransaction(tx);
            return 1;
	    }
	    else{//update distribution
           distribution.setStatus((PresenceAbsenceTerm)comboValue);
           repo.getCommonService().saveOrUpdate(distribution);
           repo.commitTransaction(tx);
           return 0;
        }
	    repo.commitTransaction(tx);
	    return -1;
	}

	public Set<DefinedTermBase> getChosenTerms() {
		VaadinSession session = VaadinSession.getCurrent();
		UUID vocUUID = (UUID) session.getAttribute(DistributionEditorUtil.SATTR_SELECTED_AREA_VOCABULARY_UUID);
//		getConversationHolder().getSession();
		TermVocabulary<DefinedTermBase> voc = CdmSpringContextHelper.getVocabularyService().load(vocUUID, Arrays.asList("terms"));
//		voc = CdmBase.deproxy(voc);
		return voc.getTerms();
	}

	public List<String> getAbbreviatedTermList() {
		Set<NamedArea> terms = getTermSet();
		List<String> list = new ArrayList<>();
		for(DefinedTermBase<?> dtb: terms){
		    for(Representation r : dtb.getRepresentations()){
		        list.add(r.getAbbreviatedLabel());
		    }
		}
		return list;
	}

	public Set<NamedArea> getNamedAreas(){
	    Set<NamedArea> namedAreas = (Set<NamedArea>) VaadinSession.getCurrent().getAttribute(DistributionEditorUtil.SATTR_SELECTED_AREAS);
	    if(namedAreas!=null && namedAreas.isEmpty()){
	        return getTermSet();
	    }
        return namedAreas;
	}

	private Set<NamedArea> getTermSet(){
	    VaadinSession session = VaadinSession.getCurrent();
	    UUID vocUUID = (UUID) session.getAttribute(DistributionEditorUtil.SATTR_SELECTED_AREA_VOCABULARY_UUID);
	    TermVocabulary<NamedArea> vocabulary = CdmSpringContextHelper.getVocabularyService().load(vocUUID, Arrays.asList("terms"));
	    vocabulary = CdmBase.deproxy(vocabulary, TermVocabulary.class);
	    return vocabulary.getTermsOrderedByLabels(Language.DEFAULT());
	}

	public HashMap<DescriptionElementBase, Distribution> getDistribution(DefinedTermBase dt, Taxon taxon) {
		Set<Feature> setFeature = new HashSet<>(Arrays.asList(Feature.DISTRIBUTION()));
		List<DescriptionElementBase> listTaxonDescription = CdmSpringContextHelper.getDescriptionService().listDescriptionElementsForTaxon(taxon, setFeature, null, null, null, DESCRIPTION_INIT_STRATEGY);
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

	public List<DescriptionElementBase> listDescriptionElementsForTaxon(Taxon taxon, Set<Feature> setFeature){
		List<DescriptionElementBase> listDescriptionElementsForTaxon = CdmSpringContextHelper.getDescriptionService().listDescriptionElementsForTaxon(taxon, setFeature, null, null, null, DESCRIPTION_INIT_STRATEGY);
		sort(listDescriptionElementsForTaxon);
		return listDescriptionElementsForTaxon;
	}

	public List<Distribution> getDistributions(Taxon taxon) {
		Set<Feature> setFeature = new HashSet<>(Arrays.asList(Feature.DISTRIBUTION()));
		List<Distribution> listTaxonDescription = CdmSpringContextHelper.getDescriptionService()
		        .listDescriptionElementsForTaxon(taxon, setFeature, null, null, null, DESCRIPTION_INIT_STRATEGY);
		return listTaxonDescription;

	}

	public List<TaxonNode> getAllNodes(){
		List<TaxonNode> allNodes = new ArrayList<>();

		List<TaxonNode> taxonNodes = getChosenTaxonNodes();
		for (TaxonNode taxonNode : taxonNodes) {
			if(taxonNode.getTaxon()!=null){
				allNodes.add(taxonNode);
			}
			allNodes.addAll(CdmSpringContextHelper.getTaxonNodeService().loadChildNodesOfTaxonNode(taxonNode, null, true, null));
		}
		return allNodes;
	}


	public List<TaxonNode> getChosenTaxonNodes() {
		VaadinSession session = VaadinSession.getCurrent();
		List<UUID> taxonNodeUUIDs = (List<UUID>) session.getAttribute(DistributionEditorUtil.SATTR_TAXON_NODES_UUID);
		UUID classificationUuid = (UUID)session.getAttribute(DistributionEditorUtil.SATTR_CLASSIFICATION);
		if((taxonNodeUUIDs==null || taxonNodeUUIDs.isEmpty()) && classificationUuid!=null){
			Classification classification = CdmSpringContextHelper.getClassificationService().load(classificationUuid);
			if(classification!=null){
				taxonNodeUUIDs = Collections.singletonList(classification.getRootNode().getUuid());
			}
		}
		List<TaxonNode> loadedNodes = CdmSpringContextHelper.getTaxonNodeService().load(taxonNodeUUIDs, null);
		if(loadedNodes!=null){
			return loadedNodes;
		}
		return Collections.emptyList();
	}

	public CdmSQLContainer getSQLContainer() throws SQLException{
		List<Integer> nodeIds = new ArrayList<>();
		for (TaxonNode taxonNode : getAllNodes()) {
			nodeIds.add(taxonNode.getId());
		}
		Set<NamedArea> namedAreas = getNamedAreas();
		if(namedAreas!=null){
			return new CdmSQLContainer(CdmQueryFactory.generateTaxonDistributionQuery(nodeIds, namedAreas));
		}
		return null;
	}

	public PresenceAbsenceTermContainer getPresenceAbsenceTermContainer() {
	    return PresenceAbsenceTermContainer.getInstance();
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
            "name.rank",
            "name.status.type",
            "taxon2.name",
    });

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

	/**
	 *
	 * {@inheritDoc}
	 */
	@Override
	protected void onPresenterReady() {
	    /*
         * The area and taxon settings window should only be displayed after login
         * and only when no classification and areas are chosen yet.
         */
	    VaadinSession vaadinSession = VaadinSession.getCurrent();
	    if(userHelper.userIsAutheticated()
	            && !userHelper.userIsAnnonymous()
	            && (vaadinSession.getAttribute(DistributionEditorUtil.SATTR_CLASSIFICATION) == null
	            || vaadinSession.getAttribute(DistributionEditorUtil.SATTR_SELECTED_AREA_VOCABULARY_UUID) == null
	            || vaadinSession.getAttribute(DistributionEditorUtil.SATTR_SELECTED_AREAS) == null)) {
            getView().openAreaAndTaxonSettings();
        }
    }

}
