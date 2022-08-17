# Exercise 2.02: Project v1.0

<!-- TOC -->
* [Exercise description](#exercise-description)
* [Exercise realization description](#exercise-realization-description)
* [How to perform required flow](#how-to-perform-required-flow)
  * [Docker images](#docker-images)
  * [Performing exercise-to-exercise flow](#performing-exercise-to-exercise-flow)
  * [How to do from the scratch](#how-to-do-from-the-scratch)
<!-- TOC -->

## Exercise description

Create a new container for the backend of the todo application.

You can use graphql or other solutions if you want.

Use ingress routing to enable access to the backend.

Create a POST /todos endpoint and a GET /todos endpoint in the new service where we can post a new todo and get all of the todos. 
You can also move the image logic to the new service if it requires backend logic.

The todos can be saved into memory, we'll add database later.

Frontend already has an input field. 
Connect it into our backend so that inputting data and pressing send will add a new todo into the list.

## Exercise realization description

In this exercise I created 3 submodules for the ToDo Application: to-do-web, to-do-api, and to-do-common.
A new [ToDoResource](../to-do-project/to-do-api/src/main/java/com/github/katushka/devopswithkubernetescourse/todobackend/resources/ToDoResource.java) is added to the to-do-api
to serve a POST /todos endpoint and a GET /todos endpoint. It saves ToDos in collection in memory.

The image logic stays in to-do-web.
A [ToDoService](../to-do-project/to-do-web/src/main/java/com/github/katushka/devopswithkubernetescourse/todoproject/services/ToDoService.java) makes requests to the to-do-api /todos endpoints.

The revision of the code for this exercise is tagged with `Exercise_2.02`.

In order to perform this exercise I implemented kubernetes manifests as follows:

[persistentvolume.yaml](./manifests/0.persistentvolume.yaml)
```yaml
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
```yaml
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
```yaml
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
```yaml
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
```yaml
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

## How to perform required flow

### Docker images

Docker images can be found here:
- docker pull katushka/to-do-api:1.0
- docker pull katushka/to-do-web:1.0

### Performing exercise-to-exercise flow

1. Open shell and moved to the project folder.
2. Move to the folder of the previous ToDo application exercise (Exercise 1.12) with script:
    ```shell
    cd Exercise\ 1.12
    ```
3. Delete previous artifacts with script:
    ```shell
    kubectl delete -f manifests
    ```
4. Move to the current exercise folder with the script:
    ```shell
    cd ../Exercise\ 2.02
    ```
5. Create a folder for persistent volume (it was deleted in previous exercise):
   ```shell
    docker exec k3d-k3s-default-agent-0 mkdir -p /tmp/kube/images
   ```
6. Applied configs with script:
    ```shell
    kubectl apply -f manifests/
    ```  
7. Open http://localhost:8081/to-do in browser and add some todos.

### How to do from the scratch

Assuming you have k3d and kubectl already installed.

1. Opened shell and moved to the project folder.
2. Checkout tag `Exercise_2.02`:
    ```shell
    git fetch --all --tags
    git checkout tags/Exercise_2.02
    ```
3. Moved to the folder of this exercise with script:
    ```shell
    cd Exercise\ 2.02
    ```
4. Edit docker images labels in [docker-compose.yaml](./docker-compose.yaml) and create them by running docker-compose with script:
    ```shell
    docker-compose build
    ```
5. Push docker images to Docker Hub with scripts (remember to change labels to the same that were chosen on the previous step):
    ```shell
    docker image push <your docker account>/to-do-api:1.0
    docker image push <your docker account>/to-do-web:1.0
    ```
6. Create a k3d cluster:
    ```shell
    k3d cluster create -p 8081:80@loadbalancer
    ```
7. Create a folder for persistent volume
   ```shell
    docker exec k3d-k3s-default-agent-0 mkdir -p /tmp/kube/images
   ```
8. Apply configs with script:
    ```shell
    kubectl apply -f manifests/
    ```  
9. Open http://localhost:8081/to-do in browser and add some todos.