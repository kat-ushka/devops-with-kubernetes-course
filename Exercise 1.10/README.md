# Exercise 1.10: Even more services

# Exercise realization description

Log Output Application description can be found in its [README](../log-output/README.md).
Timestamp Generator Application description can be found in its [README](../timestamp-generator/README.md).

Code revision for this exercise was `dbff412e`.

In order to perform this exercise I implemented deployment manifest as follows:

[deployment.yaml](./manifests/deployment.yaml)
```shell
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


# How to perform required flow

Docker images can be found here:
- docker pull katushka/log-output:1.3
- docker pull katushka/timestamp-generator:1.0

To perform exercise flow I did next steps:

1. Opened shell and moved to this folder.
2. Created docker images by running docker-compose with script:
    ```shell
    docker-compose build
    ```
3. Pushed docker images to Docker Hub with scripts:
    ```shell
    docker image push katushka/log-output:1.3
    docker image push katushka/ping-pong:1.0
    ```
4. Applied configs with script:
    ```shell
    kubectl apply -f manifests/
    ```  
5. After the pod was initialized opened http://localhost:8081 to see the generated string.
