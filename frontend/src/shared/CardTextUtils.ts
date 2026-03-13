type EffectsByActivation = Record<string, unknown[]>;

import i18n from "@/i18n/i18n";

function translateOrFallback(key: string, fallback: string): string {
  const translated = i18n.global.t(key);
  return translated === key ? fallback : translated;
}

export function getKeywordDescriptions(keywords?: string[]): { keyword: string; description: string }[] {
  if (!keywords || keywords.length === 0) return [];
  const defaultDescription = i18n.global.t("cards.keyword_descriptions.default");
  return keywords.map((keyword) => ({
    keyword,
    description: translateOrFallback(`cards.keyword_descriptions.${keyword}`, defaultDescription),
  }));
}

export function getCardActivations(effects?: EffectsByActivation): { name?: string; activation: string }[] {
  const results: { name?: string; activation: string }[] = [];
  const entries = effects ? Object.keys(effects) : [];
  for (const activation of entries) {
    results.push({
      name: activation,
      activation: translateOrFallback(`cards.activation_labels.${activation}`, activation),
    });
  }
  return results;
}

export function getActivationLabel(activation: string): string {
  return translateOrFallback(`cards.activation_labels.${activation}`, activation);
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
