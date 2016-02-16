### 1.0.4 - Bug Fix

- Add: META-INF to distribution zip 
- Fix: #3, When a file in a subdirectory is first created by Slicy plugin gives an erroneous warning that file move failed
- Fix: #4, Need to have sticky balloon notifications if they contain errors
- Change: update Kotlin runtime to 1.0.0-release-IJ143-70 
- Change: only files located under the project base directory will be processed, otherwise multiple open projects would attempt to process the same files. Now only the project that contains the files under its base directory will process the files.  

### 1.0.3 - Min Java Version Fix & Kotlin Runtime

- Add: plugin version number to notification title 
- Fix: min java version 1.6 
- Add: Kotlin run-time jars to distribution

### 1.0.2 - Bug Fix

- Fix: #2, Change delete/rename and delete/copy operations used to process slicy files into copy contents then delete slicy generated file. Otherwise the files can become un-tracked under VCS because they were deleted.

### 1.0.1 - Bug Fix and Notification Cleanup

- Fix: #1, Handling of one level directory nesting of Slicy generated directories.
- Change: Cleaned up notification of nested files to be useful and cleaner, initial one looked more like a debug trace than a notification 
- Add: Screenshot to plugin information and readme files

### 1.0.0 - Initial Release

- Add: [Slicy] file renamer: from slicy's `_dark@2x.ext` to `@2x_dark.ext` format of IntelliJ 
- Add: [Slicy] file mover: move files created with sub-directory layer groups to files for IntelliJ icon naming conventions. see  [Readme]  

[Slicy]: http://www.macrabbit.com/slicy    
[Readme]: https://github.com/vsch/PluginDevelopersToolbox/blob/master/README.md
