package zusatz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class MongoDbRest implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(MongoDbRest.class, args);
    }

    @Autowired
    PersonRepository personRepository;

    @Override
    public void run(String... args) throws Exception {
        initDB();
    }

    private void initDB() {
        if (personRepository.findAll().isEmpty()) {
            List<String> firstNames = Arrays.asList("Alex", "Jared", "Florian", "Julia", "Laura");
            List<String> lastNames = Arrays.asList("Nguyen", "Tran", "Dang", "Chu", "Le");
            for (String firstName : firstNames) {
                for (String lastName : lastNames) {
                    personRepository.save(new Person(firstName, lastName));
                }
            }
        } else {
            System.out.println("Database initialized.");
        }

    }
}
