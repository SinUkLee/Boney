package com.ssafy.boney.domain.account.service;

import com.ssafy.boney.global.config.ExternalApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountAuthService {

    private final WebClient webClient = WebClient.create();
    private final ExternalApiProperties externalApiProperties;

    public Map<String, Object> sendAuthRequest(String accountNo) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");

        String transmissionDate = now.format(dateFormatter);
        String transmissionTime = now.format(timeFormatter);
        String random6 = String.format("%06d", new Random().nextInt(1_000_000));
        String institutionTransactionUniqueNo = transmissionDate + transmissionTime + random6;

        Map<String, Object> header = Map.of(
                "apiName", externalApiProperties.getAccountAuth().getApiServiceCode(),
                "transmissionDate", transmissionDate,
                "transmissionTime", transmissionTime,
                "institutionCode", externalApiProperties.getAccountAuth().getInstitutionCode(),
                "fintechAppNo", externalApiProperties.getAccountAuth().getFintechAppNo(),
                "apiServiceCode", externalApiProperties.getAccountAuth().getApiServiceCode(),
                "institutionTransactionUniqueNo", institutionTransactionUniqueNo,
                "apiKey", externalApiProperties.getAccountAuth().getApiKey(),
                "userKey", externalApiProperties.getAccountAuth().getUserKey()
        );

        Map<String, Object> body = Map.of(
                "Header", header,
                "accountNo", accountNo,
                "authText", "SSAFY"
        );

        return webClient.post()
                .uri(externalApiProperties.getAccountAuth().getUrlOneCoin())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

    public Map<String, Object> verifyAuthCode(String accountNo, String authCode) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");

        String transmissionDate = now.format(dateFormatter);
        String transmissionTime = now.format(timeFormatter);
        String random6 = String.format("%06d", new Random().nextInt(1_000_000));
        String institutionTransactionUniqueNo = transmissionDate + transmissionTime + random6;

        Map<String, Object> header = Map.of(
                "apiName", "checkAuthCode",
                "transmissionDate", transmissionDate,
                "transmissionTime", transmissionTime,
                "institutionCode", externalApiProperties.getAccountAuth().getInstitutionCode(),
                "fintechAppNo", externalApiProperties.getAccountAuth().getFintechAppNo(),
                "apiServiceCode", "checkAuthCode",
                "institutionTransactionUniqueNo", institutionTransactionUniqueNo,
                "apiKey", externalApiProperties.getAccountAuth().getApiKey(),
                "userKey", externalApiProperties.getAccountAuth().getUserKey()
        );

        Map<String, Object> body = Map.of(
                "Header", header,
                "accountNo", accountNo,
                "authText", "SSAFY",
                "authCode", authCode
        );

        return webClient.post()
                .uri(externalApiProperties.getAccountAuth().getUrlOneCoinCheck())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

    // 계좌 생성 api
    public Map<String, Object> createDemandDepositAccount() {
        LocalDateTime now = LocalDateTime.now();
        String transmissionDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String transmissionTime = now.format(DateTimeFormatter.ofPattern("HHmmss"));
        String random6 = String.format("%06d", new Random().nextInt(1_000_000));
        String institutionTransactionUniqueNo = transmissionDate + transmissionTime + random6;

        Map<String, Object> header = Map.of(
                "apiName", "createDemandDepositAccount",
                "transmissionDate", transmissionDate,
                "transmissionTime", transmissionTime,
                "institutionCode", externalApiProperties.getAccountAuth().getInstitutionCode(),
                "fintechAppNo", externalApiProperties.getAccountAuth().getFintechAppNo(),
                "apiServiceCode", "createDemandDepositAccount",
                "institutionTransactionUniqueNo", institutionTransactionUniqueNo,
                "apiKey", externalApiProperties.getAccountAuth().getApiKey(),
                "userKey", externalApiProperties.getAccountAuth().getUserKey()
        );

        Map<String, Object> body = Map.of(
                "Header", header,
                "accountTypeUniqueNo", externalApiProperties.getAccountAuth().getAccountTypeUniqueNo()
        );

        return webClient.post()
                .uri(externalApiProperties.getAccountAuth().getUrlAccountCreate())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }


    // 입급 api 
    public Map<String, Object> depositToAccount(String accountNo, Long amount) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");

        String transmissionDate = now.format(dateFormatter);
        String transmissionTime = now.format(timeFormatter);
        String random6 = String.format("%06d", new Random().nextInt(1_000_000));
        String institutionTransactionUniqueNo = transmissionDate + transmissionTime + random6;

        Map<String, Object> header = Map.of(
                "apiName", "updateDemandDepositAccountDeposit",
                "transmissionDate", transmissionDate,
                "transmissionTime", transmissionTime,
                "institutionCode", externalApiProperties.getAccountAuth().getInstitutionCode(),
                "fintechAppNo", externalApiProperties.getAccountAuth().getFintechAppNo(),
                "apiServiceCode", "updateDemandDepositAccountDeposit",
                "institutionTransactionUniqueNo", institutionTransactionUniqueNo,
                "apiKey", externalApiProperties.getAccountAuth().getApiKey(),
                "userKey", externalApiProperties.getAccountAuth().getUserKey()
        );

        Map<String, Object> body = Map.of(
                "Header", header,
                "accountNo", accountNo,
                "transactionBalance", String.valueOf(amount),
                "transactionSummary", "입금"
        );

        return webClient.post()
                .uri(externalApiProperties.getAccountAuth().getUrlAccountDeposit())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

    
}
