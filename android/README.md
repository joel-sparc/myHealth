Android Client Installation
========
Introduction
--------
This document describes the process for setting up the Android client software to run on a device simulator.

Installation Guide
--------

### Install the Android SDK

[Download a copy of the Android SDK](http://developer.android.com/sdk/index.html)

Extract the contents of the compressed sdk folder into \<base_dir\>/myHealth/android

This should result in a new server directory named \<base_dir\>/myHealth/android/android-sdk-linux

Open the pom.xml file and set the android.sdk.path property as shown below (replace BASE_DIR appropriately) and save it.
```
<android.sdk.path>BASE_DIR/myHealth/android/android-sdk-linux</android.sdk.path>
```


Open the Fuse IDE.

Right click anywhere in the Package Explorer View.

Select Import...

Select Maven -> Existing Maven Projects

Browse to \<base_dir\>/myHealth/android/redHatRA
