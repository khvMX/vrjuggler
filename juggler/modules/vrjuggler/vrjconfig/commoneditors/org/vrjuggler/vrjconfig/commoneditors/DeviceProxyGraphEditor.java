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

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.*;

import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.layout.JGraphLayoutAlgorithm;
import org.jgraph.layout.SpringEmbeddedLayoutAlgorithm;

import org.vrjuggler.jccl.config.*;
import org.vrjuggler.jccl.config.event.ConfigEvent;
import org.vrjuggler.jccl.config.event.ConfigListener;

import org.vrjuggler.vrjconfig.commoneditors.devicegraph.*;


/**
 * This editor handles all the details of setting up a
 * <code>org.vrjuggler.vrjconfig.commoneditors.DeviceGraph</code> that
 * instance based on a given configuration.  The graph is populated by
 * invoking <i>one</i> of the <code>setConfig()</code> methods
 * <i>exactly once</i>.
 *
 * <p>
 * This editor can create a graph that allows the relationships between all
 * proxies, aliases, and devices to be edited, or it can create a graph with
 * a specific subset of all possible proxies and devices.  This editor has no
 * user interface of its own.  The contained DeviceGraph instance provdies
 * the interface for editing the device/proxy/alias relationships, but no
 * additional buttons and such are provided.  It is expected that higher
 * level editors would provide that part of the user interface.
 * </p>
 *
 * @see #setConfig(ConfigContext)
 * @see #setConfig(List,ConfigContext)
 * @see #setConfig(ConfigContext,ConfigElement)
 */
