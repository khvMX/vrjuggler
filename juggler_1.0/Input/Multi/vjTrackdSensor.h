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

#ifndef _VJ_TRACKD_SENSOR_H_
#define _VJ_TRACKD_SENSOR_H_

#include <vjConfig.h>
#include <Input/vjPosition/vjPosition.h>
#include <Input/Multi/aTrackdSensor.h>
#include <vector>


//-----------------------------------------------------------------------------
//: vjPosition-derived class for interfacing with trackd sensor data
//+ located on the local machine in a shared memory arena
//
//! NOTE: A note on reciever access:
//+  Clients of Juggler should access tracker recievers as [0-n].  For
//+  example, if you have recievers 1,2, and 4 with transmitter on 3, then
//+  you can access the data, in order, as 0, 1, 2.
//
// See also: vjPosition
//-----------------------------------------------------------------------------
//!PUBLIC_API:
class vjTrackdSensor : public vjPosition {
public:

    // ------------------------------------------------------------------------
    //: Constructor.
    // ------------------------------------------------------------------------
    vjTrackdSensor();

    // ------------------------------------------------------------------------
    //: Destructor.
    //
    //! PRE: None.
    //! POST: Shared memory is released
    // ------------------------------------------------------------------------
    ~vjTrackdSensor();

    // ------------------------------------------------------------------------
    //: Configure the trackd sensor with the given config chunk.
    //
    //! PRE: c must be a chunk that has trackd sensor config information
    //! POST: If c is a valid config chunk, the device is configured using its
    //+       contents.  Otherwise, configuration fails and false is returned
    //+       to the caller.
    //
    //! ARGS: c - A pointer to a vjTrackdSensor config chunk.
    //
    //! RETURNS: true - The device was configured succesfully.
    //! RETURNS: false - The config chunk is invalid.
    // ------------------------------------------------------------------------
    virtual bool config(vjConfigChunk* c);

   //: Begin sampling
   int startSampling() { return 1; }

   //: Stop sampling
   int stopSampling() { return 1; }

   //: Sample a value
   int sample() { return 1; }

    // ------------------------------------------------------------------------
    //: Update to the sampled data.
    //
    //! PRE: None.
    //! POST: Most recent value is copied over to temp area
    // ------------------------------------------------------------------------
    void updateData();

    //: Get the device name.
    inline char*
    getDeviceName () {
        return "vjTrackdSensor";
    }

    //: Return what chunk type is associated with this class.
    static std::string
    getChunkType () {
        return std::string("TrackdSensor");
    }

    // ------------------------------------------------------------------------
    //: Get the transform for the given sensor number.
    //
    //! RETURNS: NULL - The device is not active.
    //! RETURNS: Non-NULL - A pointer to the given sensor's matrix.
    //
    //! NOTE: Clients of Juggler should access tracker recievers as [0-n]
    //+  For example, if you have recievers 1,2, and 4 with transmitter on 3,
    //+  then you can access them, in order, as 0,1,2.
    // ------------------------------------------------------------------------
    vjMatrix* getPosData(int dev = 0);

private:
    aTrackdSensor*         mTrackdSensors;      // The sensors that we are dealing with
    std::vector<vjMatrix>   mCurSensorValues; // The current (up-to-date) values
};

#endif

