# Log Output application

## Brief description

Application *log-output* is a JAX-RS webapp.  
It writes to console a new timestamp and a string generated on startup every 5 seconds.
Application is hosted by Tomcat (10.0.23) webserver.

## How to create docker image

An image can be created with a docker command:  
```shell
docker image build . 
```
