# Exercise 2.08: Project v1.2

<!-- TOC -->
* [Exercise realization description](#exercise-realization-description)
* [How to perform required flow](#how-to-perform-required-flow)
  * [Docker images](#docker-images)
  * [Performing exercise-to-exercise flow](#performing-exercise-to-exercise-flow)
  * [How to do from the scratch](#how-to-do-from-the-scratch)
<!-- TOC -->

## Exercise realization description

To implement this exercise I created a to-do-daily module in to-do-project.
It contains only a shell script that uses `curl` to request random wikipedia page and to post a new TODO with to-do-api application.

A cronjob object was added to kubernetes manifests folder as follows:

[cronjob.yaml](./manifests/7.cronjob.yaml)
```yaml
---
apiVersion: batch/v1
kind: CronJob
metadata:
  namespace: to-do-project
  name: to-do-daily
spec:
  schedule: "0 6 * * *" # daily at 6 A.M.
  jobTemplate:
    spec:
      ttlSecondsAfterFinished: 60 # the job and dependant objects will be deleted 60 seconds after completion
      template:
        spec:
          containers:
            - name: daily
              image: katushka/to-do-daily:1.0
              imagePullPolicy: Always
              env:
                - name: TODO_API_URL
                  value: "http://to-do-api-svc:2345/to-do-api/api/todos"
          restartPolicy: Never

```
## How to perform required flow

### Docker images

Docker images can be found here:
- docker pull katushka/to-do-daily:1.0
- docker pull katushka/to-do-db:1.0
- docker pull katushka/to-do-api:1.1
- docker pull katushka/to-do-web:1.1

### Performing exercise-to-exercise flow
To perform exercise flow I did next steps:

1. Opened shell and checkout tag Exercise_2.09:
    ```shell
    git fetch --all --tags
    git checkout tags/Exercise_2.09
    ```
2. Moved to the folder of this exercise:
    ```shell
    cd Exercise\ 2.09
    ```
3. Created docker images by running docker-compose with script:
    ```shell
    docker-compose build daily
    ```
4. Pushed a new docker images to Docker Hub with a script:
    ```shell
    docker image push katushka/to-do-daily:1.0
    ```
5. Applied manifests:
    ```shell
    kubectl apply -f manifests
    ```
6. Triggered a new CronJob object using Lens application
7. After the job was completed opened http://localhost:8081/to-do and saw a new TODO.
8. After a minute checked that job object and dependant pod were deleted automatically.

### How to do from the scratch

Assuming you have, k3d, kubectl, Lens, age, and sops already installed.

1. Open shell and checkout tag Exercise_2.09:
    ```shell
    git fetch --all --tags
    git checkout tags/Exercise_2.09
    ```
2. Perform the flow of Exercise_2.09 as it is described in [README.md](../Exercise%202.08/README.md) except the very first step.
3. Move to the folder of this exercise:
    ```shell
    cd ../Exercise\ 2.09
    ```
4. Edit docker image label in [docker-compose.yaml](docker-compose.yaml) and created it by running docker-compose with script:
    ```shell
    docker-compose build
    ```
5. Push a new docker image to Docker Hub with a script (remember to change label to the same that was chosen on the previous step):
    ```shell
    docker image push <your docker account>/to-do-daily:1.0
    ```
6. Apply manifests:
    ```shell
    kubectl apply -f manifests
    ```
7. Trigger a new CronJob object using Lens application.
8. After the job is completed open http://localhost:8081/to-do to see a new TODO.
9. After a minute check that job object and dependant pod were deleted automatically.
