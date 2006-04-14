/*************** <auto-copyright.pl BEGIN do not edit this line> **************
 *
 * VR Juggler is (C) Copyright 1998-2006 by Iowa State University
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

#ifndef _GADGET_ASCENSION_WANDA_EXCEPTIONS_H_
#define _GADGET_ASCENSION_WANDA_EXCEPTIONS_H_

#include <exception>


namespace wanda
{

/**
 * Basic exception that may be thrown by the standalone Wanda driver.
 */
class WandaException : public std::exception
{
public:
   WandaException(const std::string& msg)
      : std::exception()
      , mMessage(msg)
   {
      /* Do nothing. */ ;
   }

   virtual ~WandaException() throw ()
   {
      /* Do nothing. */ ;
   }

   const char* what() const throw ()
   {
      return mMessage.c_str();
   }

private:
   std::string mMessage;
};

/**
 * An I/O exception type.
 */
class IOException : public WandaException
{
public:
   IOException(const std::string& msg = "")
      : WandaException(msg)
   {
      /* Do nothing. */ ;
   }

   virtual ~IOException() throw ()
   {
      /* Do nothing. */ ;
   }
};

/**
 * An I/O exception type.
 */
class TimeoutException : public wanda::IOException
{
public:
   TimeoutException(const std::string& msg = "")
      : wanda::IOException(msg)
   {
      /* Do nothing. */ ;
   }

   virtual ~TimeoutException() throw ()
   {
      /* Do nothing. */ ;
   }
};

/**
 * A generic protocol exception.
 */
class ProtocolException : public WandaException
{
public:
   ProtocolException(const std::string& msg = "")
      : WandaException(msg)
   {
      /* Do nothing. */ ;
   }

   virtual ~ProtocolException() throw ()
   {
      /* Do nothing. */ ;
   }
};

/**
 * An invalid button identifier was requested from the wanda driver.
 */
class InvalidButtonException : public WandaException
{
public:
   InvalidButtonException(const size_t buttonNum, const std::string& msg = "")
      : WandaException(msg)
      , mButtonNum(buttonNum)
   {
      /* Do nothing. */ ;
   }

   virtual ~InvalidButtonException() throw ()
   {
      /* Do nothing. */ ;
   }

   size_t getButtonNum() const
   {
      return mButtonNum;
   }

private:
   const size_t mButtonNum;
};

} // End of wanda namespace


#endif /* _GADGET_ASCENSION_WANDA_EXCEPTIONS_H_ */