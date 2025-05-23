package org.metacorp.mindbug.dto.choice;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.metacorp.mindbug.dto.CardDTO;
import org.metacorp.mindbug.model.choice.ChoiceType;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class ChoiceDTODeserializer extends StdDeserializer<AbstractChoiceDTO> {

    /**
     * Required default constructor
     */
    public ChoiceDTODeserializer() {
        this(null);
    }

    public ChoiceDTODeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public AbstractChoiceDTO deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        ChoiceType choiceType = ChoiceType.valueOf(node.get("type").asText());

        ObjectMapper mapper = new ObjectMapper();
        UUID playerToChoose = UUID.fromString(node.get("playerToChoose").asText());

        switch (choiceType) {
            case BOOLEAN, FRENZY -> {
                CardDTO sourceCard = mapper.treeToValue(node.get("sourceCard"),  new TypeReference<>() {});
                return new ChoiceDTO(choiceType, playerToChoose,  sourceCard);
            }
            case SIMULTANEOUS -> {
                Set<CardDTO> cards = mapper.treeToValue(node.get("availableEffects"),  new TypeReference<>() {});
                return new SimultaneousChoiceDTO(playerToChoose, cards);
            }
            case HUNTER -> {
                CardDTO sourceCard = mapper.treeToValue(node.get("sourceCard"),  new TypeReference<>() {});
                Set<CardDTO> cards = mapper.treeToValue(node.get("availableTargets"),  new TypeReference<>() {});
                return new HunterChoiceDTO(playerToChoose,  sourceCard, cards);
            }
            case TARGET -> {
                CardDTO sourceCard = mapper.treeToValue(node.get("sourceCard"),  new TypeReference<>() {});
                Set<CardDTO> cards = mapper.treeToValue(node.get("availableTargets"),  new TypeReference<>() {});
                return new TargetChoiceDTO(playerToChoose, sourceCard, cards, node.get("targetsCount").asInt());
            }
        }

        // Should not happen
        return null;
    }
}
