
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

}
