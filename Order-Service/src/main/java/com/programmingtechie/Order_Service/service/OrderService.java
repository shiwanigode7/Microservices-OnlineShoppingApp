package com.programmingtechie.Order_Service.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.programmingtechie.Order_Service.dto.InventoryResponse;
import com.programmingtechie.Order_Service.dto.OrderLineItemsDto;
import com.programmingtechie.Order_Service.dto.OrderRequest;
import com.programmingtechie.Order_Service.model.Order;
import com.programmingtechie.Order_Service.model.OrderLineItems;
import com.programmingtechie.Order_Service.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

	private final OrderRepository orderRepository;
	private final WebClient webClient;

	public void placeOrder(OrderRequest orderRequest) {
		Order order = new Order();
		order.setOrderNumber(UUID.randomUUID().toString());

		List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList().stream().map(this::mapToDto)
				.toList();

		order.setOrderLineItemsList(orderLineItems);
		
		List<String> skuCodes = order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();
		
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost:8082/api/inventory");
		for (String skuCode : skuCodes) {
		    uriBuilder.queryParam("skuCode", skuCode);
		}

		// call inventory service, and place order if product is in stock
		InventoryResponse[] inventoryResponseArray = webClient
				.get()
				.uri(uriBuilder.build().toUri())
				.retrieve().bodyToMono(InventoryResponse[].class)
				.block();

		boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);

		if (allProductsInStock) {
			orderRepository.save(order);
		} else {
			throw new IllegalArgumentException("Product is not in stock, please try again later");
		}
	}

	private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
		OrderLineItems orderLineItems = new OrderLineItems();
		orderLineItems.setPrice(orderLineItemsDto.getPrice());
		orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
		orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
		return orderLineItems;
	}
}
