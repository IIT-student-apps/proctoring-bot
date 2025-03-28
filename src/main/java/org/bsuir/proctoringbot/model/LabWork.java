package org.bsuir.proctoringbot.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "labs")
public class LabWork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private SimpleTelegramUser user;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String labNumber;

    @Column(nullable = false)
    private String link;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private LabWorkStatus status = LabWorkStatus.ADDED;

}
