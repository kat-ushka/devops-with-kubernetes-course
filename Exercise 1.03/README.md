# Exercise 1.03: Declarative approach

## Exercise realization description

Application description can be found in its [README](../log-output/README.md).

The revision of the code for this exercise was `ec911503`.

## How to perform required flow

Docker images can be found here:
- docker pull katushka/log-output:1.0

To perform exercise flow I did next steps:

1. Opened shell and moved to this folder.
2. Deleted previous deployment with command:  
    ```shell
    kubectl delete deployment log-output-dep
   ```
3. Applied new deployment with manifest:  
    ```shell
    kubectl apply manifests                     
   ``` 
4. After the pod had been initialized checked its name with command:
   ```shell
    kubectl get pods
   ```
   Got log-output-dep-6dcf4cdd66-kfs2g as created pod name and got it logs with command:
   ```shell
    kubectl logs log-output-dep-6dcf4cdd66-kfs2g
   ```