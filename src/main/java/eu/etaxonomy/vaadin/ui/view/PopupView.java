package eu.etaxonomy.vaadin.ui.view;

import com.vaadin.ui.Window;

import eu.etaxonomy.vaadin.ui.CanCastComponent;
import eu.etaxonomy.vaadin.ui.navigation.NavigationManagerBean;

/**
 * This interface defines the api used by the {@link NavigationManagerBean} to
 * show popup view in a {@link Window} with caption etc.
 *
 * @author a.kohlbecker
 * @since Apr 5, 2017
 *
 */
public interface PopupView extends CanCastComponent {

    String getWindowCaption();

    boolean isResizable();

    /**
     * @return a positive number to define the initial with of the windows.
     * A negative number implies <b>unspecified size</b> (terminal is free
     * to set the size).
     */
    int getWindowPixelWidth();

    boolean isModal();

    /**
     * The initial width of the window. A value of <code>-1</code>
     * means undefined.
     *
     * @return
     */
    boolean isWindowCaptionAsHtml();

    /**
     * Is called once the popView is displayed in the UI
     */
    void viewEntered();

    /**
     * Can be implemented by editor views to set the focus on a specific form
     * item.
     */
    void focusFirst();

    /**
     * Cancel any operation in progress and close the view.
     */
    public void cancel();


}
