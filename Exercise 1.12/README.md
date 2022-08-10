# Exercise 1.12: Project v0.6

# Exercise realization description

ToDo Application description can be found in its [README](../to-do-project/README.md).  
In this exercise I added a UI part displaying a random photo to the project and a new environment variable `UPLOAD_LOCATION` with which it is possible to define an image location.
If it is not defined a default location `/usr/src/app/files/to-do-today.jpg` would be used.

Code revision for this exercise was `3d8cfb3a`.

In order to perform this exercise I implemented deployment manifest as follows:

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
[deployment.yaml](./manifests/2.deployment.yaml)
```yaml
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: to-do-project-dep
spec:
  replicas: 1
  selector:
    matchLabels:
      app: to-do-project
  template:
    metadata:
      labels:
        app: to-do-project
    spec:
      volumes:
        - name: shared-image
          persistentVolumeClaim:
            claimName: image-claim
      containers:
        -
          image: "katushka/to-do-project:0.6"
          name: to-do-project
          volumeMounts:
            - name: shared-image
              mountPath: /usr/src/app/local_files/images
          env:
            - name: UPLOAD_LOCATION
              value: "/usr/src/app/local_files/images/to-do-today.jpg"

```
[service.yaml](./manifests/3.service.yaml)
```yaml
apiVersion: v1
kind: Service
metadata:
  name: to-do-project-svc
spec:
  type: ClusterIP
  selector:
    app: to-do-project # This is the app as declared in the deployment.
  ports: # The following will let TCP traffic from port 2345 to port 8080.
    - port: 2345
      protocol: TCP
      targetPort: 8080
```
[ingress.yaml](./manifests/4.ingress.yaml)
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: to-do-project-ingress
spec:
  rules:
    - http:
        paths:
          - path: /to-do
            pathType: Prefix
            backend:
              service:
                name: to-do-project-svc
                port:
                  number: 2345

```

# How to perform required flow

Docker images can be found here:
- docker pull katushka/to-do-project:0.6

To perform exercise flow I did next steps:

1. Opened shell and moved to this folder.
2. Created docker images by running docker-compose with script:
    ```shell
    docker-compose build
    ```
3. Pushed docker images to Docker Hub with scripts:
    ```shell
    docker image push katushka/to-do-project:0.6
    ```
4. Created a directory for volume with script:
    ```shell
    docker exec k3d-k3s-default-agent-0 mkdir -p /tmp/kube/images
    ```
5. Applied configs with script:
    ```shell
    kubectl apply -f manifests/
    ```  
6. After the pod was initialized opened http://localhost:8081/to-do to see the image.
