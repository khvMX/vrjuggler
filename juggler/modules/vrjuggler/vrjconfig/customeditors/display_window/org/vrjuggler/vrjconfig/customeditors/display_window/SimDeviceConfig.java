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

package org.vrjuggler.vrjconfig.customeditors.display_window;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.vrjuggler.jccl.config.*;


/**
 * This is a helper class designed to collect together all the config elements
 * associated with a given config element for a simulated device type.  It
 * finds all the proxies pointing at the given config element and all the
 * aliases that refer to those proxies.  There is nothing that specifically
 * restricts this class to being used with simulated devices.  It just
 * happens to have evolved out of a need for handling simulated device type
 * config elements.
 */
public class SimDeviceConfig
   implements EditorConstants
{
   /**
    * Simple constructor for a simulated device type's config element.
    * This looks in the context for all proxies pointing at the given
    * simulated device's config element and all aliases referring to those
    * discovered proxies.
    *
    * @param ctx                the context where the config element is used
    * @param simDeviceElt       the config element for a Juggler simulated
    *                           device type
    */
   public SimDeviceConfig(ConfigContext ctx, ConfigElement simDeviceElt)
   {
      device = simDeviceElt;

      findProxies(ctx);

      if ( proxies.size() == 0 )
      {
         System.out.println("No proxy found for " + device.getName());
      }
      else
      {
         findAliases(ctx);
      }
   }

   /**
    * Simple constructor for a simulated device type's config element.
    * This looks in the context for all proxies of then given type pointing at
    * the given simulated device's config element and all aliases referring to
    * those discovered proxies.  For device types that allow multiple proxy
    * types to be used, use the constructor overload that performs an untyped
    * proxy search.
    *
    * @param ctx                the context where the config element is used
    * @param simDeviceElt       the config element for a Juggler simulated
    *                           device type
    * @param proxyTypeName      the name of the proxy type that can point at
    *                           simDeviceElt
    *
    * @see #SimDeviceConfig(ConfigContext,ConfigElement)
    */
   public SimDeviceConfig(ConfigContext ctx, ConfigElement simDeviceElt,
                          String proxyTypeName)
   {
      device = simDeviceElt;

      findTypedProxies(ctx, proxyTypeName);

      if ( proxies.size() == 0 )
      {
         System.out.println("No proxy found for " + device.getName());
      }
      else
      {
         findAliases(ctx);
      }
   }

   /**
    * Simple constructor for a simulated device type's config element.
    * This looks in the context for all proxies of then given type pointing at
    * the given simulated device's config element and all aliases referring to
    * those discovered proxies.  For device types that allow multiple proxy
    * types to be used, use the constructor overload that performs an untyped
    * proxy search.
    *
    * @param ctx                the context where the config element is used
    * @param simDeviceElt       the config element for a Juggler simulated
    *                           device type
    * @param proxyType          the config definition for the proxy type that
    *                           can point at simDeviceElt
    *
    * @see #SimDeviceConfig(ConfigContext,ConfigElement)
    */
   public SimDeviceConfig(ConfigContext ctx, ConfigElement simDeviceElt,
                          ConfigDefinition proxyType)
   {
      device = simDeviceElt;

      findTypedProxies(ctx, proxyType);

      if ( proxies.size() == 0 )
      {
         System.out.println("No proxy found for " + device.getName());
      }
      else
      {
         findAliases(ctx);
      }
   }

   /**
    * Device + proxy list constructor.  This constructor assumes that the
    * given list of proxy config elements is both correct (the proxies
    * refer to the givem simulated device config element) and complete (the
    * proxies in the list are all those proxies in the context that refer
    * to the given simulated device config element).  This looks in the
    * context for all aliases referring to the given list of proxies.
    *
    * @param ctx                the context where the config element is used
    * @param simDeviceElt       the config element for a Juggler simulated
    *                           device type
    * @param proxyElts          the complete list of proxy config elements in
    *                           ctx that point to simDeviceElt
    */
   public SimDeviceConfig(ConfigContext ctx, ConfigElement simDeviceElt,
                          List proxyElts)
   {
      device  = simDeviceElt;
      proxies = proxyElts;
      findAliases(ctx);
   }

   /**
    * Device + proxy list constructor.  This constructor assumes that the
    * given list of proxy config elements is both correct (the proxies
    * refer to the givem simulated device config element) and complete (the
    * proxies in the list are all those proxies in the context that refer
    * to the given simulated device config element).  This looks in the
    * context for all aliases referring to the given list of proxies.
    *
    * @param ctx                the context where the config element is used
    * @param simDeviceElt       the config element for a Juggler simulated
    *                           device type
    * @param proxyElts          the complete array of proxy config elements in
    *                           ctx that point to simDeviceElt
    */
   public SimDeviceConfig(ConfigContext ctx, ConfigElement simDeviceElt,
                          ConfigElement[] proxyElts)
   {
      device  = simDeviceElt;

      for ( int i = 0; i < proxyElts.length; ++i )
      {
         proxies.add(proxyElts[i]);
      }

      findAliases(ctx);
   }

   /**
    * Device + proxy list + alias list constructor.  This constructor assumes
    * that the given lists of proxy and alias config elements are correct
    * (the proxies refer to the givem simulated device config element and the
    * aliases refer to the given list of proxies) and complete (the proxies in
    * the list are all those proxies in the context that refer to the given
    * simulated device config element and the aliases are all those in the
    * context that refer to those proxies).
    *
    * @param ctx                the context where the config element is used
    * @param simDeviceElt       the config element for a Juggler simulated
    *                           device type
    * @param proxyElts          the complete list of proxy config elements in
    *                           ctx that point to simDeviceElt
    * @param aliasElts          the complete list of alias config elements in
    *                           ctx that refer to the proxies in proxyElts
    */
   public SimDeviceConfig(ConfigContext ctx, ConfigElement simDeviceElt,
                          List proxyElts, List aliasElts)
   {
/*
      ConfigDefinition def = aliasElt.getDefinition();
      if ( ! def.getToken().equals(ALIAS_TYPE) )
      {
         throw new IllegalArgumentException("Invalid alias element type '" +
                                            def.getToken() + "'" +
                                            " (expected " + ALIAS_TYPE + ")");
      }
*/
      device  = simDeviceElt;
      proxies = proxyElts;
      aliases = aliasElts;
   }

   /**
    * Device + proxy list + alias list constructor.  This constructor assumes
    * that the given lists of proxy and alias config elements are correct
    * (the proxies refer to the givem simulated device config element and the
    * aliases refer to the given list of proxies) and complete (the proxies in
    * the list are all those proxies in the context that refer to the given
    * simulated device config element and the aliases are all those in the
    * context that refer to those proxies).
    *
    * @param ctx                the context where the config element is used
    * @param simDeviceElt       the config element for a Juggler simulated
    *                           device type
    * @param proxyElts          the complete array of proxy config elements in
    *                           ctx that point to simDeviceElt
    * @param aliasElts          the complete array of alias config elements in
    *                           ctx that refer to the proxies in proxyElts
    */
   public SimDeviceConfig(ConfigContext ctx, ConfigElement simDeviceElt,
                          ConfigElement[] proxyElts, ConfigElement[] aliasElts)
   {
      device  = simDeviceElt;

      for ( int i = 0; i < proxyElts.length; ++i )
      {
         proxies.add(proxyElts[i]);
      }

      for ( int i = 0; i < aliasElts.length; ++i )
      {
         aliases.add(aliasElts[i]);
      }
   }

   /**
    * Returns the list of aliases referring to this simulated device's
    * proxy config elements.
    */
   public List getAliases()
   {
      return aliases;
   }

   /**
    * Returns the list of proxies referring to this simulated device's config
    * element.
    */
   public List getProxies()
   {
      return proxies;
   }

   /**
    * Returns the config element for this simulated device's configuration.
    */
   public ConfigElement getDevice()
   {
      return device;
   }

   /**
    * Presents a string representation of this simulated device's
    * configuration.
    */
   public String toString()
   {
      return device.getName();
   }

   /**
    * Searches the context for any proxy that points to our config element.
    * Proxy config elements are determined by checking the element's config
    * definition for a parent of type EditorConstants.PROXY_TYPE.  This is a
    * slow search, but it is exhaustive.
    */
   private void findProxies(ConfigContext ctx)
   {
      ConfigBroker broker = new ConfigBrokerProxy();
      List elts = broker.getElements(ctx);

      for ( Iterator i = elts.iterator(); i.hasNext(); )
      {
         ConfigElement cur_elt = (ConfigElement) i.next();
         List parents = cur_elt.getDefinition().getParents();

         for ( Iterator j = parents.iterator(); j.hasNext(); )
         {
            String p = (String) j.next();
            if ( p.equals(PROXY_TYPE) )
            {
               ConfigElementPointer dev_ptr =
                  (ConfigElementPointer) cur_elt.getProperty(DEVICE_PROPERTY, 0);

               if ( dev_ptr.getTarget() != null &&
                    dev_ptr.getTarget().equals(device.getName()) )
               {
                  proxies.add(cur_elt);
               }
            }
         }
      }
   }

   private void findTypedProxies(ConfigContext ctx, String proxyTypeName)
   {
      ConfigBroker broker = new ConfigBrokerProxy();
      findTypedProxies(ctx, broker.getRepository().get(proxyTypeName));
   }

   private void findTypedProxies(ConfigContext ctx, ConfigDefinition proxyType)
   {
      ConfigBroker broker = new ConfigBrokerProxy();
      List proxy_elts =
         ConfigUtilities.getElementsWithDefinition(broker.getElements(ctx),
                                                   proxyType);

      for ( Iterator i = proxy_elts.iterator(); i.hasNext(); )
      {
         ConfigElement cur_elt = (ConfigElement) i.next();
         ConfigElementPointer dev_ptr =
            (ConfigElementPointer) cur_elt.getProperty(DEVICE_PROPERTY, 0);

         if ( dev_ptr.getTarget() != null &&
              dev_ptr.getTarget().equals(device.getName()) )
         {
            proxies.add(cur_elt);
         }
      }
   }

   private void findAliases(ConfigContext ctx)
   {
      ConfigBroker broker = new ConfigBrokerProxy();
      List elts = broker.getElements(ctx);
      List alias_elts = ConfigUtilities.getElementsWithDefinition(elts,
                                                                  ALIAS_TYPE);

      for ( Iterator i = alias_elts.iterator(); i.hasNext(); )
      {
         ConfigElement cur_elt = (ConfigElement) i.next();
         ConfigElementPointer proxy_ptr =
            (ConfigElementPointer) cur_elt.getProperty(PROXY_PROPERTY, 0);

         for ( Iterator j = proxies.iterator(); j.hasNext(); )
         {
            ConfigElement proxy = (ConfigElement) j.next();

            if ( proxy_ptr.getTarget() != null &&
                 proxy_ptr.getTarget().equals(proxy.getName()) )
            {
               aliases.add(cur_elt);
            }
         }
      }
   }

   private List          aliases = new ArrayList();
   private List          proxies = new ArrayList();
   private ConfigElement device  = null;
}
