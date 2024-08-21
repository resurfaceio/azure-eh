// © 2016-2024 Graylog, Inc.

package EventHubProcessorClient;

import Logger.HttpLoggerForAzureEH;
import com.azure.core.util.logging.ClientLogger;
import com.azure.messaging.eventhubs.*;
import com.azure.messaging.eventhubs.checkpointstore.blob.BlobCheckpointStore;
import com.azure.storage.blob.*;

import static org.slf4j.simple.SimpleLogger.DEFAULT_LOG_LEVEL_KEY;

public class EventProcessor {
    //Azure
    private static final String connectionString = System.getenv("AZURE_EH_CONNECTION_STRING");
    private static final String eventHubName = System.getenv("EVENT_HUB_NAME");
    private static final String consumerGroup = System.getenv("EVENT_HUB_CONSUMER_GROUP");
    private static final String storageConnectionString = System.getenv("AZURE_STORAGE_CONNECTION_STRING");
    private static final String storageContainerName = System.getenv("STORAGE_CONTAINER_NAME");
    private static final int checkpointFrequency = System.getenv("CHECKPOINT_FREQ") == null ? 10 :
            Integer.parseInt(System.getenv("CHECKPOINT_FREQ"));
    private static final String logLevel = System.getenv("DEFAULT_LOG_LEVEL");
    // Resurface
    private static final String loggerURL = System.getenv("USAGE_LOGGERS_URL");
    private static final String loggerRules = System.getenv("USAGE_LOGGERS_RULES");
    private static final Boolean loggerEnabled = !"true".equals(System.getenv("USAGE_LOGGERS_DISABLED"));

    public static void main(String[] args) {
        System.setProperty(DEFAULT_LOG_LEVEL_KEY, logLevel == null ? "info" : logLevel);
        final ClientLogger logger = new ClientLogger(EventProcessor.class.getName());

        BlobContainerAsyncClient blobContainerAsyncClient = new BlobContainerClientBuilder()
                .connectionString(storageConnectionString)
                .containerName(storageContainerName)
                .buildAsyncClient();

        HttpLoggerForAzureEH msgProcessor = new HttpLoggerForAzureEH(loggerURL, loggerEnabled, loggerRules);
        logger.info("Resurface logger enabled: {}", msgProcessor.isEnabled());
        EventProcessorClient eventProcessorClient = new EventProcessorClientBuilder()
                .connectionString(connectionString, eventHubName)
                .consumerGroup(consumerGroup == null ? EventHubClientBuilder.DEFAULT_CONSUMER_GROUP_NAME : consumerGroup)
                .processEvent(eventContext -> {
                    msgProcessor.send(eventContext.getEventData().getBody(), logger::verbose, logger::error);
                    if (eventContext.getEventData().getSequenceNumber() % checkpointFrequency == 0) {
                        eventContext.updateCheckpoint();
                    }
                })
                .processError(errorContext -> logger.error(
                        "Error occurred in partition processor for partition {}, {}}.",
                        errorContext.getPartitionContext().getPartitionId(),
                        errorContext.getThrowable())
                )
                .checkpointStore(new BlobCheckpointStore(blobContainerAsyncClient))
                .buildEventProcessorClient();

        eventProcessorClient.start();
    }
}
