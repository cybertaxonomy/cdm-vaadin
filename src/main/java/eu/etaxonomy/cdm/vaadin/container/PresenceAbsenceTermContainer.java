package eu.etaxonomy.cdm.vaadin.container;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinSession;

import eu.etaxonomy.cdm.model.common.TermType;
import eu.etaxonomy.cdm.model.description.PresenceAbsenceTerm;
import eu.etaxonomy.cdm.model.metadata.CdmPreference;
import eu.etaxonomy.cdm.model.metadata.PreferencePredicate;
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
        Collection<PresenceAbsenceTerm> distributionStatus = getDistributionStatusList(TERMS_INIT_STRATEGY);

        defaultDistributionStatus = distributionStatus;
		TermCacher termCacher = TermCacher.getInstance();
		addAll(distributionStatus);
		for (PresenceAbsenceTerm presenceAbsenceTerm : distributionStatus) {
			termCacher.addPresenceAbsenceTerm(presenceAbsenceTerm);
		}
    }

	public static PresenceAbsenceTermContainer getInstance(){
	    if(instance == null){
	        instance = new PresenceAbsenceTermContainer();
	    }
        Collection<PresenceAbsenceTerm> distributionStatus = new HashSet<>();
        Object attribute = VaadinSession.getCurrent().getAttribute(DistributionEditorUtil.SATTR_DISTRIBUTION_STATUS);
        if(attribute != null){
            distributionStatus = (Set<PresenceAbsenceTerm>) attribute;
        }
        if(!distributionStatus.isEmpty() && !distributionStatus.equals(defaultDistributionStatus)){
            instance.removeAllItems();
            instance.addAll(distributionStatus);
            defaultDistributionStatus = distributionStatus;
        }
	    return instance;
	}

    public static List<PresenceAbsenceTerm> getDistributionStatusList(List<String> propertyPath){
        CdmPreference statusPref = CdmSpringContextHelper.getPreferenceService().findVaadin(PreferencePredicate.AvailableDistributionStatus);
        if (statusPref != null){
            List<UUID> uuidList = statusPref.getValueUuidList();
            return (List)CdmSpringContextHelper.getTermService().load(uuidList, propertyPath);
        }else{
            return CdmSpringContextHelper.getTermService().listByTermType(
                    TermType.PresenceAbsenceTerm, null, null, null, propertyPath);
        }
    }

    protected static final List<String> TERMS_INIT_STRATEGY = Arrays.asList(new String []{
            "$",
            "representations",
    });

}
