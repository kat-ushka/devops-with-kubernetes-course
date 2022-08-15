# Log Output application

## Brief description

Web application *ping-pong* is a JAX-RS webapp which serves simple GET request.
On each request it increments internal counter and return string "pong {counter}" as response. 
Application is hosted by Tomcat (10.0.23) webserver.

## How to create docker image

An image can be created with a docker command:  
```shell
docker image build . 
```
