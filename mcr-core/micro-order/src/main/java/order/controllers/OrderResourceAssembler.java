package order.controllers;

import order.models.Order;
import order.models.OrderStatus;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class OrderResourceAssembler implements ResourceAssembler<Order, Resource<Order>> {
    @Override
    public Resource<Order> toResource(Order order) {
        Resource<Order> resource = new Resource<>(order,
                linkTo(methodOn(OrderController.class)
                        .getOrderById(order.getOrderId())).withSelfRel(),
                linkTo(methodOn(OrderController.class)
                        .getAllOrders()).withRel("customers"));
        if (order.getStatus() == OrderStatus.IN_PROGRESS) {
            resource.add(linkTo(methodOn(OrderController.class)
                    .completeOrder(order.getOrderId()))
                    .withRel("complete"));
        }

        return resource;
    }
}
