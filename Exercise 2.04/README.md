# Exercise 2.04: Project v1.1

# Exercise realization description

No application code changes were performed in this exercise.
A new namespace `to-do-project` was created with [0.namespace.yaml](./manifests/0.namespace.yaml).

Code revision for this exercise was `0a34f1de`.

In order to perform this exercise I implemented deployment manifest as follows:
[namespace.yaml](./manifests/0.namespace.yaml)
```yaml
---
apiVersion: v1
kind: Namespace
metadata:
  name: to-do-project

```
[persistentvolume.yaml](./manifests/1.persistentvolume.yaml)
```yaml
---
apiVersion: v1
kind: PersistentVolume
metadata:
  namespace: to-do-project
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
[persistentvolumeclaim.yaml](./manifests/2.persistentvolumeclaim.yaml)
```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  namespace: to-do-project
  name: image-claim
spec:
  storageClassName: manual
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 1Gi
```
[service.yaml](./manifests/3.service.yaml)
```yaml
---
apiVersion: v1
kind: Service
metadata:
  namespace: to-do-project
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
  namespace: to-do-project
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
[deployment.yaml](./manifests/4.deployment.yaml)
```yaml
---
apiVersion: apps/v1
kind: Deployment
metadata:
  namespace: to-do-project
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
  namespace: to-do-project
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
[ingress.yaml](./manifests/5.ingress.yaml)
```yaml
---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  namespace: to-do-project
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
2. Moved to the folder of the previous ToDo application exercise (Exercise 2.02) with script:
    ```shell
    cd Exercise\ 2.02
    ```
3. Deleted previous artifacts with script:
    ```shell
    kubectl delete -f manifests
    ```
4. Moved to the current exercise folder with the script:
    ```shell
    cd ..
    cd Exercise\ 2.04
    ```
5. Applied configs with script:
    ```shell
    kubectl apply -f manifests/
    ```  
6. After the pod was initialized opened http://localhost:8081/to-do and added some todos ti check that everything still works.
