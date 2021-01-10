import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StreamProducer {
    static int key = 0;
    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "client-producer-1");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        var random = new Random();
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
            ()->{
                var value = String.valueOf(random.nextDouble()*999999);
                producer.send(
                    new ProducerRecord<>("test1", String.valueOf(key), value),
                    (metadata, ex)->{
                        System.out.println("Sending Message key=>"+key+" Value =>"+value);
                        //System.out.println("Partition => "+metadata.partition()+" Offset=>"+metadata.offset());
                    }
                );
                key++;
            },1,1, TimeUnit.SECONDS
        );
    }
}
