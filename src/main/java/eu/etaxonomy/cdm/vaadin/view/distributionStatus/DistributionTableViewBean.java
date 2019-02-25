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
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.FooterRow;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import eu.etaxonomy.cdm.hibernate.HibernateProxyHelper;
import eu.etaxonomy.cdm.i18n.Messages;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.common.Language;
import eu.etaxonomy.cdm.model.common.Representation;
import eu.etaxonomy.cdm.model.description.DescriptionElementBase;
import eu.etaxonomy.cdm.model.description.PresenceAbsenceTerm;
import eu.etaxonomy.cdm.model.location.NamedArea;
import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.vaadin.component.distributionStatus.AreaAndTaxonSettingsConfigWindow;
import eu.etaxonomy.cdm.vaadin.component.distributionStatus.DetailWindow;
import eu.etaxonomy.cdm.vaadin.component.distributionStatus.DistributionStatusSettingsConfigWindow;
import eu.etaxonomy.cdm.vaadin.component.distributionStatus.DistributionToolbar;
import eu.etaxonomy.cdm.vaadin.component.distributionStatus.HelpWindow;
import eu.etaxonomy.cdm.vaadin.container.CdmSQLContainer;
import eu.etaxonomy.cdm.vaadin.event.error.DelegatingErrorHandler;
import eu.etaxonomy.cdm.vaadin.event.error.HibernateExceptionHandler;
import eu.etaxonomy.cdm.vaadin.permission.AccessRestrictedView;
import eu.etaxonomy.cdm.vaadin.util.CdmQueryFactory;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.util.DistributionEditorUtil;
import eu.etaxonomy.cdm.vaadin.view.AbstractPageView;

/**
 * The main view of the distribution status editor.
 *
 * @author freimeier
 * @since 18.10.2017
 *
 */
