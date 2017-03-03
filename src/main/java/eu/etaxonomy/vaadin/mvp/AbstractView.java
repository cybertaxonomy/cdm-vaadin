package eu.etaxonomy.vaadin.mvp;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

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

	private P presenter;

	private ApplicationContext applicationContext;

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
		Logger.getLogger(getClass().getSimpleName()).info("View ready");
	}

	protected P getPresenter() {
		return presenter;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
