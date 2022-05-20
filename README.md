# resurfaceio-azure-eh
Easily log API requests and responses to your own [system of record](https://resurface.io/).

## Requirements

* docker
* an Azure subscription might be required in order to use Event Hubs, Storage Blobs, and Azure API Management

## Ports Used

* 7700 - Resurface API Explorer & Trino database UI
* 7701 - Resurface microservice

## Set Up
In order to run Resurface for Azure APIM, some previous configuration is needed. Specifically, four resources need to be created and deployed: an [Event Hub](https://docs.microsoft.com/en-us/azure/event-hubs/event-hubs-about), a [Storage Account](https://docs.microsoft.com/en-us/azure/storage/common/storage-account-overview), a [Logger](https://docs.microsoft.com/en-us/rest/api/apimanagement/current-ga/logger/create-or-update), and a [Policy](https://docs.microsoft.com/en-us/azure/api-management/set-edit-policies) for your APIM instance.

### Automatic deployment
Click the **Deploy to Azure** button below to deploy all necessary resources using an [ARM template](https://docs.microsoft.com/en-us/azure/azure-resource-manager/templates/overview):

[![Deploy to Azure](https://aka.ms/deploytoazurebutton)](https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2Fresurfaceio%2Fiac-templates%2Fmaster%2Fazure%2Fazuredeployresources.json)

This uses [a custom template](https://github.com/resurfaceio/iac-templates/blob/master/azure/azuredeployresources.json) to create and deploy an [Event Hubs instance](https://github.com/resurfaceio/iac-templates/blob/master/azure/event-hub.json), a [Storage Account](https://github.com/resurfaceio/iac-templates/blob/master/azure/storageaccount.json), and adds a [Logger and Policy](https://github.com/resurfaceio/iac-templates/blob/master/azure/logger-and-policy.json) to your existing APIM instance.

### Manual setup

If you would like to configure everything yourself using the Azure console instead, just follow Resurface's [Capturing from APIM get-started guide](https://resurface.io/azure-get-started#manual-setup), where the entire process is documented in a step-by-step manner.

<a name="logging_from_azure_event_hubs"/>

## Streaming data From Azure Event Hubs to Resurface

- Set following the environment variables in your `.env` file:

| Variable | Set to |
|:---------|:-------|
|`AZURE_EH_CONNECTION_STRING`|[Connection string for a specific Azure Event Hubs namespace](https://docs.microsoft.com/en-us/azure/event-hubs/event-hubs-get-connection-string)|
|`EVENT_HUB_NAME`            |Name of your Event Hub instance|
|`AZURE_STORAGE_CONNECTION_STRING`|[Connection string for a specific Azure Storage Account]([https://docs.microsoft.com/en-us/azure/event-hubs/event-hubs-get-connection-string](https://docs.microsoft.com/en-us/azure/storage/common/storage-configure-connection-string))|
|`STORAGE_CONTAINER_NAME`    |Name of your storage container|
|`USAGE_LOGGERS_URL`         |DB capture endpoint for your [Resurface instance](https://resurface.io/installation)|
|`USAGE_LOGGERS_RULES`       |(**Optional**) Set of [rules](#protecting-user-privacy).<br />Only necessary if you want to exclude certain API calls from being logged.|
|`EVENT_HUB_CONSUMER_GROUP`  |(**Optional**) Name of a consumer group from your Event Hub.<br />Only necessary if you have created a specific consumer group for your Event Hub instance|
|`PARTITION_NUMBER`          |(**Deprecated**) Partition number configured in `policy.xml`. Should be `"0"` by default.|

- (Optional) Build the container image

```bash
docker build -t listener-azure-eh:1.0.0 .
```

- Run the container

```bash
docker run -d --name resurface-azure-eh --env-file .env resurfaceio/listener-azure-eh:1.0.0
```

Or, if you built the image yourself in the previous step:

```bash
docker run -d --name resurface-azure-eh --env-file .env listener-azure-eh:1.0.0
```

- Use your API as you always do. Go to the [API Explorer](https://resurface.io/docs#api-explorer) of your Resurface instance and verify that the API Calls are being captured.
- That's it!

## Deploy Containers Locally

Clone this repository to run the containers as an on-prem solution.
You will need to [install `docker-compose`](https://docs.docker.com/compose/install/) in addition to the requirements above.

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

## Deploy Containers to Azure Container Instances

Click down below to deploy both containers as Azure Container Instances and run them as a cloud-based solution

[![Deploy to Azure](https://aka.ms/deploytoazurebutton)](https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2Fresurfaceio%2Fiac-templates%2Fmaster%2Fazure%2Fcontainer-group.json)

## Deploy Containers to Azure Kubernetes Service (AKS)

// Coming soon!

<a name="privacy"/>

## Protecting User Privacy

Loggers always have an active set of <a href="https://resurface.io/rules.html">rules</a> that control what data is logged
and how sensitive data is masked. All of the examples above apply a predefined set of rules (`include debug`),
but logging rules are easily customized to meet the needs of any application.

<a href="https://resurface.io/rules.html">Logging rules documentation</a>

---
<small>&copy; 2016-2021 <a href="https://resurface.io">Resurface Labs Inc.</a></small>
