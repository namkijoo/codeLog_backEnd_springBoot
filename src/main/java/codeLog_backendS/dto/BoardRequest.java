package codeLog_backendS.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BoardRequest {
    private Integer problemNumber;
    private String problemUrl;
    private String platformName;
    private List<String> tags;
    private String difficulty;
    private String title;
    private String language;
    private String code;
    private String memo;
}