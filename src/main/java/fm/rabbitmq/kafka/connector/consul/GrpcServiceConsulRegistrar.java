package fm.rabbitmq.kafka.connector.consul;

import com.ecwid.consul.v1.agent.model.NewService;
import org.lognet.springboot.grpc.context.GRpcServerInitializedEvent;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.discovery.HeartbeatProperties;
import org.springframework.cloud.consul.serviceregistry.ConsulAutoRegistration;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistration;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.event.EventListener;

public class GrpcServiceConsulRegistrar implements SmartLifecycle {

    private final ConsulServiceRegistry consulServiceRegistry;
    private ConsulRegistration registration;

    public GrpcServiceConsulRegistrar(ConsulServiceRegistry consulServiceRegistry) {
        this.consulServiceRegistry = consulServiceRegistry;
    }

    @EventListener
    public void onGrpcServerStarted(GRpcServerInitializedEvent initializedEvent) {
        registration = getRegistration(initializedEvent);
        consulServiceRegistry.register(registration);

    }

    private ConsulRegistration getRegistration(GRpcServerInitializedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();

        ConsulDiscoveryProperties properties = applicationContext.getBean(ConsulDiscoveryProperties.class);

        NewService grpcService = new NewService();
        grpcService.setPort(event.getServer().getPort());
        if (!properties.isPreferAgentAddress()) {
            grpcService.setAddress(properties.getHostname());
        }
        String appName = ConsulAutoRegistration.getAppName(properties, applicationContext.getEnvironment());
        grpcService.setName(ConsulAutoRegistration.normalizeForDns(appName));
        grpcService.setId(ConsulAutoRegistration.getInstanceId(properties, applicationContext));

        NewService.Check check = new NewService.Check();
        String tcp = String.join(":", properties.getHostname(), Integer.toString(event.getServer().getPort()));
        check.setTcp(tcp);
        check.setInterval("10s");
        check.setTimeout("3s");
        check.setDeregisterCriticalServiceAfter("30s");
        grpcService.setCheck(check);

        /*
         * service.setTags(createTags(properties)); setCheck(service,
         * autoServiceRegistrationProperties, properties, context, heartbeatProperties);
         */

        return new ConsulRegistration(grpcService, properties);
    }

    public static NewService.Check createCheck(Integer port, HeartbeatProperties ttlConfig,
            ConsulDiscoveryProperties properties) {
        NewService.Check check = new NewService.Check();
        check.setInterval("10s");
        check.setTimeout("3s");

      
        return check;
    }

    @Override
    public boolean isAutoStartup() {
        return false;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public void start() {

    }

    // @Override
    public synchronized void stop() {
        consulServiceRegistry.deregister(registration);
        consulServiceRegistry.close();
        registration = null;

    }

    @Override
    public synchronized boolean isRunning() {
        return null != registration;
    }

    @Override
    public int getPhase() {
        return 0;
    }
}
