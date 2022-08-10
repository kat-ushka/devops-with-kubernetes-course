# Exercise 1.11: Persisting data

# Exercise realization description

Log Output Application description can be found in its [README](../log-output/README.md).  
Timestamp Generator Application description can be found in its [README](../timestamp-generator/README.md).
Pingpong Application description can be found in its [README](../ping-pong/README.md).

Code revision for this exercise was `0e1dca1a`.

In order to perform this exercise I implemented deployment manifest as follows:

[persistentvolume.yaml](./manifests/0.persistentvolume.yaml)
```yaml
---
apiVersion: v1
kind: PersistentVolume
metadata:
  name: test-pv
spec:
  storageClassName: manual
  capacity:
    storage: 1Gi # Could be e.q. 500Gi. Small amount is to preserve space when testing locally
  volumeMode: Filesystem # This declares that it will be mounted into pods as a directory
  accessModes:
    - ReadWriteOnce
  local:
    path: /tmp/kube
  nodeAffinity: ## This is only required for local, it defines which nodes can access it
    required:
      nodeSelectorTerms:
        - matchExpressions:
            - key: kubernetes.io/hostname
              operator: In
              values:
                - k3d-k3s-default-agent-0
```
[persistentvolumeclaim.yaml](./manifests/1.persistentvolumeclaim.yaml)
```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: pings-claim
spec:
  storageClassName: manual
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 1Gi
```
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
        - name: shared-pings
          persistentVolumeClaim:
            claimName: pings-claim
      containers:
        - image: "katushka/log-output:1.5"
          name: log-output
          volumeMounts:
            - name: shared-file
              mountPath: /usr/src/app/local_files
            - name: shared-pings
              mountPath: /usr/src/app/files
          env:
            - name: TIME_STAMP_FILEPATH
              value: "/usr/src/app/local_files/timestamp"
        - image: "katushka/timestamp-generator:1.1"
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
      volumes:
        - name: shared-pings
          persistentVolumeClaim:
            claimName: pings-claim
      containers:
        -
          image: "katushka/ping-pong:1.3"
          name: ping-pong
          volumeMounts:
            - name: shared-pings
              mountPath: /usr/src/app/files

```
[service.yaml](./manifests/3.service.yaml)
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
[ingress.yaml](./manifests/4.ingress.yaml)
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
- docker pull katushka/log-output:1.5
- docker pull katushka/timestamp-generator:1.1
- docker pull katushka/ping-pong:1.3

To perform exercise flow I did next steps:

1. Opened shell and moved to this folder.
2. Created docker images by running docker-compose with script:
    ```shell
    docker-compose build
    ```
3. Pushed new docker images to Docker Hub with scripts (timestamp-generator:1.1 was already there):
    ```shell
    docker image push katushka/log-output:1.5
    docker image push katushka/ping-pong:1.3
    ```
4. Created a directory for volume with script:
    ```shell
    docker exec k3d-k3s-default-agent-0 mkdir -p /tmp/kube
    ```
5. Applied configs with script:
    ```shell
    kubectl apply -f manifests/
    ```  
6. After the pod was initialized opened http://localhost:8081 to see the generated string with 0 ping-pongs.
   Then visited http://localhost:8081/pingpong to increase the number of ping-pongs and renewed http://localhost:8081 to see an update.
