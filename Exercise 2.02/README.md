# Exercise 2.02: Project v1.0

# Exercise realization description

ToDo Application description can be found in its [README](../to-do-project/README.md).  
In this exercise I created 3 submodules: to-do-web, to-do-api, and to-do-common.

Code revision for this exercise was `9d5c4197`.

In order to perform this exercise I implemented deployment manifest as follows:

[persistentvolume.yaml](./manifests/0.persistentvolume.yaml)
```shell
---
apiVersion: v1
kind: PersistentVolume
metadata:
  name: image-pv
spec:
  storageClassName: manual
  capacity:
    storage: 1Gi # Could be e.q. 500Gi. Small amount is to preserve space when testing locally
  volumeMode: Filesystem # This declares that it will be mounted into pods as a directory
  accessModes:
    - ReadWriteOnce
  local:
    path: /tmp/kube/images
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
```shell
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: image-claim
spec:
  storageClassName: manual
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 1Gi
```
[service.yaml](./manifests/2.service.yaml)
```shell
---
apiVersion: v1
kind: Service
metadata:
  name: to-do-api-svc
spec:
  type: ClusterIP
  selector:
    app: to-do-api # This is the app as declared in the deployment.
  ports: # The following will let TCP traffic from port 2345 to port 8080.
    - port: 2345
      protocol: TCP
      targetPort: 8080
      name: http

---
apiVersion: v1
kind: Service
metadata:
  name: to-do-web-svc
spec:
  type: ClusterIP
  selector:
    app: to-do-web # This is the app as declared in the deployment.
  ports: # The following will let TCP traffic from port 2345 to port 8080.
    - port: 2345
      protocol: TCP
      targetPort: 8080
      name: http

```
[deployment.yaml](./manifests/3.deployment.yaml)
```shell
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: to-do-api-dep
spec:
  replicas: 1
  selector:
    matchLabels:
      app: to-do-api
  template:
    metadata:
      labels:
        app: to-do-api
    spec:
      containers:
        - image: "katushka/to-do-api:1.0"
          name: to-do-api

---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: to-do-web-dep
spec:
  replicas: 1
  selector:
    matchLabels:
      app: to-do-web
  template:
    metadata:
      labels:
        app: to-do-web
    spec:
      volumes:
        - name: shared-image
          persistentVolumeClaim:
            claimName: image-claim
      containers:
        -
          image: "katushka/to-do-web:1.0"
          name: to-do-project
          volumeMounts:
            - name: shared-image
              mountPath: /usr/src/app/local_files/images
          env:
            - name: UPLOAD_LOCATION
              value: "/usr/src/app/local_files/images/to-do-today.jpg"
            - name: TODO_API_URI
              value: "http://to-do-api-svc:2345/to-do-api/api/todos"

```
[ingress.yaml](./manifests/4.ingress.yaml)
```shell
---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: to-do-web-ingress
spec:
  rules:
    - http:
        paths:
          - path: /to-do
            pathType: Prefix
            backend:
              service:
                name: to-do-web-svc
                port:
                  name: http

```
# How to perform required flow

Docker images can be found here:
- docker pull katushka/to-do-api:1.0
- docker pull katushka/to-do-web:1.0

To perform exercise flow I did next steps:

1. Opened shell and moved to the whole project root folder.  
2. Moved to the folder of the previous ToDo application exercise (Exercise 1.12) with script:
    ```shell
    cd Exercise\ 1.12
    ```
3. Deleted previous artifacts with script:
    ```shell
    kubectl delete -f manifests
    ```
4. Moved to the current exercise folder with the script:
    ```shell
    cd ..
    cd cd Exercise\ 2.02
    ```
5. Created a folder for persistent volume (it was deleted in previous exercise):
   ```shell
    docker exec k3d-k3s-default-agent-0 mkdir -p /tmp/kube/images
   ```
6. Created docker images by running docker-compose with script:
    ```shell
    docker-compose build
    ```
7. Pushed docker images to Docker Hub with scripts:
    ```shell
    docker image push katushka/to-do-api:1.0
    docker image push katushka/to-do-web:1.0
    ```
8. Applied configs with script:
    ```shell
    kubectl apply -f manifests/
    ```  
9. After the pod was initialized opened http://localhost:8081/to-do and added some todos.
