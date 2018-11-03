# Version History

[TOC]: # " "

- [1.1.6 - Bug Fix](#116---bug-fix)
- [1.1.4 - Fix 2016.3 Compatibility](#114---fix-20163-compatibility)
- [1.1.2 - Fix 2016.3 Compatibility](#112---fix-20163-compatibility)
- [1.1.0 - Add Project Settings](#110---add-project-settings)
- [1.0.7 - Minor Tweaks & Bug Fix](#107---minor-tweaks--bug-fix)
- [1.0.6 - Minor Tweaks & Bug Fix](#106---minor-tweaks--bug-fix)
- [1.0.5 - Bug Fix](#105---bug-fix)
- [1.0.4 - Bug Fix](#104---bug-fix)
- [1.0.3 - Min Java Version Fix & Kotlin Runtime](#103---min-java-version-fix--kotlin-runtime)
- [1.0.2 - Bug Fix](#102---bug-fix)
- [1.0.1 - Bug Fix and Notification Cleanup](#101---bug-fix-and-notification-cleanup)
- [1.0.0 - Initial Release](#100---initial-release)


### 1.1.6 - Bug Fix

* Fix: plugin project settings were instantiated as service instead of project component.

### 1.1.4 - Fix 2016.3 Compatibility

* Fix: enable/disable setting was ignored

### 1.1.2 - Fix 2016.3 Compatibility

* Fix: 2016.3 compatibility
* Change: since build to 2016.3

### 1.1.0 - Add Project Settings

* Add Project settings to enable plugin operation. Disabled by default.

### 1.0.7 - Minor Tweaks & Bug Fix

- Fix: slicy files in newly created directories that ended in `_dark@2x` but did not equal this
  string would not be properly processed.

### 1.0.6 - Minor Tweaks & Bug Fix

- Add: different color to newly created files
- Fix: slicy files in newly created directories would be ignored unless the directory name was
  used as a splice to the file name.

### 1.0.5 - Bug Fix

- Add: skipping of files whose content has not changed to prevent unnecessary VCS caused
  thrashing.

### 1.0.4 - Bug Fix

- Add: META-INF to distribution zip
- Fix: #3, When a file in a subdirectory is first created by Slicy plugin gives an erroneous
  warning that file move failed
- Fix: #4, Need to have sticky balloon notifications if they contain errors
- Change: update Kotlin runtime to 1.0.0-release-IJ143-70
- Change: only files located under the project base directory will be processed, otherwise
  multiple open projects would attempt to process the same files. Now only the project that
  contains the files under its base directory will process the files.

### 1.0.3 - Min Java Version Fix & Kotlin Runtime

- Add: plugin version number to notification title
- Fix: min java version 1.6
- Add: Kotlin run-time jars to distribution

### 1.0.2 - Bug Fix

- Fix: #2, Change delete/rename and delete/copy operations used to process slicy files into copy
  contents then delete slicy generated file. Otherwise the files can become un-tracked under VCS
  because they were deleted.

### 1.0.1 - Bug Fix and Notification Cleanup

- Fix: #1, Handling of one level directory nesting of Slicy generated directories.
- Change: Cleaned up notification of nested files to be useful and cleaner, initial one looked
  more like a debug trace than a notification
- Add: Screenshot to plugin information and readme files

### 1.0.0 - Initial Release

- Add: [Slicy] file renamer: from slicy's `_dark@2x.ext` to `@2x_dark.ext` format of IntelliJ
- Add: [Slicy] file mover: move files created with sub-directory layer groups to files for
  IntelliJ icon naming conventions. see [Readme]

[Readme]: https://github.com/vsch/PluginDevelopersToolbox/blob/master/README.md
[Slicy]: http://www.macrabbit.com/slicy

