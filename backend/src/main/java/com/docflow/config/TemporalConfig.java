package com.docflow.config;

import com.docflow.workflow.DocumentActivitiesImpl;
import com.docflow.workflow.DocumentApprovalWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class TemporalConfig {
    
    @Value("${temporal.host:localhost}")
    private String temporalHost;
    
    @Value("${temporal.port:7233}")
    private int temporalPort;
    
    @Value("${temporal.namespace:default}")
    private String namespace;
    
    @Value("${temporal.task-queue:docflow-task-queue}")
    private String taskQueue;
    
    @Bean
    public WorkflowServiceStubs workflowServiceStubs() {
        return WorkflowServiceStubs.newServiceStubs(
            WorkflowServiceStubsOptions.newBuilder()
                .setTarget(temporalHost + ":" + temporalPort)
                .build()
        );
    }
    
    @Bean
    public WorkflowClient workflowClient(WorkflowServiceStubs serviceStubs) {
        return WorkflowClient.newInstance(
            serviceStubs,
            WorkflowClientOptions.newBuilder()
                .setNamespace(namespace)
                .build()
        );
    }
    
    @Bean
    public String taskQueue() {
        return taskQueue;
    }
    
    @Bean
    @Lazy
    public WorkerFactory workerFactory(
            WorkflowClient workflowClient,
            DocumentActivitiesImpl documentActivities) {
        
        WorkerFactory factory = WorkerFactory.newInstance(workflowClient);
        
        Worker worker = factory.newWorker(taskQueue);
        
        // Register workflow implementation
        worker.registerWorkflowImplementationTypes(DocumentApprovalWorkflowImpl.class);
        
        // Register activities
        worker.registerActivitiesImplementations(documentActivities);
        
        // Start worker
        factory.start();
        
        System.out.println("Temporal worker started for task queue: " + taskQueue);
        
        return factory;
    }
}
