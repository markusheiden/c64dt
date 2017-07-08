package de.heiden.c64dt.assembler.detector;

import de.heiden.c64dt.assembler.command.CommandBuffer;

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
  public boolean detect(CommandBuffer commands);
}
