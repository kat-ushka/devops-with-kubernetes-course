# Exercise 4.02: Project v1.7

## Exercise description

Create the required Probes and endpoint for the project to ensure that it's working and connected to a database.

Test that it's indeed working with a version without database access, for example by supplying a wrong database url or credentials.

## Exercise realization

Dedicated health check http-endpoints were added to ToDo web and api applications.

Readiness and liveness probes were added as follows:

[deployment.yaml](https://github.com/kat-ushka/to-do-project/blob/main/manifests/deployment.yaml)

```yaml
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: to-do-api
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
        - image: to-do-api
          name: to-do-api
          readinessProbe:
            initialDelaySeconds: 10
            periodSeconds: 10
            httpGet:
              path: /api/healthz/ready
              port: 8080
          livenessProbe:
            initialDelaySeconds: 30
            periodSeconds: 20
            httpGet:
              path: /api/healthz/alive
              port: 8080
          ...

---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: to-do-web
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
      ...
      containers:
        - image: to-do-web
          name: to-do-web
          readinessProbe:
            initialDelaySeconds: 90
            periodSeconds: 20
            httpGet:
              path: /todo/api/healthz
              port: 8080
          livenessProbe:
            initialDelaySeconds: 90
            periodSeconds: 20
            httpGet:
              path: /todo/test.xhtml
              port: 8080
          ...

```

The code and the manifests can be found in [this](https://github.com/kat-ushka/to-do-project) repo.