package Forcloud.chat;

import Forcloud.chat.config.MessageDto;
import lombok.Data;

@Data
public class GetChattingsResponse {
    private Long number;
    private MessageDto last;
}
