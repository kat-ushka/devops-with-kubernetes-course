# Exercise 2.10: Project v1.3

<!-- TOC -->
* [Exercise description](#exercise-description)
* [Exercise realization description](#exercise-realization-description)
* [How to perform required flow](#how-to-perform-required-flow)
  * [Docker images](#docker-images)
  * [Performing exercise-to-exercise flow](#performing-exercise-to-exercise-flow)
  * [How to do from the scratch](#how-to-do-from-the-scratch)
<!-- TOC -->

## Exercise description

The project could really use logging.

Add request logging so that you can monitor every todo that is sent to the backend.

Set the limit of 140 characters for todos into the backend as well. 

Use Postman or curl to test that too long todos are blocked by the backend and you can see the non-allowed messages in your grafana.

## Exercise realization description

To implement this exercise I added some logs and a length restriction to the to-do-api module.

No significant changes to manifests were made.

## How to perform required flow

### Docker images

Docker images can be found here:
- docker pull katushka/to-do-daily:1.0
- docker pull katushka/to-do-db:1.0
- docker pull katushka/to-do-api:1.2
- docker pull katushka/to-do-web:1.1

### Performing exercise-to-exercise flow

1. Open shell and checkout tag Exercise_2.10:
    ```shell
    git fetch --all --tags
    git checkout tags/Exercise_2.10
    ```
2. Move to the folder of this exercise:
    ```shell
    cd Exercise\ 2.10
    ```
3. Create docker images by running docker-compose with script:
    ```shell
    docker-compose build
    ```
4. Push a new docker images to Docker Hub with a script:
    ```shell
    docker image push katushka/to-do-api:1.2
    docker image push katushka/to-do-web:1.2
    ```
5. Apply manifests:
    ```shell
    kubectl apply -f manifests
    ```
6. Try to create too long TODO executing curl from the to-do-web-dep pod:
    ```shell
    TODO_TEXT="This is a very long todo text. This is a very long todo text. This is a very long todo text. This is a very long todo text. This is a very long todo text."
    curl -d "$TODO_TEXT" -H "Content-Type: application/json" -s -v -X POST http://to-do-api-svc:2345/to-do-api/api/todos
    ```
7. Check the to-do-api-dep application logs using Grafana and Loki

### How to do from the scratch

Assuming you have, k3d, kubectl, Lens, age, and sops already installed.

1. Open shell and checkout tag Exercise_2.10:
    ```shell
    git fetch --all --tags
    git checkout tags/Exercise_2.10
    ```
2. Perform the flow of Exercise_2.09 as it is described in [README.md](../Exercise%202.09/README.md) except the very first step.
3. Move to the folder of this exercise:
    ```shell
    cd ../Exercise\ 2.10
    ```
4. Edit docker image label in [docker-compose.yaml](docker-compose.yaml) and create it by running docker-compose with script:
    ```shell
    docker-compose build
    ```
5. Push a new docker image to Docker Hub with a script (remember to change label to the same that was chosen on the previous step):
    ```shell
    docker image push <your docker account>/to-do-api:1.2
    docker image push <your docker account>/to-do-web:1.2
    ```
6. Apply manifests:
    ```shell
    kubectl apply -f manifests
    ```
7. Try to create too long TODO with curl:
    ```shell
    TODO_TEXT="This is a very long todo text. This is a very long todo text. This is a very long todo text. This is a very long todo text. This is a very long todo text."
    curl -d "$TODO_TEXT" -H "Content-Type: application/json" -s -X POST http://to-do-api-svc:2345/to-do-api/api/todos
    ```
8. Check the to-do-api application logs using Grafana and Loki 
