package eu.etaxonomy.cdm.vaadin.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.vaadin.data.util.BeanItemContainer;

import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.location.NamedArea;

public class NamedAreaContainer extends BeanItemContainer<NamedArea> {

    private static final long serialVersionUID = 9150424479597481361L;

	public static final Map<String, NamedArea> titleToNamedAreaMap = new HashMap<>();

	public NamedAreaContainer(TermVocabulary<NamedArea> vocabulary)
	        throws IllegalArgumentException {
	    super(NamedArea.class);
	    Set<NamedArea> namedAreas = vocabulary.getTerms();
	    for (NamedArea namedArea: namedAreas) {
	        titleToNamedAreaMap.put(namedArea.getTitleCache(), namedArea);
	        addBean(namedArea);
	    }
	}

}
