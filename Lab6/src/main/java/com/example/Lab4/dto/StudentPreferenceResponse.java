package com.example.Lab4.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "studentPreference")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentPreferenceResponse {

    @JacksonXmlProperty(localName = "studentId")
    private Long studentId;

    @JacksonXmlProperty(localName = "studentName")
    private String studentName;

    @JacksonXmlProperty(localName = "year")
    private Integer year;

    @JacksonXmlElementWrapper(localName = "entries")
    @JacksonXmlProperty(localName = "entry")
    private List<PreferenceEntryResponse> entries;
}



