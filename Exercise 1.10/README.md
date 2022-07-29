# Exercise 1.10: Even more services

# Exercise realization description

Application *timestamp-generator* is a simple Java executable jar.  
It generates a new timestamp every 5 seconds and writes it to file /usr/src/app/files/timestamp.
The file path is passed through env value *filepath* described in [deployment.yaml](Exercise 1.10/manifests/deployment.yaml).

Web application *log-output-web* is a JAX-RS webapp which serves simple GET request.
On each request it reads */usr/src/app/files/timestamp*, adds random string generated on application start, and returns it as response.

# How to perform required flow

Docker images can be found here:
- docker pull katushka/log-output-web:1.0
- docker pull katushka/timestamp-generator:1.0

To perform exercise flow I did next steps:

1. Opened shell and moved to this folder.
2. Started kubernetes cluster with command:  
    `k3d cluster create -p 8081:80@loadbalancer --agents 2`
3. Applied configs with command:  
   `kubectl apply -f manifests/`  
4. After the pod was initialized opened http://localhost:8081 to see the generated string.
