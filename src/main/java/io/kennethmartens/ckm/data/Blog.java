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

    private Date datePosted;
    private Date dateModified;

    private String id;
    private String title;
    private String subtitle;
    private ImageReference headerImageReference;

    private Integer loves;

    private String introduction;
    private String body;

}
