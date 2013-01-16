package liber.edit.client;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
 
/**
 * 	Provides help by showing explanatory diagrams for various windows.
 *
 *	@author Feikje Hielkema
 *	@version 1.1 November 21st, 2007
 *	@version 1.3 May 30th, 2008
 */
public class Help extends MyDialogBox
{
	private WYSIWYMinterface parent;
	private int type;
	
	/**	Constructor
	 *	@param p Entrypoint
	 *	@param t Session type (edit, query, browse)
	 */
	public Help(WYSIWYMinterface p, int t)
	{	
		super(false, false);
		type = t;
		parent = p;
		setPopupPosition(Window.getClientWidth() / 2, 50);
		setHeight(500);
		setWidth(500);
	}

	private void show(String title, Widget widget)
	{
		setText(title);
		setWidget(widget);
		show();
		String user = parent.getUser();
		if (user == null)
			return;
	}

	/**	Shows a diagram for the main window of a module.
	 */
	public void showTabHelp()
	{
		VerticalPanel panel = new VerticalPanel();
		panel.setSpacing(10);
		Image image = new Image();
		String title = "";
		
		if (type == WYSIWYMinterface.EDIT_TAB)
		{
			panel.add(new HTML("Please upload and describe your resources by editing the feedback text. " +
			"By clicking on the red, boldface and blue, italic phrases you can view pop-up menus that contain the different kinds of " +
			"information you can add about the corresponding object. For instance, to specify a person's email address, " +
			"click on that person's anchor and select 'has email'.<br>When you are satisfied with your description and no more red anchors " +
			"are left, you can submit your description."));
			image.setUrl("http://www.csd.abdn.ac.uk/~fhielkem/edittab.png");
			image.setSize("466px", "241px");
			title = "Using the 'Describe your resource' tab.";
		}
		else if (type == WYSIWYMinterface.QUERY_TAB)
		{
			panel.add(new HTML("The 'Search for resources' tab enables you to find resources in the archive by creating a detailed query. " +
			"By clicking on the blue, italic phrases you can view pop-up menus that contain the different kinds of " +
			"information you can use to search. For instance, to specify a paper's publication date, " +
			"click on that paper's anchor and select 'date of publication'. The query tab continually shows how many resources match your query." +
			"<br>When you are satisfied with your query, or there are but few matching resources" +
			", you can submit your description, after which the resources in the archive that match your query will be displayed." +
			"<br>The image below shows the functions of the different components of this tab."));
			image.setUrl("http://www.csd.abdn.ac.uk/~fhielkem/querytab.png");
			image.setSize("452px", "284px");
			title = "Using the 'Searching for resources' tab.";
		}
		else
		{
			panel.add(new HTML("The browsing panes enable you to view the full descriptions of resources and people in the archive. " +
			"By clicking on the blue, italic phrases you can view pop-up menus that options for retrieving additional information, " +
			"adding the anchor's object to the 'describe your resource' tab, and adding your own comments to existing descriptions." +
			"<br>If you add any comments, don't forget to submit them before closing the window." +
			"<br>The image below shows the functions of the different components of this tab."));
			image.setUrl("http://www.csd.abdn.ac.uk/~fhielkem/browsetab.png");
			image.setSize("490px", "346px");
			title = "Using the browsing panes.";
		}
		setPopupPosition(50, 50);
		panel.add(image);	
		show(title, panel);
	}
	
	/**	Shows a diagram for the option selection.
	 */
	public void showArchiveOptionsHelp()
	{
		VerticalPanel panel = new VerticalPanel();
		panel.add(new Label("This pane shows the objects in the archive that match your description. "));
		panel.setSpacing(10);
	
		Image image = new Image();
		image.setUrl("http://www.csd.abdn.ac.uk/~fhielkem/queryresult.png");
		image.setSize("333px", "139px");
		setPopupPosition(50, 50);
		panel.add(image);	
		show("Viewing the matching objects in the archive.", panel);
	}
	
