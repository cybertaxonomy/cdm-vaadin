/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
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
public abstract class AbstractPopupView<P extends AbstractPresenter>
        extends AbstractView<P>
        implements PopupView  {

	private Window window;

	protected void updateWindowCaption(String caption) {
	    window.setCaption(caption);
	}

    @Override
    public boolean isResizable() {
        return false;
    }

    @Override
    public boolean isClosable(){
        return false;
    }

    @Override
    public int getWindowWidth() {
        return -1;
    }

    @Override
    public Unit getWindowWidthUnit() {
        return Unit.PIXELS;
    }

    /**
     * {@inheritDoc}
     *
     *  <p>
     *  <b>NOTE:</b> setting 100% as default height. If the height
     *  would be undefined the window, will fit the size of
     *  the content and will sometimes exceed the height of the
     *  main window and will not get a scroll bar in this situation.
     *  see #6843
     *  </p>
     */
    @Override
    public int getWindowHeight() {
        return 100;
    }

    @Override
    public Unit getWindowHeightUnit() {
        return Unit.PERCENTAGE;
    }

    @Override
    public boolean isModal() {
        return false;
    }

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