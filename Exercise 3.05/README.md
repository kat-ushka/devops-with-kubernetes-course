# Exercise 3.05: Project v1.4.2

<!-- TOC -->
* [Exercise description](#exercise-description)
* [Exercise realization description](#exercise-realization-description)
* [How to perform required flow](#how-to-perform-required-flow)
<!-- TOC -->

## Exercise description

Finally, create a new workflow so that deleting a branch deletes the environment.

## Exercise realization description

No manifests or significant code changes were made for this exercise.

The [delete.yaml](https://github.com/kat-ushka/to-do-project/blob/main/.github/workflows/delete.yaml) file was created to support a required new workflow.

I duplicate it contains below:

```yaml
name: Delete application

on:
  delete:

jobs:
  delete-namespace:
    name: Delete namespace
    runs-on: ubuntu-latest
    steps:
      - name: Set up Cloud SDK
        uses: google-github-actions/setup-gcloud@v0
        with:
          project_id: ${{ secrets.GKE_DWK_PROJECT }}
          service_account_key: ${{ secrets.GKE_SA_KEY }}
          export_default_credentials: true
      - run: gcloud --quiet auth configure-docker
      - run: gcloud container clusters get-credentials "$GKE_CLUSTER" --zone "$GKE_ZONE"
      - name: Delete namespace
        run: |-
          kubectl delete namespace ${{ github.event.ref }} || true

env:
  GKE_CLUSTER: dwk-cluster
  GKE_ZONE: europe-north1-c

```

## How to perform required flow

1. Follow the [README](https://github.com/kat-ushka/to-do-project/blob/main/README.md) in the project's repo to prepare for deploying the application.
2. Create a new branch and push some small change to it.
3. Check the Actions tab in GitHub repo to see a new job started.
4. Check GCP to see workloads and services created in a new namespace.
5. Delete remote branch
6. Check the Actions tab in GitHub repo to see a new job started.
7. Check GCP to see all workloads and services in the branch namespace were deleted
