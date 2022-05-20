package EventHubProcessorClient;

import Logger.HttpLoggerForAzureEH;
import com.azure.messaging.eventhubs.*;
import com.azure.messaging.eventhubs.checkpointstore.blob.BlobCheckpointStore;
import com.azure.storage.blob.*;

public class EventProcessor {
    //Azure
    private static final String connectionString = System.getenv("AZURE_EH_CONNECTION_STRING");
    private static final String eventHubName = System.getenv("EVENT_HUB_NAME");
    private static final String consumerGroup = System.getenv("EVENT_HUB_CONSUMER_GROUP");
    private static final String storageConnectionString = System.getenv("AZURE_STORAGE_CONNECTION_STRING");
    private static final String storageContainerName = System.getenv("STORAGE_CONTAINER_NAME");
    // Resurface
    private static final String loggerURL = System.getenv("USAGE_LOGGERS_URL");
    private static final String loggerRules = System.getenv("USAGE_LOGGERS_RULES");
    private static final Boolean loggerEnabled = !"true".equals(System.getenv("USAGE_LOGGERS_DISABLED"));

    public static void main(String[] args) {
        BlobContainerAsyncClient blobContainerAsyncClient = new BlobContainerClientBuilder()
                .connectionString(storageConnectionString)
                .containerName(storageContainerName)
                .buildAsyncClient();

        HttpLoggerForAzureEH msgProcessor = new HttpLoggerForAzureEH(loggerURL, loggerEnabled, loggerRules);
        System.out.printf("Resurface logger enabled: %b%n", msgProcessor.isEnabled());
        EventProcessorClient eventProcessorClient = new EventProcessorClientBuilder()
                .connectionString(connectionString, eventHubName)
                .consumerGroup(consumerGroup == null ? EventHubClientBuilder.DEFAULT_CONSUMER_GROUP_NAME : consumerGroup)
                .processEvent(eventContext -> {
                    msgProcessor.send(eventContext.getEventData().getBody());
                    if (eventContext.getEventData().getSequenceNumber() % 10 == 0) {
                        eventContext.updateCheckpoint();
                    }
                })
                .processError(errorContext -> System.out.printf(
                        "Error occurred in partition processor for partition %s, %s.%n",
                        errorContext.getPartitionContext().getPartitionId(),
                        errorContext.getThrowable())
                )
                .checkpointStore(new BlobCheckpointStore(blobContainerAsyncClient))
                .buildEventProcessorClient();

        eventProcessorClient.start();
    }
}
