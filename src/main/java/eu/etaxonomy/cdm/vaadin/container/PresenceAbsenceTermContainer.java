package eu.etaxonomy.cdm.vaadin.container;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinSession;

import eu.etaxonomy.cdm.model.common.TermType;
import eu.etaxonomy.cdm.model.description.PresenceAbsenceTerm;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.util.DistributionEditorUtil;
import eu.etaxonomy.cdm.vaadin.util.TermCacher;

public class PresenceAbsenceTermContainer extends BeanItemContainer<PresenceAbsenceTerm> {

	private static final long serialVersionUID = -7891310979870159325L;

	private static PresenceAbsenceTermContainer instance;
	private static Collection<PresenceAbsenceTerm> defaultDistributionStatus;

	private PresenceAbsenceTermContainer()
			throws IllegalArgumentException {
		super(PresenceAbsenceTerm.class);
		initDataModel();
	}

    private void initDataModel() {
        Collection<PresenceAbsenceTerm> distributionStatus = new HashSet<>();
        distributionStatus = CdmSpringContextHelper.getTermService().listByTermType(TermType.PresenceAbsenceTerm, null, null, null, null);
        defaultDistributionStatus = distributionStatus;
		TermCacher termCacher = TermCacher.getInstance();
		addAll(distributionStatus);
		for (PresenceAbsenceTerm presenceAbsenceTerm : distributionStatus) {
			termCacher.addPresenceAbsenceTerm(presenceAbsenceTerm);
		}
    }

	public static PresenceAbsenceTermContainer getInstance(){
	    if(instance==null){
	        instance = new PresenceAbsenceTermContainer();
	    }
        Collection<PresenceAbsenceTerm> distributionStatus = new HashSet<>();
        Object attribute = VaadinSession.getCurrent().getAttribute(DistributionEditorUtil.SATTR_DISTRIBUTION_STATUS);
        if(attribute!=null){
            distributionStatus = (Set<PresenceAbsenceTerm>) attribute;
        }
        if(!distributionStatus.isEmpty() && !distributionStatus.equals(defaultDistributionStatus)){
            instance.removeAllItems();
            instance.addAll(distributionStatus);
            defaultDistributionStatus = distributionStatus;
        }
	    return instance;
	}

}
