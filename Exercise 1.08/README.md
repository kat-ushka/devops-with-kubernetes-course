# Exercise 1.08: Project v0.5

## Exercise realization description

Application *to-do-project* is a JAX-RS webapp which serves simple GET request.   
It is deployed in Tomcat webserver. Tomcat http port can be de set through CATALINA_HTTP_PORT env property. 

In order to perform this exercise I implemented manifests files as follows:

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

Docker image can be found here:
- docker pull katushka/to-do-project:0.3  
  There were no changes to the code or deployment, so the image is the same as for the Exercise 1.05.

To perform exercise flow I did next steps:

1. Opened shell and moved to this folder.
2. Deleted the ingress for log-output application with script:  
    ```shell
    kubectl delete -f "../Exercise 1.07/manifests/ingress.yaml"
    ```
3. Applied new configs with script:  
   `kubectl apply -f manifests/`  
4. Opened url http://localhost:8081 in browser to see the response from to-do-project.
