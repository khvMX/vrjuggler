/*************** <auto-copyright.pl BEGIN do not edit this line> **************
 *
 * VR Juggler is (C) Copyright 1998, 1999, 2000 by Iowa State University
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

#ifndef _VPR_IOSYS_FAKE_H_
#define _VPR_IOSYS_FAKE_H_

#include <vpr/vprConfig.h>

// determine which implementation of vprSystem to include.
#if defined(VPR_USE_NSPR)
#   include <vpr/md/NSPR/IO/IOSysNSPR.h>

namespace vpr {
    typedef IOSysNSPR IOSys;
};
#elif defined(VPR_USE_IRIX_SPROC) || defined(VPR_USE_PTHREADS)
#   include <vpr/md/POSIX/IO/IOSysUnix.h>

namespace vpr {
    typedef IOSysUnix IOSys;
};
#elif defined(VPR_USE_WIN32)
#   include <vpr/md/WIN32/IO/IOSysWin32.h>

namespace vpr {
    typedef IOSysWin32 IOSys;
};
#endif

#endif   /* _VPR_IOSYS_FAKE_H_ */
