package com.ssafy.boney.domain.transaction.controller;

import com.ssafy.boney.domain.account.entity.Account;
import com.ssafy.boney.domain.account.repository.AccountRepository;
import com.ssafy.boney.domain.transaction.dto.CategoryUpdateRequest;
import com.ssafy.boney.domain.transaction.dto.HashtagUpdateRequest;
import com.ssafy.boney.domain.transaction.dto.TransactionResponse;
import com.ssafy.boney.domain.transaction.exception.ResourceNotFoundException;
import com.ssafy.boney.domain.transaction.service.TransactionService;
import com.ssafy.boney.domain.user.entity.User;
import com.ssafy.boney.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final UserRepository userRepository;  // 추가
    private final AccountRepository accountRepository;

    @GetMapping
    public ResponseEntity<?> getTransactionHistory(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(defaultValue = "all") String type,
            @AuthenticationPrincipal UserDetails userDetails) {

        // JWT에서 얻은 이메일로 사용자 조회 (수정 부분)
        String userEmail = userDetails.getUsername();
        User user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        // 사용자 엔티티로 계좌 조회 (이 부분을 이렇게 수정)
        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("계좌를 찾을 수 없습니다."));

        // 서비스 호출
        transactionService.syncExternalTransactions(account.getAccountNumber(), year, month);
        List<TransactionResponse> data = transactionService.getTransactions(year, month, type, user);


        // 정상 응답 반환
        return ResponseEntity.ok(Map.of(
                "status", "200",
                "message", "거래 내역 조회 성공",
                "data", data
        ));
    }

    // 단일 거래 상세 조회
    @GetMapping("{transactionId}")
    public ResponseEntity<?> getTransactionDetail(
            @PathVariable Integer transactionId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // 로그인 사용자 정보 확인
        String userEmail = userDetails.getUsername();
        User user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        // Service 호출
        TransactionResponse detailDto = transactionService.getTransactionDetail(transactionId, user);

        return ResponseEntity.ok(Map.of(
                "status", "200",
                "data", detailDto
        ));
    }

    // 카테고리 수정
    @PatchMapping("/{transactionId}/category")
    public ResponseEntity<?> updateTransactionCategory(
            @PathVariable Integer transactionId,
            @RequestBody CategoryUpdateRequest dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // 로그인 사용자 식별
        String userEmail = userDetails.getUsername();

        // Service 로직 호출
        transactionService.updateTransactionCategory(transactionId, userEmail, dto.getTransactionCategoryId());

        // 보통 성공 시 갱신된 내용 조회 후 반환하는 경우가 많음
        return ResponseEntity.ok(Map.of(
                "status", "200",
                "message", "카테고리 수정 완료"
        ));
    }

    // 해시태그 수정
    @PatchMapping("/{transactionId}/hashtags")
    public ResponseEntity<?> updateTransactionHashtags(
            @PathVariable Integer transactionId,
            @RequestBody HashtagUpdateRequest dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userEmail = userDetails.getUsername();

        transactionService.updateTransactionHashtags(transactionId, userEmail, dto.getHashtags());

        return ResponseEntity.ok(Map.of(
                "status", "200",
                "message", "해시태그 수정 완료"
        ));
    }
}
