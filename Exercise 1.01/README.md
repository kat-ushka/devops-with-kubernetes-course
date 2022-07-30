# Exercise 1.01: Getting started

## Exercise realization description

Application description can be found in its [README](../log-output/README.md).

The revision of the code for this exercise was `e8ef00d6`.

## How to perform required flow

Docker images can be found here:
- docker pull katushka/log-output:1.0

To perform exercise flow I did next steps:

1. Opened shell and moved to this folder.
2. Created docker image with running docker-compose with command:  
    ```shell
    docker-compose build
   ```
3. Pushed docker images to Docker Hub with command:  
    ```shell
    docker image push katushka/log-output:1.0
   ```  
4. Created kubernetes cluster with command:  
    ```shell
    k3d cluster create -a 2
   ```
5. Set up kubectl config with command:  
    ```shell
    kubectl config use-context k3d-k3s-default
   ```
6. Deployed an application with command:
    ```shell
    kubectl create deployment log-output-dep --image=katushka/log-output:1.0
    ```
7. After the pod had been initialized checked its name with command:
   ```shell
    kubectl get pods
   ```
   Got log-output-dep-7b4b9c5454-gq7sh as created pod name and got it logs with command:
   ```shell
    kubectl logs log-output-dep-7b4b9c5454-gq7sh
   ```