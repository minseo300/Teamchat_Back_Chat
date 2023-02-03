package Forcloud.chat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostFileResponse {
    private String fileId;
    private Long roomId;
    private Long memberId;
    private String timestamp;
    private String nickName;

}
