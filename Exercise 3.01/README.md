# Exercise 3.01: Pingpong GKE

<!-- TOC -->
* [Exercise description](#exercise-description)
* [Exercise realization description](#exercise-realization-description)
* [How to perform required flow](#how-to-perform-required-flow)
  * [Docker images](#docker-images)
  * [Performing exercise-to-exercise flow](#performing-exercise-to-exercise-flow)
  * [How to do from the scratch](#how-to-do-from-the-scratch)
<!-- TOC -->

## Exercise description

Deploy ping / pong application into GKE.

In this exercise use a LoadBalancer service to expose the service.

If your postgres logs say

>initdb: error: directory "/var/lib/postgresql/data" exists but is not empty
It contains a lost+found directory, perhaps due to it being a mount point.
Using a mount point directly as the data directory is not recommended.
Create a subdirectory under the mount point.

you can add subPath configuration:

statefulset.yaml
```yaml
volumeMounts:
- name: data
  mountPath: /var/lib/postgresql/data
  subPath: postgres

```
This will create a postgres directory where the data will reside. 
subPaths also make it possible to use single volume for multiple purposes.

## Exercise realization description

To implement this exercise I copied previously done manifests from the Exercise 2.07 and updated them for using in GKE:
- The type of the pingpong-svc Service object was changed to LoadBalancer.
- storageClassName was removed from the volumeClaimTemplates spec of the postgresql StatefulSet object.
- subPath was added to the volumeMounts of the postgresql StatefulSet object.

No changes to the code were made.

The changed manifests looks as follows:

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
              mountPath: /var/lib/postgresql/data
              subPath: postgres
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
        #storageClassName: local-path
        resources:
          requests:
            storage: 100Mi

```

## How to perform required flow

### Docker images

Docker images can be found here:
- docker pull katushka/ping-pong:1.9
- docker pull katushka/log-output-db:1.0

### Performing exercise-to-exercise flow

1. Open shell and move to the folder of this exercise:
    ```shell
    cd Exercise\ 3.01
    ```
2. Create a new project named `dwk-gke` on [resources page](https://console.cloud.google.com/cloud-resource-manager).
3. Install Google CLoud SDK (instruction is [here](https://cloud.google.com/sdk/docs/install))
4. After completing the initialization set the previously created project to be used:
    ```shell
    gcloud config set project dwk-gke
    ```
5. Enable the container.googleapis.com service:
    ```shell
    gcloud services enable container.googleapis.com
    ```
6. Create a new  kubernetes cluster:
    ```shell
    gcloud container clusters create dwk-cluster --zone=europe-north1-b --cluster-version=1.22
    ```
7. Create a namespace:
    ```shell
    kubectl apply -f manifests/0.namespace.yaml
    ```
8. Create a postgres secret config:
    ```shell
    sops --decrypt manifests/2.secret.enc.yaml | kubectl apply -f -
    ``` 
9. Apply other manifests (don't mind the error with 2.secret.enc.yaml):
    ```shell
    kubectl apply -f manifests
    ```
10. Check an IP of the pingpong-svc with kubectl script:
     ```shell
     kubectl get svc --watch
     ```
11. Visit http://<pingpong-svc host>:<pingpong-svc port>/pingpong to increase the number of ping-pongs
12. Delete the cluster to avoid using up the credits:
     ```shell
     gcloud container clusters delete dwk-cluster --zone=europe-north1-b
     ```

### How to do from the scratch

Assuming you have, k3d, kubectl, Lens, age, and sops already installed.

1. Perform the flow of Exercise_2.07 as it is described in *How to do from the scratch* part of [README.md](../Exercise%202.07/README.md).
2. Continue with *Performing exercise-to-exercise flow* part of this README.
