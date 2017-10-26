/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.distributionStatus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import eu.etaxonomy.cdm.hibernate.HibernateProxyHelper;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.common.Language;
import eu.etaxonomy.cdm.model.common.Representation;
import eu.etaxonomy.cdm.model.description.DescriptionElementBase;
import eu.etaxonomy.cdm.model.description.PresenceAbsenceTerm;
import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.vaadin.component.DetailWindow;
import eu.etaxonomy.cdm.vaadin.component.DistributionToolbar;
import eu.etaxonomy.cdm.vaadin.container.CdmSQLContainer;
import eu.etaxonomy.cdm.vaadin.security.AccessRestrictedView;
import eu.etaxonomy.cdm.vaadin.util.CdmQueryFactory;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.util.DistributionEditorUtil;
import eu.etaxonomy.cdm.vaadin.util.TermCacher;
import eu.etaxonomy.cdm.vaadin.view.AbstractPageView;

/**
 * @author freimeier
 * @since 18.10.2017
 *
 */
@SpringView(name=DistributionTableViewBean.NAME)
public class DistributionTableViewBean
            extends AbstractPageView<DistributionTablePresenter>
            implements IDistributionTableView, AccessRestrictedView {

	private static final long serialVersionUID = 1L;
    public static final String NAME = "distTable";

    @Autowired
    private DistributionToolbar toolbar;

	private Table table;
	private Grid grid;

    private CdmSQLContainer container;
	private AreaAndTaxonSettingsConfigWindow areaAndTaxonConfigWindow;;
	private DistributionStatusSettingsConfigWindow distributionStatusConfigWindow;

	public DistributionTableViewBean() {
		super();
	}

	private AbsoluteLayout initLayout() {
		AbsoluteLayout mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");

		setWidth("100.0%");
		setHeight("100.0%");

		//Horizontal Toolbar
		mainLayout.addComponent(toolbar, "top:0.0px;right:0.0px;");

		// table + formatting
		table = new Table(){
			private static final long serialVersionUID = -5148756917468804385L;

			@Override
			protected String formatPropertyValue(Object rowId, Object colId, Property<?> property) {
				String formattedValue = null;
				PresenceAbsenceTerm presenceAbsenceTerm = null;
				Object value = property.getValue();
				if(value instanceof String){
					presenceAbsenceTerm = TermCacher.getInstance().getPresenceAbsenceTerm((String) value);
				}
				if(presenceAbsenceTerm != null){
					Representation representation = presenceAbsenceTerm.getRepresentation(Language.DEFAULT());
					if(representation!=null){
						if(DistributionEditorUtil.isAbbreviatedLabels()){
							formattedValue = representation.getAbbreviatedLabel();
						}
						else{
							formattedValue = representation.getLabel();
						}
					}
					if(formattedValue==null){
						formattedValue = presenceAbsenceTerm.getTitleCache();
					}
					return formattedValue;
				}
				return super.formatPropertyValue(rowId, colId, property);
			}
		};
		table.setImmediate(false);
		table.setWidth("100.0%");
		table.setHeight("100.0%");

        table.setColumnReorderingAllowed(true);
        table.setSortEnabled(false);

        table.setColumnCollapsingAllowed(true);
        table.setSelectable(true);
        table.setPageLength(20);
        table.setFooterVisible(true);
        table.setCacheRate(20);

		table.addItemClickListener(event -> {
            if(!(event.getPropertyId().toString().equalsIgnoreCase(CdmQueryFactory.TAXON_COLUMN))
            		&& !(event.getPropertyId().toString().equalsIgnoreCase(CdmQueryFactory.RANK_COLUMN))){
                final Item item = event.getItem();
                Property<?> itemProperty = item.getItemProperty("uuid");
                UUID uuid = UUID.fromString(itemProperty.getValue().toString());
                final Taxon taxon = CdmBase.deproxy(CdmSpringContextHelper.getTaxonService()
                		.load(uuid,Arrays.asList("descriptions.descriptionElements","name.taxonBases","updatedBy")), Taxon.class);
                final String areaID = (String)event.getPropertyId();
                PresenceAbsenceTerm presenceAbsenceTerm = null;
                Object statusValue = item.getItemProperty(areaID).getValue();
                if(statusValue instanceof String){
                	presenceAbsenceTerm = TermCacher.getInstance().getPresenceAbsenceTerm((String) statusValue);
                }
                //popup window
                final Window popup = new Window("Choose distribution status");
                final ListSelect termSelect = new ListSelect();
                termSelect.setSizeFull();
                termSelect.setContainerDataSource(getPresenter().getPresenceAbsenceTermContainer());
                termSelect.setNullSelectionAllowed(presenceAbsenceTerm != null);
                if(presenceAbsenceTerm != null){
                	termSelect.setNullSelectionItemId("[no status]");
                }else{
                    logger.debug("No distribution status exists yet for area");
                }
                termSelect.setValue(presenceAbsenceTerm);
                termSelect.addValueChangeListener(valueChangeEvent -> {
//						System.out.println(valueChangeEvent);
						Object distributionStatus = valueChangeEvent.getProperty().getValue();
						getPresenter().updateDistributionField(areaID, distributionStatus, taxon);
						container.refresh();
						popup.close();
				});
                VerticalLayout layout = new VerticalLayout(termSelect);
                popup.setContent(layout);
                popup.setModal(true);
                popup.center();
                UI.getCurrent().addWindow(popup);
            }
        });

		mainLayout.addComponent(table, "top:75px;right:10.0px;left:10.0px;");

		return mainLayout;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public void enter(ViewChangeEvent event) {
	    update();
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public void update(){
		try {
			container = getPresenter().getSQLContainer();
		} catch (SQLException e) {
			DistributionEditorUtil.showSqlError(e);
			return;
		}
		if(container==null){
			return;
		}

		table.setContainerDataSource(container);

		List<String> columnHeaders = new ArrayList<>(Arrays.asList(table.getColumnHeaders()));
		columnHeaders.remove(CdmQueryFactory.DTYPE_COLUMN);
		columnHeaders.remove(CdmQueryFactory.ID_COLUMN);
		columnHeaders.remove(CdmQueryFactory.UUID_COLUMN);
		columnHeaders.remove(CdmQueryFactory.CLASSIFICATION_COLUMN);

		List<String> columnList = new ArrayList<>(columnHeaders);

		String[] string = new String[columnList.size()];

		table.setVisibleColumns(columnList.toArray());
		table.setColumnHeaders(columnList.toArray(string));
		table.setColumnFooter(CdmQueryFactory.TAXON_COLUMN, "Total amount of Taxa displayed: " + container.size());
	}

	private void createEditClickListener(){
		//details
	    Button detailButton = toolbar.getDetailButton();
		detailButton.setCaption("Taxon Details");
		detailButton.addClickListener(event -> {
				Object selectedItemId = DistributionTableViewBean.this.table.getValue();
//				Object selectedItemId = DistributionTableViewBean.this.grid.getSelectedRow();
				if(selectedItemId!=null){
				    final UUID uuid = UUID.fromString(table.getContainerDataSource().getItem(selectedItemId).getItemProperty("uuid").getValue().toString());
//					final UUID uuid = UUID.fromString(grid.getContainerDataSource().getItem(selectedItemId).getItemProperty("uuid").getValue().toString());
					Taxon taxon = HibernateProxyHelper.deproxy(CdmSpringContextHelper.getTaxonService().load(uuid), Taxon.class);
					List<DescriptionElementBase> listDescriptions = getPresenter().listDescriptionElementsForTaxon(taxon, null);
					DetailWindow detailWindow = new DetailWindow(taxon, listDescriptions);
					Window window = detailWindow.createWindow();
					window.center();
					getUI().addWindow(window);
				}
				else{
					Notification.show("Please select a taxon", Type.HUMANIZED_MESSAGE);
				}
			}
		);

		//area and taxon
		Button areaAndTaxonSettingsButton = toolbar.getDistributionSettingsButton();
		areaAndTaxonSettingsButton.addClickListener(event -> openAreaAndTaxonSettings());

		//distr status
		Button distrStatusButton = toolbar.getSettingsButton();
		distrStatusButton.addClickListener(event -> openStatusSettings());
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public void openStatusSettings() {
        if(distributionStatusConfigWindow==null){
            distributionStatusConfigWindow = new DistributionStatusSettingsConfigWindow(this);
        }
        Window window  = distributionStatusConfigWindow.createWindow("Status");
        window.setWidth("25%");
        window.setHeight("60%");
        UI.getCurrent().addWindow(window);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public void openAreaAndTaxonSettings() {
		if(areaAndTaxonConfigWindow==null){
			areaAndTaxonConfigWindow = new AreaAndTaxonSettingsConfigWindow(this);
		}
        Window window  = areaAndTaxonConfigWindow.createWindow("Areas and Taxa");
        UI.getCurrent().addWindow(window);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public boolean allowAnonymousAccess() {
		// TODO Auto-generated method stub
		return false;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public Collection<Collection<GrantedAuthority>> allowedGrantedAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	protected String getHeaderText() {
		// TODO Auto-generated method stub
		return null;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	protected String getSubHeaderText() {
		// TODO Auto-generated method stub
		return null;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	protected void initContent() {
	    // initialize layout
        AbsoluteLayout mainLayout = initLayout();
        setCompositionRoot(mainLayout);
        // add click listener on DistributionToolbar-buttons
        createEditClickListener();
	}
}