	/**	Shows a diagram for a class hierarchy.
	 */
	public void showTreeHelp()
	{
		VerticalPanel panel = new VerticalPanel();
		panel.add(new Label("You are trying to create a new object. " +
			"But before you can start describing it, I need to know what kind of object it is: " + 
			"an academic paper, an interview transcript, a person?<br>" +
			"It is important that you are as specific as possible! " + 
			"The panel shows a tree of the possible types. The deeper a type is in the tree, the more specific it is."));
		panel.setSpacing(10);
	
		Image image = new Image();
		image.setUrl("http://www.csd.abdn.ac.uk/~fhielkem/objectpane3.png");
		image.setSize("538px", "438px");
		setPopupPosition(50, 50);
		panel.add(image);	
		show("Creating an object - specifying its type.", panel);
	}
	
	/**	Shows a diagram for creating an object with a form.
	 */
	public void showFormHelp()
	{
		VerticalPanel panel = new VerticalPanel();
		panel.add(new Label("You are trying to create a new object. " +
			"This form contains enables you to supply basic information such as its name or title. Please fill in the form and press 'ok'."));
		panel.setSpacing(10);
	
		Image image = new Image();
		image.setUrl("http://www.csd.abdn.ac.uk/~fhielkem/objectpane2.png");
		image.setSize("361px", "223px");
		setPopupPosition(50, 50);
		panel.add(image);	
		show("Creating a new object - the initial description.", panel);
	}
	
	/**	Shows a diagram for the object property pane.
	 */
	public void showObjectPropertyHelp()
	{
		VerticalPanel panel = new VerticalPanel();
		panel.add(new Label("The image below shows an example of someone using this pane to specify which people have presented a certain presentation."));
		panel.setSpacing(10);
		
		Image image = new Image();
		image.setUrl("http://www.csd.abdn.ac.uk/~fhielkem/objectpane1.png");
		image.setSize("538px", "298px");
		setPopupPosition(50, 50);
		panel.add(image);
		show("Specifying objects as values - an example", panel);
	}
	
	private TreeItem getTagCloudItem()
	{
		TreeItem tagcloud = new TreeItem("The tag cloud.");
		tagcloud.addItem(new TreeItem("The tag cloud appears when you are asked to specify tags " +
		"for the <i>property</i> you have selected. It contains the tags that other users have provided in this context, with font " +
		"size corresponding to the frequency with which each tag has been used. If you click on a tag in the cloud, that tag " +
		"will appear in your tag specification pane."));
		return tagcloud;
	}
	
	/**	Shows a diagram for adding a single value.
	 */
	public void showOneValueHelp(int datatype)
	{
		if (type == WYSIWYMinterface.QUERY_TAB)
			showQueryMultipleValueHelp(datatype);
		
		Tree tree = new Tree();
		StringBuffer sb = new StringBuffer("You have selected a menu item (or <i>property</i>) ");
		if (datatype < 6)
			sb.append("that takes a number as its value.<br>");
		else 
			sb.append("that can only have one value (e.g. a publication can only have one title).<br>");
	
		sb.append("Please specify a value or select one from the tag cloud, and click 'OK'.");
		
		tree.addItem(new TreeItem(sb.toString()));
		if (datatype == 6)
			tree.addItem(getTagCloudItem());
		
		VerticalPanel panel = new VerticalPanel();
		panel.add(tree);
		panel.setSpacing(10);
		
		Image image = new Image();
		image.setUrl("http://www.csd.abdn.ac.uk/~fhielkem/singlevalue.png");
		image.setSize("370px", "188px");
		setPopupPosition(50, 50);
		panel.add(image);
	
		show("Specifying a value.", panel);
	}
	
