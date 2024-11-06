package org.example;

import com.example.grpc.SlaughterhouseProto;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import com.example.grpc.SlaughterhouseProto.Part;
import com.example.grpc.SlaughterhouseProto.ProductAnimal;
import com.example.grpc.SlaughterhouseProto.Recall;
import com.example.grpc.SlaughterhouseProto.Tray;
import com.example.grpc.SlaughterhouseProto.Animal;
import com.example.grpc.SlaughterhouseProto.Product;
import org.example.Service.SlaughterhouseServiceImpl;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

@Component
public class SlaughterhouseServer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SlaughterhouseServiceImpl slaughterhouseService;

    public static final Map<Integer, SlaughterhouseProto.Animal> animals = new HashMap<>();
    public static final Map<Integer, SlaughterhouseProto.Product> products = new HashMap<>();
    public static final Map<Integer, SlaughterhouseProto.Part> parts = new HashMap<>();
    public static final Map<Integer, SlaughterhouseProto.ProductAnimal> productAnimals = new HashMap<>();
    public static final Map<Integer, SlaughterhouseProto.ProductPart> productParts = new HashMap<>();
    public static final Map<Integer, SlaughterhouseProto.Recall> recalls = new HashMap<>();
    public static final Map<Integer, SlaughterhouseProto.Tray> trays = new HashMap<>();

    private void initializeData() throws SQLException {

        String animalQuery = "SELECT ID, Species, Weight, arrivaldate,recall_id FROM slaughterhouse.animal";
        try {
            jdbcTemplate.query(animalQuery, (rs, rowNum) -> {
                int id = rs.getInt("ID");
                String species = rs.getString("Species");
                double weight = rs.getDouble("Weight");
                String registrationNumber = rs.getString("Registration_Number");

                animals.put(id, Animal.newBuilder()
                        .setId(id)
                        .setSpecies(species)
                        .setWeight(weight)
                        .setRegistrationNumber(registrationNumber)
                        .build());
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


        String productQuery = "SELECT ID FROM slaughterhouse.product";
        try {
            jdbcTemplate.query(productQuery, (rs, rowNum) -> {
                int id = rs.getInt("ID");
                products.put(id, Product.newBuilder().setId(id).build());
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        String partQuery = "SELECT ID, Animal_ID, weight,type FROM slaughterhouse.part";
        try {
            jdbcTemplate.query(partQuery, (rs, rowNum) -> {
                int id = rs.getInt("ID");
                int animalId = rs.getInt("Animal_ID");
                String name = rs.getString("Name");

                parts.put(id, Part.newBuilder()
                        .setId(id)
                        .setAnimalId(animalId)
                        .setName(name)
                        .build());
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


        String productAnimalQuery = "SELECT ID, type,tray_ids FROM slaughterhouse.productanimal";
        try {
            jdbcTemplate.query(productAnimalQuery, (rs, rowNum) -> {
                int productId = rs.getInt("Product_ID");
                int animalId = rs.getInt("Animal_ID");

                ProductAnimal productAnimal = ProductAnimal.newBuilder()
                        .setProductId(productId)
                        .setAnimalId(animalId)
                        .build();
                productAnimals.put(productId * 10000 + animalId, productAnimal);
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


        String productPartQuery = "SELECT ID, type, max_weight,tray_ids,productiondate,status FROM slaughterhouse.productpart";
        try {
            jdbcTemplate.query(productPartQuery, (rs, rowNum) -> {
                int productId = rs.getInt("Product_ID");
                int partId = rs.getInt("Part_ID");

                SlaughterhouseProto.ProductPart productPart = SlaughterhouseProto.ProductPart.newBuilder()
                        .setProductId(productId)
                        .setPartId(partId)
                        .build();
                productParts.put(productId * 10000 + partId, productPart);
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        String recallQuery = "SELECT ID, dateinitiated, Reason FROM slaughterhouse.recall";
        try {
            jdbcTemplate.query(recallQuery, (rs, rowNum) -> {
                int id = rs.getInt("ID");
                int productId = rs.getInt("Product_ID");
                String reason = rs.getString("Reason");

                recalls.put(id, Recall.newBuilder()
                        .setId(id)
                        .setProductId(productId)
                        .setReason(reason)
                        .build());
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        String trayQuery = "SELECT ID, type_of_part, Max_Weight,current_weight FROM slaughterhouse.tray";
        try {
            jdbcTemplate.query(trayQuery, (rs, rowNum) -> {
                int id = rs.getInt("ID");
                int productId = rs.getInt("Product_ID");
                double maxWeight = rs.getDouble("Max_Weight");

                trays.put(id, Tray.newBuilder()
                        .setId(id)
                        .setProductId(productId)
                        .setMaxWeight(maxWeight)
                        .build());
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Adding sample data , this was just to see if i did things correctly
        //animals.put(1, Animal.newBuilder().setId(1).setRegistrationNumber("ANIMAL001").setSpecies(":Cow").setWeight(500).build());
        //animals.put(2, Animal.newBuilder().setId(2).setRegistrationNumber("ANIMAL002").setSpecies(":Pig").setWeight(250).build());

        // Adding sample products same as above
        //products.put(1, Product.newBuilder().setId(1).addAnimalIds(1).build());
       // products.put(2, Product.newBuilder().setId(2).addAnimalIds(2).build());
    }

    @Override
    public void run(String... args) throws Exception {
        initializeData();

        Server server = ServerBuilder.forPort(50051)
                .addService(slaughterhouseService)
                .build();

        server.start();
        System.out.println("Server started, please wait:");
        sleep(3000);
        System.out.println("Welcome to the Slaughterhouse!");
        sleep(1000);

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\nChoose an option to view data (or type 'exit' to quit):");
            System.out.println("1. View Animals");
            System.out.println("2. View Products");
            System.out.println("3. View Parts");
            System.out.println("4. View Products by Animal ");
            System.out.println("5. View Product parts /cuts ");
            System.out.println("6. View Product Recalls");
            System.out.println("7. View Trays after slaughter");

            String input = scanner.nextLine();

            switch (input.toLowerCase()) {
                case "1":
                    for (Animal animal : animals.values())
                        System.out.println("Animal: ID=" + animal.getId() + ", Species=" + animal.getSpecies() + ", Weight=" + animal.getWeight());
                    break;
                case "2":
                    for (Product product : products.values())
                        System.out.println("Product: ID=" + product.getId() + ", Animals=" + product.getAnimalIdsList());
                    break;
                case "3":
                    for (Part part : parts.values())
                        System.out.println("Part: ID=" + part.getId() + ", AnimalID=" + part.getAnimalId() + ", Name=" + part.getName());
                    break;
                case "4":
                    for (ProductAnimal productAnimal : productAnimals.values())
                        System.out.println("ProductAnimal: ProductID=" + productAnimal.getProductId() + ", AnimalID=" + productAnimal.getAnimalId());
                    break;
                case "5":
                    for (SlaughterhouseProto.ProductPart productPart : productParts.values())
                        System.out.println("ProductPart: ProductID=" + productPart.getProductId() + ", PartID=" + productPart.getPartId());
                    break;
                case "6":
                    for (Recall recall : recalls.values())
                        System.out.println("Recall: ID=" + recall.getId() + ", ProductID=" + recall.getProductId() + ", Reason=" + recall.getReason());
                    break;
                case "7":
                    for (Tray tray : trays.values())
                        System.out.println("Tray: ID=" + tray.getId() + ", ProductID=" + tray.getProductId() + ", MaxWeight=" + tray.getMaxWeight());
                    break;
                case "exit":
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        System.out.println("Shutting down the server...");
        if (server != null) {
            server.shutdown();
            try {
                if (!server.awaitTermination(30, TimeUnit.SECONDS)) {
                    server.shutdownNow();
                    if (!server.awaitTermination(30, TimeUnit.SECONDS)) {
                        System.err.println("Server did not terminate");
                    }
                }
            } catch (InterruptedException e) {
                server.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        System.exit(0);
    }
}