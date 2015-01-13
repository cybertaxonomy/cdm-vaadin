package eu.etaxonomy.cdm.vaadin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.etaxonomy.cdm.model.description.DescriptionElementBase;
import eu.etaxonomy.cdm.model.description.Distribution;

public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private Map<String, String> prop;

	public User(String name) {
		this.name = name;
		prop = new HashMap<String, String>();
	}

	public void addProp(String column, String value) {
		prop.put(column, value);
	}

	public Collection<String> getPropertyId() {
		List<String> propertyId = new ArrayList<String>();
		for (Map.Entry<String, String> entry : prop.entrySet()) {
			propertyId.add(entry.getKey());
		}
		return propertyId;
	}

	public Collection<String> getItemId() {
		List<String> getItemId = new ArrayList<String>();
		for (Map.Entry<String, String> entry : prop.entrySet()) {
			getItemId.add(entry.getValue());
		}
		return getItemId;
	}
}