public class DeviceProxyGraphEditor
   extends JPanel
   implements EditorConstants
            , ConfigListener
{
   public DeviceProxyGraphEditor()
   {
      mBroker = new ConfigBrokerProxy();
      mBroker.addConfigListener(this);

      try
      {
         jbInit();
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
      }
   }

   /**
    * Handles the addition of a new config element by checking to see if it
    * is among the allowed types for this editor instance.  If the new element
    * is of an allowed type, it is added to the graph.
    */
   public void configElementAdded(ConfigEvent e)
   {
      ConfigElement added_elt = e.getElement();
      ConfigDefinition def = added_elt.getDefinition();

      if ( this.allowedTypes.contains(def) )
      {
         List device_elts = new ArrayList(1), proxy_elts = new ArrayList(1);

         if ( def.isOfType(PROXY_TYPE) )
         {
            device_elts.add(0, added_elt);
         }
         else
         {
            proxy_elts.add(0, added_elt);
         }

         populateGraph(device_elts, proxy_elts, mBroker.getElements(mContext));
      }
   }

   /** Does nothing. */
   public void configElementRemoved(ConfigEvent e)
   {
//      ConfigElement removed_elt = e.getElement();
//      ConfigDefinition def = removed_elt.getDefinition();
   }

   /**
    * Populates the graph with all devices and proxies in the given context.
    *
    * <p>
    * <b>NOTE:</b>
    * <blockquote>
    * One overload of <code>setConfig()</code> may be invoked <i>at most</i>
    * once per object lifetime.
    * </blockquote>
    * </p>
    *
    * @param context    the context in which device and proxy config elements
    *                   will be discovered and edited
    *
    * @see #setConfig(List,ConfigContext)
    * @see #setConfig(ConfigContext,ConfigElement)
    */
   public void setConfig(ConfigContext context)
   {
      setConfig(null, context);
   }

   /**
    * Populates the graph with all devices and proxies in the given context
    * that have definitions appearing in <code>allowedTypes</code>.  If
    * <code>allowedTypes</code> is null, then all devices and proxies in
    * <code>context</code> are added to the graph.
    *
    * <p>
    * <b>NOTE:</b>
    * <blockquote>
    * One overload of <code>setConfig()</code> may be invoked <i>at most</i>
    * once per object lifetime.
    * </blockquote>
    * </p>
    *
    * @param allowedTypes       a list of <code>ConfigDefinition</code>
    *                           objects that identifies which device and proxy
    *                           config elements will be edited by this editor
    *                           instance or null to indicate that all device
    *                           and proxy elements will be edited
    * @param context            the context in which device and proxy config
    *                           elements will be discovered and edited
    *
    * @see #setConfig(ConfigContext)
    * @see #setConfig(ConfigContext,ConfigElement)
    */
   public void setConfig(List allowedTypes, ConfigContext context)
   {
      mContext = context;
      this.allowedTypes.clear();

      if ( allowedTypes == null )
      {
         List base_types = new ArrayList(2);
         base_types.add(0, PROXY_TYPE);
         base_types.add(1, INPUT_DEVICE_TYPE);
         ConfigDefinitionRepository repos = mBroker.getRepository();
         List all_defs = repos.getAllLatest();

         for ( Iterator d = all_defs.iterator(); d.hasNext(); )
         {
            ConfigDefinition def = (ConfigDefinition) d.next();
            if ( ! def.isAbstract() )
            {
               for ( Iterator t = base_types.iterator(); t.hasNext(); )
               {
                  if ( def.isOfType((String) t.next()) )
                  {
                     System.out.println("Adding " + def.getToken());
                     this.allowedTypes.add(def);
                     break;
                  }
               }
            }
         }
      }
      else
      {
         this.allowedTypes.addAll(allowedTypes);
      }

      List device_elts = new ArrayList(), proxy_elts = new ArrayList();
      List all_elts = mBroker.getElements(mContext);

      for ( Iterator d = this.allowedTypes.iterator(); d.hasNext(); )
      {
         ConfigDefinition cur_def = (ConfigDefinition) d.next();

         List cur_elts = ConfigUtilities.getElementsWithDefinition(all_elts,
                                                                   cur_def);

         if ( cur_def.isOfType(INPUT_DEVICE_TYPE) )
         {
            device_elts.addAll(cur_elts);
         }
         else if ( cur_def.isOfType(PROXY_TYPE) )
         {
            proxy_elts.addAll(cur_elts);
         }
      }

      populateGraph(proxy_elts, device_elts, all_elts);
   }

   /**
    * Initializes the graph will the given proxy and all devices at which it
    * is allowed to point (including the device it currently points at if it
    * is so configured.
    *
    * <p>
    * <b>NOTE:</b>
    * <blockquote>
    * One overload of <code>setConfig()</code> may be invoked <i>at most</i>
    * once per object lifetime.
    * </blockquote>
    * </p>
    *
    * @param ctx        the context containing the given config element and
    *                   any devices to which that config element may refer
    * @param proxyElt   the config element for the single proxy that will be
    *                   edited in this editor instance.  It must be of type
    *                   <code>EditorConstants.PROXY_TYPE</code>.
    *
    * @see #setConfig(ConfigContext)
    * @see #setConfig(List,ConfigContext)
    * @see org.vrjuggler.vrjconfig.commoneditors.EditorConstants
    */
   public void setConfig(ConfigContext ctx, ConfigElement proxyElt)
   {
      if ( proxyElt.getDefinition().isOfType(PROXY_TYPE) )
      {
         mContext = ctx;

         this.allowedTypes.add(proxyElt.getDefinition());

         // For this proxy, we need to find all the device config elements at
         // which it is allowed to point.
         PropertyDefinition device_prop_def =
            proxyElt.getDefinition().getPropertyDefinition(DEVICE_PROPERTY);

         ConfigBroker broker = new ConfigBrokerProxy();
         List all_defs = broker.getRepository().getAllLatest();

         List allowed_dev_types = device_prop_def.getAllowedTypes();
         List all_elts          = broker.getElements(mContext);
         List device_elts       = new ArrayList();

         // Find all the non-astract config defiitions that are allowed types
         // for the proxy we were given.  Use those definitions to find all
         // the device config elements in the given context.
         for ( Iterator t = allowed_dev_types.iterator(); t.hasNext(); )
         {
            List defs = 
               ConfigUtilities.getDefinitionsOfType(all_defs,
                                                    (String) t.next());

            for ( Iterator d = defs.iterator(); d.hasNext(); )
            {
               ConfigDefinition cur_def = (ConfigDefinition) d.next();
               if ( ! cur_def.isAbstract() )
               {
                  this.allowedTypes.add(cur_def);
                  device_elts.addAll(
                     ConfigUtilities.getElementsWithDefinition(all_elts,
                                                               cur_def)
                  );
               }
            }
         }

         List proxy_elts = new ArrayList(1);
         proxy_elts.add(0, proxyElt);
         populateGraph(proxy_elts, device_elts, all_elts);
      }
      else
      {
         throw new IllegalArgumentException(
            "proxyElt is not a proxy type: " +
            proxyElt.getDefinition().getToken()
         );
      }
   }

   public DeviceGraph getGraph()
   {
      return this.graph;
   }

   public List getAllowedTypes()
   {
      return this.allowedTypes;
   }

   /**
    * Changes the graph layout algorithm to be used by this editor instance
    * to the given reference (which may be null).  If the given algorithm
    * reference is null, then subsequent invocations of
    * <code>applyGraphLayoutAlgorithm()</code> will have no effect.
    */
   public void setGraphLayoutAlgorithm(JGraphLayoutAlgorithm algorithm)
   {
      this.graphLayoutAlgorithm = algorithm;
   }

   /**
    * Applies the current graph layout algorithm to all the roots of the
    * graph.
    *
    * @see #applyGraphLayoutAlgorithm(Object[])
    */
   public void applyGraphLayoutAlgorithm()
   {
      applyGraphLayoutAlgorithm(
         DefaultGraphModel.getRoots(this.graph.getModel())
      );
   }

   /**
    * Applies the current graph layout algorithm to the given array of roots.
    * If the current graph layout algorithm is null, this method does nothing.
    * The given graph cells must be roots of the graph being used by this
    * editor instance.  Furthermore, the given graph cells are passed off to
    * the graph layout algorithm as dynamic cells.
    *
    * @param cells      the collection of roots in the graph that will be
    *                   laid out using the current layout algorithm
    *
    * @see #setGraphLayoutAlgorithm(JGraphLayoutAlgorithm)
    */
   public void applyGraphLayoutAlgorithm(Object[] cells)
   {
      if ( this.graphLayoutAlgorithm != null )
      {
         JGraphLayoutAlgorithm.applyLayout(this.graph,
                                           this.graphLayoutAlgorithm, cells);
      }
   }

   private void jbInit()
      throws Exception
   {
      this.setLayout(new BorderLayout());
      this.add(this.graph, BorderLayout.CENTER);
   }

   private void populateGraph(List proxyElts, List deviceElts, List allElts)
   {
      List cells = new ArrayList();
      ConnectionSet cs = new ConnectionSet();
      Map attributes = new HashMap();

      Map device_elt_map = new HashMap();

      // Handle all the input devices first.
      for ( Iterator e = deviceElts.iterator(); e.hasNext(); )
      {
         ConfigElement elt = (ConfigElement) e.next();
         try
         {
            DefaultGraphCell cell =
               GraphHelpers.createDeviceCell(elt, mContext, attributes);
            cells.add(cell);
            device_elt_map.put(elt.getName(), cell);
         }
         catch (IllegalArgumentException ex)
         {
            System.err.println(ex.getMessage());
         }
      }

      for ( Iterator e = proxyElts.iterator(); e.hasNext(); )
      {
         ConfigElement elt = (ConfigElement) e.next();

         // To create the graph cell for this proxy, we have to find all its
         // aliases.
         List aliases = new ArrayList();
         List alias_elts =
            ConfigUtilities.getElementsWithDefinition(allElts, ALIAS_TYPE);

         // Search alias_elts for those that refer to elt.
         for ( Iterator a = alias_elts.iterator(); a.hasNext(); )
         {
            ConfigElement alias_elt = (ConfigElement) a.next();
            ConfigElementPointer proxy_ptr =
               (ConfigElementPointer) alias_elt.getProperty(PROXY_PROPERTY, 0);
            if ( proxy_ptr != null && proxy_ptr.getTarget() != null &&
                 proxy_ptr.getTarget().equals(elt.getName()) )
            {
               aliases.add(alias_elt);
            }
         }

         // Create the graph cell for this proxy.
         DefaultGraphCell proxy_cell =
            GraphHelpers.createProxyCell(elt, mContext, aliases, attributes);
         cells.add(proxy_cell);

         // Look up the cell for the device pointed to by this proxy so that
         // we can create the connection between the new proxy cell and the
         // device cell.
         ConfigElementPointer dev_ptr =
            (ConfigElementPointer) elt.getProperty(DEVICE_PROPERTY, 0);

         if ( dev_ptr != null && dev_ptr.getTarget() != null &&
              ! dev_ptr.getTarget().equals("") )
         {
            DefaultGraphCell dev_cell =
               (DefaultGraphCell) device_elt_map.get(dev_ptr.getTarget());

            // If there is no cell for the device pointed at by this proxy,
            // we cannot make a connection.  This indicates that the user has
            // an incomplete configuration loaded.
            if ( dev_cell != null )
            {
               try
               {
                  cells.add(GraphHelpers.connectProxyToDevice(proxy_cell,
                                                              dev_cell, cs,
                                                              attributes));
               }
               catch (NoSuchPortException ex)
               {
                  System.err.println("WARNING: Failed to connect proxy " +
                                     "to device!\n\t" + ex.getMessage());
               }
            }
         }
      }

      Object[] cell_array = cells.toArray();
      this.graph.getModel().insert(cell_array, attributes, cs, null, null);
      applyGraphLayoutAlgorithm(cell_array);
   }

   private ConfigBroker  mBroker      = null;
   private ConfigContext mContext     = null;
   private List          allowedTypes = new ArrayList();

   private JGraphLayoutAlgorithm graphLayoutAlgorithm =
      new SpringEmbeddedLayoutAlgorithm();

   private DeviceGraph graph = new DeviceGraph();
}