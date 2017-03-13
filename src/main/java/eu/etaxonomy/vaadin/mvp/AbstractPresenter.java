package eu.etaxonomy.vaadin.mvp;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

import eu.etaxonomy.cdm.api.application.CdmRepository;

/**
 * AbstractPresenter is the base class of all presenter components. Presenter's
 * role is to govern the view and control the complex UI logic based on
 * notifications presenter receives from its view.
 *
 * @author Peter / Vaadin
 *
 * @param <V>
 *            type of the view this presenter governs
 */
@SpringComponent
@ViewScope
public abstract class AbstractPresenter<V extends ApplicationView> {

    public static final Logger logger = Logger.getLogger(AbstractPresenter.class);

	private V view;


	protected V getView() {
		return view;
	}

	@Autowired
	@Qualifier("cdmRepository")
	private CdmRepository repo;

	/**
	 * @return the repo
	 */
	public CdmRepository getRepo() {
	    return repo;
	}

	/**
	 * Notifies the presenter that its view is initialized so that presenter can
	 * start its own initialization if required.
	 *
	 * @param view
	 */
	protected final void init(V view) {
	    logger.trace("Presenter init");
		this.view = view;
		onPresenterReady();
	}

	/**
	 * Extending classes should overwrite this method in order to perform logic
	 * after presenter has finished initializing.
	 */
	protected void onPresenterReady() {
	    logger.trace("Presenter ready");
	}

	/**
	 * Extending classes should overwrite this method to react to the event when
	 * user has navigated into the view that this presenter governs.
	 */
	public void onViewEnter() {
	    logger.trace("View entered");
	}

	public void onViewExit() {

	}

}
