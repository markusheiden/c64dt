package de.heiden.c64dt.reassembler.xml;

import de.heiden.c64dt.bytes.HexUtil;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import org.jspecify.annotations.Nullable;

/**
 * JAXB-Adapter for Hex-Words.
 */
public class HexWordAdapter extends XmlAdapter<String, Integer> {
  @Override
  public @Nullable Integer unmarshal(@Nullable String string) throws Exception {
    return string != null ? HexUtil.parseHexWordPlain(string) : null;
  }

  @Override
  public @Nullable String marshal(@Nullable Integer word) throws Exception {
    return word != null ? HexUtil.hexWordPlain(word) : null;
  }
}
