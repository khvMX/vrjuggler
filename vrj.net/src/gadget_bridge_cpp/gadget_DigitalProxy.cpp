/*************** <auto-copyright.pl BEGIN do not edit this line> **************
 *
 * VRJ.NET is (C) Copyright 2004 by Patrick Hartling
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

// Generated from Revision: 1.56 of RCSfile: class_cxx.tmpl,v



#include "sharppy.h"
#include <gadget/Type/DigitalProxy.h>

extern "C"
{
   // Copy constructor wrapper.
   SHARPPY_API gadget::DigitalProxy* gadget_DigitalProxy_DigitalProxy__gadget_DigitalProxy(const gadget::DigitalProxy* p)
   {
      gadget::DigitalProxy* obj = new gadget::DigitalProxy(*p);
      return obj;
   }

   // Constructor wrapper.
   SHARPPY_API gadget::DigitalProxy* gadget_DigitalProxy_DigitalProxy__()
   {
      gadget::DigitalProxy* obj = new gadget::DigitalProxy();
      return obj;
   }

   SHARPPY_API void delete_gadget_DigitalProxy(gadget::DigitalProxy* self_)
   {
      delete self_;
   }

   // Wrapper for non-virtual method gadget::DigitalProxy::getTimeStamp()
   SHARPPY_API vpr::Interval* gadget_DigitalProxy_getTimeStamp__(gadget::DigitalProxy* self_)
   {
      vpr::Interval* result;
      result = new vpr::Interval(self_->getTimeStamp());

      return result;
   }

   // Wrapper for non-virtual method gadget::DigitalProxy::getData()
   SHARPPY_API int gadget_DigitalProxy_getData__(gadget::DigitalProxy* self_)
   {
      int result;
      result = self_->getData();

      return result;
   }

   // Wrapper for non-virtual method gadget::DigitalProxy::getDigitalData()
   SHARPPY_API gadget::DigitalData* gadget_DigitalProxy_getDigitalData__(gadget::DigitalProxy* self_)
   {
      gadget::DigitalData* result;
      result = self_->getDigitalData();

      return result;
   }

   // Wrapper for non-virtual method gadget::DigitalProxy::getDigitalPtr()
   SHARPPY_API gadget::Digital* gadget_DigitalProxy_getDigitalPtr__(gadget::DigitalProxy* self_)
   {
      gadget::Digital* result;
      result = self_->getDigitalPtr();

      return result;
   }

   // Wrapper for non-virtual method gadget::DigitalProxy::getUnit()
   SHARPPY_API int gadget_DigitalProxy_getUnit__(gadget::DigitalProxy* self_)
   {
      int result;
      result = self_->getUnit();

      return result;
   }



   // Wrapper for virtual method gadget::DigitalProxy::updateData()
   SHARPPY_API void gadget_DigitalProxy_updateData__(gadget::DigitalProxy* self_)
   {
      self_->updateData();
   }

   // Wrapper for virtual method gadget::DigitalProxy::config()
   SHARPPY_API bool gadget_DigitalProxy_config__boost_shared_ptr_jccl__ConfigElement(gadget::DigitalProxy* self_, boost::shared_ptr<jccl::ConfigElement> p0)
   {
      bool result;
      result = self_->config(p0);

      return result;
   }

   // Wrapper for virtual method gadget::DigitalProxy::getProxiedInputDevice()
   SHARPPY_API gadget::Input* gadget_DigitalProxy_getProxiedInputDevice__(gadget::DigitalProxy* self_)
   {
      gadget::Input* result;
      result = self_->getProxiedInputDevice();

      return result;
   }


   // Wrapper for static method gadget::DigitalProxy::getElementType()
   SHARPPY_API char* gadget_DigitalProxy_getElementType__()
   {
      char* result;
      std::basic_string<char,std::char_traits<char>,std::allocator<char> > temp_result;
      temp_result = gadget::DigitalProxy::getElementType();
      result = strdup(temp_result.c_str());

      return result;
   }



} // extern "C" for gadget::DigitalProxy


