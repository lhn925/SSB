package sky.Sss.store.entity;


import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter(value = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
public class SkyStore {

    @Id
    @GeneratedValue
    private Long id;

    private String name;






}
