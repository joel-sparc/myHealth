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

Update the Android SDK
```
\<base_dir\>/myHealth/android/android-sdk-linux/tools/android update sdk --no-ui --obsolete --force
```

Install 32 bit libraries if necessary. For RHEL6:
```
su -c "yum install glibc.i686 glibc-devel.i686 zlib-devel.i686 ncurses-devel.i686 libX11-devel.i686 libXrender.i686 libXrandr.i686"
su -c "yum upgrade libstdc++"
su -c "yum install libstdc++.i686"
```

### FuseIDE setup

Open the Fuse IDE.

Right click anywhere in the Package Explorer View.

Import...

Maven -> Existing Maven Projects

Browse to \<base_dir\>/myHealth/android/redHatRA














