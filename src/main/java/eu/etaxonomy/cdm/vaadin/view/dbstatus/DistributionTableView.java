package eu.etaxonomy.cdm.vaadin.view.dbstatus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.PopupView.PopupVisibilityEvent;
import com.vaadin.ui.PopupView.PopupVisibilityListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.Window;

import eu.etaxonomy.cdm.api.conversation.ConversationHolder;
import eu.etaxonomy.cdm.model.description.DescriptionElementBase;
import eu.etaxonomy.cdm.model.description.Feature;
import eu.etaxonomy.cdm.model.description.PresenceAbsenceTerm;
import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.model.taxon.TaxonBase;
import eu.etaxonomy.cdm.vaadin.component.DetailWindow;
import eu.etaxonomy.cdm.vaadin.component.HorizontalToolbar;
import eu.etaxonomy.cdm.vaadin.container.CdmSQLContainer;

public class DistributionTableView<E> extends CustomComponent implements IDistributionTableComponent, View{

    private static final long serialVersionUID = 1L;
    @AutoGenerated
	private AbsoluteLayout mainLayout;
    private HorizontalToolbar toolbar;
	@AutoGenerated
	private Table table;

	private Taxon currentTaxon;

	private ArrayList<Object> propertyList = new ArrayList<Object>();

	private DistributionTableComponentListener listener;

	protected List<Field> fields = new ArrayList<Field>();

	List<String> columnList;
	ArrayList<String> headerList;

	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	public DistributionTableView() {
		buildMainLayout();
		setCompositionRoot(mainLayout);
		createEditClickListener();

	}

	@Override
	public void addListener(DistributionTableComponentListener listener) {
	   this.listener = listener;
	}

	@AutoGenerated
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");

		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		//Horizontal Toolbar
		toolbar = new HorizontalToolbar();
		mainLayout.addComponent(toolbar, "top:0.0px;right:0.0px;");

		// table_1
		table = new Table();
		table.setImmediate(false);
		table.setWidth("100.0%");
		table.setHeight("100.0%");
		mainLayout.addComponent(table, "top:75px;right:0.0px;");

