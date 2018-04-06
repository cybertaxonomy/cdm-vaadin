package eu.etaxonomy.cdm.vaadin.container;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinSession;

import eu.etaxonomy.cdm.model.common.TermType;
import eu.etaxonomy.cdm.model.description.PresenceAbsenceTerm;
import eu.etaxonomy.cdm.model.metadata.CdmPreference;
import eu.etaxonomy.cdm.model.metadata.PreferencePredicate;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.util.DistributionEditorUtil;

public class PresenceAbsenceTermContainer extends BeanItemContainer<PresenceAbsenceTerm> {

	private static final long serialVersionUID = -7891310979870159325L;

	private static PresenceAbsenceTermContainer instance;

	private static Collection<PresenceAbsenceTerm> defaultDistributionStatus;

    static class PresenceAbsenceTermComparator implements Comparator<PresenceAbsenceTerm> {
        @Override
        public int compare(PresenceAbsenceTerm pa1, PresenceAbsenceTerm pa2) {
            return pa1.compareTo(pa2);
        }

    }

	private PresenceAbsenceTermContainer()
			throws IllegalArgumentException {
		super(PresenceAbsenceTerm.class);
		initDataModel();
	}

    private void initDataModel() {
        Collection<PresenceAbsenceTerm> distributionStatus = getDistributionStatusList(TERMS_INIT_STRATEGY);
        defaultDistributionStatus = distributionStatus;
		addAll(distributionStatus);
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
        List<PresenceAbsenceTerm> paList;
        if (statusPref != null){
            List<UUID> uuidList = statusPref.getValueUuidList();
            paList = CdmSpringContextHelper.getTermService().load(uuidList, propertyPath).stream().map(db -> (PresenceAbsenceTerm)db).collect(Collectors.toList());
            paList.sort(new PresenceAbsenceTermComparator());
            return paList;
        }else{
            paList = CdmSpringContextHelper.getTermService().listByTermType(TermType.PresenceAbsenceTerm, null, null, null,propertyPath);
            paList.sort(new PresenceAbsenceTermComparator());
            return paList;
        }
    }

    protected static final List<String> TERMS_INIT_STRATEGY = Arrays.asList(new String []{
            "$",
            "representations",
    });
}
