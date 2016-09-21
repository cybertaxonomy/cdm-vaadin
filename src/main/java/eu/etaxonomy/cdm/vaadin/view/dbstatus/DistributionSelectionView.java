package eu.etaxonomy.cdm.vaadin.view.dbstatus;

import java.sql.SQLException;
import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;

import eu.etaxonomy.cdm.model.common.DefinedTermBase;
import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.taxon.TaxonNode;
import eu.etaxonomy.cdm.vaadin.container.TaxonNodeContainer;

public class DistributionSelectionView extends CustomComponent implements IDistributionSelectionComponent, View, ClickListener{

    private VerticalLayout mainLayout;
    private Panel panel_1;
    private VerticalLayout verticalLayout_2;
    private Button button_proceed;
    private ComboBox distributionAreaBox;
    private ComboBox classificationBox;
    private Tree taxonTree;
    private Label label_1;

    private static final long serialVersionUID = 1L;
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
		TaxonNode taxonNode = (TaxonNode) taxonTree.getValue();
		if(taxonNode==null){
			taxonNode = (TaxonNode) classificationBox.getValue();
		}
		TermVocabulary<DefinedTermBase> term = (TermVocabulary<DefinedTermBase>)distributionAreaBox.getValue();
		try {
			distListener.buttonClick(taxonNode, term);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void dataBinding(){
		classificationBox.setItemCaptionPropertyId(TaxonNodeContainer.LABEL);
		classificationBox.setContainerDataSource(new TaxonNodeContainer(null));
		classificationBox.setImmediate(true);
		classificationBox.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				TaxonNode parentNode = (TaxonNode) event.getProperty().getValue();
				taxonTree.setContainerDataSource(new TaxonNodeContainer(parentNode));
			}
		});

        taxonTree.setItemCaptionPropertyId(TaxonNodeContainer.LABEL);
        
		List<TermVocabulary<DefinedTermBase>> namedAreaList = distListener.getNamedAreaList();
		Container d = new IndexedContainer(namedAreaList);
		distributionAreaBox.setContainerDataSource(d);
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

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
        label_1.setValue("Please choose a Classification and/or taxon and a distribution area to proceed.");
        verticalLayout_2.addComponent(label_1);

        // classificationBox
        classificationBox = new ComboBox();
        classificationBox.setCaption("Classification: ");
        classificationBox.setImmediate(false);
        classificationBox.setWidth("200px");
        classificationBox.setHeight("-1px");
        verticalLayout_2.addComponent(classificationBox);

        // distributionAreaBox
        distributionAreaBox = new ComboBox();
        distributionAreaBox.setCaption("Distribution Area");
        distributionAreaBox.setImmediate(false);
        distributionAreaBox.setWidth("200px");
        distributionAreaBox.setHeight("-1px");
        verticalLayout_2.addComponent(distributionAreaBox);

        // taxon tree
        taxonTree = new Tree("Taxonomy");
        taxonTree.setWidth("200px");
        verticalLayout_2.addComponent(taxonTree);

        // button_proceed
        button_proceed = new Button();
        button_proceed.setCaption("Proceed");
        button_proceed.setImmediate(true);
        button_proceed.setWidth("-1px");
        button_proceed.setHeight("-1px");
        verticalLayout_2.addComponent(button_proceed);
        verticalLayout_2.setComponentAlignment(button_proceed, new Alignment(10));

        return verticalLayout_2;
    }

}