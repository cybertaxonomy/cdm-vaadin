package eu.etaxonomy.cdm.vaadin.container;

import java.util.Collection;
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

	public PresenceAbsenceTermContainer()
			throws IllegalArgumentException {
		super(PresenceAbsenceTerm.class);
		Collection<PresenceAbsenceTerm> distributionStatus = (Set<PresenceAbsenceTerm>) VaadinSession.getCurrent().getAttribute(DistributionEditorUtil.SATTR_DISTRIBUTION_STATUS);
        if(distributionStatus==null || distributionStatus.isEmpty()){
            distributionStatus = CdmSpringContextHelper.getTermService().listByTermType(TermType.PresenceAbsenceTerm, null, null, null, null);
        }
		TermCacher termCacher = TermCacher.getInstance();
		for (PresenceAbsenceTerm presenceAbsenceTerm : distributionStatus) {
			termCacher.addPresenceAbsenceTerm(presenceAbsenceTerm);
			addBean(presenceAbsenceTerm);
		}
	}

}
