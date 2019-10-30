package store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import store.models.Category;
import store.models.Product;
import store.services.CategoryService;
import store.services.ProductService;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SpringBootApplication
@EnableEurekaClient
public class ProductApplication implements CommandLineRunner {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        initDatabase();
    }

    private void initDatabase() {
        List<String> categoryName = Arrays.asList("Fashion", "Information", "Office");
        List<String> productFashion = Arrays.asList("Jean", "Short", "T-Shirt", "Glass", "Hat");
        List<String> productInfo = Arrays.asList("Macbook", "Thinkpad", "Iphone", "Ipad", "Motherboard");
        List<String> productOffice = Arrays.asList("Pencil", "Paper", "Printer", "Ruler", "Calculator");
        Random r = new Random();
        for (String i : categoryName) {
            Category c = new Category();
            c.setName(i);
            categoryService.save(c);
        }

        List<Category> categories = categoryService.findAllCategories();
        for (Category i : categories) {
            if (i.getName().equals("Fashion")) {
                for (String j : productFashion) {
                    Product p = new Product();
                    p.setCategory(i);
                    p.setDescription("fashion@htwk-leipzig.de");
                    p.setName(j);
                    p.setUnit("Stück");
                    p.setPrice(20 + r.nextDouble()*(80 - 20));
                    productService.save(p);
                }
            } else if (i.getName().equals("Information")) {
                for (String j : productInfo) {
                    Product p = new Product();
                    p.setName(j);
                    p.setUnit("Stück");
                    p.setDescription("info@htwk-leipzig.de");
                    p.setCategory(i);
                    p.setPrice(200 + r.nextDouble()*(1000 - 200));
                    productService.save(p);
                }
            } else {
                for (String j : productOffice) {
                    Product p = new Product();
                    p.setCategory(i);
                    p.setName(j);
                    p.setDescription("Office@htwk-leipzig.de");
                    p.setUnit("Stück");
                    p.setPrice(10 + r.nextDouble()*(30 - 10));
                    productService.save(p);
                }
            }
        }

    }
}
