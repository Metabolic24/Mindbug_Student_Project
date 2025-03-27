package com.mindbug.services.gamecore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Battle {

    private PlayerCard attacking;
    private PlayerCard blocking;
}
