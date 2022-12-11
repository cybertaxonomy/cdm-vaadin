package eu.etaxonomy.cdm.vaadin.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;

import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.location.NamedArea;
import eu.etaxonomy.cdm.model.term.OrderedTermVocabulary;
import eu.etaxonomy.cdm.model.term.TermVocabulary;

/**
 * A container containing the {@link NamedArea}s of a specified {@link TermVocabulary}.
 * @author fabreim
 * @since May 17, 2018
 */
public class NamedAreaContainer extends BeanItemContainer<NamedArea> {

    private static final long serialVersionUID = 9150424479597481361L;

    /**
     * Creates a NamedAreaContainer containing all {@link NamedArea}s of the given {@code vocabulary}
     * in alphabetically ascending order.
     * @param vocabulary The vocabulary containing the {@link NamedArea}s to populate the container with.
     * @throws IllegalArgumentException
     */
	public NamedAreaContainer(TermVocabulary<NamedArea> vocabulary)
	        throws IllegalArgumentException {
	    super(NamedArea.class);
	    List<NamedArea> namedAreas;
	    if (vocabulary.isInstanceOf(OrderedTermVocabulary.class)) {
	        OrderedTermVocabulary<NamedArea> orderedVoc = CdmBase.deproxy(vocabulary, OrderedTermVocabulary.class);
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

	/**
	 * Comparator to sort the {@link NamedArea}s contained in {@link NamedAreaContainer}s.
	 */
	private class AlphabeticallyAscendingNamedAreaComparator implements Comparator<NamedArea>{

        @Override
        public int compare(NamedArea o1, NamedArea o2) {
            return o2.compareTo(o1);
        }
	}
}