# Log Output application

## Brief description

Application *timestamp-generator* is a simple executable jar java application.
It generates a new timestamp every 5 seconds and saves it into a file.
A filepath can be set with env variable `$TIME_STAMP_FILEPATH`

## How to create docker image

An image can be created with a docker command:  
```shell
docker image build . 
```
