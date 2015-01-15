package eu.etaxonomy.cdm.vaadin.view.dbstatus;

import java.util.List;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import eu.etaxonomy.cdm.model.common.DefinedTermBase;
import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.taxon.Classification;

public class DistributionSelectionView extends CustomComponent implements IDistributionSelectionComponent, View, ClickListener{

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	@AutoGenerated
	private VerticalLayout mainLayout;
	@AutoGenerated
	private Panel panel_1;
	@AutoGenerated
	private VerticalLayout verticalLayout_2;
	@AutoGenerated
	private Button button_proceed;
	@AutoGenerated
	private Accordion accordion;
	@AutoGenerated
	private OptionGroup distributionSelection;


    @AutoGenerated
	private OptionGroup classificationSelection;
	@AutoGenerated
	private Label label_1;
	private DistributionSelectionComponentListener distListener;
	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	public DistributionSelectionView(){
		buildMainLayout();
		setCompositionRoot(mainLayout);
		setStyleName("login");
		button_proceed.addClickListener(this);
		button_proceed.setClickShortcut(KeyCode.ENTER, null);
	}


	@Override
	public void addListener(DistributionSelectionComponentListener listener) {
		distListener = listener;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		if(classificationSelection != null && distributionSelection != null){
			Classification classification = (Classification) classificationSelection.getValue();
			TermVocabulary<DefinedTermBase> term = (TermVocabulary<DefinedTermBase>)distributionSelection.getValue();
			distListener.buttonClick(classification, term);
		}
	}

	public void dataBinding(){
		List<Classification> classificationList = distListener.getClassificationList();
		List<TermVocabulary<DefinedTermBase>> namedAreaList = distListener.getNamedAreaList();
		Container c = new IndexedContainer(classificationList);
		classificationSelection.setContainerDataSource(c);
		Container d = new IndexedContainer(namedAreaList);
		distributionSelection.setContainerDataSource(d);
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

	@AutoGenerated
	private VerticalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");

		// panel_1
		panel_1 = buildPanel_1();
		mainLayout.addComponent(panel_1);
		mainLayout.setComponentAlignment(panel_1, new Alignment(48));

		return mainLayout;
	}

	@AutoGenerated
	private Panel buildPanel_1() {
		// common part: create layout
		panel_1 = new Panel();
		panel_1.setImmediate(false);
		panel_1.setWidth("-1px");
		panel_1.setHeight("-1px");

		// verticalLayout_2
		verticalLayout_2 = buildVerticalLayout_2();
		panel_1.setContent(verticalLayout_2);

		return panel_1;
	}

	@AutoGenerated
	private VerticalLayout buildVerticalLayout_2() {
		// common part: create layout
		verticalLayout_2 = new VerticalLayout();
		verticalLayout_2.setImmediate(false);
		verticalLayout_2.setWidth("-1px");
		verticalLayout_2.setHeight("-1px");
		verticalLayout_2.setMargin(true);
		verticalLayout_2.setSpacing(true);

		// label_1
		label_1 = new Label();
		label_1.setImmediate(false);
		label_1.setWidth("213px");
		label_1.setHeight("-1px");
		label_1.setValue("Please choose a Classification and a distribution area to proceed.");
		verticalLayout_2.addComponent(label_1);

		// accordion
		accordion = buildAccordion();
		verticalLayout_2.addComponent(accordion);
		verticalLayout_2.setComponentAlignment(accordion, new Alignment(48));

		// button_1
		button_proceed = new Button();
		button_proceed.setCaption("Proceed");
		button_proceed.setImmediate(true);
		button_proceed.setWidth("-1px");
		button_proceed.setHeight("-1px");
		verticalLayout_2.addComponent(button_proceed);
		verticalLayout_2.setComponentAlignment(button_proceed, new Alignment(10));

		return verticalLayout_2;
	}

	@AutoGenerated
	private Accordion buildAccordion() {
		// common part: create layout
		accordion = new Accordion();
		accordion.setImmediate(true);
		accordion.setWidth("-1px");
		accordion.setHeight("-1px");

		// classificationSelection
		classificationSelection = new OptionGroup();
		classificationSelection.setImmediate(false);
		classificationSelection.setWidth("-1px");
		classificationSelection.setHeight("-1px");
		accordion.addTab(classificationSelection, "Classifcation", null);

		// distributionSelection
		distributionSelection = new OptionGroup();
		distributionSelection.setImmediate(false);
		distributionSelection.setWidth("-1px");
		distributionSelection.setHeight("-1px");
		accordion.addTab(distributionSelection, "Distribution Area", null);

		return accordion;
	}

}
