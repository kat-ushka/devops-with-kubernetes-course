Exercise 1.05: Project v0.3

## Exercise realization description

Application description can be found in its [README](../to-do-project/README.md).
For this exercise `com.github.katushka.devopswithkubernetescourse.todoproject.web.RandomResponseController` was added to serve simple get request.

The revision of the code for this exercise was `8ebefec2`.

## How to perform required flow

Docker image can be found here:
- docker pull katushka/to-do-project:0.3

To perform exercise flow I did next steps:
1. Opened shell and moved to this folder.
2. Created docker image with running docker-compose with script:  
   `docker-compose build`
3. Pushed docker image to Docker Hub with script:  
   `docker image push katushka/to-do-project:0.3`
4. Applied deployment change with manifest:
    ```shell
    kubectl apply -f manifests                     
   ```
5. Found out the name of the pod (*to-do-project-dep-7ff648ff59-4kvnc*) with script:
   ```shell
    kubectl get pods
   ```
6. Set port forwarding with script:
   ```shell
    kubectl port-forward to-do-project-dep-7ff648ff59-4kvnc 8080:8080 
   ```
7. Opened url http://localhost:8080/ in browser and saw the response.