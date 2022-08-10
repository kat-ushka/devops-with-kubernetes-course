# Exercise 2.01: Connecting pods

# Exercise realization description

Log Output Application description can be found in its [README](../log-output/README.md).  
Timestamp Generator Application description can be found in its [README](../timestamp-generator/README.md).
Pingpong Application description can be found in its [README](../ping-pong/README.md).

To perform this exercise a new endpoint `/counter` was added to Pingpong Application.
Also, a new environment variable `PINGS_URL` was introduced in Log Output Application were a Pingpong Application url 
accessible from the other pod should be put.
No changes were made to Timestamp Generator Application, and it still outputs timestamps to the shared local file.

Code revision for this exercise was `53808ec0`.

In order to perform this exercise I implemented deployment manifest as follows:

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
# How to perform required flow

Docker images can be found here:
- docker pull katushka/log-output:1.6
- docker pull katushka/timestamp-generator:1.1
- docker pull katushka/ping-pong:1.4

To perform exercise flow I did next steps:

1. Opened shell and moved to the root project folder.  
2. Moved to the folder of the previous log output application exercise (Exercise 1.11) with script:
    ```shell
    cd Exercise\ 1.11
    ```
3. Deleted previous artifacts with script:
    ```shell
    kubectl delete -f manifests
    ```
4. Deleted shared folder to be sure that it was not used anymore:
    ```shell
    docker exec k3d-k3s-default-agent-0 rm -rf /tmp/kube
    ```
5. Moved to the current exercise folder with the script:
    ```shell
    cd ..
    cd Exercise\ 2.01
    ```
6. Created docker images by running docker-compose with script:
    ```shell
    docker-compose build
    ```
7. Pushed new docker images to Docker Hub with scripts (timestamp-generator:1.1 was already there):
    ```shell
    docker image push katushka/log-output:1.6
    docker image push katushka/ping-pong:1.4
    ```
8. Applied configs with script:
    ```shell
    kubectl apply -f manifests/
    ```  
9. After the pod was initialized opened http://localhost:8081 to see the generated string with 0 ping-pongs.
   Then visited http://localhost:8081/pingpong to increase the number of ping-pongs and renewed http://localhost:8081 to see an update.
