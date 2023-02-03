package Forcloud.chat.config;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto implements Serializable {
    private String nickName;
    private String msg;
    private String timestamp;
    private Long roomId;
    private Long memberId;
    private String msgType;
    private String originalFileName;
}

