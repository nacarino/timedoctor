#!/bin/bash

ECLIPSE_BASE_LOCATION=/psc/proj/ssd/Ipa/Projects/timedoctor/tools/linux/eclipse-SDK-3.2.2/eclipse
BUILDTYPE=I
BUILDID=TestBuild
SKIPFETCH=true
CONFIG="linux, gtk, x86"
# set ARCHIVESFORMAT="linux, gtk, x86 - Folder"

# YOU MAY NOT NEED TO EDIT ANYTHING BELOW THIS
BUILD_DIRECTORY=$PWD/build
PLUGIN_PATH=$PWD/build
BUILDER=$PWD/configuration
BUILDFILE=$PWD/build.xml
PRODUCT_PATH=$PLUGIN_PATH/features/net.timedoctor.feature.workbench/workbench.product
JAVACSOURCE=1.5
JAVACTARGET=1.5

java -jar $ECLIPSE_BASE_LOCATION/startup.jar -application org.eclipse.ant.core.antRunner -Dconfigs="$CONFIG" -DarchivePrefix=TimeDoctor -DoutputUpdateJars=false -DbuildDirectory=$BUILD_DIRECTORY -Dbuilder=$BUILDER -Dproduct=$PRODUCT_PATH -DbaseLocation=$ECLIPSE_BASE_LOCATION -DbuildId=$BUILDID -DbuildType=$BUILDTYPE -DskipFetch=$SKIPFETCH -DpluginPath=$PLUGIN_PATH -DjavacSource=$JAVACSOURCE -DjavacTarget=$JAVACTARGET -buildfile $BUILDFILE 2>&1 | tee buildOutput-linux.log 

