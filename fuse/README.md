PCF/LIS Installation
========
Introduction
--------
This document describes the process for installing the pcf and lis features on a JBoss Fuse 6 server. While the pcf and lis features are installed on the same JBoss Fuse 6 instance in this guide, they could also be installed on separate JBoss Fuse 6 instances.

Installation Guide
--------

### JBoss Fuse 6.0 download and installation

[Download a copy of JBoss Fuse 6.0](http://www.jboss.org/products/fuse)

Extract the contents of jboss-fuse-full-6.0.0.redhat-024.zip into \<base_dir\>/myHealth/fuse

This should result in a new server directory named \<base_dir\>/myHealth/fuse/jboss-fuse-6.0.0.redhat-024

### Configure the JBoss Fuse 6.0 Instance

Copy all of the files from \<base_dir\>/myHealth/fuse/etc/ into the \<base_dir\>/myHealth/fuse/jboss-fuse-6.0.0.redhat-024/etc folder replacing any existing files. The pcf.cfg file is the configuration file for the pcf feature and the lis.cfg file is the configuration file for the lis feature. If the Hospital Information System is to be installed on a different machine then these two files will need to be modified accordingly. The activemq.xml file configures the embedded broker.

### Build the pcf and lis features

From a terminal, enter the following:
```
cd \<base_dir\>/myHealth/fuse/
mvn install
```
This will build the two features and install them to the local maven repository.

### Start the JBoss Fuse 6.0 Instance

From a terminal, enter the following:
```
cd \<base_dir\>/myHealth/fuse/jboss-fuse-6.0.0.redhat-02
./bin/fuse
```

Once the server has started, ensure that there are no errors by entering the following command into the Karaf console
```
JBossFuse:karaf@root>log:display

```
Now install the pcf and lis features by entering the following command into the Karaf console
```
JBossFuse:karaf@root> features:addUrl mvn:com.sparc.myHealth/pcf-features/1.0.0-SNAPSHOT/xml/features
JBossFuse:karaf@root> features:addUrl mvn:com.sparc.myHealth/lis-features/1.0.0-SNAPSHOT/xml/features
JBossFuse:karaf@root> features:install pcf
JBossFuse:karaf@root> features:install lis

```

Ensure once again that there are no errors by entering the following command into the Karaf console
```
JBossFuse:karaf@root>log:display

```





