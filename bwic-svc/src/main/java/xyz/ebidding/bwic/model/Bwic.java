package xyz.ebidding.bwic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Where(clause="active=1")
public class Bwic {
    @Id
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @GeneratedValue(generator = "system-uuid")
    private String id;
    private String clientId;
    private Double size;
    private Instant dueDate;

    private Double startingPrice;

    @ManyToOne
    @JoinColumn(name= "cusip", referencedColumnName = "cusip")
    @NotNull
    private BondReference bondReference;

    @Version
    private Long version;

    private Boolean active;

}
