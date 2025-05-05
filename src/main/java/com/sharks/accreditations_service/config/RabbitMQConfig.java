package com.sharks.accreditations_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue accreditationConfirmationQueue() {
        return new Queue("accreditation-confirmation", true);
    }

    @Bean
    public TopicExchange emailExchange() {
        return new TopicExchange("email-exchange", true, false);
    }

    @Bean
    public Binding accreditationConfirmationBinding() {
        return BindingBuilder.bind(accreditationConfirmationQueue()).to(emailExchange())
                .with("accreditation.confirmation");
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }

    @Bean
    public RabbitTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        return rabbitTemplate(connectionFactory, jsonMessageConverter());
    }

    @Bean
    public ApplicationRunner runner(ConnectionFactory connectionFactory) {
        return args -> connectionFactory.createConnection().close();
    }
}
