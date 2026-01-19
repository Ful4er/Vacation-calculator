package org.example.vacationcalculator;

import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;
import okhttp3.Headers;
import org.example.vacationcalculator.exception.ExternalServiceException;
import org.example.vacationcalculator.service.impl.CalendarApiServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CalendarApiServiceTest {

    private MockWebServer mockWebServer;
    private CalendarApiServiceImpl service;

    private final Headers headers = new Headers.Builder()
            .add("Content-Type", "application/json")
            .build();

    @BeforeEach
    void setup() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        service = new CalendarApiServiceImpl(webClient);
    }

    @AfterEach
    void tearDown() {
        mockWebServer.close();
    }

    @Test
    void isWorkingDay_true() {
        mockWebServer.enqueue(
                new MockResponse(200, headers, "{\"isWorkingDay\":true}")
        );

        StepVerifier.create(service.isWorkingDay(LocalDate.of(2026, 1, 17)))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void isWorkingDay_false() {
        mockWebServer.enqueue(
                new MockResponse(200, headers, "{\"isWorkingDay\":false}")
        );

        StepVerifier.create(service.isWorkingDay(LocalDate.now()))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void isWorkingDay_externalServiceError_throwsExternalServiceException() {
        mockWebServer.enqueue(
                new MockResponse(500, headers, "{\"isWorkingDay\":true}")
        );

        StepVerifier.create(service.isWorkingDay(LocalDate.now()))
                .expectErrorMatches(ex ->
                        ex instanceof ExternalServiceException)
                .verify();
    }

    @Test
    void httpRequestPath_isCorrect() throws InterruptedException {
        mockWebServer.enqueue(
                new MockResponse(200, headers, "{\"isWorkingDay\":true}")
        );

        service.isWorkingDay(LocalDate.of(2026, 2, 3)).block();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getUrl().encodedPath()).contains("/2026/2/3");
    }
}
