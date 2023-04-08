package com.sprint.repository.entity;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Long id;

    @Column(name = "ucc", length = 10)
    String ucc;

    @Column(name = "fund_id")
    Long fundId;

    @Column(name = "upstox_scheme_id", length = 25)
    private String upstoxSchemeId;

    @Column(name = "type", length = 1)
    String type;

    @Column(name = "category", length = 10)
    String category;

    @Column(name = "order_date")
    LocalDateTime orderDate;

    @Column(name = "order_id", nullable = false, length = 100)
    String orderId;

    // ToDo: need to update with id after auto generated (Max Length should be 19)
    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "order_amount")
    Double orderAmount;

    @Column(name = "order_units", precision = 50, scale = 15)
    BigDecimal orderUnits;

    @Column(name = "order_type", length = 10)
    String orderType;

    @Column(name = "units", precision = 50, scale = 15)
    BigDecimal units;

    @Column(name = "nav")
    Double nav;

    @Column(name = "amount")
    Double amount;

    @Column(name = "isin", length = 100)
    String isin;

    @Column(name = "bse_scheme", length = 20)
    String bseScheme;

    @Column(name = "folio_number", length = 20)
    String folioNumber;

    @Column(name = "status", length = 20)
    String status;

    @Lob
    @Column(name = "remarks")
    String remarks;

    @Column(name = "exchange_order_id")
    Long exchangeOrderId;

    @Column(name = "exchange_settlement_id")
    Long exchangeSettlementId;

    @Column(name = "reference_key", length = 100)
    String referenceKey;

    @Column(name = "rta_scheme_code", length = 20)
    String rtaSchemeCode;

    @Column(name = "rta_trans_no", length = 30)
    String rtaTransNo;

    @Column(name = "stt")
    Double stt;

    @Column(name = "sett_no", length = 20)
    String settNo;

    @Column(name = "sett_type", length = 20)
    String settType;

    @Column(name = "stamp_duty")
    Double stampDuty;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    LocalDateTime updatedAt;

    @Column(name = "tax")
    Double tax;

    @Column(name = "exit_load")
    Double exitLoad;

    @Column(name = "payout_amount")
    Double payoutAmount;

    @Column(name = "payout_date")
    String payoutDate;

    @Type(type = "json")
    @Column(name = "account_number", columnDefinition = "json")
    AccountNumbers accountNumber;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
}
