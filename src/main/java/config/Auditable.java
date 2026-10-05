package config;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
public abstract class Auditable {


    @Column(name = "CreatedBy")
    private String createdBy;


    @Column(name = "CreatedDate")
    private LocalDateTime createdDate;


    @Column(name = "LastModifiedBy")
    private String lastModifiedBy;

    @Column(name = "LastModifiedDate")
    private LocalDateTime lastModifiedDate;

    @Column(name = "IsActive")
    private Boolean isActive;

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = LocalDateTime.now();
    }
    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
        this.isActive = true;
    }
}
