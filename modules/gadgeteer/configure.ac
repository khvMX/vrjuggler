# ************** <auto-copyright.pl BEGIN do not edit this line> **************
#
# VR Juggler is (C) Copyright 1998-2006 by Iowa State University
#
# Original Authors:
#   Allen Bierbaum, Christopher Just,
#   Patrick Hartling, Kevin Meinert,
#   Carolina Cruz-Neira, Albert Baker
#
# This library is free software; you can redistribute it and/or
# modify it under the terms of the GNU Library General Public
# License as published by the Free Software Foundation; either
# version 2 of the License, or (at your option) any later version.
#
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Library General Public License for more details.
#
# You should have received a copy of the GNU Library General Public
# License along with this library; if not, write to the
# Free Software Foundation, Inc., 59 Temple Place - Suite 330,
# Boston, MA 02111-1307, USA.
#
# -----------------------------------------------------------------
# File:          $RCSfile$
# Date modified: $Date$
# Version:       $Revision$
# -----------------------------------------------------------------
#
# *************** <auto-copyright.pl END do not edit this line> ***************

# -----------------------------------------------------------------------------
# Base configure.in for Gadgeteer.
# -----------------------------------------------------------------------------
# This file is "compiled" by GNU autoconf to generate the configure script
# that is actually run.
# -----------------------------------------------------------------------------

AC_INIT([Gadgeteer], [1.1], [vrjuggler-devel@lists.sourceforge.net])
AC_CONFIG_HEADERS([gadget/gadgetDefines.h])
AC_PREREQ([2.53])
AC_COPYRIGHT([VR Juggler is (C) Copyright 1998-2006 by Iowa State University])
AC_CONFIG_SRCDIR([gadget/gadgetConfig.h])
AC_REVISION($Revision$)
AC_CONFIG_AUX_DIR([../../share/config])
DPP_PREREQ([2.1.0])
DPP_INIT

# Generate the reconfig script first so that it's easy to run configure again
# if it fails later on.
DPP_GEN_RECONFIG

# -----------------------------------------------------------------------------
# Command-line arguments (--enable-option, --with-pkg=package_name).
# -----------------------------------------------------------------------------

# ------------------------------------------------------ #
# --enable-feature[=arg] and --disable-feature arguments #
# ------------------------------------------------------ #

AC_ARG_ENABLE([xwindows],
              [  --enable-xwindows       Use X11 for windowing code ],
              [XWIN_UI="$enableval"], [XWIN_UI='no'])

DPP_STD_CPP(yes)

# -------------------------------------------- #
# --with-pkg[=arg] and --without-pkg arguments #
# -------------------------------------------- #

# Allow forcing the use of GCC as the compiler.
DPP_WITH_GCC(no)

# Define the binary format.
DPP_ABI_SETUP

# -----------------------------------------------------------------------------
# System-dependent stuff.
# -----------------------------------------------------------------------------
DPP_WIN32_SETUP
DPP_SYSTEM_SETUP

# Templates for Autoheader.
AH_TEMPLATE([_CMA_NOWRAPPERS_], [Define if building on HP-UX.])

# Depending on the target operating system, set various command options and
# such.
case $target_os in
   # Darwin, aka Mac OS X.
   darwin*)
      if test "x$XWIN_UI" != "xyes" ; then
         AC_MSG_WARN([WARNING: Forcing the use of the X Window System. The Carbon support is currently unusable.])
         XWIN_UI='yes'
      fi
      ;;
   # SGI running IRIX 6.*.
   irix6*)
      if test "x$USE_GCC" != "xyes" ; then
         DBG_FLAGS="$DBG_FLAGS -gslim"
      fi
      ;;
   # HP PA-RISC machine running HP-UX 10.20.
   hpux10.20)
      AC_DEFINE(_CMA_NOWRAPPERS_,)
      ;;
   # HP PA-RISC machine running HP-UX 11.x.
   hpux11*)
      AC_DEFINE(_CMA_NOWRAPPERS_,)
      ;;
esac

# -----------------------------------------------------------------------------
# Path setup.
# -----------------------------------------------------------------------------

# $srcdir is the root directory of the juggler source tree.  To get a value for
# $GADGETROOT_ABS, we cd there and save the value of running pwd.  Then return
# to the directory where configure is being run ($topdir).
cd "$srcdir"
GADGETROOT_ABS=`pwd`

