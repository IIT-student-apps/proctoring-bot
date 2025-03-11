package org.bsuir.proctoringbot.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IntermediateStateData implements Serializable {

    private String pickedSubject;

    private String pickedWorkType;

}
