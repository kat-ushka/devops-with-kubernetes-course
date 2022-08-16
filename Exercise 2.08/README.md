# Exercise 2.08: Project v1.2

<!-- TOC -->
* [Exercise description](#exercise-description)
* [Exercise realization description](#exercise-realization-description)
* [How to perform required flow](#how-to-perform-required-flow)
  * [Docker images](#docker-images)
  * [Performing exercise-to-exercise flow](#performing-exercise-to-exercise-flow)
  * [How to do from the scratch](#how-to-do-from-the-scratch)
<!-- TOC -->
## Exercise description

Create a database and save the todos there.

Use Secrets and/or ConfigMaps to have the backend access the database.

## Exercise realization description

To implement this exercise I created a database module in to-do-project.
Database folder contains only an initializing script.

SQL database interaction was added to the to-do-api module.

A headless service for Postgesql, a Secret with Postgres environment variables and a StatefulSet Postgesql objects were added for kubernetes deployments.

In order to perform this exercise I implemented manifests as follows (those that were created or changed):

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

...
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
5. Created a secret.yaml like follows:
    ```yaml
    apiVersion: v1
    kind: Secret
    metadata:
     namespace: to-do-project
     name: postgres-secret-config
    type: Opaque
    data:
     POSTGRES_DB: <base64 database name>
     POSTGRES_USER: <base64 user name>
     POSTGRES_PASSWORD: <base64 password>
    ```
6. Used an age key created before to encrypt secret.yaml:
    ```shell
    sops --encrypt --age age1rf6mvs2deuyrv34qsl5ftq7tfvs8f9f3d5f4y63hjcpgznyuc42qj6ep8z --encrypted-regex '^(data)$' secret.yaml > secret.enc.yaml
    ```
7. Created secret config with script:
    ```shell
    sops --decrypt secret.enc.yaml | kubectl apply -f -
    ``` 
8. Applied other manifests:
    ```shell
    kubectl apply -f manifests
    ```
9. Opened http://localhost:8081/to-do and add some todos to check that everything works.

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
5. Create a secret.yaml file like this:
    ```yaml
    apiVersion: v1
    kind: Secret
    metadata:
     namespace: to-do-project
     name: postgres-secret-config
    type: Opaque
    data:
     POSTGRES_DB: <base64 database name>
     POSTGRES_USER: <base64 user name>
     POSTGRES_PASSWORD: <base64 password>
    ```
6. Create an age key with script:
    ```shell
    age-keygen -o key.txt
    ```
7. Encrypt secret.yaml config with script:
    ```shell
    sops --encrypt --age <public key from the previous step> --encrypted-regex '^(data)$' secret.yaml > secret.enc.yaml
    ```
8. Create a k3d cluster:
    ```shell
    k3d cluster create -p 8081:80@loadbalancer
    ```
9. Create a namespace:
   ```shell
   kubectl apply -f manifests/0.namespace.yaml
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
12. Open http://localhost:8081/to-do and add some todos to check that everything works.
