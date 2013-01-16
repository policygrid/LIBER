package liber.edit.server;

import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.io.PrintWriter;
import java.io.IOException;
import WYSIWYM.testclasses.DescriptionCompiler;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**	Descriptions2 is used by ourSpaces to produce small descriptions of
 *	users with the given names, for quick identification. It receives HttpRequests
 *	with first and last name of the user, and sends XML as response.
 *
 *	Other than Descriptions, this class returns descriptions of all users,
 *	registered and unregistered.
 *
 *	@author Feikje Hielkema
 *	@version 1.4 October 2009
 */
public class Descriptions2 extends HttpServlet implements Servlet
{
	/**	Receives the first and last name from ourSpaces, and returns descriptions
	 *	of unregistered usres with those names.
	 *	@param request HttpServletRequest 
	 *	@param response HttpServletResponse
	 *	@throws ServletException
	 *	@throws IOException
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		doGet(request,response);
	}
	
	/**	Receives the first and last name from ourSpaces, and returns descriptions
	 *	of unregistered usres with those names.
	 *	@param request HttpServletRequest 
	 *	@param response HttpServletResponse
	 *	@throws ServletException
	 *	@throws IOException
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try
		{
			PrintWriter out = response.getWriter();
			String cmd = request.getParameter("action");
			if (cmd != null) 
        	{
        	    if (cmd.equals("userinfo")) 
        	    	getDescriptions(request, response, out);
            	else
            		out.flush();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**	Retrieves descriptions about unregistered users with this first and last name
	 */
	private void getDescriptions(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		String first = request.getParameter("firstname"); 
		String last = request.getParameter("lastname");
		Map<String, String> result = new DescriptionCompiler().getDescriptions(first, last, true);		
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		
		out.println("<root>");
		for (Iterator it = result.keySet().iterator(); it.hasNext(); )
		{
			String id = (String) it.next();
			out.println("<user>");
			out.println("<info>" + result.get(id) + "</info>");
			out.println("<id>" + id + "</id>");
			out.println("</user>");
		}
		out.println("</root>");
	}
}
