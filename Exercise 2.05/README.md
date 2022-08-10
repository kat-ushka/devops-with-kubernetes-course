# Exercise 2.06: Documentation and ConfigMaps

# Exercise realization description

Just followed a SOPS demo.
Put scripts here to keep on hand.

# How to perform required flow

To perform exercise flow I did next steps:

1. Opened shell and moved to this folder. 
2. Installed sops and age with the brew:
    ```shell
    brew install sops
    brew install age
    ```
3. Created age key with a script:
    ```shell
    age-keygen -o key.txt
    ```
4. Created example secret.yaml in manifests folder.
5. Encrypted it with the script:
    ```shell
    sops --encrypt \
         --age <public_key from the previous step> \
         --encrypted-regex '^(data)$' \
         manifests/secret.yaml > manifests/secret.enc.yaml
    ```
6. Exported the key file in SOPS_AGE_KEY_FILE wnv var with the script:
    ```shell
    export SOPS_AGE_KEY_FILE=$(pwd)/key.txt
    ```
7. Decrypted secret.enc.yaml with the script:
    ```shell
    sops --decrypt manifests/secret.enc.yaml > manifests/secret2.yaml
    ```
