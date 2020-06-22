/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonia.webapp.qrtravel;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/attendee")
public class AttendeeController
{
  private final static Config CONFIG = Config.getInstance();

  private final static Logger LOGGER = LoggerFactory.getLogger(AttendeeController.class.getName());

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public AttendeeInfo attendee(
    @RequestParam(name = "mail", required = true) String mail )
  {
    LOGGER.info("mail = " + mail);
    
    AttendeeInfo attendee = new AttendeeInfo();
    
    if ( mail.equalsIgnoreCase("carl.mustermann@ostfalia.de"))
    {
      attendee.setMail("carl.mustermann@ostfalia.de");
      attendee.setGivenname("Carl");
      attendee.setSurname("Mustermann");
      attendee.setStudentnumber("12345678");
    }
    else
    {
      attendee.setMail(mail.toLowerCase());
    }
    
    return attendee;
  }
}
