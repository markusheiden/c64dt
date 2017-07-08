package de.heiden.c64dt.assembler.util;

import de.heiden.c64dt.bytes.HexUtil;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * JAXB-Adapter for Hex-Bytes.
 */
public class HexByteAdapter extends XmlAdapter<String, Integer> {
  @Override
  public Integer unmarshal(String string) throws Exception {
    return string != null ? HexUtil.parseHexBytePlain(string) : null;
  }

  @Override
  public String marshal(Integer word) throws Exception {
    return word != null ? HexUtil.hexBytePlain(word) : null;
  }
}
