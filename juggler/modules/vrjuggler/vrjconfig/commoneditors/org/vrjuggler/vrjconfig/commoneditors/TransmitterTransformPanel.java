/*************** <auto-copyright.pl BEGIN do not edit this line> **************
 *
 * VR Juggler is (C) Copyright 1998-2005 by Iowa State University
 *
 * Original Authors:
 *   Allen Bierbaum, Christopher Just,
 *   Patrick Hartling, Kevin Meinert,
 *   Carolina Cruz-Neira, Albert Baker
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * -----------------------------------------------------------------
 * File:          $RCSfile$
 * Date modified: $Date$
 * Version:       $Revision$
 * -----------------------------------------------------------------
 *
 *************** <auto-copyright.pl END do not edit this line> ***************/

package org.vrjuggler.vrjconfig.commoneditors;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.vrjuggler.jccl.config.*;
import org.vrjuggler.jccl.config.event.*;
import org.vrjuggler.jccl.editors.PropertyEditorPanel;
import org.vrjuggler.vrjconfig.commoneditors.event.*;


public class TransmitterTransformPanel
   extends JPanel
   implements ChangeListener
{
   public TransmitterTransformPanel()
   {
      mCoordFrames[0]  = new CoordFrame(0.0, 0.0, 0.0);
      mCoordFrames[1]  = new CoordFrame(90.0, 0.0, 0.0);
      mCoordFrames[2]  = new CoordFrame(180.0, 0.0, 0.0);
      mCoordFrames[3]  = new CoordFrame(180.0, 0.0, -90.0);
      mCoordFrames[4]  = new CoordFrame(180.0, 0.0, 90.0);
      mCoordFrames[5]  = new CoordFrame(0.0, 180.0, 0.0);
      mCoordFrames[6]  = new CoordFrame(180.0, -90.0, 0.0);
      mCoordFrames[7]  = new CoordFrame(0.0, 0.0, 90.0);
      mCoordFrames[8]  = new CoordFrame(0.0, 0.0, 180.0);
      mCoordFrames[9]  = new CoordFrame(0.0, 0.0, -90.0);
      mCoordFrames[10] = new CoordFrame(90.0, 0.0, 90.0);
      mCoordFrames[11] = new CoordFrame(90.0, 0.0, 180.0);
      mCoordFrames[12] = new CoordFrame(90.0, 0.0, -90.0);
      mCoordFrames[13] = new CoordFrame(90.0, 90.0, 0.0);
      mCoordFrames[14] = new CoordFrame(-180.0, 90.0, 0.0);
      mCoordFrames[15] = new CoordFrame(-90.0, 90.0, 0.0);
      mCoordFrames[16] = new CoordFrame(0.0, 90.0, 0.0);
      mCoordFrames[17] = new CoordFrame(-90.0, 0.0, 180.0);
      mCoordFrames[18] = new CoordFrame(-90.0, 0.0, -90.0);
      mCoordFrames[19] = new CoordFrame(-90.0, 0.0, 0.0);
      mCoordFrames[20] = new CoordFrame(-90.0, 0.0, 90.0);
      mCoordFrames[21] = new CoordFrame(90.0, -90.0, 0.0);
      mCoordFrames[22] = new CoordFrame(0.0, -90.0, 0.0);
      mCoordFrames[23] = new CoordFrame(-90.0, -90.0, 0.0);

      // Create the renderers for mVrjCoordChooser and mTrackerCoordChooser.
      CoordFrameRenderer r0 = new CoordFrameRenderer(mCoordFrames);
      CoordFrameRenderer r1 = new CoordFrameRenderer(mCoordFrames);

      try
      {
         ClassLoader loader = getClass().getClassLoader();
         String img_base = "org/vrjuggler/vrjconfig/commoneditors/images";

         for ( int i = 0; i < mCoordFrames.length; ++i )
         {
            StringBuffer buf = new StringBuffer(Integer.toString(i));
            if ( buf.length() == 1 )
            {
               buf.insert(0, 0);
            }
            mCoordFrames[i].setIcon(
               new ImageIcon(loader.getResource(img_base + "/axis" + buf +
                                                ".png"))
            );
         }

         // Only set up preferred sizes if all the icons were loaded
         // successfully.
         int w = 70, h = 70;
         r0.setPreferredSize(new Dimension(w, h));
         r1.setPreferredSize(new Dimension(w, h));
         mVrjCoordChooserPane.setMinimumSize(new Dimension(w, h));
         mVrjCoordChooserPane.setPreferredSize(new Dimension(w, h));
         mTrackerCoordChooserPane.setMinimumSize(new Dimension(w, h));
         mTrackerCoordChooserPane.setPreferredSize(new Dimension(w, h));
      }
      // If any one icon failed to load, clear all the coordinate frame icons.
      catch(NullPointerException ex)
      {
         for ( int i = 0; i < mCoordFrames.length; ++i )
         {
            mCoordFrames[i].setIcon(null);
         }
      }

      // Set up the list models for the coordinate frame choosers.
      DefaultListModel vrj_model     = new DefaultListModel();
      DefaultListModel tracker_model = new DefaultListModel();

      for ( int i = 0; i < mCoordFrames.length; ++i )
      {
         vrj_model.addElement(new Integer(i));
         tracker_model.addElement(new Integer(i));
      }

      mVrjCoordChooser.setModel(vrj_model);
      mVrjCoordChooser.setCellRenderer(r0);
      mTrackerCoordChooser.setModel(tracker_model);
      mTrackerCoordChooser.setCellRenderer(r1);

      mTranslationUnits    = new MeasurementUnit[4];
      mTranslationUnits[0] = new MeasurementUnit("Meters", 1.0);
      mTranslationUnits[1] = new MeasurementUnit("Centimeters", 0.1);
      mTranslationUnits[2] = new MeasurementUnit("Feet", 0.3048);
      mTranslationUnits[3] = new MeasurementUnit("Inches", 0.0254);

      for ( int i = 0; i < mTranslationUnits.length; ++i )
      {
         mTrackerPosUnitsChooser.addItem(mTranslationUnits[i].name);
      }

      mTrackerXRotSpinner.setModel(new SpinnerNumberModel(0.0, -180.0,
                                                          180.0, 0.1));
      mTrackerYRotSpinner.setModel(new SpinnerNumberModel(0.0, -180.0,
                                                          180.0, 0.1));
      mTrackerZRotSpinner.setModel(new SpinnerNumberModel(0.0, -180.0,
                                                          180.0, 0.1));

/*
      try
      {
         jbInit();
      }
      catch(Exception ex)
      {
         ex.printStackTrace();
      }
*/
   }

   public void stateChanged(ChangeEvent e)
   {
      JSpinner source = (JSpinner) e.getSource();

      int value_index;

      if ( source == mTrackerXRotSpinner )
      {
         value_index = 0;
      }
      else if ( source == mTrackerYRotSpinner )
      {
         value_index = 1;
      }
      else if ( source == mTrackerYRotSpinner )
      {
         value_index = 2;
      }
      else
      {
         System.err.println("[TransmitterTransformPanel.stateChanged()] " +
                            "Received state change event from unknown source " +
                            source);
         return;
      }

      // Using the object returned by source.getValue() seems to be safe
      // because every change in the spinner's value creates a new object.
      // NOTE: The object returned is of type Double rather than Float.
      mElement.setProperty("pre_rotation", value_index, source.getValue());
   }

   public void setConfig(ConfigContext ctx, ConfigElement elt)
   {
      if ( elt.getDefinition().getToken().equals("position_transform_filter") )
      {
         mContext = ctx;
         mElement = elt;

         elt.addConfigElementListener(new ElementListener());
         mSensorUnitsEditor =
            new PropertyEditorPanel(ctx, elt.getProperty("device_units", 0),
                                    elt.getDefinition().getPropertyDefinition("device_units"),
                                    elt, 0, Color.white);

         mCustomUnitsEditor =
            new PropertyEditorPanel(ctx, elt.getProperty("custom_scale", 0),
                                    elt.getDefinition().getPropertyDefinition("custom_scale"),
                                    elt, 0, Color.white);

         boolean enable_custom =
            ((Number) elt.getProperty("device_units", 0)).floatValue() == 0.0;
         mCustomUnitsEditor.setEnabled(enable_custom);

         mTrackerXPosField.setValue(elt.getProperty("pre_translate", 0));
         mTrackerYPosField.setValue(elt.getProperty("pre_translate", 1));
         mTrackerZPosField.setValue(elt.getProperty("pre_translate", 2));

         double x_rot =
            ((Number) elt.getProperty("pre_rotation", 0)).doubleValue();
         double y_rot =
            ((Number) elt.getProperty("pre_rotation", 1)).doubleValue();
         double z_rot =
            ((Number) elt.getProperty("pre_rotation", 2)).doubleValue();

         mTrackerXRotSpinner.setValue(new Double(x_rot));
         mTrackerYRotSpinner.setValue(new Double(y_rot));
         mTrackerZRotSpinner.setValue(new Double(z_rot));

         try
         {
            jbInit();
         }
         catch(Exception ex)
         {
            ex.printStackTrace();
         }

         // This is slow, but I don't think that there is a better way to do
         // it.
         // NOTE: This must be done *after* jbInit() is called so that the
         // scroll pane will be updated to display the selected coordinate
         // frame.
         int i;
         for ( i = 0; i < mCoordFrames.length; ++i )
         {
            if ( mCoordFrames[i].getXRot() == x_rot &&
                 mCoordFrames[i].getYRot() == y_rot &&
                 mCoordFrames[i].getZRot() == z_rot )
            {
               mTrackerCoordChooser.setSelectedIndex(i);
               mTrackerCoordChooser.ensureIndexIsVisible(i);
               break;
            }
         }

         mVrjCoordChooser.setSelectedIndex(0);
         mVrjCoordChooser.ensureIndexIsVisible(0);

         System.out.println("[TrackerTransformPanel.setConfig()] i == " + i);
         System.out.println("[TrackerTransformPanel.setConfig()] mCoordFrames.length == " + mCoordFrames.length);

         // If the search through mCoordFrames turned up no match, then we
         // will enable the manual rotation editing just to be friendly.
         if ( i == mCoordFrames.length )
         {
            mTrackerManualRotButton.setSelected(true);
         }

         // The change listener should be added after the spinners are fully
         // set up.  This will help avoid needless event handling on our part.
         mTrackerXRotSpinner.addChangeListener(this);
         mTrackerYRotSpinner.addChangeListener(this);
         mTrackerZRotSpinner.addChangeListener(this);
      }
      else
      {
         throw new IllegalArgumentException("Cannot handle config element " +
                                            "of type '" +
                                            elt.getDefinition().getToken() +
                                            "'");
      }
   }

   public void addJugglerTransformChangeListener(TransformChangeListener l)
   {
      mJugglerListeners.add(TransformChangeListener.class, l);
   }

   public void removeJugglerTransformChangeListener(TransformChangeListener l)
   {
      mJugglerListeners.remove(TransformChangeListener.class, l);
   }

   public void addTrackerTransformChangeListener(TransformChangeListener l)
   {
      mTrackerListeners.add(TransformChangeListener.class, l);
   }

   public void removeTrackerTransformChangeListener(TransformChangeListener l)
   {
      mTrackerListeners.remove(TransformChangeListener.class, l);
   }

   public double getJugglerXRot()
   {
      return mCoordFrames[mVrjCoordChooser.getSelectedIndex()].getXRot();
   }

   public double getJugglerYRot()
   {
      return mCoordFrames[mVrjCoordChooser.getSelectedIndex()].getYRot();
   }

   public double getJugglerZRot()
   {
      return mCoordFrames[mVrjCoordChooser.getSelectedIndex()].getZRot();
   }

   public double getTrackerXRot()
   {
      double x_rot;

      if ( mTrackerManualRotButton.isSelected() )
      {
         x_rot = ((Double) mTrackerXRotSpinner.getValue()).doubleValue();
      }
      else
      {
         x_rot = mCoordFrames[mTrackerCoordChooser.getSelectedIndex()].getXRot();
      }

      return x_rot;
   }

   public double getTrackerYRot()
   {
      double y_rot;

      if ( mTrackerManualRotButton.isSelected() )
      {
         y_rot = ((Double) mTrackerYRotSpinner.getValue()).doubleValue();
      }
      else
      {
         y_rot = mCoordFrames[mTrackerCoordChooser.getSelectedIndex()].getYRot();
      }

      return y_rot;
   }

   public double getTrackerZRot()
   {
      double z_rot;

      if ( mTrackerManualRotButton.isSelected() )
      {
         z_rot = ((Double) mTrackerZRotSpinner.getValue()).doubleValue();
      }
      else
      {
         z_rot = mCoordFrames[mTrackerCoordChooser.getSelectedIndex()].getZRot();
      }

      return z_rot;
   }

   protected void fireTransformChanged(EventListenerList listenerList,
                                       double xRot, double yRot, double zRot)
   {
      TransformChangeEvent change_event = null;
      Object[] listeners = listenerList.getListenerList();

      // Process the listeners last to first, notifying
      // those that are interested in this event
      for ( int i = listeners.length - 2; i >= 0; i -= 2 )
      {
         if (listeners[i] == TransformChangeListener.class)
         {
            // Lazily create the event:
            if ( change_event == null)
            {
               change_event = new TransformChangeEvent(this, xRot, yRot, zRot);
            }
            ((TransformChangeListener) listeners[i + 1]).transformChanged(change_event);
         }
      }
   }

   private void jbInit()
      throws Exception
   {
      this.setLayout(mMainLayout);
      mRotationPanel.setBorder(mRotationBorder);
      mRotationPanel.setLayout(mRotationPanelLayout);
      mRotationBorder.setTitle("Orientation");
      mTranslationPanel.setBorder(mTranslationBorder);
      mTranslationPanel.setLayout(mTranslationPanelLayout);
      mTranslationBorder.setTitle("Position");
      mVrjCoordChooserLabel.setLabelFor(mVrjCoordChooser);
      mVrjCoordChooserLabel.setText("<html>VR Juggler Coordinate Frame</html>");
      mVrjCoordChooserLabel.setHorizontalAlignment(SwingConstants.CENTER);
      mVrjCoordPanel.setLayout(mVrjCoordPanelLayout);
      mVrjCoordChooser.setEnabled(false);
      mVrjCoordChooser.addListSelectionListener(new
         TransmitterTransformPanel_mVrjCoordChooser_listSelectionAdapter(this));
      mVrjCoordAdvButton.setText("Advanced");
      mVrjCoordAdvButton.addActionListener(new
         TransmitterTransformPanel_mVrjCoordAdvButton_actionAdapter(this));
      mTrackerCoordPanel.setLayout(mTrackerCoordPanelLayout);
      mTrackerCoordChooserLabel.setLabelFor(mTrackerCoordChooser);
      mTrackerCoordChooserLabel.setText(
         "<html>Transmitter Coordinate Frame</html>");
      mTrackerCoordChooserLabel.setHorizontalAlignment(SwingConstants.CENTER);
      mTrackerAdvPanel.setLayout(mTrackerAdvPanelLayout);
      mTrackerAnglesLabel.setLabelFor(mTrackerAnglesPanel);
      mTrackerAnglesLabel.setText(
         "<html>Transmitter Pre-Rotation Angles</html>");
      mTrackerAnglesLabel.setHorizontalAlignment(SwingConstants.CENTER);
      mTrackerAnglesPanel.setLayout(mTrackerAnglesPanelLayout);
      mTrackerXRotLabel.setHorizontalAlignment(SwingConstants.TRAILING);
      mTrackerXRotLabel.setLabelFor(mTrackerXRotSpinner);
      mTrackerXRotLabel.setText("X Rotation:");
      mTrackerXRotLabel.setForeground(Color.red);
      mTrackerYRotLabel.setHorizontalAlignment(SwingConstants.TRAILING);
      mTrackerYRotLabel.setLabelFor(mTrackerYRotSpinner);
      mTrackerYRotLabel.setText("Y Rotation:");
      mTrackerYRotLabel.setForeground(Color.green);
      mTrackerZRotLabel.setHorizontalAlignment(SwingConstants.TRAILING);
      mTrackerZRotLabel.setLabelFor(mTrackerZRotSpinner);
      mTrackerZRotLabel.setText("Z Rotation:");
      mTrackerZRotLabel.setForeground(Color.blue);
      mTrackerManualRotButton.setText("Manual Entry");
      mTrackerManualRotButton.addActionListener(new
         TransmitterTransformPanel_mTrackerManualRotButton_actionAdapter(this));
      mTrackerXRotSpinner.setEnabled(false);
      mTrackerYRotSpinner.setEnabled(false);
      mTrackerZRotSpinner.setEnabled(false);
      mTrackerPosLabel.setLabelFor(mTrackerPosPanel);
      mTrackerPosLabel.setText(
         "<html>Position of the tracker transmitter from VR Juggler origin (pre-translation)</html>");
      mTrackerPosPanel.setLayout(mTrackerPosPanelLayout);
      mTrackerXPosLabel.setHorizontalAlignment(SwingConstants.TRAILING);
      mTrackerXPosLabel.setLabelFor(mTrackerXPosField);
      mTrackerXPosLabel.setText("X:");
      mTrackerPosUnitsLabel.setLabelFor(mTrackerPosUnitsChooser);
      mTrackerPosUnitsLabel.setText("Measurement Units:");
      mTrackerXPosField.addPropertyChangeListener(new
         TransmitterTransformPanel_mTrackerXPosField_propertyChangeAdapter(this));
      mTrackerXPosField.setHorizontalAlignment(SwingConstants.TRAILING);
      mTrackerXPosField.setPreferredSize(new Dimension(75, 22));
      mTrackerYPosLabel.setLabelFor(mTrackerYPosField);
      mTrackerYPosLabel.setText("Y:");
      mTrackerYPosField.addPropertyChangeListener(new
         TransmitterTransformPanel_mTrackerYPosField_propertyChangeAdapter(this));
      mTrackerYPosField.setHorizontalAlignment(SwingConstants.TRAILING);
      mTrackerYPosField.setPreferredSize(new Dimension(75, 22));
      mTrackerZPosLabel.setLabelFor(mTrackerZPosField);
      mTrackerZPosLabel.setText("Z:");
      mTrackerZPosField.addPropertyChangeListener(new
         TransmitterTransformPanel_mTrackerZPosField_propertyChangeAdapter(this));
      mTrackerZPosField.setHorizontalAlignment(SwingConstants.TRAILING);
      mTrackerZPosField.setPreferredSize(new Dimension(75, 22));
      mSensorUnitsPanel.setBorder(mTrackerUnitsBorder);
      mSensorUnitsPanel.setLayout(mSensorUnitsPanelLayout);
      mTrackerUnitsBorder.setTitle("Tracker Units");
      mSensorUnitsEditorPanel.setLayout(mSensorUnitsEditorPanelLayout);
      mSensorUnitsLabel.setLabelFor(mSensorUnitsEditor);
      mSensorUnitsLabel.setText("Sensor(s) Sample Units:");
      mCustomUnitsEditor.setEnabled(false);
      mCustomUnitsEditor.setToolTipText(
         "Set custom unit conversion (to meters) for sensor samples");
      mTrackerPosUnitsChooser.setToolTipText(
         "Choose the units of the values entered below");
      mTrackerPosUnitsChooser.addActionListener(new
         TransmitterTransformPanel_mTrackerPosUnitsChooser_actionAdapter(this));
      mSensorUnitsEditor.setToolTipText(
         "Choose the units of samples collected from sensors");
      mTrackerCoordChooser.addListSelectionListener(new
         TransmitterTransformPanel_mTrackerCoordChooser_listSelectionAdapter(this));
      mVrjCoordChooserPane.getViewport().add(mVrjCoordChooser);
      mTrackerCoordChooserPane.getViewport().add(mTrackerCoordChooser);
      mSensorUnitsEditorPanel.add(mSensorUnitsLabel,
                                  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                                         GridBagConstraints.EAST,
                                                         GridBagConstraints.NONE,
                                                         new Insets(0, 0, 0, 3),
                                                         0, 0));
      mSensorUnitsEditorPanel.add(mSensorUnitsEditor,
                                  new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                                                         GridBagConstraints.WEST,
                                                         GridBagConstraints.NONE,
                                                         new Insets(0, 0, 0, 0),
                                                         0, 0));
      mSensorUnitsEditorPanel.add(mCustomUnitsEditor,
                                  new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0,
                                                         GridBagConstraints.WEST,
                                                         GridBagConstraints.NONE,
                                                         new Insets(0, 3, 0, 2),
                                                         50, 0));
      mSensorUnitsPanel.add(mSensorUnitsEditorPanel,
                            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                                                   GridBagConstraints.WEST,
                                                   GridBagConstraints.NONE,
                                                   new Insets(0, 3, 0, 0),
                                                   0, 0));
      mVrjCoordPanel.add(mVrjCoordChooserLabel,
                         new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                                GridBagConstraints.CENTER,
                                                GridBagConstraints.HORIZONTAL,
                                                new Insets(0, 0, 0, 0), 0, 0));
      mVrjCoordPanel.add(mVrjCoordChooserPane,
                         new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                                                GridBagConstraints.CENTER,
                                                GridBagConstraints.BOTH,
                                                new Insets(0, 0, 0, 2), 0, 0));
      mVrjCoordPanel.add(mVrjCoordAdvButton,
                         new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                                                GridBagConstraints.CENTER,
                                                GridBagConstraints.HORIZONTAL,
                                                new Insets(0, 0, 0, 0), 0, 0));
      mTrackerCoordPanel.add(mTrackerCoordChooserLabel,
                             new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                                    GridBagConstraints.CENTER,
                                                    GridBagConstraints.HORIZONTAL,
                                                    new Insets(0, 0, 0, 0),
                                                    0, 0));
      mTrackerCoordPanel.add(mTrackerCoordChooserPane,
                             new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
                                                    GridBagConstraints.CENTER,
                                                    GridBagConstraints.BOTH,
                                                    new Insets(0, 0, 0, 2),
                                                    0, 0));
      mRotationPanel.add(mVrjCoordPanel,
                         new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                                                GridBagConstraints.NORTH,
                                                GridBagConstraints.HORIZONTAL,
                                                new Insets(0, 3, 0, 5), 0, 0));
      mRotationPanel.add(mTrackerCoordPanel,
                         new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
                                                GridBagConstraints.NORTH,
                                                GridBagConstraints.HORIZONTAL,
                                                new Insets(0, 0, 0, 5), 0, 0));
      mRotationPanel.add(mTrackerAdvPanel,
                         new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0,
                                                GridBagConstraints.CENTER,
                                                GridBagConstraints.BOTH,
                                                new Insets(0, 0, 0, 2),
                                                -66, -89));
      mTrackerAnglesPanel.add(mTrackerXRotLabel,
                              new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                                     GridBagConstraints.EAST,
                                                     GridBagConstraints.NONE,
                                                     new Insets(0, 0, 0, 3),
                                                     0, 0));
      mTrackerAnglesPanel.add(mTrackerXRotSpinner,
                              new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
                                                     GridBagConstraints.WEST,
                                                     GridBagConstraints.NONE,
                                                     new Insets(2, 0, 3, 0),
                                                     0, 0));
      mTrackerAnglesPanel.add(mTrackerYRotLabel,
                              new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                                     GridBagConstraints.EAST,
                                                     GridBagConstraints.NONE,
                                                     new Insets(0, 0, 0, 3),
                                                     0, 0));
      mTrackerAnglesPanel.add(mTrackerYRotSpinner,
                              new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
                                                     GridBagConstraints.WEST,
                                                     GridBagConstraints.NONE,
                                                     new Insets(0, 0, 3, 0),
                                                     0, 0));
      mTrackerAnglesPanel.add(mTrackerZRotLabel,
                              new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                                                     GridBagConstraints.EAST,
                                                     GridBagConstraints.NONE,
                                                     new Insets(0, 0, 0, 3),
                                                     0, 0));
      mTrackerAnglesPanel.add(mTrackerZRotSpinner,
                              new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0,
                                                     GridBagConstraints.WEST,
                                                     GridBagConstraints.NONE,
                                                     new Insets(0, 0, 2, 0),
                                                     0, 0));
      mTrackerAdvPanel.add(mTrackerAnglesLabel,
                           new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                                  GridBagConstraints.CENTER,
                                                  GridBagConstraints.HORIZONTAL,
                                                  new Insets(0, 0, 3, 0),
                                                  0, 0));
      mTrackerAdvPanel.add(mTrackerAnglesPanel,
                           new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                                                  GridBagConstraints.CENTER,
                                                  GridBagConstraints.BOTH,
                                                  new Insets(0, 0, 0, 0),
                                                  0, 0));
      mTrackerAdvPanel.add(mTrackerManualRotButton,
                           new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                                                  GridBagConstraints.CENTER,
                                                  GridBagConstraints.HORIZONTAL,
                                                  new Insets(3, 0, 0, 0),
                                                  0, 0));
      mTranslationPanel.add(mTrackerPosLabel,
                            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                                   GridBagConstraints.WEST,
                                                   GridBagConstraints.BOTH,
                                                   new Insets(0, 3, 0, 0),
                                                   0, 0));
      mTrackerPosPanel.add(mTrackerPosUnitsLabel,
                           new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                                  GridBagConstraints.CENTER,
                                                  GridBagConstraints.NONE,
                                                  new Insets(0, 0, 0, 3),
                                                  0, 0));
      mTrackerPosPanel.add(mTrackerPosUnitsChooser,
                           new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                                  GridBagConstraints.WEST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(0, 0, 0, 0),
                                                  0, 0));
      mTrackerPosPanel.add(mTrackerXPosLabel,
                           new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                                  GridBagConstraints.EAST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(0, 0, 0, 3),
                                                  0, 0));
      mTrackerPosPanel.add(mTrackerXPosField,
                           new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                                                  GridBagConstraints.WEST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(0, 0, 0, 1),
                                                  0, 0));
      mTrackerPosPanel.add(mTrackerYPosLabel,
                           new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                                                  GridBagConstraints.EAST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(0, 0, 0, 3),
                                                  0, 0));
      mTrackerPosPanel.add(mTrackerYPosField,
                           new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
                                                  GridBagConstraints.WEST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(0, 0, 0, 1),
                                                  0, 0));
      mTrackerPosPanel.add(mTrackerZPosLabel,
                           new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                                                  GridBagConstraints.EAST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(0, 0, 0, 3),
                                                  0, 9));
      mTrackerPosPanel.add(mTrackerZPosField,
                           new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0,
                                                  GridBagConstraints.WEST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(0, 0, 0, 1),
                                                  0, 0));
      mTranslationPanel.add(mTrackerPosPanel,
                            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                                                   GridBagConstraints.CENTER,
                                                   GridBagConstraints.BOTH,
                                                   new Insets(0, 3, 0, 0),
                                                   0, 0));
      this.add(mSensorUnitsPanel,
               new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                                      GridBagConstraints.CENTER,
                                      GridBagConstraints.HORIZONTAL,
                                      new Insets(0, 0, 0, 0), 0, 0));
      this.add(mRotationPanel,
               new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                                      GridBagConstraints.CENTER,
                                      GridBagConstraints.BOTH,
                                      new Insets(0, 0, 0, 0), 0, 30));
      this.add(mTranslationPanel,
               new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
                                      GridBagConstraints.CENTER,
                                      GridBagConstraints.HORIZONTAL,
                                      new Insets(0, 0, 2, 0), 0, 0));
   }

   private ConfigContext mContext = null;
   private ConfigElement mElement = null;

   private CoordFrame[]      mCoordFrames      = new CoordFrame[24];
   private EventListenerList mJugglerListeners = new EventListenerList();
   private EventListenerList mTrackerListeners = new EventListenerList();
   private MeasurementUnit[] mTranslationUnits = null;

   private JPanel mRotationPanel = new JPanel();
   private TitledBorder mRotationBorder = new TitledBorder("");
   private JPanel mTranslationPanel = new JPanel();
   private TitledBorder mTranslationBorder = new TitledBorder("");
   private JPanel mVrjCoordPanel = new JPanel();
   private JLabel mVrjCoordChooserLabel = new JLabel();
   private JScrollPane mVrjCoordChooserPane = new JScrollPane();
   private JList mVrjCoordChooser = new JList();
   private JCheckBox mVrjCoordAdvButton = new JCheckBox();
   private JPanel mTrackerCoordPanel = new JPanel();
   private JLabel mTrackerCoordChooserLabel = new JLabel();
   private JScrollPane mTrackerCoordChooserPane = new JScrollPane();
   private JList mTrackerCoordChooser = new JList();
   private JPanel mTrackerAdvPanel = new JPanel();
   private JLabel mTrackerAnglesLabel = new JLabel();
   private JPanel mTrackerAnglesPanel = new JPanel();
   private JLabel mTrackerXRotLabel = new JLabel();
   private JSpinner mTrackerXRotSpinner = new JSpinner();
   private JLabel mTrackerYRotLabel = new JLabel();
   private JSpinner mTrackerYRotSpinner = new JSpinner();
   private JLabel mTrackerZRotLabel = new JLabel();
   private JSpinner mTrackerZRotSpinner = new JSpinner();
   private JCheckBox mTrackerManualRotButton = new JCheckBox();
   private JLabel mTrackerPosLabel = new JLabel();
   private JPanel mTrackerPosPanel = new JPanel();
   private JLabel mTrackerXPosLabel = new JLabel();
   private JComboBox mTrackerPosUnitsChooser = new JComboBox();
   private JLabel mTrackerPosUnitsLabel = new JLabel();
   private JFormattedTextField mTrackerXPosField = new JFormattedTextField();
   private JLabel mTrackerYPosLabel = new JLabel();
   private JFormattedTextField mTrackerYPosField = new JFormattedTextField();
   private JLabel mTrackerZPosLabel = new JLabel();
   private JFormattedTextField mTrackerZPosField = new JFormattedTextField();
   private JPanel mSensorUnitsPanel = new JPanel();
   private TitledBorder mTrackerUnitsBorder = new TitledBorder("");
   private JPanel mSensorUnitsEditorPanel = new JPanel();
   private JLabel mSensorUnitsLabel = new JLabel();
   private PropertyEditorPanel mSensorUnitsEditor = null;
   private PropertyEditorPanel mCustomUnitsEditor = null;
   private GridBagLayout mVrjCoordPanelLayout = new GridBagLayout();
   private GridBagLayout mTrackerCoordPanelLayout = new GridBagLayout();
   private GridBagLayout mTrackerAdvPanelLayout = new GridBagLayout();
   private GridBagLayout mTrackerAnglesPanelLayout = new GridBagLayout();
   private GridBagLayout mRotationPanelLayout = new GridBagLayout();
   private GridBagLayout mSensorUnitsPanelLayout = new GridBagLayout();
   private GridBagLayout mSensorUnitsEditorPanelLayout = new GridBagLayout();
   private GridBagLayout mTrackerPosPanelLayout = new GridBagLayout();
   private GridBagLayout mTranslationPanelLayout = new GridBagLayout();
   private GridBagLayout mMainLayout = new GridBagLayout();

   private class ElementListener
      extends ConfigElementAdapter
   {
      public void propertyValueChanged(ConfigElementEvent evt)
      {
         if ( evt.getProperty().equals("device_units") )
         {
            Object value = mElement.getProperty("device_units", 0);
            boolean enable = ((Number) value).floatValue() == 0.0;
            mCustomUnitsEditor.setEnabled(enable);
         }
      }
   }

   void mVrjCoordAdvButton_actionPerformed(ActionEvent actionEvent)
   {
      mVrjCoordChooser.setEnabled(mVrjCoordAdvButton.isSelected());
   }

   void mVrjCoordChooser_valueChanged(ListSelectionEvent listSelectionEvent)
   {
      CoordFrame cf = mCoordFrames[mVrjCoordChooser.getSelectedIndex()];
      fireTransformChanged(mJugglerListeners, cf.getXRot(), cf.getYRot(),
                           cf.getZRot());
   }

   void mTrackerCoordChooser_valueChanged(ListSelectionEvent listSelectionEvent)
   {
      CoordFrame cf = mCoordFrames[mTrackerCoordChooser.getSelectedIndex()];
      mTrackerXRotSpinner.getModel().setValue(new Double(cf.getXRot()));
      mTrackerYRotSpinner.getModel().setValue(new Double(cf.getYRot()));
      mTrackerZRotSpinner.getModel().setValue(new Double(cf.getZRot()));

      fireTransformChanged(mTrackerListeners, cf.getXRot(), cf.getYRot(),
                           cf.getZRot());
   }

   void mTrackerManualRotButton_actionPerformed(ActionEvent actionEvent)
   {
      boolean enabled = mTrackerManualRotButton.isSelected();
      mTrackerXRotSpinner.setEnabled(enabled);
      mTrackerYRotSpinner.setEnabled(enabled);
      mTrackerZRotSpinner.setEnabled(enabled);
   }

   void mTrackerPosUnitsChooser_actionPerformed(ActionEvent actionEvent)
   {
      double conv_factor =
         mTranslationUnits[mTrackerPosUnitsChooser.getSelectedIndex()].toMeters;
      double x_pos =
         ((Number) mTrackerXPosField.getValue()).doubleValue() * conv_factor;
      double y_pos =
         ((Number) mTrackerXPosField.getValue()).doubleValue() * conv_factor;
      double z_pos =
         ((Number) mTrackerXPosField.getValue()).doubleValue() * conv_factor;

      mTrackerXPosField.setValue(new Double(x_pos));
      mTrackerYPosField.setValue(new Double(y_pos));
      mTrackerZPosField.setValue(new Double(z_pos));
   }

   void mTrackerXPosField_propertyChange(PropertyChangeEvent propertyChangeEvent)
   {
      if ( propertyChangeEvent.getPropertyName().equals("value") )
      {
         mElement.setProperty("pre_translate", 0, mTrackerXPosField.getValue());
      }
   }

   void mTrackerYPosField_propertyChange(PropertyChangeEvent propertyChangeEvent)
   {
      if ( propertyChangeEvent.getPropertyName().equals("value") )
      {
         mElement.setProperty("pre_translate", 1, mTrackerYPosField.getValue());
      }
   }

   void mTrackerZPosField_propertyChange(PropertyChangeEvent propertyChangeEvent)
   {
      if ( propertyChangeEvent.getPropertyName().equals("value") )
      {
         mElement.setProperty("pre_translate", 2, mTrackerZPosField.getValue());
      }
   }
}

