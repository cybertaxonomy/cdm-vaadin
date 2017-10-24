package eu.etaxonomy.vaadin.mvp;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.vaadin.server.ServletPortletHelper;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.vaadin.server.CdmSpringVaadinServletService;
import eu.etaxonomy.cdm.vaadin.server.RequestStartListener;
import eu.etaxonomy.cdm.vaadin.session.IntraViewConversationDirector;
import eu.etaxonomy.cdm.vaadin.session.ViewScopeConversationHolder;
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
public abstract class AbstractPresenter<V extends ApplicationView> implements Serializable, IntraViewConversationDirector, RequestStartListener {


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

	private ViewScopeConversationHolder conversationHolder;

	protected DefaultTransactionDefinition definition = null;

    protected boolean conversationBound;

	@Autowired
	private void setConversationHolder(ViewScopeConversationHolder conversationHolder){
	    this.conversationHolder = conversationHolder;
	    this.conversationHolder.setDefinition(getTransactionDefinition());
	}

	protected TransactionDefinition getTransactionDefinition(){
	    if(definition == null){
    	    definition = new DefaultTransactionDefinition();
    	    definition.setReadOnly(true);
	    }
	    return definition;
	}




	/**
	 * @return the repo
	 */
	public CdmRepository getRepo() {
	    if(!conversationBound){
	        // this is the central access point for getting access to the service layer.
	        // In case the presenter needs access to the repository, it most probably will use
	        // a service, so it is a good idea to bind the conversation at this point.
	        bindConversation();
	    }
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
     * @return
     */
    protected Session getSession() {
        Session session = conversationHolder.getSession();
        logger.trace(this._toString() + ".getSession() - session:" + session.hashCode() +", persistenceContext: " + ((SessionImplementor)session).getPersistenceContext() + " - " + session.toString());
        return session;
    }

    protected String _toString(){
        return this.getClass().getSimpleName() + "@" + this.hashCode();
    }

	/**
	 * Notifies the presenter that its view is initialized so that presenter can
	 * start its own initialization if required.
	 *
	 * @param view
	 */
	protected final void init(V view) {
	    logger.trace(String.format("Presenter %s init()", _toString()));
		this.view = view;
		// bind the conversation to the thread of the first request send to the according View
		// all other requests are handled in onRequestStart()
		// logger.trace(String.format(">>>>> %s init() bind()", _toString()));
	    ensureBoundConversation();
	    // register as request start and end listener
	    VaadinService service = UI.getCurrent().getSession().getService();
	    if(service instanceof CdmSpringVaadinServletService){
	        logger.trace(String.format("~~~~~ %s register as request listener", _toString()));
	        ((CdmSpringVaadinServletService)service).addRequestEndListener(this);
	        if(logger.isTraceEnabled()){
	            ((CdmSpringVaadinServletService)service).addRequestStartListener(this);
	        }
	    } else {
	        throw new RuntimeException("Using the CdmSpringVaadinServletService is required for proper per view conversation handling");
	    }
		onPresenterReady();
	}

	/**
	 * Returns <code>true</code> for:
	 * <ul>
	 *   <li>..</li>
	 * <ul>
	 *
	 * Return <code>false</code> for:
	 *
	 * <ul>
     *   <li>UILD request in a existing view, like clicking on a button</li>
     * <ul>
     *
	 * @return
	protected boolean isActiveView(){
        return UI.getCurrent() != null && getView() != null && getView() == navigationManager.getCurrentView();
    }
	 */

    /**
     *
     */
	protected void bindConversation() {
        logger.trace(String.format(">>>>> %s bind()", _toString()));
        conversationHolder.bind();
        conversationBound = true;
    }

    @Override
    public void ensureBoundConversation() {
        if(!conversationBound){
            bindConversation();
        }
        if(!conversationHolder.isTransactionActive()){
            logger.trace(String.format(">>   %s starting transaction ", _toString()));
            conversationHolder.startTransaction();
        }
    }

    /**
     *
     */
    protected void unbindConversation() {
        logger.trace(String.format("<<<<< %s unbind()", _toString()));
        conversationHolder.unbind();
        // FIXME conversationHolder.isTransactionActive() always returns true
        // see https://dev.e-taxonomy.eu/redmine/issues/6780
        if(false && conversationHolder.isTransactionActive()){
            logger.trace(String.format("<<    %s comitting transaction ", _toString()));
            try{
                conversationHolder.commit(false);
            } catch (IllegalTransactionStateException | IllegalStateException e){
                // log this exception, but stop from propagating
                // FIXME remove this catch once https://dev.e-taxonomy.eu/redmine/issues/6780 is fixed
                logger.error(e.getMessage());
            }
        }
        conversationBound = false;
    }

    /**
	 * Extending classes should overwrite this method in order to perform logic
	 * after presenter has finished initializing.
	 *
	 * At this point in the life cycle of the MVP the {@link AbstractView#initContent() initContent()}
	 * method has been executed already.
	 */
	protected void onPresenterReady() {
	    logger.trace(String.format("Presenter %s ready", _toString()));
	}

    /**
     * <b>ONLY USED FOR LOGGING</b> when Level==TRACE
     * {@inheritDoc}
     */
    @Override
    public void onRequestStart(VaadinRequest request){

        if( ! requestNeedsConversation(request) ){
            // ignore hartbeat, fileupload, push etc
            logger.trace("ignoring request:" + request.getPathInfo());
            return;
        }
        logger.trace("onRequestStart() " + request.getPathInfo() + " " + _toString());
    }

    /**
     * @param request
     * @return
     */
    protected boolean requestNeedsConversation(VaadinRequest request) {
        return !(
                ServletPortletHelper.isAppRequest(request) // includes published file request
             || ServletPortletHelper.isFileUploadRequest(request)
             || ServletPortletHelper.isHeartbeatRequest(request)
             || ServletPortletHelper.isPushRequest(request)
             );
    }

    @Override
    public void onRequestEnd(VaadinRequest request, VaadinSession session){

        if( ! requestNeedsConversation(request) ){
            // ignore hartbeat, fileupload, push etc
            logger.trace("ignoring request:" + request.getPathInfo());
            return;
        }

        // always unbind at the end of a request to clean up the threadLocal variables in the
        // TransactionManager. This is crucial since applications containers manage threads in a pool
        // and the recycled threads may still have a reference to a SessionHolder from the processing
        // of a former request
        logger.trace("onRequestEnd() " + request.getPathInfo() + " " + _toString());
        if(conversationBound){
            unbindConversation();
        }
    }

    public final void onViewEnter() {
	    logger.trace(String.format("%s onViewEnter()", _toString()));
	    handleViewEntered();
	}

	public final void onViewExit() {
	    logger.trace(String.format("%s onViewExit()", _toString()));
	    handleViewExit();
	    // un-register as request start and end listener
	    if(conversationBound){
    	    logger.trace(String.format("<<<<< %s onViewExit() unbind()", _toString()));
            conversationHolder.unbind();
            conversationBound = false;
	    }
	    logger.trace(String.format("<<<<< %s onViewExit() close()", _toString()));
	    conversationHolder.close();
        VaadinService service = UI.getCurrent().getSession().getService();
        if(service instanceof CdmSpringVaadinServletService){
            logger.trace(String.format("~~~~~ %s un-register as request listener", _toString()));
            ((CdmSpringVaadinServletService)service).removeRequestEndListener(this);
            if(logger.isTraceEnabled()){
                ((CdmSpringVaadinServletService)service).removeRequestStartListener(this);
            }
        } else {
            throw new RuntimeException("Using the CdmSpringVaadinServletService is required for proper per view conversation handling");
        }
        conversationHolder = null;
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
     * governs. This method is executed before un-binding and closing the
     * conversation holder.
     */
    public void handleViewExit() {
    }

    /**
     * @return the navigationManager
     */
    public NavigationManager getNavigationManager() {
        return navigationManager;
    }

    protected ViewScopeConversationHolder getConversationHolder(){
        return conversationHolder;
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
