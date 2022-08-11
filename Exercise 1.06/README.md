# Exercise 1.06: Project v0.4

<!-- TOC -->
* [Exercise realization description](#exercise-realization-description)
* [How to perform required flow](#how-to-perform-required-flow)
<!-- TOC -->

## Exercise realization description

Application description can be found in its [README](../to-do-project/README.md).  
The revision of the code for this exercise is tagged with `Exercise_1.06`.

In order to perform this exercise I implemented new service manifest file as follows:  

[service.yaml](./manifests/service.yaml)
```yaml
apiVersion: v1
kind: Service
metadata:
   name: to-do-project-svc
spec:
   type: NodePort
   selector:
      app: to-do-project # This is the app as declared in the deployment.
   ports: # The following will let TCP traffic from port 2345 to port 8080.
      - name: http
        nodePort: 30080 # This is the port that is available outside. Value for nodePort can be between 30000-32767
        protocol: TCP
        port: 1234 # This is a port that is available to the cluster, in this case it can be ~ anything
        targetPort: 8080 # This is the target port
```

## How to perform required flow

Docker image can be found here:
- docker pull katushka/to-do-project:0.3  
There were no changes to the code or deployment, so the image is the same as for the Exercise 1.05. 

To perform exercise flow I did next steps:

1. Opened shell and moved to the project folder.
2. Checkout tag `Exercise_1.06`:
    ```shell
    git fetch --all --tags
    git checkout tags/Exercise_1.06
    ```
3. Moved to the exercise folder:
    ```shell
    cd Exercise\ 1.06
    ```
4. Deleted existed cluster with script:
    ```shell
    k3d cluster delete
    ```
5. Created new cluster with opened ports:
    ```shell
    k3d cluster create --port 8082:30080@agent:0 -p 8081:80@loadbalancer --agents 2
    ```
6. Applied a new deployment with manifest:
    ```shell
    kubectl apply -f manifests/deployment.yaml                     
   ```
7. Applied a new service with manifest:
   ```shell
    kubectl apply -f manifests/service.yaml
   ```
8. Opened url http://localhost:8082/ in browser and saw the response.