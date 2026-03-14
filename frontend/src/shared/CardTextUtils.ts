type EffectsByActivation = Record<string, unknown[]>;

const KEYWORD_DESCRIPTIONS: Record<string, string> = {
  SNEAKY: "It can only be blocked by a card with SNEAKY.",
  POISONOUS: "It destroys any card it attacks or blocks.",
  FRENZY: "After dealing damage, it may attack again.",
  HUNTER: "It can choose a target to attack.",
  TOUGH: "The first time it would be destroyed, it survives.",
};

const ACTIVATION_LABELS: Record<string, string> = {
  PLAY: "This effect triggers when the card is played.",
  ATTACK: "This effect triggers when the card declares an attack.",
  PASSIVE: "This effect is always active while the card is in play.",
  ACTION: "This effect can be activated by its controller during their turn.",
  DEFEATED: "This effect triggers when the card is defeated.",
  LIFE_LOST: "This effect triggers when a player loses life.",
};


export function getKeywordDescriptions(keywords?: string[]): { keyword: string; description: string }[] {
  if (!keywords || keywords.length === 0) return [];
  return keywords.map((keyword) => ({
    keyword,
    description: KEYWORD_DESCRIPTIONS[keyword] ?? "Effet spécial.",
  }));
}

export function getCardActivations(effects?: EffectsByActivation): { name?: string; activation: string }[] {
  const results: { name?: string; activation: string }[] = [];
  const entries = effects ? Object.keys(effects) : [];
  for (const activation of entries) {
    results.push({
      name: activation,
      activation: ACTIVATION_LABELS[activation] ?? activation,
    });
  }
  return results;
}

export function getActivationLabel(activation: string): string {
  return ACTIVATION_LABELS[activation] ?? activation;
}

export function getEvolutionTargetId(effects?: EffectsByActivation): number | null {
  if (!effects) return null;
  for (const list of Object.values(effects)) {
    for (const effect of list || []) {
      if (!effect || typeof effect !== "object") continue;
      const entry = effect as Record<string, unknown>;
      if (entry.type === "EVOLVE" && typeof entry.id === "number") {
        return entry.id;
      }
    }
  }
  return null;
}

export function getEvolutionActivation(effects?: EffectsByActivation): string | null {
  if (!effects) return null;
  for (const [activation, list] of Object.entries(effects)) {
    for (const effect of list || []) {
      if (!effect || typeof effect !== "object") continue;
      const entry = effect as Record<string, unknown>;
      if (entry.type === "EVOLVE") {
        return activation;
      }
    }
  }
  return null;
}
