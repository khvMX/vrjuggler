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

#ifndef _GADGET_FASTRAK_
#define _GADGET_FASTRAK_

#include <gadget/Devices/DriverConfig.h>

#include <iostream>
#include <string>
#include <gmtl/Matrix.h>

#include <vpr/vpr.h>
#include <vpr/Thread/Thread.h>
#include <jccl/Config/ConfigElementPtr.h>

#include <gadget/Type/InputDevice.h>

#include "FastrakStandalone.h"


namespace gadget
{

class Fastrak
   : public InputDevice<Position>
{
public:
   /**
    * Constructor.
    *
    * @param port Serial port to connect to Fastrak on.
    * @param baud Speed to communicate with Fastrak at. (38400, 19200, 9600, 14400, etc.)
    */
   Fastrak(const char* port = "/dev/ttyS0", const int baud = 115200);

   virtual ~Fastrak();

   // return what element type is associated with this class.
   static std::string getElementType();

public:
   /// gadget::Input pure virtual functions
   virtual bool config(jccl::ConfigElementPtr fastrakElement);

   /** Starts a new thread. */
   virtual bool startSampling();

   /** Reads data from the Fastrak. */
   virtual bool sample();

   /** Swaps the data and gadget::Input tri-buffered indicies. */
   virtual void updateData();

   /** Kills the sample thread. */
   virtual bool stopSampling();

private:
   void controlLoop();

   int mButtonState;                   /**< only one button on station 0 */
   vpr::Thread* mSampleThread;
   bool mExitFlag;

   char* mConfigFile;

   FastrakStandalone mFastrak;
};

} // End of gadget namespace


#endif // _GADGET_FASTRAK_
