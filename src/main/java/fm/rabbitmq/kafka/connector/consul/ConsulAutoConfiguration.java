package fm.rabbitmq.kafka.connector.consul;

import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsulAutoConfiguration {
    @Bean
    public GrpcServiceConsulRegistrar consulRegistrar(ConsulServiceRegistry consulServiceRegistry) {
        return new GrpcServiceConsulRegistrar(consulServiceRegistry);
    }
}
