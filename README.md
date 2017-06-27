DNSMadeEasyClient
================

The goal of this project is to create a Java client for the DNS Made Easy REST API.

Documentation for the API is at http://dnsmadeeasy.com/pdf/API-Docv2.pdf

In order to run the integration tests, please use the following command, from the root director of the project where 
the pom.xml file is located. It is assumed that maven is configured to run from command line.
The tests require a config properties file which is to be named 'dnsmadeeasy-client.properties'. Please
refers TestConfig.CONFIG_LOCATIONS for more information on the locations for the properties file.
A sample file is checked in with the source code, 'dnsmadeeasyclient.properties'.

mvn integration-test