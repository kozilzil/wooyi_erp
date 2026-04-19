package kr.church.erp.organization.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "organizations")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected Organization() {
    }

    public static Organization create(String code, String name, Long parentId, String type, boolean active) {
        Organization organization = new Organization();
        organization.code = code;
        organization.name = name;
        organization.parentId = parentId;
        organization.type = type;
        organization.active = active;
        organization.createdAt = LocalDateTime.now();
        return organization;
    }

    public void update(String name, Long parentId, String type, boolean active) {
        this.name = name;
        this.parentId = parentId;
        this.type = type;
        this.active = active;
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.active = false;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Long getParentId() {
        return parentId;
    }

    public String getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
}
