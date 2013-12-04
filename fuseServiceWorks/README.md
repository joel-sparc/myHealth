HIS Installation
========
Introduction
--------
This document describes the process for installing the HIS Switchyard application on a JBoss Fuse Service Works 6.0 Beta server.


Installation Guide
--------

###Install the MySQL Connector
Before running the FSW installer, the MySQL Connector should be downloaded. Extract the connector jar to any folder.

###Create the Databases

From the mysql prompt, enter the following:
```
mysql> CREATE DATABASE HIS;
mysql> CREATE DATABASE HIS_DATA;
```

### JBoss Fuse Service Works 6.0 Beta download and installation

[Download a copy of Fuse Service Works 6.0 Beta](http://www.jboss.org/products/fsw.html)

Run the installer (java -jar jboss-eap-6.0.0.fsw.ci-installer.jar) and take all of the defaults throughout the installation process. Install into the \<base_dir\>/myHealth/fuseServiceWorks folder.

On the "Post-Install Configuration" screen select the option to "Perform additional post-install configuration". Choose "Install JDBC driver".

On the "JDBC Driver Setup" screen choose "MySQL" as the driver vendor and click the "Browse" button to add the MySQL connector jar that was installed earlier.

On the "Database Configuration" screen enter the credentials of the MySQL user. Enter the following in the Url text field:
```
jdbc:mysql://localhost:3306/HIS?transformedBitIsBoolean=true
```

In the end, there should be a new server directory named \<base_dir\>/myHealth/fuseServiceWorks/jboss-eap-6.1

### Configure the JBoss Fuse Service Works 6.0 Beta Server

In the \<base_dir\>/myHealth/fuseServiceWorks/jboss-eap-6.1/standalone/standalone-full.xml file, disable hornetq security by adding \<security-enabled\>false\</security-enabled\> to the \<hornetq-server\> element.

In the \<base_dir\>/myHealth/fuseServiceWorks/jboss-eap-6.1/standalone/standalone-full.xml file, create the HISDS data source by adding the element shown below to the datasources element. The "user" and "password" values should be replaced with the credentials of the MySQL user.
```
<datasource jndi-name="java:jboss/datasources/HISDS" pool-name="HISDS" enabled="true" use-java-context="true">
    <connection-url>jdbc:mysql://localhost:3306/HIS_DATA</connection-url>
    <driver>mysql</driver>
    <security>
        <user-name>*user*</user-name>
        <password>*password*</password>
    </security>
</datasource>
```

Open port 5455
```
su -c “iptables -I INPUT -p tcp ––dport 5455 -j ACCEPT”
su -c “/sbin/service iptables save”
```

### Start the JBoss Fuse Service Works 6.0 Beta Server

From a terminal, enter the following:
```
cd <base_dir>/myHealth/fuseServiceWorks/jboss-eap-6.1/bin
./standalone.sh -c standalone-full.xml
```


### Build and deploy the HIS application

From a terminal, enter the following:
```
cd <base_dir>/myHealth/fuseServiceWorks/his
mvn clean package
mvn jboss-as:deploy
```

Installation is complete.





