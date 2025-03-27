package com.mindbug.services.gamecore;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Battle {

    private PlayerCard attacking;
    private PlayerCard blocking;
}
