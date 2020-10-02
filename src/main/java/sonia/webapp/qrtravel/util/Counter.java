package sonia.webapp.qrtravel.util;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
public class Counter
{
  public Counter()
  {
    this.value = 0;
  }

  public Counter(int value)
  {
    this.value = value;
  }

  public int add(int value)
  {
    this.value += value;
    return this.value;
  }

  public int getIncrement()
  {
    return ++value;
  }
  
  public int getDecrement()
  {
    return --value;
  }

  @Getter
  @Setter
  private int value;
}
