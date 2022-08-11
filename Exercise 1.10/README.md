# Exercise 1.10: Even more services

<!-- TOC -->
* [Exercise realization description](#exercise-realization-description)
* [How to perform required flow](#how-to-perform-required-flow)
<!-- TOC -->

## Exercise realization description

Log Output Application description can be found in its [README](../log-output/README.md).  
Timestamp Generator Application description can be found in its [README](../timestamp-generator/README.md).

The revision of the code for this exercise is tagged with `Exercise_1.10`.

In order to perform this exercise I implemented deployment manifest as follows:

[deployment.yaml](./manifests/deployment.yaml)
```yaml
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: log-output-dep
spec:
  replicas: 1
  selector:
    matchLabels:
      app: log-output
  template:
    metadata:
      labels:
        app: log-output
    spec:
      volumes: # Define volume
        - name: shared-file
          emptyDir: { }
      containers:
        - image: "katushka/log-output:1.3"
          name: log-output
          volumeMounts: # Mount volume
            - name: shared-file
              mountPath: /usr/src/app/files
          env:
            - name: TIME_STAMP_FILEPATH
              value: "/usr/src/app/files/timestamp"
        - image: "katushka/timestamp-generator:1.0"
          name: timestamp-generator
          volumeMounts: # Mount volume
            - name: shared-file
              mountPath: /usr/src/app/files
          env:
            - name: TIME_STAMP_FILEPATH
              value: "/usr/src/app/files/timestamp"
```

## How to perform required flow

Docker images can be found here:
- docker pull katushka/log-output:1.3
- docker pull katushka/timestamp-generator:1.0

To perform exercise flow I did next steps:

1. Opened shell and moved to the project folder.
2. Checkout tag `Exercise_1.10`:
    ```shell
    git fetch --all --tags
    git checkout tags/Exercise_1.10
    ```
3. Moved to the exercise folder:
    ```shell
    cd Exercise\ 1.10
    ```
4. Created docker images by running docker-compose with script:
    ```shell
    docker-compose build
    ```
5. Pushed docker images to Docker Hub with scripts:
    ```shell
    docker image push katushka/log-output:1.3
    docker image push katushka/ping-pong:1.0
    ```
6. Applied configs with script:
    ```shell
    kubectl apply -f manifests/
    ```  
7. After the pod was initialized opened http://localhost:8081 to see the generated string.