cd "$GADGETROOT_ABS/../.."
UNIX_JUGGLERROOT_ABS=`pwd`
cd "$topdir"

if test "x$CYGPATH" != "xno" ; then
   JUGGLERROOT_ABS=`cygpath -w "$UNIX_JUGGLERROOT_ABS" | sed -e 's/\\\\/\\//g'`
else
   JUGGLERROOT_ABS="$UNIX_JUGGLERROOT_ABS"
fi

# -----------------------------------------------------------------------------
# Checks for programs.
# -----------------------------------------------------------------------------
VJ_COMPILER_SETUP
VJ_PROG_CC_PROF_FLAG(yes)
VJ_PROG_CXX_PROF_FLAG(yes)

# Ensure that the C++ compiler we've found is capable of compiling the newer
# newer C++ features that we need.
DPP_CXX_NAMESPACE([AC_MSG_ERROR([*** The library requires C++ namesapce support ***])])
DPP_CXX_HAVE_STD
DPP_CXX_INLINE([AC_MSG_ERROR([*** The library requires C++ inline support ***])])
DPP_CXX_RTTI([AC_MSG_ERROR([*** The library requires C++ RTTI support ***])])
DPP_CXX_STATIC_CAST([AC_MSG_ERROR([*** The library requires C++ static_cast<> ***])])
DPP_CXX_DYNAMIC_CAST([AC_MSG_ERROR([*** The library requires C++ dynamic_cast<> ***])])

# Ensure that a version of Perl greater than or equal to 5.004 is available.
DPP_PERL_VER(5.004, , , [AC_MSG_ERROR([*** Perl is required ***])])
AC_CHECK_PROG(MTREE_CMD, mtree, mtree, [\$(PERL) \$(scriptdir)/mtree.pl])
DPP_HAVE_GNU_MAKE(3.78, ,
    [AC_MSG_ERROR([*** The build system requires GNU make 3.78 or newer ***])])
DPP_BASIC_PROGS($PLATFORM, $OS_TYPE)
DPP_PROG_INSTALL
VJ_LINKER_SETUP

# -----------------------------------------------------------------------------
# Checks for libraries.
# -----------------------------------------------------------------------------
VPR_PATH([1.1.16], , [AC_MSG_ERROR([*** VPR required for Gadgeteer ***])])
VPR_SUBSYSTEM=`$VPR_CONFIG --subsystem`
JCCL_PATH_CXX([1.1.2], ,
              [AC_MSG_ERROR([*** JCCL C++ API required for Gadgeteer ***])])

gadsave_CPPFLAGS="$CPPFLAGS"
CPPFLAGS="$CPPFLAGS $STDFLAGS"
GMTL_PATH(0.1.9, [/usr/local], , ,
          [AC_MSG_ERROR([*** GMTL required for Gadgeteer ***])])
CPPFLAGS="$gadsave_CPPFLAGS"

AC_PATH_XTRA

gadget_use_x11='no'

if test "x$have_x" = "xyes" ; then
   AH_TEMPLATE([GADGET_USE_X11], [Define if the X Window System will be used])

   if test "x$PLATFORM" = "xDarwin" ; then
      if test "x$XWIN_UI" = "xyes" ; then
         gadget_use_x11='yes'
         AC_DEFINE(GADGET_USE_X11,)
      fi
   elif test "x$OS_TYPE" = "xUNIX" ; then
      gadget_use_x11='yes'
      AC_DEFINE(GADGET_USE_X11,)
   fi
# If --enable-xwindows was passed on the command line but X11 was not found,
# then we have a problem.
elif test "x$XWIN_UI" = "xyes" ; then
   AC_MSG_ERROR([*** Use of the X Window System requested, but it was not found ***])
fi

# -----------------------------------------------------------------------------
# Checks for header files.
# -----------------------------------------------------------------------------
AC_HEADER_STDC
AC_CHECK_HEADERS([sys/time.h unistd.h])

# -----------------------------------------------------------------------------
# Checks for typedefs, structures, and compiler characteristics.
# -----------------------------------------------------------------------------
AC_TYPE_SIZE_T

# -----------------------------------------------------------------------------
# Checks for library functions.
# -----------------------------------------------------------------------------
AC_CHECK_FUNCS(strdup strerror)

