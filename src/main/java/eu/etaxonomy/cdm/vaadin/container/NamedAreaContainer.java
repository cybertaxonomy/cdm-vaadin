package eu.etaxonomy.cdm.vaadin.container;

import java.util.Set;

import com.vaadin.data.util.BeanItemContainer;

import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.location.NamedArea;
import eu.etaxonomy.cdm.vaadin.util.TermCacher;

public class NamedAreaContainer extends BeanItemContainer<NamedArea> {

    private static final long serialVersionUID = 9150424479597481361L;

	public NamedAreaContainer(TermVocabulary<NamedArea> vocabulary)
	        throws IllegalArgumentException {
	    super(NamedArea.class);
	    Set<NamedArea> namedAreas = vocabulary.getTerms();
	    TermCacher termCacher = TermCacher.getInstance();
	    for (NamedArea namedArea: namedAreas) {
	        termCacher.addNamedArea(namedArea);
	        addBean(namedArea);
	    }
	}

}
