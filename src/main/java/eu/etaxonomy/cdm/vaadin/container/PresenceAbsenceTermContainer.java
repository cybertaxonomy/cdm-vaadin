package eu.etaxonomy.cdm.vaadin.container;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.data.util.BeanItemContainer;

import eu.etaxonomy.cdm.model.common.TermType;
import eu.etaxonomy.cdm.model.description.PresenceAbsenceTerm;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;

public class PresenceAbsenceTermContainer extends BeanItemContainer<PresenceAbsenceTerm> {

	private static final long serialVersionUID = -7891310979870159325L;
	
	private static PresenceAbsenceTermContainer instance;
	
	public static final Map<String, PresenceAbsenceTerm> titleToTermMap = new HashMap<>();

	private PresenceAbsenceTermContainer()
			throws IllegalArgumentException {
		super(PresenceAbsenceTerm.class);
		List<PresenceAbsenceTerm> terms = CdmSpringContextHelper.getTermService().listByTermType(TermType.PresenceAbsenceTerm, null, null, null, null);
		for (PresenceAbsenceTerm presenceAbsenceTerm : terms) {
			titleToTermMap.put(presenceAbsenceTerm.getTitleCache(), presenceAbsenceTerm);
			addBean(presenceAbsenceTerm);
		}
	}
	
	public static PresenceAbsenceTermContainer getInstance(){
		if(instance==null){
			instance = new PresenceAbsenceTermContainer();
		}
		return instance;
	}


}
