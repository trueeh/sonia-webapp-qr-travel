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
@RequestMapping("/qrcode")
public class QrCodeController
{
  private final static Config CONFIG = Config.getInstance();

  private final static Logger LOGGER = LoggerFactory.getLogger(
    QrCodeController.class.getName());

  @GetMapping(produces = MediaType.IMAGE_PNG_VALUE)
  public ResponseEntity<BufferedImage> qrCode(
    @RequestParam(name = "p", required = false) String pin)
  {
    LOGGER.info("pin = " + pin);

    BufferedImage image = null;
    try
    {
      image = generateQRCodeImage( CONFIG.getWebServiceUrl()+"/?p=" + pin );
    }
    catch (WriterException ex)
    {
      LOGGER.error("creating qrcode ", ex);
    }
    
    return ResponseEntity.ok(image);
  }

  public static BufferedImage generateQRCodeImage(String barcodeText) throws
    WriterException
  {
    QRCodeWriter barcodeWriter = new QRCodeWriter();
    
    Map hintMap = new HashMap();
    hintMap.put( EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M );

    BitMatrix bitMatrix
      = barcodeWriter.encode( barcodeText, BarcodeFormat.QR_CODE, 640, 640, hintMap );
    
    return MatrixToImageWriter.toBufferedImage(bitMatrix);
  }

  @Bean
  public HttpMessageConverter<BufferedImage> createImageHttpMessageConverter()
  {
    return new BufferedImageHttpMessageConverter();
  }
}
