# Exercise 1.09: More services

# Exercise realization description

Log Output Application description can be found in its [README](../log-output/README.md).
Pingpong Application description can be found in its [README](../ping-pong/README.md).

Code revision for this exercise was `ddd47930`.

In order to perform this exercise I implemented new service manifest file as follows:

[log_output_deployment.yaml](./manifests/log_output_deployment.yaml)
```yaml
---
apiVersion: apps/v1
kind: Deployment
metadata:
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
      containers:
        -
          image: "katushka/log-output:1.2"
          name: log-output
```
[pingpong_deployment.yaml](./manifests/pingpong_deployment.yaml)
```yaml
---
apiVersion: apps/v1
kind: Deployment
metadata:
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
        -
          image: "katushka/ping-pong:1.1"
          name: ping-pong
```
[log_output_service.yaml](./manifests/log_output_service.yaml)
```yaml
---
apiVersion: v1
kind: Service
metadata:
  name: log-output-svc
spec:
  type: ClusterIP
  selector:
    app: log-output # This is the app as declared in the deployment.
  ports: # The following will let TCP traffic from port 2345 to port 8080.
    - port: 2345
      protocol: TCP
      targetPort: 8080
```
[pingpong_service.yaml](./manifests/pingpong_service.yaml)
```yaml
---
apiVersion: v1
kind: Service
metadata:
  name: pingpong-svc
spec:
  type: ClusterIP
  selector:
    app: ping-pong # This is the app as declared in the deployment.
  ports: # The following will let TCP traffic from port 2345 to port 8080.
    - port: 2345
      protocol: TCP
      targetPort: 8080
```
[ingress.yaml](./manifests/ingress.yaml)
```yaml
---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
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
                  number: 2345
          - path: /pingpong
            pathType: Prefix
            backend:
              service:
                name: pingpong-svc
                port:
                  number: 2345
```
# How to perform required flow

Docker images can be found here:
- docker pull katushka/log-output:1.2
- docker pull katushka/ping-pong:1.0

To perform exercise flow I did next steps:

1. Opened shell and moved to this folder.
2. Created docker images by running docker-compose with script:  
    ```shell
    docker-compose build
    ```
3. Pushed docker images to Docker Hub with scripts:  
    ```shell
    docker image push katushka/log-output:1.2
    docker image push katushka/ping-pong:1.0
    ```
4. Applied configs with script:  
    ```shell
    kubectl apply -f manifests/
    ```
5. After the pod was initialized opened http://localhost:8081 to see the response from log-output and http://localhost:8081/pingpong for response from ping-pong.
