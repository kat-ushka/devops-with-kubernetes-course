# Exercise 1.04: Project v0.2

<!-- TOC -->
* [Exercise realization description](#exercise-realization-description)
* [How to perform required flow](#how-to-perform-required-flow)
<!-- TOC -->

## Exercise realization description

Application description can be found in its [README](../to-do-project/README.md).  

The revision of the code for this exercise is tagged with `Exercise_1.04`.

In order to perform this exercise I implemented manifest file as follows:  

[deployment.yaml](./manifests/deployment.yaml)
```yaml
---
apiVersion: apps/v1
kind: Deployment
metadata:
   name: to-do-project-dep
spec:
   replicas: 1
   selector:
      matchLabels:
         app: to-do-project
   template:
      metadata:
         labels:
            app: to-do-project
      spec:
         containers:
            -
               image: "katushka/to-do-project:0.1"
               name: to-do-project
```

## How to perform required flow

Docker image can be found here:
- docker pull katushka/to-do-project:0.1  
it is the same as for the Exercise 1.02.

To perform exercise flow I did next steps:

1. Opened shell and moved to the project folder.
2. Checkout tag `Exercise_1.04`:
    ```shell
    git fetch --all --tags
    git checkout tags/Exercise_1.04
    ```
3. Moved to the exercise folder:
    ```shell
    cd Exercise\ 1.04
    ```
4. Deleted already created deployment with command:
    ```shell
    kubectl delete deployment to-do-project-dep
    ```
5. Applied new deployment with manifest:
    ```shell
    kubectl apply -f manifests                     
   ```
6. After the pod had been initialized checked its name with command:
   ```shell
    kubectl get pods
   ```
   Got to-do-project-dep-77d56c7648-cz4nx as created pod name and got it logs with command:
   ```shell
    kubectl logs to-do-project-dep-77d56c7648-cz4nx 
   ```