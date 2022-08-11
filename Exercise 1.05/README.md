# Exercise 1.05: Project v0.3

<!-- TOC -->
* [Exercise realization description](#exercise-realization-description)
* [How to perform required flow](#how-to-perform-required-flow)
<!-- TOC -->

## Exercise realization description

Application description can be found in its [README](../to-do-project/README.md).  
For this exercise `com.github.katushka.devopswithkubernetescourse.todoproject.web.RandomResponseController` was added to serve simple get request.  

The revision of the code for this exercise is tagged with `Exercise_1.05`.

## How to perform required flow

Docker image can be found here:
- docker pull katushka/to-do-project:0.3

To perform exercise flow I did next steps:

1. Opened shell and moved to the project folder.
2. Checkout tag `Exercise_1.05`:
    ```shell
    git fetch --all --tags
    git checkout tags/Exercise_1.05
    ```
3. Moved to the exercise folder:
    ```shell
    cd Exercise\ 1.05
    ```
4. Created docker image with running docker-compose with script:  
   `docker-compose build`
5. Pushed docker image to Docker Hub with script:  
   `docker image push katushka/to-do-project:0.3`
6. Applied deployment change with manifest:
    ```shell
    kubectl apply -f manifests                     
   ```
7. Found out the name of the pod (*to-do-project-dep-7ff648ff59-4kvnc*) with script:
   ```shell
    kubectl get pods
   ```
8. Set port forwarding with script:
   ```shell
    kubectl port-forward to-do-project-dep-7ff648ff59-4kvnc 8080:8080 
   ```
9. Opened url http://localhost:8080/ in browser and saw the response.