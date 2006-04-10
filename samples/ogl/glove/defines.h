/*
 * VRJuggler
 *   Copyright (C) 1997,1998,1999,2000
 *   Iowa State University Research Foundation, Inc.
 *   All Rights Reserved
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
 */

#ifndef MACROS_AND_DEFINES_FOR_GLOVE_APP
#define MACROS_AND_DEFINES_FOR_GLOVE_APP

// Macros and defines:
namespace gloveDefined
{
    //: MAX macro
    //  returns - the maximum of a, b, and c.
    template<class T> 
    inline const T& nMax( const T& a, const T& b, const T& c )
	     { return (a>=b) ? ((a>=c)?a:c) : ((b>=c)?b:c); }
    
    //: MIN macro
    //  returns - the minimum of a, b, and c.
    template<class T> 
    inline const T& nMin( const T& a, const T& b, const T& c )
	     { return (a<=b) ? ((a<=c)?a:c) : ((b<=c)?b:c); }
    
    // Converts a number in radians to a number in degrees
    const double    TO_DEG_D         = 57.2957795131;
    
    // Converts a number in radians to a number in degrees
    const float     TO_DEG_F         = 57.2957795131f;
    
    // Converts a number in degrees to a number in radians
    const double    TO_RAD_D         = 0.01745329252;
    
    // Converts a number in degrees to a number in radians
    const float     TO_RAD_F         = 0.01745329252f;
};

using namespace gloveDefined;

#endif