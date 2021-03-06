LIS and PCF Installation
========
Introduction
--------
This document describes the process for installing the pcf and lis features on a JBoss Fuse 6 server.

PCF Installation
--------

### JBoss Fuse 6.0 download and installation

[Download a copy of JBoss Fuse 6.0](http://www.jboss.org/products/fuse)

Extract the contents of jboss-fuse-full-6.0.0.redhat-024.zip into \<base_dir\>/myHealth/fuse

This should result in a new server directory named \<base_dir\>/myHealth/fuse/jboss-fuse-6.0.0.redhat-024

### Configure the Server

Copy all of the files from \<base_dir\>/myHealth/fuse/etc/ into the \<base_dir\>/myHealth/fuse/jboss-fuse-6.0.0.redhat-024/etc folder replacing any existing files. The pcf.cfg file is the configuration file for the pcf feature. If the Hospital Information System is to be installed on a different machine then these two files will need to be modified accordingly. The activemq.xml file configures the embedded broker.

Update the /etc/hosts file adding the name given to the local machine as a synonym of 127.0.0.1
For example, if the local machine name is my.rhel.machine (use the hostname command to get the name of the local machine) then the hosts file would need an entry that looks similar to the one below.
```
127.0.0.1   localhost localhost.localdomain localhost4 localhost4.localdomain4 my.rhel.machine
```

Open ports 8888 and 1883
```
su -c “iptables -I INPUT -p tcp ––dport 8888 -j ACCEPT”
su -c “iptables -I INPUT -p tcp ––dport 1883 -j ACCEPT”
su -c “/sbin/service iptables save”
```

### Build the pcf feature

From a terminal, enter the following:
```
cd <base_dir>/myHealth/fuse/
mvn clean install
```
This will build the feature and install it to the local maven repository.

### Start the JBoss Fuse 6.0 Instance

From a terminal, enter the following:
```
cd <base_dir>/myHealth/fuse/jboss-fuse-6.0.0.redhat-02
./bin/fuse
```

Once the server has started, ensure that there are no errors by entering the following command into the Karaf console
```
JBossFuse:karaf@root>log:display

```
###Install the pcf feature

Enter the following commands into the Karaf console
```
JBossFuse:karaf@root> features:addUrl mvn:com.sparc.myHealth/pcf-features/1.0.0-SNAPSHOT/xml/features
JBossFuse:karaf@root> features:install pcf

```

Ensure once again that there are no errors by entering the following command into the Karaf console
```
JBossFuse:karaf@root>log:display

```

Ensure that all bundles are installed by entering the following command into the Karaf console
```
JBossFuse:karaf@root> osgi:list
```
The end of the list should show that the 7 required bundles are installed:
```
[ 240] [Active     ] [            ] [       ] [   60] The Netty Project (3.2.4.Final)
[ 241] [Active     ] [            ] [       ] [   60] wrap_mvn_org.hornetq_hornetq-core_2.2.7.Final (0)
[ 242] [Active     ] [            ] [       ] [   60] wrap_mvn_org.hornetq_hornetq-core-client_2.2.7.Final (0)
[ 243] [Active     ] [            ] [       ] [   60] wrap_mvn_org.hornetq_hornetq-jms-client_2.2.7.Final (0)
[ 244] [Active     ] [            ] [Started] [   60] HL7-in (1.0.0.SNAPSHOT)
[ 245] [Active     ] [            ] [Started] [   60] Android-in (1.0.0.SNAPSHOT)
[ 246] [Active     ] [            ] [Started] [   60] Android-out (1.0.0.SNAPSHOT)
```



Installation is complete. Use ctrl-d to stop the server.



LIS Installation
--------

### JBoss Fuse 6.0 download and installation

[Download a copy of JBoss Fuse 6.0](http://www.jboss.org/products/fuse)

Extract the contents of jboss-fuse-full-6.0.0.redhat-024.zip into \<base_dir\>/myHealth/fuse

This should result in a new server directory named \<base_dir\>/myHealth/fuse/jboss-fuse-6.0.0.redhat-024

### Configure the Server

Copy all of the files from \<base_dir\>/myHealth/fuse/etc/ into the \<base_dir\>/myHealth/fuse/jboss-fuse-6.0.0.redhat-024/etc folder replacing any existing files. The lis.cfg file is the configuration file for the lis feature. If the Hospital Information System is to be installed on a different machine then these two files will need to be modified accordingly. The activemq.xml file configures the embedded broker.

Update the /etc/hosts file adding the name given to the local machine as a synonym of 127.0.0.1
For example, if the local machine name is my.rhel.machine (use the hostname command to get the name of the local machine) then the hosts file would need an entry that looks similar to the one below.
```
127.0.0.1   localhost localhost.localdomain localhost4 localhost4.localdomain4 my.rhel.machine
```

### Build the lis feature

From a terminal, enter the following:
```
cd <base_dir>/myHealth/fuse/
mvn clean install
```
This will build the feature and install it to the local maven repository.

### Start the JBoss Fuse 6.0 Instance

From a terminal, enter the following:
```
cd <base_dir>/myHealth/fuse/jboss-fuse-6.0.0.redhat-02
./bin/fuse
```

Once the server has started, ensure that there are no errors by entering the following command into the Karaf console
```
JBossFuse:karaf@root>log:display

```
###Install the lis feature

Enter the following commands into the Karaf console
```
JBossFuse:karaf@root> features:addUrl mvn:com.sparc.myHealth/lis-features/1.0.0-SNAPSHOT/xml/features
JBossFuse:karaf@root> features:install lis

```

Ensure once again that there are no errors by entering the following command into the Karaf console
```
JBossFuse:karaf@root>log:display

```

Ensure that all bundles are installed by entering the following command into the Karaf console
```
JBossFuse:karaf@root> osgi:list
```
The end of the list should show that the 5 required bundles are installed:
```
[ 240] [Active     ] [            ] [       ] [   60] The Netty Project (3.2.4.Final)
[ 241] [Active     ] [            ] [       ] [   60] wrap_mvn_org.hornetq_hornetq-core_2.2.7.Final (0)
[ 242] [Active     ] [            ] [       ] [   60] wrap_mvn_org.hornetq_hornetq-core-client_2.2.7.Final (0)
[ 243] [Active     ] [            ] [       ] [   60] wrap_mvn_org.hornetq_hornetq-jms-client_2.2.7.Final (0)
[ 244] [Active     ] [            ] [Started] [   60] HL7-Handler (1.0.0.SNAPSHOT)
```



Installation is complete. Use ctrl-d to stop the server.





