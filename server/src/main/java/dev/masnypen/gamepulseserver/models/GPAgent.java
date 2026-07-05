package dev.masnypen.gamepulseserver.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "agents")
public class GPAgent {

    @Id
    @GeneratedValue
    private UUID id;

    private String hostname;
    private String lastIp;
    private Long lastAuth;
}