package order.controllers;

import order.clients.Customer;
import order.clients.CustomerClient;
import order.clients.Product;
import order.clients.StoreClient;
import order.controllers.exceptions.ResourceNotFoundException;
import order.models.Form;
import order.models.Order;
import order.models.OrderLine;
import order.services.OrderService;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class OrderController {
    private static final Logger LOG = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;
    private final CustomerClient customerClient;
    private final StoreClient storeClient;
    private final OrderResourceAssembler orderResourceAssembler;

    @Autowired
    public OrderController(OrderService orderService,
                           CustomerClient customerClient,
                           StoreClient storeClient,
                           OrderResourceAssembler orderResourceAssembler) {
        this.orderService = orderService;
        this.customerClient = customerClient;
        this.storeClient = storeClient;
        this.orderResourceAssembler = orderResourceAssembler;
    }

    @GetMapping(value = {"/", ""})
    public ResponseEntity<Resources<Resource<Order>>> getAllOrders() {
        List<Order> orders = orderService.findAllOrders();
        if (orders.isEmpty())
            throw new ResourceNotFoundException("Order");
        final List<Resource<Order>> resources = orders
                .stream()
                .map(orderResourceAssembler::toResource)
                .collect(Collectors.toList());
        final Resources<Resource<Order>> response = new Resources<>(resources);
        LOG.info("Found: - {}", orders);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{orderId}")
    public ResponseEntity<Resource<Order>> getOrderById(@PathVariable ObjectId orderId) {
        Order order = orderService.findOrderById(orderId);
        if (order == null)
            throw new ResourceNotFoundException("Order", "id", orderId);
        LOG.info("Found: - {}", order);
        return ResponseEntity.created(linkTo(methodOn(OrderController.class)
                .getOrderById(orderId)).toUri())
                .body(orderResourceAssembler.toResource(order));
    }

    @GetMapping(value = "/customer/{customerId}")
    public ResponseEntity<Resources<Resource<Order>>> getOrdersByCustomer(@PathVariable ObjectId customerId) {
        List<Order> orders = orderService.findOrdersByCustomerId(customerId);
        if (orders.isEmpty())
            throw new ResourceNotFoundException("Order", "customerId", customerId);
        final List<Resource<Order>> resources = orders
                .stream()
                .map(orderResourceAssembler::toResource)
                .collect(Collectors.toList());
        final Resources<Resource<Order>> response = new Resources<>(resources);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order o = orderService.create(order);
        LOG.info("Created: - {}", o);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/{orderId}/complete")
    public ResponseEntity<Order> completeOrder(@PathVariable ObjectId orderId) {
        Order o = orderService.findOrderById(orderId);
        orderService.complete(o);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/init")
    public ResponseEntity<Form> initOrder() {
        List<Product> products = storeClient.findAllProducts();
        List<Customer> customers = customerClient.findAllCustomers();
        Form f = new Form();
        f.setCustomerId(customers.get(0).getCustomerId());
        Random index = new Random();
        for (int i = 0; i < 5; i++) {
            f.addFormLine(1 + index.nextInt(9),
                    products.get(index.nextInt(products.size())).getProductId());
        }
        return ResponseEntity.ok(f);
    }
}
