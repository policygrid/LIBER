package WYSIWYM.transformer;

import WYSIWYM.model.*;
import WYSIWYM.ontology.*;
import WYSIWYM.libraries.*;
import WYSIWYM.util.*;
import simplenlg.realiser.AnchorString;
import java.util.*;
import javax.naming.NameAlreadyBoundException;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import com.hp.hpl.jena.ontology.OntProperty;
import java.net.MalformedURLException;

/**
 *	BrowsingPlanner plans the text produced for a browsing user.
 *
 * @author Feikje Hielkema
 *
 *	@version 1.2 25-03-2007
 *
 *	@deprecated	
 */

public class BrowsingPlanner extends TextPlanner
{
	private String paragraph;
	
	/**	Constructs an empty text planner, taking only an ontology reader
	 *
	 *	@param r Ontology
	 */
    public BrowsingPlanner(OntologyReader r) 
    {
    	super(r);
    }
    
    /**	Constructs a text planner with a SemanticGraph
	 *
	 *	@param r Ontology
	 *	@param g SemanticGraph	
	 */
    public BrowsingPlanner(OntologyReader r, SemanticGraph g) 
    {
    	super(r, g);
    }
}	