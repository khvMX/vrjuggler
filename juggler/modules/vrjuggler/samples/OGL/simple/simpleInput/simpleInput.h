#ifndef _SIMPLE_INPUT
#define _SIMPLE_INPUT

#include <vrj/vjConfig.h>

#include <iostream>
#include <iomanip>

#include <vrj/Draw/OGL/GlApp.h>
#include <vrj/Math/Matrix.h>

#include <vrj/Input/Type/PosInterface.h>
#include <vrj/Input/Type/AnalogInterface.h>
#include <vrj/Input/Type/DigitalInterface.h>


using namespace vrj;

//----------------------------------------------------
//: Simple Input Demonstration application
//
// This application demonstrates getting and printing input
//
// NOTE: It is derived from GlApp only because App
//        is an abstract base class.
//----------------------------------------------------
class simpleInput : public GlApp
{
public:
   simpleInput()
   {;}

   virtual ~simpleInput (void) {;}

public: // ---- INITIALIZATION FUNCITONS ---- //
   // Execute any initialization needed before the API is started
   //
   // POST: Device interfaces are initialized with the device names
   //       we want to use
   //! NOTE: This is called once before OpenGL is initialized
   virtual void init()
   {
      std::cout << "---------- App:init() ---------------" << std::endl;
      // Initialize devices
      mWand.init("VJWand");
      mHead.init("VJHead");
      mButton0.init("VJButton0");
      mButton1.init("VJButton1");
   }

public:

   //: Called before start of frame
   //!NOTE: Function called after device updates but before start of drawing
   virtual void preFrame()
   {
      if(mButton0->getData())
         { std::cout << "Button 0 pressed" << std::endl; }
      if(mButton1->getData())
         { std::cout << "Button 1 pressed" << std::endl; }

      std::cout  << "Wand Buttons:"
                 << " 0:" << mButton0->getData()
                 << " 1:" << mButton1->getData()
                 << std::endl;

      // -- Get Wand matrix --- //
      Matrix wand_matrix;
      wand_matrix = *(mWand->getData());
      std::cout << "Wand pos: \n" << wand_matrix << std::endl;
   }

public:
   virtual void draw() {;}

public:
   PosInterface    mWand;         // Positional interface for Wand position
   PosInterface    mHead;         // Positional interface for Head position
   DigitalInterface   mButton0;   // Digital interface for button 0
   DigitalInterface   mButton1;   // Digital interface for button 1
};


#endif
