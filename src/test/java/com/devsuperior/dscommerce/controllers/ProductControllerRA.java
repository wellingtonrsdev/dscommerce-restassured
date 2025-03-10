package com.devsuperior.dscommerce.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProductControllerRA {

	private Long existingProductId, nonExistingProductId;
	
	@BeforeEach
	public void setUp() {
		baseURI = "http://localhost:8080";
	}
	
	@Test
	public void findByIdShouldReturnProductWhenIdExists() {
		existingProductId = 2L;
		
		given()
			.get("/products/{id}", existingProductId)
		.then()
			.statusCode(200)
			.body("id", is(2))
			.body("name", equalTo("Smart TV"))
			.body("imgUrl", equalTo( "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/2-big.jpg"))
			.body("price", is(2190.0F))
			.body("categories.id", hasItems(2, 3))
			.body("categories.name", hasItems("Eletrônicos", "Computadores"));
	}
}
