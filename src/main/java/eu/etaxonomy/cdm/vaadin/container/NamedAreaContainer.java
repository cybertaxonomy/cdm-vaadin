package eu.etaxonomy.cdm.vaadin.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;

import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.location.NamedArea;
import eu.etaxonomy.cdm.vaadin.util.TermCacher;

public class NamedAreaContainer extends BeanItemContainer<NamedArea> {

    private static final long serialVersionUID = 9150424479597481361L;

	public NamedAreaContainer(TermVocabulary<NamedArea> vocabulary)
	        throws IllegalArgumentException {
	    super(NamedArea.class);
	    List<NamedArea> namedAreas = new ArrayList<>(vocabulary.getTerms());
	    Collections.sort(namedAreas, new AlphabeticallyAscendingNamedAreaComparator());
	    TermCacher termCacher = TermCacher.getInstance();
	    for (NamedArea namedArea: namedAreas) {
	        termCacher.addNamedArea(namedArea);
	        addBean(namedArea);
	    }
	}

	private class AlphabeticallyAscendingNamedAreaComparator implements Comparator<NamedArea>{

        @Override
        public int compare(NamedArea o1, NamedArea o2) {
            return o2.compareTo(o1);
        }

	}

}
