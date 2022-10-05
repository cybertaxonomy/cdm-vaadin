/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.mvp;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.vaadin.spring.events.EventBus;

import com.vaadin.ui.CustomComponent;

import eu.etaxonomy.cdm.vaadin.permission.ReleasableResourcesView;

/**
 * AbstractView is the base class of all MVP views. It takes care of finding
 * appropriate presenter component for the view.
 *
 * @param <P> type of the presenter this view uses.
 * @param <V>
 * @author Peter / Vaadin
 */
@SuppressWarnings("serial")
public abstract class AbstractView<P extends AbstractPresenter> extends CustomComponent
		implements ApplicationContextAware, ReleasableResourcesView, DisposableBean {

    private final static Logger logger = LogManager.getLogger();

	private P presenter;


	//TODO remove?
	private ApplicationContext applicationContext;

	//this is used by subclasses in same package
    @Autowired
    EventBus.ViewEventBus viewEventBus;

	@SuppressWarnings("unchecked")
    @PostConstruct
	protected final void init() {
		LogManager.getLogger(getClass().getSimpleName()).info("View init");
		if(!ApplicationView.class.isAssignableFrom(this.getClass())){
		    throw new RuntimeException("Any view bean must implement the ApplicationView interface: ViewBean ---> ViewInterface ---> ApplicationView");
		}

		initContent();

		presenter.init((ApplicationView<P>) this);

		onViewReady();
	}

	protected void setPresenter(P presenter) {
		this.presenter = presenter;
	}

    @Autowired
	protected final void injectPresenter(P presenter){
        logger.trace(this.toString() + " injecting presenter " + presenter.toString());
	    setPresenter(presenter);
	}

	@Override
	public void detach() {
		getPresenter().onViewExit();
		super.detach();
	}

	/**
	 * Initialize the Components of the View
	 */
	protected abstract void initContent();

	/**
	 * This method is called after the content of the view and the presenter
	 * are initialized and ready.
	 */
	protected void onViewReady() {
	    logger.trace("View ready");
	}

	protected P getPresenter() {
		return presenter;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public EventBus.ViewEventBus getViewEventBus(){
	    return viewEventBus;
	}

   @Override
    public void releaseResourcesOnAccessDenied() {
        getPresenter().onViewExit();
    }

    @Override
    public void destroy() throws Exception {
       if(presenter != null){
           presenter.destroy();
       }
    }
}