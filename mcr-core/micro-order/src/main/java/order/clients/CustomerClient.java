package order.clients;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static order.clients.MyRestTemplate.getRestTemplate;

@Component
public class CustomerClient {
    private static final Logger LOG = LoggerFactory.getLogger(CustomerClient.class);

    private RestTemplate restTemplate;
    private String customerServiceHost;
    private long customerServicePort;

    static class CustomerPagedResources extends PagedResources<Customer> {

    }

    @Autowired
    public CustomerClient(
            @Value("${customer.services.host:customer}") String customerServiceHost,
            @Value("${customer.services.port:8080}") long customerServicePort) {
        this.restTemplate = getRestTemplate();
        this.customerServiceHost = customerServiceHost;
        this.customerServicePort = customerServicePort;
    }

    public boolean isValidCustomerId(ObjectId customerId) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> entity = restTemplate.getForEntity(
                    customerURL() + customerId, String.class);
            return entity.getStatusCode().is2xxSuccessful();
        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404)
                return false;
            else
                throw e;
        }
    }

    public List<Customer> findAllCustomers() {
        PagedResources<Customer> pagedResources = getRestTemplate()
                .getForObject(customerURL(), CustomerPagedResources.class);
        return new ArrayList<>(pagedResources.getContent());
    }

    private String customerURL() {
        String url = String.format("http://%s:%s/", customerServiceHost, customerServicePort);
        LOG.trace("Customer: URL {} ", url);
        return url;
    }

    /*public Customer findCustomerById(ObjectId customerId) {
        return restTemplate.getForObject(customerURL() + customerId,
                Customer.class);
    }*/
}
