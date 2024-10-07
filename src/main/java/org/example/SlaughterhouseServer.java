package org.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import com.example.grpc.SlaughterhouseProto.Animal;
import com.example.grpc.SlaughterhouseProto.Product;
import com.example.grpc.SlaughterhouseServiceGrpc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SlaughterhouseServer
{

  // Simulated database
  private static Map<Integer, Animal> animals = new HashMap<>();
  private static Map<Integer, Product> products = new HashMap<>();

  public static void main(String[] args)
      throws IOException, InterruptedException
  {

    initializeData();

    Server server = ServerBuilder.forPort(50051).addService(
            new SlaughterhouseServiceImpl())  // implementation for the services
        .build();

    // Start the server
    server.start();
    System.out.println("Server started: ======>");

    // Await termination
    server.awaitTermination();
  }

  private static void initializeData()
  {
    // Adding sample shit
    animals.put(1,
        Animal.newBuilder().setId(1).setRegistrationNumber("ANIMAL001")
            .setSpecies("Cow").setWeight(500).build());
    animals.put(2,
        Animal.newBuilder().setId(2).setRegistrationNumber("ANIMAL002")
            .setSpecies("Pig").setWeight(250).build());

    // Adding sample products (associated with animals)
    products.put(1, Product.newBuilder().setId(1).addAnimalIds(1).build());
    products.put(2, Product.newBuilder().setId(2).addAnimalIds(2).build());
  }

  static class SlaughterhouseServiceImpl
      extends SlaughterhouseServiceGrpc.SlaughterhouseServiceImplBase
  {

    @Override public void getAnimalInfo(Product request,
        StreamObserver<Animal> responseObserver)
    {
      if (request.getAnimalIdsCount() > 0)
      {
        int animalId = request.getAnimalIds(0);  // Get the first animal ID
        Animal animal = animals.get(
            animalId);   // Retrieve animal from the "database"

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
