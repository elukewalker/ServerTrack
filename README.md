# Introduction

ServerTrack challenge.  Note the Dropwizard example application was used as a starting point for this project.

# Overview

Server Load
This specific challenge is called ServerTrack and allows you to demonstrate how you might implement a basic server monitoring system.
 
In this project, two API endpoints are necessary. They are:

1. Record load for a given server
This should take a:

server name (string)
CPU load (double)
RAM load (double)
And apply the values to an in-memory model used to provide the data in endpoint #2.

2. Display loads for a given server

This should return data (if it has any) for the given server:

A list of the average load values for the last 60 minutes broken down by minute
A list of the average load values for the last 24 hours broken down by hour
Both endpoints would be under a continuous load being called multiple times a second. There is no need to persist the results to storage.


# Running The Application

To test the example application run the following commands.

* To package the example run.

        mvn package

* To setup the h2 database run.

        java -jar target/ServerTrack-0.8.0-rc2-SNAPSHOT.jar db migrate example.yml

* To run the server run.

        java -jar target/ServerTrack-0.8.0-rc2-SNAPSHOT.jar server example.yml


* To post data into the application.

	curl -H "Content-Type: application/json" -X POST -d '{"serverName":"name","cpuLoad":"33.3","memLoad":"22.2"}' http://localhost:8080/records
	
	or type the following into the browser
	http://localhost:8080/records?serverName=name&cpuLoad=33.3&memLoad=22.2
	
* To view server data.
	
	open http://localhost:8080/server/name/byMinute
	
	open http://localhost:8080/server/name/byHour
