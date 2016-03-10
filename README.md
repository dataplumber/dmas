# Data Management and Archive System (DMAS)
The Data Management and Archive System  addresses several NASA Distributed Active Archive Center needs:
* Automate workflow for data discovery, ingestion, validation, metadata harvesting, and archival.
* Unified metadata model in compliance with community standards (EOS Clearing House, Global Climate Master Directory, ISO-19115, and the Federal Geographic Data Committee, etc.) for all data artifacts to be archived and distributed.
* Provide policy-driven architecture to address project/product-specific quality-of-service for data capturing and distribution, and storage requirements.
* Provide scalable solution to capture heterogeneous Earth science data artifacts.
* Provide system management and monitoring solution to address PO.DAAC operation requirements.
* Provide tools for data product definition and curation.

As a distributed data system, DMAS is equipped with support services. These services complement DMAS without being specific for DMAS, which can be infused independently into an enterprise system.
* Significant Event Service. In a highly distributed, horizontally scaled operation environment, event reporting and error tracking present a major challenge for operation. Existing solutions are either bound to specific platform, programming language, and specific communication protocols. The Significant Event Service offers centralized event reporting and notification capabilities, that is simple and portable. Unlike some of the commonly known messaging solutions that are language, platform, and/or communication protocol-specific, the Significant Event Service offers a simple RESTful solution for publishing events. It supports various methods for event notification including email, Java Message Service, and Twitter mini-blogs. Within a system like DMAS, where hundreds of transactions and tasks are being handled a the same time, having a centralized event reporting service enables an operator to quickly identify the origin of any given event.
* Security Service.  A centrailized authentication and authentication service interface.  It can be integrated with local LDAP and database.

