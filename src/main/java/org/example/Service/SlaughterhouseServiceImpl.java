package org.example.Service;

import com.example.grpc.SlaughterhouseProto;
import com.example.grpc.SlaughterhouseServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.example.CLI.SlaughterhouseServer;
import org.springframework.stereotype.Service;

@Service
public class SlaughterhouseServiceImpl extends SlaughterhouseServiceGrpc.SlaughterhouseServiceImplBase {

    @Override
    public void getAnimalInfo(SlaughterhouseProto.AnimalRequest request, StreamObserver<SlaughterhouseProto.Animal> responseObserver) {
        int animalId = request.getId();
        SlaughterhouseProto.Animal animal = SlaughterhouseServer.animals.get(animalId);

        if (animal != null) {
            responseObserver.onNext(animal);
        } else {
            responseObserver.onError(new Exception("Animal not found"));
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getProductInfo(SlaughterhouseProto.ProductRequest request, StreamObserver<SlaughterhouseProto.Product> responseObserver) {
        int productId = request.getId();
        SlaughterhouseProto.Product product = SlaughterhouseServer.products.get(productId);

        if (product != null) {
            responseObserver.onNext(product);
        } else {
            responseObserver.onError(new Exception("Product not found"));
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getPartInfo(SlaughterhouseProto.PartRequest request, StreamObserver<SlaughterhouseProto.Part> responseObserver) {
        int partId = request.getId();
        SlaughterhouseProto.Part part = SlaughterhouseServer.parts.get(partId);

        if (part != null) {
            responseObserver.onNext(part);
        } else {
            responseObserver.onError(new Exception("Part not found"));
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getProductAnimal(SlaughterhouseProto.ProductAnimalRequest request, StreamObserver<SlaughterhouseProto.ProductAnimal> responseObserver) {
        int productId = request.getProductId();
        SlaughterhouseProto.ProductAnimal productAnimal = SlaughterhouseServer.productAnimals.get(productId);

        if (productAnimal != null) {
            responseObserver.onNext(productAnimal);
        } else {
            responseObserver.onError(new Exception("ProductAnimal association not found"));
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getProductPart(SlaughterhouseProto.ProductPartRequest request, StreamObserver<SlaughterhouseProto.ProductPart> responseObserver) {
        int productId = request.getProductId();
        SlaughterhouseProto.ProductPart productPart = SlaughterhouseServer.productParts.get(productId);

        if (productPart != null) {
            responseObserver.onNext(productPart);
        } else {
            responseObserver.onError(new Exception("ProductPart association not found"));
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getRecall(SlaughterhouseProto.RecallRequest request, StreamObserver<SlaughterhouseProto.Recall> responseObserver) {
        int recallId = request.getId();
        SlaughterhouseProto.Recall recall = SlaughterhouseServer.recalls.get(recallId);

        if (recall != null) {
            responseObserver.onNext(recall);
        } else {
            responseObserver.onError(new Exception("Recall not found"));
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getTray(SlaughterhouseProto.TrayRequest request, StreamObserver<SlaughterhouseProto.Tray> responseObserver) {
        int trayId = request.getId();
        SlaughterhouseProto.Tray tray = SlaughterhouseServer.trays.get(trayId);

        if (tray != null) {
            responseObserver.onNext(tray);
        } else {
            responseObserver.onError(new Exception("Tray not found"));
        }
        responseObserver.onCompleted();
    }
}