# -----------------------------------------------------------------------------
# Miscellaneous checks.
# -----------------------------------------------------------------------------
DPP_INSTALLER(vrjuggler, 0644, 0755, 0755)

# -----------------------------------------------------------------------------
# Do Makefile substitutions.
# -----------------------------------------------------------------------------

# Add these C++ options when compiling with G++.
#if test "x$GXX" = "xyes" ; then
#   CXXFLAGS="-fhonor-std $CXXFLAGS"
#fi

CXXFLAGS="$CXXFLAGS $CXX_VISIBILITY_FLAGS $VPR_CXXFLAGS_MIN $JCCL_CXXFLAGS_MIN $GMTL_INCLUDES"
BASE_APP_LINK_FLAGS="$STDFLAGS $BASE_APP_LINK_FLAGS"
DEPEND_FLAGS="$VPR_INCLUDES_MIN $JCCL_INCLUDES_MIN $GMTL_INCLUDES"
gadget_cxxflags="$STDFLAGS"

if test "x$PLATFORM" = "xIRIX" -a "x$USE_GCC" != "xyes" ; then
#    CFLAGS="$CFLAGS -woff 1685,515,608,658,799,803,852,1048,1233,1499"
    CXXFLAGS="$CXXFLAGS -w2"
    BASE_APP_LINK_FLAGS="$BASE_APP_LINK_FLAGS -J4 -all"
elif test "x$PLATFORM" = "xDarwin" ; then
    changequote(<<, >>)
    vpr_gcc_major=`echo $dpp_gcc_ver | sed -e 's/^\([0-9]*\)\..*/\1/'`
    changequote([, ])

    # GCC 4.0 in Darwin removed -fexport-coalesced.
    if test $vpr_gcc_major -lt 4 ; then
       CXXFLAGS_DYNLIB="$CXXFLAGS_DYNLIB -fexport-coalesced"
    fi

    BASE_APP_LINK_FLAGS="$BASE_APP_LINK_FLAGS -Wl,-bind_at_load"
elif test "x$OS_TYPE" = "xWin32" -a "x$USE_GCC" != "xyes" ; then
    vc_cflags='/QIfdiv /QI0f'
    vc_cxxflags='/GR /GX /EHc /QIfdiv /QI0f'
    CFLAGS="$CFLAGS $vc_cflags"
    CXXFLAGS="$CXXFLAGS $vc_cxxflags"
    BASE_APP_LINK_FLAGS="$BASE_APP_LINK_FLAGS /nologo /incremental:no"
    DBG_FLAGS="$DBG_FLAGS /MDd"
    OPT_FLAGS="$OPT_FLAGS /MD"
    gadget_cxxflags="$vc_cxxflags $gadget_cxxflags"
fi

# For makedepend(1) to work properly on HP-UX with aCC, we have to include
# these extra paths.
if test "x$PLATFORM" = "xHP" ; then
    _aCC_ROOT="/opt/aCC"
    DEPEND_EXTRAS="$DEPEND_EXTRAS -I${_aCC_ROOT} -I${_aCC_ROOT}/include -I${_aCC_ROOT}/include/iostream"
elif test "x$OS_TYPE" = "xWin32" ; then
    DEPEND_EXTRAS="$DEPEND_EXTRAS -D__cplusplus"
fi

# Define the base path to the source directory using $(GADGETROOT_ABS) as an
# alternative to using $(srcdir).
UNIX_GADGETROOT_ABS="$GADGETROOT_ABS"

# $APP_EXTRA_LIBS must list all the libraries need to link a Gadgeteer-based
# application.
if test "x$OS_TYPE" = "xWin32" ; then
    APP_EXTRA_LIBS="$LDFLAGS $JCCL_LIBS_LD_MIN $JCCL_EXTRA_LIBS_LD_MIN $VPR_LIBS_LD_MIN $LIBS $VPR_EXTRA_LIBS_LD_MIN comctl32.lib gdi32.lib user32.lib"
    APP_EXTRA_PROF_LIBS="$APP_EXTRA_LIBS"
