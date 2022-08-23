# Exercise 3.03: Project v1.4

<!-- TOC -->
* [Exercise description](#exercise-description)
* [Exercise realization description](#exercise-realization-description)
* [How to perform required flow](#how-to-perform-required-flow)
<!-- TOC -->

## Exercise description

Setup automatic deployment for the project as well.

## Exercise realization description

To implement this exercise I moved to-do-project to the separate [repo](https://github.com/kat-ushka/to-do-project).
Some code changes were performed to support ingress health checks.

To implement this exercise I changed previously done manifests from the Exercises 2.08 - 2.10 and updated them for using in GKE:
- The type of the to-do-api-svc and to-do-web-svc Service objects was changed to NodePort.
- storageClassName was removed from the volumeClaimTemplates spec of the postgresql StatefulSet object.
- subPath was added to the volumeMounts of the postgresql StatefulSet object.

Here I duplicate the manifests that were changed to support working in google cloud:

[service.yaml](https://github.com/kat-ushka/to-do-project/blob/main/manifests/service.yaml)

```yaml
...
---
apiVersion: v1
kind: Service
metadata:
  namespace: to-do-project
  name: to-do-api-svc
spec:
  type: NodePort
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
  namespace: to-do-project
  name: to-do-web-svc
spec:
  type: NodePort
  selector:
    app: to-do-web # This is the app as declared in the deployment.
  ports: # The following will let TCP traffic from port 2345 to port 8080.
    - port: 2345
      protocol: TCP
      targetPort: 8080
      name: http

```

[persistentvolumeclain.yaml](https://github.com/kat-ushka/to-do-project/blob/main/manifests/persistentvolumeclaim.yaml)

```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  namespace: to-do-project
  name: image-claim
spec:
  storageClassName: standard-rwo
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 10Mi

```

Also, I put the [main.yaml](https://github.com/kat-ushka/to-do-project/blob/main/.github/workflows/main.yaml) file to support the GitHub pipline.
I added several GitHub repo actions secrets to provide the information about GKE project, images names and age key to decrypt the secret.
I duplicate it contains below:

```yaml
name: Release application

on:
  push:

jobs:
  build-publish-deploy:
    name: Build, Publish and Deploy
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v3
      - name: Set up Cloud SDK
        uses: google-github-actions/setup-gcloud@v0
        with:
          project_id: ${{ secrets.GKE_DWK_PROJECT }}
          service_account_key: ${{ secrets.GKE_SA_KEY }}
          export_default_credentials: true
      - run: gcloud --quiet auth configure-docker
      - run: gcloud container clusters get-credentials "$GKE_CLUSTER" --zone "$GKE_ZONE"
      - name: Build
        run: |-
          docker build \
            --file Dockerfile.api \
            --tag "gcr.io/$PROJECT_ID/$TODO_API_IMAGE:${GITHUB_REF#refs/heads/}-$GITHUB_SHA" \
            .
          docker build \
            --file Dockerfile.web \
            --tag "gcr.io/$PROJECT_ID/$TODO_WEB_IMAGE:${GITHUB_REF#refs/heads/}-$GITHUB_SHA" \
            .
          docker build \
            --file Dockerfile.db \
            --tag "gcr.io/$PROJECT_ID/$TODO_DB_IMAGE:${GITHUB_REF#refs/heads/}-$GITHUB_SHA" \
            .
          docker build \
            --file Dockerfile.daily \
            --tag "gcr.io/$PROJECT_ID/$TODO_DAILY_IMAGE:${GITHUB_REF#refs/heads/}-$GITHUB_SHA" \
            .
      - name: Publish
        run: |-
          docker push "gcr.io/$PROJECT_ID/$TODO_API_IMAGE:${GITHUB_REF#refs/heads/}-$GITHUB_SHA"
          docker push "gcr.io/$PROJECT_ID/$TODO_WEB_IMAGE:${GITHUB_REF#refs/heads/}-$GITHUB_SHA"
          docker push "gcr.io/$PROJECT_ID/$TODO_DB_IMAGE:${GITHUB_REF#refs/heads/}-$GITHUB_SHA"
          docker push "gcr.io/$PROJECT_ID/$TODO_DAILY_IMAGE:${GITHUB_REF#refs/heads/}-$GITHUB_SHA"
      - name: Set up Kustomize
        uses: imranismail/setup-kustomize@v1
      - name: Decrypt secret
        run: |-
          curl -O -L -C - https://github.com/mozilla/sops/releases/download/v3.7.3/sops-v3.7.3.linux
          sudo mv sops-v3.7.3.linux /usr/bin/sops
          sudo chmod +x /usr/bin/sops
          export SOPS_AGE_KEY=${{ secrets.GKE_DWK_SOPS_AGE_KEY }}
          sops --decrypt manifests/secret.enc.yaml > manifests/secret.yaml
      - name: Deploy
        run: |-
          kustomize edit set image to-do-api=gcr.io/$PROJECT_ID/$TODO_API_IMAGE:${GITHUB_REF#refs/heads/}-$GITHUB_SHA
          kustomize edit set image to-do-web=gcr.io/$PROJECT_ID/$TODO_WEB_IMAGE:${GITHUB_REF#refs/heads/}-$GITHUB_SHA
          kustomize edit set image to-do-db=gcr.io/$PROJECT_ID/$TODO_DB_IMAGE:${GITHUB_REF#refs/heads/}-$GITHUB_SHA
          kustomize edit set image to-do-daily=gcr.io/$PROJECT_ID/$TODO_DAILY_IMAGE:${GITHUB_REF#refs/heads/}-$GITHUB_SHA
          kustomize build . | kubectl apply -f -
          kubectl rollout status statefulset $TODO_DB_IMAGE -n to-do-project
          kubectl rollout status deployment $TODO_API_IMAGE -n to-do-project
          kubectl rollout status deployment $TODO_WEB_IMAGE -n to-do-project
          kubectl get services -o wide
env:
  PROJECT_ID: ${{ secrets.GKE_DWK_PROJECT }}
  GKE_CLUSTER: dwk-cluster
  GKE_ZONE: europe-north1-c
  TODO_API_IMAGE: to-do-api
  TODO_WEB_IMAGE: to-do-web
  TODO_DB_IMAGE: to-do-db
  TODO_DAILY_IMAGE: to-do-daily

```

## How to perform required flow

Follow the [README](https://github.com/kat-ushka/to-do-project/blob/main/README.md) in the project's repo.
