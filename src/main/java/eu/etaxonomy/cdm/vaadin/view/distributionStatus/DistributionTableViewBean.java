/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.distributionStatus;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;

import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.FooterCell;
import com.vaadin.ui.Grid.FooterRow;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import eu.etaxonomy.cdm.i10n.Messages;
import eu.etaxonomy.cdm.model.common.Language;
import eu.etaxonomy.cdm.model.description.DescriptionElementBase;
import eu.etaxonomy.cdm.model.location.NamedArea;
import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.vaadin.component.DetailWindow;
import eu.etaxonomy.cdm.vaadin.component.DistributionToolbar;
import eu.etaxonomy.cdm.vaadin.container.CdmSQLContainer;
import eu.etaxonomy.cdm.vaadin.security.AccessRestrictedView;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.util.DistributionEditorUtil;
import eu.etaxonomy.cdm.vaadin.util.DistributionStatusQuery;
import eu.etaxonomy.cdm.vaadin.util.converter.PresenceAbsenceTermUuidObjectConverter;
import eu.etaxonomy.cdm.vaadin.util.converter.PresenceAbsenceTermUuidTitleStringConverter;
import eu.etaxonomy.cdm.vaadin.view.AbstractPageView;

/**
 * @author freimeier
 * @since 18.10.2017
 *
 */
