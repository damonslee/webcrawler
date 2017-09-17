package com.hafidsousa.webcrawler.config;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import javax.jms.JMSException;
import javax.jms.Session;

/**
 * @author Hafid Ferreira Sousa
 */
@EnableJms
@Configuration
public class AppConfig {

    @Bean
    public AmazonDynamoDBAsync getDynamoDBClient() {

        return AmazonDynamoDBAsyncClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider("gmail"))
                .withRegion(Regions.AP_SOUTHEAST_2)
                .build();

    }

    @Bean
    public AmazonSQSAsync getSQSClient() {

        return AmazonSQSAsyncClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider("gmail"))
                .withRegion(Regions.AP_SOUTHEAST_2)
                .build();

    }

    @Bean
    public SQSConnectionFactory sqsConnectionFactory() throws JMSException {

        return new SQSConnectionFactory(
                new ProviderConfiguration(),
                AmazonSQSClientBuilder.standard()
                        .withRegion(Regions.AP_SOUTHEAST_2)
                        .withCredentials(new ProfileCredentialsProvider("gmail"))
        );
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() throws JMSException {

        DefaultJmsListenerContainerFactory factory =
                new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(sqsConnectionFactory());
        factory.setDestinationResolver(new DynamicDestinationResolver());
        factory.setConcurrency("3-10");
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return factory;
    }

    @Bean
    public JmsTemplate defaultJmsTemplate() throws JMSException {

        return new JmsTemplate(sqsConnectionFactory());
    }
}