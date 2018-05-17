package eu.etaxonomy.cdm.vaadin.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;

import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.common.OrderedTermVocabulary;
import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.location.NamedArea;

public class NamedAreaContainer extends BeanItemContainer<NamedArea> {

    private static final long serialVersionUID = 9150424479597481361L;

	public NamedAreaContainer(TermVocabulary<NamedArea> vocabulary)
	        throws IllegalArgumentException {
	    super(NamedArea.class);
	    List<NamedArea> namedAreas;
	    if (vocabulary.isInstanceOf(OrderedTermVocabulary.class)) {
	        OrderedTermVocabulary orderedVoc = CdmBase.deproxy(vocabulary, OrderedTermVocabulary.class);
	        namedAreas = new ArrayList<>(orderedVoc.getOrderedTerms());
	        Collections.reverse(namedAreas);
	    }else {
	        namedAreas = new ArrayList<>(vocabulary.getTerms());
	        Collections.sort(namedAreas, new AlphabeticallyAscendingNamedAreaComparator());
	    }

	    for (NamedArea namedArea: namedAreas) {
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
