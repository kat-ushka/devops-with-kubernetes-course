# Exercise 1.07: External access with Ingress

<!-- TOC -->
* [Exercise realization description](#exercise-realization-description)
* [How to perform required flow](#how-to-perform-required-flow)
<!-- TOC -->

## Exercise realization description

Application description can be found in its [README](../log-output/README.md).  
To complete this exercise `com.github.katushka.devopswithkubernetescourse.resource.GeneratedStringResource` endpoint was added.
It serves simple get request.

The revision of the code for this exercise is tagged with `Exercise_1.07`.

In order to perform this exercise I implemented manifests files as follows:

[service.yaml](./manifests/service.yaml)
```yaml
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
[ingress.yaml](./manifests/service.yaml)
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: log-output-ingress
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
```
## How to perform required flow

Docker images can be found here:
- docker pull katushka/log-output:1.1

To perform exercise flow I did next steps:

1. Opened shell and moved to the project folder.
2. Checkout tag `Exercise_1.07`:
    ```shell
    git fetch --all --tags
    git checkout tags/Exercise_1.07
    ```
3. Moved to the exercise folder:
    ```shell
    cd Exercise\ 1.07
    ```
4. Created docker image with running docker-compose with command:  
    ```shell
    docker-compose build
    ```
5. Pushed docker image to Docker Hub with command:
    ```shell
    docker image push katushka/log-output:1.1
    ```
6. Applied configs with command:  
    ```shell
    kubectl apply -f manifests/
    ```
7. After the pod was initialized opened http://localhost:8081 to see the response from log-output.
