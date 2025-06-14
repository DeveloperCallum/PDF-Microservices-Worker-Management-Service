package com.willcocks.callum.workermanagementservice.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    public Queue documentProcessingQueue() {
        return QueueBuilder.nonDurable("documentProcessingQueue")  // Non-durable queue
                .deadLetterExchange("dlx-exchange")  // Assign a DLX
                .deadLetterRoutingKey("dlx-routing-key")  // Routing key for dead letters
                .build();
    }

    @Bean
    public Queue imageProcessingQueue() {
        return QueueBuilder.nonDurable("imageProcessingQueue")  // Non-durable queue
                .deadLetterExchange("dlx-exchange")  // Assign a DLX
                .deadLetterRoutingKey("dlx-routing-key")  // Routing key for dead letters
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable("dlx-queue")  // Name of the DLQ
                .build();
    }

    @Bean
    public Declarables deadLetterSetup() {
        return new Declarables(
                new DirectExchange("dlx-exchange"), // Create the DLX
                new Queue("dlx-queue"), // Create the DLQ
                new Binding("dlx-queue", Binding.DestinationType.QUEUE, "dlx-exchange", "dlx-routing-key", null) // Bind DLQ to DLX
        );
    }

    @Bean
    public Binding documentBinding(Queue documentProcessingQueue, DirectExchange exchange) {
        return BindingBuilder.bind(documentProcessingQueue).to(exchange).with("pdf.workers.document.processing");
    }

    @Bean
    public Queue documentReplyQueue() {
        return QueueBuilder.nonDurable("documentProcessingReplyQueue")  // Non-durable queue
                .deadLetterExchange("dlx-exchange")  // Assign a DLX
                .deadLetterRoutingKey("dlx-routing-key")  // Routing key for dead letters
                .build();
    }

    @Bean
    public Queue imageReplyQueue() {
        return QueueBuilder.nonDurable("imageReplyQueue")  // Non-durable queue
                .deadLetterExchange("dlx-exchange")  // Assign a DLX
                .deadLetterRoutingKey("dlx-routing-key")  // Routing key for dead letters
                .build();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    //Application runner is a callback interface that allows you to run code after the application has started but before it is ready to accept requests.
    public ApplicationRunner runner(RabbitAdmin rabbitAdmin) {
        return args -> rabbitAdmin.initialize();
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("myExchange");
    }

    @Bean
    public Binding imageBinding(Queue imageProcessingQueue, DirectExchange exchange) {
        return BindingBuilder.bind(imageProcessingQueue).to(exchange).with("pdf.workers.image.processing");
    }
}
