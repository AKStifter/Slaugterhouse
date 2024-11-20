package org.example.CLI;

import com.example.grpc.SlaughterhouseProto;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import com.example.grpc.SlaughterhouseProto.*;
import org.example.Service.SlaughterhouseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.List;

@Component
public class SlaughterhouseServer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(SlaughterhouseServer.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SlaughterhouseServiceImpl slaughterhouseService;

    public static final Map<Integer, Animal> animals = new HashMap<>();
    public static final Map<Integer, Product> products = new HashMap<>();
    public static final Map<Integer, Part> parts = new HashMap<>();
    public static final Map<Integer, ProductAnimal> productAnimals = new HashMap<>();
    public static final Map<Integer, SlaughterhouseProto.ProductPart> productParts = new HashMap<>();
    public static final Map<Integer, Recall> recalls = new HashMap<>();
    public static final Map<Integer, Tray> trays = new HashMap<>();

    private void initializeData() {
        logger.info("Initializing data...");

        String animalQuery = "SELECT id, species, weight,  arrivaldate FROM slaughterhouse.animal";
        try {
            List<Animal> animalList = jdbcTemplate.query(animalQuery, 
                (rs, rowNum) -> Animal.newBuilder()
                    .setId(rs.getInt("id"))
                    .setRegistrationNumber(rs.getString("registration_number"))
                    .setSpecies(rs.getString("species"))
                    .setWeight(rs.getDouble("weight"))
                    .setOrigin(rs.getString("origin"))
                    .setArrivalDate(rs.getDate("arrival_date").toString())
                    .build());
            animalList.forEach(animal -> animals.put(animal.getId(), animal));
        } catch (Exception e) {
            logger.error("Error executing animalQuery: ", e);
        }

        // Similar changes for other queries...

        String trayQuery = "SELECT id, type_of_part, max_weight, current_weight FROM slaughterhouse.tray";
        try {
            List<Tray> trayList = jdbcTemplate.query(trayQuery,
                (rs, rowNum) -> Tray.newBuilder()
                    .setId(rs.getInt("id"))
                    .setType(rs.getString("type_of_part"))
                    .setMaxWeight(rs.getDouble("max_weight"))
                    .setCurrentWeight(rs.getDouble("current_weight"))
                    .build());
            trayList.forEach(tray -> trays.put(tray.getId(), tray));
        } catch (Exception e) {
            logger.error("Error executing trayQuery: ", e);
        }

        logger.info("Data initialization complete.");
        logger.info("Animals: {}", animals.size());
        logger.info("Products: {}", products.size());
        logger.info("Parts: {}", parts.size());
        logger.info("ProductAnimals: {}", productAnimals.size());
        logger.info("ProductParts: {}", productParts.size());
        logger.info("Recalls: {}", recalls.size());
        logger.info("Trays: {}", trays.size());
    }

    @Override
    public void run(String... args) throws Exception {
        initializeData();

        Server server = ServerBuilder.forPort(50051)
                .addService(slaughterhouseService)
                .build();

        try {
            server.start();
            logger.info("Server started, listening on port 50051");
            logger.info("Welcome to the Slaughterhouse!");

            Scanner scanner = new Scanner(System.in);
            boolean exit = false;

            while (!exit) {
                System.out.println("\nChoose an option to view data (or type 'exit' to quit):");
                System.out.println("1. View Animals");
                System.out.println("2. View Products");
                System.out.println("3. View Parts");
                System.out.println("4. View Products by Animal");
                System.out.println("5. View Product Parts");
                System.out.println("6. View Product Recalls");
                System.out.println("7. View Trays");

                String input = scanner.nextLine();

                switch (input.toLowerCase()) {
                    case "1":
                        animals.values().forEach(animal -> 
                            System.out.println("Animal: ID=" + animal.getId() + ", Species=" + animal.getSpecies() + ", Weight=" + animal.getWeight()));
                        break;
                    case "2":
                        products.values().forEach(product -> 
                            System.out.println("Product: ID=" + product.getId() + ", Name=" + product.getName() + ", Weight=" + product.getWeight()));
                        break;
                    case "3":
                        parts.values().forEach(part -> 
                            System.out.println("Part: ID=" + part.getId() + ", Name=" + part.getName() + ", Weight=" + part.getWeight()));
                        break;
                    case "4":
                        productAnimals.values().forEach(pa -> 
                            System.out.println("ProductAnimal: ProductID=" + pa.getProductId() + ", AnimalID=" + pa.getAnimalId() + ", Species=" + pa.getSpecies()));
                        break;
                    case "5":
                        productParts.values().forEach(pp -> 
                            System.out.println("ProductPart: ProductID=" + pp.getProductId() + ", PartID=" + pp.getPartId()));
                        break;
                    case "6":
                        recalls.values().forEach(recall -> 
                            System.out.println("Recall: ID=" + recall.getId() + ", ProductID=" + recall.getProductId() + ", Reason=" + recall.getReason()));
                        break;
                    case "7":
                        trays.values().forEach(tray -> 
                            System.out.println("Tray: ID=" + tray.getId() + ", Type=" + tray.getType() + ", MaxWeight=" + tray.getMaxWeight()));
                        break;
                    case "exit":
                        exit = true;
                        break;
                    default:
                        System.out.println("Oops!.... Please try again.");
                }
            }

            scanner.close();
        } catch (IOException e) {
            logger.error("Error starting the server", e);
        } finally {
            if (server != null) {
                server.shutdown();
            }
        }
    }
}