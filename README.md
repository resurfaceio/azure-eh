# resurfaceio-azure-eh
Easily log API requests and responses to your own [system of record](https://resurface.io/).

## Requirements

* docker
* docker-compose
* an Azure subscription might be required in order to use Azure Event Hubs and Azure API Management

## Ports Used

* 7700 - Resurface API Explorer & Trino database UI
* 7701 - Resurface microservice

## Deploy to Azure

Click down below to deploy both containers as Azure Container Instances and run them as a cloud-based solution

[![Deploy to Azure](https://aka.ms/deploytoazurebutton)](https://portal.azure.com/#create/Microsoft.Template/uri/https%3A%2F%2Fraw.githubusercontent.com%2Fresurfaceio%2Fazure-eh%2Fv3%2Fazure%2Farm-templates%2Fcontainer-group.json)

## Deploy Locally

Clone this repository to run the containers as an on-prem solution

```
make start     # rebuild and start containers
make bash      # open shell session
make logs      # follow container logs
make stop      # halt and remove containers
```

<a name="logging_from_azure_event_hubs"/>

## Logging From Azure Event Hubs

- Add the Policy expression [`policy.xml`](https://github.com/resurfaceio/listener-azure-eh/blob/master/policy.xml) to your API Management Service as indicated [here](https://docs.microsoft.com/en-us/azure/api-management/set-edit-policies). Remember to modify the attributes mentioned in the comment block of [`policy.xml`](https://github.com/resurfaceio/listener-azure-eh/blob/master/policy.xml).

- If you are running the containers locally, you need to set following the environment variables in the [`.env`](https://github.com/resurfaceio/azure-eh/blob/master/.env) file to their corresponding values before doing `make start`:

| Variable                   | Set to                                                                                                                                          |
|:---------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------|
|`AZURE_EH_CONNECTION_STRING`|[Connection string for a specific Azure Event Hubs namespace](https://docs.microsoft.com/en-us/azure/event-hubs/event-hubs-get-connection-string)|
|`PARTITION_NUMBER`          |Partition number configured in `policy.xml`. Should be `"0"` by default                                                                          |
|`EVENT_HUB_NAME`            |Name of your Event Hub instance                                                                                                                  |
|`EVENT_HUB_CONSUMER_GROUP`  |(**Optional**) Name of a consumer group from your Event Hub.<br />Only necessary if you have created a specific consumer group for your Event Hub instance                                                    |
|`USAGE_LOGGERS_URL`          |(**Optional**) Resurface database connection URL.<br />Only necessary if your [Resurface instance](https://resurface.io/installation) uses a different connection URL than the one provided by default   |
|`USAGE_LOGGERS_RULES`        |(**Optional**) Set of [rules](#protecting-user-privacy).<br />Only necessary if you want to exclude certain API calls from being logged.         |

- Use your API as you always do. Enjoy! 

<a name="privacy"/>

## Protecting User Privacy

Loggers always have an active set of <a href="https://resurface.io/rules.html">rules</a> that control what data is logged
and how sensitive data is masked. All of the examples above apply a predefined set of rules (`include debug`),
but logging rules are easily customized to meet the needs of any application.

<a href="https://resurface.io/rules.html">Logging rules documentation</a>

---
<small>&copy; 2016-2021 <a href="https://resurface.io">Resurface Labs Inc.</a></small>
