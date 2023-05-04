package com.boutouil.demo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Random;
import java.util.function.Function;

@Slf4j
@EnableScheduling
@SpringBootApplication
@RequiredArgsConstructor
public class Application {

    private static final Random RANDOM = new Random();
    private static final String PRICE_CALCULATOR_OUT = "price-calculator-out-0";

    private final StreamBridge bridge;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Generates random price values and sends them to the PRICE_CALCULATOR_OUT channel.
     * This method is scheduled to run at a fixed rate of 10 seconds (10000 milliseconds).
     */
    @Scheduled(fixedRate = 10000)
    public void generatePrices() {
        double value = RANDOM.nextDouble();

        log.info("NEW PRICE ==> {}", value);

        bridge.send(PRICE_CALCULATOR_OUT, value);
    }

    /**
     * Defines a Function bean that consumes price values from the input channel, calculates a percentage,
     * and sends the result to the output channel. The input channel is defined in the application configuration
     * as PRICE.IN.QUEUE, and the output channel is defined as PRICE.PERCENTAGE.IN.QUEUE.
     *
     * @return a Function that processes Message<String> and produces Message<Double>
     */
    @Bean
    public Function<Message<String>, Message<Double>> priceCalculator() {
        return event -> {
            final var payload = Double.parseDouble(event.getPayload());
            final var result = payload * 100;

            log.info("\nRECEIVED PRICE ==> {}\nGENERATED PERCENTAGE ==> {}", payload, result);

            return MessageBuilder.withPayload(result)
                    .build();
        };
    }
}
