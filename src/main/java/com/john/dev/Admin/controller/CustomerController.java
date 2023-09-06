package com.john.dev.Admin.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.john.dev.Admin.entity.Customer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import java.util.HashMap;
import java.util.Map;


@Controller
public class CustomerController {

    private final RestTemplate restTemplate;
    private final String apiUrl = "https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=get_customer_list";

    @Autowired
    public CustomerController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/dashboard")
    public String customerlist(Model model, HttpServletRequest request) throws JsonProcessingException {

        HttpSession session = request.getSession();

        String jsonToken = (String)session.getAttribute("token");

        if (jsonToken != null) {

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonToken);

            // Extract the "access_token" value
            String accessToken = jsonNode.get("access_token").asText();

            HttpHeaders headers = new HttpHeaders();

            headers.set("Authorization", "Bearer " +accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Customer[]> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Customer[].class);

            Customer[] customers = responseEntity.getBody();

            model.addAttribute("CustomerList", customers);

            return "CustomerList";
        } else {
            return "redirect:/login";
        }
    }


    @GetMapping("/delete/{uuid}")
    public String deleteCustomer(@PathVariable String uuid, Model model, HttpServletRequest request)  throws JsonProcessingException {
        String jsonToken = (String) request.getSession().getAttribute("token");

        if (jsonToken != null) {

            String accessToken = extractAccessTokenFromJsonToken(jsonToken);
            String apiUrl = "https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=delete&uuid=" + uuid;
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return "redirect:/dashboard";
            } else {

                model.addAttribute("errorMessage", "Failed to delete the customer.");
                return "errorPage"; // Create an errorPage.html for error handling
            }
        } else {
            return "redirect:/login";
        }
    }

    private String extractAccessTokenFromJsonToken(String jsonToken)throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonToken);
        String accessToken = jsonNode.get("access_token").asText();

        return accessToken;
    }


    @GetMapping("/add")
    private String ShowAddCustomerForm(){
        return "addCustomer";
    }

    @PostMapping("/add")
    public String addCustomer(
            @RequestParam String first_name,
            @RequestParam String last_name,
            @RequestParam String street,
            @RequestParam String address,
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam String email,
            @RequestParam String phone,
            Model model,
            HttpServletRequest request
    ) throws JsonProcessingException{
        String jsonToken = (String) request.getSession().getAttribute("token");

        if (jsonToken != null) {
            String accessToken = extractAccessTokenFromJsonToken(jsonToken);
            String apiUrl = "https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=create";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> requestBodyMap = new HashMap<>();
            requestBodyMap.put("first_name", first_name);
            requestBodyMap.put("last_name", last_name);
            requestBodyMap.put("street", street);
            requestBodyMap.put("address", address);
            requestBodyMap.put("city", city);
            requestBodyMap.put("state", state);
            requestBodyMap.put("email", email);
            requestBodyMap.put("phone", phone);


            ObjectMapper objectMapper = new ObjectMapper();
            String requestBodyJson;
            try {
                requestBodyJson = objectMapper.writeValueAsString(requestBodyMap);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return "errorPage";
            }

            System.out.println(requestBodyJson);

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyJson, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return "redirect:/dashboard";
            } else {
                model.addAttribute("errorMessage", "Failed to create the customer.");
                return "errorPage";
            }
        } else {

            return "redirect:/login";
        }
    }


    @PostMapping("/update/{uuid}")
    public String updateCustomer(
            @PathVariable String uuid,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String street,
            @RequestParam String address,
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam String email,
            @RequestParam String phone,
            Model model,
            HttpServletRequest request
    )throws JsonProcessingException {
        // Fetch the Bearer token from the session
        String jsonToken = (String) request.getSession().getAttribute("token");

        if (jsonToken != null) {

            String accessToken = extractAccessTokenFromJsonToken(jsonToken);

            String apiUrl = "https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=update&uuid=" + uuid;


            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create a Map to represent the JSON request body
            Map<String, String> requestBodyMap = new HashMap<>();
            requestBodyMap.put("first_name", firstName);
            requestBodyMap.put("last_name", lastName);
            requestBodyMap.put("street", street);
            requestBodyMap.put("address", address);
            requestBodyMap.put("city", city);
            requestBodyMap.put("state", state);
            requestBodyMap.put("email", email);
            requestBodyMap.put("phone", phone);

            ObjectMapper objectMapper = new ObjectMapper();
            String requestBodyJson;
            try {
                requestBodyJson = objectMapper.writeValueAsString(requestBodyMap);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return "errorPage";
            }

            System.out.println(requestBodyJson);

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBodyJson, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class);

            // Check the response status code and handle it accordingly
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return "redirect:/dashboard";
            } else {
                model.addAttribute("errorMessage", "Failed to update the customer.");
                return "errorPage"; // Create an errorPage.html for error handling
            }
        } else {
            return "redirect:/login";
        }
    }


    @GetMapping("/edit/{uuid}")
    public String editCustomerForm(
            @PathVariable String uuid,
            Model model,
            HttpServletRequest request
    ) throws JsonProcessingException {

        String jsonToken = (String) request.getSession().getAttribute("token");

        if (jsonToken != null) {
            model.addAttribute("uuid", uuid); // Add the UUID to the model
            return "updateCustomer";
        } else {
            return "redirect:/login";
        }
    }


}







