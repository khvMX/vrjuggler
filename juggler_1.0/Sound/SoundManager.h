
#ifndef _VRJUGGLER_SOUND_MANAGER_
#define _VRJUGGLER_SOUND_MANAGER_

#include <Kernel/vjConfigChunkHandler.h>

namespace vrj
{
   //: vrjuggler sound manager
   //!PUBLIC_API:
   class SoundManager : public vjConfigChunkHandler   
   {
   public:
      //: Add the chunk to the configuration
      //! PRE: configCanHandle(chunk) == true
      virtual bool configAdd( vjConfigChunk* chunk ) { return false; }

      //: Remove the chunk from the current configuration
      //! PRE: configCanHandle(chunk) == true
      virtual bool configRemove( vjConfigChunk* chunk ) { return false; }

      //: Can the handler handle the given chunk?
      //! RETURNS: true - Can handle it
      //+          false - Can't handle it
      virtual bool configCanHandle( vjConfigChunk* chunk ) { return false; }

   public:
      //: Enable a frame to be drawn
      virtual void update() {}

      //: Blocks until the end of the frame
      //! POST: The frame has been rendered
      virtual void sync() {}
   };
}; // end namespace

#endif   /* _VRJUGGLER_SOUND_MANAGER_ */
