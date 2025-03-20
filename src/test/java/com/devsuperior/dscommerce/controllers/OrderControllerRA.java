package com.devsuperior.dscommerce.controllers;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dscommerce.tests.TokenUtil;

import io.restassured.http.ContentType;

public class OrderControllerRA {

	private String clientUsername, clientPassword, adminUsername, adminPassword;
	private String clientToken, adminToken, invalidToken;
	private Long existingOrderId, nonExistingOrderId;

	@BeforeEach
	public void setUp() {
		baseURI = "http://localhost:8080";
		
		existingOrderId = 1L;
		nonExistingOrderId = 100L;

		clientUsername = "maria@gmail.com";
		clientPassword = "123456";
		adminUsername = "alex@gmail.com";
		adminPassword = "123456";

		clientToken = TokenUtil.obtainAcessToken(clientUsername, clientPassword);
		adminToken = TokenUtil.obtainAcessToken(adminUsername, adminPassword);
		invalidToken = adminToken + "xpto"; // Invalid token
	}
	
	@Test
	public void findByIdShouldReturnOrderWhenIdExistsAndAdminLogged() {
		
		given()
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + adminToken)
			.accept(ContentType.JSON)
		.when()
			.get("/orders/{id}", existingOrderId)
		.then()
			.statusCode(200)
			.body("id", is(1))
			.body("moment", equalTo("2022-07-25T13:00:00Z"))
			.body("status", equalTo("PAID"))
			.body("client.name", equalTo( "Maria Brown"))
			.body("payment.moment", equalTo("2022-07-25T15:00:00Z"))
			.body("items.name", hasItems("The Lord of the Rings", "Macbook Pro"))
			.body("total", is(1431.0F));
	}
	
	@Test
	public void findByIdShouldReturnOrderWhenIdExistsAndClientLogged() {
		
		given()
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + clientToken)
			.accept(ContentType.JSON)
		.when()
			.get("/orders/{id}", existingOrderId)
		.then()
			.statusCode(200)
			.body("id", is(1))
			.body("moment", equalTo("2022-07-25T13:00:00Z"))
			.body("status", equalTo("PAID"))
			.body("client.name", equalTo( "Maria Brown"))
			.body("payment.moment", equalTo("2022-07-25T15:00:00Z"))
			.body("items.name", hasItems("The Lord of the Rings", "Macbook Pro"))
			.body("total", is(1431.0F));
	}
	
	@Test
	public void findByIdShouldReturnForbiddenWhenIdExistsAndClientLoggedAndOrderDoesNotBelongUser() {
		Long otherOrderId = 2L;
		
		given()
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + clientToken)
			.accept(ContentType.JSON)
		.when()
			.get("/orders/{id}", otherOrderId)
		.then()
			.statusCode(403);
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExistAndAdminLogged() {
		
		given()
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + adminToken)
			.accept(ContentType.JSON)
		.when()
			.get("/orders/{id}", nonExistingOrderId)
		.then()
			.statusCode(404);
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExistAndClientLogged() {
		
		given()
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + clientToken)
			.accept(ContentType.JSON)
		.when()
			.get("/orders/{id}", nonExistingOrderId)
		.then()
			.statusCode(404);
	}
	
	@Test
	public void findByIdShouldReturnUnauthorizedWhenIdExistsAndInvalidToken() {
		
		given()
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + invalidToken)
			.accept(ContentType.JSON)
		.when()
			.get("/orders/{id}", existingOrderId)
		.then()
			.statusCode(401);
	}
}
