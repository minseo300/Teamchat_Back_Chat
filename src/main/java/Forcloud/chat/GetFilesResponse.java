package Forcloud.chat;

import lombok.Data;

import java.util.List;

@Data
public class GetFilesResponse {
    List<GetMinute> minutesList;
    List<GetFile> filesList;
}
