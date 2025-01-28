package org.metacorp.mindbug;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.player.Player;

@Data
@AllArgsConstructor
public class AttackHolder {
    @NonNull
    private CardInstance attackCard;

    @NonNull
    private Player defender;

    private CardInstance defendCard;
}