		return mainLayout;
	}

    /* (non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }



	public void dataBinding() throws SQLException{
		CdmSQLContainer container = listener.getSQLContainer();

		table.setContainerDataSource(container);
		table.setColumnReorderingAllowed(true);
		table.setSortEnabled(true);

		columnList = new ArrayList<String>(Arrays.asList(new String[]{"Taxon","Rank"}));
		columnList.addAll(listener.getTermList());
		Object[] visibleColumns = columnList.toArray();
		table.setVisibleColumns(visibleColumns);

		headerList = new ArrayList<String>(Arrays.asList(new String[]{"Taxon","Rang"}));
		headerList.addAll(listener.getAbbreviatedTermList());
//		table.setColumnHeaders(headerList.toArray(new String[headerList.size()]));//new String[]{"Taxon", "Rang"});// ,"Deutschland"

		table.setColumnCollapsingAllowed(true);
		table.setSelectable(true);
		table.setPageLength(20);
		table.setFooterVisible(true);
		table.setColumnFooter("Taxon", "Total amount of Taxa displayed: " + container.size());

		table.setCacheRate(20);
	}


	private void createEditClickListener(){
		Button detailButton = toolbar.getDetailButton();
		detailButton.setCaption("Detail View");
		detailButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				try{
					HashSet<Feature> featureSet = new HashSet<Feature>(Arrays.asList(Feature.DESCRIPTION(), Feature.DISTRIBUTION()));
				if(currentTaxon != null){
					List<DescriptionElementBase> listDescriptions = listener.listDescriptionElementsForTaxon(currentTaxon, null);
					DetailWindow dw = new DetailWindow(currentTaxon, listDescriptions);
					Window window = dw.createWindow();
					getUI().addWindow(window);
				}else{
					Notification.show("Please select a Taxon.", Notification.Type.HUMANIZED_MESSAGE);
				}
				}catch(Exception e){
					Notification.show("Unexpected Error, \n\n Please log in again!", Notification.Type.WARNING_MESSAGE);
				}
			}
		});

		Button settingsButton = toolbar.getSettingsButton();
		settingsButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                SettingsConfigWindow cw = new SettingsConfigWindow();
                Window window  = cw.createWindow();
                getUI().addWindow(window);
            }
        });

		Button saveButton = toolbar.getSaveButton();
		saveButton.setClickShortcut(KeyCode.S, ModifierKey.CTRL);
		saveButton.setDescription("Shortcut: CTRL+S");
		saveButton.setCaption("Save Data");
		saveButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				ConversationHolder conversationHolder = (ConversationHolder) VaadinSession.getCurrent().getAttribute("conversation");
				try{
					conversationHolder.commit();
				}catch(Exception stateException){
					//TODO create Table without DTO
				}
				if(propertyList != null){
					for(Object propertyId:propertyList){
						table.removeGeneratedColumn(propertyId);
					}
					redrawTable();
				}
				Notification.show("Data saved", Notification.Type.HUMANIZED_MESSAGE);
				propertyList = null;
				propertyList = new ArrayList<Object>();
				table.setEditable(false);
				toolbar.getSaveButton().setCaption("Save Data");
			}
		});


		//FIXME: Due lack of time needs to be properly done

//		Button editButton = toolbar.getEditButton();
//		editButton.setClickShortcut(KeyCode.E, ModifierKey.CTRL);
//		editButton.setDescription("Shortcut: CTRL+e");
//		editButton.addClickListener(new ClickListener() {
//			private static final long serialVersionUID = 1L;
//			@Override
//			public void buttonClick(ClickEvent event) {
//			    //				if(table.isEditable() == false){
//			    //					table.setEditable(true);
//			    for(Object prop:table.getContainerPropertyIds()){
//			            if(!prop.equals("Taxon")&&!prop.equals("Rank")){
//			                table.addGeneratedColumn(prop, createTableColumnGenerator());
//			            }
//			    }
////				}//else{
////					table.setEditable(false);
////					table.refreshRowCache();
////				}
//			}
//		});

		/**Double Click listener for Table*/
		table.addItemClickListener(new ItemClickListener() {
		    private static final long serialVersionUID = 1L;

		    @Override
		    public void itemClick(ItemClickEvent event) {
		        TaxonBase taxonBase = loadTaxonFromSelection(event);
		        currentTaxon = (Taxon)taxonBase;
		        if(event.isDoubleClick()){
		            if(!(event.getPropertyId().toString().equalsIgnoreCase("Taxon")) && !(event.getPropertyId().toString().equalsIgnoreCase("Rank"))){
		                if(!table.removeGeneratedColumn(event.getPropertyId())){
		                    table.addGeneratedColumn(event.getPropertyId(), createTableColumnGenerator());
		                    propertyList.add(event.getPropertyId());
		                }else{
		                    table.removeGeneratedColumn(event.getPropertyId());
		                    propertyList.remove(event.getPropertyId());
		                    redrawTable();
		                }
		            }
		        }
		    }
		});
	}

	private TaxonBase loadTaxonFromSelection(ItemClickEvent event) {
	    Item item = event.getItem();
	    Property itemProperty = item.getItemProperty("uuid");
	    UUID uuid = UUID.fromString(itemProperty.getValue().toString());
	    TaxonBase taxonBase = listener.getTaxonService().load(uuid);
	    return taxonBase;
	}

	private String refreshValue(ComboBox box, Object value){
	    if(box.getValue() == null){
	        if(value != null){
	            return value.toString();
	        }else{
	            return "click me for new Distribution Status";
	        }
	    }else{
	        return box.getValue().toString();
	    }
	}

	private ColumnGenerator createTableColumnGenerator(){

	    ColumnGenerator generator = new ColumnGenerator() {

	        private static final long serialVersionUID = 1L;

	        @Override
	        public Object generateCell(Table source, Object itemId, Object columnId) {
	            Property containerProperty = source.getContainerProperty(itemId, columnId);
	            Object item = itemId;
	            Object value = null;
	            if(containerProperty != null){
	                value = containerProperty.getValue();
	            }
	            Container containerDataSource = source.getContainerDataSource();
	            final UUID uuid = UUID.fromString(table.getItem(itemId).getItemProperty("uuid").getValue().toString());
	            final ComboBox box = new ComboBox("Occurrence Status: ", listener.getPresenceAbsenceContainer());
	            final String area = columnId.toString();
	            box.setImmediate(true);
	            final Object value1 = value;
	            box.addValueChangeListener(new ValueChangeListener() {
	                @Override
	                public void valueChange(ValueChangeEvent event) {
	                    Taxon taxon = (Taxon)listener.getTaxonService().load(uuid);

	                    if(value1==null){
	                        listener.createDistributionField(taxon, box.getValue(), area);
	                        Notification.show("Create Status", Notification.Type.TRAY_NOTIFICATION);
	                    }else{
	                        int result = listener.updateDistributionField(area, box.getValue(), taxon);
	                        if(result == 1){
	                            Notification.show("Delete Status", Notification.Type.TRAY_NOTIFICATION);
	                        }else if(result == 0){
	                            Notification.show("DescriptionService wrote", Notification.Type.TRAY_NOTIFICATION);
	                        }
	                    }
	                }
	            });
	            final PopupView popup = new PopupView(new PopupView.Content() {
	                private static final long serialVersionUID = 1L;
	                @Override
	                public String getMinimizedValueAsHTML() {
	                    return refreshValue(box, value1);
	                }
	                @Override
	                public Component getPopupComponent() {
	                    //FIXME: find a better solution
	                    box.setValue(compareObjectToPAT(value1));
	                    box.setBuffered(true);
	                    return box;
	                }
	            });
	            popup.addPopupVisibilityListener(new PopupVisibilityListener() {

	                @Override
	                public void popupVisibilityChange(PopupVisibilityEvent event) {

	                }
	            });
	            popup.setHideOnMouseOut(true);
	            return popup;
	        }
	    };
	    return generator;
	}

	private PresenceAbsenceTerm compareObjectToPAT(Object object){
	    List<PresenceAbsenceTerm> presenceAbsenceTerms = listener.getPresenceAbsenceTerms();
	    for(PresenceAbsenceTerm term:presenceAbsenceTerms){
	        if(term.getTitleCache().equals(object)){
	            return term;
	        }
	    }
	    return null;
	}

	private void redrawTable(){
	    try {
	        CdmSQLContainer sqlContainer = listener.getSQLContainer();
	        sqlContainer.refresh();
	        table.setContainerDataSource(sqlContainer);
	        table.setVisibleColumns(columnList.toArray());
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


}