@ViewScope
@SpringView(name=DistributionTableViewBean.NAME)
public class DistributionTableViewBean extends AbstractPageView<DistributionTablePresenter>
            implements IDistributionTableView, AccessRestrictedView {

	private static final long serialVersionUID = 1L;
    public static final String NAME = "distGrid"; //$NON-NLS-1$

    @Autowired
    private DistributionToolbar toolbar;

	private Table table;
	private Grid grid;
	private FooterRow footerRow;

    private CdmSQLContainer container;
    private LazyQueryContainer gridcontainer;
	private AreaAndTaxonSettingsConfigWindow areaAndTaxonConfigWindow;;
	private DistributionStatusSettingsConfigWindow distributionStatusConfigWindow;
	private HelpWindow helpWindow;
    private String accessDeniedMessage;

	/**
	 * Creates a new distribution status editor view.
	 */
	public DistributionTableViewBean() {
		super();
	}

	/**
	 * Initializes the layout of the view,
	 * adds the {@link DistributionToolbar} and
	 * creates the distribution status table and adds click listener for editing to it.
	 * @return Layout of the view.
	 */
	private AbsoluteLayout initLayout() {
		AbsoluteLayout mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%"); //$NON-NLS-1$
		mainLayout.setHeight("100%"); //$NON-NLS-1$

		setWidth("100.0%"); //$NON-NLS-1$
		setHeight("100.0%"); //$NON-NLS-1$

		//Horizontal Toolbar
		mainLayout.addComponent(toolbar, "top:0.0px;right:0.0px;"); //$NON-NLS-1$

		// table + formatting
		table = new Table(){
			private static final long serialVersionUID = -5148756917468804385L;

			@Override
			protected String formatPropertyValue(Object rowId, Object colId, Property<?> property) {
				String formattedValue = null;
				PresenceAbsenceTerm presenceAbsenceTerm = null;
				Object value = property.getValue();
				if(value instanceof String){
                    try {
                        presenceAbsenceTerm = (PresenceAbsenceTerm)CdmSpringContextHelper.getTermService().load(UUID.fromString((String)value));
                    }catch(IllegalArgumentException|ClassCastException e) {
                        // Not a PresenceAbsenceTerm Column
                    }
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

                final String areaString = (String)event.getPropertyId();
                final NamedArea area = getPresenter().getAreaFromString(areaString);

                if(!getPresenter().getReadOnlyAreas().contains(area)) {
                    final Item item = event.getItem();
                    Property<?> itemProperty = item.getItemProperty(CdmQueryFactory.UUID_COLUMN);
                    UUID uuid = UUID.fromString(itemProperty.getValue().toString());
                    final Taxon taxon = CdmBase.deproxy(CdmSpringContextHelper.getTaxonService()
                    		.load(uuid, DistributionEditorUtil.INCLUDE_UNPUBLISHED, Arrays.asList("descriptions.descriptionElements","name.taxonBases","updatedBy")), Taxon.class);
                    PresenceAbsenceTerm presenceAbsenceTerm = null;
                    Object statusValue = item.getItemProperty(areaString).getValue();
                    if(statusValue instanceof String){
                        try {
                            presenceAbsenceTerm = (PresenceAbsenceTerm)CdmSpringContextHelper.getTermService().load(UUID.fromString((String)statusValue));
                        }catch(IllegalArgumentException|ClassCastException e) {
                            // Not a PresenceAbsenceTerm Column
                        }
                    }
                    //popup window
                    final Window popup = new Window(Messages.getLocalizedString(Messages.DistributionTableViewBean_CHOOSE_DISTRIBUTION_STATUS));
                    DelegatingErrorHandler errorHandler = new DelegatingErrorHandler();
                    errorHandler.registerHandler(new HibernateExceptionHandler());
                    popup.setErrorHandler(errorHandler);
                    final ListSelect termSelect = new ListSelect();
                    termSelect.setSizeFull();
                    termSelect.setContainerDataSource(getPresenter().getPresenceAbsenceTermContainer());
                    termSelect.setNullSelectionAllowed(presenceAbsenceTerm != null);
                    if(presenceAbsenceTerm != null){
                    	termSelect.setNullSelectionItemId(Messages.getLocalizedString(Messages.DistributionTableViewBean_NO_STATUS_SELECT));
                    }else{
                        logger.debug("No distribution status exists yet for area");
                    }
                    termSelect.setValue(presenceAbsenceTerm);
                    termSelect.addValueChangeListener(valueChangeEvent -> {
    						PresenceAbsenceTerm distributionStatus = (PresenceAbsenceTerm) valueChangeEvent.getProperty().getValue();
    						getPresenter().updateDistributionField(area, distributionStatus, taxon);
    						container.refresh();
    						popup.close();
    				});
                    VerticalLayout layout = new VerticalLayout(termSelect);
                    popup.setContent(layout);
                    popup.setModal(true);
                    popup.center();
                    UI.getCurrent().addWindow(popup);
                }
            }
        });

		mainLayout.addComponent(table, "top:75px;right:10.0px;left:10.0px;");

//		grid = new Grid();
//		grid.setSizeFull();
//		grid.setEditorEnabled(true);
//      grid.setFooterVisible(true);
//		mainLayout.addComponent(grid, "top:75px;right:10.0px;left:10.0px;"); //$NON-NLS-1$

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
		table.setColumnFooter(CdmQueryFactory.TAXON_COLUMN, String.format(Messages.getLocalizedString(Messages.DistributionTableViewBean_TOTAL_TAXA), container.size()));

//        gridcontainer = getPresenter().getAreaDistributionStatusContainer();
//        if(gridcontainer==null){
//            return;
//        }
//
//        if(footerRow != null) {
//            grid.removeFooterRow(footerRow);
//        }
//		grid.removeAllColumns();
//
//        grid.setContainerDataSource(gridcontainer);
//
//        Column uuidColumn = grid.getColumn(DistributionStatusQuery.UUID_COLUMN);
//        uuidColumn.setEditable(false);
//        uuidColumn.setHidden(true);
//        Column taxonColumn = grid.getColumn(DistributionStatusQuery.TAXON_COLUMN);
//        taxonColumn.setEditable(false);
//        taxonColumn.setHeaderCaption(Messages.DistributionTableViewBean_TAXON);
//        taxonColumn.setLastFrozenColumn();
//
//        Converter<String, UUID> displayConverter = new PresenceAbsenceTermUuidTitleStringConverter();
//        Converter<Object, UUID> editorConverter = new PresenceAbsenceTermUuidObjectConverter();
//        for(Column c : grid.getColumns()) {
//            if(c.isEditable()) {
//                NamedArea namedArea = (NamedArea) CdmSpringContextHelper.getTermService().load((UUID.fromString(c.getHeaderCaption())));
//                String caption = DistributionEditorUtil.isAbbreviatedLabels() ?
//                        namedArea.getRepresentation(Language.DEFAULT()).getAbbreviatedLabel() : namedArea.getTitleCache();
//                c.setHeaderCaption(caption);
//                c.setConverter(displayConverter);
//
//                NativeSelect termSelect = new NativeSelect();
//                termSelect.setSizeFull();
//                termSelect.setContainerDataSource(getPresenter().getPresenceAbsenceTermContainer());
//                termSelect.setItemCaptionMode(ItemCaptionMode.PROPERTY);
//                termSelect.setItemCaptionPropertyId("titleCache"); //$NON-NLS-1$
//                termSelect.setConverter(editorConverter);
//                termSelect.setImmediate(true);
//                c.setEditorField(termSelect);
//            }
//        }
//        grid.getEditorFieldGroup().addCommitHandler(new CommitHandler() {
//            private static final long serialVersionUID = 7515807188410712420L;
//
//            @Override
//            public void preCommit(CommitEvent commitEvent) throws CommitException {
//
//            }
//
//            @Override
//            public void postCommit(CommitEvent commitEvent) throws CommitException {
//                gridcontainer.commit();
//            }
//        });
//
//        footerRow = grid.appendFooterRow();
//        Object[] cells = grid.getColumns().stream().map(c -> c.getPropertyId()).toArray(Object[]::new);
//        if(cells.length == 0) {
//            return;
//        }
//        FooterCell footerCell = null;
//        if(cells.length > 1) {
//            footerCell = footerRow.join(cells);
//        }else {
//            footerCell = footerRow.getCell(cells[0]);
//        }
//        footerCell.setText(String.format(Messages.DistributionTableViewBean_TOTAL_TAXA, gridcontainer.size()));
	}

	/**
	 * Adds click listener to the buttons defined in the {@link DistributionToolbar}.
	 */
	private void createEditClickListener(){
		//details
	    Button detailButton = toolbar.getDetailButton();
		detailButton.setCaption(Messages.getLocalizedString(Messages.DistributionTableViewBean_TAXON_DETAILS));
		detailButton.addClickListener(event -> {
				Object selectedItemId = DistributionTableViewBean.this.table.getValue();
//				Object selectedItemId = DistributionTableViewBean.this.grid.getSelectedRow();
				if(selectedItemId!=null){
				    final UUID uuid = UUID.fromString(table.getContainerDataSource().getItem(selectedItemId).getItemProperty(CdmQueryFactory.UUID_COLUMN).getValue().toString());
//					final UUID uuid = (UUID) selectedItemId;
					Taxon taxon = HibernateProxyHelper.deproxy(CdmSpringContextHelper.getTaxonService().load(uuid), Taxon.class);
//					Taxon taxon = (Taxon) CdmSpringContextHelper.getTaxonService().load(uuid);
					List<DescriptionElementBase> listDescriptions = getPresenter().listDescriptionElementsForTaxon(taxon, null);
					DetailWindow detailWindow = new DetailWindow(taxon, listDescriptions);
					Window window = detailWindow.createWindow();
					window.center();
					getUI().addWindow(window);
				}
				else{
					Notification.show(Messages.getLocalizedString(Messages.DistributionTableViewBean_SELECT_TAXON), Type.HUMANIZED_MESSAGE);
				}
			}
		);

		//area and taxon
		Button areaAndTaxonSettingsButton = toolbar.getDistributionSettingsButton();
		areaAndTaxonSettingsButton.addClickListener(event -> openAreaAndTaxonSettings());

		//distr status
		Button distrStatusButton = toolbar.getSettingsButton();
		distrStatusButton.addClickListener(event -> openStatusSettings());

	    //help
        Button helpButton = toolbar.getHelpButton();
        helpButton.addClickListener(event -> openHelpWindow());
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public void openStatusSettings() {
        if(distributionStatusConfigWindow==null){
            distributionStatusConfigWindow = new DistributionStatusSettingsConfigWindow(this);
        }
        Window window  = distributionStatusConfigWindow.createWindow(Messages.getLocalizedString(Messages.DistributionTableViewBean_STATUS));
        window.setWidth("25%"); //$NON-NLS-1$
        window.setHeight("60%"); //$NON-NLS-1$
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
        Window window  = areaAndTaxonConfigWindow.createWindow(Messages.getLocalizedString(Messages.DistributionTableViewBean_AREAS_AND_TAXA));
        UI.getCurrent().addWindow(window);
	}

	public void openHelpWindow() {
	       if(helpWindow==null){
	           helpWindow = new HelpWindow(this);
	        }
	        Window window  = helpWindow.createWindow(Messages.getLocalizedString(Messages.DistributionToolbar_HELP));
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

    @Override
    public String getAccessDeniedMessage() {
        return accessDeniedMessage;
    }

    @Override
    public void setAccessDeniedMessage(String accessDeniedMessage) {
        this.accessDeniedMessage = accessDeniedMessage;

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
