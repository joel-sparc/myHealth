My Health by JBoss
========
Introduction
--------
This document outlines the basic application architecture, describes each of the two supported use cases, and provides a set of installation prerequisites. Each major component within this repository also contains a README file to provide component-level overviews and installation instructions.

Overview
--------
The purpose of this application is to provide a reference architecture that demonstrates some features of Fuse 6, Fuse Service Works, and MQTT on Android. A medical setting was chosen for this demonstration and use cases were designed to exercise a cooperation between medical facilities and care givers. The diagram below outlines the major components and the basic relationships between them.


![Overview Image](./overview.png "Reference Architecture Overview")

The **Hospital Information System** is the central management application that coordinates the flow of information between Ancillary Services and Patient Care Facilities.

The **Patient Registration Application** represents the software application that provides a way to enter information about newly admited patients into the Hospital Information System. In this architecture, the Patient Registration Application is hosted by the Patient Care Facility and employees of that facility are responsible for patient data entry.

The **Patient Care Facility** represents a hospital-affiliated medical practice. This could be a family medical practice or a pediatric practice, for example. In real-world applications, multiple Patient Care Facilities would likely be connected to a network of one or more Hospital Information Systems.

The **Ancillary Service** represents a specialized service provider. This could be a pharmacy or a laboratory, for example. In real-world applications, multiple Ancillary Services would likely be connected to a network of one or more Hospital Information Systems.

**Android** clients represent the smart devices that are used by doctors and nurses to place orders and request information.




Use Case 1
--------
In the first use case, a patient is admited to the Patient Care Facility. As shown in the diagram below, patient information is submitted to the Patient Care Facility via a HAPI test panel (which is used to simulate a Patient Registration Application), stored by the Hospital Information System, and finally forwarded to the laboratory. The "lab" in the diagram is a "Laboratory Information System" and is used in this architecture to provide an example ancillary service.

![Use Case 1](./useCase1.png "Use Case 1")


Use Case 2
--------
In the second use case, a care giver requests a test from the lab. The request comes in to the Patient Care Facility from the Android client and is ultimately processed by the lab. Once the test has been performed, test results are sent back to the client. In order to build a more complete request, the Patient Care Facility calls upon the Hospital Information System's patient lookup service to provide additional patient information. In this demonstration, that additional patient information is simply the patient's last name which was stored in the database as part of the first use case.

![Use Case 2](./useCase2.png "Use Case 2")



