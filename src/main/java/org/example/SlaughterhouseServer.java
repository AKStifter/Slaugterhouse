package org.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.sql.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.example.grpc.SlaughterhouseProto.Animal;
import com.example.grpc.SlaughterhouseProto.Product;
import org.example.Controller.SlaughterhouseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static java.lang.Thread.sleep;

@Component
public class SlaughterhouseServer implements CommandLineRunner {

    @Autowired
    private SlaughterhouseServiceImpl slaughterhouseService;

    private static Connection connect() throws SQLException, IOException {
        Properties props = new Properties();
        try (InputStream input = SlaughterhouseServer.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new IOException("Unable to find application.properties");
            }
            props.load(input);
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        return DriverManager.getConnection(url, user, password);
    }

    public static final Map<Integer, Animal> animals = new HashMap<>();
    public static final Map<Integer, Product> products = new HashMap<>();

    public static void initializeData() throws SQLException, IOException {
        try (Connection connection = connect()) {
            String animalQuery = "SELECT ID, Species, Weight FROM slaughterhouse.animal";
            try {
                PreparedStatement statement = connection.prepareStatement(animalQuery);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String species = resultSet.getString("species");
                    double weight = resultSet.getDouble("weight");

                    animals.put(id, Animal.newBuilder().setId(id).setSpecies(species)
                            .setWeight(weight).build());
                }
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Adding sample data
        animals.put(1, Animal.newBuilder().setId(1).setRegistrationNumber("ANIMAL001").setSpecies(":Cow").setWeight(500).build());
        animals.put(2, Animal.newBuilder().setId(2).setRegistrationNumber("ANIMAL002").setSpecies(":Pig").setWeight(250).build());
        animals.put(3, Animal.newBuilder().setId(3).setRegistrationNumber("ANIMAL003").setSpecies(":Sheep").setWeight(300).build());
        animals.put(4, Animal.newBuilder().setId(4).setRegistrationNumber("ANIMAL004").setSpecies(":Goat").setWeight(400).build());
        animals.put(5, Animal.newBuilder().setId(5).setRegistrationNumber("ANIMAL005").setSpecies(":Chicken").setWeight(20).build());
        animals.put(6, Animal.newBuilder().setId(6).setRegistrationNumber("ANIMAL006").setSpecies(":Deer").setWeight(70).build());
        animals.put(7, Animal.newBuilder().setId(7).setRegistrationNumber("ANIMAL007").setSpecies(":Rabbit").setWeight(10).build());
        animals.put(8, Animal.newBuilder().setId(8).setRegistrationNumber("ANIMAL008").setSpecies(":Duck").setWeight(15).build());

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

        //  type 'exit' to stop the server
        while (!scanner.nextLine().equalsIgnoreCase("exit")) {
            System.out.println("Type 'exit' to shut down the server.");
        }

        // Stop the server
        System.out.println("Shutting down the server...");
        server.shutdownNow();

        // Await termination
        server.awaitTermination();
    }


}