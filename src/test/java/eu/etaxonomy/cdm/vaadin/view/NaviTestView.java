package eu.etaxonomy.cdm.vaadin.view;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import eu.etaxonomy.cdm.vaadin.ui.NavigatorTestUI;

public class NaviTestView extends CustomComponent implements View {

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	@AutoGenerated
	private VerticalLayout mainLayout;
	@AutoGenerated
	private Panel panel;
	@AutoGenerated
	private VerticalLayout panelVLayout;
	@AutoGenerated
	private Button button;
	@AutoGenerated
	private TextArea textArea;
	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	public NaviTestView() {
		buildMainLayout();
		setCompositionRoot(mainLayout);		
		button.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				UI.getCurrent().getNavigator().navigateTo(NavigatorTestUI.SECOND_VIEW);
				
			}
			
		});
		button.setClickShortcut(KeyCode.ENTER, null);
	}

	public void setText(String text) {
		textArea.setValue(text);
	}
	
	public void removeButton() {
		panelVLayout.removeComponent(button);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	@AutoGenerated
	private VerticalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// panel
		panel = buildPanel();
		mainLayout.addComponent(panel);
		mainLayout.setComponentAlignment(panel, new Alignment(48));
		
		return mainLayout;
	}

	@AutoGenerated
	private Panel buildPanel() {
		// common part: create layout
		panel = new Panel();
		panel.setImmediate(false);
		panel.setWidth("-1px");
		panel.setHeight("-1px");
		
		// panelVLayout
		panelVLayout = buildPanelVLayout();
		panel.setContent(panelVLayout);
		
		return panel;
	}

	@AutoGenerated
	private VerticalLayout buildPanelVLayout() {
		// common part: create layout
		panelVLayout = new VerticalLayout();
		panelVLayout.setImmediate(false);
		panelVLayout.setWidth("-1px");
		panelVLayout.setHeight("-1px");
		panelVLayout.setMargin(true);
		panelVLayout.setSpacing(true);
		
		// textArea
		textArea = new TextArea();
		textArea.setImmediate(false);
		textArea.setWidth("214px");
		textArea.setHeight("-1px");
		panelVLayout.addComponent(textArea);
		panelVLayout.setComponentAlignment(textArea, new Alignment(48));
		
		// button
		button = new Button();
		button.setCaption("Click Me to navigate to next view");
		button.setImmediate(true);
		button.setWidth("-1px");
		button.setHeight("-1px");
		panelVLayout.addComponent(button);
		panelVLayout.setComponentAlignment(button, new Alignment(48));
		
		return panelVLayout;
	}

}