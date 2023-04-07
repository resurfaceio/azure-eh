#Contributing

## Run Containers Locally

Clone this repository to run the containers as an on-prem solution.
You will need to [install `docker-compose`](https://docs.docker.com/compose/install/) in addition to the requirements listed above.

```bash
git clone https://github.com/resurfaceio/azure-eh.git
cd azure-eh
make start
```

Additional commands:

```bash
make start     # rebuild and start containers
make bash      # open shell session
make logs      # follow container logs
make stop      # halt and remove containers
```

## Run Containers as Azure Container Instances

Click down below to deploy both containers as Azure Container Instances and run them as a cloud-based solution

[![Deploy to Azure](https://aka.ms/deploytoazurebutton)](https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2Fresurfaceio%2Fiac-templates%2Fmaster%2Fazure%2Fcontainer-group.json)
