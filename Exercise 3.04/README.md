# Exercise 3.04: Project v1.4.1

<!-- TOC -->
* [Exercise description](#exercise-description)
* [Exercise realization description](#exercise-realization-description)
* [How to perform required flow](#how-to-perform-required-flow)
<!-- TOC -->

## Exercise description

Improve the deployment so that each branch creates its own environment.

## Exercise realization description

No manifests or significant code changes were made for this exercise.

The [main.yaml](https://github.com/kat-ushka/to-do-project/blob/main/.github/workflows/main.yaml) file was slightly changed.
Hardcoded namespace was deleted.

I duplicate it contains below:

```yaml
name: Release application

env:
  PROJECT_ID: ${{ secrets.GKE_DWK_PROJECT }}
  GKE_CLUSTER: dwk-cluster
  GKE_ZONE: europe-north1-c
  TODO_API_IMAGE: to-do-api
  TODO_WEB_IMAGE: to-do-web
  TODO_DB_IMAGE: to-do-db
  TODO_DAILY_IMAGE: to-do-daily

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
          kubectl create namespace ${GITHUB_REF#refs/heads/} || true
          kubectl config set-context --current --namespace=${GITHUB_REF#refs/heads/}
          kustomize edit set namespace ${GITHUB_REF#refs/heads/}
          kustomize edit set image to-do-api=gcr.io/$PROJECT_ID/$TODO_API_IMAGE:${GITHUB_REF#refs/heads/}-$GITHUB_SHA
          kustomize edit set image to-do-web=gcr.io/$PROJECT_ID/$TODO_WEB_IMAGE:${GITHUB_REF#refs/heads/}-$GITHUB_SHA
          kustomize edit set image to-do-db=gcr.io/$PROJECT_ID/$TODO_DB_IMAGE:${GITHUB_REF#refs/heads/}-$GITHUB_SHA
          kustomize edit set image to-do-daily=gcr.io/$PROJECT_ID/$TODO_DAILY_IMAGE:${GITHUB_REF#refs/heads/}-$GITHUB_SHA
          kustomize build . | kubectl apply -f -
          kubectl rollout status statefulset $TODO_DB_IMAGE
          kubectl rollout status deployment $TODO_API_IMAGE
          kubectl rollout status deployment $TODO_WEB_IMAGE
          kubectl get services -o wide

```

## How to perform required flow

1. Follow the [README](https://github.com/kat-ushka/to-do-project/blob/main/README.md) in the project's repo to prepare for deploying the application.
2. Create a new branch and push some small change to it.
3. Check the Actions tab in GitHub repo to see a new job started.
4. Check GCP to see workloads and services created in a new namespace.

