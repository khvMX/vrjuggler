#include <math.h>
#include <GL/gl.h>
#include <GL/glu.h>

#include <Math/vjVec3.h>
#include <Math/vjCoord.h>

#include <wandApp.h>


//----------------------------------------------
//  Draw the scene.  A box on the end of the wand
//----------------------------------------------

void wandApp::myDraw()
{
   //cout << "\n--- myDraw() ---\n";

   //cout << "HeadPos:" << vjCoord(*mHead->getData()).pos << "\t"
   //     << "WandPos:" << vjCoord(*mWand->getData()).pos << endl;

   glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
   glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
   glMatrixMode(GL_MODELVIEW);

      // -- Draw box on wand --- //
   vjMatrix* wandMatrix;
   wandMatrix = mWand->getData();


      // Build a matrix with only rotation around y in local wand system
   vjMatrix yRotMat;
   yRotMat = *wandMatrix;        // Set it to the wand matrix
   yRotMat.setTrans(0,0,0);

   float xRot, yRot, zRot;
   wandMatrix->getXYZEuler(xRot, yRot, zRot);
   vjMatrix mx,mxInverse;           // Inverse of the x component
   mx.makeRot(xRot,vjVec3(1,0,0));
   mxInverse.invert(mx);
   vjMatrix mz,mzInverse;           // Inverse of the z component
   mz.makeRot(zRot,vjVec3(0,0,1));
   mzInverse.invert(mz);

   yRotMat.preMult(mxInverse);

   yRotMat.getXYZEuler(xRot,yRot,zRot);

   //cerr << "yRot: " << yRot << endl;

   //yRotMat.postMult(mzInverse);

   //glPushMatrix();

   /*** Rotation for tracker coord system testing
   * glRotatef(-180.0f, 0.0,0.0,1.0);
   * glRotatef(-90,1.0,0.0,0.0);
   ****/
   glPushMatrix();
      // cout << "wand:\n" << *wandMatrix << endl;
      glPushMatrix();
         glMultMatrixf(wandMatrix->getFloatPtr());
         glColor3f(1.0f, 0.0f, 1.0f);
         glScalef(0.25f, 0.25f, 0.25f);
         drawCube();
      glPopMatrix();

         // A little laser pointer
      glLineWidth(5.0f);

      glPushMatrix();
         glMultMatrixf(yRotMat.getFloatPtr());
         glBegin(GL_LINES);
            glColor3f(1.0f, 0.0f, 0.0f);
            glVertex3f(0.0f, 0.0f, 0.0f);
            glVertex3f(0.0f, 0.0f, -10.0f);
         glEnd();
      glPopMatrix();


      // Draw Axis
      glDisable(GL_LIGHTING);
      glPushMatrix();
         glMultMatrixf(wandMatrix->getFloatPtr());    // Goto wand position

         vjVec3 x_axis(7.0f,0.0f,0.0f);
         vjVec3 y_axis(0.0f, 7.0f, 0.0f);
         vjVec3 z_axis(0.0f, 0.0f, 7.0f);
         vjVec3 origin(0.0f, 0.0f, 0.0f);

         glBegin(GL_LINES);
            glColor3f(1.0f, 0.0f, 0.0f);
	         glVertex3fv(origin.vec);
	         glVertex3fv(x_axis.vec);

            glColor3f(0.0f, 1.0f, 0.0f);
	         glVertex3fv(origin.vec);
	         glVertex3fv(y_axis.vec);

            glColor3f(0.0f, 0.0f, 1.0f);
	         glVertex3fv(origin.vec);
	         glVertex3fv(z_axis.vec);
         glEnd();
      glPopMatrix();
      glEnable(GL_LIGHTING);
   glPopMatrix();

}

