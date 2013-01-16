package liber.browse.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Command;

import java.util.List;
import java.util.ArrayList;
import java.lang.StringBuffer;

/**
 * 	Executed when a menu item from PopupMenu is selected. Various widgets can be
 *	shown to elicit values from the user, depending on the type of property selected.
 *	Information (if necessary) is send to the server and the feedback text is regenerated.
 *
 *	@author		Feikje Hielkema
 *	@version	1.2 March 26, 2008
 */
public class BrowseCommand extends WysiwymCommand
{
	/**	Constructor
	 *	@see WysiwymCommand#WysiwymCommand(String,String,String,WysiwymTab,WYSIWYMinterface,PopupMenu)
	 */
	public BrowseCommand(String p, String nl, String i, WysiwymTab parent, WYSIWYMinterface wysInt, PopupMenu m)
	{
		super(p, nl, i, parent, wysInt, m);
		type = BROWSE;
	}
}