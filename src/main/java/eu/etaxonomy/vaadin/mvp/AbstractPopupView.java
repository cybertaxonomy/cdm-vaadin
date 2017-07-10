package eu.etaxonomy.vaadin.mvp;

import com.vaadin.ui.Window;

import eu.etaxonomy.vaadin.ui.view.PopupView;


/**
 * @author a.kohlbecker
 * @since Apr 5, 2017
 *
 * @param <P>
 */
@SuppressWarnings("serial")
public abstract class AbstractPopupView<P extends AbstractPresenter> extends AbstractView<P> implements PopupView  {

	private Window window;

	protected void updateWindowCaption(String caption) {
	    window.setCaption(caption);
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isResizable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getWindowPixelWidth() {
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isModal() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWindowCaptionAsHtml() {
        return false;
    }

    @Override
    public void viewEntered(){
        getPresenter().onViewEnter();
    }

	/* Methods which existed in the original version of the AbstractPopupView
	 * There are not needed here since the creation of the window and disposal
	 * is done by the NavigationManagerBean
	@Override
	protected void onViewReady() {
		super.onViewReady();

		window = new Window();
		window.setWidth(getWindowPixelWidth(), Unit.PIXELS);
		window.setModal(isModal());
		window.setContent(this);

		UI.getCurrent().addWindow(window);
	}

	public void close() {
	    window.close();
	}
	*/




}
