package eu.etaxonomy.cdm.vaadin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private final String name;
	private final Map<String, String> prop;

	public User(String name) {
		this.name = name;
		prop = new HashMap<String, String>();
	}

	public void addProp(String column, String value) {
		prop.put(column, value);
	}
	
	public String getName(){
		return name;
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

    /**
     * @param itemId
     */
    public Object getItem(Object itemId) {
        String item = itemId.toString();
        for (Map.Entry<String, String> entry : prop.entrySet()) {
            if(item.equalsIgnoreCase(entry.getValue())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public Object getItemProperty(Object propertyId) {
        String item = propertyId.toString();
        for (Map.Entry<String, String> entry : prop.entrySet()) {
            if(item.equalsIgnoreCase(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
    /**
     * @param itemId
     * @param propertyId
     */
    public Object getContainerProperty(Object itemId, Object propertyId) {
        if(prop.containsKey(propertyId) && prop.containsValue(itemId)){
            for (Map.Entry<String, String> entry : prop.entrySet()) {
                if(propertyId.toString().equalsIgnoreCase(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }
}