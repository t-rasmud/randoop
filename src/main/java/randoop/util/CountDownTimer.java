package randoop.util;

import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.framework.qual.HasQualifierParameter;

@HasQualifierParameter(NonDet.class)
public class CountDownTimer {

  private @PolyDet long totalTime;
  private @NonDet long startTime;

  private CountDownTimer(long timeMillis) {
    this.totalTime = timeMillis;
    this.startTime = System.currentTimeMillis();
  }

  public @NonDet boolean reachedZero() {
    return (elapsedTime() >= totalTime);
  }

  public @NonDet long elapsedTime() {
    return System.currentTimeMillis() - this.startTime;
  }

  public @NonDet long remainingTime() {
    long remainingTime = totalTime - elapsedTime();
    if (remainingTime < 0) {
      return 0;
    }
    return remainingTime;
  }

  public static CountDownTimer createAndStart(long totalTimeMillis) {
    return new CountDownTimer(totalTimeMillis);
  }

  @Override
  public @NonDet String toString() {
    return "elapsed: " + elapsedTime() + ", remaining: " + remainingTime();
  }
}
