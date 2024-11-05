package org.example.Controller;

import com.example.grpc.SlaughterhouseProto;
import com.example.grpc.SlaughterhouseServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.example.SlaughterhouseServer;

public class SlaughterhouseServiceImpl extends SlaughterhouseServiceGrpc.SlaughterhouseServiceImplBase {

    @Override
    public void getAnimalInfo(SlaughterhouseProto.Product request, StreamObserver<SlaughterhouseProto.Animal> responseObserver) {
        if (request.getAnimalIdsCount() > 0) {
            int animalId = request.getAnimalIds(0);  // Get the first animal ID
            SlaughterhouseProto.Animal animal = SlaughterhouseServer.animals.get(animalId);   // from the "database"

            if (animal != null) {
                // Send the animal information
                responseObserver.onNext(animal);
            } else {
                responseObserver.onError(new Exception("Animal not found"));
            }
        } else {
            responseObserver.onError(new Exception("No animal IDs associated with the product"));
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getProductInfo(SlaughterhouseProto.Animal request, StreamObserver<SlaughterhouseProto.Product> responseObserver) {
        int animalId = request.getId();

        for (SlaughterhouseProto.Product product : SlaughterhouseServer.products.values()) {
            if (product.getAnimalIdsList().contains(animalId)) {
                // Send the product information
                responseObserver.onNext(product);
                responseObserver.onCompleted();
                return;
            }
        }
        responseObserver.onError(new Exception("No product found for this animal"));
    }
}