# Exercise 1.08: Project v0.5

# Exercise realization description

Application *to-do-project* is a JAX-RS webapp which serves simple GET request.  

# How to perform required flow

Docker images can be found here:
- docker pull katushka/to-do-project:0.5

To perform exercise flow I did next steps:

1. Opened shell and moved to this folder.
2. Created docker image with running docker-compose with command:  
    `docker-compose build`
3. Pushed docker image to Docker Hub with commands:  
    `docker image push katushka/to-do-project:0.5`
4. Started kubernetes cluster with command:  
    `k3d cluster create -p 8081:80@loadbalancer --agents 2`
5. Applied configs with commands:  
   `kubectl apply -f manifests/`  
6. After the pod was initialized opened http://localhost:8081 to see the response from to-do-project.
