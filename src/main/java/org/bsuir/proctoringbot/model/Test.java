package org.bsuir.proctoringbot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tests")
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private SimpleTelegramUser author;

    @Column(nullable = false)
    private String groupNumber;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String url;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private TestStatus status = TestStatus.CREATED;

    @Column(nullable = false)
    private String tableLink;

    private LocalDateTime startTime;

}
