# Exercise 3.09: Resource limits

## Exercise description

Set sensible resource limits for the "Ping-pong" and "Log output" applications. The exact values are not important. Test what works.

## Exercise realization

Resources limits were added to to-do-web application as follows:

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
        - image: "katushka/ping-pong:1.10"
          imagePullPolicy: Always
          name: ping-pong
          resources:
            limits:
              cpu: "200m"
              memory: "300Mi"
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
        - image: "katushka/log-output:1.10"
          imagePullPolicy: Always
          name: log-output
          resources:
            limits:
              cpu: "150m"
              memory: "200Mi"
          volumeMounts:
            - name: shared-file
              mountPath: /usr/src/app/local_files
          env:
            - name: TIME_STAMP_FILEPATH
              value: "/usr/src/app/local_files/timestamp"
            - name: PINGS_URL
              value: "http://pingpong-svc/pingpong/api/counter"
          envFrom:
            - configMapRef:
                name: logoutput-config-env-file
        - image: "katushka/timestamp-generator:1.3"
          imagePullPolicy: Always
          name: timestamp-generator
          resources:
            limits:
              cpu: "50m"
              memory: "50Mi"
          volumeMounts: # Mount volume
            - name: shared-file
              mountPath: /usr/src/app/local_files
          env:
            - name: TIME_STAMP_FILEPATH
              value: "/usr/src/app/local_files/timestamp"


```

And a new HorizontalPodAutoscaler object was created in manifests:

[horizontalpodautoscaler.yaml](./manifests/6.horizontalpodautoscaler.yaml)

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: ping-pong-hpa
  namespace: log-output
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: ping-pong-dep
  minReplicas: 1
  maxReplicas: 3
  behavior:
    scaleDown:
      stabilizationWindowSeconds: 60
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 60

---
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: log-output-hpa
  namespace: log-output
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: log-output-dep
  minReplicas: 1
  maxReplicas: 3
  behavior:
    scaleDown:
      stabilizationWindowSeconds: 60
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 60

```

## How to perform required flow

### Docker images

Docker images can be found here:
- docker pull katushka/ping-pong:1.10
- docker pull katushka/timestamp-generator:1.3
- docker pull katushka/ping-pong:1.10
- docker pull katushka/log-output-db:1.0

### Performing exercise-to-exercise flow

1. Open shell and move to the folder of this exercise:
    ```shell
    cd Exercise\ 3.09
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
6. Apply other manifests (don't mind the error with 2.secret.enc.yaml):
    ```shell
    kubectl apply -f manifests
    ```
7. Check an IP of the created ingress with kubectl script:
    ```shell
    kubectl get ing --watch -n log-output
    ```
8. Visit http://<dwk-ingress address>:<dwk-ingress port>/pingpong/api/counter/increment to increase the number of ping-pongs
9. Visit http://<dwk-ingress address>:<dwk-ingress port> to see the generated source
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
    git checkout tags/Exercise_3.09
    ```
2. Move to the folder of this exercise:
    ```shell
    cd Exercise\ 3.09
    ```
3. Edit docker images labels in [docker-compose.yaml](docker-compose.yaml) and create them by running docker-compose with script:
    ```shell
    docker-compose build
    ```
4. Push new docker images to Docker Hub with a script (remember to change labels to the same that were chosen on the previous step):
    ```shell
    docker image push <your docker account>/log-output-db:1.0
    docker image push <your docker account>/timestamp-generator:1.3
    docker image push <your docker account>/ping-pong:1.10
    docker image push <your docker account>/log-output:1.10
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
15. Apply other manifests (don't mind the error with 2.secret.enc.yaml):
     ```shell
     kubectl apply -f manifests
     ```
16. Check an IP of the created ingress with kubectl script:
     ```shell
     kubectl get ing --watch -n log-output
     ```
17. Visit http://<dwk-ingress address>:<dwk-ingress port>/pingpong/api/counter/increment to increase the number of ping-pongs
18. Visit http://<dwk-ingress address>:<dwk-ingress port> to see the generated source
19. Delete the cluster to avoid using up the credits:
     ```shell
     gcloud container clusters delete dwk-cluster --zone=europe-north1-b
     ```