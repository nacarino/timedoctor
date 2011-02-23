This directory contains files for building TimeDoctor IDE and Workbench from command line.

Contents:
ide/
	feature/ contains files and run.cmd for building feature-release for TimeDoctor ide
ide/
	update-feature/ contains files and run.cmd for building update-feature for TimeDoctor ide
sources/
	features/
	plugins/
	Empty directory to copy TimeDoctor features and plugins to. masterBuild.cmd uses the 
	plugins	and features in these directories before buidling
workbench/
	product/ contains files and run.cmd (run.sh for *nix) to build TimeDoctor RCP application
	update-feature/ contains files and run.cmd for building update-feature for TimeDoctor RCP	
masterBuild.cmd
	windows script that calls run.cmd in each of the above directories to initiate the build
	
For fine-tuning, please modify run.cmd in each of the above directories	

NOTE: Be sure to have the RCP-delta-pack installed in the base Eclipse used to build.

NOTE: currently, the build is based on copying plugins manually. However, this can be further 
automated using automatic fetch from CVS using map files 
(please refer "Fetching from Repositories" topic:
http://help.eclipse.org/help32/topic/org.eclipse.pde.doc.user/guide/tasks/pde_fetch_phase.htm)

References:

Building an RCP application from a product configuration file
http://help.eclipse.org/help32/topic/org.eclipse.pde.doc.user/guide/tasks/pde_product_build.htm

Building features
http://help.eclipse.org/help32/topic/org.eclipse.pde.doc.user/guide/tasks/pde_feature_build.htm

Advanced PDE Build topics (contains source-build, version generation fetching code from repositories etc)
http://help.eclipse.org/help32/topic/org.eclipse.pde.doc.user/guide/tasks/pde_build_advanced_topics.htm
