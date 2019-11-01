package order.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class Form {
    private ObjectId customerId;
    private List<OrderLine> orderLine;

    public Form() {
        this.orderLine = new ArrayList<>();
    }

    @JsonSerialize(using = ObjectId_Serializer.class)
    public ObjectId getCustomerId() {
        return customerId;
    }

    public void setCustomerId(ObjectId customerId) {
        this.customerId = customerId;
    }

    public List<OrderLine> getOrderLine() {
        return orderLine;
    }

    public void setOrderLine(List<OrderLine> orderLine) {
        this.orderLine = orderLine;
    }

    public void addFormLine(int amount, ObjectId productId) {
        this.orderLine.add(new OrderLine(amount, productId));
    }

    private static class OrderLine {
        private ObjectId productId;
        private int amount;

        public OrderLine(int amount, ObjectId productId) {
            this.productId = productId;
            this.amount = amount;
        }

        @JsonSerialize(using = ObjectId_Serializer.class)
        public ObjectId getProductId() {
            return productId;
        }

        public void setProductId(ObjectId productId) {
            this.productId = productId;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }
}
