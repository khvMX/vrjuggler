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


#include <vjConfig.h>
#include <VPR/Sync/vjBarrier.h>


// ---------------------------------------------------------------------------
// Block the caller until all <count> threads have called <wait> and then
// allow all the caller threads to continue in parallel.
// ---------------------------------------------------------------------------
int
vjBarrier::wait (void) {
    vjGuard<vjMutex> guard(mutex);

    vjSubBarrier* curvjSubBarrier = this->subBarrier[currentGeneration];

    // Check for shutdown...
    if (curvjSubBarrier == NULL) {
        return -1;
    }

    if (curvjSubBarrier->runningThreads == 1)
    {
        // We're the last running thread, so swap generations and tell
        // all the threads waiting on the barrier to continue on their
        // way.

        curvjSubBarrier->runningThreads = this->count;

        // Swap generations.
        currentGeneration = 1 - this->currentGeneration;	    // Cycles between 0 and 1
        curvjSubBarrier->barrierFinished.broadcast();
    }
    else
    {
        --curvjSubBarrier->runningThreads;

        // Block until all the other threads wait().
        while (curvjSubBarrier->runningThreads != count)
            curvjSubBarrier->barrierFinished.wait ();
    }

    return 0;
}