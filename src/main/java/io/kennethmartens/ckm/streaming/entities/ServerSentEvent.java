package io.kennethmartens.ckm.streaming.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ServerSentEvent<TId, TObject> {

    private TId id;
    private TObject data;
}
