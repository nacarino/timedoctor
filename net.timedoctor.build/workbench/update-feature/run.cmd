@echo off
rem set ECLIPSE_BASE_LOCATION=
rem set BUILD_LAUNCH_CMD=
set BUILDTYPE=I
set BUILDID=TestBuild
set SKIPFETCH=true

rem YOU MAY NOT NEED TO EDIT ANYTHING BELOW THIS
rem set ARCHIVESFORMAT="*,*,* - Folder"
set BUILD_DIRECTORY=%CD%/build
set CONFIGS="*,*,*"
set ARCHIVEPREFIX=eclipse
set JAVACSOURCE=1.5
set JAVACTARGET=1.5
set BUILDER=%CD%/configuration

set BUILDFILE=%CD%/build.xml

%BUILD_LAUNCH_CMD% -application org.eclipse.ant.core.antRunner -DbaseLocation=%ECLIPSE_BASE_LOCATION% -DbuildDirectory=%BUILD_DIRECTORY% -Dconfigs=%CONFIGS% -DarchivePrefix=%ARCHIVEPREFIX% -DjavacSource=%JAVACSOURCE% -DjavacTarget=%JAVACTARGET% -DbuildId=%BUILDID% -DbuildType=%BUILDTYPE% -DskipFetch=%SKIPFETCH% -DgenerateFeatureVersionSuffix=true -Dbuilder=%BUILDER% -DoutputUpdateJars=true -buildfile %BUILDFILE%  > buildOutput.log 2>&1
