package com.example.buensaborback.domain.entities;

import lombok.Data;
import org.hibernate.envers.Audited;

@Audited
@Data
public class PreferenceMP {
    private String id;
    private int statusCode;
}
