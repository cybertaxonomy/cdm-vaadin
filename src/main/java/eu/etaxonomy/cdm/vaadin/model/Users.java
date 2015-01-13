package eu.etaxonomy.cdm.vaadin.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;

public class Users implements Container {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private final List<User> users;

	public Users() {

		User user1 = new User("first user");
		user1.addProp("p1", "val_b_1");
		user1.addProp("p2", "val_b_2");

		User user2 = new User("second_user");
		user2.addProp("p1", "val_a_1");
		user2.addProp("p2", "val_a_2");

		users = new ArrayList<User>();
		users.add(user1);
		users.add(user2);
	}

	@Override
	public Item getItem(Object itemId) {
		String tool = "string";
		String bar = "foo";
		return null;
	}

	@Override
	public Collection<?> getContainerPropertyIds() {
	    Collection<String> c = new ArrayList<String>();
		for(User user:users){
			c.addAll(user.getPropertyId());
		}
		return c;
	}

	@Override
	public Collection<?> getItemIds() {
	    Collection<String> c = new ArrayList<String>();
		for(User user:users){
			c.addAll(user.getItemId());
		}
		return c;
	}

	@Override
	public Property getContainerProperty(Object itemId, Object propertyId) {
		// TODO Auto-generated method stub
	    ObjectProperty<String> property = new ObjectProperty<String>((String)itemId);
		return property;
	}

	@Override
	public Class<?> getType(Object propertyId) {
		// TODO Auto-generated method stub
		return String.class;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return users.size();
	}

	@Override
	public boolean containsId(Object itemId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Item addItem(Object itemId) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object addItem() throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeItem(Object itemId)
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addContainerProperty(Object propertyId, Class<?> type,
			Object defaultValue) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeContainerProperty(Object propertyId)
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAllItems() throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return false;
	}
}