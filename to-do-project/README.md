# Log Output application

## Brief description

Application *to-do-project* is a JAX-RS webapp that outputs "Server started in port NNNN" when it is started.   
It is deployed in Tomcat (10.0.23) webserver.  
Tomcat http port can be defined with setting up CATALINA_HTTP_PORT env property.  
In order to do that Tomcat layer is modified with adding a setenv.sh script that calculates and adds CATALINA_HTTP_PORT to JAVA_OPTS on startup.
Creating setenv.sh script is a method that is recommended for setting JAVA_OPTS in Tomcat.
Also, server.xml content is changed with replacing `port=8080` to `port=${port.http.nonssl}`.
## How to create docker image

An image can be created with a docker command:  
```shell
docker image build . 
```
