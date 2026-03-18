package org.example;

import org.example.domain.CharityCase;
import org.example.repository.CharityCaseJdbcRepository;
import org.example.repository.CharityCaseRepository;

import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.setProperty("jdbc.url", "jdbc:sqlite:teledon.db");

        CharityCaseRepository repo = new CharityCaseJdbcRepository(props);
        System.out.println("Cazuri din DB:");
        for(CharityCase c : repo.findAll()) {
            System.out.println(c.getName() + ": " + c.getTotalAmount());
        }
    }
}