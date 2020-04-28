
# Iam-manager & Docker

This application is fully ready to be used with docker. You can find all the files in `src/main/docker`

## Deploy

### Dev environment

If you want to write code in your local machine and test with `mvn quarkus:dev` you can execute: 

```bash
# In the base dir:
docker-compose up -d
```

### Prod environment from code

If you want to containerize your application and test it in a docker network you can execute:

* For the `native` image:

  ```bash
  # In the base dir:
  docker-compose -f docker-compose.yml -f docker-conf/overrides/build-prod-native.yml up -d
  ```

* For the `jvm` image:

  ```bash
  # In the base dir:
  docker-compose -f docker-compose.yml -f docker-conf/overrides/build-prod-jvm.yml up -d
  ```

All configuration files to be used by the containers should be placed in `./docker-conf`:

*  `./docker-conf/keycloak/quickstart-realm.json` &rarr; contains a testing realm for keycloak.
* `./docker-conf/iam-manager/application.properties` &rarr; Quarkus configuration file customized to be used in a contenerized environment.

## Upload

1. Generate the artifact:

   * For the native image:

     ```bash
     # In the base of the project
     mvn package -Pnative -Dquarkus.native.container-runtime=docker
     ```

   * For the JVM image:

     ```bash
     # In the base of the project
     mvn package
     ```

2. Generate the docker images:

   * For the native image:

     ```bash
     # In the base of the project
     docker build -f src/main/docker/Dockerfile.native -t iam_manager:native . 
     ```

   * For the JVM image:

     ```bash
     # In the base of the project
     docker build -f src/main/docker/Dockerfile.jvm -t iam_manager:jvm . 
     ```

3. Retag the docker images for the repository:

   * For the native image:

     ```bash
     # For a private docker registry:
     # docker tag iam_manager:jvm <registry_url>/<your_organization>/iam_manager:native
     
     # For the docker hub:
     docker tag iam_manager:native rpardom/iam_manager:native
     ```

   * For the JVM machine:

     ```bash
     # For a private docker registry:
     # docker tag iam_manager:jvm <registry_url>/<your_organization>/iam_manager:jvm
     
     # For the docker hub:
     docker tag iam_manager:jvm rpardom/iam_manager:jvm
     ```

4. Push the tagged images:

   ```bash
   # For a private docker registry:
   # docker login <registry_url>
   # docker push <registry_url>/<your_organization>/iam_manager
   
   # For the docker hub:
   docker login
   docker push rpardom/iam_manager
   ```