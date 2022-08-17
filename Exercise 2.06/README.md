# Exercise 2.06: Documentation and ConfigMaps

<!-- TOC -->
* [Exercise description](#exercise-description)
* [Exercise realization description](#exercise-realization-description)
* [How to perform required flow](#how-to-perform-required-flow)
  * [Docker images](#docker-images)
  * [Performing exercise-to-exercise flow](#performing-exercise-to-exercise-flow)
  * [How to do from the scratch](#how-to-do-from-the-scratch)
<!-- TOC -->

## Exercise description

Use the official Kubernetes documentation for this exercise. 
[This page](https://kubernetes.io/docs/concepts/configuration/configmap/) and [this one](https://kubernetes.io/docs/tasks/configure-pod-container/configure-pod-configmap/) should contain everything you need.

Create a ConfigMap for a "dotenv file". A file where you define environment variables that are loaded by the application. 
For this use an environment variable "MESSAGE" with value "Hello" to test and print the value. 
The values from ConfigMap can be either saved to a file and read by the application, or set as environment variables and used by the application through that. 
Implementation is up to you but the output should look like this:

>Hello
2020-03-30T12:15:17.705Z: 8523ecb1-c716-4cb6-a044-b9e83bb98e43.
Ping / Pongs: 3

## Exercise realization description

In this exercise use of a new environment variable `MESSAGE` was added to Log Output Application.
No code changes besides that were made.

Configmap logoutput-config-env-file is created from the new `env-file.properties`.

The revision of the code for this exercise is tagged with `Exercise_2.06`.

In order to perform this exercise I changed deployment manifest as follows:

[deployment.yaml](./manifests/2.deployment.yaml)
```yaml
---
apiVersion: apps/v1
kind: Deployment
metadata:
  namespace: log-output
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
      volumes:
        # You set volumes at the Pod level, then mount them into containers inside that Pod
        - name: shared-file
          emptyDir: { }
      containers:
        - image: "katushka/log-output:1.7"
          imagePullPolicy: Always
          name: log-output
          volumeMounts:
            - name: shared-file
              mountPath: /usr/src/app/local_files
          env:
            - name: TIME_STAMP_FILEPATH
              value: "/usr/src/app/local_files/timestamp"
            - name: PINGS_URL
              value: "http://pingpong-svc:2345/pingpong/counter"
          envFrom:
            - configMapRef:
                name: logoutput-config-env-file
        - image: "katushka/timestamp-generator:1.1"
          imagePullPolicy: Always
          name: timestamp-generator
          volumeMounts: # Mount volume
            - name: shared-file
              mountPath: /usr/src/app/local_files
          env:
            - name: TIME_STAMP_FILEPATH
              value: "/usr/src/app/local_files/timestamp"
...
```

## How to perform required flow

### Docker images

Docker images can be found here:
- docker pull katushka/log-output:1.7
- docker pull katushka/ping-pong:1.4
- docker pull katushka/timestamp-generator:1.1

### Performing exercise-to-exercise flow

1. Open shell and move to the project folder.
2. Checkout tag `Exercise_2.06`:
    ```shell
    git fetch --all --tags
    git checkout tags/Exercise_2.06
    ```  
3. Moved to the exercise folder:
    ```shell
    cd Exercise\ 2.06
    ```
4. Generate configmap from the source file:
    ```shell
    kubectl create configmap logoutput-config-env-file -n log-output --from-env-file=../log-output/configs/env-file.properties
    ```
5. Checked the generated configmap:
    ```shell
    kubectl get configmap logoutput-config-env-file -n log-output -o yaml
    ```
6. Applied configs with script:
    ```shell
    kubectl apply -f manifests/
    ```  
7. Open http://localhost:8081 to see the generated string with 0 ping-pongs and a new message.

### How to do from the scratch

1. Open shell and move to the project folder.
2. Checkout tag `Exercise_2.06`:
    ```shell
    git fetch --all --tags
    git checkout tags/Exercise_2.06
    ```  
3. Move to the exercise folder:
    ```shell
    cd Exercise\ 2.06
    ```
4. Edit docker images labels in [docker-compose.yaml](./docker-compose.yaml) and create them by running docker-compose with script:
    ```shell
    docker-compose build
    ```
5. Push docker images to Docker Hub with scripts (remember to change labels to the same that were chosen on the previous step):
    ```shell
    docker image push <your docker account>/log-output:1.7
    docker image push <your docker account>/ping-pong:1.4
    docker image push <your docker account>/timestamp-generator:1.1
    ```
6. Create a k3d cluster:
    ```shell
    k3d cluster create -p 8081:80@loadbalancer
    ```
7. Generate configmap from the source file:
    ```shell
    kubectl create configmap logoutput-config-env-file -n log-output --from-env-file=../log-output/configs/env-file.properties
    ```
8. Check the generated configmap:
    ```shell
    kubectl get configmap logoutput-config-env-file -n log-output -o yaml
    ```
9. Applied configs with script:
    ```shell
    kubectl apply -f manifests/
    ```
10. Open http://localhost:8081 to see the generated string with 0 ping-pongs and a new message.