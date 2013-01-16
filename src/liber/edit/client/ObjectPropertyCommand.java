package liber.edit.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.lang.StringBuffer;


/**	ObjectPropertyCommmand is initialised when a user selects an object property and elicits which or what
 *	type of objects are in the range of the property.
 *
 *	@author Feikje Hielkema
 *	@version 1.1 December 2007
 *
 *	@version 1.2 March 2008
 *
 *	@version 1.3 May 2008
 *
 *	@version 1.4 January 2009
 */
 public class ObjectPropertyCommand extends InputPopupPanel implements ClickListener
 {
 	private String property, nlname;
 	private String anchor;
 	private int cardinal;
 	private WysiwymTab tab;
 	private WYSIWYMinterface parent;
 	private int type = 0;
 	private String key;
 	private Help help;
 	
 	private Button okBtn = new Button("OK", this);
 	private Button cancelBtn = new Button("Cancel", this);
 	private Button helpBtn = new Button("Help!", this);
 	private Button addNewBtn = new Button("Add another", this);
 	private Button resetBtn = new Button("Start over", this);
 	
 	private HorizontalPanel newValuePanel = new HorizontalPanel();
 	private Tree tree, confirmTree;
 	private VerticalPanel treePanel, parentPanel;
 	private HorizontalPanel instancePanel;
 	private PopupPanel confirmationPP, treePP, formPP;
 	
 	private TextBox titleBox = new TextBox();
 	
 	private HashMap oldRange, newInstances, chosenFromArchive, hierarchyMap;
 	private List currentRange;
 	private int buttonsPressed = 0;
 	private Hierarchy[] hierarchy;
 	private TagCloud cloud;
 	private int selectedCloudIndex = -1;	//index of object currently selected from the tag cloud
 	private InstanceData tempData;
 	
 	private FormInfo[] formInformation;
 	private Widget[] inputWidget;
 	private SuggestBox suggestBox;
 	private CheckBox[] na;
 	
 	protected MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
 	
 	/**	Constructor.
 	 *	@param p Property name
 	 *	@param nl Property's nl-expression
 	 *	@param a Anchor ID
 	 *	@param car Maximum cardinality constraint
 	 *	@param wt Parent module
 	 *	@param wysInt Entrypoint
 	 */		
 	public ObjectPropertyCommand(String p, String nl, String a, int car, WysiwymTab wt, WYSIWYMinterface wysInt)
 	{
 		super();
 		property = p;
 		nlname = nl;
 		anchor = a;
 		cardinal = car;
 		tab = wt;
 		parent = wysInt;
 		key = wt.getKey();
 		if (tab instanceof QueryTab)
			type = wysInt.QUERY_TAB;
		else if (tab instanceof BrowsingTab)
			type = wysInt.BROWSE_TAB;
		help = new Help(parent, type);
 	}
 	
 	/**	Shows this pop-up, and tells the parent module that this is now the
 	 *	last shown pop-up. Also show tagcloud if it's there
 	 */
 	public void show()
 	{
 		super.show();
 		tab.setPreviousPopup(this);
 		if (cloud != null)
 			showTagCloud();
 	}
 	
 	/**	Hide this pop-up and tagcloud.
 	 */
 	public void hide()
 	{
 		tab.hideFolksonomy();
 		super.hide();
 	}

	/**	Initialise. Retrieve all individuals that are currently in the range of the property,
	 *	and all individuals that could be in the range.
	 */
 	public void init()
 	{
   		String userID = parent.getUser();
		if (userID == null)
			return;
   		parent.getHandler().getInstances(userID, property, anchor, type, key, new AsyncCallback()
		{
			public void onSuccess(Object o)
			{
				if (o == null)
				{
					Window.alert("There was an error when searching for individuals. Please try again.");
					parent.hideHourglass();
					return;
				}
				ExistingInstances result = (ExistingInstances) o;
				if (result.getRange() == null)
				{
					Window.alert("I'm afraid your session has expired. Please return to ourSpaces to start a new session.");
					parent.close();
				}
				else
					showCompletePane(result);
			}

			public void onFailure(Throwable caught)
			{
				parent.hideHourglass();
   				Window.alert(caught.getMessage());
   			}
		});
   	}
   	
   	/**	Display a list of objects currently in range, with 'x' buttons to remove
   	 *	them; also display an 'add another' button and a message
   	 */
   	private void showCompletePane(ExistingInstances result)
   	{
   		setStyleName("wysiwym-popup-textbox");
   		setPopupPosition(50, 50);
   		setWidth("450px");
   		
   		oldRange = new HashMap();	//original range, save so we can reset
   		hierarchyMap = new HashMap();	//class hierarchy of range
   		newInstances = new HashMap();	//new instances added by user
   		chosenFromArchive = new HashMap();	//instances chosen from archive
   		currentRange = new ArrayList();	//objects currently in range
   		getHierarchy();
   		
   		parentPanel = new VerticalPanel();
   		parentPanel.setSpacing(5);
   		
   		for (int i = 0; i < result.getRange().length; i++)	//save the old range
   		{												//which is also the current range!
   			oldRange.put(result.getRange()[i], result.getRangeID()[i]);
   			currentRange.add(result.getRange()[i]);
   		} 		
   		updateHtmlPanel();  
   		cloud = result.getTagCloud();	
   		
   		HorizontalPanel hp = new HorizontalPanel();
 		hp.add(okBtn);
 		hp.add(cancelBtn);
 		hp.add(helpBtn);
 		hp.add(resetBtn);
 		hp.setSpacing(20);
 		parentPanel.add(hp);

   		setWidget(parentPanel);
   		parent.hideHourglass();
   		show();
   	}
   	
   	/**	Updates the list of range objects.
   	 */
   	private void updateHtmlPanel()
   	{
   		if (parentPanel.getWidgetCount() > 1)
   		{
	   		parentPanel.remove(0);	//remove the label and horizontal panel
   			parentPanel.remove(0);
   		}
   		
   		HorizontalPanel hp = new HorizontalPanel();
   		hp.setSpacing(10);
   		
   		if (currentRange.size() == 0)
   		{
   			parentPanel.insert(new Label("You have not added any values yet for the property '" + nlname + 
   			"'. Please add some values by pressing 'add'."), 0);
   			hp.add(new HTMLPanel(""));
   		}
   		else
   		{
   			parentPanel.insert(new Label("The values you have added so far for the property '" + nlname + "' are listed below. " +
   				"You can remove them by clicking 'x', or add new values by pressing 'add'. Press 'start over' to retrieve the original values."), 0);
   			
   			StringBuffer html = new StringBuffer("These are the values you have added so far:<ul>");
   			HashMap tmp = new HashMap();

   			for (int i = 0; i < currentRange.size(); i++)
   			{
   				String key = (String) currentRange.get(i);
   				html.append("<li>" + key + "&nbsp;<span id='");
   				String id = HTMLPanel.createUniqueId();
   				DeleteLabel lb = new DeleteLabel("x", key);
   				tmp.put(id, lb);
	   			html.append(id + "'></span>");
   				DOM.setStyleAttribute(lb.getElement(), "display", "inline");		
   			}
   		
   			html.append("</ul>");
   			HTMLPanel summaryPanel = new HTMLPanel(html.toString());
   			for (Iterator it = tmp.keySet().iterator(); it.hasNext(); )
   			{
   				String key = (String) it.next();
   				summaryPanel.add((DeleteLabel) tmp.get(key), key);
   			}
   			hp.add(summaryPanel);
   		}
   			
   		hp.add(addNewBtn);
   		parentPanel.insert(hp, 1);
   	}
   	
   	/**	'x' button to remove an object from the range
   	 */
   	private class DeleteLabel extends Label
   	{
   		private String remove;
   		
   		public DeleteLabel(String text, String r)
   		{
   			super(text);
   			remove = r;
   			sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS | Event.ONMOUSEWHEEL);
	   		setStyleName("wysiwym-label-large");
	   		
	   		addClickListener(new ClickListener()
  			{
  				public void onClick(Widget sender)
  				{	//remove the value from the range
  					currentRange.remove(remove);
  					updateHtmlPanel();	//and update the html panel
				} 			
  			});		
   		}
   	}
   	
   	/**	Retrieves the hierarchy of classes that are in the range of this property
   	 */
   	private void getHierarchy()
   	{
   		String user = parent.getUser();
		if (user == null)
			return;
   		parent.getHandler().getRangeHierarchy(user, property, new AsyncCallback()
		{
			public void onSuccess(Object o)
			{
				hierarchy = (Hierarchy[]) o;
				if (hierarchy.length == 0)
				{
					Window.alert("I'm afraid your session has expired. Please return to ourSpaces to start a new session.");
					parent.close();
				}
				makeTree();
			}

			public void onFailure(Throwable caught)
			{
				parent.hideHourglass();
   				Window.alert(caught.getMessage());
   			}
		});
   	}
   	
   	/**	Add the value that the user has found in the database to the summary list;
   	 *	store the title and id in 'chosenFromArchive'.
   	 *	@param id Sesame ID
   	 *	@param title NL-representation of value
   	 */
   	public void addValue(String id, String title)
   	{
   		int cntr = 1;
   		StringBuffer key = new StringBuffer(title);
   		if ((title.length() == 0) && (tempData != null))
   			key.append("The " + tempData.getType());
   		key.append(" (1)");
   		while (chosenFromArchive.containsKey(key.toString()))
   		{	//make sure there are no two entries with the same title
   			if (chosenFromArchive.get(key.toString()).equals(id))
   			{
	   			Window.alert("You have already added that object.");
   				parent.hideHourglass();
   				return;
   			}
   			cntr++;
   			key.replace(key.length() - 2, key.length() - 1, Integer.toString(cntr));
   		}
   		chosenFromArchive.put(key.toString(), id);
	   	currentRange.add(key.toString());
	   	updateHtmlPanel();
   		parent.hideHourglass();
   		tempData = null;
   	}
   	
   	/**	Creates a tree with the class hierarchy of the range
   	 */
   	public void makeTree()
   	{	//create the tree hierarchy
   		treePP = new InputPopupPanel();
   		treePP.setStyleName("wysiwym-popup-textbox");
   		tree = new Tree();
   		for (int i = 0; i < hierarchy.length; i++)
   			tree.addItem(addHierarchy(hierarchy[i]));

   		treePanel = new VerticalPanel();
   		treePanel.setSpacing(20);
   		treePanel.add(new Label("Please select the type of object you want to create."));
		treePanel.add(tree);
		openTree(tree, 20);

		HorizontalPanel btnPanel = new HorizontalPanel();
		Button ok = new Button("OK", new ClickListener()
		{
			public void onClick(Widget sender)
			{
				TreeItem item = tree.getSelectedItem();
   				if (item == null)
   					Window.alert("Please select the type of item you want to add from the tree.");
   				else
   				{
   					treePP.hide();
   					if ((type != parent.QUERY_TAB) && (item.getChildCount() > 0))
   						confirmChoice(item);
   					else
	   					getForm(item.getText(), (String) hierarchyMap.get(item.getText()));				
   				}		
			}
		});
		btnPanel.add(ok);
		
		Button cancel = new Button("Cancel", new ClickListener()
		{
			public void onClick(Widget sender)
			{
   				treePP.hide();				
			}
		});
		btnPanel.add(cancel);
		
		Button h = new Button("Help!", new ClickListener()
		{
			public void onClick(Widget sender)
			{
				help.showTreeHelp();
			}
		});
		btnPanel.add(h);
		
		btnPanel.setSpacing(20);
   		treePanel.add(btnPanel);
   		treePP.add(treePanel);
   	}
   	
   	/**	Shows the class hierarchy to the user
   	 */
   	public void showTree()
   	{
   		if ((hierarchy.length == 1) && (hierarchy[0].getSub().length == 0))//if there is only one type, there is no point asking the user!
   			getForm(hierarchy[0].getNLExpr(), hierarchy[0].getValue());	//so create a form for that object
   		else
   		{
	   		treePP.setPopupPosition(200, 50);
   			treePP.show();
   			parent.hideHourglass();
   		}
   	}
   	
   	/** Opens the first levels in the tree (so the class hierarchy doesn't become
	 * too huge to fit in the screen)
	 */
	private void openTree(Tree tree, int max)
	{
		int visibleItems = tree.getItemCount();
		for (int x = 0; x < tree.getItemCount(); x++)
		{
			TreeItem item = tree.getItem(x);
			visibleItems += item.getChildCount();
			if (visibleItems > max)
				break;
			else
				item.setState(true);
		}
	}
	   	
   	private TreeItem addHierarchy(Hierarchy h)
 	{
 		TreeItem item = new TreeItem(h.getNLExpr());
 		hierarchyMap.put(h.getNLExpr(), h.getValue());
 		Hierarchy[] sub = h.getSub();
 		for (int i = 0; i < sub.length; i++)
 			item.addItem(addHierarchy(sub[i]));
 		return item;
 	}
 	
 	private TreeItem copyItem(TreeItem item)
 	{
 		TreeItem copy = new TreeItem(item.getText());
 		for (int i = 0; i < item.getChildCount(); i++)
 			copy.addItem(copyItem(item.getChild(i)));
 		return copy;
 	}
 	
 	/**	Asks user to confirm whether he does not want to choose a more specific type
 	 */
 	private void confirmChoice(TreeItem item)
	{
		confirmationPP = new PopupPanel();
		confirmationPP.setStyleName("wysiwym-popup-textbox");
		VerticalPanel vp = new VerticalPanel();
		Label lb = new Label("Are you sure you do not want to choose a more specific resource type? " +
		"The more specific the type, the more information can be specified about it.");
		vp.add(lb);
		
		confirmTree = new Tree();
		TreeItem copy = copyItem(item);
		confirmTree.addItem(copy);
		vp.add(confirmTree);
		openTree(confirmTree, 20);
		
		HorizontalPanel btnPanel = new HorizontalPanel();
		Button ok = new Button("OK", new ClickListener()
		{
			public void onClick(Widget sender)
			{
				buttonsPressed++;
				TreeItem item = confirmTree.getSelectedItem();
				if (item == null)
   					Window.alert("Please select an item or press 'cancel'.");
   				else
   				{
  					confirmationPP.hide();
   					getForm(item.getText(), (String) hierarchyMap.get(item.getText()));
   				}
			}
		});
		btnPanel.setSpacing(10);
		btnPanel.add(ok);
		Button cancel = new Button("Cancel", new ClickListener()
		{
			public void onClick(Widget sender)
			{
				buttonsPressed++;
				confirmationPP.hide();
			}
		});
		btnPanel.add(cancel);
		Button helpBtn2 = new Button("Help!", new ClickListener()
		{
			public void onClick(Widget sender)
			{
				buttonsPressed++;
				help.showSpecificTypeHelp();
			}
		});
		btnPanel.add(helpBtn2);
	
		vp.add(btnPanel);
		vp.setSpacing(10);	
		confirmationPP.setWidget(vp);
		confirmationPP.setPopupPosition(200, 100);
		confirmationPP.setWidth("300px");
		confirmationPP.show();
		ok.setFocus(true);
	}
 	
 	private List getTypes(Hierarchy[] h)
 	{
 		List result = new ArrayList();
 		for (int i = 0; i < h.length; i++)
 		{
 			result.add(h[i].getValue());
 			result.addAll(getTypes(h[i].getSub()));
 		}
 		return result;
 	}
 	
 	/**	Checks the archive for existing objects of the given type(s) and with the 
 	 *	given name/title
 	 */
 	private void checkDatabase()
 	{
 		String title = titleBox.getText();
 		String[] types = new String[0];
 		types = (String[]) getTypes(hierarchy).toArray(types);
 		
 		parent.showHourglass();
 		String user = parent.getUser();
		if (user == null)
			return;
 		parent.getHandler().checkDatabase(user, anchor, property, type, key, title, types, new AsyncCallback()
    	{
    		public void onSuccess(Object o) 
    		{
    			if (o == null)
    			{
    				parent.hideHourglass();
    				Window.alert("There was an error when trying to process your input. Please try again.");
    			}
    			else
    			{
	    			String[] result = (String[]) o;
    				if (result.length == 0)
    				{
    					parent.hideHourglass();
    					Window.alert("There are no objects in the database with the name or title you provided, besides those that may be listed in the second pane.");
    				}
    				else
    					displayOptions(result, false);
    			}
    		}	
		  
		    public void onFailure(Throwable caught) 
		    {
		    	parent.hideHourglass();
		   		Window.alert(caught.getMessage());
		    }
    	});
 	}
 	
 	/**	Checks the archive for existing objects with the information provided in the
 	 *	form.
 	 */
 	private void checkDatabase(InstanceData data)
 	{
 		parent.showHourglass();
 		String user = parent.getUser();
		if (user == null)
			return;
 		parent.getHandler().checkDatabase(user, anchor, property, type, key, data, new AsyncCallback()
    	{
    		public void onSuccess(Object o) 
    		{
    			if (o == null)			//something went wrong with the search;
    				addValue();			//so just add a new instance
    			else
    			{
	    			String[] result = (String[]) o;
    				if (result.length == 0)	//no results found; add a new instance
    					addValue();
    				else
    					displayOptions(result, true);
    			}
    		}	
		  
		    public void onFailure(Throwable caught) 
		    {
		    	parent.hideHourglass();
		   		Window.alert(caught.getMessage());
		    }
    	});
 	}
 	
 	/**	Produces a form containing all string datatype properties of the new instance 
 	 *	which have min. cardinality >0.
 	 *	@param nl NL-representation of class
 	 *	@param classType Class name
 	 */
 	public void getForm(final String nl, final String classType)
 	{	//get all cardinal (string datatype?) properties from the server
 		String user = parent.getUser();
		if (user == null)
			return;
 		parent.showHourglass();	
 		parent.getHandler().getCardinalStringProperties(user, classType, new AsyncCallback()
 		{
 			public void onSuccess(Object o) 
   			{
   				if (o == null)
   				{
   					Window.alert("There was an error while retrieving some information. Please try again.");
   					parent.hideHourglass();
   					return;
   				}
   				formInformation = (FormInfo[]) o;
   				makeForm(nl, classType);
   			}
   	
   			public void onFailure(Throwable caught) 
   			{
   				parent.hideHourglass();
   				Window.alert(caught.getMessage());
   			}
 		}); 		
 	}
 	
 	/**	Shows a tag cloud with the most frequent tags used for the various properties
	 *	in a form. This tag cloud is non-clickable, as we don't know what tags 
	 *	are meant for which boxes! Users will just have to copy paste.
	 */
	public void showTagCloud()
	{
		StringBuffer sb = new StringBuffer();
		String[] idArray = new String[cloud.size()];
		FolksonomyLabel[] lbArray = new FolksonomyLabel[cloud.size()];
		suggestBox = new SuggestBox(oracle);
		suggestBox.addEventHandler(new SuggestionHandler()
		{
			public void onSuggestionSelected(SuggestionEvent event)
			{	//if someone selects a suggestion from the box, request additional information
				String selection = event.getSelectedSuggestion().getReplacementString();
				int idx = cloud.getIndex(selection);
				if (idx >= 0)
					getInformation(idx);
			}
		});
		suggestBox.addKeyboardListener(new KeyboardListenerAdapter()
		{
			public void onKeyPress(Widget sender, char keyCode, int modifiers)
			{
				if (inputWidget != null)	//if the user types in a new name, the other fields should be enabled
					for (int i = 1; i < inputWidget.length; i++)
						((FocusWidget) inputWidget[i]).setEnabled(true);
			}
		});
		
		for (int i = 0; i < cloud.size(); i++)
		{
			lbArray[i] = new FolksonomyLabel(cloud.getValue(i), suggestBox, this, i);
	  		oracle.add(cloud.getValue(i));
   			String id = HTMLPanel.createUniqueId();
   			idArray[i] = id;
   			
   			sb.append("<font size=\"");
   			sb.append(Integer.toString(cloud.getFrequency(i) + 1));
   			sb.append("\"><span id='");
   			sb.append(id);
   			sb.append("'></span></font> &nbsp; ");
   			DOM.setStyleAttribute(lbArray[i].getElement(), "display", "inline");				
   		}
   		HTMLPanel result = new HTMLPanel(sb.toString());
   		for (int i = 0; i < idArray.length; i++)
		{
			if (idArray[i] != null)
				result.add(lbArray[i], idArray[i]);
		}
   		tab.showFolksonomy(result);
	}
 	
 	/**	Retrieves information about the i'th item in the tag cloud.
 	 *	@param i Index
 	 */
 	public void getInformation(int i)
 	{	//store the id
 		selectedCloudIndex = i;
 		if (formInformation.length < 2)
 			return;		//only one or no properties must be specified; tag cloud suffices for that

 		String user = parent.getUser();
 		if (user == null)
 			return;		
 		parent.showHourglass();	
 		parent.getHandler().getDescriptionValues(user, cloud.getID(i), formInformation, new AsyncCallback()
 		{
 			public void onSuccess(Object o) 
   			{
   				if (o == null)
   				{
   					parent.hideHourglass();
   					return;
   				}
   				completeForm((String[]) o);
   			}
   	
   			public void onFailure(Throwable caught) 
   			{
   				parent.hideHourglass();
   			}
 		}); 		
 	}
 	
 	/**	Fills in the form with the description of the object selected from the
 	 *	tag cloud.
 	 *	@param values Values of the properties in the form
 	 */
 	public void completeForm(String[] values)
 	{	//first item is already filled in, so skip first inputwidget
 		for (int i = 0; i < values.length; i++)
 		{
 			if (inputWidget[i+1] instanceof TextBox)
 				((TextBox) inputWidget[i+1]).setText(values[i]);
 			else if (inputWidget[i+1] instanceof ListBox)
 			{
 				ListBox lb = (ListBox) inputWidget[i+1];
 				for (int j = 0; j < lb.getItemCount(); j++)
 				{
 					if (lb.getItemText(j).equalsIgnoreCase(values[i]))
 					{
 						lb.setSelectedIndex(j);
 						continue;
 					}
 				}
 			}
 			((FocusWidget) inputWidget[i+1]).setEnabled(false);	//make clear that these values cannot be changed!
 		}
 		parent.hideHourglass();
 	}
 	
 	/**	Creates a form, and presents it to the user.
 	 *	@param nl NL-expression of class
 	 *	@param classType Class name
 	 */
 	public void makeForm(String nl, final String classType)
 	{	
 		if (formInformation.length == 0)
 		{	//no properties have to be specified, so add the new instance to summary
 			tempData = new InstanceData(classType, 0);
 			newInstances.put("A new " + nl, tempData);
 			tempData = null;
 			currentRange.add("A new " + nl);
 			updateHtmlPanel();
 			parent.hideHourglass();
 			return;
 		}

 		formPP = new InputPopupPanel();
   		formPP.setStyleName("wysiwym-popup-tree");
   		VerticalPanel panel = new VerticalPanel();
   		panel.setSpacing(20);
   		Label l = new Label("Please specify the following information:");
   		l.setStyleName("wysiwym-label-large");
   		panel.add(l);
   		
   		HorizontalPanel hp = new HorizontalPanel();
   		inputWidget = new Widget[formInformation.length];
   		VerticalPanel left = new VerticalPanel();
   		VerticalPanel right = new VerticalPanel();
   		left.setSpacing(20);
   		right.setSpacing(20);
   		
   		for (int i = 0; i < formInformation.length; i++)
   		{			
   			left.add(new Label(formInformation[i].getNL()));
   			String[] values = formInformation[i].getValues();
   			if (values.length == 0)	//free-text value, so add a textbox
   			{
   				if (i > 0)
	   				inputWidget[i] = new TextBox();
	   			else	//the first will be the name/title property (if it exists)
	   			{
	   				inputWidget[i] = suggestBox;	//new SuggestBox(oracle);
	   				suggestBox.setText("");
	   			}
	   		}
   			else
   			{	//list of restricted values, so create a listbox
   				ListBox lb = new ListBox();
   				for (int j = 0; j < values.length; j++)
   					lb.addItem(values[j]);
   				inputWidget[i] = lb;
   			}
   			right.add(inputWidget[i]);
   		}
   		hp.add(left);
   		hp.add(right);
   		panel.add(hp);
   		
   		HorizontalPanel btnPanel = new HorizontalPanel();
   		btnPanel.setSpacing(20);
   		Button ok = new Button("OK", new ClickListener()
   		{
   			public void onClick(Widget sender)
   			{	
   				parent.showHourglass();
   				storeNewInstance(classType);
   			}
   		});
   		Button cancel = new Button("Cancel", new ClickListener()
   		{
   			public void onClick(Widget sender)
   			{
   				formPP.hide();
   				selectedCloudIndex = -1;
   			}
   		});
   		
   		Button h = new Button("Help!", new ClickListener()
		{
			public void onClick(Widget sender)
			{
				help.showFormHelp();
			}
		});
   		btnPanel.add(ok);
   		btnPanel.add(cancel);
   		btnPanel.add(h);
   		panel.add(btnPanel);
   		formPP.setWidget(panel);
		formPP.setPopupPosition(150, 30);
		formPP.show();

		parent.hideHourglass();
		if (inputWidget[0] instanceof FocusWidget)
		{
			((FocusWidget) inputWidget[0]).setFocus(true);
			if (inputWidget[0] instanceof TextBox)
   				((TextBox) inputWidget[0]).setCursorPos(0);
   		}
		else if (inputWidget[0] instanceof SuggestBox)
			((SuggestBox) inputWidget[0]).setFocus(true);		
 	}
 	
 	/**	Stores the data supplied by the user in an InstanceData.
 	 *	@param classType Class name
 	 */
 	public void storeNewInstance(String classType)
 	{	//check if the user has selected a value from the tag cloud, and stuck with it
 		if ((selectedCloudIndex >= 0) && suggestBox.getText().equalsIgnoreCase(cloud.getValue(selectedCloudIndex)))
 		{
 			String value = cloud.getValue(selectedCloudIndex);
 			if (chosenFromArchive.containsKey(value))
 			{
 				int cntr = 2;
	 			while (true)
 				{	//make sure the key is unique (e.g. 'John Smith (2)'
 					String v = value + "(" + Integer.toString(cntr++) + ")";
 					if (!chosenFromArchive.containsKey(v))
 					{
 						value = v;
 						break;
 					} 				
 				}
 			}
 			
 			currentRange.add(value);
 			chosenFromArchive.put(value, cloud.getID(selectedCloudIndex));
 			selectedCloudIndex = -1;
 			updateHtmlPanel();
 			formPP.hide();
 			parent.hideHourglass();
 			return;
 		 }
 		 	
 		//check if all input has been supplied, then store it and add a reference to summary
   		tempData = new InstanceData(classType, inputWidget.length);
   		for (int i = 0; i < inputWidget.length; i++)
   		{
   			String text = "";
   			if (inputWidget[i] instanceof TextBox)
   				text = ((TextBox)inputWidget[i]).getText();
   			else if (inputWidget[i] instanceof SuggestBox)
   				text = ((SuggestBox)inputWidget[i]).getText();		
   			else if (inputWidget[i] instanceof ListBox)
   			{
   				ListBox lb = (ListBox) inputWidget[i];
   				if (lb.getSelectedIndex() >= 0)
   					text = lb.getItemText(lb.getSelectedIndex());
   			}
   				
   			if (text.length() == 0)
   			{
   				Window.alert("Please provide or select a value for all properties.");
   				parent.hideHourglass();
   				return;	
   			}
   			tempData.add(formInformation[i].getProperty(), text, i);
   		}

   		formPP.hide();   	
   		checkDatabase(tempData);	//check if there is an object with this description in the archive
   	}
   	
   	/**	Adds a representation of the newly created object in summaryLB
   	 */
   	public void addValue()
   	{	//create a header and put it in summary
   		String firstProp = tempData.getProperty(0);
   		StringBuffer key = new StringBuffer("The new ");
   		key.append(tempData.getType());
   		if (firstProp.equals("Name") || firstProp.equals("Title"))
   			key.append(" \"" + tempData.getValue(0) + "\"");

   		int cntr = 2;
   		while (newInstances.containsKey(key.toString()))
   		{	//make sure there are no two entries with the same title
   			if (cntr == 2)
   				key.append(" (2)");
   			else
   				key.replace(key.length() - 2, key.length() - 1, Integer.toString(cntr));
   			cntr++;
   		}
   		newInstances.put(key.toString(), tempData);	//then store the collected data
   		currentRange.add(key.toString());
   		updateHtmlPanel();
   		tempData = null;
   		parent.hideHourglass();
 	}
 	
 	private void displayOptions(String[] options, boolean automatic)
 	{
 		new ArchiveOptionsPresenter(options, parent, this, tab, automatic);
 	}
 	
 	/**	Sends the elicited information to the server
 	 */
 	private void update()
 	{
 		parent.showHourglass();
 		hide();
 		String user = parent.getUser();
		if (user == null)
			return;
		
		InstanceData[] range = new InstanceData[currentRange.size()]; //[summaryLB.getItemCount()];
		for (int i = 0; i < currentRange.size(); i++)
		{
			String key = (String) currentRange.get(i);
			if (oldRange.containsKey(key))
				range[i] = new InstanceData((String) oldRange.get(key));
			else if (newInstances.containsKey(key))
				range[i] = (InstanceData) newInstances.get(key);
			else if (chosenFromArchive.containsKey(key))
				range[i] = new InstanceData((String) chosenFromArchive.get(key));
		}

 		parent.getHandler().updateObjectProperty(user, anchor, property, range, type, key, new AsyncCallback()
 		{
 			public void onSuccess(Object o) 
   			{
   				if (o == null)
   				{
   					Window.alert("There was an error while updating the feedback text. Please try again.");
   					parent.hideHourglass();
   					return;
   				}
   				AnchorInfo[] result = (AnchorInfo[]) o;
   				if (result.length == 0)
   				{
   					Window.alert("I'm afraid your session has expired. Please return to ourSpaces to start a new session.");
   					parent.close();
   				}
   				else
	   				tab.initPanel(result);
   			}
   	
   			public void onFailure(Throwable caught) 
   			{
   				parent.hideHourglass();
   				Window.alert(caught.getMessage());
   			}
 		});
 	}
 	
 	/**	Click listener, handles all button events. If cancel, hide the pop-up;
 	 *	if help, show help window; if reset, remove all values; if addNew, show
 	 *	the class hierarchy; and if ok, check that some values were added and 
 	 *	update the feedback text.
 	 *	@param sender Button Widget
 	 */
   	public void onClick(Widget sender)
   	{
   		buttonsPressed++;
   		if (sender == okBtn)
   		{
   			if (currentRange.size() == 0)	//(summaryLB.getItemCount() == 0)
   				Window.alert("Please specify some item(s) that apply to the property '" + nlname + "'.");
   			else
   				update();
   		}
   		else if (sender == cancelBtn)
   			hide();  			
   		else if (sender == helpBtn)
   			help.showObjectPropertyHelp();
   		else if (sender == addNewBtn)
   			showTree();
   		else if (sender == resetBtn)
   		{
   			currentRange = new ArrayList(oldRange.keySet());
   			updateHtmlPanel();
   		}
   	}
 }