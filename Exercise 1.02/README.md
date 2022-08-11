# Exercise 1.02: Project v0.1

<!-- TOC -->
* [Exercise realization description](#exercise-realization-description)
* [How to perform required flow](#how-to-perform-required-flow)
<!-- TOC -->

## Exercise realization description

Application description can be found in its [README](../to-do-project/README.md).  

The revision of the code for this exercise is tagged with `Exercise_1.02`.  

## How to perform required flow

Docker image can be found here:
- docker pull katushka/to-do-project:0.1

To perform exercise flow I did next steps:

1. Opened shell and moved to the project folder.
2. Checkout tag `Exercise_1.02`:
    ```shell
    git fetch --all --tags
    git checkout tags/Exercise_1.02
    ```
3. Moved to the exercise folder:
    ```shell
    cd Exercise\ 1.02
    ```
4. Created docker image with running docker-compose with command:  
    ```shell
    docker-compose build
   ```
5. Pushed docker images to Docker Hub with command:  
    ```shell
    docker image push katushka/to-do-project:0.1
   ```  
6. Deployed an application to already existing k3d cluster with command:
    ```shell
    kubectl create deployment to-do-project-dep --image=katushka/to-do-project:0.1
    ```
7. After the pod had been initialized checked its name with command:
   ```shell
    kubectl get pods
   ```
   Got to-do-project-dep-58ddfb7669-q85rr as created pod name and got it logs with command:
   ```shell
    kubectl logs to-do-project-dep-58ddfb7669-q85rr 
   ```