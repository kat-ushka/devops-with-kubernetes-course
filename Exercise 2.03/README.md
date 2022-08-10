# Exercise 2.03: Keep them separated

# Exercise realization description

No application code changes were performed in this exercise.
A new namespace `log-output` was created with [0.namespace.yaml](./manifests/0.namespace.yaml).
All the previous manifests for Log Output Application and Pingpong Application were updated with a new namespace.

Code revision for this exercise was `01405b67`.

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
# How to perform required flow

Docker images can be found here:
- docker pull katushka/log-output:1.6
- docker pull katushka/timestamp-generator:1.1
- docker pull katushka/ping-pong:1.4

To perform exercise flow I did next steps:

1. Opened shell and moved to the whole project root folder.  
2. Moved to the folder of the previous Log Output application exercise (Exercise 2.01) with script:
    ```shell
    cd Exercise\ 2.01
    ```
3. Deleted previous artifacts with script:
    ```shell
    kubectl delete -f manifests
    ```
4. Moved to the current exercise folder with the script:
    ```shell
    cd ..
    cd Exercise\ 2.03
    ```
5. Applied configs with script:
    ```shell
    kubectl apply -f manifests/
    ```  
6. After the pod was initialized opened http://localhost:8081 to see the generated string with 0 ping-pongs.
   Then visited http://localhost:8081/pingpong to increase the number of ping-pongs and renewed http://localhost:8081 to see an update.

