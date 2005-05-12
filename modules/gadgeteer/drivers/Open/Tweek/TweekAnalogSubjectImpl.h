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
 * -----------------------------------------------------------------
 * File:          $RCSfile$
 * Date modified: $Date$
 * Version:       $Revision$
 * -----------------------------------------------------------------
 *
 *************** <auto-copyright.pl END do not edit this line> ***************/

#ifndef _GADGET_TWEEK_ANALOG_SUBJECT_IMPL_H_
#define _GADGET_TWEEK_ANALOG_SUBJECT_IMPL_H_

#include <gadget/Devices/DriverConfig.h>

#include <vpr/vpr.h>
#include <vpr/Sync/Mutex.h>
#include <vpr/Sync/Guard.h>
#include <tweek/CORBA/SubjectImpl.h>

#include <TweekAnalogSubject.h>


namespace gadget
{

class TweekGadget;

/**
 */
class TweekAnalogSubjectImpl : public POA_gadget::TweekAnalogSubject,
                               public tweek::SubjectImpl
{
public:
   /**
    * Default constructor.
    */
   TweekAnalogSubjectImpl(TweekGadget* device) : mMyDev(device), mValue(0.0f)
   {
      ;
   }

   /**
    * Default destructor.
    */
   virtual ~TweekAnalogSubjectImpl()
   {
      ;
   }

   virtual void setValue(CORBA::Float state);

   virtual CORBA::Float getValue()
   {
      vpr::Guard<vpr::Mutex> guard(mValueLock);
      return mValue;
   }

   gadget::TweekAnalogSubject_ptr _this()
   {
      return POA_gadget::TweekAnalogSubject::_this();
   }

private:
   // These two have to be here because Visual C++ will try to make them
   // exported public symbols.  This causes problems because copying
   // vpr::Mutex objects is not allowed.
   TweekAnalogSubjectImpl(const TweekAnalogSubjectImpl& subj)
      : omniServant(subj)
      , tweek::_impl_Subject(subj)
      , gadget::_impl_TweekAnalogSubject(subj)
      , PortableServer::ServantBase(subj)
      , POA_tweek::Subject(subj)
      , POA_gadget::TweekAnalogSubject(subj)
      , tweek::SubjectImpl(subj)
   {
      /* Do nothing. */ ;
   }

   void operator=(const TweekAnalogSubjectImpl& subj)
   {
      /* Do nothing. */ ;
   }

   TweekGadget* mMyDev;

   /** */
   CORBA::Float mValue;
   vpr::Mutex mValueLock;
};

} // End of gadget namespace


#endif /* _GADGET_TWEEK_ANALOG_SUBJECT_IMPL_H_ */