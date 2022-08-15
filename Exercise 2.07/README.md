# Exercise 2.07: Stateful applications

<!-- TOC -->
* [Exercise realization description](#exercise-realization-description)
* [How to perform required flow](#how-to-perform-required-flow)
<!-- TOC -->

## Exercise realization description

To implement this exercise I changed the project structure and combined Pingpong App, Log Output App and Timestamp Generator App in one module along with the database module.
Database folder contains only initializing scripts.

Pingpong Application was massively changed: SQL database interaction was added for counter endpoint and JSF support for displaying the Ping/pong page.

A headless service for Postgesql, a Secret and a StatefulSet objects were added for kubernetes deployments.

In order to perform this exercise I implemented manifests as follows:

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
apiVersion: v1 # Includes the Service for lazyness
kind: Service
metadata:
  namespace: log-output
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
[statefulset.yaml](./manifests/3.statefulset.yaml)
```yaml
---
apiVersion: apps/v1
kind: StatefulSet
metadata:
  namespace: log-output
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
          image: katushka/log-output-db:1.0
          imagePullPolicy: Always
          ports:
            - name: web
              containerPort: 5432
          volumeMounts:
            - name: data
              mountPath: /data
          env:
            - name: POSTGRES_DB
              value: ping
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
[deployment.yaml](./manifests/4.deployment.yaml)
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
        - image: "katushka/log-output:1.9"
          imagePullPolicy: Always
          name: log-output
          volumeMounts:
            - name: shared-file
              mountPath: /usr/src/app/local_files
          env:
            - name: TIME_STAMP_FILEPATH
              value: "/usr/src/app/local_files/timestamp"
            - name: PINGS_URL
              value: "http://pingpong-svc:2345/pingpong/api/counter"
          envFrom:
            - configMapRef:
                name: logoutput-config-env-file
        - image: "katushka/timestamp-generator:1.3"
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
        - image: "katushka/ping-pong:1.9"
          imagePullPolicy: Always
          name: ping-pong
          env:
            - name: DB_USER_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: postgres-secret-config
                  key:  POSTGRES_PASSWORD
                  optional: false
            - name: DB_USER
              value: "postgres"
            - name: DB_URL
              value: "jdbc:postgresql://db-svc:5432/ping"

```
[ingress.yaml](./manifests/5.ingress.yaml)
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
- docker pull katushka/log-output:1.9
- docker pull katushka/timestamp-generator:1.3
- docker pull katushka/ping-pong:1.9
- docker pull katushka/log-output-db:1.0

### Performing exercise-to-exercise flow
To perform exercise flow I did next steps:

1. Opened shell and checkout tag Exercise_2.07:
    ```shell
    git fetch --all --tags
    git checkout tags/Exercise_2.07
    ```
2. Moved to the folder of this exercise:
    ```shell
    cd Exercise\ 2.07
    ```
3. Created docker images by running docker-compose with script:
    ```shell
    docker-compose build
    ```
4. Pushed a new docker images to Docker Hub with a script:
    ```shell
    docker image push katushka/log-output-db:1.0
    docker image push katushka/timestamp-generator:1.3
    docker image push katushka/ping-pong:1.9
    docker image push katushka/log-output:1.9
    ```
5. Created an age key with script:
    ```shell
    age-keygen -o key.txt
    ```
6. Encrypted secret.yaml config with script:
    ```shell
    sops --encrypt --age age1rf6mvs2deuyrv34qsl5ftq7tfvs8f9f3d5f4y63hjcpgznyuc42qj6ep8z --encrypted-regex '^(data)$' secret.yaml > secret.enc.yaml
    ```
7. Created secret config with script:
    ```shell
    export SOPS_AGE_KEY_FILE=$(pwd)/key.txt
    sops --decrypt secret.enc.yaml | kubectl apply -f -
    ``` 
8. Applied other manifests:
    ```shell
    kubectl apply -f manifests
    ```
9. After the pod was initialized opened http://localhost:8081 to see the generated string with 0 ping-pongs.
   Then visited http://localhost:8081/pingpong to increase the number of ping-pongs and renewed http://localhost:8081 to see an update.

### How to do from the scratch

Assuming you have, k3d, kubectl, age, and sops already installed.

1. Open shell and checkout tag Exercise_2.07:
    ```shell
    git fetch --all --tags
    git checkout tags/Exercise_2.07
    ```
2. Move to the folder of this exercise:
    ```shell
    cd Exercise\ 2.07
    ```
3. Edit docker images labels in [docker-compose.yaml](docker-compose.yaml) and created them by running docker-compose with script:
    ```shell
    docker-compose build
    ```
4. Push new docker images to Docker Hub with a script (remember to change labels to the same that were chosen on the previous step):
    ```shell
    docker image push <your docker account>/log-output-db:1.0
    docker image push <your docker account>/timestamp-generator:1.3
    docker image push <your docker account>/ping-pong:1.9
    docker image push <your docker account>/log-output:1.9
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
9. Create a ConfigMap for the Log Output application:
   ```shell
   kubectl create configmap logoutput-config-env-file -n log-output --from-env-file=../log-output-project/log-output/configs/env-file.properties
   ```
10. Create a secret config:
     ```shell
     export SOPS_AGE_KEY_FILE=$(pwd)/key.txt
     sops --decrypt secret.enc.yaml | kubectl apply -f -
     ``` 
11. Applied all the other manifests:
     ```shell
     kubectl apply -f manifests
     ```
12. After the pod was initialized opened http://localhost:8081 to see the generated string with 0 ping-pongs.
    Then visited http://localhost:8081/pingpong to increase the number of ping-pongs and renewed http://localhost:8081 to see an update.
