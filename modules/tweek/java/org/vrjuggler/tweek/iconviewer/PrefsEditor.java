package org.vrjuggler.tweek.iconviewer;

import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.*;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.XMLOutputter;


public class PrefsEditor extends JPanel
{
   public PrefsEditor()
   {
      try
      {
         jbInit();
      }
      catch(Exception e)
      {
         e.printStackTrace();
      }

      mPrefsFileName = System.getProperty("user.home") + File.separator +
                       ".tweek-iconviewer-prefs";
   }

   public boolean isSmallGui()
   {
      return smallGui;
   }

   public void hasSmallGui(boolean smallGui)
   {
      this.smallGui = smallGui;
   }

   public String getName()
   {
      return "Icon View Preferences";
   }

   public void load()
   {
      File prefs_file = new File(mPrefsFileName);

      if ( prefs_file.exists() )
      {
         org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder();

         try
         {
            mPrefsDoc = builder.build(prefs_file);

            Element root = mPrefsDoc.getRootElement();
            Element gui_element = root.getChild("gui");

            smallGui = gui_element.getAttribute("small").getBooleanValue();
         }
         catch (JDOMException e)
         {
            e.printStackTrace();
         }
      }
      else
      {
         mPrefsDoc = new Document();
         Element root = new Element("IconViewerPrefs");
         Element gui_element = new Element("gui");
         gui_element.setAttribute("small", String.valueOf(smallGui));
         root.addContent(gui_element);
         mPrefsDoc.setRootElement(root);

         // Now that we have a document in memory, write it to disk.
         save();
      }
   }

   public void save()
   {
      XMLOutputter outputter = new XMLOutputter("  ", true);
      outputter.setLineSeparator(System.getProperty("line.separator"));

      try
      {
         FileWriter writer = new FileWriter(mPrefsFileName);
         outputter.output(mPrefsDoc, writer);
         writer.close();
      }
      catch (java.io.IOException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Adds a new listener for preferences events.
    *
    * @param l The object that wishes to listen for preferences events.
    */
   public synchronized void addPrefsEventListener(PrefsEventListener l)
   {
      this.mListeners.add(l);
   }

   /**
    * Removes the given given listener from this object. If the object is not
    * already a listener of this object, no work is done.
    *
    * @param l The listener to remove from this object.
    */
   public synchronized void removePrefsEventListener(PrefsEventListener l)
   {
      this.mListeners.remove(l);
   }

   protected void firePrefsChanged()
   {
      PrefsEvent evt = new PrefsEvent(this, smallGui);

      PrefsEventListener l = null;
      Vector listenersCopy;

      synchronized (this)
      {
         listenersCopy = (Vector) mListeners.clone();
      }

      for ( Iterator itr = listenersCopy.iterator(); itr.hasNext(); )
      {
         l = (PrefsEventListener) itr.next();
         l.prefsChanged(evt);
      }
   }

   private void jbInit() throws Exception
   {
      mSizeToggleButton.setText("Small GUI");
      mSizeToggleButton.addItemListener(new java.awt.event.ItemListener()
      {
         public void itemStateChanged(ItemEvent e)
         {
            smallGui = (e.getStateChange() == ItemEvent.SELECTED);
            setGuiElement();
            firePrefsChanged();
         }
      });
      this.add(mSizeToggleButton, null);
   }

   /**
    * Updates the <gui> element based on the current preferences.
    */
   private void setGuiElement()
   {
      Element root = mPrefsDoc.getRootElement();
      Element gui_element = root.getChild("gui");
      gui_element.setAttribute("small", String.valueOf(smallGui));
   }

   private JToggleButton mSizeToggleButton = new JToggleButton();
   private boolean smallGui = false;

   private Vector mListeners = new Vector();
   private String mPrefsFileName;
   private Document mPrefsDoc;
}