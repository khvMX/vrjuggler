# -----------------------------------------------------------------------------
# $Id$
#
# This include file <vj.clean.mk> handles clean-up operations.  It adds a
# 'clean' target and a 'cleandepend' target.
# -----------------------------------------------------------------------------
# The Makefiles that include this file can add to the ${CLEAN_FILES} variable
# to dictate which files should be removed when 'make clean' is done.
# Common files that are likely to always be removed should be listed here.
#
# To provide a "local" 'clean' target in the file including this code,
# define ${_LOCAL_CLEAN} before including this file and define a target
# called '_clean'.  It will be run after the standard 'clean' target is
# executed.
#
# Example:
#     CLEAN_FILES += file1.o file2.o
# -----------------------------------------------------------------------------

CLEAN_FILES	= Makedepend core so_locations

clean:
	rm -f ${CLEAN_FILES}
	if [ -d "ii_files" ] ; then rm -r ii_files ; fi
ifdef _LOCAL_CLEAN
	${MAKE} _clean
endif

cleandepend:
	-rm -f .depend_done
