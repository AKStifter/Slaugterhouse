package org.example.Controller;

import com.example.grpc.SlaughterhouseProto;
import com.example.grpc.SlaughterhouseServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.example.SlaughterhouseServer;
import org.springframework.stereotype.Service;

@Service
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

    @Override
    public void getPartInfo(SlaughterhouseProto.Part request, StreamObserver<SlaughterhouseProto.Part> responseObserver) {
        // Implementation for getting part info
        SlaughterhouseProto.Part part = SlaughterhouseServer.parts.get(request.getId());
        if (part != null) {
            responseObserver.onNext(part);
        } else {
            responseObserver.onError(new Exception("Part not found"));
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getProductAnimal(SlaughterhouseProto.ProductAnimal request, StreamObserver<SlaughterhouseProto.ProductAnimal> responseObserver) {
        // Implementation for getting product-animal association
        // This is a placeholder implementation. You'll need to adjust it based on how you store this data.
        SlaughterhouseProto.ProductAnimal productAnimal = SlaughterhouseServer.productAnimals.get(request.getProductId());
        if (productAnimal != null) {
            responseObserver.onNext(productAnimal);
        } else {
            responseObserver.onError(new Exception("ProductAnimal association not found"));
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getProductPart(SlaughterhouseProto.ProductPart request, StreamObserver<SlaughterhouseProto.ProductPart> responseObserver) {
        // getting product-part association
    // need to adjust it based on how you store this data.
        SlaughterhouseProto.ProductPart productPart = SlaughterhouseServer.productParts.get(request.getProductId());
        if (productPart != null) {
            responseObserver.onNext(productPart);
        } else {
            responseObserver.onError(new Exception("ProductPart association not found"));
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getRecall(SlaughterhouseProto.Recall request, StreamObserver<SlaughterhouseProto.Recall> responseObserver) {
        SlaughterhouseProto.Recall recall = SlaughterhouseServer.recalls.get(request.getId());
        if (recall != null) {
            responseObserver.onNext(recall);
        } else {
            responseObserver.onError(new Exception("Recall not found"));
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getTray(SlaughterhouseProto.Tray request, StreamObserver<SlaughterhouseProto.Tray> responseObserver) {
        SlaughterhouseProto.Tray tray = SlaughterhouseServer.trays.get(request.getId());
        if (tray != null) {
            responseObserver.onNext(tray);
        } else {
            responseObserver.onError(new Exception("Tray not found"));
        }
        responseObserver.onCompleted();    }





}