// © 2016-2024 Graylog, Inc.

package EventHubConsumerClient;

import Logger.HttpLoggerForAzureEH;
import com.azure.messaging.eventhubs.*;
import com.azure.messaging.eventhubs.models.EventPosition;


public class EventConsumer {
    // Azure
    private static final String connectionString = System.getenv("AZURE_EH_CONNECTION_STRING");
    private static final String eventHubName = System.getenv("EVENT_HUB_NAME");
    private static final String consumerGroup = System.getenv("EVENT_HUB_CONSUMER_GROUP");
    private static final String partitionNumber = System.getenv("PARTITION_NUMBER");
    // Resurface
    private static final String loggerURL = System.getenv("USAGE_LOGGERS_URL");
    private static final String loggerRules = System.getenv("USAGE_LOGGERS_RULES");
    private static final Boolean loggerEnabled = !"true".equals(System.getenv("USAGE_LOGGERS_DISABLED"));


    public static void main(String[] args) {
        EventHubConsumerAsyncClient consumer = new EventHubClientBuilder()
                .connectionString(connectionString, eventHubName)
                .consumerGroup(consumerGroup == null ? EventHubClientBuilder.DEFAULT_CONSUMER_GROUP_NAME : consumerGroup)
                .buildAsyncConsumerClient();

        HttpLoggerForAzureEH msgProcessor = new HttpLoggerForAzureEH(loggerURL, loggerEnabled, loggerRules);
        System.out.printf("Resurface logger enabled: %b%n", msgProcessor.isEnabled());
        consumer.receiveFromPartition(partitionNumber, EventPosition.latest())
                .subscribe(event -> {
                    msgProcessor.send(event.getData().getBody());
                }, error -> System.err.print(error.toString()));
    }
}
