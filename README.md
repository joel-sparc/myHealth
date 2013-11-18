My Health by JBoss
========
Overview
--------
The purpose of this application is to provide a reference architecture that demonstrates some features of Fuse 6, Fuse Service Works, and MQTT on Android. A medical setting was chosen for this demonstration and use cases were designed to exercise a cooperation between medical facilities and care givers. The diagram below outlines the major components and the basic relationships between them.


![Overview Image](./overview.png "Reference Architecture Overview")

The **Hospital Information System** is the central management application that coordinates the flow of information between Ancillary Services and Patient Care Facilities.

The **Patient Registration Application** represents the software application that provides a way to enter information about newly admited patients into the Hospital Information System. In this architecture, the Patient Registration Application is hosted by the Patient Care Facility and employees of that facility are responsible for patient data entry.

The **Patient Care Facility** represents a hospital-affiliated medical practice. This could be a family medical practice or a pediatric practice, for example. In real-world applications, multiple Patient Care Facilities would likely be connected to a network of one or more Hospital Information Systems.

The **Ancillary Service** represents a specialized service provider. This could be a pharmacy or a laboratory, for example. In real-world applications, multiple Ancillary Services would likely be connected to a network of one or more Hospital Information Systems.

**Android** clients represent the smart devices that are used by doctors and nurses to place orders and request information.




This architecture currently supports the two specific use cases that are detailed in the following two sections.

Use Case 1
--------
In the first use case, a patient is admited


Use Case 2
--------
