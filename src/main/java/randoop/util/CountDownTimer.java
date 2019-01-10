package randoop.util;

import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;

public class CountDownTimer {

  private long totalTime;
  private @NonDet long startTime;

  private @NonDet CountDownTimer(@Det long timeMillis) {
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
    // Need to use @NonDet in conditional
    @SuppressWarnings("determinism")
    @Det
    long remainingTime = totalTime - elapsedTime();
    if (remainingTime < 0) {
      return 0;
    }
    return remainingTime;
  }

  public static @NonDet CountDownTimer createAndStart(@Det long totalTimeMillis) {
    return new CountDownTimer(totalTimeMillis);
  }

  @Override
  public @PolyDet("up") String toString() {
    @SuppressWarnings("determinism") // this is @NonDet, but all instances will be @NonDet
    @PolyDet("up")
    String result = "elapsed: " + elapsedTime() + ", remaining: " + remainingTime();
    return result;
  }
}