@ViewScope
@SpringView(name=DistributionTableViewBean.NAME)
public class DistributionTableViewBean
            extends AbstractPageView<DistributionTablePresenter>
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

	public DistributionTableViewBean() {
		super();
	}

	private AbsoluteLayout initLayout() {
		AbsoluteLayout mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%"); //$NON-NLS-1$
		mainLayout.setHeight("100%"); //$NON-NLS-1$

		setWidth("100.0%"); //$NON-NLS-1$
		setHeight("100.0%"); //$NON-NLS-1$

		//Horizontal Toolbar
		mainLayout.addComponent(toolbar, "top:0.0px;right:0.0px;"); //$NON-NLS-1$

//		// table + formatting
//		table = new Table(){
//			private static final long serialVersionUID = -5148756917468804385L;
//
//			@Override
//			protected String formatPropertyValue(Object rowId, Object colId, Property<?> property) {
//				String formattedValue = null;
//				PresenceAbsenceTerm presenceAbsenceTerm = null;
//				Object value = property.getValue();
//				if(value instanceof String){
//					presenceAbsenceTerm = TermCacher.getInstance().getPresenceAbsenceTerm((String) value);
//				}
//				if(presenceAbsenceTerm != null){
//					Representation representation = presenceAbsenceTerm.getRepresentation(Language.DEFAULT());
//					if(representation!=null){
//						if(DistributionEditorUtil.isAbbreviatedLabels()){
//							formattedValue = representation.getAbbreviatedLabel();
//						}
//						else{
//							formattedValue = representation.getLabel();
//						}
//					}
//					if(formattedValue==null){
//						formattedValue = presenceAbsenceTerm.getTitleCache();
//					}
//					return formattedValue;
//				}
//				return super.formatPropertyValue(rowId, colId, property);
//			}
//		};
//		table.setImmediate(false);
//		table.setWidth("100.0%");
//		table.setHeight("100.0%");
//
//        table.setColumnReorderingAllowed(true);
//        table.setSortEnabled(false);
//
//        table.setColumnCollapsingAllowed(true);
//        table.setSelectable(true);
//        table.setPageLength(20);
//        table.setFooterVisible(true);
//        table.setCacheRate(20);
//
//		table.addItemClickListener(event -> {
//            if(!(event.getPropertyId().toString().equalsIgnoreCase(CdmQueryFactory.TAXON_COLUMN))
//            		&& !(event.getPropertyId().toString().equalsIgnoreCase(CdmQueryFactory.RANK_COLUMN))
//            		// TODO: HACK FOR RL 2017, REMOVE AS SOON AS POSSIBLE
//            		&& !(event.getPropertyId().toString().equalsIgnoreCase("DE"))
//            		&& !(event.getPropertyId().toString().equalsIgnoreCase("Deutschland"))){
//                final Item item = event.getItem();
//                Property<?> itemProperty = item.getItemProperty("uuid");
//                UUID uuid = UUID.fromString(itemProperty.getValue().toString());
//                final Taxon taxon = CdmBase.deproxy(CdmSpringContextHelper.getTaxonService()
//                		.load(uuid,Arrays.asList("descriptions.descriptionElements","name.taxonBases","updatedBy")), Taxon.class);
//                final String areaID = (String)event.getPropertyId();
//                PresenceAbsenceTerm presenceAbsenceTerm = null;
//                Object statusValue = item.getItemProperty(areaID).getValue();
//                if(statusValue instanceof String){
//                	presenceAbsenceTerm = TermCacher.getInstance().getPresenceAbsenceTerm((String) statusValue);
//                }
//                //popup window
//                final Window popup = new Window("Choose distribution status");
//                final ListSelect termSelect = new ListSelect();
//                termSelect.setSizeFull();
//                termSelect.setContainerDataSource(getPresenter().getPresenceAbsenceTermContainer());
//                termSelect.setNullSelectionAllowed(presenceAbsenceTerm != null);
//                if(presenceAbsenceTerm != null){
//                	termSelect.setNullSelectionItemId("[no status]");
//                }else{
//                    logger.debug("No distribution status exists yet for area");
//                }
//                termSelect.setValue(presenceAbsenceTerm);
//                termSelect.addValueChangeListener(valueChangeEvent -> {
//						Object distributionStatus = valueChangeEvent.getProperty().getValue();
//						getPresenter().updateDistributionField(areaID, distributionStatus, taxon);
//						container.refresh();
//						popup.close();
//				});
//                VerticalLayout layout = new VerticalLayout(termSelect);
//                popup.setContent(layout);
//                popup.setModal(true);
//                popup.center();
//                UI.getCurrent().addWindow(popup);
//            }
//        });
//
//		mainLayout.addComponent(table, "top:75px;right:10.0px;left:10.0px;");

		grid = new Grid();
		grid.setSizeFull();
		grid.setEditorEnabled(true);
        grid.setFooterVisible(true);
		mainLayout.addComponent(grid, "top:75px;right:10.0px;left:10.0px;"); //$NON-NLS-1$

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
//		try {
//			container = getPresenter().getSQLContainer();
//		} catch (SQLException e) {
//			DistributionEditorUtil.showSqlError(e);
//			return;
//		}
//		if(container==null){
//			return;
//		}

//		table.setContainerDataSource(container);
//
//		List<String> columnHeaders = new ArrayList<>(Arrays.asList(table.getColumnHeaders()));
//		columnHeaders.remove(CdmQueryFactory.DTYPE_COLUMN);
//		columnHeaders.remove(CdmQueryFactory.ID_COLUMN);
//		columnHeaders.remove(CdmQueryFactory.UUID_COLUMN);
//		columnHeaders.remove(CdmQueryFactory.CLASSIFICATION_COLUMN);
//		columnHeaders.sort(new Comparator<String>() {
//            @Override
//            public int compare(String o1, String o2) {
//                if(o1.equals(CdmQueryFactory.TAXON_COLUMN) || o2.equals(CdmQueryFactory.TAXON_COLUMN)) {
//                    return o1.equals(CdmQueryFactory.TAXON_COLUMN) ? -1 : 1;
//                }
//                if(o1.equals(CdmQueryFactory.RANK_COLUMN) || o2.equals(CdmQueryFactory.RANK_COLUMN)) {
//                    return o1.equals(CdmQueryFactory.RANK_COLUMN) ? -1 : 1;
//                }
//
//                // TODO: HACK FOR RL 2017, REMOVE AS SOON AS POSSIBLE
//                if(o1.equals("DE") || o1.equals("Deutschland")
//                        || o2.equals("DE") || o2.equals("Deutschland")) {
//                    return (o1.equals("DE") || o1.equals("Deutschland")) ? -1 : 1;
//                }
//
//                return o1.compareTo(o2);
//            }
//		});
//
//		List<String> columnList = new ArrayList<>(columnHeaders);
//
//		String[] string = new String[columnList.size()];
//
//		table.setVisibleColumns(columnList.toArray());
//		table.setColumnHeaders(columnList.toArray(string));
//		table.setColumnFooter(CdmQueryFactory.TAXON_COLUMN, "Total amount of Taxa displayed: " + container.size());

        gridcontainer = getPresenter().getAreaDistributionStatusContainer();
        if(gridcontainer==null){
            return;
        }

        if(footerRow != null) {
            grid.removeFooterRow(footerRow);
        }
		grid.removeAllColumns();

        grid.setContainerDataSource(gridcontainer);

        Column uuidColumn = grid.getColumn(DistributionStatusQuery.UUID_COLUMN);
        uuidColumn.setEditable(false);
        uuidColumn.setHidden(true);
        Column taxonColumn = grid.getColumn(DistributionStatusQuery.TAXON_COLUMN);
        taxonColumn.setEditable(false);
        taxonColumn.setHeaderCaption(Messages.DistributionTableViewBean_TAXON);
        taxonColumn.setLastFrozenColumn();

        Converter<String, UUID> displayConverter = new PresenceAbsenceTermUuidTitleStringConverter();
        Converter<Object, UUID> editorConverter = new PresenceAbsenceTermUuidObjectConverter();
        for(Column c : grid.getColumns()) {
            if(c.isEditable()) {
                NamedArea namedArea = (NamedArea) CdmSpringContextHelper.getTermService().load((UUID.fromString(c.getHeaderCaption())));
                String caption = DistributionEditorUtil.isAbbreviatedLabels() ?
                        namedArea.getRepresentation(Language.DEFAULT()).getAbbreviatedLabel() : namedArea.getTitleCache();
                c.setHeaderCaption(caption);
                c.setConverter(displayConverter);

                NativeSelect termSelect = new NativeSelect();
                termSelect.setSizeFull();
                termSelect.setContainerDataSource(getPresenter().getPresenceAbsenceTermContainer());
                termSelect.setItemCaptionMode(ItemCaptionMode.PROPERTY);
                termSelect.setItemCaptionPropertyId("titleCache"); //$NON-NLS-1$
                termSelect.setConverter(editorConverter);
                termSelect.setImmediate(true);
                c.setEditorField(termSelect);
            }
        }
        grid.getEditorFieldGroup().addCommitHandler(new CommitHandler() {
            private static final long serialVersionUID = 7515807188410712420L;

            @Override
            public void preCommit(CommitEvent commitEvent) throws CommitException {

            }

            @Override
            public void postCommit(CommitEvent commitEvent) throws CommitException {
                gridcontainer.commit();
            }
        });

        footerRow = grid.appendFooterRow();
        Object[] cells = grid.getColumns().stream().map(c -> c.getPropertyId()).toArray(Object[]::new);
        if(cells.length == 0) {
            return;
        }
        FooterCell footerCell = null;
        if(cells.length > 1) {
            footerCell = footerRow.join(cells);
        }else {
            footerCell = footerRow.getCell(cells[0]);
        }
        footerCell.setText(String.format(Messages.DistributionTableViewBean_TOTAL_TAXA, gridcontainer.size()));
	}

	private void createEditClickListener(){
		//details
	    Button detailButton = toolbar.getDetailButton();
		detailButton.setCaption(Messages.DistributionTableViewBean_TAXON_DETAILS);
		detailButton.addClickListener(event -> {
//				Object selectedItemId = DistributionTableViewBean.this.table.getValue();
				Object selectedItemId = DistributionTableViewBean.this.grid.getSelectedRow();
				if(selectedItemId!=null){
//				    final UUID uuid = UUID.fromString(table.getContainerDataSource().getItem(selectedItemId).getItemProperty("uuid").getValue().toString());
					final UUID uuid = (UUID) selectedItemId;
//					Taxon taxon = HibernateProxyHelper.deproxy(CdmSpringContextHelper.getTaxonService().load(uuid), Taxon.class);
					Taxon taxon = (Taxon) CdmSpringContextHelper.getTaxonService().load(uuid);
					List<DescriptionElementBase> listDescriptions = getPresenter().listDescriptionElementsForTaxon(taxon, null);
					DetailWindow detailWindow = new DetailWindow(taxon, listDescriptions);
					Window window = detailWindow.createWindow();
					window.center();
					getUI().addWindow(window);
				}
				else{
					Notification.show(Messages.DistributionTableViewBean_SELECT_TAXON, Type.HUMANIZED_MESSAGE);
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
        Window window  = distributionStatusConfigWindow.createWindow(Messages.DistributionTableViewBean_STATUS);
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
        Window window  = areaAndTaxonConfigWindow.createWindow(Messages.DistributionTableViewBean_AREAS_AND_TAXA);
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
