package edu.java.bot.controller;

import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.service.BotService;
import edu.java.models.dto.LinkUpdate;
import edu.java.models.dto.response.ApiErrorResponse;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/updates")
public class BotController {
    private final BotService botService;
    private final Bucket bucket;

    @Autowired
    public BotController(BotService botService, ApplicationConfig config) {
        this.botService = botService;
        bucket = Bucket.builder()
            .addLimit(
                Bandwidth
                    .classic(config.timeRateConfig().capacity(),
                        Refill.intervally(config.timeRateConfig().tokens(), config.timeRateConfig().duration())
                    )
            ).build();
    }

    @Operation(summary = "Отправить обновление")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Обновление обработано"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Чат не существует",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<Void> sendUpdates(@RequestBody LinkUpdate linkUpdate) {
        if (bucket.tryConsume(1)) {
            log.info("Process request {} on /updates POST", linkUpdate);
            botService.sendUpdatesInfo(linkUpdate);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }
}
