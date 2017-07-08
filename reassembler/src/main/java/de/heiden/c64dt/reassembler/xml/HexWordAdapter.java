package de.heiden.c64dt.reassembler.xml;

import de.heiden.c64dt.bytes.HexUtil;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * JAXB-Adapter for Hex-Words.
 */
public class HexWordAdapter extends XmlAdapter<String, Integer> {
  @Override
  public Integer unmarshal(String string) throws Exception {
    return string != null ? HexUtil.parseHexWordPlain(string) : null;
  }

  @Override
  public String marshal(Integer word) throws Exception {
    return word != null ? HexUtil.hexWordPlain(word) : null;
  }
}
