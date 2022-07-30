# Exercise 1.08: Project v0.5

## Exercise realization description

Application *to-do-project* is a JAX-RS webapp which serves simple GET request.   
It is deployed in Tomcat webserver. Tomcat http port can be de set through CATALINA_HTTP_PORT env property. 

In order to perform this exercise I implemented manifests files as follows:

[deployment.yaml](./manifests/deployment.yaml)
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
      containers:
        -
          image: "katushka/to-do-project:0.5"
          name: to-do-project
```
[service.yaml](./manifests/service.yaml)
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
[ingress.yaml](./manifests/service.yaml)
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: to-do-project-ingress
spec:
  rules:
    - http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: to-do-project-svc
                port:
                  number: 2345

```
## How to perform required flow

Docker images can be found here:
- docker pull katushka/to-do-project:0.5

To perform exercise flow I did next steps:

0. Code revision is 85b61eef6762894c5b0e758381feffbae62aff65.
1. Opened shell and moved to this folder.
2. Created docker image with running docker-compose with command:  
    `docker-compose build`
3. Pushed docker image to Docker Hub with commands:  
    `docker image push katushka/to-do-project:0.5`
4. Started kubernetes cluster with command:  
    `k3d cluster create -p 8081:80@loadbalancer --agents 2`
5. Applied configs with commands:  
   `kubectl apply -f manifests/`  
6. After the pod was initialized opened http://localhost:8081 to see the response from to-do-project.
