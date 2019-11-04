package order.clients;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.PagedResources;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static order.MyRestTemplate.getRestTemplate;

@Component
public class StoreClient {
    private static final Logger LOG = LoggerFactory.getLogger(StoreClient.class);

    public static class ProductPagedResources extends PagedResources<Product> {

    }

    private RestTemplate restTemplate;
    private String storeServiceHost;
    private long storeServicePort;

    @Autowired
    public StoreClient(
            @Value("${store.services.host:store}") String storeServiceHost,
            @Value("${store.services.port:8080}") long storeServicePort) {
        this.restTemplate = getRestTemplate();
        this.storeServiceHost = storeServiceHost;
        this.storeServicePort = storeServicePort;
    }

    public List<Product> findAllProducts() {
        PagedResources<Product> pagedResources = restTemplate.getForObject(
                storeURL(), ProductPagedResources.class);
        return new ArrayList<>(pagedResources.getContent());
    }

    private String storeURL() {
        String url = String.format("http://%s:%s/", storeServiceHost, storeServicePort);
        LOG.trace("Store: URL {} ", url);
        return url;
    }

    public Product findProductById(ObjectId productId) {
        Product p = restTemplate.getForObject(storeURL() + productId, Product.class);
        LOG.info("Found: - {}", p);
        return p;
    }

    public double getPrice(ObjectId productId) {
        return findProductById(productId).getPrice();
    }
}
