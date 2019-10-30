package customer;

import customer.models.Customer;
import customer.services.CustomerServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class CustomerApplication implements CommandLineRunner {

    @Autowired
    private CustomerServices customerServices;

    public static void main(String[] args) {
        SpringApplication.run(CustomerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        initDatabase();
    }

    private void initDatabase() {
        Customer c = new Customer();
        c.setName("Alex");
        c.setEmail("abc@xyz.de");
        c.setAddress("HTWK");
        c.setCity("Leipzig");

        customerServices.save(c);
    }
}