class TransmitterTransformPanel_mTrackerCoordChooser_listSelectionAdapter
   implements ListSelectionListener
{
   private TransmitterTransformPanel adaptee;
   TransmitterTransformPanel_mTrackerCoordChooser_listSelectionAdapter(
      TransmitterTransformPanel adaptee)
   {
      this.adaptee = adaptee;
   }

   public void valueChanged(ListSelectionEvent listSelectionEvent)
   {
      adaptee.mTrackerCoordChooser_valueChanged(listSelectionEvent);
   }
}

class TransmitterTransformPanel_mVrjCoordChooser_listSelectionAdapter
   implements ListSelectionListener
{
   private TransmitterTransformPanel adaptee;
   TransmitterTransformPanel_mVrjCoordChooser_listSelectionAdapter(
      TransmitterTransformPanel adaptee)
   {
      this.adaptee = adaptee;
   }

   public void valueChanged(ListSelectionEvent listSelectionEvent)
   {
      adaptee.mVrjCoordChooser_valueChanged(listSelectionEvent);
   }
}

class TransmitterTransformPanel_mTrackerXPosField_propertyChangeAdapter
   implements PropertyChangeListener
{
   private TransmitterTransformPanel adaptee;
   TransmitterTransformPanel_mTrackerXPosField_propertyChangeAdapter(
      TransmitterTransformPanel adaptee)
   {
      this.adaptee = adaptee;
   }

   public void propertyChange(PropertyChangeEvent propertyChangeEvent)
   {
      adaptee.mTrackerXPosField_propertyChange(propertyChangeEvent);
   }
}

