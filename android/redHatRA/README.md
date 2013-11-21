The Android Client
========
Introduction
--------
This document outlines the basic architecture of the Android client.

Overview
--------
The Android client is a small application that sends observation requests for the lab and listens for incoming results.

The user clicks a connect button to start the "Result Receiver" thread and subscribe to the Patient Care Facility's OUT.AHE.PATIENT.\* topic. Any incoming result message will cause the "results screen" to display immediately showing the results. In a real-world application incoming results would likely be stored in a list somewhere.

A "Place Order" button kicks off an "Order Sender" task that publishes the observation request to the Patient Care Facility's VirtualTopic.IN.AHE.PATIENT.\* topic.


![Overview Image](./android.png "Architectural Overview")
