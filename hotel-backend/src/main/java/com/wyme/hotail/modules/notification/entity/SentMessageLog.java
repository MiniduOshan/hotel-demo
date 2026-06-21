package com.wyme.hotail.modules.notification.entity;

import com.wyme.hotail.shared.kernel.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "sent_message_logs")
public class SentMessageLog extends BaseEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name = "type")
    private String type = "email"; // email or sms

    @Column(name = "audience_size")
    private Integer audienceSize = 0;

    @Column(name = "status")
    private String status = "Success";
}
