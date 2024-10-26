import com.example.grpc.SlaughterhouseProto.Animal;
import com.example.grpc.SlaughterhouseProto.Product;
import io.grpc.stub.StreamObserver;
import org.example.SlaughterhouseServer;
import org.example.SlaughterhouseServer.SlaughterhouseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class SlaughterHouseTest {

    @BeforeEach
    public void setUp() throws SQLException, IOException {
        SlaughterhouseServer.initializeData();
    }

    @Test
    public void testInitializeData() {

        assertEquals(8, SlaughterhouseServer.animals.size(), "Animals map should contain 8 entries");


        assertNotNull(SlaughterhouseServer.animals.get(1), "Animal with ID 1 should be present");
        assertEquals(":Cow", SlaughterhouseServer.animals.get(1).getSpecies(), "Animal with ID 1 should be a Cow");


        assertEquals(2, SlaughterhouseServer.products.size(), "Products map should contain 2 entries");


        assertNotNull(SlaughterhouseServer.products.get(1), "Product with ID 1 should be present");
        assertEquals(1, SlaughterhouseServer.products.get(1).getAnimalIds(0), "Product with ID 1 should be associated with Animal ID 1");
    }

    @Test
    public void testGetAnimalInfo() throws InterruptedException {
        SlaughterhouseServiceImpl service = new SlaughterhouseServiceImpl();
        Product request = Product.newBuilder().addAnimalIds(1).build();
        CountDownLatch latch = new CountDownLatch(1);

        service.getAnimalInfo(request, new StreamObserver<Animal>() {
            @Override
            public void onNext(Animal animal) {
                assertNotNull(animal, "Animal should not be null");
                assertEquals(1, animal.getId(), "Animal ID should be 1");
                assertEquals(":Cow", animal.getSpecies(), "Animal species should be Cow");
            }

            @Override
            public void onError(Throwable t) {
                fail("Should not have thrown an error");
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        assertTrue(latch.await(1, TimeUnit.SECONDS), "The response should be received within 1 second");
    }

    @Test
    public void testGetProductInfo() throws InterruptedException {
        SlaughterhouseServiceImpl service = new SlaughterhouseServiceImpl();
        Animal request = Animal.newBuilder().setId(1).build();
        CountDownLatch latch = new CountDownLatch(1);

        service.getProductInfo(request, new StreamObserver<Product>() {
            @Override
            public void onNext(Product product) {
                assertNotNull(product, "Product should not be null");
                assertEquals(1, product.getId(), "Product ID should be 1");
                assertTrue(product.getAnimalIdsList().contains(1), "Product should be associated with Animal ID 1");
            }

            @Override
            public void onError(Throwable t) {
                fail("Should not have thrown an error");
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        assertTrue(latch.await(1, TimeUnit.SECONDS), "The response should be received within 1 second");
    }
}
