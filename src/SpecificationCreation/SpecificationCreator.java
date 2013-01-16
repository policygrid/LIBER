package SpecificationCreation;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import SpecificationCreation.ontology.*;
import SpecificationCreation.look.*;
import WYSIWYM.util.OntologyInputException;
import liber.edit.client.Hierarchy;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;

/**	This is a stand-alone desktop tool that reads an ontology and enables the 
 *	user to adapt it for use in LIBER, by creating linguistic specifications,
 *	making class- and property names more nlg-like, adding cardinality 
 *	constraints, defining which properties should be visible to end-users,
 *	and shaping the pop-up menus.
 *
 *	@author 	Feikje Hielkema
 *	@version	1.0 17-10-2008
 */
 public class SpecificationCreator extends JFrame implements ActionListener
 {
 	public static final String BASEURI = "base-uri.txt";
 	private Button nextBtn = new Button("Next >>");
	
	private JTree classTree;
	private JMenuBar topMenu;
	private JMenuItem newItem, openItem, saveItem, exitItem;
 	private JFileChooser fileChooser;
 	private String directory;
 	
 	private SpecificationOntologyReader reader;
 	private java.util.List<CheckedHierarchy> hierarchy = null;
 	private java.util.List<String> submenus = new ArrayList<String>();
 	private ClassPane classPane;
 	
 	/**	Default constructor
 	 */
 	public SpecificationCreator()
 	{
 		super("LIBER ontology preparation");
 		setBounds(100, 100, 800, 600);
 		setResizable(true);
		setBackground(Color.white);
		addWindowListener(new SaveBeforeClosing());
 		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		Container content = getContentPane();
		content.setLayout(new GridBagLayout());
   		content.setBackground(Color.white);
   		((JPanel)content).setOpaque(true);
		init(content);
	}
 	
 	/**	Asks the user if he wants to save before closing, with options yes 
 	 *	(data is saved and program closed), no (program is closed) and cancel
 	 *	(nothing happens).
 	 */
 	private class SaveBeforeClosing extends WindowAdapter
 	{
 		public void windowClosing(WindowEvent e)
 		{
 			saveAndExit();
 		}
 	}
 	
 	private void saveAndExit()
 	{
 		if (reader == null)	//no project open yet, so just exit
 			System.exit(0);
 			
 		int answer = JOptionPane.showConfirmDialog(SpecificationCreator.this, "Do you want to save the changes you made before exiting this program?",
 			"Saving your work", JOptionPane.YES_NO_CANCEL_OPTION);
 			
 		switch(answer)
 		{
 			case JOptionPane.YES_OPTION: save(); System.exit(0);
 			case JOptionPane.NO_OPTION: System.exit(0);
 		}	//default case do nothing
	}
 	
 	private void save()
 	{
 		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
 		reader.save(directory + "ontology.rdf");
 		try
 		{
	 		FileWriter fw = new FileWriter(directory + BASEURI);
			PrintWriter w = new PrintWriter(fw);
			w.print(reader.getBaseURI());
			w.close();
			fw.close();
 		}
 		catch(IOException e)
 		{
 			e.printStackTrace();
 		}
 		setCursor(Cursor.getDefaultCursor());
 	}
 	
 	/**	Creates the initial welcome and starting screen
	 */
	private void init(Container c)
	{
		topMenu = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_ALT);
		topMenu.add(fileMenu);
		
		newItem = new JMenuItem("New project", KeyEvent.VK_N);
		newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		newItem.addActionListener(this);
		fileMenu.add(newItem);
		openItem = new JMenuItem("Open project", KeyEvent.VK_O);
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		openItem.addActionListener(this);
		fileMenu.add(openItem);
		saveItem = new JMenuItem("Save project", KeyEvent.VK_S);
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveItem.addActionListener(this);
		fileMenu.add(saveItem);
		exitItem = new JMenuItem("Exit", KeyEvent.VK_Q);
		exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		exitItem.addActionListener(this);
		fileMenu.add(exitItem);
		setJMenuBar(topMenu);
		
		GridBagConstraints constraint = new GridBagConstraints();
		JTextArea area = new JTextArea("Welcome to the LIBER ontology preparation tool. " +
			"Please open an existing project, or start a new project by uploading the ontology you wish to adapt for use in LIBER.");
		area.setEditable(false);
		area.setLineWrap(true);
 		area.setWrapStyleWord(true);
		constraint.insets = new Insets(0, 10, 10, 0);
		constraint.anchor = GridBagConstraints.CENTER;
		constraint.gridx = 0;
		constraint.gridy = 0;
		c.add(area, constraint);
	}
	 	
 	/**	Reads an ontology from the selected file.
 	 */
 	private boolean readOntology(File file, String baseURI)
 	{
 		try
 		{
 			System.out.println("reading ontology");
	 		reader = new SpecificationOntologyReader(file, baseURI);
	 		setCursor(Cursor.getDefaultCursor());
	 		System.out.println("true");
	 		return true;
	 	}
	 	catch (OntologyInputException e)
	 	{
	 		System.out.println("false");
	 		System.out.println(e.getMessage());
	 		e.printStackTrace();
	 		return false;
	 	}
 	}
 	
 	/**	Makes a directory in which the adapted ontology and all specifications 
 	 *	will be stored, unless that directory already exists.
 	 */
 	private void makeDir(String filename)
 	{
 		int start = filename.lastIndexOf("/");
	 	if (start < 0)
	 		start = filename.lastIndexOf("\\");
	 	int end = filename.lastIndexOf(".");
	 	if (start < 0)
	 		filename = filename.substring(0, end);
	 	else
	 		filename = filename.substring(start + 1, end);
	 	
	 	directory = "data/" + filename + "/";
	 	File dir = new File(directory);
	 	if (!dir.exists())
	 		dir.mkdir();
	 	save();
 	}
 	
 	/**	Initialises a screent that shows the ontology's class hierarchy.
 	 *	The user can select a class to proceed to add linguistic information
 	 *	about it.
 	 */
 	public void getClassHierarchyPane()
 	{
 		Container content = getContentPane();
 		content.removeAll();
 		
 		GridBagConstraints constraint = new GridBagConstraints();
 		JTextArea area = new JTextArea("The class hierarchy of your ontology is displayed below. " + 
 		"Each of these classes must be adapted for LIBER. To adapt a class, select it in the tree and click 'next'. " +
 		"Classes that have been wholly adapted are marked with an 'ok' icon; classes that are imported from another " +
 		"ontology, and therefore cannot be adapted now, are marked in grey. When each class has an 'ok' sign, the adaptation is finished.");
 		area.setEditable(false);
 		area.setLineWrap(true);
 		area.setWrapStyleWord(true);
 		constraint.insets = new Insets(10, 10, 10, 10);
 		constraint.fill = GridBagConstraints.HORIZONTAL;
		constraint.anchor = GridBagConstraints.CENTER;
		constraint.weightx = 1.0;
 		constraint.gridwidth = 2;
		constraint.gridx = 0;
		constraint.gridy = 0;
		content.add(area, constraint);
 		
		constraint.weightx = 0;
		constraint.weighty = 1.0;	//add extra space to the tree widget, so it can expand
		constraint.anchor = GridBagConstraints.FIRST_LINE_START;
		constraint.gridx = 0;
		constraint.gridy = 1;
		constraint.fill = GridBagConstraints.BOTH;		
		classTree = getTree();
		JScrollPane scroll = new JScrollPane(classTree);
 		content.add(scroll, constraint);
 		
 		constraint.fill = GridBagConstraints.NONE;
 		constraint.anchor = GridBagConstraints.PAGE_START;
 		constraint.weighty = 0;	
 		constraint.weightx = 0;
 		constraint.gridx = 0;
 		constraint.gridy = 2;
 		constraint.gridwidth = 1;
 		nextBtn.addActionListener(new ActionListener()
 		{
 			public void actionPerformed(ActionEvent e)
 			{
 				getClassPane(classTree.getSelectionPath());
 			}
 		});
 		content.add(nextBtn, constraint);
 		
 		content.validate();
 	}
 	
 	/**	Returns the class hierarchy tree
 	 */
 	private JTree getTree()
 	{
 		if (hierarchy == null)	
	 		hierarchy = reader.getCheckedClassHierarchy();	//get the class hierarchy
 	
 		DefaultMutableTreeNode root = new ClassTreeNode("Owl:Thing", true, false);
 		for (int i = 0; i < hierarchy.size(); i++)
 			root.add(getTreeNode(hierarchy.get(i)));	
	
 		classTree = new JTree(root);
 		classTree.setCellRenderer(new TreeRenderer("images/ok.png"));
 		classTree.addMouseListener(new MouseAdapter()
 		{
 			public void mousePressed(MouseEvent e)
 			{
 				int selRow = classTree.getRowForLocation(e.getX(), e.getY());
        		TreePath path = classTree.getPathForLocation(e.getX(), e.getY());
         		if ((selRow != -1) && (e.getClickCount() == 2))	//if the user double clicked this node
                	getClassPane(path);				//that counts as selected
 			}
 		});
   		return classTree;
 	}
 	
 	private DefaultMutableTreeNode getTreeNode(CheckedHierarchy h)
 	{
 		DefaultMutableTreeNode node = new ClassTreeNode(h.getValue(), h.isShaded(), isCompleted(h.getValue()));
 		for (int i = 0; i < h.size(); i++)
 			node.add(getTreeNode(h.getSub(i)));
 		return node;
 	}
 	
 	/**	Checks whether the class contains the minimum amount of linguistic information
 	 *	it needs. If true, it will receive an ok-sign to show it's completed.
 	 */
 	private boolean isCompleted(String name)
 	{
 		Map<String,Integer[]> cardinalityMap = reader.getCardinalities(name);
 		for (OntProperty p : reader.getDomainProperties(name))
 		{
 			String property = p.getLocalName();
 			OntProperty inverse = reader.getInverse(p);
 			File spec = new File(directory + property + ".xml");
 			if (!spec.exists() && (inverse != null))
 				spec = new File(directory + inverse.getLocalName() + ".xml");
 			int card = 0;
 			if (cardinalityMap.containsKey(property))
				card = cardinalityMap.get(property)[0];
 			if ((card == 0) && (!reader.hasComment(p, SpecificationOntologyReader.MENU)))
 				continue;	//skip properties that do not show in interface!
			if (!(spec.exists() || reader.hasComment(p, SpecificationOntologyReader.PROPERNAME)))
				return false;
 		}
 		return true;
 	}
 	
 	private void getClassPane(TreePath path)
 	{
 		if (path == null)
 			return;
 		ClassTreeNode node = (ClassTreeNode) path.getLastPathComponent();
 		if (node == null)
 			return;
 		classPane = new ClassPane(node.toString(), getContentPane(), reader, this, node.isShaded());
 	}
 	
 	public String getDirectory()
 	{
 		return directory;
 	}
 	
 	/**	Action listener
 	 *	@param e ActionEvent
 	 */
 	public void actionPerformed(ActionEvent e)
 	{
 		Object source = e.getSource();
 		if ((source == saveItem) && (reader != null))
 			save();
 		else if (source == exitItem)
 			saveAndExit();
 		else
 		{
 			if (reader != null)
 			{	//ask user if he wants to save before switching projects
 				int answer = JOptionPane.showConfirmDialog(SpecificationCreator.this, "Do you want to save the changes you made before exiting this program?",
 					"Saving your work", JOptionPane.YES_NO_CANCEL_OPTION);
 				if (answer == JOptionPane.CANCEL_OPTION)
 					return;
 				if (answer == JOptionPane.YES_OPTION)
 					save();
 				reader = null;
 			}
 			
 			fileChooser = new JFileChooser(new File("data/"));
 			boolean existing = (source == openItem);	// || (source == openBtn));
 			if (existing)
 				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
 			else
 				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
 				
	 		int returnVal = fileChooser.showOpenDialog(SpecificationCreator.this);
		    if (returnVal == JFileChooser.APPROVE_OPTION)
    		{
	   			try
  	   			{
  	   				if (existing)
  	   				{
  	   					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
  	   					directory = fileChooser.getSelectedFile().getPath() + "\\";
  	   					System.out.println(directory);
  	   					FileReader reader = new FileReader(directory + BASEURI);
  	   					BufferedReader b = new BufferedReader(reader);
  	   					String baseURI = b.readLine();
  	   					b.close();
  	   					reader.close();
  	   					
  	   					if (readOntology(new File(directory + "ontology.rdf"), baseURI))
   	   	    			{
							JOptionPane.showMessageDialog(SpecificationCreator.this, "Your ontology was read successfully.",
   	   	    					"Finished reading ontology", JOptionPane.INFORMATION_MESSAGE);
	   	   	    			getClassHierarchyPane();
	   	   	    		}	
	   	   	    		else
	   	   	    		{
	   	   	    			setCursor(Cursor.getDefaultCursor());
	   	   	    			JOptionPane.showMessageDialog(SpecificationCreator.this, 
	   	   	  	 	 			"There was an error reading your ontology. Please check the file and the base URI.", "Error when reading ontology", JOptionPane.ERROR_MESSAGE);  					
	   	   	    		}
  	   				}
  	   				else
  	   				{
  	   					String baseURI = JOptionPane.showInputDialog(SpecificationCreator.this, "Please enter the base URI of this particular ontology:", 
   	   	    				"Base URI", JOptionPane.QUESTION_MESSAGE);
   	   	    		
   	   	    			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
   	   	    			File f = fileChooser.getSelectedFile();
   	   	    			if (readOntology(f, baseURI))
   	   	    			{
   	   	    				makeDir(f.getName());
   	   	    				JOptionPane.showMessageDialog(SpecificationCreator.this, "Your ontology was read successfully.",
   	   	    					"Finished reading ontology", JOptionPane.INFORMATION_MESSAGE);
	   	   	    			getClassHierarchyPane();
	   	   	    		}	
	   	   	    		else
	   	   	    		{
	   	   	   				setCursor(Cursor.getDefaultCursor());
	   	   	    			JOptionPane.showMessageDialog(SpecificationCreator.this, 
	   	   	  	 	 			"There was an error reading your ontology. Please check the file and the base URI.", "Error when reading ontology", JOptionPane.ERROR_MESSAGE);
	   	   	    		}
  	   				}
   	   	    	}
   	   	    	catch(Exception ex)
   	   	    	{
   	   	    		ex.printStackTrace();
   	   	    		setCursor(Cursor.getDefaultCursor());
   	   	    		JOptionPane.showMessageDialog(SpecificationCreator.this, 
	   	   		   		"There was an error reading your ontology. Please check the file and the base URI.", "Error when reading ontology", JOptionPane.ERROR_MESSAGE);
   	   	    	}
   	   		}
   	   }
 	}
 	
 	/**	Main method
 	 */
 	public static void main(String[] args)
 	{
 		new SpecificationCreator().setVisible(true);
 	}
 }