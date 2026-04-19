package kr.church.erp.commoncode.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "common_codes")
public class CommonCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_code", nullable = false, length = 50)
    private String groupCode;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected CommonCode() {
    }

    public static CommonCode create(
        String groupCode,
        String code,
        String name,
        int sortOrder,
        boolean active,
        String description
    ) {
        CommonCode commonCode = new CommonCode();
        commonCode.groupCode = groupCode;
        commonCode.code = code;
        commonCode.name = name;
        commonCode.sortOrder = sortOrder;
        commonCode.active = active;
        commonCode.description = description;
        commonCode.createdAt = LocalDateTime.now();
        return commonCode;
    }

    public void update(String name, int sortOrder, boolean active, String description) {
        this.name = name;
        this.sortOrder = sortOrder;
        this.active = active;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.active = false;
    }

    public Long getId() {
        return id;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public boolean isActive() {
        return active;
    }

    public String getDescription() {
        return description;
    }
}
