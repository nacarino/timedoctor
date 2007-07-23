#!/bin/bash

set ECLIPSE_BASE_LOCATION=
set BUILDTYPE=I
set BUILDID=TestBuild
set SKIPFETCH=true
set CONFIG="linux, gtk, x86"
# set ARCHIVESFORMAT="linux, gtk, x86 - Folder"

# YOU MAY NOT NEED TO EDIT ANYTHING BELOW THIS
set BUILD_DIRECTORY=$PWD/build
set PLUGIN_PATH=$PWD/build
set BUILDER=$PWD/configuration
set BUILDFILE=$PWD/build.xml
set PRODUCT_PATH=$PLUGIN_PATH/features/com.nxp.timedoctor.feature.workbench/workbench.product
set JAVACSOURCE=1.5
set JAVACTARGET=1.5

java -jar $ECLIPSE_BASE_LOCATION/startup.jar -application org.eclipse.ant.core.antRunner -Dconfigs=$CONFIG -DarchivePrefix=TimeDoctor -DoutputUpdateJars=false -DbuildDirectory=$BUILD_DIRECTORY -Dbuilder=$BUILDER -Dproduct=$PRODUCT_PATH -DbaseLocation=$ECLIPSE_BASE_LOCATION -DbuildId=$BUILDID -DbuildType=$BUILDTYPE -DskipFetch=$SKIPFETCH -DpluginPath=$PLUGIN_PATH -DjavacSource=$JAVACSOURCE -DjavacTarget=$JAVACTARGET -buildfile $BUILDFILE > buildOutput-linux.log 2>&1

