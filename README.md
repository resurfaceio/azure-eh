# resurfaceio-logger-azure-eh
Example Resurface logger for Azure Event Hubs

## Requirements

* docker
* docker-compose
* an Azure subscription might be required in order to use Azure Event Hubs

## Ports Used

* 4002 - Resurface API Explorer
* 4001 - Resurface microservice
* 4000 - Trino database UI

## Deploy Locally

```
make start     # rebuild and start containers
make bash      # open shell session
make logs      # follow container logs
make stop      # halt and remove containers
```

<a name="logging_from_azure_event_hubs"/>

## Logging From Azure Event Hubs

- Add the Policy expression [`policy.xml`](https://github.com/resurfaceio/logger-azure-eh/blob/master/policy.xml) to your API Management Service as indicated [here](https://docs.microsoft.com/en-us/azure/api-management/set-edit-policies). Remember to modify the attributes mentioned in the comment block of [`policy.xml`](https://github.com/resurfaceio/logger-azure-eh/blob/master/policy.xml).

- Set following the environment variables in the [`.env`](https://github.com/resurfaceio/test-azure-eh/blob/master/.env) file to their corresponding values:

| Variable                   | Set to                                                                                                                                          |
|:---------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------|
|`AZURE_EH_CONNECTION_STRING`|[Connection string for a specific Azure Event Hubs namespace](https://docs.microsoft.com/en-us/azure/event-hubs/event-hubs-get-connection-string)|
|`PARTITION_NUMBER`          |Partition number configured in `policy.xml`. Should be `"0"` by default                                                                          |
|`EVENT_HUB_NAME`            |Name of your Event Hub instance                                                                                                                  |
|`EVENT_HUB_CONSUMER_GROUP`  |(**Optional**) Name of a consumer group from your Event Hub.<br />Only necessary if you have created a specific consumer group for your Event Hub instance                                                    |
|`USAGE_LOGGERS_URL`          |(**Optional**) Resurface database connection URL.<br />Only necessary if your [Resurface instance](https://resurface.io/pilot-installation) uses a different connection URL than the one provided by default   |
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