package liber.edit.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Command;

import java.util.List;
import java.util.ArrayList;
import java.lang.StringBuffer;
 
/**
 * 	Executed when an 'download' menu item from a PopupMenu is selected. 
 *
 *	@author	Feikje Hielkema
 *	@version 1.2 March 17, 2008
 *	@version 1.5 August 2008
 */
public class DownloadCommand implements Command
{
	protected String key, title, uri;
	private PopupMenu menu;
	
	/**	Constructor
	 *	@param u String URL where resource can be downloaded
	 *	@param nl Window title (title of resource)
	 *	@param m PopupMenu
	 */
	public DownloadCommand(String u, String nl, PopupMenu m)
	{
		uri = u;
		title = nl;
		menu = m;
	}

	public void execute()
	{	//if it's a datatype, let the user specify a value, then update the feedback text
		menu.hide();
		Window.open(uri, title, null);
	}
}