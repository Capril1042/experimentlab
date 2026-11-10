
package com.experimentlab;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ExperimentlabApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JsonMapper jsonMapper;

	@Test
	void contextLoads() {
	}

	@Test
	void createVariantSuccessfully() throws Exception {

		// Step 1: Create a demo session.
		MvcResult sessionResult = mockMvc.perform(
						get("/api/demo/session")
				)
				.andExpect(status().isOk())
				.andReturn();

		Cookie sessionCookie = sessionResult
				.getResponse()
				.getCookie("DEMO_SESSION_ID");

		// Step 2: Create an experiment.
		String experimentRequest = """
                {
                    "name": "Homepage Button Test",
                    "description": "Testing button variations",
                    "hypothesis": "A green button will increase conversions",
                    "primaryMetric": "CONVERSION_RATE"
                }
                """;

		MvcResult experimentResult = mockMvc.perform(
						post("/api/experiments")
								.cookie(sessionCookie)
								.contentType(MediaType.APPLICATION_JSON)
								.content(experimentRequest)
				)
				.andExpect(status().isCreated())
				.andReturn();

		JsonNode experimentJson = jsonMapper.readTree(
				experimentResult.getResponse().getContentAsString()
		);

		String experimentId = experimentJson.get("id").asText();

		// Step 3: Create a variant.
		String variantRequest = """
                {
                    "name": "Green Button",
                    "description": "Button with green background",
                    "configuration": {
                        "buttonText": "Get Started",
                        "backgroundColor": "#22c55e",
                        "fontSize": 16
                    },
                    "trafficPercentage": 50.00
                }
                """;

		MvcResult variantResult = mockMvc.perform(
						post("/api/experiments/" + experimentId + "/variants")
								.cookie(sessionCookie)
								.contentType(MediaType.APPLICATION_JSON)
								.content(variantRequest)
				)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("Green Button"))
				.andExpect(jsonPath("$.experimentId").value(experimentId))
				.andExpect(jsonPath("$.trafficPercentage").value(50.00))
				.andExpect(jsonPath("$.configuration.buttonText").value("Get Started"))
				.andExpect(jsonPath("$.configuration.backgroundColor").value("#22c55e"))
				.andReturn();

		JsonNode variantJson = jsonMapper.readTree(
				variantResult.getResponse().getContentAsString()
		);

		String variantId = variantJson.get("id").asText();

		// Step 4: Retrieve variants.
		mockMvc.perform(
						get("/api/experiments/" + experimentId + "/variants")
								.cookie(sessionCookie)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name").value("Green Button"))
				.andExpect(jsonPath("$[0].configuration.buttonText").value("Get Started"));

		// Step 5: Update the variant.
		String updateRequest = """
                {
                    "name": "Blue Button",
                    "description": "Updated button variation",
                    "configuration": {
                        "buttonText": "Join Now",
                        "backgroundColor": "#3b82f6",
                        "fontSize": 18
                    },
                    "trafficPercentage": 50.00
                }
                """;

		mockMvc.perform(
						put("/api/experiments/" + experimentId + "/variants/" + variantId)
								.cookie(sessionCookie)
								.contentType(MediaType.APPLICATION_JSON)
								.content(updateRequest)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Blue Button"))
				.andExpect(jsonPath("$.configuration.buttonText").value("Join Now"))
				.andExpect(jsonPath("$.configuration.backgroundColor").value("#3b82f6"));

		// Step 6: Delete the variant.
		mockMvc.perform(
						delete("/api/experiments/" + experimentId + "/variants/" + variantId)
								.cookie(sessionCookie)
				)
				.andExpect(status().isNoContent());

// Step 7: Verify the variant is gone.
		mockMvc.perform(
						get("/api/experiments/" + experimentId + "/variants")
								.cookie(sessionCookie)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(0));
	}


	@Test
	void anotherDemoSessionCannotAccessVariants() throws Exception {

		// Create Session A.
		MvcResult sessionAResult = mockMvc.perform(
						get("/api/demo/session")
				)
				.andExpect(status().isOk())
				.andReturn();

		Cookie sessionACookie = sessionAResult
				.getResponse()
				.getCookie("DEMO_SESSION_ID");

		// Create an experiment owned by Session A.
		String experimentRequest = """
            {
                "name": "Private Experiment",
                "description": "Belongs to Session A",
                "hypothesis": "Testing session isolation",
                "primaryMetric": "CONVERSION_RATE"
            }
            """;

		MvcResult experimentResult = mockMvc.perform(
						post("/api/experiments")
								.cookie(sessionACookie)
								.contentType(MediaType.APPLICATION_JSON)
								.content(experimentRequest)
				)
				.andExpect(status().isCreated())
				.andReturn();

		String experimentId = jsonMapper.readTree(
				experimentResult.getResponse().getContentAsString()
		).get("id").asText();

		// Create Session B.
		MvcResult sessionBResult = mockMvc.perform(
						get("/api/demo/session")
				)
				.andExpect(status().isOk())
				.andReturn();

		Cookie sessionBCookie = sessionBResult
				.getResponse()
				.getCookie("DEMO_SESSION_ID");

		// Session B attempts to access Session A's variants.
		mockMvc.perform(
						get("/api/experiments/" + experimentId + "/variants")
								.cookie(sessionBCookie)
				)
				.andExpect(status().isNotFound());
	}

	@Test
	void variantsSupportDifferentConfigurationStructures() throws Exception {

		// Create a demo session.
		MvcResult sessionResult = mockMvc.perform(
						get("/api/demo/session")
				)
				.andExpect(status().isOk())
				.andReturn();

		Cookie sessionCookie = sessionResult
				.getResponse()
				.getCookie("DEMO_SESSION_ID");

		// Create an experiment.
		String experimentRequest = """
            {
                "name": "Flexible Configuration Test",
                "description": "Testing different configuration types",
                "hypothesis": "Different configurations can be stored",
                "primaryMetric": "CONVERSION_RATE"
            }
            """;

		MvcResult experimentResult = mockMvc.perform(
						post("/api/experiments")
								.cookie(sessionCookie)
								.contentType(MediaType.APPLICATION_JSON)
								.content(experimentRequest)
				)
				.andExpect(status().isCreated())
				.andReturn();

		String experimentId = jsonMapper.readTree(
				experimentResult.getResponse().getContentAsString()
		).get("id").asText();

		// Create a headline variant.
		String headlineConfiguration = """
            {
                "name": "Headline Variation",
                "description": "Alternative headline",
                "configuration": {
                    "headline": "Start your free trial",
                    "fontSize": 32,
                    "textColor": "#ffffff"
                },
                "trafficPercentage": 50.00
            }
            """;

		mockMvc.perform(
						post("/api/experiments/" + experimentId + "/variants")
								.cookie(sessionCookie)
								.contentType(MediaType.APPLICATION_JSON)
								.content(headlineConfiguration)
				)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.configuration.headline")
						.value("Start your free trial"))
				.andExpect(jsonPath("$.configuration.fontSize").value(32));

		// Create a pricing variant.
		String pricingConfiguration = """
            {
                "name": "Pricing Variation",
                "description": "Discounted monthly pricing",
                "configuration": {
                    "monthlyPrice": 19.99,
                    "discountPercentage": 20,
                    "billingPeriod": "monthly"
                },
                "trafficPercentage": 50.00
            }
            """;

		mockMvc.perform(
						post("/api/experiments/" + experimentId + "/variants")
								.cookie(sessionCookie)
								.contentType(MediaType.APPLICATION_JSON)
								.content(pricingConfiguration)
				)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.configuration.monthlyPrice").value(19.99))
				.andExpect(jsonPath("$.configuration.discountPercentage").value(20))
				.andExpect(jsonPath("$.configuration.billingPeriod").value("monthly"));
	}
	@Test
	void createVariantWithEmptyConfigurationReturnsBadRequest() throws Exception {
		MvcResult sessionResult = mockMvc.perform(get("/api/demo/session"))
				.andExpect(status().isOk())
				.andReturn();

		Cookie sessionCookie = sessionResult.getResponse().getCookie("DEMO_SESSION_ID");

		MvcResult experimentResult = mockMvc.perform(post("/api/experiments")
						.cookie(sessionCookie)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
                            {
                                "name": "Configuration Validation Test",
                                "description": "Testing empty configuration",
                                "hypothesis": "Validation should reject empty configurations",
                                "primaryMetric": "CONVERSION_RATE"
                            }
                            """))
				.andExpect(status().isCreated())
				.andReturn();

		String experimentId = JsonMapper.builder()
				.build()
				.readTree(experimentResult.getResponse().getContentAsString())
				.get("id")
				.asString();

		mockMvc.perform(post("/api/experiments/" + experimentId + "/variants")
						.cookie(sessionCookie)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
                            {
                                "name": "Empty Configuration",
                                "description": "This should be rejected",
                                "configuration": {},
                                "trafficPercentage": 100
                            }
                            """))
				.andExpect(status().isBadRequest());
	}

	@Test
	void cannotStartExperimentWhenTrafficDoesNotTotal100Percent() throws Exception {

		// Create a demo session.
		MvcResult sessionResult = mockMvc.perform(
						get("/api/demo/session")
				)
				.andExpect(status().isOk())
				.andReturn();

		Cookie sessionCookie = sessionResult.getResponse()
				.getCookie("DEMO_SESSION_ID");

		// Create an experiment.
		MvcResult experimentResult = mockMvc.perform(
						post("/api/experiments")
								.cookie(sessionCookie)
								.contentType(MediaType.APPLICATION_JSON)
								.content("""
                            {
                                "name": "Traffic Allocation Test",
                                "description": "Testing traffic validation",
                                "hypothesis": "Traffic must total 100%",
                                "primaryMetric": "CONVERSION_RATE"
                            }
                            """)
				)
				.andExpect(status().isCreated())
				.andReturn();

		String experimentId = jsonMapper.readTree(
				experimentResult.getResponse().getContentAsString()
		).get("id").asString();

		// Create the first variant with 40% traffic.
		mockMvc.perform(
				post("/api/experiments/" + experimentId + "/variants")
						.cookie(sessionCookie)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
                            {
                                "name": "Control",
                                "description": "Control variant",
                                "configuration": {
                                    "buttonText": "Get Started"
                                },
                                "trafficPercentage": 40
                            }
                            """)
		).andExpect(status().isCreated());

		// Create the second variant with 40% traffic.
		mockMvc.perform(
				post("/api/experiments/" + experimentId + "/variants")
						.cookie(sessionCookie)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
                            {
                                "name": "Treatment",
                                "description": "Treatment variant",
                                "configuration": {
                                    "buttonText": "Join Now"
                                },
                                "trafficPercentage": 40
                            }
                            """)
		).andExpect(status().isCreated());

		// Attempt to start with only 80% allocated.
		mockMvc.perform(
				post("/api/experiments/" + experimentId + "/start")
						.cookie(sessionCookie)
		).andExpect(status().isBadRequest());
	}


	@Test
	void startingExperimentLocksVariantConfiguration() throws Exception {

		// Create a demo session.
		MvcResult sessionResult = mockMvc.perform(
						get("/api/demo/session")
				)
				.andExpect(status().isOk())
				.andReturn();

		Cookie sessionCookie = sessionResult.getResponse()
				.getCookie("DEMO_SESSION_ID");

		// Create an experiment.
		MvcResult experimentResult = mockMvc.perform(
						post("/api/experiments")
								.cookie(sessionCookie)
								.contentType(MediaType.APPLICATION_JSON)
								.content("""
                            {
                                "name": "Configuration Lock Test",
                                "description": "Testing configuration locking",
                                "hypothesis": "Started experiments cannot be modified",
                                "primaryMetric": "CONVERSION_RATE"
                            }
                            """)
				)
				.andExpect(status().isCreated())
				.andReturn();

		String experimentId = jsonMapper.readTree(
				experimentResult.getResponse().getContentAsString()
		).get("id").asString();

		// Create Control variant.
		MvcResult controlResult = mockMvc.perform(
						post("/api/experiments/" + experimentId + "/variants")
								.cookie(sessionCookie)
								.contentType(MediaType.APPLICATION_JSON)
								.content("""
                            {
                                "name": "Control",
                                "description": "Original experience",
                                "configuration": {
                                    "buttonText": "Get Started"
                                },
                                "trafficPercentage": 50
                            }
                            """)
				)
				.andExpect(status().isCreated())
				.andReturn();

		String controlId = jsonMapper.readTree(
				controlResult.getResponse().getContentAsString()
		).get("id").asString();

		// Create Treatment variant.
		mockMvc.perform(
				post("/api/experiments/" + experimentId + "/variants")
						.cookie(sessionCookie)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
                            {
                                "name": "Treatment",
                                "description": "New experience",
                                "configuration": {
                                    "buttonText": "Join Now"
                                },
                                "trafficPercentage": 50
                            }
                            """)
		).andExpect(status().isCreated());

		// Start experiment.
		mockMvc.perform(
						post("/api/experiments/" + experimentId + "/start")
								.cookie(sessionCookie)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("RUNNING"));

		// Attempt to create another variant.
		mockMvc.perform(
				post("/api/experiments/" + experimentId + "/variants")
						.cookie(sessionCookie)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
                            {
                                "name": "Third Variant",
                                "description": "Should be rejected",
                                "configuration": {
                                    "buttonText": "Try Now"
                                },
                                "trafficPercentage": 10
                            }
                            """)
		).andExpect(status().isConflict());

		// Attempt to update Control.
		mockMvc.perform(
				put("/api/experiments/" + experimentId + "/variants/" + controlId)
						.cookie(sessionCookie)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
                            {
                                "name": "Updated Control",
                                "description": "Should be rejected",
                                "configuration": {
                                    "buttonText": "Updated"
                                },
                                "trafficPercentage": 50
                            }
                            """)
		).andExpect(status().isConflict());

		// Attempt to delete Control.
		mockMvc.perform(
				delete("/api/experiments/" + experimentId + "/variants/" + controlId)
						.cookie(sessionCookie)
		).andExpect(status().isConflict());
	}


}
