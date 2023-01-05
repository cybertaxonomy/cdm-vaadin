/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.mvp;

import java.io.Serializable;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.spring.events.EventBus;

import com.vaadin.ui.Field;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction;
import eu.etaxonomy.cdm.vaadin.event.EditorActionContext;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent;
import eu.etaxonomy.vaadin.ui.navigation.NavigationManager;
import eu.etaxonomy.vaadin.ui.navigation.NavigationManagerBean;
import eu.etaxonomy.vaadin.ui.view.DoneWithPopupEvent;
import eu.etaxonomy.vaadin.ui.view.PopupView;

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
public abstract class AbstractPresenter<V extends ApplicationView> implements Serializable, DisposableBean {

    private static final Logger logger = LogManager.getLogger();

    private static final long serialVersionUID = 5260910510283481832L;

	private V view;

	protected V getView() {
	    if(view == null){
            logger.warn("CDM-VAADIN#6562: presenter " + toString() + " without view.");
        }
		return view;
	}

	@Autowired
	@Qualifier("cdmRepository")
	private CdmRepository repo;

	@Autowired
	private NavigationManager navigationManager;

    protected EventBus.ViewEventBus viewEventBus;

    @Autowired
    protected void setViewEventBus(EventBus.ViewEventBus viewEventBus){
        this.viewEventBus = viewEventBus;
        eventViewBusSubscription(viewEventBus);
    }

    /**
     * Override if needed, e.g. to skip subscription
     *
     * @param viewEventBus
     */
    protected void eventViewBusSubscription(EventBus.ViewEventBus viewEventBus){
            viewEventBus.subscribe(this);
    }

    public void unsubscribeFromEventBuses(){
        viewEventBus.unsubscribe(this);
    }


	//	protected DefaultTransactionDefinition definition = null;

    //	protected TransactionDefinition getTransactionDefinition(){
    //	    if(definition == null){
    //    	    definition = new DefaultTransactionDefinition();
    //    	    definition.setReadOnly(true);
    //	    }
    //	    return definition;
    //	}


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


    protected String _toString(){
        return this.getClass().getSimpleName() + "@" + this.hashCode();
    }

	/**
	 * Notifies the presenter that its view is initialized so that presenter can
	 * start its own initialization if required.
	 *
	 * @param view
	 */
	protected void init(V view) {
	    logger.trace(String.format("Presenter %s init()", _toString()));
		this.view = view;
		onPresenterReady();
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

    public final void onViewEnter() {
	    logger.trace(String.format("%s onViewEnter()", _toString()));
	    handleViewEntered();
	}

	public void onViewExit() {
	    logger.trace(String.format("%s onViewExit()", _toString()));
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
        // fist of all clear the session to wipe out any left overs from previous usage of the session
        // this can happen if not all operations within a session are cleaned up correctly in
        // turn of an exception
	        getRepo().clearSession();
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

    protected boolean checkFromOwnView(AbstractEditorAction event) {
        return getView() != null && getView() == event.getSourceView();
    }

    /**
     * Opens a new PopView or editor of the type <code>popupViewClass</code> for the current view.
     * If the <code>event</code> is not null and if it contains a target field the newly created editor
     * will be registered with this field as target. Once the popup editor is being saved a
     * {@link eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent} will be emitted which will hold a
     * reference to the popup editor in the {@link eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent#getSourceView() sourceView}
     * property. By this reference the target field can be retrieved in editor presenters via the method
     * {@link AbstractEditorPresenter#boundTargetField(PopupView)}:
     * <p>
     * {@code
     *   BoundField targetField = boundTargetField(entityChangeEvent.getSourceView())
     * }
     * </p>
     * In case the target field is bound to a bean property the propertyId is available in the {@link BoundField}
     * object and can be used to decide on which bean property to update with the data saved in the popup editor or to
     * act in any other appropriate way.
     *
     * @param popupViewClass
     * @param event
     * @return
     */
    protected <T extends PopupView> T openPopupEditor(Class<T> popupViewClass, AbstractEditorAction<?> event) {
        Field<?> targetField = event != null? event.getTarget(): null;
        return getNavigationManager().showInPopup(popupViewClass, getView(), targetField);
    }

    protected boolean isFromOwnView(EntityChangeEvent event) {
        return event.getSourceView() != null && event.getSourceView().equals(getView());
    }

    public EditorActionContext editorActionContextRoot(PopupView popupView) {
        Stack<EditorActionContext>context = ((AbstractPopupEditor)popupView).getEditorActionContext();
        return context.get(0);
    }

    public boolean isAtContextRoot(PopupView popupView) {
        AbstractPopupEditor popupEditor = ((AbstractPopupEditor)popupView);
        if(popupEditor.getEditorActionContext().size() > 1){
            EditorActionContext topContext = (EditorActionContext) popupEditor.getEditorActionContext().get(popupEditor.getEditorActionContext().size() - 2);
            return getView().equals(topContext.getParentView());
        } else {
            logger.error("Invalid EditorActionContext size. A popupeditor should at leaset have the workingset as root");
            return false;
        }
    }

    /**
     * @param event
     */
    public boolean isFromOwnView(DoneWithPopupEvent event) {
        EditorActionContext contextRoot = editorActionContextRoot(event.getPopup());
        return getView().equals(contextRoot.getParentView());
    }


    @Override
    public void destroy() throws Exception {
        unsubscribeFromEventBuses();
        view = null;
    }

}
