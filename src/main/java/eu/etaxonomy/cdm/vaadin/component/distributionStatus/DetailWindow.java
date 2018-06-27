package eu.etaxonomy.cdm.vaadin.component.distributionStatus;
import java.util.Collection;

import org.springframework.context.annotation.Scope;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;

import eu.etaxonomy.cdm.i18n.Messages;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.common.Language;
import eu.etaxonomy.cdm.model.description.CategoricalData;
import eu.etaxonomy.cdm.model.description.CommonTaxonName;
import eu.etaxonomy.cdm.model.description.DescriptionElementBase;
import eu.etaxonomy.cdm.model.description.Distribution;
import eu.etaxonomy.cdm.model.description.PresenceAbsenceTerm;
import eu.etaxonomy.cdm.model.description.StateData;
import eu.etaxonomy.cdm.model.description.TextData;
import eu.etaxonomy.cdm.model.taxon.Taxon;

/**
 * A window to display details of a taxon.
 *
 */
@Scope("prototype")
public class DetailWindow extends CustomComponent{


	private final Collection<DescriptionElementBase> listDescriptions;
	private final Taxon taxon;

	/**
	 * Displays a window showing the given details of the given taxon.
	 * @param taxon The taxon the details belong to.
	 * @param listDescriptions The details to show.
	 */
	public DetailWindow(Taxon taxon, Collection<DescriptionElementBase> listDescriptions) {
		this.taxon = taxon;
		this.listDescriptions = listDescriptions;

	}

	/**
	 * Creates the detail window.
	 * @return Window to be displayed.
	 */
	public Window createWindow(){
		Window window = new Window();
		window.setHeightUndefined();
		window.setHeight("600px"); //$NON-NLS-1$
		window.setWidth("400px"); //$NON-NLS-1$
		window.setCaption(taxon.getName().getTitleCache());
		window.setCloseShortcut(KeyCode.W, ModifierKey.CTRL);
		if(listDescriptions.isEmpty()){
			window.setContent(new Label(Messages.getLocalizedString(Messages.DetailWindow_NO_DESCRIPTIVE_DATA_FOUND)));
		}
		else{
			window.setContent(constructDescriptionTree());
		}
		return window;
	}

	/**
	 * Creates a tree view of the details contained in {@link #listDescriptions}.
	 * @return Tree of details contained in {@link #listDescriptions}.
	 */
	private Tree constructDescriptionTree(){
		Tree tree = new Tree();
		tree.setSizeUndefined();
		String parent = "Descriptive Data"; //$NON-NLS-1$
		tree.setValue(parent);
		initDescriptionTree(tree, listDescriptions, parent);
		return tree;
	}

	/**
	 * Populates a given {@link Tree} with the details contained in {@code listDescriptions} and sets its root to {@code parent}. 
	 * @param tree The tree to populate.
	 * @param listDescriptions The details to show.
	 * @param parent The root of the tree.
	 */
	private void initDescriptionTree(Tree tree, Collection<DescriptionElementBase>listDescriptions, Object parent) {
		//TODO: sorting List
		for (DescriptionElementBase deb : listDescriptions){
			tree.addItem(deb.getFeature());
			tree.setItemCaption(deb.getFeature(), deb.getFeature().getTitleCache());
			tree.setParent(deb.getFeature(), parent);
			tree.setChildrenAllowed(deb.getFeature(), true);

			if(deb.isInstanceOf(CategoricalData.class)){
				CategoricalData cd = CdmBase.deproxy(deb, CategoricalData.class);
				if(cd.getStatesOnly().size() <= 1){
					for(StateData st  : cd.getStateData()){
						tree.addItem(st);
						tree.setItemCaption(st, st.getState().getTitleCache());
						tree.setParent(st, deb.getFeature());
						tree.setChildrenAllowed(st, false);
					}
				}else{
					//TODO: implement recursion
				}
			}else if(deb.isInstanceOf(TextData.class)){
				TextData td = CdmBase.deproxy(deb,TextData.class);
				tree.addItem(td);
				tree.setItemCaption(td, td.getText(Language.GERMAN()));
				tree.setParent(td, deb.getFeature());
				tree.setChildrenAllowed(td, false);
			}else if(deb.isInstanceOf(CommonTaxonName.class)){
			    CommonTaxonName td = CdmBase.deproxy(deb, CommonTaxonName.class);
			    tree.addItem(td);
			    tree.setItemCaption(td, td.getName());
			    tree.setParent(td, deb.getFeature());
			    tree.setChildrenAllowed(td, false);
			}else if(deb.isInstanceOf(Distribution.class)){
			    Distribution db = CdmBase.deproxy(deb, Distribution.class);
			    PresenceAbsenceTerm status = db.getStatus();
			    if(status!=null){
			        tree.addItem(db.toString());
			        tree.setParent(db.toString(), deb.getFeature());
			        tree.setChildrenAllowed(db.toString(), true);
				    tree.addItem(status.toString());
				    tree.setParent(status.toString(), db.toString());
				    tree.setChildrenAllowed(status.toString(), false);
				}
			}
			tree.expandItemsRecursively(parent);
		}

	}

}
