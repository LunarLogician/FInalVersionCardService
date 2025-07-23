package com.example.Card_Service_V2.services;

import com.example.Card_Service_V2.models.CardModel;
import com.example.Card_Service_V2.models.CardSensitiveData;
import org.springframework.http.MediaType;
import com.example.Card_Service_V2.models.CardPlan;
import com.example.Card_Service_V2.repositories.CardRepo;
import com.example.Card_Service_V2.repositories.CardPlanRepository;
import com.example.Card_Service_V2.services.dtos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import com.fasterxml.jackson.databind.JsonNode;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CardService {
    
    private static final Logger logger = LoggerFactory.getLogger(CardService.class);

    @Autowired
    private CardRepo repo;

    @Autowired
    private CardPlanRepository cardPlanRepository;

    private SecureRandom secureRandom = new SecureRandom();

    // Card Status Constants
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_BLOCKED = "BLOCKED";
    private static final String STATUS_FREEZE = "FREEZE";
    private static final String STATUS_EXPIRED = "EXPIRED";
    private static final String STATUS_SUSPENDED = "SUSPENDED";

    // Card Type Constants
    private static final String TYPE_PHYSICAL = "PHYSICAL";
    private static final String TYPE_VIRTUAL = "VIRTUAL";

    // Card Network Constants
    private static final String NETWORK_VISA = "VISA";
    private static final String NETWORK_MASTERCARD = "MASTERCARD";
    private static final String NETWORK_OTHER = "OTHER";
    public AccountInfo fetchAccountInfoFromToken(String token) {
    RestTemplate restTemplate = new RestTemplate();
    System.out.println("Fetching account info for token: " + token);
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + token);
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> entity = new HttpEntity<>("{}", headers); // empty JSON body
    ResponseEntity<JsonNode> response = restTemplate.exchange(
        "http://localhost:8082/api/v1/accounts/by-token",
        HttpMethod.GET,
        entity,
        JsonNode.class
    );
    JsonNode body = response.getBody();
    if (body == null || !body.has("accountId") || !body.has("userId")) {
        throw new RuntimeException("Invalid response from accounts API");
    }
    int id = body.get("accountId").asInt();
    int userId = body.get("userId").asInt();
    String currency = body.has("currency") ? body.get("currency").asText() : null;
    if (currency != null) {
        return new AccountInfoWithCurrency(id, userId, currency);
    }
    return new AccountInfo(id, userId);
}
public static class AccountInfo {
    public final int id;
    public final int userId;
    public AccountInfo(int id, int userId) {
        this.id = id;
        this.userId = userId;
    }
}
public static class AccountInfoWithCurrency extends AccountInfo {
        public final String currency;
        public AccountInfoWithCurrency(int id, int userId, String currency) {
            super(id, userId);
            this.currency = currency;
        }
    }

    // New method for card creation with currency validation
    public CreateCardDTO processCardCreationWithCurrency(CreateCardRequestDTO request, String token, String accountCurrency) {
        try {
            validateCreateCardRequest(request);
            validatePlanAssignmentLimits(request);

            // Enforce currency match
            if (request.getCurrency() != null && accountCurrency != null && !request.getCurrency().equalsIgnoreCase(accountCurrency)) {
                throw new ValidationException("Currency mismatch: Card currency does not match account currency");
            }

            if (isDuplicateCard(request)) {
                throw new ValidationException("Duplicate card: A card with this user, account, type, and network already exists.");
            }

            CardSensitiveData sensitiveData = createSensitiveData(request);
            CardModel card = createCardModel(request, sensitiveData);

            return createCard(request.getUserId(), request.getAccountId(), card);
        } catch (ValidationException e) {
            logger.error("Validation error during card creation: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during card creation", e);
            throw new RuntimeException("Card creation failed", e);
        }
    }


    /**
     * Process card creation with proper validation and error handling
     */
    public CreateCardDTO processCardCreation(CreateCardRequestDTO request) {
        try {
            validateCreateCardRequest(request);
            validatePlanAssignmentLimits(request);

            if (isDuplicateCard(request)) {
                throw new ValidationException("Duplicate card: A card with this user, account, type, and network already exists.");
            }

            CardSensitiveData sensitiveData = createSensitiveData(request);
            CardModel card = createCardModel(request, sensitiveData);

            return createCard(request.getUserId(), request.getAccountId(), card);
        } catch (ValidationException e) {
            logger.error("Validation error during card creation: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during card creation", e);
            throw new RuntimeException("Card creation failed", e);
        }
    }


    private CardSensitiveData createSensitiveData(CreateCardRequestDTO request) {
        CardSensitiveData sensitiveData = new CardSensitiveData();
        sensitiveData.setCardNumber(generateCardNumber());
        sensitiveData.setCardCvv(generateCVV());
        sensitiveData.setCardPin(request.getCardPin());
        sensitiveData.setCardExpiry(generateExpiry());
        return sensitiveData;
    }

    private CardModel createCardModel(CreateCardRequestDTO request, CardSensitiveData sensitiveData) {
        CardModel card = new CardModel();
        card.setUserId(request.getUserId());
        card.setAccountId(request.getAccountId());
        card.setType(toCamelCase(request.getType()));
        card.setNetwork(toCamelCase(request.getNetwork()));
        card.setSensitiveData(sensitiveData);
        if (request.getPlanId() == null) {
            cardPlanRepository.findById(1).ifPresent(card::setPlan);
        }
        if (request.getPlanId() != null) {
            cardPlanRepository.findById(request.getPlanId()).ifPresent(card::setPlan);
        }
        return card;
    }

    private String toCamelCase(String value) {
        if (value == null || value.isEmpty()) return value;
        value = value.trim().toLowerCase();
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    private CreateCardDTO createCard(Integer userId, Integer accountId, CardModel card) {
        if (TYPE_PHYSICAL.equalsIgnoreCase(card.getType())) {
            card.setCardstatus(STATUS_PENDING);
        } else if (TYPE_VIRTUAL.equalsIgnoreCase(card.getType())) {
            card.setCardstatus(STATUS_ACTIVE);
        }

        card.setCreatedAt(LocalDateTime.now());
        card.setUserId(userId);
        card.setAccountId(accountId);

        CardModel saved = repo.save(card);
        logger.info("Card created successfully with ID: {}", saved.getId());

        return buildCreateCardResponse(saved);
    }

    private CreateCardDTO buildCreateCardResponse(CardModel saved) {
        CreateCardDTO response = new CreateCardDTO();
        response.setCardId(saved.getId());
        response.setMaskedCardNumber(maskCardNumber(saved.getSensitiveData().getCardNumber()));
        response.setCardExpiry(saved.getSensitiveData().getCardExpiry());
        response.setCardStatus(saved.getCardstatus());
        response.setType(saved.getType());
        response.setCreatedAt(saved.getCreatedAt());
        return response;
    }

    private void validateCreateCardRequest(CreateCardRequestDTO request) {
        if (request == null) {
            throw new ValidationException("Request cannot be null");
        }
        if (request.getUserId() == null) {
            throw new ValidationException("userId is missing - this should be set from the JWT token");
        }
        if (request.getAccountId() == null) {
            throw new ValidationException("accountId is required");
        }
        if (request.getCardPin() == null || request.getCardPin().trim().isEmpty()) {
            throw new ValidationException("cardPin is required");
        }
        if (!request.getCardPin().matches("\\d{4}")) {
            throw new ValidationException("Invalid PIN. It must be exactly 4 digits.");
        }
        if (request.getType() == null || request.getType().trim().isEmpty()) {
            throw new ValidationException("Card type is required");
        }
        String type = request.getType().trim().toUpperCase();
        if (!TYPE_PHYSICAL.equals(type) && !TYPE_VIRTUAL.equals(type)) {
            throw new ValidationException("Invalid card type. Only 'Physical' and 'Virtual' are allowed.");
            
        }
        if (request.getNetwork() == null || request.getNetwork().trim().isEmpty()) {
            throw new ValidationException("Network is required");
        }
        String network = request.getNetwork().trim().toUpperCase();
        if (!NETWORK_VISA.equals(network) && !NETWORK_MASTERCARD.equals(network) && !NETWORK_OTHER.equals(network)) {
            throw new ValidationException("Invalid network. Only 'Visa', 'MasterCard', or 'Other' are allowed.");
        }
    }

    
    public CardModel blockCard(int cardId) {
        try {
            CardModel card = repo.findById(cardId)
                    .orElseThrow(() -> new RuntimeException("Card not found with ID: " + cardId));
            
            card.setCardstatus(STATUS_BLOCKED);
            CardModel blocked = repo.save(card);
            logger.info("Card {} blocked successfully", cardId);
            return blocked;
        } catch (Exception e) {
            logger.error("Error blocking card {}: {}", cardId, e.getMessage());
            throw e;
        }
    }

    
    public CardModel unblockCard(int cardId) {
        try {
            CardModel card = repo.findById(cardId)
                    .orElseThrow(() -> new RuntimeException("Card not found with ID: " + cardId));
            
            card.setCardstatus(STATUS_ACTIVE);
            CardModel unblocked = repo.save(card);
            logger.info("Card {} unblocked successfully", cardId);
            return unblocked;
        } catch (Exception e) {
            logger.error("Error unblocking card {}: {}", cardId, e.getMessage());
            throw e;
        }
    }

   
    public void processCardUpdate(int cardId, UpdateCardRequestDTO request) {
        try {
            validateUpdateCardRequest(request);

            boolean hasPin = request.getCardpin() != null && !request.getCardpin().trim().isEmpty();
            boolean hasStatus = request.getCardstatus() != null && !request.getCardstatus().trim().isEmpty();

            if (!hasPin && !hasStatus) {
                throw new IllegalArgumentException("At least one field (cardpin or cardstatus) must be provided for update");
            }

            if (hasPin) {
                String pin = request.getCardpin().trim();
                if (!pin.matches("\\d{4}")) {
                    throw new IllegalArgumentException("Invalid PIN. It must be exactly 4 digits.");
                }
                changePin(cardId, pin);
            }

            if (hasStatus) {
                String status = request.getCardstatus().trim().toUpperCase();
                if (!STATUS_FREEZE.equals(status) && !STATUS_ACTIVE.equals(status)) {
                    throw new IllegalArgumentException("Invalid status. Allowed values: FREEZE, ACTIVE.");
                }
                updateCardStatus(cardId, status);
            }
        } catch (Exception e) {
            logger.error("Error updating card {}: {}", cardId, e.getMessage());
            throw e;
        }
    }

    private void changePin(int cardId, String pin) {
        CardModel card = repo.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found with ID: " + cardId));
        
        if (card.getSensitiveData() != null) {
            card.getSensitiveData().setCardPin(pin);
            repo.save(card);
            logger.info("PIN changed successfully for card {}", cardId);
        } else {
            throw new RuntimeException("Card sensitive data not found");
        }
    }

    public CardModel getCardById(int cardId) {
        return repo.findById(cardId).orElseThrow(() -> new RuntimeException("Card not found with ID: " + cardId));
    }

    public Optional<CardModel> findCardByNumber(String cardNumber) {
        return repo.findAll().stream()
                .filter(card -> card.getSensitiveData() != null && 
                               cardNumber.equals(card.getSensitiveData().getCardNumber()))
                .findFirst();
    }

    public Optional<CardModel> findCardByNumberAndAccountId(String cardNumber, Integer accountId) {
        return repo.findAll().stream()
                .filter(card -> card.getSensitiveData() != null &&
                        cardNumber.equals(card.getSensitiveData().getCardNumber()) &&
                        (accountId == null || accountId.equals(card.getAccountId())))
                .findFirst();
    }

    public void updateCardStatus(int cardId, String status) {
        CardModel card = repo.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found with ID: " + cardId));
        card.setCardstatus(status);
        repo.save(card);
        logger.info("Card {} status updated to {}", cardId, status);
    }

    private void validateUpdateCardRequest(UpdateCardRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Update request cannot be null");
        }
    }

    /**
     * Process card verification for external requests
     */
    public CardVerificationResponseDTO processCardVerification(int userId, String cardNumber) {
        try {
            if (cardNumber == null || cardNumber.trim().isEmpty()) {
                return new CardVerificationResponseDTO(false, "Card number is required", userId, null);
            }
            
            boolean isValid = verifyCard(userId, cardNumber);
            
            if (isValid) {
                String maskedNumber = cardNumber.substring(Math.max(0, cardNumber.length() - 4));
                return new CardVerificationResponseDTO(
                    true, 
                    "Card verified successfully", 
                    userId, 
                    maskedNumber
                );
            } else {
                return new CardVerificationResponseDTO(
                    false, 
                    "Card verification failed. Card not found, inactive, or doesn't belong to user", 
                    userId, 
                    null
                );
            }
        } catch (Exception e) {
            logger.error("Error verifying card for user {}: {}", userId, e.getMessage());
            return new CardVerificationResponseDTO(false, "Verification failed due to system error", userId, null);
        }
    }

    private boolean verifyCard(int userId, String cardNumber) {
        try {
            Optional<CardModel> cardOpt = findCardByNumber(cardNumber);
            
            if (!cardOpt.isPresent()) {
                return false;
            }
            
            CardModel card = cardOpt.get();
            
            // Check ownership
            if (card.getUserid() != userId) {
                return false;
            }
            
            // Check status
            return isCardActive(card);
            
        } catch (Exception e) {
            logger.error("Error in card verification: {}", e.getMessage());
            return false;
        }
    }
    

   public boolean processInternalCardVerification(InternalCardVerificationRequestDTO request) {
    try {
        if (request == null) return false;
        Optional<CardModel> cardOpt = findCardByNumberAndAccountId(request.getCardNumber(), request.getAccountId());
        if (!cardOpt.isPresent()) return false;
        CardModel card = cardOpt.get();
        // Validation logic
        boolean hasPin = request.getCardPin() != null && !request.getCardPin().trim().isEmpty();
        boolean hasCvv = request.getCardCvv() != null && !request.getCardCvv().trim().isEmpty();
        if (hasPin == hasCvv) {
            // Both present or both missing
            logger.error("Either cardPin or cardCvv must be provided, but not both.");
            return false;
        }
        if (hasPin) {
            if (!request.getCardPin().equals(card.getSensitiveData().getCardPin())) return false;
        } else if (hasCvv) {
            if (card.getSensitiveData() == null || !request.getCardCvv().equals(card.getSensitiveData().getCardCvv())) return false;
        }
        CardPlan plan = card.getPlan();
        if (plan == null) return false;
        if (request.getAmount() != null && request.getAmount() > plan.getLimitAmount()) return false;
        return isCardActive(card);
    } catch (Exception e) {
        logger.error("Error in internal card verification: {}", e.getMessage());
        return false;
    }
}
     
    private boolean isValidInternalVerificationRequest(InternalCardVerificationRequestDTO request) {
        return request != null &&
               request.getCardNumber() != null && !request.getCardNumber().trim().isEmpty() &&
               request.getCardPin() != null && !request.getCardPin().trim().isEmpty();
    }

    private boolean isCardActive(CardModel card) {
        String status = card.getCardstatus();
        if (status == null) {
            return false;
        }
        
        String upperStatus = status.toUpperCase();
        return !STATUS_BLOCKED.equals(upperStatus) && 
               !STATUS_EXPIRED.equals(upperStatus) &&
               !STATUS_SUSPENDED.equals(upperStatus) &&
               !STATUS_PENDING.equals(upperStatus);
    }

    /**
     * Process card listing with filters
     */
    public List<CardListDTO> processCardListing(String status, String type, Integer userId, 
                                               Integer accountId, String network, 
                                               boolean isAdmin, Integer tokenUserId) {
        try {
            if (isAdmin) {
                return processAdminCardListing(status, type, userId, accountId, network);
            } else {
                return processUserCardListing(status, type, tokenUserId, accountId, network);
            }
        } catch (Exception e) {
            logger.error("Error processing card listing: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve cards", e);
        }
    }

    private List<CardListDTO> processAdminCardListing(String status, String type, Integer userId, 
                                                     Integer accountId, String network) {
        boolean hasFilters = status != null || type != null || userId != null || accountId != null || network != null;
        
        if (hasFilters) {
            CardFilterDTO filters = new CardFilterDTO();
            filters.setStatus(status);
            filters.setType(type);
            filters.setUserId(userId);
            filters.setAccountId(accountId);
            filters.setNetwork(network);
            return getFilteredCardsForAdmin(filters);
        } else {
            return getAllCardsForAdmin();
        }
    }

    private List<CardListDTO> processUserCardListing(String status, String type, Integer tokenUserId, 
                                                    Integer accountId, String network) {
        CardFilterDTO filters = new CardFilterDTO();
        filters.setStatus(status);
        filters.setType(type);
        filters.setUserId(tokenUserId);
        filters.setAccountId(accountId);
        filters.setNetwork(network);
        
        return getFilteredCardsForUser(filters, tokenUserId);
    }

    private List<CardListDTO> getAllCardsForAdmin() {
        List<CardModel> cards = repo.findAll();
        return convertToCardListDTOForAdmin(cards);
    }

    private List<CardListDTO> getFilteredCardsForAdmin(CardFilterDTO filters) {
        List<CardModel> cards = repo.findWithFilters(
            filters.getStatus(),
            filters.getType(),
            filters.getUserId(),
            filters.getAccountId(),
            filters.getNetwork()
        );
        return convertToCardListDTOForAdmin(cards);
    }

    private List<CardListDTO> getFilteredCardsForUser(CardFilterDTO filters, Integer tokenUserId) {
        List<CardModel> cards = repo.findWithFilters(
            filters.getStatus(),
            filters.getType(),
            filters.getUserId(),
            filters.getAccountId(),
            filters.getNetwork()
        );
        return convertToCardListDTOForUser(cards, tokenUserId);
    }

    private List<CardListDTO> convertToCardListDTOForAdmin(List<CardModel> cards) {
        List<CardListDTO> dtoList = new ArrayList<>();
        for (CardModel card : cards) {
            CardListDTO dto = createCardListDTO(card, false, null);
            dtoList.add(dto);
        }
        return dtoList;
    }

    private List<CardListDTO> convertToCardListDTOForUser(List<CardModel> cards, Integer tokenUserId) {
        List<CardListDTO> dtoList = new ArrayList<>();
        for (CardModel card : cards) {
            boolean showFullNumber = tokenUserId != null && tokenUserId.equals(card.getUserid());
            CardListDTO dto = createCardListDTO(card, showFullNumber, tokenUserId);
            dtoList.add(dto);
        }
        return dtoList;
    }

    private CardListDTO createCardListDTO(CardModel card, boolean showFullNumber, Integer tokenUserId) {
        CardListDTO dto = new CardListDTO();
        dto.setCardId(card.getId());
        
        if (card.getSensitiveData() != null) {
            if (showFullNumber) {
                dto.setCardNumber(card.getSensitiveData().getCardNumber());
            } else {
                dto.setCardNumber(maskCardNumber(card.getSensitiveData().getCardNumber()));
            }
            dto.setCardexpiry(card.getSensitiveData().getCardExpiry());
        }
        
        dto.setCardstatus(card.getCardstatus());
        dto.setAccountid(card.getAccountId());
        dto.setType(card.getType());
        dto.setUserId(card.getUserid());
        
        return dto;
    }

    /**
     * Check if user can access a specific card
     */
    public boolean canUserAccessCard(int cardId, int userId) {
        return isCardOwnedByUser(cardId, userId);
    }

    private boolean isCardOwnedByUser(int cardId, int userId) {
        try {
            Optional<CardModel> cardOpt = repo.findById(cardId);
            return cardOpt.isPresent() && cardOpt.get().getUserid() == userId;
        } catch (Exception e) {
            logger.error("Error checking card ownership: {}", e.getMessage());
            return false;
        }
    }

    // Utility methods
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    private String generateCardNumber() {
        StringBuilder cardNumber = new StringBuilder();
        // Start with a valid prefix (4 for Visa)
        cardNumber.append("4");
        
        // Generate remaining 15 digits
        for (int i = 0; i < 15; i++) {
            cardNumber.append(secureRandom.nextInt(10));
        }
        
        return cardNumber.toString();
    }

    private String generateCVV() {
        int cvv = 100 + secureRandom.nextInt(900);
        return String.valueOf(cvv);
    }

    private String generateExpiry() {
        int month = 1 + secureRandom.nextInt(12);
        int year = Year.now().getValue() + 3 + secureRandom.nextInt(4);
        return String.format("%02d/%02d", month, year % 100);
    }

    // Custom exceptions
    public static class ValidationException extends RuntimeException {
        public ValidationException(String message) {
            super(message);
        }
    }

    private boolean isDuplicateCard(CreateCardRequestDTO request) {
        String type = request.getType() != null ? request.getType().toUpperCase() : null;
        String network = request.getNetwork() != null ? request.getNetwork().toUpperCase() : null;
        List<CardModel> duplicates = repo.findByUseridAndAccountIdAndTypeAndNetwork(
            request.getUserId(),
            request.getAccountId(),
            type,
            network
        );
        return !duplicates.isEmpty();
    }

    public Map<String, Object> getUserCardSensitiveData(int cardId, int userId) {
        CardModel card = getCardById(cardId);
        if (card == null || card.getUserid() != userId) {
            throw new IllegalArgumentException("Access denied. Card does not belong to user");
        }
        if (card.getSensitiveData() == null) {
            throw new IllegalArgumentException("Sensitive data not found for card");
        }
        return Map.of(
            "cardId", card.getId(),
            "cardNumber", card.getSensitiveData().getCardNumber(),
            "cardexpiry", card.getSensitiveData().getCardExpiry(),
            "cardPin", card.getSensitiveData().getCardPin(),
            "cardCvv", card.getSensitiveData().getCardCvv(),
            "accountid", card.getAccountId(),
            "type", card.getType(),
            "cardstatus", card.getCardstatus(),
            "userId", card.getUserid()
        );
    }

    public void processDeliverCard(int cardId, String newStatus, boolean isAdmin) {
        if (!isAdmin) {
            throw new ValidationException("Access denied. Only ADMIN can deliver cards");
        }
        if (newStatus == null || !"DELIVERED".equalsIgnoreCase(newStatus.trim())) {
            throw new ValidationException("cardStatus must be exactly 'DELIVERED'");
        }
        CardModel card = getCardById(cardId);
        if (!"PENDING".equalsIgnoreCase(card.getCardstatus())) {
            throw new ValidationException("Card status must be PENDING to deliver");
        }
        updateCardStatus(cardId, "DELIVERED");
    }

    public void processActivateCardByUser(String cardNumber, String cardCvv, Integer userId) {
        if (cardNumber == null || cardCvv == null) {
            throw new ValidationException("cardNumber and cardCvv are required");
        }
        if (userId == null) {
            throw new ValidationException("Unauthorized: user not found in token");
        }
        CardModel card = findCardByNumberAndAccountId(cardNumber, null)
            .filter(c -> c.getUserid() == userId)
            .orElse(null);
        if (card == null) {
            throw new ValidationException("Card not found or does not belong to user");
        }
        if (!"DELIVERED".equalsIgnoreCase(card.getCardstatus())) {
            throw new ValidationException("Card must be DELIVERED to activate");
        }
        if (card.getSensitiveData() == null || !cardCvv.equals(card.getSensitiveData().getCardCvv())) {
            throw new ValidationException("Invalid CVV");
        }
        updateCardStatus(card.getId(), "ACTIVE");
    }

    private void validatePlanAssignmentLimits(CreateCardRequestDTO request) {
        Integer planId = request.getPlanId();
        Integer userId = request.getUserId();
        if (planId == null) return; 
        if (planId == 2) {
            long userCount = repo.findByPlan_Id(2).stream().map(CardModel::getUserid).distinct().count();
            if (userCount >= 5) {
                throw new ValidationException("Plan 2 (Gold) can only be assigned to 5 users");
            }
            if (!repo.findByPlan_IdAndUserid(2, userId).isEmpty()) {
                throw new ValidationException("Plan 2 (Gold) can only be assigned once per user");
            }
        } else if (planId == 3) {
            long userCount = repo.findByPlan_Id(3).stream().map(CardModel::getUserid).distinct().count();
            if (userCount >= 7) {
                throw new ValidationException("Plan 3 (Platinum) can only be assigned to 7 users");
            }
            if (!repo.findByPlan_IdAndUserid(3, userId).isEmpty()) {
                throw new ValidationException("Plan 3 (Platinum) can only be assigned once per user");
            }
        }
    }
}