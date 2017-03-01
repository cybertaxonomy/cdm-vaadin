package eu.etaxonomy.vaadin.ui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.server.FontAwesome;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
// @Qualifier  // FIXME
public @interface MenuItem {

	// @Nonbinding   // FIXME
	String name();

	// @Nonbinding   // FIXME
	FontAwesome icon();

	// @Nonbinding   // FIXME
	int order();
}
