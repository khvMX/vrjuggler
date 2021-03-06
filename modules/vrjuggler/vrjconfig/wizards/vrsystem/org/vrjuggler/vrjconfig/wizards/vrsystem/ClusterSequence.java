/*************** <auto-copyright.pl BEGIN do not edit this line> **************
 *
 * VR Juggler is (C) Copyright 1998-2011 by Iowa State University
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
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301, USA.
 * 
 *************** <auto-copyright.pl END do not edit this line> ***************/

package org.vrjuggler.vrjconfig.wizards.vrsystem;

import java.util.*;
import javax.swing.*;

import org.vrjuggler.tweek.wizard.*;

public class ClusterSequence
   extends WizardSequence
{
   /**
    * Construct a sequence of steps which will allow us to configure a
    * clustered VR Juggler simulator.
    */
   public ClusterSequence()
   {
      //NodeSettingsStep ns = new NodeSettingsStep();
      //CreateClusteredSimDevicesStep ccs = new CreateClusteredSimDevicesStep();

      //add(ns);
      //add(ccs);
   }
   /**
    * For now we will default to always entering this wizard sequence.
    */
   protected boolean shouldEnter()
   {
      if (null == mWhiteBoard)
      {
         System.out.println("WhiteBoard is null.");
      }
      Object obj = mWhiteBoard.get("cluster_configuration");
      if (null == obj)
      {
         return true;
      }
      boolean val = ((Boolean)obj).booleanValue();
      return val;
   }
}