else
    APP_EXTRA_LIBS="$LDFLAGS $JCCL_LIBS_CC_MIN $JCCL_EXTRA_LIBS_CC_MIN $VPR_LIBS_CC_MIN $LIBS $DTK_LDFLAGS $VPR_EXTRA_LIBS_CC_MIN"
    APP_EXTRA_PROF_LIBS="$LDFLAGS $JCCL_PROF_LIBS_CC_MIN $JCCL_EXTRA_LIBS_CC_MIN $VPR_PROF_LIBS_CC_MIN $LIBS $DTK_LDFLAGS $VPR_EXTRA_LIBS_CC_MIN"

    if test "x$gadget_use_x11" = "xyes" ; then
       APP_EXTRA_LIBS="$APP_EXTRA_LIBS $X_PRE_LIBS $X_LIBS -lX11 $X_EXTRA_LIBS"
       APP_EXTRA_PROF_LIBS="$APP_EXTRA_PROF_LIBS $X_PRE_LIBS $X_LIBS -lX11 $X_EXTRA_LIBS"
    elif test "x$PLATFORM" = "xDarwin" ; then
       APP_EXTRA_LIBS="$APP_EXTRA_LIBS -framework Carbon"
       APP_EXTRA_PROF_LIBS="$APP_EXTRA_PROF_LIBS -framework Carbon"
    fi
fi

if test "x$OS_TYPE" = "xUNIX" ; then
   if test "x$gadget_use_x11" = "xyes" ; then
      INCLUDES="$INCLUDES $X_CFLAGS"
      gadget_cxxflags="$gadget_cxxflags $X_CFLAGS"
      APP_EXTRA_LIBS_X11="$X_PRE_LIBS $X_LIBS -lX11 $X_EXTRA_LIBS"
   fi
fi

# Remove duplicated arguments from each of the following.  This can help speed
# up compile times, and it reduces the (perceived) complexity of the commands.
# These steps are performed here because no further additions are made to any
# of these variables after this point.
VJ_STRIP_DUPLICATE_ARGS(CFLAGS, [$CFLAGS])
VJ_STRIP_DUPLICATE_ARGS(CXXFLAGS, [$CXXFLAGS])
VJ_STRIP_DUPLICATE_ARGS(DEPEND_FLAGS, [$DEPEND_FLAGS])
VJ_STRIP_DUPLICATE_ARGS(DEPEND_EXTRAS, [$DEPEND_EXTRAS])
VJ_STRIP_DUPLICATE_ARGS(INCLUDES, [$INCLUDES])
VJ_STRIP_DUPLICATE_ARGS(gadget_cxxflags, [$gadget_cxxflags])

# Put together the basic information needed to compile Gadgeteer applications.
VJ_APP_COMPILER($CC, $CFLAGS, $CXX, $CXXFLAGS, $DBG_FLAGS, $OPT_FLAGS,
                GADGET_BASE_DIR, $DEFS, $INCLUDES, ${_EXTRA_FLAGS})
VJ_APP_LINKER([$CXX], [$BASE_APP_LINK_FLAGS], [$LDOPTS_DBG], [$LDOPTS_OPT],
              [GADGET_BASE_DIR], [gadget], [$APP_EXTRA_LIBS])

APP_BASE_DIR='$(topdir)/instlinks'
APP_BASE_DIR_INST='$(GADGET_BASE_DIR)'

DYLIB_DEPS="$APP_EXTRA_LIBS"
DYLIB_PROF_DEPS="$APP_EXTRA_PROF_LIBS"

# Translate paths from UNIX-style to Win32.
if test "x$OS_TYPE" = "xWin32" ; then
    GADGETROOT_ABS=`dospath -p "$GADGETROOT_ABS"`
    JUGGLERROOT_ABS=`dospath -p "$JUGGLERROOT_ABS"`

    DEPEND_EXTRAS=`dospath "$DEPEND_EXTRAS"`
    INCLUDES=`dospath "$INCLUDES"`
    LIBS=`dospath "$LIBS"`
    LN_S='cp -r'
    RM_LN='rm -rf'
    MTREE_CMD=`dospath "$MTREE_CMD"`

#    APP_EXTRA_LIBS=`dospath "$APP_EXTRA_LIBS"`
else
    RM_LN='rm -f'
    GADGETROOT_ABS="$GADGETROOT_ABS"
    JUGGLERROOT_ABS="$JUGGLERROOT_ABS"
fi

