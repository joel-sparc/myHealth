The HL7 Handler Bundle
========
Introduction
--------
This document outlines the basic architecture of the Laboratory Information System's "HL7 Handler" bundle.

Overview
--------
The "HL7 Handler" bundle is responsible for processing all incoming HL7 messages. Two types of HL7 messages are currently handled:
**Patient Admit (ADT-A01)** - Patient Admit messages are routed to the log.
**Observation Request (ORM-01)** - A fictional response is generated for each incoming Observation Request message. The HL7 Handler currently supports one type of Strep Test and Complete Blood Counts (CBC).
