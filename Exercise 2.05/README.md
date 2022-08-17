# Exercise 2.05: Secrets

<!-- TOC -->
* [Exercise description](#exercise-description)
* [Exercise realization description](#exercise-realization-description)
* [How to perform required flow](#how-to-perform-required-flow)
<!-- TOC -->

## Exercise description

In all future exercises if you are using an API key or a password, such as a database password, you will use Secrets. 
You can use SOPS to store it to a git repository. Never save unencrypted files into a git repository.

There's nothing specific to submit, all following submissions should follow the rule above.

## Exercise realization description

Just followed a SOPS demo.
Put scripts here to keep on hand.

## How to perform required flow

To perform exercise do the next steps:

1. Open shell and move to this folder. 
2. Install sops and age with the brew (check the other options [here for sops](https://github.com/mozilla/sops) and [here for age](https://github.com/FiloSottile/age)):
    ```shell
    brew install sops
    brew install age
    ```
3. Create age key with a script:
    ```shell
    age-keygen -o key.txt
    ```
4. See the example secret.yaml in manifests folder.
5. Encrypt it with the script:
    ```shell
    sops --encrypt \
         --age <public_key from the step 3> \
         --encrypted-regex '^(data)$' \
         manifests/secret.yaml > manifests/secret.enc.yaml
    ```
6. Export the key file in SOPS_AGE_KEY_FILE env var with the script:
    ```shell
    export SOPS_AGE_KEY_FILE=$(pwd)/key.txt
    ```
7. Decrypt `manifests/secret.enc.yaml` with the script:
    ```shell
    sops --decrypt manifests/secret.enc.yaml > manifests/secret2.yaml
    ```
8. Check the resulted manifests/secret2.yaml