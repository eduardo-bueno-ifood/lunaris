package dev.tricht.lunaris.info.poeprices;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ItemPricePrediction {
    private int error;
    private String min;
    private String max;
    private String currency;
    @JsonProperty("pred_confidence_score")
    private String confidenceScore;

    public String toString() {
        if (error != 0) {
            return "poeprices.info gave back an error.";
        }
        return String.format(
                "%.2f~%.2f %s (%.2f%%)",
                Double.parseDouble(min),
                Double.parseDouble(max),
                currency,
                Double.parseDouble(confidenceScore)
        );
    }
}
