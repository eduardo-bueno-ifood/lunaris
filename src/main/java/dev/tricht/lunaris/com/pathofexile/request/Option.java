package dev.tricht.lunaris.com.pathofexile.request;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Option {
    public Object option;
}
