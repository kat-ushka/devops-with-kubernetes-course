# Exercise 1.09: More services

# Exercise realization description

Application *log-output* is a JAX-RS webapp which serves simple GET request.  
It writes to console a new timestamp every 5 seconds and returns current timestamp as a response to GET request.

Web application *ping-pong* is a JAX-RS webapp which serves simple GET request.
On each request it increments internal counter and return string "pong {counter}" as response.

# How to perform required flow

Docker images can be found here:
- docker pull katushka/log-output:2.4
- docker pull katushka/ping-pong:1.1

To perform exercise flow I did next steps:

1. Opened shell and moved to this folder.
2. Created docker images with running docker-compose with command:  
    `docker-compose build`
3. Pushed docker images to Docker Hub with commands:  
    `docker image push katushka/log-output:2.4`  
    `docker image push katushka/ping-pong:1.1`  
2. Started kubernetes cluster with command:  
    `k3d cluster create -p 8081:80@loadbalancer --agents 2`
3. Applied configs with commands:  
   `kubectl apply -f ping-pong/manifests/`  
   `kubectl apply -f log-output/manifests/`  
4. After the pod was initialized opened http://localhost:8081 to see the response from log-output and http://localhost:8081/pingpong for response from ping-pong.
