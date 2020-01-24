package fm.rabbitmq.kafka.connector.consul;

import org.springframework.context.Lifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestSmartLifeCycle implements Lifecycle {

    boolean runStatus = false;

    public TestSmartLifeCycle() {
    }

    @Bean
    public TestSmartLifeCycle testApp() {
        return new TestSmartLifeCycle();
    }

    @Override
    public void start() {
        System.err.println(">> call: start (Lifecycle)");
        runStatus = true;
    }

    @Override
    public void stop() {
        System.err.println(">> call: stop (Lifecycle)");
        runStatus = false;
    }

  @Override
  public boolean isRunning() {
    System.err.println(">> call: is running: " + runStatus);
    return runStatus;
  }
}