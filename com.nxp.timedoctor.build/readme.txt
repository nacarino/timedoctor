This directory contains files for building TimeDoctor IDE and Workbench from commandline.

Contents:
ide/
	feature/ contains files and run.cmd for building feature-release for TimeDoctor ide
ide/
	update-feature/ contains files and run.cmd for building update-feature for TimeDoctor ide
sources/
	features/
	plugins/
	Empty directory to copy TimeDoctor features and plugins to. masterBuild.cmd uses the plugins
	and features in these directories before buidling
workbench/
	product/ contains files and run.cmd (run.sh for *nix) to build TimeDoctor RCP application
	update-feature/ contains files and run.cmd for building update-feature for TimeDoctor RCP	
masterBuild.cmd
	windows script that calls run.cmd in each of the above directories to initiate the build
	
For fine-tuning, please modify run.cmd in each of the above directories	
