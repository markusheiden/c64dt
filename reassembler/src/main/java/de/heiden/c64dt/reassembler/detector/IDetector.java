package de.heiden.c64dt.reassembler.detector;

import de.heiden.c64dt.reassembler.command.CommandBuffer;

/**
 * Interface for code type detectors.
 */
public interface IDetector {
  /**
   * Detect type of code.
   *
   * @param commands command buffer
   * @return whether a change of code types has taken place
   */
  boolean detect(CommandBuffer commands);
}
