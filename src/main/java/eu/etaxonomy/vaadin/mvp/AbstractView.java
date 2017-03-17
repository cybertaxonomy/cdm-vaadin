package eu.etaxonomy.vaadin.mvp;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;

import com.vaadin.ui.CustomComponent;

/**
 * AbstractView is the base class of all MVP views. It takes care of finding
 * appropriate presenter component for the view.
 *
 * @param
 * 			<P>
 *            type of the presenter this view uses.
 *
 * @author Peter / Vaadin
 */
public abstract class AbstractView<P extends AbstractPresenter> extends CustomComponent
		implements ApplicationContextAware {


    public static final Logger logger = Logger.getLogger(AbstractView.class);

	private P presenter;

	private ApplicationContext applicationContext;

    @Autowired
    protected ApplicationEventPublisher eventBus;

	@PostConstruct
	protected final void init() {
		Logger.getLogger(getClass().getSimpleName()).info("View init");
		presenter.init((ApplicationView) this);

		onViewReady();
	}

	protected void setPresenter(P presenter) {
		this.presenter = presenter;
	}

	protected abstract void injectPresenter(P presenter);

	@Override
	public void detach() {
		getPresenter().onViewExit();
		super.detach();
	}

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

	public ApplicationEventPublisher getEventBus(){
	    return eventBus;
	}
}
