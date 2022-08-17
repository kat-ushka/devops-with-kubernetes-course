# Exercise 2.01: Connecting pods

<!-- TOC -->
* [Exercise description](#exercise-description)
* [Exercise realization description](#exercise-realization-description)
* [How to perform required flow](#how-to-perform-required-flow)
  * [Docker images](#docker-images)
  * [Performing exercise-to-exercise flow](#performing-exercise-to-exercise-flow)
  * [How to do from the scratch](#how-to-do-from-the-scratch)
  * [How to do from the scratch](#how-to-do-from-the-scratch)
<!-- TOC -->

## Exercise description

Connect the "Log output" application and "Ping-pong" application. 
Instead of sharing data via files use HTTP endpoints to respond with the number of pongs. 
Deprecate all the volume between the two applications for the time being.

The output will stay the same:  
>2020-03-30T12:15:17.705Z: 8523ecb1-c716-4cb6-a044-b9e83bb98e43.
Ping / Pongs: 3

## Exercise realization description

To perform this exercise a new endpoint `/counter` was added to Pingpong Application.
Also, a new environment variable `PINGS_URL` was introduced in Log Output Application where a Pingpong Application url 
accessible from the other pod should be put.

No changes were made to Timestamp Generator Application, and it still outputs timestamps to the shared local file.

The revision of the code for this exercise is tagged with `Exercise_2.01`.

In order to perform this exercise I implemented kubernetes manifests as follows:

[deployment.yaml](./manifests/2.deployment.yaml)
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
      volumes:
        - name: shared-file
          emptyDir: { }
      containers:
        - image: "katushka/log-output:1.6"
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
[service.yaml](./manifests/1.service.yaml)
```yaml
---
apiVersion: v1
kind: Service
metadata:
  name: log-output-svc
spec:
  type: ClusterIP
  selector:
    app: log-output # This is the app as declared in the deployment.
  ports: # The following will let TCP traffic from port 2345 to port 8080.
    - port: 2345
      protocol: TCP
      targetPort: 8080
      name: http

---
apiVersion: v1
kind: Service
metadata:
  name: pingpong-svc
spec:
  type: ClusterIP
  selector:
    app: ping-pong # This is the app as declared in the deployment.
  ports: # The following will let TCP traffic from port 2345 to port 8080.
    - port: 2345
      protocol: TCP
      targetPort: 8080
      name: http

```
[ingress.yaml](./manifests/3.ingress.yaml)
```yaml
---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: dwk-ingress
spec:
  rules:
    - http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: log-output-svc
                port:
                  name: http
          - path: /pingpong
            pathType: Prefix
            backend:
              service:
                name: pingpong-svc
                port:
                  name: http

```

## How to perform required flow

### Docker images

Docker images can be found here:
- docker pull katushka/log-output:1.6
- docker pull katushka/timestamp-generator:1.1
- docker pull katushka/ping-pong:1.4

### Performing exercise-to-exercise flow

1. Open shell and move to the project folder.
2. Checkout tag `Exercise_2.01`:
    ```shell
    git fetch --all --tags
    git checkout tags/Exercise_2.01
    ```
3. Move to the folder of the previous log output application exercise (Exercise 1.11) with script:
    ```shell
    cd Exercise\ 1.11
    ```
4. Delete previous artifacts with script:
    ```shell
    kubectl delete -f manifests
    ```
5. Delete shared folder to be sure that it is not used anymore:
    ```shell
    docker exec k3d-k3s-default-agent-0 rm -rf /tmp/kube
    ```
6. Move to the current exercise folder with the script:
    ```shell
    cd ../Exercise\ 2.01
    ```
7. Apply configs with script:
    ```shell
    kubectl apply -f manifests/
    ```  
8. Open http://localhost:8081 to see the generated string with 0 ping-pongs.
   Then visit http://localhost:8081/pingpong to increase the number of ping-pongs and renew http://localhost:8081 to see an update.

### How to do from the scratch

Assuming you have k3d and kubectl already installed.

1. Open shell and checkout tag Exercise_2.01:
    ```shell
    git fetch --all --tags
    git checkout tags/Exercise_2.01
    ```
2. Move to the folder of this exercise:
    ```shell
    cd Exercise\ 2.01
    ```
3. Edit docker images labels in [docker-compose.yaml](./docker-compose.yaml) and create them by running docker-compose with script:
    ```shell
    docker-compose build
    ```
4. Push new docker images to Docker Hub with a script (remember to change labels to the same that were chosen on the previous step):
    ```shell
    docker image push <your docker account>/log-output:1.6
    docker image push <your docker account>/timestamp-generator:1.1
    docker image push <your docker account>/ping-pong:1.4
    ```
5. Create a k3d cluster:
    ```shell
    k3d cluster create -p 8081:80@loadbalancer
    ```
6. Apply configs with script:
    ```shell
    kubectl apply -f manifests/
    ```  
7. Open http://localhost:8081 to see the generated string with 0 ping-pongs.
   Then visit http://localhost:8081/pingpong to increase the number of ping-pongs and renew http://localhost:8081 to see an update.