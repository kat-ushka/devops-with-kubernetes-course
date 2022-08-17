# Exercise 2.07: Stateful applications

<!-- TOC -->
* [Exercise description](#exercise-description)
* [Exercise realization description](#exercise-realization-description)
* [How to perform required flow](#how-to-perform-required-flow)
  * [Docker images](#docker-images)
  * [Performing exercise-to-exercise flow](#performing-exercise-to-exercise-flow)
  * [How to do from the scratch](#how-to-do-from-the-scratch)
<!-- TOC -->

## Exercise description

Run a postgres database and save the Ping-pong application counter into the database.

The postgres database and Ping-pong application should not be in the same pod. 
A single postgres database is enough and it may disappear with the cluster but it should survive 
even if all pods are taken down.

You should not write the database password in plain text.

## Exercise realization description

To implement this exercise I changed the project structure and combined Pingpong App, Log Output App and Timestamp Generator App in one module along with the database module.
Database folder contains only initializing scripts.

Pingpong Application was massively changed: SQL database interaction was added for counter endpoint and JSF support for displaying the Ping/pong page.

A headless service for Postgesql, a Secret and a StatefulSet objects were added for kubernetes deployments.

In order to perform this exercise I changed kubernetes manifests as follows:

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

...

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
...

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

## How to perform required flow

### Docker images

Docker images can be found here:
- docker pull katushka/log-output:1.9
- docker pull katushka/timestamp-generator:1.3
- docker pull katushka/ping-pong:1.9
- docker pull katushka/log-output-db:1.0

### Performing exercise-to-exercise flow

1. Open shell and move to the folder of this exercise:
    ```shell
    cd Exercise\ 2.07
    ```
2. Create an age key with script:
    ```shell
    age-keygen -o key.txt
    ```
3. Create a secret.yaml file like this:
    ```yaml
    apiVersion: v1
    kind: Secret
    metadata:
      namespace: log-output
      name: postgres-secret-config
    type: Opaque
    data:
      POSTGRES_PASSWORD: <base64 password>
    ```
4. Encrypt secret.yaml config with script:
    ```shell
    sops --encrypt --age <public_key from the step 3> --encrypted-regex '^(data)$' secret.yaml > secret.enc.yaml
    ```
5. Create secret config with script:
    ```shell
    export SOPS_AGE_KEY_FILE=$(pwd)/key.txt
    sops --decrypt secret.enc.yaml | kubectl apply -f -
    ``` 
6. Apply other manifests:
    ```shell
    kubectl apply -f manifests
    ```
7. Open http://localhost:8081 to see the generated string with 0 ping-pongs.
   Then visit http://localhost:8081/pingpong to increase the number of ping-pongs and renew http://localhost:8081 to see an update.

### How to do from the scratch

Assuming you have, k3d, kubectl, age, and sops already installed.
If not check [README.md](../README.md) for the installation links.

1. Open shell and checkout tag Exercise_2.07:
    ```shell
    git fetch --all --tags
    git checkout tags/Exercise_2.07
    ```
2. Move to the folder of this exercise:
    ```shell
    cd Exercise\ 2.07
    ```
3. Edit docker images labels in [docker-compose.yaml](docker-compose.yaml) and create them by running docker-compose with script:
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
11. Applied all the other manifests (remember to change labels of the docker images if you had created you own on the step 4):
     ```shell
     kubectl apply -f manifests
     ```
12. Open http://localhost:8081 to see the generated string with 0 ping-pongs.
    Then visit http://localhost:8081/pingpong to increase the number of ping-pongs and renew http://localhost:8081 to see an update.
