package com.dchoc.iphonewrappers;

import zlport.ios.Ios;

public class MovieWrapper {
   public static boolean isMovieFinished() {
      return Ios.platform().isMovieFinished();
   }

   public static void playMovie(String path) {
      Ios.platform().playMovie(path);
   }

   public static void resumeMovie() {
      Ios.platform().resumeMovie();
   }
}
