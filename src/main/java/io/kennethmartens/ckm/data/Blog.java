package io.kennethmartens.ckm.data;

import lombok.*;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class Blog {

    private String id;
    private String title;
    private String subtitle;
    private Date datePosted;
    private ImageReference headerImageReference;
    private Integer loves;

    private String introduction;
    private String body;

}