# Information needed to generate gadgeteer-config.
case $OS_TYPE in
    UNIX)
        if test "x$GNU_LD" = "xyes" ; then
            static_begin="-Wl,-Bstatic"
            static_end="-Wl,-Bdynamic"
            gadget_ldflags_compiler="-L\$prefix/lib\$LIBBITSUF -Wl,--export-dynamic"
            gadget_ldflags_linker="-L\$prefix/lib\$LIBBITSUF --export-dynamic"
        else
            static_begin="-B static"
            static_end="-B dynamic"
            gadget_ldflags_compiler="-L\$prefix/lib\$LIBBITSUF"
            gadget_ldflags_linker="$gadget_ldflags_compiler"
        fi

        gadget_libs='-lgadget'
        gadget_prof_libs='-lgadget_p'
        gadget_extra_libs="$LDFLAGS $LIBS $DTK_LDFLAGS $APP_EXTRA_LIBS_X11"

        Gadgeteer_lib='-lgadget'
        ;;
    Win32)
        gadget_ldflags_compiler="/link /libpath:\$prefix/lib/debug "
        gadget_ldflags_linker="/libpath:\$prefix/lib/debug "
        gadget_libs='gadget.lib'
        gadget_prof_libs='gadget.lib'
        gadget_extra_libs="$LDFLAGS $LIBS $DTK_LDFLAGS comctl32.lib gdi32.lib user32.lib"

        Gadgeteer_lib='gadget.lib'
        ;;
esac

if test "x$PLATFORM" = "xIRIX" -a "x$USE_GCC" != "xyes" ; then
    gadget32_flags='-n32'
    gadget64_flags='-64'
elif test "x$GCC" = "xyes" ; then
    gadget32_flags='-m32'
    gadget64_flags='-m64'
fi

gadget_extra_ldflags_compiler="$APP_EXTRA_LIBS_BEGIN"
gadget_extra_ldflags_linker="$gadget_extra_ldflags_compiler"

VJ_VERSION_GROK(VERSION)

DPP_SUBST

AC_SUBST(topdir)
AC_SUBST(UNIX_GADGETROOT_ABS)
AC_SUBST(UNIX_JUGGLERROOT_ABS)
AC_SUBST(GADGETROOT_ABS)
AC_SUBST(JUGGLERROOT_ABS)
AC_SUBST(GADGET_SHARE_DIR)
AC_SUBST(VPR_SUBSYSTEM)

AC_SUBST(EXTRA_LDFLAGS)
AC_SUBST(RM_LN)

AC_SUBST(APP_BASE_DIR)
AC_SUBST(APP_BASE_DIR_INST)

AC_SUBST(DYLIB_DEPS)
AC_SUBST(DYLIB_PROF_DEPS)

AC_SUBST(APP_EXTRA_LIBS_X11)

AC_SUBST(static_begin)
AC_SUBST(static_end)
AC_SUBST(gadget_cxxflags)
AC_SUBST(gadget_extra_cxxflags)
AC_SUBST(gadget_ldflags_compiler)
AC_SUBST(gadget_ldflags_linker)
AC_SUBST(gadget_libs)
AC_SUBST(gadget_prof_libs)
AC_SUBST(Gadgeteer_lib)
AC_SUBST(gadget_extra_ldflags_compiler)
AC_SUBST(gadget_extra_ldflags_linker)
AC_SUBST(gadget_extra_libs)
AC_SUBST(gadget32_flags)
AC_SUBST(gadget64_flags)
AC_SUBST(gadget_use_x11)

# -----------------------------------------------------------------------------
# Final file generation step.
# -----------------------------------------------------------------------------

VJ_MTREE_LIB_GEN(GADGET, mtree, $PLATFORM, $ISA)

AC_CONFIG_FILES([
   gadgeteer-config
   Makefile
   Makefile.inc
   common.defs.mk
   make.defs.mk
   cluster/Makefile
   cluster/Packets/Makefile
   gadget/Makefile
   gadget/Devices/Makefile
   gadget/Devices/KeyboardMouseDevice/Makefile
   gadget/Devices/Sim/Makefile
   gadget/Filter/Makefile
   gadget/Filter/Position/Makefile
   gadget/Type/Makefile
   gadget/Type/KeyboardMouse/Makefile
   gadget/Type/Position/Makefile
   gadget/Util/Makefile
   test/Makefile
   mtree/GADGET.data.dist
   mtree/GADGET.include.dist
   mtree/GADGET.install.dist
   mtree/GADGET.samples.dist
   mtree/GADGET.test.dist
   mtree/GADGET.tools.dist
   VARS.pl
   ],
   [chmod a+x gadgeteer-config])

AC_OUTPUT