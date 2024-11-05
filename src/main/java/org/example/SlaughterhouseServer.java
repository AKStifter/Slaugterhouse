package org.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.grpc.SlaughterhouseProto.Animal;
import com.example.grpc.SlaughterhouseProto.Product;
import org.example.Controller.SlaughterhouseServiceImpl;

import java.sql.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static java.lang.Thread.sleep;

@Component
public class SlaughterhouseServer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SlaughterhouseServiceImpl slaughterhouseService;

    public static final Map<Integer, Animal> animals = new HashMap<>();
    public static final Map<Integer, Product> products = new HashMap<>();

    private void initializeData() throws SQLException {
        String animalQuery = "SELECT ID, Species, Weight FROM slaughterhouse.animal";
        try {
            jdbcTemplate.query(animalQuery, (rs, rowNum) -> {
                int id = rs.getInt("id");
                String species = rs.getString("species");
                double weight = rs.getDouble("weight");

                animals.put(id, Animal.newBuilder().setId(id).setSpecies(species)
                        .setWeight(weight).build());
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Adding sample data
        animals.put(1, Animal.newBuilder().setId(1).setRegistrationNumber("ANIMAL001").setSpecies(":Cow").setWeight(500).build());
        animals.put(2, Animal.newBuilder().setId(2).setRegistrationNumber("ANIMAL002").setSpecies(":Pig").setWeight(250).build());
        // ... (other sample animals)

        // Adding sample products (associated with animals)
        products.put(1, Product.newBuilder().setId(1).addAnimalIds(1).build());
        products.put(2, Product.newBuilder().setId(2).addAnimalIds(2).build());
    }

    @Override
    public void run(String... args) throws Exception {
        initializeData();

        Server server = ServerBuilder.forPort(50051)
                .addService(slaughterhouseService)
                .build();

        // Start the server
        server.start();
        System.out.println("Server started please wait:");
        sleep(3000);
        System.out.println("Welcome to the Slaughterhouse! ");
        sleep(1000);
        System.out.println("To view current animal livestock, press C to continue:");
        Scanner scanner = new Scanner(System.in);
        if (scanner.nextLine().equalsIgnoreCase("C")) {
            for (Animal animal : animals.values())
                System.out.println("Loaded Animal: ID=" + animal.getId() + ", Species=" + animal.getSpecies() + ", Weight=" + animal.getWeight());
        }

        // type 'exit' to stop the server
        while (!scanner.nextLine().equalsIgnoreCase("exit")) {
            System.out.println("Type 'exit' to shut down the server.");
        }

        // Stop the server
        System.out.println("Shutting down the server...");
        server.shutdownNow();


    }
}