class TransmitterTransformPanel_mTrackerYPosField_propertyChangeAdapter
   implements PropertyChangeListener
{
   private TransmitterTransformPanel adaptee;
   TransmitterTransformPanel_mTrackerYPosField_propertyChangeAdapter(
      TransmitterTransformPanel adaptee)
   {
      this.adaptee = adaptee;
   }

   public void propertyChange(PropertyChangeEvent propertyChangeEvent)
   {
      adaptee.mTrackerYPosField_propertyChange(propertyChangeEvent);
   }
}

class TransmitterTransformPanel_mTrackerZPosField_propertyChangeAdapter
   implements PropertyChangeListener
{
   private TransmitterTransformPanel adaptee;
   TransmitterTransformPanel_mTrackerZPosField_propertyChangeAdapter(
      TransmitterTransformPanel adaptee)
   {
      this.adaptee = adaptee;
   }

   public void propertyChange(PropertyChangeEvent propertyChangeEvent)
   {
      adaptee.mTrackerZPosField_propertyChange(propertyChangeEvent);
   }
}

class TransmitterTransformPanel_mTrackerPosUnitsChooser_actionAdapter
   implements ActionListener
{
   private TransmitterTransformPanel adaptee;
   TransmitterTransformPanel_mTrackerPosUnitsChooser_actionAdapter(
      TransmitterTransformPanel adaptee)
   {
      this.adaptee = adaptee;
   }

   public void actionPerformed(ActionEvent actionEvent)
   {
      adaptee.mTrackerPosUnitsChooser_actionPerformed(actionEvent);
   }
}

