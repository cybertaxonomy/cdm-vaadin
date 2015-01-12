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

import eu.etaxonomy.cdm.api.service.IDescriptionService;
import eu.etaxonomy.cdm.api.service.ITermService;
import eu.etaxonomy.cdm.api.service.IVocabularyService;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.common.DefinedTermBase;
import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.description.DescriptionElementBase;
import eu.etaxonomy.cdm.model.description.Distribution;
import eu.etaxonomy.cdm.model.description.Feature;
import eu.etaxonomy.cdm.model.description.PresenceAbsenceTerm;
import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.view.dbstatus.DistributionTableView;
import eu.etaxonomy.cdm.vaadin.view.dbstatus.IDistributionTableComponent;


public class DistributionTablePresenter implements IDistributionTableComponent.DistributionTableComponentListener{

	private final IVocabularyService vocabularyService;
	private final IDescriptionService descriptionService;
	private final ITermService termService;
	private final DistributionTableView view;

	public DistributionTablePresenter(DistributionTableView dtv){
	    this.view = dtv;
	    view.addListener(this);

		vocabularyService = (IVocabularyService)CdmSpringContextHelper.newInstance().getBean("vocabularyServiceImpl");
		descriptionService = (IDescriptionService)CdmSpringContextHelper.newInstance().getBean("descriptionServiceImpl");
		termService = (ITermService)CdmSpringContextHelper.newInstance().getBean("termServiceImpl");
	}


	@Override
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
		for(DescriptionElementBase deb : listTaxonDescription){
			if(deb instanceof Distribution){
				Distribution db = (Distribution)deb;
				String titleCache = dt.getTitleCache();
				if(db.getArea().getTitleCache().equalsIgnoreCase(titleCache)){
					HashMap<DescriptionElementBase, Distribution> map = new HashMap<DescriptionElementBase, Distribution>();
					map.put(deb, db);
					return map;
				}
			}
		}
		return null;
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



}
