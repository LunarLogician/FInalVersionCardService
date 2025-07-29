package com.example.Card_Service_V2.controllers;

import com.example.Card_Service_V2.models.CardModel;
import com.example.Card_Service_V2.services.CardService;
import com.example.Card_Service_V2.services.dtos.CardListDTO;
import com.example.Card_Service_V2.services.dtos.CardVerificationResponseDTO;
import com.example.Card_Service_V2.services.dtos.CreateCardDTO;
import com.example.Card_Service_V2.services.dtos.CreateCardRequestDTO;
import com.example.Card_Service_V2.services.dtos.UpdateCardRequestDTO;
import com.example.Card_Service_V2.services.dtos.InternalCardVerificationRequestDTO;
import com.example.Card_Service_V2.utils.ApiResponse;
import com.example.Card_Service_V2.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    @GetMapping
    public ResponseEntity<?> cards(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer accountId,
            @RequestParam(required = false) String network,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        if (!AuthUtils.isAdmin(request) && !AuthUtils.isUser(request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied. Invalid role", "Forbidden"));
        }

        boolean isAdmin = AuthUtils.isAdmin(request);
        Integer tokenUserId = AuthUtils.getUserIdAsInt(request);

        org.springframework.data.domain.Page<CardListDTO> cardPage = cardService.processCardListingPaginated(
                status, type, userId, accountId, network, isAdmin, tokenUserId, page, size);

        ApiResponse.Pagination pagination = new ApiResponse.Pagination(
            cardPage.getNumber(),
            cardPage.getSize(),
            cardPage.getTotalElements(),
            cardPage.getTotalPages()
        );
        return ResponseEntity.ok(ApiResponse.success("Cards fetched successfully", cardPage.getContent(), pagination));
    }

    @GetMapping("{userId}/verify/{cardNumber}")
    public ResponseEntity<?> verifyCard(@PathVariable int userId, @PathVariable String cardNumber,
            HttpServletRequest request) {

        if (!AuthUtils.canAccessUser(request, userId)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied. You can only verify your own cards", "Forbidden"));
        }

        CardVerificationResponseDTO result = cardService.processCardVerification(userId, cardNumber);

        if (result.isVerified()) {
            return ResponseEntity.ok(ApiResponse.success("Card verified", result));
        } else {
            return ResponseEntity.status(400).body(ApiResponse.error("Card verification failed", "VerificationFailed"));
        }
    }

   @PostMapping
public ResponseEntity<?> createCard(@RequestBody CreateCardRequestDTO request, HttpServletRequest httpRequest) {
    String token = httpRequest.getHeader("Authorization");
    if (token != null && token.startsWith("Bearer ")) {
        token = token.substring(7);
    }
    try {
        CardService.AccountInfo accountInfo = cardService.fetchAccountInfoFromToken(token, request.getCurrency());
        request.setUserId(accountInfo.userId);
        request.setAccountId(accountInfo.id);
        String accountCurrency = null;
        if (accountInfo instanceof CardService.AccountInfoWithCurrency) {
            accountCurrency = ((CardService.AccountInfoWithCurrency) accountInfo).currency;
        }
        if (request.getCurrency() == null && accountCurrency != null) {
            request.setCurrency(accountCurrency);
        }
        CreateCardDTO createdCard = cardService.processCardCreationWithCurrency(request, token, accountCurrency);
        return ResponseEntity.ok(ApiResponse.success("Card created successfully", createdCard));
    } catch (CardService.ValidationException e) {
        return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage(), "ValidationException"));
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage(), "IllegalArgumentException"));
    } catch (Exception e) {
        return ResponseEntity.status(500).body(ApiResponse.error("Internal server error during card creation", "ServerError"));
    }
}
    @PutMapping("/{cardId}/block")
    public ResponseEntity<?> blockCard(@PathVariable int cardId, HttpServletRequest request) {
        if (!AuthUtils.isAdmin(request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied. Only ADMIN can block cards", "Forbidden"));
        }
        CardModel blockedCard = cardService.blockCard(cardId);
        String cardNumber = (blockedCard.getSensitiveData() != null && blockedCard.getSensitiveData().getCardNumber() != null)
            ? blockedCard.getSensitiveData().getCardNumber() : "";
        return ResponseEntity.ok(ApiResponse.success("Card blocked", Map.of("cardNumber", cardNumber)));
    }

    @PutMapping("/{cardId}/unblock")
    public ResponseEntity<?> unblockCard(@PathVariable int cardId, HttpServletRequest request) {
        if (!AuthUtils.isAdmin(request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied. Only ADMIN can unblock cards", "Forbidden"));
        }
        CardModel unblockedCard = cardService.unblockCard(cardId);
        String cardNumber = (unblockedCard.getSensitiveData() != null && unblockedCard.getSensitiveData().getCardNumber() != null)
            ? unblockedCard.getSensitiveData().getCardNumber() : "";
        return ResponseEntity.ok(ApiResponse.success("Card unblocked", Map.of("cardNumber", cardNumber)));
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<?> updateCard(@PathVariable int cardId, @RequestBody UpdateCardRequestDTO request,
            HttpServletRequest httpRequest) {
        if (AuthUtils.isUser(httpRequest)) {
            Integer userId = AuthUtils.getUserIdAsInt(httpRequest);
            if (userId == null || !cardService.canUserAccessCard(cardId, userId)) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied. You can only update your own cards", "Forbidden"));
            }
        }
        try {
            cardService.processCardUpdate(cardId, request);
            return ResponseEntity.ok(ApiResponse.success("Card updated successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage(), "IllegalArgumentException"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("Internal server error during card update", "ServerError"));
        }
    }

    @PostMapping("/internal/verify")
    public ResponseEntity<?> internalVerifyCard(@RequestBody InternalCardVerificationRequestDTO request) {
        boolean response = cardService.processInternalCardVerification(request);
        return ResponseEntity.ok(ApiResponse.success("Internal card verification processed", response));
    }
    @PutMapping("/deliver/{cardId}")
    public ResponseEntity<?> deliverCard(@PathVariable int cardId, @RequestBody Map<String, String> body, HttpServletRequest request) {
        boolean isAdmin = AuthUtils.isAdmin(request);
        String newStatus = body.get("cardStatus");
        try {
            cardService.processDeliverCard(cardId, newStatus, isAdmin);
            return ResponseEntity.ok(ApiResponse.success("Card status set to " + newStatus, null));
        } catch (CardService.ValidationException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage(), "ValidationException"));
        }
    }

    @PostMapping("/user/activate")
    public ResponseEntity<?> activateCardByUser(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String cardNumber = body.get("cardNumber");
        String cardCvv = body.get("cardCvv");
        Integer userId = AuthUtils.getUserIdAsInt(request);
        try {
            cardService.processActivateCardByUser(cardNumber, cardCvv, userId);
            return ResponseEntity.ok(ApiResponse.success("Card activated successfully", null));
        } catch (CardService.ValidationException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage(), "ValidationException"));
        }
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<?> getUserCardSensitiveData(@PathVariable int cardId, HttpServletRequest request) {
        Integer userId = AuthUtils.getUserIdAsInt(request);
        if (userId == null) {
            return ResponseEntity.status(403).body(ApiResponse.error("Unauthorized: user not found in token", "Unauthorized"));
        }
        try {
            Map<String, Object> result = cardService.getUserCardSensitiveData(cardId, userId);
            return ResponseEntity.ok(ApiResponse.success("Card sensitive data fetched", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage(), "Forbidden"));
        }
    }

    

    @GetMapping("/number/{cardId}")
    public ResponseEntity<?> getCardNumber(@PathVariable int cardId, HttpServletRequest request) {
        Integer userId = AuthUtils.getUserIdAsInt(request);
        if (userId == null) {
            return ResponseEntity.status(403).body(ApiResponse.error("Unauthorized: user not found in token", "Unauthorized"));
        }
        try {
            CardModel card = cardService.getCardById(cardId);
            if (card.getUserid() != userId) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied. Card does not belong to user", "Forbidden"));
            }
            String cardNumber = card.getSensitiveData() != null ? card.getSensitiveData().getCardNumber() : null;
            return ResponseEntity.ok(ApiResponse.success("Card number fetched", Map.of("cardNumber", cardNumber)));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(ApiResponse.error("Card not found", "NotFound"));
        }
    }

    @GetMapping("/cvv/{cardId}")
    public ResponseEntity<?> getCardCvv(@PathVariable int cardId, HttpServletRequest request) {
        Integer userId = AuthUtils.getUserIdAsInt(request);
        if (userId == null) {
            return ResponseEntity.status(403).body(ApiResponse.error("Unauthorized: user not found in token", "Unauthorized"));
        }
        try {
            CardModel card = cardService.getCardById(cardId);
            if (card.getUserid() != userId) {
                return ResponseEntity.status(403).body(ApiResponse.error("Access denied. Card does not belong to user", "Forbidden"));
            }
            String cardCvv = card.getSensitiveData() != null ? card.getSensitiveData().getCardCvv() : null;
            return ResponseEntity.ok(ApiResponse.success("Card CVV fetched", Map.of("cardCvv", cardCvv)));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(ApiResponse.error("Card not found", "NotFound"));
        }
    }
}

@RestControllerAdvice
class CardValidationExceptionHandler {
    @ExceptionHandler(CardService.ValidationException.class)
    public ResponseEntity<?> handleValidationException(CardService.ValidationException ex) {
        return ResponseEntity.badRequest().body(
            com.example.Card_Service_V2.utils.ApiResponse.error(ex.getMessage(), "ValidationException")
        );
    }

}