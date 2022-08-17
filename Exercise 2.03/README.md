# Exercise 2.03: Keep them separated

<!-- TOC -->
* [Exercise description](#exercise-description)
* [Exercise realization description](#exercise-realization-description)
* [How to perform required flow](#how-to-perform-required-flow)
  * [Docker images](#docker-images)
  * [Performing exercise-to-exercise flow](#performing-exercise-to-exercise-flow)
  * [How to do from the scratch](#how-to-do-from-the-scratch)
<!-- TOC -->

## Exercise description

Create a namespace for the applications in the exercises. Move the "Log output" and "Ping-pong" to that namespace 
and use that in the future for all of the exercises. 
You can follow the material in the default namespace.

## Exercise realization description

No application code changes were performed in this exercise.
A new namespace `log-output` was created with [0.namespace.yaml](./manifests/0.namespace.yaml).
All the previous manifests for Log Output Application and Pingpong Application were updated with a new namespace.

The revision of the code for this exercise is tagged with `Exercise_2.03`.

In order to perform this exercise I implemented deployment manifest as follows:

[namespace.yaml](./manifests/0.namespace.yaml)
```yaml
---
apiVersion: v1
kind: Namespace
metadata:
  name: log-output

```
[service.yaml](./manifests/1.service.yaml)
```yaml
---
apiVersion: v1
kind: Service
metadata:
  namespace: log-output
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
  namespace: log-output
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
[ingress.yaml](./manifests/3.ingress.yaml)
```yaml
---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  namespace: log-output
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
2. Move to the folder of the previous Log Output application exercise (Exercise 2.01) with script:
    ```shell
    cd Exercise\ 2.01
    ```
3. Delete previous artifacts with script:
    ```shell
    kubectl delete -f manifests
    ```
4. Move to the current exercise folder with the script:
    ```shell
    cd ../Exercise\ 2.03
    ```
5. Apply configs with script:
    ```shell
    kubectl apply -f manifests/
    ``` 
6. Check that new pods were created in a new namespace with the script:
    ```shell
    kubectl get pods -n log-output
    ``` 
7. Open http://localhost:8081 to see the generated string with 0 ping-pongs.
   Then visit http://localhost:8081/pingpong to increase the number of ping-pongs and renewed http://localhost:8081 to see an update.

### How to do from the scratch

1. Open shell and move to the project folder.
2. Checkout tag `Exercise_2.03`:
    ```shell
    git fetch --all --tags
    git checkout tags/Exercise_2.03
    ```
3. Follow steps 3 to 7 of the *How to do from the scratch* in [README.md](../Exercise 2.01/README.md)
4. Move to the current exercise folder with the script:
    ```shell
    cd ../Exercise\ 2.03
    ```
5. Apply configs with script:
    ```shell
    kubectl apply -f manifests/
    ``` 
6. Check that new pods were created in a correct namespace with the script:
    ```shell
    kubectl get pods -n log-output
    ``` 
7. Open http://localhost:8081 to see the generated string with 0 ping-pongs.
   Then visit http://localhost:8081/pingpong to increase the number of ping-pongs and renewed http://localhost:8081 to see an update.