/****************** <VPR heading BEGIN do not edit this line> *****************
 *
 * VR Juggler Portable Runtime
 *
 * Original Authors:
 *   Allen Bierbaum, Patrick Hartling, Kevin Meinert, Carolina Cruz-Neira
 *
 * -----------------------------------------------------------------
 * File:          $RCSfile$
 * Date modified: $Date$
 * Version:       $Revision$
 * -----------------------------------------------------------------
 *
 ****************** <VPR heading END do not edit this line> ******************/

/*************** <auto-copyright.pl BEGIN do not edit this line> **************
 *
 * VR Juggler is (C) Copyright 1998-2005 by Iowa State University
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
 *************** <auto-copyright.pl END do not edit this line> ***************/

#include <vpr/vprConfig.h>

#include <iomanip>
#include <algorithm>
#include <boost/filesystem/exception.hpp>
#include <boost/filesystem/operations.hpp>

#include <vpr/Util/Debug.h>
#include <vpr/Util/Assert.h>
#include <vpr/DynLoad/Library.h>

#include <vpr/DynLoad/LibraryLoader.h>


namespace fs = boost::filesystem;

namespace vpr
{

#ifdef VPR_DEBUG
static const std::string DSO_NAME_EXT("_d");
#else
static const std::string DSO_NAME_EXT("");
#endif

#if defined(VPR_OS_Win32)
static const std::string PATH_SEP("\\");
static const std::string DSO_FILE_EXT(".dll");
#else
static const std::string PATH_SEP("/");

#  if defined(VPR_OS_Darwin)
static const std::string DSO_FILE_EXT(".dylib");
#  else
static const std::string DSO_FILE_EXT(".so");
#  endif
#endif

vpr::LibraryPtr LibraryLoader::findDSO(const std::string& dsoBaseName,
                                       const std::vector<std::string>& searchPath)
{
   std::vector<fs::path> fs_path;
   makeBoostFsVector(searchPath, fs_path);
   return findDSO(dsoBaseName, fs_path);
}

vpr::LibraryPtr LibraryLoader::findDSO(const std::string& dsoBaseName,
                                       const std::vector<fs::path>& searchPath)
{
   vpr::LibraryPtr dso;

   const std::string dso_name = makeFullDSOName(dsoBaseName);

   for ( std::vector<fs::path>::const_iterator i = searchPath.begin();
         i != searchPath.end();
         ++i )
   {
      try
      {
         fs::path temp_path = *i / dso_name;

         vprDEBUG(vprDBG_ALL, vprDBG_VERB_LVL)
            << "[vpr::LibraryLoader::findDSO()] Looking for '"
            << temp_path.native_file_string() << "'\n" << vprDEBUG_FLUSH;

         if ( fs::exists(temp_path) )
         {
            dso = vpr::LibraryPtr(new vpr::Library(temp_path.native_file_string()));
            break;
         }
      }
      catch(fs::filesystem_error& fsEx)
      {
         vprDEBUG(vprDBG_ERROR, vprDBG_CRITICAL_LVL)
            << clrOutNORM(clrRED, "ERROR:")
            << " File system exception caught: " << fsEx.what()
            << std::endl << vprDEBUG_FLUSH;
      }
   }

   return dso;
}

vpr::ReturnStatus LibraryLoader::findDSOAndLookup(const std::string& dsoBaseName,
                                                  const std::vector<std::string>& searchPath,
                                                  const std::string& funcName,
                                                  boost::function1<bool, void*> callback,
                                                  vpr::LibraryPtr& dso)
{
   std::vector<fs::path> fs_path;
   makeBoostFsVector(searchPath, fs_path);
   return findDSOAndLookup(dsoBaseName, fs_path, funcName, callback, dso);
}

vpr::ReturnStatus LibraryLoader::findDSOAndLookup(const std::string& dsoBaseName,
                                                  const std::vector<fs::path>& searchPath,
                                                  const std::string& funcName,
                                                  boost::function1<bool, void*> callback,
                                                  vpr::LibraryPtr& dso)
{
   vpr::ReturnStatus status;
   bool load_attempted(false);

   dso = findDSO(dsoBaseName, searchPath);

   if ( dso.get() != NULL )
   {
      try
      {
         status = findEntryPoint(dso, funcName, callback);
         load_attempted = true;
      }
      catch(fs::filesystem_error& fsEx)
      {
         vprDEBUG(vprDBG_ERROR, vprDBG_CRITICAL_LVL)
            << clrOutNORM(clrRED, "ERROR:")
            << " File system exception caught: " << fsEx.what()
            << std::endl << vprDEBUG_FLUSH;
      }
   }

   // If no load was attempted, that means that the DSO does not exist in
   // searchPath.  We'll give it one last shot here using whatever features
   // the run-time loader has for searching on its own.
   if ( ! load_attempted )
   {
      const std::string dso_name = makeFullDSOName(dsoBaseName);

      vprDEBUG(vprDBG_ALL, vprDBG_STATE_LVL)
         << "[vpr::LibraryLoader::findDSOAndLookup()] Falling back on run-time loader search for '"
         << dso_name << "'\n" << vprDEBUG_FLUSH;
      vpr::LibraryPtr temp_dso = vpr::LibraryPtr(new vpr::Library(dso_name));
      status = findEntryPoint(temp_dso, funcName, callback);

      // If findEntryPoint succeeded, then store the vpr::LibraryPtr object
      // in the return storage.  Otherwise, we let it go out of scope and get
      // deleted.
      if ( ! status.failure() )
      {
         dso = temp_dso;
      }
   }

   return status;
}

vpr::ReturnStatus LibraryLoader::findEntryPoint(vpr::LibraryPtr dso,
                                                const std::string& funcName,
                                                boost::function1<bool, void*> callback)
{
   vprASSERT(dso.get() != NULL && "Invalid vpr::LibraryPtr object");

   vpr::ReturnStatus status;
   const int lib_name_width(50);
   bool had_to_load(false);

   // Load the DSO if it has not already been loaded.
   if ( ! dso->isLoaded() )
   {
      had_to_load = true;
      vprDEBUG(vprDBG_ALL, vprDBG_CONFIG_STATUS_LVL)
         << "Loading library: " << std::setiosflags(std::ios::right)
         << std::setfill(' ') << std::setw(lib_name_width) << dso->getName()
         << std::resetiosflags(std::ios::right) << "     " << vprDEBUG_FLUSH;

      dso->load();
   }

   if ( dso->isLoaded() )
   {
      vprDEBUG(vprDBG_ALL, vprDBG_STATE_LVL)
         << "Loaded DSO successfully.\n" << vprDEBUG_FLUSH;

      // Look up the named function in the DSO and store it in the pointer
      // called "entry_point".
      void* entry_point = dso->findSymbol(funcName);

      if ( NULL != entry_point )
      {
         // Only print this closing thing if we had to load the DSO ourselves.
         if ( had_to_load )
         {
            vprDEBUG_CONT(vprDBG_ALL, vprDBG_CONFIG_STATUS_LVL)
               << "[ " << clrSetNORM(clrGREEN) << "OK" << clrRESET << " ]\n"
               << vprDEBUG_FLUSH;
         }

         vprDEBUG(vprDBG_ALL, vprDBG_STATE_LVL)
            << "Got pointer to DSO entry point function.\n" << vprDEBUG_FLUSH;

         // Use the caller-provided callback function for actual invocation
         // of entry_point.  It is up to this function to cast creator to the
         // desired type and do the cool stuff with it.
         if ( ! callback(entry_point) )
         {
            status.setCode(vpr::ReturnStatus::Fail);
         }
      }
      else
      {
         // Only print this closing thing if we had to load the DSO ourselves.
         if ( had_to_load )
         {
            vprDEBUG_CONT(vprDBG_ALL, vprDBG_CONFIG_LVL)
               << "[ " << clrSetNORM(clrRED) << "FAILED lookup" << clrRESET
               << " ]\n" << vprDEBUG_FLUSH;
         }

         vprDEBUG(vprDBG_ALL, vprDBG_WARNING_LVL)
            << clrOutNORM(clrYELLOW, "WARNING")
            << ": Failed to look up entry point function '" << funcName
            << "' in DSO '" << dso->getName() << "'\n"
            << vprDEBUG_FLUSH;
         status.setCode(vpr::ReturnStatus::Fail);
      }
   }
   else
   {
      status.setCode(vpr::ReturnStatus::Fail);

      // Only print this closing thing if we had to load the DSO ourselves.
      if ( had_to_load )
      {
         vprDEBUG_CONT(vprDBG_ALL, vprDBG_CONFIG_LVL)
            << "[ " << clrSetNORM(clrRED) << "FAILED" << clrRESET << " ]\n"
            << vprDEBUG_FLUSH;
      }
   }

   return status;
}

std::string LibraryLoader::makeFullDSOName(const std::string& dsoBaseName)
{
   return dsoBaseName + DSO_NAME_EXT + DSO_FILE_EXT;
}

void LibraryLoader::makeBoostFsVector(const std::vector<std::string>& strVec,
                                      std::vector<fs::path>& boostFsVec)
{
   boostFsVec.resize(strVec.size());

   // Convert the vector of std::string objects to a vector of
   // boost::filesystem::path objects.
   for ( std::vector<std::string>::size_type i = 0; i < strVec.size(); ++i )
   {
      try
      {
         vprDEBUG(vprDBG_ALL, vprDBG_HVERB_LVL)
            << "[vpr::LibraryLoader::makeBoostFsVector()] Converting std::string '"
            << strVec[i] << "' to a boost::filesystem::path object.\n"
            << vprDEBUG_FLUSH;

         // Use a Boost FS path object here so that we can indicate that
         // native path names are allowed.
         boostFsVec[i] = fs::path(strVec[i], fs::native);
      }
      catch(fs::filesystem_error& fsEx)
      {
         vprDEBUG(vprDBG_ERROR, vprDBG_CRITICAL_LVL)
            << clrOutNORM(clrRED, "ERROR:")
            << " File system exception caught while converting\n"
            << vprDEBUG_FLUSH;
         vprDEBUG_NEXT(vprDBG_ERROR, vprDBG_CRITICAL_LVL)
            << "'" << strVec[i] << "'\n" << vprDEBUG_FLUSH;
         vprDEBUG_NEXT(vprDBG_ERROR, vprDBG_CRITICAL_LVL)
            << "to a Boost.Filesystem path.\n" << vprDEBUG_FLUSH;
         vprDEBUG_NEXT(vprDBG_ERROR, vprDBG_CRITICAL_LVL)
            << fsEx.what() << std::endl << vprDEBUG_FLUSH;
      }
   }
}

}
