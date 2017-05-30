package eu.etaxonomy.vaadin.mvp;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.TransactionStatus;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.vaadin.ui.navigation.NavigationManager;
import eu.etaxonomy.vaadin.ui.navigation.NavigationManagerBean;

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
public abstract class AbstractPresenter<V extends ApplicationView> implements Serializable {


    private static final long serialVersionUID = 5260910510283481832L;

    public static final Logger logger = Logger.getLogger(AbstractPresenter.class);

	private V view;


	protected V getView() {
	    if(view == null){
            Logger.getLogger(this.getClass()).warn("CDM-VAADIN#6562: presenter " + toString() + " without view.");
        }
		return view;
	}

	@Autowired
	@Qualifier("cdmRepository")
	private CdmRepository repo;

	@Autowired
	private NavigationManager navigationManager;

	/**
	 * @return the repo
	 */
	public CdmRepository getRepo() {
	    return repo;
	}

	/**
     * @return
     *
     * FIXME is it ok to use the SecurityContextHolder or do we need to hold the context in the vaadin session?
     */
    protected SecurityContext currentSecurityContext() {
        return SecurityContextHolder.getContext();
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

	public final void onViewEnter() {
	    logger.trace("View entered");
	    TransactionStatus tx = getRepo().startTransaction();
	    handleViewEntered();
	    getRepo().commitTransaction(tx);
	}

	public final void onViewExit() {
	    handleViewExit();
	}

	/**
	 * Extending classes should overwrite this method to react to the event when
	 * user has navigated into the view that this presenter governs.
	 * For implementations of {@link AbstractPopupEditor AbstractPopupEditors} this is usually
	 * called before the data item has been bound. This order is guaranteed since popup editors
	 * are managed through the {@link NavigationManagerBean}
	 */
	public void handleViewEntered() {
	}

    /**
     * Extending classes may overwrite this method to react to
     * the event when user leaves the view that this presenter
     * governs.
     */
    public void handleViewExit() {
    }

    /**
     * @return the navigationManager
     */
    public NavigationManager getNavigationManager() {
        return navigationManager;
    }

    /**
     * @param repo the repo to set
     */
    protected void setRepo(CdmRepository repo) {
        this.repo = repo;
    }

    /**
     * @param navigationManager the navigationManager to set
     */
    protected void setNavigationManager(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
    }



}
