package ch.icclab.sentinel;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;

/**
 * Created by piyush on 5/3/17.
 */
public class KafkaTestProducer {
    //change send to main if you wish to have this as a standalone sender application
    public static void send(String[] args)
    {
        Properties props = new Properties();
        props.put("bootstrap.servers", "kafka.demonstrator.info:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);
        TestCallback callback = new TestCallback();
        for(int i = 0; i < 100; i++)
            producer.send(new ProducerRecord<String, String>("testing2", "udr", Integer.toString(i)), callback);

        producer.close();
    }


    private static class TestCallback implements Callback {
        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            if (e != null) {
                System.out.format("Error while producing message to topic : {}", recordMetadata);
                e.printStackTrace();
            } else {
                String message = String.format("Sent message to topic:%s partition:%s  offset:%s", recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset());
                System.out.println(message);
            }
        }
    }
}
