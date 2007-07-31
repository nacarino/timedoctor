@echo off
set ECLIPSE_BASE_LOCATION=f:\Tools\eclipse-SDK-3.2\eclipse
set BUILD_LAUNCH_CMD=java -jar %ECLIPSE_BASE_LOCATION%\startup.jar

set ROOTDIR=%CD%
set SOURCEDIR=%ROOTDIR%\sources

set IDEFEATURE=%ROOTDIR%\ide\feature
set IDEUPDATEFEATURE=%ROOTDIR%\ide\update-feature
set WORKBENCHPRODUCT=%ROOTDIR%\workbench\product
set WORKBENCHFEATURE=%ROOTDIR%\workbench\update-feature

echo #########################################################################################################
echo Make sure you have copied all features and plugins into features\ and plugins\ 
echo directory into the %ROOTDIR%\sources directory
echo #########################################################################################################
pause

echo #########################################################################################################
cd %IDEFEATURE%
echo cleaning up files in %IDEFEATURE% ...
del /s /f /q build\* 2>NUL 1>NUL
echo copying files into %IDEFEATURE%\build\ ...
xcopy /e /q /r %SOURCEDIR%\* build\
echo Starting build ...
call run.cmd

if %ERRORLEVEL% NEQ 0 (
    echo Build failed for %IDEFEATURE%. Please refer %IDEFEATURE%\buildOutput.log for details
    echo Build failed for %IDEFEATURE%. Please refer %IDEFEATURE%\buildOutput.log for details >> %ROOTDIR%\FAILED.LOG
)

echo #########################################################################################################
cd %IDEUPDATEFEATURE%
echo cleaning up files in %IDEUPDATEFEATURE% ...
del /s /f /q build\* 2>NUL 1>NUL
echo copying files into %IDEUPDATEFEATURE%\build\ ...
xcopy /e /q /r %SOURCEDIR%\* build\
echo Starting build ...
call run.cmd

if %ERRORLEVEL% NEQ 0 (
    echo Build failed for %IDEUPDATEFEATURE%. Please refer %IDEUPDATEFEATURE%\buildOutput.log for details
    echo Build failed for %IDEUPDATEFEATURE%. Please refer %IDEUPDATEFEATURE%\buildOutput.log for details >> %ROOTDIR%\FAILED.LOG
)
echo #########################################################################################################

cd %WORKBENCHPRODUCT%
echo cleaning up files in %WORKBENCHPRODUCT% ...
del /s /f /q build\* 2>NUL 1>NUL
echo copying files into %WORKBENCHPRODUCT%\build\ ...
xcopy /e /q /r %SOURCEDIR%\* build\
echo Starting build ...
call run.cmd

if %ERRORLEVEL% NEQ 0 (
    echo Build failed for %WORKBENCHPRODUCT%. Please refer %WORKBENCHPRODUCT%\buildOutput.log for details
    echo Build failed for %WORKBENCHPRODUCT%. Please refer %WORKBENCHPRODUCT%\buildOutput.log for details >> %ROOTDIR%\FAILED.LOG
)
echo #########################################################################################################

cd %WORKBENCHFEATURE%
echo cleaning up files in %WORKBENCHFEATURE% ...
del /s /f /q build\* 2>NUL 1>NUL
echo copying files into %WORKBENCHFEATURE%\build\ ...
xcopy /e /q /r %SOURCEDIR%\* build\
echo Starting build ...
call run.cmd

if %ERRORLEVEL% NEQ 0 (
    echo Build failed for %WORKBENCHFEATURE%. Please refer %WORKBENCHFEATURE%\buildOutput.log for details
    echo Build failed for %WORKBENCHFEATURE%. Please refer %WORKBENCHFEATURE%\buildOutput.log for details >> %ROOTDIR%\FAILED.LOG
)
echo #########################################################################################################
cd %ROOTDIR%
