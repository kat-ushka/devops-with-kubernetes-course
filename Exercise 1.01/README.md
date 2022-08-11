# Exercise 1.01: Getting started

<!-- TOC -->
* [Exercise realization description](#exercise-realization-description)
* [How to perform required flow](#how-to-perform-required-flow)
<!-- TOC -->

## Exercise realization description

Application description can be found in its [README](../log-output/README.md).

The revision of the code for this exercise is tagged with`Exercise_1.01`.

## How to perform required flow

Docker images can be found here:
- docker pull katushka/log-output:1.0

To perform exercise flow I did next steps:

1. Opened shell and moved to the project folder.
2. Checkout tag `Exercise_1.01`:
    ```shell
    git fetch --all --tags
    git checkout tags/Exercise_1.01
    ```
3. Moved to the exercise folder:
    ```shell
    cd Exercise\ 1.01
    ```
4. Created docker image with running docker-compose with command:  
    ```shell
    docker-compose build
   ```
5. Pushed docker images to Docker Hub with command:  
    ```shell
    docker image push katushka/log-output:1.0
   ```  
6. Created kubernetes cluster with command:  
    ```shell
    k3d cluster create -a 2
   ```
7. Set up kubectl config with command:  
    ```shell
    kubectl config use-context k3d-k3s-default
   ```
8. Deployed an application with command:
    ```shell
    kubectl create deployment log-output-dep --image=katushka/log-output:1.0
    ```
9. After the pod had been initialized checked its name with command:
   ```shell
    kubectl get pods
   ```
   Got log-output-dep-7b4b9c5454-gq7sh as created pod name and got it logs with command:
   ```shell
    kubectl logs log-output-dep-7b4b9c5454-gq7sh
   ```