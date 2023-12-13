package com.rewe.sendemail.controllers;

//import com.gothub.dto.RepositoryDto;
//import com.gothub.service.RepositoriesService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.rewe.sendemail.models.Email;
import com.rewe.sendemail.services.ProducerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RequestMapping("/api/emails")
@RestController
@Slf4j
public class SendEmailController {
    private final ProducerService producerService;

//    @Operation(summary = "Gets a list of repositories with optional filters")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Found repositories",
//                    content = { @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = List.class))
//            }),
//            @ApiResponse(responseCode = "400", description = "Invalid parameter supplied",
//                    content = @Content)
//            })
    @GetMapping(path = "/send", produces = {"application/json"})
    public ResponseEntity<List<String>> sendRandom() {
        log.info("GET /repositories triggered");
        List<String> domains = List.of("@gmail.com", "@yahoo.com", "@me.com");
        List<String> addresses = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            String from = UUID.randomUUID() + domains.get((int) (Math.random() * 3));
            addresses.add(from);

            Email email = Email.builder()
                    .withSubject("need help")
                    .withContent("Hello from me")
                    .withTo(List.of("support@rewe.de"))
                    .withFrom(from)
                    .build();
            producerService.sendMessage(email);
        }
        return ResponseEntity.ok(addresses);
    }

}
