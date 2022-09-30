package io.kennethmartens.ckm.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageReference {
    private String id;
    private URI imageResource;
}