void wandApp::initGLState()
{
   GLfloat light0_ambient[] = { 0.1f,  0.1f,  0.1f,  1.0f};
   GLfloat light0_diffuse[] = { 0.8f,  0.8f,  0.8f,  1.0f};
   GLfloat light0_specular[] = { 1.0f,  1.0f,  1.0f,  1.0f};
   GLfloat light0_position[] = {0.0f, 0.75f, 0.75f, 0.0f};

   GLfloat mat_ambient[] = { 0.7, 0.7,  0.7,  1.0};
   GLfloat mat_diffuse[] = { 1.0,  0.5,  0.8,  1.0};
   GLfloat mat_specular[] = { 1.0,  1.0,  1.0,  1.0};
   GLfloat mat_shininess[] = { 50.0};
   GLfloat mat_emission[] = { 1.0,  1.0,  1.0,  1.0};
   GLfloat no_mat[] = { 0.0,  0.0,  0.0,  1.0};

   glLightfv(GL_LIGHT0, GL_AMBIENT,  light0_ambient);
   glLightfv(GL_LIGHT0, GL_DIFFUSE,  light0_diffuse);
   glLightfv(GL_LIGHT0, GL_SPECULAR,  light0_specular);
   glLightfv(GL_LIGHT0, GL_POSITION,  light0_position);

   glMaterialfv( GL_FRONT, GL_AMBIENT, mat_ambient );
   glMaterialfv( GL_FRONT,  GL_DIFFUSE, mat_diffuse );
   glMaterialfv( GL_FRONT, GL_SPECULAR, mat_specular );
   glMaterialfv( GL_FRONT,  GL_SHININESS, mat_shininess );
   glMaterialfv( GL_FRONT,  GL_EMISSION, no_mat);

   glEnable(GL_DEPTH_TEST);
   glEnable(GL_NORMALIZE);
   glEnable(GL_LIGHTING);
   glEnable(GL_LIGHT0);
   glEnable(GL_COLOR_MATERIAL);
   glShadeModel(GL_SMOOTH);
}

//: Utility function for drawing a cube
void drawbox(GLdouble x0, GLdouble x1, GLdouble y0, GLdouble y1,
             GLdouble z0, GLdouble z1, GLenum type)
{
   static GLdouble n[6][3] = {
      {-1.0, 0.0, 0.0}, {0.0, 1.0, 0.0}, {1.0, 0.0, 0.0},
      {0.0, -1.0, 0.0}, {0.0, 0.0, 1.0}, {0.0, 0.0, -1.0}
   };
   static GLint faces[6][4] = {
      { 0, 1, 2, 3}, { 3, 2, 6, 7}, { 7, 6, 5, 4},
      { 4, 5, 1, 0}, { 5, 6, 2, 1}, { 7, 4, 0, 3}
   };
   GLdouble v[8][3], tmp;
   GLint i;

   if (x0 > x1)
   {
      tmp = x0; x0 = x1; x1 = tmp;
   }
   if (y0 > y1)
   {
      tmp = y0; y0 = y1; y1 = tmp;
   }
   if (z0 > z1)
   {
      tmp = z0; z0 = z1; z1 = tmp;
   }
   v[0][0] = v[1][0] = v[2][0] = v[3][0] = x0;
   v[4][0] = v[5][0] = v[6][0] = v[7][0] = x1;
   v[0][1] = v[1][1] = v[4][1] = v[5][1] = y0;
   v[2][1] = v[3][1] = v[6][1] = v[7][1] = y1;
   v[0][2] = v[3][2] = v[4][2] = v[7][2] = z0;
   v[1][2] = v[2][2] = v[5][2] = v[6][2] = z1;

   for (i = 0; i < 6; i++)
   {
      glBegin(type);
         glNormal3dv(&n[i][0]);
         glVertex3dv(&v[faces[i][0]][0]);
         glNormal3dv(&n[i][0]);
         glVertex3dv(&v[faces[i][1]][0]);
         glNormal3dv(&n[i][0]);
         glVertex3dv(&v[faces[i][2]][0]);
         glNormal3dv(&n[i][0]);
         glVertex3dv(&v[faces[i][3]][0]);
      glEnd();
   }
}
