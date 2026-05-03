package kr.church.erp.finance.voucher.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "voucher_approval_histories")
public class VoucherApprovalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "voucher_id", nullable = false)
    private Long voucherId;

    @Column(name = "action", nullable = false, length = 30)
    private String action;

    @Column(name = "actor_id")
    private Long actorId;

    @Column(name = "comment", length = 500)
    private String comment;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected VoucherApprovalHistory() {}

    public static VoucherApprovalHistory create(Long voucherId, String action, Long actorId, String comment) {
        VoucherApprovalHistory history = new VoucherApprovalHistory();
        history.voucherId = voucherId;
        history.action = action;
        history.actorId = actorId;
        history.comment = comment;
        history.createdAt = LocalDateTime.now();
        return history;
    }
}
