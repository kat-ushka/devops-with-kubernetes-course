# Exercise 4.04: Project v1.8

## Exercise description

Create an AnalysisTemplate for the project that will follow the cpu usage of all containers in the namespace.

If the CPU usage rate sum for the namespace increases above a set value (you may choose a good hardcoded value for your project, may vary) within 10 minutes revert the update.

Make sure that the application doesn't get updated if the value is set too low.

## Exercise realization

1. First of all I set up Prometheus to GCP with:
    ```shell
    kubectl create namespace prometheus
    helm install prometheus-community/kube-prometheus-stack --generate-name --namespace prometheus
    ```
2. Then I installed argo-rollouts in a separate namespace with:
   ```shell
   kubectl create namespace argo-rollouts
   kubectl apply -n argo-rollouts -f https://github.com/argoproj/argo-rollouts/releases/latest/download/install.yaml
   ```
3. Wrote a request for Prometheus to find out CPU rate usage as follows:
   ```sql
   namespace_cpu:kube_pod_container_resource_limits:sum{namespace="NAMESPACE"} offset 10m
   ```
   and used it in an analysistemplate.yaml like this:
   ```yaml
   apiVersion: argoproj.io/v1alpha1
   kind: AnalysisTemplate
   metadata:
     name: cpu-rate
     namespace: NAMESPACE
   spec:
     metrics:
       - name: cpu-rate
         initialDelay: 10m
         successCondition: result[0] < 0.85
         provider:
           prometheus:
             address: http://kube-prometheus-stack-1662-prometheus.prometheus.svc.cluster.local:9090
             query: |
               namespace_cpu:kube_pod_container_resource_limits:sum{namespace="NAMESPACE"} offset 10m
   
   ```
4. Migrated my deployment.yaml config to rollout.yaml config by changing the apiVersion, kind and adding strategy as follows:
   ```yaml
   ...
     strategy:
       canary:
         steps:
           - setWeight: 50
           - analysis:
               templates:
                 - templateName: cpu-rate
   ...
   ```
5. Added a new analysistemplate.yaml config to kustomization.yaml and replaced the deployment.yaml with rollout.yaml
6. Added an `Install kubectl argo rollouts plugin` step to a build-publish-deploy job in .github/workflows/main.yaml to install argo rollouts kubectl plugin on running on machine
7. Changed  `kubectl rollout status ...` to `kubectl-argo-rollouts status` for new rollouts.
8. Pushed the changes and see the difference. At first I put the target CPU rate value to 0.6 and it fails and rollback the release. Then I changed it to 0.8 and it succeded.