# Exercise 3.02: Back to Ingress

<!-- TOC -->
* [Exercise description](#exercise-description)
* [Exercise realization description](#exercise-realization-description)
* [How to perform required flow](#how-to-perform-required-flow)
  * [Docker images](#docker-images)
  * [Performing exercise-to-exercise flow](#performing-exercise-to-exercise-flow)
  * [How to do from the scratch](#how-to-do-from-the-scratch)
<!-- TOC -->

## Exercise description

Deploy the "Log output" and "Ping-pong" applications into GKE and expose it with Ingress.

"Ping-pong" will have to respond from /pingpong path. This may require you to rewrite parts of the code.

## Exercise realization description

To implement this exercise I changed previously done manifests from the Exercise 2.07 and updated them for using in GKE:
- The type of the pingpong-svc and log-output-svc Service objects was changed to NodePort.
- statefulset object was overriden with the one from the Exercise 3.01.

I also added a new endpoint to the Pingpong allocation to serve GET requests to / and /pingpong paths.

The changed manifests looks as follows:

[service.yaml](./manifests/1.service.yaml)

```yaml
...
---
apiVersion: v1
kind: Service
metadata:
  namespace: log-output
  name: log-output-svc
spec:
  type: NodePort
  selector:
    app: log-output # This is the app as declared in the deployment.
  ports: # The following will let TCP traffic from port 2345 to port 8080.
    - port: 80
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
  type: NodePort
  selector:
    app: ping-pong # This is the app as declared in the deployment.
  ports: # The following will let TCP traffic from port 2345 to port 8080.
    - port: 80
      protocol: TCP
      targetPort: 8080
      name: http

```

## How to perform required flow

### Docker images

Docker images can be found here:
- docker pull katushka/log-output:1.9
- docker pull katushka/timestamp-generator:1.3
- docker pull katushka/ping-pong:1.10
- docker pull katushka/log-output-db:1.0

### Performing exercise-to-exercise flow

1. Open shell and move to the folder of this exercise:
    ```shell
    cd Exercise\ 3.02
    ```
2. Create a new kubernetes cluster:
    ```shell
    gcloud container clusters create dwk-cluster --zone=europe-north1-b --cluster-version=1.22
    ```
3. Create a namespace:
    ```shell
    kubectl apply -f manifests/0.namespace.yaml
    ```
4. Create a postgres secret config:
    ```shell
    sops --decrypt manifests/2.secret.enc.yaml | kubectl apply -f -
    ``` 
5. Create a Log Output Application config:
    ```shell
    kubectl create configmap logoutput-config-env-file -n log-output --from-env-file=../log-output-project/log-output/configs/env-file.properties
    ```
6. Apply other manifests (don't mind the error with 2.secret.enc.yaml):
    ```shell
    kubectl apply -f manifests
    ```
7. Check created ingress object:
    ```shell
    kubectl get ing -n log-output
    ```
8. Visit http://<dwk-ingress ip>:<dwk-ingress port>/pingpong to increase the number of ping-pongs.
9. Visit http://<dwk-ingress ip>:<dwk-ingress port>/ to see the generated string.
10. Delete the cluster to avoid using up the credits:
     ```shell
     gcloud container clusters delete dwk-cluster --zone=europe-north1-b
     ```

### How to do from the scratch

Assuming you have kubectl, age, sops, and Google Cloud SDK already installed and configured.
If not check [README.md](../README.md) for the installation links.

Check *Performing exercise-to-exercise flow* part of [README.md](../Exercise%203.01/README.md) for initial Google Cloud project configuring instructions.

1. Open shell and checkout tag Exercise_3.02:
    ```shell
    git fetch --all --tags
    git checkout tags/Exercise_3.02
    ```
2. Move to the folder of this exercise:
    ```shell
    cd Exercise\ 3.02
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
    docker image push <your docker account>/log-output:1.9
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
8. Follow with steps 2 to last of the *Performing exercise-to-exercise flow* part of this README.
