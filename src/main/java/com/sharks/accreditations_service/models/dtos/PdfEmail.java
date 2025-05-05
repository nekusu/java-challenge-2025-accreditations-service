package com.sharks.accreditations_service.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PdfEmail {

    private UserDTO user;

    private byte[] pdfBytes;
}
