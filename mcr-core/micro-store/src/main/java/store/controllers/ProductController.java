package store.controllers;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import store.controllers.ResouceAssembler.ProductResourceAssembler;
import store.controllers.exceptions.ResourceNotFoundException;
import store.models.Category;
import store.models.Product;
import store.services.CategoryService;
import store.services.ProductService;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class ProductController {

    private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;
    private final ProductResourceAssembler productResourceAssembler;
    private final CategoryService categoryService;

    @Autowired
    public ProductController(ProductService productService,
                             ProductResourceAssembler productResourceAssembler,
                             CategoryService categoryService) {
        this.productService = productService;
        this.productResourceAssembler = productResourceAssembler;
        this.categoryService = categoryService;
    }

    @GetMapping(value = {"/", ""})
    public ResponseEntity<Resources<Resource<Product>>> getAllProducts() {
        List<Product> products = productService.findAllProducts();
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("Products");
        }
        final List<Resource<Product>> resources = products
                .stream()
                .map(productResourceAssembler::toResource)
                .collect(Collectors.toList());
        final Resources<Resource<Product>> response = new Resources<>(resources);
        LOG.info("Found: - {}", products);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/category")
    public ResponseEntity<Resources<Resource<Product>>> getAllProductByCategory(@RequestParam ObjectId id) {
        List<Product> products = productService.findAllProductsByCategory(id);
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("Products", "Category", id);
        }
        final List<Resource<Product>> resources = products
                .stream()
                .map(productResourceAssembler::toResource)
                .collect(Collectors.toList());
        Resources<Resource<Product>> response = new Resources<>(resources);
        LOG.info("Found: - [{}]", products);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/pages")
    public ResponseEntity<Page<Product>> findAllProductsWithPage
            (@RequestParam int page, @RequestParam int size) {
        Page<Product> products = productService.findAllProductsWithPages(page, size);
        if (products.getTotalElements() == 0) {
            throw new ResourceNotFoundException("Product");
        }
        final PageResourceSupport<Product> response =
                new PageResourceSupport<>(products, "page", "size");
        LOG.info("Found: - [{}, Page: - {}, Size: - {}].", products, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/pages/category")
    public ResponseEntity<Page<Product>> findAllProductsByCategoryWithPage(@RequestParam ObjectId id,
                                                                           @RequestParam int page,
                                                                           @RequestParam int size) {
        Page<Product> products = productService.findAllProductsByCategoryWithPages(id, page, size);
        if (products.getTotalElements() == 0) {
            throw new ResourceNotFoundException("Product");
        }
        final PageResourceSupport<Product> response = new PageResourceSupport<>(products,
                "page",
                "size");
        LOG.info("Found: - [{}, Page: - {}, Size: - {}]", products, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{productId}")
    public ResponseEntity<Resource<Product>> getProductById(@PathVariable ObjectId productId) {
        Product product = productService.findById(productId);
        if (product == null) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
        LOG.info("Found: - {}", product);
        return ResponseEntity.created(linkTo(methodOn(ProductController.class)
                .getProductById(productId)).toUri())
                .body(productResourceAssembler.toResource(product));
    }

    @PostMapping(value = "/")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Category c = product.getCategory();
        if (!categoryService.findAllCategories().contains(c))
            categoryService.save(c);
        Product p = productService.save(product);
        LOG.info("Created: - {}", p);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/{productId}")
    public ResponseEntity<Product> deleteProduct(@PathVariable ObjectId productId) {
        productService.delete(productId);
        LOG.info("Deleted: - Product: - {}", productId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /*@PutMapping(value = "/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable ObjectId productId,
                                                 @RequestBody Product product) {
        return new ResponseEntity<>(HttpStatus.OK);
    }*/
}
