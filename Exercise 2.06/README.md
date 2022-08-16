# Exercise 2.06: Documentation and ConfigMaps

<!-- TOC -->
* [Exercise realization description](#exercise-realization-description)
* [How to perform required flow](#how-to-perform-required-flow)
<!-- TOC -->

## Exercise realization description

In this exercise use of a new environment variable `MESSAGE` was added to Log Output Application.
No code changes besides that were made.
Configmap logoutput-config-env-file is created form the [env-file.properties](../log-output/configs/env-file.properties).

The revision of the code for this exercise is tagged with `Exercise_2.06`.

In order to perform this exercise I implemented deployment manifest as follows:

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

---
apiVersion: apps/v1
kind: Deployment
metadata:
  namespace: log-output
  name: ping-pong-dep
spec:
  replicas: 1
  selector:
    matchLabels:
      app: ping-pong
  template:
    metadata:
      labels:
        app: ping-pong
    spec:
      containers:
        - image: "katushka/ping-pong:1.4"
          imagePullPolicy: Always
          name: ping-pong
```

## How to perform required flow

To perform exercise flow I did next steps:

1. Opened shell and moved to the project folder.
2. Checkout tag `Exercise_2.06`:
    ```shell
    git fetch --all --tags
    git checkout tags/Exercise_2.06
    ```  
3. Moved to the exercise folder:
    ```shell
    cd Exercise\ 2.06
    ```
4. Created docker images by running docker-compose with script:
    ```shell
    docker-compose build
    ```
5. Pushed a new docker image to Docker Hub with a script:
    ```shell
    docker image push katushka/log-output:1.7
    ```
6. Generated configmap from the source file:
    ```shell
    kubectl create configmap logoutput-config-env-file -n log-output --from-env-file=../log-output/configs/env-file.properties
    ```
7. Checked the generated configmap:
    ```shell
    kubectl get configmap logoutput-config-env-file -n log-output -o yaml
    ```
8. Applied configs with script:
    ```shell
    kubectl apply -f manifests/
    ```  
9. After the pod was initialized opened http://localhost:8081 to see the generated string with 0 ping-pongs and a new message.