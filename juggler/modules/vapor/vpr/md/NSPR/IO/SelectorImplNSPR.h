/****************** <VPR heading BEGIN do not edit this line> *****************
 *
 * VR Juggler Portable Runtime
 *
 * Original Authors:
 *   Allen Bierbaum, Patrick Hartling, Kevin Meinert, Carolina Cruz-Neira
 *
 * -----------------------------------------------------------------
 * File:          $RCSfile$
 * Date modified: $Date$
 * Version:       $Revision$
 * -----------------------------------------------------------------
 *
 ****************** <VPR heading END do not edit this line> ******************/

/*************** <auto-copyright.pl BEGIN do not edit this line> **************
 *
 * VR Juggler is (C) Copyright 1998-2003 by Iowa State University
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
 *************** <auto-copyright.pl END do not edit this line> ***************/

#ifndef _VPR_SELECTOR_IMPL_NSPR_H_
#define _VPR_SELECTOR_IMPL_NSPR_H_

#include <vpr/vprConfig.h>

#include <vector>
#include <map>
#include <string>
#include <prio.h>

#include <vpr/IO/SelectorBase.h>
#include <vpr/IO/IOSys.h>
#include <vpr/Util/Debug.h>
#include <vpr/Util/ReturnStatus.h>


namespace vpr
{

/**
 *  NSPR Implementation of ---> Cross-platform selection interface.
 *
 * A selector is used to wait on a set of Handles untils any of the
 * events occur that the user is interested in.
 *
 * Implementation site of the Selector_t bridge.
 */
class VPR_CLASS_API SelectorImplNSPR : public SelectorBase
{
public:
   // XXX: What should we do when we are adding a handler while
   //    also iterating through the current handles.  Bad things "may" happend
   // This happens ALL the time with acceptors because they add to the reactor as they
   // are executing an handleEvent themselves

   /**
    * Adds the given handle to the selector.
    *
    * @pre handle is a valid handle.
    * @post handle is added to the handle set, and initialized to a mask of
    *       no-events.
    */
   bool addHandle(IOSys::Handle handle, vpr::Uint16 mask=0);

   /**
    * Removes a handle from the selector.
    *
    * @pre handle is in the selector.
    * @post handle is removed from the set of valid handles.
    */
   bool removeHandle(IOSys::Handle handle);

   /**
    * Sets the event flags going in to the select to mask.
    */
   bool setIn(IOSys::Handle handle, vpr::Uint16 mask);

   /**
    * Gets the current in flag mask.
    */
   vpr::Uint16 getIn(IOSys::Handle handle);

   /**
    * Gets the current out flag mask.
    */
   vpr::Uint16 getOut(IOSys::Handle handle);

   /**
    * Select.
    *
    * @param numWithEvents Upon completion, this holds the number of items
    *                      that have events.
    * @param timeout  The number of msecs to select for (0 - don't wait).
    */
   vpr::ReturnStatus select(vpr::Uint16& numWithEvents, const vpr::Interval timeout = vpr::Interval::NoTimeout);

   /**
    * Gets the number of handles.
    */
   vpr::Uint16 getNumHandles()
   {
      return mPollDescs.size();
   }

   IOSys::Handle getHandle(vpr::Uint16 index)
   {
      return mPollDescs[index].fd;
   }

   bool containsHandle(IOSys::Handle handle)
   {
      return (getHandle(handle) != mPollDescs.end());
   }

protected:

   /**
    * Gets the index of the handle given.
    *
    * @return .end() if not found, else the index to the handle in mPollDescs.
    */
   std::vector<PRPollDesc>::iterator getHandle(PRFileDesc const* handle);

   PRUint16 convertMaskVprToNspr(vpr::Uint16 mask);
   vpr::Uint16 convertMaskNsprToVpr(PRUint16 mask);

protected:

   std::vector<PRPollDesc> mPollDescs;    /**< List of Poll Descriptions to pass to PR_Poll() */

/*
   struct PRPollDesc
   {
      PRFileDesc* fd;
      PRInt16 in_flags;
      PRInt16 out_flags;
   };
*/
};

} // End of vpr namespace


#endif  /* _VPR_SELECTOR_IMP_NSPR_H_ */