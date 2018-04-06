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
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.TransactionStatus;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;
import org.vaadin.addons.lazyquerycontainer.QueryFactory;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Notification;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.i18n.Messages;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.common.DefinedTermBase;
import eu.etaxonomy.cdm.model.common.Language;
import eu.etaxonomy.cdm.model.common.OrderedTermVocabulary;
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
import eu.etaxonomy.cdm.vaadin.util.DistributionStatusQueryDefinition;
import eu.etaxonomy.cdm.vaadin.util.DistributionStatusQueryFactory;
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
    private CdmRepository repo; // TODO remove, since this is already in the super class

	/**
     * {@inheritDoc}
     */
    @Override
    protected void eventViewBusSubscription(ViewEventBus viewEventBus) {
        // no point subscribing
    }

    public int updateDistributionField(NamedArea area, PresenceAbsenceTerm distributionStatus, Taxon taxon) {
	    TransactionStatus tx = repo.startTransaction();
	    taxon = (Taxon)repo.getTaxonService().find(taxon.getUuid());
	    if(area==null){
	    	Notification.show(Messages.getLocalizedString(Messages.DistributionTablePresenter_ERROR_UPDATE_DISTRIBUTION_TERM));
	    	repo.commitTransaction(tx);
	    	return -1;
	    }
	    List<Distribution> distributions = getDistributions(taxon);
	    Distribution distribution = null;
	    for(Distribution dist : distributions){
	        if(dist.getArea()!=null && dist.getArea().equals(area)){
	            distribution = dist;
	            break;
	        }
	    }
	    if(distribution==null){
	    	//create new distribution
	    	distribution = Distribution.NewInstance(area, distributionStatus);
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
	    else if(distributionStatus == null){//delete descriptionElementBase
	        DescriptionBase<?> desc = distribution.getInDescription();
	        desc.removeElement(distribution);
	    	repo.commitTransaction(tx);
            return 1;
	    }
	    else{//update distribution
           distribution.setStatus(distributionStatus);
           repo.getCommonService().saveOrUpdate(distribution);
           repo.commitTransaction(tx);
           return 0;
        }
	    repo.commitTransaction(tx);
	    return -1;
	}

    public CdmSQLContainer getSQLContainer() throws SQLException{
         List<Integer> nodeIds = new ArrayList<>();
         for (TaxonNode taxonNode : getAllNodes()) {
             nodeIds.add(taxonNode.getId());
         }
         List<NamedArea> namedAreas = getChosenAreas();
         if(namedAreas!=null){
             return new CdmSQLContainer(CdmQueryFactory.generateTaxonDistributionQuery(nodeIds, namedAreas));
         }
         return null;
    }

    public PresenceAbsenceTermContainer getPresenceAbsenceTermContainer() {
        return PresenceAbsenceTermContainer.getInstance();
    }

    public List<DescriptionElementBase> listDescriptionElementsForTaxon(Taxon taxon, Set<Feature> setFeature){
        List<DescriptionElementBase> listDescriptionElementsForTaxon = CdmSpringContextHelper.getDescriptionService().listDescriptionElementsForTaxon(taxon, setFeature, null, null, null, DESCRIPTION_INIT_STRATEGY);
        sort(listDescriptionElementsForTaxon);
        return listDescriptionElementsForTaxon;
    }

    public List<NamedArea> getUneditableAreas(){
        List<NamedArea> uneditable = new ArrayList<>();
        return uneditable;
    }

    public NamedArea getAreaFromString(String areaString){
        List<NamedArea> namedAreas = getChosenAreas();
        NamedArea area = null;
        for(NamedArea namedArea:namedAreas){
            Representation representation = namedArea.getRepresentation(Language.DEFAULT());
            if(representation!=null){
                if(DistributionEditorUtil.isAbbreviatedLabels()){
                    String label = representation.getLabel();
                    String abbreviatedLabel = representation.getAbbreviatedLabel();
                    if(abbreviatedLabel!=null && abbreviatedLabel.equalsIgnoreCase(areaString)){
                        area = namedArea;
                        break;
                    }
                    else if(label!=null && label.equalsIgnoreCase(areaString)){
                        area = namedArea;
                        break;
                    }
                }
            }
            if(namedArea.getTitleCache().equalsIgnoreCase(areaString)){
                area = namedArea;
                break;
            }
        }
        return area;
    }

    private List<Distribution> getDistributions(Taxon taxon) {
        Set<Feature> setFeature = new HashSet<>(Arrays.asList(Feature.DISTRIBUTION()));
        List<Distribution> listTaxonDescription = CdmSpringContextHelper.getDescriptionService()
                .listDescriptionElementsForTaxon(taxon, setFeature, null, null, null, DESCRIPTION_INIT_STRATEGY);
        return listTaxonDescription;

    }

	private List<NamedArea> getChosenAreas(){
	    List<NamedArea> namedAreas = (List<NamedArea>)VaadinSession.getCurrent().getAttribute(DistributionEditorUtil.SATTR_SELECTED_AREAS);
	    if(namedAreas!=null && namedAreas.isEmpty()){
	        return getChosenAreasFromVoc();
	    }
	    return namedAreas;
	}

	private List<NamedArea> getChosenAreasFromVoc(){
	    VaadinSession session = VaadinSession.getCurrent();
	    UUID vocUUID = (UUID) session.getAttribute(DistributionEditorUtil.SATTR_SELECTED_AREA_VOCABULARY_UUID);
	    TermVocabulary<NamedArea> vocabulary = CdmSpringContextHelper.getVocabularyService().load(vocUUID, Arrays.asList("terms")); //$NON-NLS-1$
	    vocabulary = CdmBase.deproxy(vocabulary, TermVocabulary.class);
	    if (vocabulary instanceof OrderedTermVocabulary) {
	        List<NamedArea> list = new ArrayList<> (((OrderedTermVocabulary)vocabulary).getOrderedTerms());
	        Collections.reverse(list);
	        return list;
	    }else {
	        return vocabulary.getTermsOrderedByLabels(Language.DEFAULT()).stream().collect(Collectors.toCollection(ArrayList::new));
	    }

	}

   private List<TaxonNode> getChosenTaxonNodes() {
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

	private List<TaxonNode> getAllNodes(){
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

	protected static final List<String> DESCRIPTION_INIT_STRATEGY = Arrays.asList(new String []{
            "$", //$NON-NLS-1$
            "elements.*", //$NON-NLS-1$
            "elements.sources.citation.authorship.$", //$NON-NLS-1$
            "elements.sources.nameUsedInSource.originalNameString", //$NON-NLS-1$
            "elements.area.level", //$NON-NLS-1$
            "elements.modifyingText", //$NON-NLS-1$
            "elements.states.*", //$NON-NLS-1$
            "elements.media", //$NON-NLS-1$
            "elements.multilanguageText", //$NON-NLS-1$
            "multilanguageText", //$NON-NLS-1$
            "stateData.$", //$NON-NLS-1$
            "annotations", //$NON-NLS-1$
            "markers", //$NON-NLS-1$
            "sources.citation.authorship", //$NON-NLS-1$
            "sources.nameUsedInSource", //$NON-NLS-1$
            "multilanguageText", //$NON-NLS-1$
            "media", //$NON-NLS-1$
            "name.$", //$NON-NLS-1$
            "name.rank", //$NON-NLS-1$
            "name.status.type", //$NON-NLS-1$
            "taxon2.name", //$NON-NLS-1$
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

	/**Unused Methods*/
	// TODO: Currently unused. Remove?
    private List<String> getAbbreviatedNamedAreas() {
        List<NamedArea> terms = getChosenAreasFromVoc();
        List<String> list = new ArrayList<>();
        for(DefinedTermBase<?> dtb: terms){
            for(Representation r : dtb.getRepresentations()){
                list.add(r.getAbbreviatedLabel());
            }
        }
        return list;
    }

    // TODO: Currently unused. Remove?
    private HashMap<DescriptionElementBase, Distribution> getDistribution(DefinedTermBase dt, Taxon taxon) {
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

    public LazyQueryContainer getAreaDistributionStatusContainer() {
        List<UUID> nodeUuids = getAllNodes().stream().map(n -> n.getUuid()).collect(Collectors.toCollection(ArrayList::new));
        List<NamedArea> namedAreas = getChosenAreas();
        if(namedAreas!=null){
            QueryFactory factory = new DistributionStatusQueryFactory(this.repo, nodeUuids, namedAreas);
            QueryDefinition defintion = new DistributionStatusQueryDefinition(namedAreas, true, 50);
            return new LazyQueryContainer(defintion, factory);
        }
        return null;
    }
}
