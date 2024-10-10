package org.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.sql.*;

import io.grpc.stub.StreamObserver;
import com.example.grpc.SlaughterhouseProto.Animal;
import com.example.grpc.SlaughterhouseProto.Product;
import com.example.grpc.SlaughterhouseServiceGrpc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class SlaughterhouseServer
{

  // Simulated database
  private static Connection connect() throws SQLException {
    String url = "jdbc:postgresql://localhost:5432/postgres";
    String user = "postgres";
    String password = "451311qq";
    return DriverManager.getConnection(url, user, password);
  }
  private static final Map<Integer, Animal> animals = new HashMap<>();
  private static  final Map<Integer, Product> products = new HashMap<>();

  public static void main(String[] args)
          throws IOException, InterruptedException, SQLException {

    initializeData();

    Server server = ServerBuilder.forPort(50051).addService(
            new SlaughterhouseServiceImpl())  // implementation for the services
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




    // Stop the server
    server.shutdownNow();
    //Scanner scanner = new Scanner(System.in);
    if(scanner.nextLine().equalsIgnoreCase("exit"))
      System.out.println("Shutting down the server...");

    // Await termination
    server.awaitTermination();
  }

  private static void initializeData() throws SQLException {

    try (Connection connection = connect()) {
      String animalQuery = "SELECT  ID, Species,Weight  FROM slaughterhouse.animal";
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


      }
      catch (SQLException e) {
        e.printStackTrace();
      }
    }
    // Adding sample shit
    animals.put(1,
        Animal.newBuilder().setId(1).setRegistrationNumber("ANIMAL001")
            .setSpecies(":Cow").setWeight(500).build());
    animals.put(2,
        Animal.newBuilder().setId(2).setRegistrationNumber("ANIMAL002")
            .setSpecies(":Pig").setWeight(250).build());
    animals.put(3,
            Animal.newBuilder().setId(3).setRegistrationNumber("ANIMAL003")
            .setSpecies(":Sheep").setWeight(300).build());
    animals.put(4,
            Animal.newBuilder().setId(4).setRegistrationNumber("ANIMAL004")
           .setSpecies(":Goat").setWeight(400).build());
    animals.put(5,
            Animal.newBuilder().setId(5).setRegistrationNumber("ANIMAL005")
           .setSpecies(":Chicken").setWeight(20).build());
    animals.put(6,
            Animal.newBuilder().setId(6).setRegistrationNumber("ANIMAL006")
           .setSpecies(":Deer").setWeight(70).build());
    animals.put(7,
            Animal.newBuilder().setId(7).setRegistrationNumber("ANIMAL007")
           .setSpecies(":Rabbit").setWeight(10).build());
    animals.put(8,
            Animal.newBuilder().setId(8).setRegistrationNumber("ANIMAL008")
           .setSpecies(":Duck").setWeight(15).build());



    // Adding sample products (associated with animals)
    products.put(1, Product.newBuilder().setId(1).addAnimalIds(1).build());
    products.put(2, Product.newBuilder().setId(2).addAnimalIds(2).build());
  }

  static class SlaughterhouseServiceImpl
      extends SlaughterhouseServiceGrpc.SlaughterhouseServiceImplBase
  {

    @Override public void getAnimalInfo(Product request,
        StreamObserver<Animal> responseObserver) // client gets the information about animals
    {
      if (request.getAnimalIdsCount() > 0)
      {
        int animalId = request.getAnimalIds(0);  // Get the first animal ID
        Animal animal = animals.get(
            animalId);   // from the "database"

        if (animal != null)
        {
          // Send the animal information
          responseObserver.onNext(animal);
        }
        else
        {
          responseObserver.onError(new Exception("Animal not found"));
        }
      }
      else
      {
        responseObserver.onError(
            new Exception("No animal IDs associated with the product"));
      }
      responseObserver.onCompleted();
    }

    @Override public void getProductInfo(Animal request,
        StreamObserver<Product> responseObserver)
    {

      int animalId = request.getId();

      for (Product product : products.values())
      {
        if (product.getAnimalIdsList().contains(animalId))
        {
          // Send the product information
          responseObserver.onNext(product);
          responseObserver.onCompleted();
          return;
        }
      }
      responseObserver.onError(
          new Exception("No product found for this animal"));
    }
  }
}
