package eu.etaxonomy.vaadin.ui.mainmenu;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;

import eu.etaxonomy.vaadin.ui.MainMenu;
import eu.etaxonomy.vaadin.ui.MenuItem;
import eu.etaxonomy.vaadin.ui.UIInitializedEvent;

// MenuBeanDiscovery is not yet working with spring!!!!!
//
// @NormalUIScoped // FIXME this provided a MenuBeanDiscoveryBean as a proxy, which is the equivalent in spring?
// otherwise the following error will be thrown:
//    Error creating bean with name 'testView': Scope 'vaadin-view' is not active for the current thread;
//    consider defining a scoped proxy for this bean if you intend to refer to it from a singleton
//###DISABLED:
//@SpringComponent
//@UIScope
public class MenuBeanDiscoveryBean {

	@Autowired
	private ApplicationContext beanManager;

    private UIEventBus uiEventBus;

    @Autowired
    private void setUiEventBus(UIEventBus uiEventBus){
        this.uiEventBus = uiEventBus;
        uiEventBus.subscribe(this);
    }

	private MainMenu mainMenuLookup = null;

    MenuBeanDiscoveryBean menuBeanDiscoveryBean;

	public MenuBeanDiscoveryBean() {

	    System.out.println("###########");
	}

	@Autowired
	public void setMainMenu(MainMenu mainMenu){
	    this.mainMenuLookup = mainMenu;
	}

	@EventBusListenerMethod
	protected void doMenuItemLookup(UIInitializedEvent event) {

		if (mainMenuLookup == null) {
		    // IGNORE
			return;
		}

		MainMenu mainMenu = mainMenuLookup;

		Map<String, View> beans = beanManager.getBeansOfType(View.class);

		List<View> menuItemBeans = beans.values().stream()
				.filter(bean -> bean.getClass().isAnnotationPresent(MenuItem.class)).sorted(new BeanNameComparer())
				.collect(Collectors.toList());

		menuItemBeans.forEach(menuItemBean -> {
			MenuItem menuItemAnnotation = menuItemBean.getClass().getAnnotation(MenuItem.class);
			SpringView viewAnnotation = menuItemBean.getClass().getAnnotation(SpringView.class);

			mainMenu.addMenuItem(menuItemAnnotation.name(), menuItemAnnotation.icon(), viewAnnotation.name());
		});
	}

	private static class BeanNameComparer implements Comparator<View> {

		@Override
		public int compare(View a, View b) {
			MenuItem aAnnotation = a.getClass().getAnnotation(MenuItem.class);
			MenuItem bAnnotation = b.getClass().getAnnotation(MenuItem.class);

			return aAnnotation.order() - bAnnotation.order();
		}
	}
}
