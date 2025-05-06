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

    public ChoiceDTODeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public AbstractChoiceDTO deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        ChoiceType choiceType = ChoiceType.valueOf(node.get("type").asText());

        ObjectMapper mapper = new ObjectMapper();

        switch (choiceType) {
            case BOOLEAN, FRENZY -> {
                return new ChoiceDTO(choiceType, UUID.fromString(node.get("playerToChoose").asText()),  UUID.fromString(node.get("sourceCard").asText()));
            }
            case SIMULTANEOUS -> {
                Set<CardDTO> cards = mapper.treeToValue(node.get("availableEffects"),  new TypeReference<>() {});
                return new SimultaneousChoiceDTO(cards);
            }
            case TARGET -> {
                Set<CardDTO> cards = mapper.treeToValue(node.get("availableTargets"),  new TypeReference<>() {});
                return new TargetChoiceDTO(UUID.fromString(node.get("playerToChoose").asText()),  UUID.fromString(node.get("sourceCard").asText()), cards, node.get("targetsCount").asInt());
            }
        }

        // Should not happen
        return null;
    }
}
