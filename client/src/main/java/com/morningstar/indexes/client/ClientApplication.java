package com.morningstar.indexes.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class ClientApplication {
	@Value("${customer.baseurl}")
	private String customerBaseUrl;

	@Bean
	CustomerClient customerClient(WebClient.Builder builder) {
		return HttpServiceProxyFactory
				.builder(WebClientAdapter.forClient(builder.baseUrl(customerBaseUrl).build()))
				.build()
				.createClient(CustomerClient.class);
	}

	@Bean
	ApplicationListener<ApplicationReadyEvent> readyEventApplicationListener(CustomerClient cc) {
		return event -> cc.all().subscribe(System.out::println);
	}

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

}

interface CustomerClient{

	@GetExchange("/customers")
	Flux<Customer> all();

	@GetExchange("/customers/{name}")
	Flux<Customer> byName(@PathVariable String name);

}

record Customer(Integer id, String name) {

}