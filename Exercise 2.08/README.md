# Exercise 2.08: Project v1.2

<!-- TOC -->
* [Exercise realization description](#exercise-realization-description)
* [How to perform required flow](#how-to-perform-required-flow)
  * [Docker images](#docker-images)
  * [Performing exercise-to-exercise flow](#performing-exercise-to-exercise-flow)
  * [How to do from the scratch](#how-to-do-from-the-scratch)
<!-- TOC -->

## Exercise realization description

To implement this exercise I created a database module in to-do-project.
Database folder contains only an initializing script.

SQL database interaction was added to the to-do-api module.

A headless service for Postgesql, a Secret with Postgres environment variables and a StatefulSet Postgesql objects were added for kubernetes deployments.

In order to perform this exercise I implemented manifests as follows:

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
apiVersion: v1 # Includes the Service for lazyness
kind: Service
metadata:
   namespace: to-do-project
   name: db-svc
   labels:
      app: postgres
spec:
   ports:
      - port: 5432
        name: web
   clusterIP: None
   selector:
      app: postgres-app

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
[statefulset.yaml](./manifests/4.statefulset.yaml)
```yaml
---
apiVersion: apps/v1
kind: StatefulSet
metadata:
   namespace: to-do-project
   name: postgresql
spec:
   serviceName: db-svc
   replicas: 1
   selector:
      matchLabels:
         app: postgres-app
   template:
      metadata:
         labels:
            app: postgres-app
      spec:
         containers:
            - name: postgres
              image: katushka/to-do-db:1.0
              imagePullPolicy: Always
              ports:
                 - name: web
                   containerPort: 5432
              volumeMounts:
                 - name: data
                   mountPath: /data
              envFrom:
                 - secretRef:
                      name: postgres-secret-config
   volumeClaimTemplates:
      - metadata:
           name: data
        spec:
           accessModes: ["ReadWriteOnce"]
           storageClassName: local-path
           resources:
              requests:
                 storage: 100Mi

```
[deployment.yaml](./manifests/5.deployment.yaml)
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
        - image: "katushka/to-do-api:1.1"
          name: to-do-api
          imagePullPolicy: Always
          env:
            - name: DB_HOST
              value: db-svc
            - name: DB_PORT
              value: "5432"
            - name: DB_USER_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: postgres-secret-config
                  key: POSTGRES_PASSWORD
                  optional: false
            - name: DB_USER
              valueFrom:
                secretKeyRef:
                  name: postgres-secret-config
                  key: POSTGRES_USER
                  optional: false
            - name: DB_NAME
              valueFrom:
                secretKeyRef:
                  name: postgres-secret-config
                  key: POSTGRES_DB
                  optional: false

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
        - image: "katushka/to-do-web:1.1"
          imagePullPolicy: Always
          name: to-do-web
          volumeMounts:
            - name: shared-image
              mountPath: /usr/src/app/local_files/images
          env:
            - name: UPLOAD_LOCATION
              value: "/usr/src/app/local_files/images/to-do-today.jpg"
            - name: TODO_API_URI
              value: "http://to-do-api-svc:2345/to-do-api/api/todos"

```
[ingress.yaml](./manifests/6.ingress.yaml)
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
## How to perform required flow

### Docker images

Docker images can be found here:
- docker pull katushka/to-do-db:1.0
- docker pull katushka/to-do-api:1.1
- docker pull katushka/to-do-web:1.1

### Performing exercise-to-exercise flow
To perform exercise flow I did next steps:

1. Opened shell and checkout tag Exercise_2.08:
    ```shell
    git fetch --all --tags
    git checkout tags/Exercise_2.08
    ```
2. Moved to the folder of this exercise:
    ```shell
    cd Exercise\ 2.08
    ```
3. Created docker images by running docker-compose with script:
    ```shell
    docker-compose build
    ```
4. Pushed a new docker images to Docker Hub with a script:
    ```shell
    docker image push katushka/to-do-db:1.0
    docker image push katushka/to-do-api:1.1
    docker image push katushka/to-do-web:1.1
    ```
5. Used an age key created before to encrypt secret.yaml:
    ```shell
    sops --encrypt --age age1rf6mvs2deuyrv34qsl5ftq7tfvs8f9f3d5f4y63hjcpgznyuc42qj6ep8z --encrypted-regex '^(data)$' secret.yaml > secret.enc.yaml
    ```
6. Created secret config with script:
    ```shell
    sops --decrypt secret.enc.yaml | kubectl apply -f -
    ``` 
7. Applied other manifests:
    ```shell
    kubectl apply -f manifests
    ```
8. After the pod was initialized opened http://localhost:8081/to-do and added some todos ti check that everything still works.

### How to do from the scratch

Assuming you have, k3d, kubectl, age, and sops already installed.

1. Open shell and checkout tag Exercise_2.08:
    ```shell
    git fetch --all --tags
    git checkout tags/Exercise_2.08
    ```
2. Move to the folder of this exercise:
    ```shell
    cd Exercise\ 2.08
    ```
3. Edit docker images labels in [docker-compose.yaml](docker-compose.yaml) and created them by running docker-compose with script:
    ```shell
    docker-compose build
    ```
4. Push new docker images to Docker Hub with a script (remember to change labels to the same that were chosen on the previous step):
    ```shell
    docker image push <your docker account>/to-do-db:1.0
    docker image push <your docker account>/to-do-api:1.1
    docker image push <your docker account>/to-do-web:1.1
    ```
5. Create an age key with script:
    ```shell
    age-keygen -o key.txt
    ```
6. Encrypt secret.yaml config with script:
    ```shell
    sops --encrypt --age <public key from the previous step> --encrypted-regex '^(data)$' secret.yaml > secret.enc.yaml
    ```
7. Create a k3d cluster:
    ```shell
    k3d cluster create -p 8081:80@loadbalancer
    ```
8. Create a namespace:
   ```shell
   kubectl apply -f manifests/0.namespace.yaml
   ```
9. Create a secret config:
    ```shell
    export SOPS_AGE_KEY_FILE=$(pwd)/key.txt
    sops --decrypt secret.enc.yaml | kubectl apply -f -
    ``` 
10. Applied all the other manifests (remember to change labels of the docker images if you had created you own on the step 4):
     ```shell
     kubectl apply -f manifests
     ```
11. After the pod was initialized opened http://localhost:8081/to-do and add some todos to check that everything works.