class MeasurementUnit
{
   public MeasurementUnit(String name, double toMeters)
   {
      this.name     = name;
      this.toMeters = toMeters;
   }

   public String name;
   public double toMeters;
}

class CoordFrame
{
   CoordFrame(double xRot, double yRot, double zRot)
   {
      this(xRot, yRot, zRot, null);
   }

   CoordFrame(double xRot, double yRot, double zRot, ImageIcon icon)
   {
      this.xRot = xRot;
      this.yRot = yRot;
      this.zRot = zRot;
      this.icon = icon;
   }

   public double getXRot()
   {
      return xRot;
   }

   public double getYRot()
   {
      return yRot;
   }

   public double getZRot()
   {
      return zRot;
   }

   public ImageIcon getIcon()
   {
      return icon;
   }

   public void setIcon(ImageIcon icon)
   {
      this.icon = icon;
   }

   public String toString()
   {
      return "X: " + getXRot() + "\u00B0, " +
             "Y: " + getYRot() + "\u00B0, " +
             "Z: " + getZRot() + "\u00B0";
   }

   private double xRot;
   private double yRot;
   private double zRot;
   private ImageIcon icon;
}

class CoordFrameRenderer
   extends JLabel
   implements ListCellRenderer
{
   public CoordFrameRenderer(CoordFrame[] coordFrames)
   {
      this.mCoordFrames = coordFrames;
      setOpaque(true);
      setHorizontalAlignment(LEFT);
      setVerticalAlignment(CENTER);
   }

   public Component getListCellRendererComponent(JList list, Object value,
                                                 int index,
                                                 boolean isSelected,
                                                 boolean cellHasFocus)
   {
      int selected_index = ((Integer) value).intValue();

      if ( isSelected )
      {
         setBackground(list.getSelectionBackground());
         setForeground(list.getSelectionForeground());
      }
      else
      {
         setBackground(list.getBackground());
         setForeground(list.getForeground());
      }

      if ( mCoordFrames[selected_index].getIcon() != null )
      {
         setIcon(mCoordFrames[selected_index].getIcon());
         setToolTipText(mCoordFrames[selected_index].toString());
         setHorizontalTextPosition(RIGHT);
         setVerticalTextPosition(CENTER);
      }

      setText(mCoordFrames[selected_index].toString());
      setFont(list.getFont());

      return this;
   }

   private CoordFrame[] mCoordFrames;
}

class TransmitterTransformPanel_mTrackerManualRotButton_actionAdapter
   implements ActionListener
{
   private TransmitterTransformPanel adaptee;
   TransmitterTransformPanel_mTrackerManualRotButton_actionAdapter(
      TransmitterTransformPanel adaptee)
   {
      this.adaptee = adaptee;
   }

   public void actionPerformed(ActionEvent actionEvent)
   {
      adaptee.mTrackerManualRotButton_actionPerformed(actionEvent);
   }
}

class TransmitterTransformPanel_mVrjCoordAdvButton_actionAdapter
   implements ActionListener
{
   private TransmitterTransformPanel adaptee;
   TransmitterTransformPanel_mVrjCoordAdvButton_actionAdapter(
      TransmitterTransformPanel adaptee)
   {
      this.adaptee = adaptee;
   }

   public void actionPerformed(ActionEvent actionEvent)
   {
      adaptee.mVrjCoordAdvButton_actionPerformed(actionEvent);
   }
}