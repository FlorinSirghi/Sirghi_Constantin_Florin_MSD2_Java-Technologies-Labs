package com.example.Lab4.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreferenceEntryResponse {

    @JacksonXmlProperty(localName = "courseId")
    private Long courseId;

    @JacksonXmlProperty(localName = "courseName")
    private String courseName;

    @JacksonXmlProperty(localName = "priority")
    private Integer priority;

    @JacksonXmlProperty(localName = "tieGroup")
    private Integer tieGroup;
}







