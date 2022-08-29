# Exercise 4.01: Readiness Probe

## Exercise description

Create a ReadinessProbe for "Ping-pong" application. It should be ready when it has a connection to database.

And another ReadinessProbe for "Log output" application. It should be ready when it can receive data from "Ping-pong" application.

Test that it works by applying everything but the database statefulset. 
The output of `kubectl get po` should look like this before the database is available:

>NAME&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;READY&nbsp;&nbsp;&nbsp;STATUS&nbsp;&nbsp;&nbsp;&nbsp;RESTARTS&nbsp;&nbsp;&nbsp;AGE  
>logoutput-dep-7f49547cf4-ttj4f&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1/2&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Running&nbsp;&nbsp;&nbsp;&nbsp;0&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;21s  
>pingpong-dep-9b698d6fb-jdgq9&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;0/1&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Running&nbsp;&nbsp;&nbsp;&nbsp;0&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;21s  

Adding the database should automatically move the READY states to 2/2 and 1/1 for "Log output" and "Ping-pong" respectively.

## Exercise realization

Dedicated /healthz http-endpoints were added to Log Output and Pingpong applications.

Readiness probes were added as follows:

[deployment.yaml](./manifests/4.deployment.yaml)

```yaml
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
        - image: "katushka/ping-pong:1.11"
          imagePullPolicy: Always
          name: ping-pong
          #...
          readinessProbe:
            initialDelaySeconds: 10
            periodSeconds: 10
            httpGet:
              path: /pingpong/api/healthz
              port: 8080
          #...

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
        - image: "katushka/log-output:1.11"
          imagePullPolicy: Always
          name: log-output
          #...
          readinessProbe:
            initialDelaySeconds: 10
            periodSeconds: 10
            httpGet:
              path: /healthz
              port: 8080
          #...

```
## How to perform required flow

### Docker images

Docker images can be found here:
- docker pull katushka/ping-pong:1.11
- docker pull katushka/timestamp-generator:1.3
- docker pull katushka/ping-pong:1.11
- docker pull katushka/log-output-db:1.0

### Performing exercise-to-exercise flow

1. Open shell and move to the folder of this exercise:
    ```shell
    cd Exercise\ 4.01
    ```
2. Create a new kubernetes cluster (pay attention to the version, autoscaling/v2 is not accessible in 1.22):
    ```shell
    gcloud container clusters create dwk-cluster --zone=europe-north1-b --cluster-version=1.24
    ```
3. Create a namespace:
    ```shell
    kubectl apply -f manifests/0.namespace.yaml
    ```
4. Create a config map:
    ```shell
    kubectl create configmap logoutput-config-env-file -n log-output --from-env-file=../log-output-project/log-output/configs/env-file.properties
    ```
5. Create a postgres secret config:
    ```shell
    sops --decrypt manifests/2.secret.enc.yaml | kubectl apply -f -
    ``` 
6. Apply other manifests:
    ```shell
    kubectl apply -f manifests/1.service.yaml
    kubectl apply -f manifests/4.deployment.yaml
    ```
7. Check the readiness of pods with kubectl script:
    ```shell
    kubectl get po -n log-output
    ```
8. Apply database manifest:
   ```shell
   kubectl apply -f manifests/3.statefulset.yaml
   ```
9. Wait for 5 minutes and check the readiness of the pods again:
    ```shell
    kubectl get po --watch -n log-output
    ```
10. Delete the cluster to avoid using up the credits:
     ```shell
     gcloud container clusters delete dwk-cluster --zone=europe-north1-b
     ```

### How to do from the scratch

Assuming you have kubectl, age, sops, and Google Cloud SDK already installed and configured.
If not check [README.md](../README.md) for the installation links.

Assuming also, that you've checkouted this repo to your local machine.

1. Open shell, move to the project folder, and checkout tag Exercise_3.09:
    ```shell
    git fetch --all --tags
    git checkout tags/Exercise_4.01
    ```
2. Move to the folder of this exercise:
    ```shell
    cd Exercise\ 4.01
    ```
3. Edit docker images labels in [docker-compose.yaml](docker-compose.yaml) and create them by running docker-compose with script:
    ```shell
    docker-compose build
    ```
4. Push new docker images to Docker Hub with a script (remember to change labels to the same that were chosen on the previous step):
    ```shell
    docker image push <your docker account>/log-output-db:1.0
    docker image push <your docker account>/timestamp-generator:1.3
    docker image push <your docker account>/ping-pong:1.11
    docker image push <your docker account>/log-output:1.11
    ```
5. Create a secret.yaml file like this:
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
6. Create an age key with script:
    ```shell
    age-keygen -o key.txt
    ```
7. Encrypt secret.yaml config with script:
    ```shell
    sops --encrypt --age <public key from the previous step> --encrypted-regex '^(data)$' secret.yaml > secret.enc.yaml
    ```
8. Create a new project named `dwk-gke` on [resources page](https://console.cloud.google.com/cloud-resource-manager).
9. Set the previously created project to be used:
    ```shell
    gcloud config set project dwk-gke
    ```
10. Enable the container.googleapis.com service:
     ```shell
     gcloud services enable container.googleapis.com
     ```
11. Create a new kubernetes cluster (pay attention to the version, autoscaling/v2 is not accessible in 1.22):
    ```shell
    gcloud container clusters create dwk-cluster --zone=europe-north1-b --cluster-version=1.24
    ```
12. Create a namespace:
     ```shell
     kubectl apply -f manifests/0.namespace.yaml
     ```
13. Create a config map:
     ```shell
     kubectl create configmap logoutput-config-env-file -n log-output --from-env-file=../log-output-project/log-output/configs/env-file.properties
     ```
14. Create a postgres secret config:
     ```shell
     sops --decrypt manifests/2.secret.enc.yaml | kubectl apply -f -
     ``` 
15. Apply other manifests:
     ```shell
     kubectl apply -f manifests/1.service.yaml
     kubectl apply -f manifests/4.deployment.yaml
     ```
16. Check the readiness of pods with kubectl script:
     ```shell
     kubectl get po -n log-output
     ```
17. Apply database manifest:
    ```shell
    kubectl apply -f manifests/3.statefulset.yaml
    ```
18. Wait for 5 minutes and check the readiness of pods again:
     ```shell
     kubectl get po --watch -n log-output
     ```
19. Delete the cluster to avoid using up the credits:
     ```shell
     gcloud container clusters delete dwk-cluster --zone=europe-north1-b
     ```