# Exercise 3.08: Project v1.5

## Exercise description

Set sensible resource limits for the project. The exact values are not important. Test what works.

## Exercise realization

Resources limits were added to to-do-web application as follows:

[deployment.yaml](https://github.com/kat-ushka/to-do-project/blob/main/manifests/deployment.yaml)

```yaml
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
      volumes:
        - name: shared-image
          persistentVolumeClaim:
            claimName: image-claim
      containers:
        - image: to-do-web
          imagePullPolicy: Always
          name: to-do-web
          resources:
            limits:
              cpu: "200m"
              memory: "300Mi"
          volumeMounts:
            - name: shared-image
              mountPath: /usr/src/app/local_files/images
          env:
            - name: UPLOAD_LOCATION
              value: "/usr/src/app/local_files/images/to-do-today.jpg"
            - name: TODO_API_URI
              value: "http://to-do-api-svc:2345/api/todos"

```

And a new HorizontalPodAutoscaler object was created in manifests:

[horizontalpodautoscaler.yaml](https://github.com/kat-ushka/to-do-project/blob/main/manifests/horizontalpodautoscaler.yaml)

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: web-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: to-do-web
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