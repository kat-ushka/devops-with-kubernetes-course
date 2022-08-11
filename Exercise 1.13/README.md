# Exercise 1.12: Project v0.6

<!-- TOC -->
* [Exercise realization description](#exercise-realization-description)
* [How to perform required flow](#how-to-perform-required-flow)
<!-- TOC -->

## Exercise realization description

ToDo Application description can be found in its [README](../to-do-project/README.md).  
In this exercise I added some UI elements to prepare for the next exercises.

The revision of the code for this exercise is tagged with `Exercise_1.13`.

The only change in manifests was new version of docker image in deployment.yaml:

[deployment.yaml](./manifests/2.deployment.yaml)
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
      volumes:
        - name: shared-image
          persistentVolumeClaim:
            claimName: image-claim
      containers:
        -
          image: "katushka/to-do-project:0.7"
          name: to-do-project
          volumeMounts:
            - name: shared-image
              mountPath: /usr/src/app/local_files/images
          env:
            - name: UPLOAD_LOCATION
              value: "/usr/src/app/local_files/images/to-do-today.jpg"

```

## How to perform required flow

Docker images can be found here:
- docker pull katushka/to-do-project:0.7

To perform exercise flow I did next steps:

1. Opened shell and moved to the project folder.
2. Checkout tag `Exercise_1.13`:
    ```shell
    git fetch --all --tags
    git checkout tags/Exercise_1.13
    ```
3. Moved to the exercise folder:
    ```shell
    cd Exercise\ 1.13
    ```
4. Created docker images by running docker-compose with script:
    ```shell
    docker-compose build
    ```
5. Pushed docker images to Docker Hub with scripts:
    ```shell
    docker image push katushka/to-do-project:0.7
    ```
6. Applied configs with script:
    ```shell
    kubectl apply -f manifests/
    ```  
7. After the pod was initialized opened http://localhost:8081/to-do to see the UI changes.