	/**	Shows a diagram for adding a query restriction with multiple values.
	 */
	public void showQueryMultipleValueHelp(int datatype)
	{
		Tree tree = new Tree();
		StringBuffer sb = new StringBuffer("You have selected a menu item (or <i>property</i>) ");
		if (datatype < 6)
			sb.append("that takes numbers as its values.<br>");
		else
			sb.append("that can take any number of words or phrases as its values (unless a maximum is specified).<br>");
		
		sb.append("Please add your values one by one by either typing them or selecting them from the tag cloud, " + 
		"and pressing '>>'. To remove a value, select it and press '&lt;&lt;'. When you are satisfied, click 'OK'.<br>");
		sb.append("If you add more than one value, please select 'and', 'or' or 'not' from the list, to indicate whether the object " + 
		"you are looking for should match all, some or none of the values you specified.");
		
		TreeItem item = new TreeItem(sb.toString());
		tree.addItem(item);
		if (datatype == 6)
			tree.addItem(getTagCloudItem());
		
		VerticalPanel panel = new VerticalPanel();
		panel.add(tree);
		panel.setSpacing(10);
		
		Image image = new Image();
		image.setUrl("http://www.csd.abdn.ac.uk/~fhielkem/querymultiplevalue.png");
		image.setSize("375px", "284px");
		setPopupPosition(50, 50);
		panel.add(image);
		show("Specifying values.", panel);
	}
	
	/**	Shows a diagram for selecting a value from a list.
	 */
	public void showListHelp()
	{
		if (type == WYSIWYMinterface.QUERY_TAB)
			showQueryMultipleValueHelp(0);
		
		Tree tree = new Tree();
		StringBuffer sb = new StringBuffer("You have selected a menu item (or property) " +
		"whose possible values are restricted to those you see in the list.<br>" );
		
		sb.append("Please select one or more values from the list and click 'OK'. You can select " +
			"more than one value by holding down 'Control' while you select with the mouse.<br>" +
			"If a 'maximum' is specified just above the list, this means you can add no more than "+
			"that amount of values.");
			
		tree.addItem(new TreeItem(sb.toString()));
		VerticalPanel panel = new VerticalPanel();
		panel.add(tree);
		panel.setSpacing(10);
		
		Image image = new Image();
		image.setUrl("http://www.csd.abdn.ac.uk/~fhielkem/list.png");
		image.setSize("306px", "122px");
		setPopupPosition(50, 50);
		panel.add(image);
		show("Choosing from restricted values.", panel);
	}
	
	/**	Shows a diagram for adding multiple values.
	 */
	public void showMultipleValueHelp(int datatype)
	{
		if (type == WYSIWYMinterface.QUERY_TAB)
			showQueryMultipleValueHelp(datatype);
		
		Tree tree = new Tree();
		StringBuffer sb = new StringBuffer("You have selected a menu item (or <i>property</i>) ");
		if (datatype < 6)
			sb.append("that takes numbers as its values.<br>");
		else
			sb.append("that can take any number of words or phrases as its values (unless a maximum is specified).<br>");
		
		sb.append("Please add your values one by one by either typing them or selecting them from the tag cloud, " + 
		"and pressing '>>'. To remove a value, select it and press '&lt;&lt;'. When you are satisfied, click 'OK'.");
		
		TreeItem item = new TreeItem(sb.toString());
		tree.addItem(item);
		if (datatype == 6)
			tree.addItem(getTagCloudItem());
		
		VerticalPanel panel = new VerticalPanel();
		panel.add(tree);
		panel.setSpacing(10);
		
		Image image = new Image();
		image.setUrl("http://www.csd.abdn.ac.uk/~fhielkem/multiplevalue.png");
		image.setSize("370px", "322px");
		setPopupPosition(50, 50);
		panel.add(image);
		show("Specifying values.", panel);
	}

