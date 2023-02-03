package Forcloud.chat;

import lombok.Data;

@Data
public class GetFile {
    private Long roomId;
    private String base64;
    private String name;
}
