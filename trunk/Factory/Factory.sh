#!/bin/bash
#
# Copyright (c) 2005, 2006 David Lambert
# 
# This file is part of Factory.
# 
# Factory is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
# 
# Factory is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with Factory; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
# 
# $Source: /cvsroot/projectfactory/development/Factory.sh,v $
# $Revision: 1.1 $
# $Date: 2007/02/24 08:50:03 $
# $Author: ddlamb_2000 $
# 

PGNAME=Factory
JARFILE=$PGNAME.jar

start() {
    if [ -r "$JARFILE" ] ; then
        java -classpath $JARFILE $PGNAME
    elif [ -r "$CLASSFILE" ] ; then
        java $PGNAME
    else
        echo "Can start the process (files not found)."
        exit 1
    fi
}

start