	/**	Shows a diagram for adding a date.
	 */	
	public void showDateHelp()
	{
		Tree tree = new Tree();
		StringBuffer sb = new StringBuffer("You have selected a menu item (or property) " +
		"that takes one or more dates as its values.<br>These dates can be specific " +
		"(e.g. '5 February 2006') or periods (e.g. 'February 2006', '2002', '2000-2007', '80's', '20th century', 'Mesozoic'). " +
		"If you don't know the precise start days or months of a period, you can leave them blank.<br>"); 
		
		if (type == WYSIWYMinterface.QUERY_TAB)
			sb.append("<br>You can specify whether the dates you want should be before, after, on or during the provided date. " +
			"You can also specify whether all, at least one or none of the requirements you provide should be true" +
			", by selecting 'and', 'or' or 'not' from the list.");
		else
			sb.append("<br>Please add your dates one by one, pressing 'add another date' to add another and 'OK' when finished.");
		
		tree.addItem(new TreeItem(sb.toString()));
		VerticalPanel panel = new VerticalPanel();
		panel.add(tree);
		panel.setSpacing(10);
		
		Image image = new Image();
		if (type == WYSIWYMinterface.QUERY_TAB)
		{
			image.setUrl("http://www.csd.abdn.ac.uk/~fhielkem/querydate.png");
			image.setSize("442px", "265px");
		}
		else
		{
			image.setUrl("http://www.csd.abdn.ac.uk/~fhielkem/date.png");
			image.setSize("375px", "289px");
		}
			
		setPopupPosition(50, 50);
		panel.add(image);		
		show("Specifying a date.", panel);
	}
	
	/**	Shows a diagram for the main class hierarchy.
	 */
	public void showResourceTypeHelp()
	{
		VerticalPanel panel = new VerticalPanel();
		panel.setSpacing(10);
		if (type == WYSIWYMinterface.EDIT_TAB)
			panel.add(new HTML("You are about to deposit a resource. " +
			"But before you can start describing it, you have to specify its type: " + 
			"an academic paper, an interview transcript, a questionnaire design?<br>" +
			"It is important that you are as specific as possible! " + 
			"The panel shows a tree of the possible types. The deeper a type is in the tree, the more specific it is."));
		else
			panel.add(new Label("In this pane you can specify what kind of object you are searching for. " + 
			"The more specific the type you choose (i.e. the deeper it is in the tree), the more requirements you can specify about it."));
		
		Image image = new Image();
		image.setUrl("http://www.csd.abdn.ac.uk/~fhielkem/tree.png");
		setPopupPosition(50, 50);
		image.setSize("260px", "363px");
		panel.add(image);	
	
		show("Specifying the resource type.", panel);
	}
	
	/**	Shows a diagram for the undo pane.
	 */
	public void showUndoHelp()
	{
		VerticalPanel panel = new VerticalPanel();
		panel.add(new HTML("You have stated that you want to remove some information (or property). " +
		"This panel enquires whether you wish to remove all values associated with this property, " + 
		"or only a few.<br>To remove all values, press 'Remove all values'; to remove only certain values, " +
		"select them from the list and press 'Remove selected values'. You can select " +
		"more than one value by holding down 'Control' while you select with the mouse."));
		panel.setSpacing(10);
	
		Image image = new Image();
		image.setUrl("http://www.csd.abdn.ac.uk/~fhielkem/undo.png");
		image.setSize("188px", "83px");
		setPopupPosition(50, 50);
		panel.add(image);	
		show("Removing values.", panel);
	}
	
	/**	Shows a diagram for the 'select more specific type' pane.
	 */
	public void showSpecificTypeHelp()
	{
		Tree tree = new Tree();
		tree.addItem(new TreeItem("The type you have chosen has some <i>subtypes</i>, i.e. more specific types " +
		"(e.g. a 'conference paper' is more specific than a 'paper'). The more specific a type is, " +
		"the more you can say about it (for instance, a conference paper has a place and name of the conference).<br>" +
		"However, if you cannot be more specific than the type you chose originally (e.g. if you have a 'transcript' "+
		"that is not a coded transcript), then you can stay with your original choice."));
		setHeight(300);
		show("What type should I choose?", tree);
	}
}