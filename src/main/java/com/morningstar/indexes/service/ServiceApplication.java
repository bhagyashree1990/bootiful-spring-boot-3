package com.morningstar.indexes.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@SpringBootApplication
public class ServiceApplication {

    @Bean
    ApplicationListener<ApplicationReadyEvent> readyEventApplicationListener(CustomerService cs) {
        return event -> cs.all().forEach(System.out::println);
    }

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

}

@Service
class CustomerService {
    private final JdbcTemplate template;
    private final RowMapper<Customer> customerRowMapper =
            (rs, rowNum) -> new Customer(rs.getInt("id"), rs.getString("name"));

    CustomerService(JdbcTemplate template) {
        this.template = template;
    }

    Collection<Customer> all() {
        return this.template.query("select * from customers", this.customerRowMapper);
    }

	Customer byId(Integer id) {
		return this.template.queryForObject("select * from customers where id = ?", this.customerRowMapper, id);
	}
}

record Customer(Integer id, String name) {

}