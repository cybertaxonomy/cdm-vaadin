package eu.etaxonomy.cdm.vaadin.container;

import java.util.List;

import com.vaadin.data.util.BeanContainer;

import eu.etaxonomy.cdm.model.common.Language;
import eu.etaxonomy.cdm.model.common.TermType;
import eu.etaxonomy.cdm.model.description.PresenceAbsenceTerm;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.util.TermCacher;

public class PresenceAbsenceTermContainer extends BeanContainer<String, PresenceAbsenceTerm> {

	private static final long serialVersionUID = -7891310979870159325L;

	private static PresenceAbsenceTermContainer instance;

	private PresenceAbsenceTermContainer()
			throws IllegalArgumentException {
		super(PresenceAbsenceTerm.class);
		setBeanIdResolver(new BeanIdResolver<String, PresenceAbsenceTerm>() {

            private static final long serialVersionUID = -6008191522128487319L;

            @Override
            public String getIdForBean(PresenceAbsenceTerm bean) {
                return bean.getRepresentation(Language.DEFAULT()).getAbbreviatedLabel();
            }
        });
		List<PresenceAbsenceTerm> terms = CdmSpringContextHelper.getTermService().listByTermType(TermType.PresenceAbsenceTerm, null, null, null, null);
		TermCacher termCacher = TermCacher.getInstance();
		for (PresenceAbsenceTerm presenceAbsenceTerm : terms) {
			termCacher.addPresenceAbsenceTerm(presenceAbsenceTerm);
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
