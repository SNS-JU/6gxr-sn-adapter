# South Node Adapter

## Description
This component is the bridge between 6G-XR's South Web Portal and underlying backend architectures, as well as tracking KPIs of running instances. It also handles user credentials of the South Web Portal for login and register.

## Integrate with your tools

Can be used in any IDE that supports Java and Git.

## Installation
MongoDB can be installed locally and be pointed to if needed in application.yml

Needs to setup a .env file inside the root directory of this project containing value for client_secret, ask CGE team for details.

`IEAP_CLIENT_SECRET={secret}`

## Usage
Run main class SouthNodeAdapterApplication as Java Application.

## Test and Deploy

MongoDB current Docker deployment: 
docker run -d --name mongodb-6gxr --network sn-adapter-6g-xr -p 30018:27017 -v /mnt/6gxr/data/db:/data/db mongo:latest

To deploy a new version via Docker, follow these steps:
- Update bootJar task in build.gradle with the new version.
- Run bootJar task.
- Update Dockerfile COPY command jar's version to the new one that has been compiled to bootJar.
- Inside Docker Desktop, go to the root folder of the project and perform "docker build --tag=sn-adapter:<version> ."
- Once built succesfully, do "docker save -o <your-path>\sn-adapter-<version>.tar snw-webportal:<version>"
- Upload docker image to remote machine via SSH (currently using "ssh root@10.15.126.160" on /6g-xr main folder with password abc123)
- docker load < <new-version>.tar
- docker ps
- docker stop <previous-running-version-container-id>
- docker run -d -e IEAP_CLIENT_SECRET=<secret> --name sn-adapter --network sn-adapter-6g-xr -p 30081:8080 sn-adapter:<new-version>
- In case mongoDB is not running: docker run -d --network sn-adapter-6g-xr -p 30018:27017 -v /mnt/6gxr/data/db:/data/db mongo

##Debugging IEAP deployments

ssh root@10.15.125.12 'tail -f /opt/aricent/ec/orchestrator/logs/orchestrator.log' | tee C:\Users\micro\Downloads\IEAPOrchestrator.log

## Contributing

- Create an issue in Gitlab
- Create a branch with the same issue code
- Do the merge request to develop 